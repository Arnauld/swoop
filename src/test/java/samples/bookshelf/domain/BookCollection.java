package samples.bookshelf.domain;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class BookCollection {
    private List<Book> books;
    
    public BookCollection() {
    }

    public BookCollection(List<Book> books) {
        super();
        this.books = books;
    }

    public List<Book> getBooks() {
        return books;
    }
    
    public int size() {
        return books.size();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return books.isEmpty();
    }

}
