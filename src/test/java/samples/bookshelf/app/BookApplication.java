package samples.bookshelf.app;

import static swoop.Swoop.delete;
import static swoop.Swoop.get;
import static swoop.Swoop.post;
import static swoop.Swoop.put;

import java.util.Set;

import samples.Json;
import samples.bookshelf.domain.Book;
import samples.bookshelf.domain.BookRepository;
import samples.bookshelf.domain.Isbn;
import samples.bookshelf.domain.IsbnCollection;
import samples.bookshelf.domain.PersonRepository;
import samples.bookshelf.service.BookService;
import samples.bookshelf.service.PersonService;
import swoop.Action;
import swoop.Request;
import swoop.Response;

public class BookApplication {
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        BookService bookService = new BookService(new BookRepository());
        PersonService personService = new PersonService(new PersonRepository());

        // application automatically starts once routes are defined
        defineBookRoutes(bookService, personService);
    }
    
    public static void defineBookRoutes(final BookService bookService, final PersonService personService) {
        
        // Gets all available book resources (id's)
        // this route is defined first since it could be catched by '/book/:isbn' with isbn=all
        get(new Action("/book/all") {
            @Override
            public void handle(Request request, Response response) {
                Set<Isbn> listAllIsbns = bookService.listAllIsbns();
                String json = Json.toJson(new IsbnCollection(listAllIsbns));
                response.body(json);
            }
        });

        
        // Creates a new book resource, will return the ID to the created resource
        // author and title are sent as query parameters e.g. /books?author=Foo&title=Bar
        post(new Action("/book") {
            @Override
            public void handle(Request request, Response response) {
                Book book = Json.fromJson(request.body(), Book.class);
                bookService.create(book);
                response.status(201); // 201 Created
            }
        });
        
        // Gets the book resource for the provided isbn
        get(new Action("/book/:isbn") {
            @Override
            public void handle(Request request, Response response) {
                String isbn = request.routeParam("isbn");
                Book book = bookService.findByIsbn(new Isbn(isbn));
                if (book != null) {
                    String json = Json.toJson(book);
                    response.body(json);
                    return;
                }
                
                response.status(404); // 404 Not found
            }
        });
        
        // Updates the book resource for the provided id with new information
        // author and title are sent as query parameters e.g. /book/<id>?title=Bar&subtitle=Foo
        put(new Action("/book/:isbn") {
            @Override
            public void handle(Request request, Response response) {
                String isbn = request.routeParam("isbn");
                Book book = bookService.findByIsbn(new Isbn(isbn));
                if (book != null) {
                    String newTitle = request.queryParam("title");
                    if(newTitle!=null) {
                        book.setTitle(newTitle);
                    }
                    String newSubtitle = request.queryParam("subtitle");
                    if(newSubtitle!=null) {
                        book.setSubtitle(newSubtitle);
                    }
                    bookService.modify(book);
                    return;
                }
                
                response.status(404); // 404 Not found
            }
        });
        
        // Deletes the book resource for the provided id 
        delete(new Action("/book/:isbn") {
            @Override
            public void handle(Request request, Response response) {
                String isbn = request.routeParam("isbn");
                Book book = bookService.findByIsbn(new Isbn(isbn));
                if (book != null) {
                    bookService.delete(book);
                    return;
                }
                
                response.status(404); // 404 Not found
            }
        });
        
    }
}
