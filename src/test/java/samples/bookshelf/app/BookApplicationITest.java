package samples.bookshelf.app;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static swoop.Swoop.listener;
import static swoop.Swoop.setPort;
import static swoop.Swoop.stop;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import samples.Json;
import samples.bookshelf.domain.Book;
import samples.bookshelf.domain.BookRepository;
import samples.bookshelf.domain.Isbn;
import samples.bookshelf.domain.IsbnCollection;
import samples.bookshelf.domain.PersonRepository;
import samples.bookshelf.service.BookService;
import samples.bookshelf.service.PersonService;
import swoop.it.support.PortProvider;
import swoop.it.support.SwoopServerCountDownOnceStartedListener;

public class BookApplicationITest {

    private static final String UTF8 = "UTF-8";
    // 
    private static final String REFAC_JSON = bookJson("Refactoring to Patterns", "", "0-321-21335-1");
    private static final Isbn REFAC_ISBN = new Isbn("0-321-21335-1");
    private static final String EIP_JSON = bookJson("Enterprise Integration Patterns", "Designing, Building, and Deploying Messaging Solutions", "0-321-20068-3");
    private static final Isbn EIP_ISBN = new Isbn("0-321-20068-3");
    private static final String PEAA_JSON = bookJson("Patterns of Enterprise Application Architecture", "", "0-321-12742-0");
    private static final Isbn PEAA_ISBN = new Isbn("0-321-12742-0");
    private static final String DDD_JSON = bookJson("Domain-Driven Design", "Tackling Complexity in the Heart of Software", "0-321-12521-5");
    private static final Isbn DDD_ISBN = new Isbn("0-321-12521-5");
    private static final String OO_JSON = bookJson("Oui-Oui et le Gendarme", "", "2-012-00857-7");
    private static final Isbn OO_ISBN = new Isbn("2-012-00857-7");
    //
    private int port;
    private String baseUrl;
    //
    private BookRepository bookRepository;
    private PersonRepository personRepository;
    //
    private HttpClient httpClient;
    private HttpResponse response;
    private HttpGet httpget_listall;
    private HttpPost httppost_create;

    @BeforeClass
    public void startServer() throws InterruptedException {
        bookRepository = new BookRepository();
        personRepository = new PersonRepository();
        BookService bookService = new BookService(bookRepository);
        PersonService personService = new PersonService(personRepository);

        port = PortProvider.acquire();
        CountDownLatch latch = new CountDownLatch(1);
        setPort(port);
        listener(new SwoopServerCountDownOnceStartedListener(latch));
        BookApplication.defineBookRoutes(bookService, personService);
        latch.await();
    }

    @AfterClass
    public void stopServer() throws InterruptedException {
        stop();
        PortProvider.release(port);
    }

    @BeforeMethod
    public void initConnection() {
        httpClient = new DefaultHttpClient();
        bookRepository.deleteAll();
        baseUrl = "http://0.0.0.0:" + port;
        httpget_listall = new HttpGet(baseUrl + "/book/all");
        httppost_create = new HttpPost(baseUrl + "/book");
    }

    @Test
    public void scenario1_listAnEmptyRepository() throws ClientProtocolException, IOException {
        assertListAllContainsInAnyOrder();
    }

    @Test
    public void scenario2_createOneBookAndListIsbns() throws ClientProtocolException, IOException {
        httppost_create.setEntity(new StringEntity(DDD_JSON));
        response = httpClient.execute(httppost_create);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(201));

        consumeResponseAndAssertNoContent();
        assertListAllContainsInAnyOrder(DDD_ISBN);
    }

    @Test
    public void scenario3_createSeveralBooks() throws ClientProtocolException, IOException {
        for (String json : asList(//
                DDD_JSON, PEAA_JSON, EIP_JSON, REFAC_JSON, OO_JSON)) {

            httppost_create.setEntity(new StringEntity(json));
            response = httpClient.execute(httppost_create);
            assertThat(response.getStatusLine().getStatusCode(), equalTo(201));
            consumeResponseAndAssertNoContent();
        }

        assertListAllContainsInAnyOrder(DDD_ISBN, PEAA_ISBN, REFAC_ISBN, EIP_ISBN, OO_ISBN);
    }

    @Test
    public void scenario4_createOneBookAndDeleteIt() throws ClientProtocolException, IOException {
        httppost_create.setEntity(new StringEntity(DDD_JSON));
        response = httpClient.execute(httppost_create);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(201));
        consumeResponseAndAssertNoContent();

        response = httpClient.execute(new HttpDelete(baseUrl+"/book/" + DDD_ISBN.getCode()));
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        consumeResponseAndAssertNoContent();

        assertListAllContainsInAnyOrder();
    }
    
    @Test
    public void scenario5_createOneBookAndModifyIt() throws ClientProtocolException, IOException {
        httppost_create.setEntity(new StringEntity(DDD_JSON));
        response = httpClient.execute(httppost_create);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(201));
        consumeResponseAndAssertNoContent();

        // modify it
        String httpQuery = encodeParam("title","The Blue Book");
        response = httpClient.execute(new HttpPut(baseUrl+"/book/" + DDD_ISBN.getCode() + "?" + httpQuery));
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        consumeResponseAndAssertNoContent();

        // retrieve it
        response = httpClient.execute(new HttpGet(baseUrl+"/book/" + DDD_ISBN.getCode()));
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        byte[] bytes = EntityUtils.toByteArray(response.getEntity());
        Book book = Json.fromJson(bytes, Book.class);
        assertThat(book, notNullValue());
        assertThat(book.getTitle(), equalTo("The Blue Book"));
        assertThat(book.getSubtitle(), equalTo("Tackling Complexity in the Heart of Software"));
    }

    private static String encodeParam(String name, String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(name, UTF8) + "=" + URLEncoder.encode(value, UTF8);
    }

    protected void consumeResponseAndAssertNoContent() throws IOException {
        byte[] bytes = EntityUtils.toByteArray(response.getEntity());
        assertThat("Response should be empty", bytes.length, equalTo(0));
    }

    protected void assertListAllContainsInAnyOrder(Isbn... expectedIsbns) throws IOException, ClientProtocolException {
        HttpResponse response = httpClient.execute(httpget_listall);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));

        // this consumes response
        byte[] bytes = EntityUtils.toByteArray(response.getEntity());
        IsbnCollection isbns = Json.fromJson(bytes, IsbnCollection.class);
        assertThat(isbns.getIsbns(), containsInAnyOrder(expectedIsbns));
        assertThat(isbns.size(), equalTo(expectedIsbns.length));
    }

    public static String bookJson(String title, String subtitle, String isbn) {
        String bookJsonPattern = "{\"title\":\"$title\",\"subtitle\":\"$subtitle\",\"isbn\":{\"code\":\"$isbn\"}}";
        return bookJsonPattern.replace("$title", title)//
                .replace("$subtitle", subtitle)//
                .replace("$isbn", isbn);
    }
}
