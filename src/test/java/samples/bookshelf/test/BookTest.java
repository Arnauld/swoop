package samples.bookshelf.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static samples.Json.toJson;

import org.testng.annotations.Test;

import samples.bookshelf.domain.Book;
import samples.bookshelf.domain.Isbn;

public class BookTest {

    private static String dddJson = "{\"title\":\"Domain-Driven Design\",\"subtitle\":\"Tackling Complexity in the Heart of Software\",\"isbn\":{\"code\":\"0-321-12521-5\"},\"authors\":[]}";

    @Test
    public void bookToJson() {
        Isbn isbn = new Isbn("0-321-12521-5");
        Book book = new Book(isbn, "Domain-Driven Design", "Tackling Complexity in the Heart of Software");
        assertThat(toJson(book), equalTo(dddJson));
    }
}
