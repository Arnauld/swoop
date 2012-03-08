package samples.bookshelf.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;

import samples.bookshelf.infra.BookshelfException;
import swoop.util.New;

public class BookRepository {
    private Map<Isbn, Book> bookPerIsbn = New.concurrentHashMap();
    
    public Set<Isbn> listAllIsbns() {
        return bookPerIsbn.keySet();
    }
    
    public void deleteAll() {
        bookPerIsbn.clear();
    }
    
    public void delete(Book book) {
        Isbn isbn = book.getIsbn();
        if(isbn==null)
            throw new BookshelfException("Isbn is required to delete a book");
        bookPerIsbn.remove(isbn);
    }
    
    public void save(Book book) {
        Isbn isbn = book.getIsbn();
        if(isbn==null)
            throw new BookshelfException("Isbn is required to save a book");
        bookPerIsbn.put(isbn, book);
    }

    public Book findByIsbn(Isbn isbn) {
        return bookPerIsbn.get(isbn);
    }
    
    public List<Book> findByTitle(String title) {
        List<Book> founds = New.arrayList();
        for(Book book : bookPerIsbn.values()) {
            if(title.equalsIgnoreCase(book.getTitle()))
                founds.add(book);
        }
        return founds;
    }

}
