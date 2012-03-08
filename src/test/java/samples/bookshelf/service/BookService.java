package samples.bookshelf.service;

import java.util.List;
import java.util.Set;

import samples.bookshelf.domain.Book;
import samples.bookshelf.domain.BookRepository;
import samples.bookshelf.domain.Isbn;
import samples.bookshelf.infra.BookshelfException;

public class BookService {
    
    private BookRepository repository;
    

    public BookService(BookRepository repository) {
        super();
        this.repository = repository;
    }
    
    public Set<Isbn> listAllIsbns() {
        return repository.listAllIsbns();
    }

    public List<Book> findByTitle(String title) {
        return repository.findByTitle(title);
    }
    
    public Book findByIsbn(Isbn isbn) {
        return repository.findByIsbn(isbn);
    }
    
    public void delete(Book book) {
        Isbn isbn = book.getIsbn();
        if(isbn==null)
            throw new BookshelfException("Isbn is required to delete a book");
        repository.delete(book);
    }
    
    public void modify(Book book) {
        Isbn isbn = book.getIsbn();
        if(isbn==null)
            throw new BookshelfException("Isbn is required to modify a book");
        Book existing = repository.findByIsbn(isbn);
        if(existing==null)
            throw new BookshelfException("No book with this isbn exists");
        repository.save(book);
    }
    
    public void create(Book book) {
        Isbn isbn = book.getIsbn();
        if(isbn==null)
            throw new BookshelfException("Isbn is required to create a book");
        Book existing = repository.findByIsbn(isbn);
        if(existing!=null)
            throw new BookshelfException("A book with this isbn already exists");
        repository.save(book);
    }

}
