package samples.bookshelf.domain;

import java.util.List;

import swoop.util.New;

public class Book {
    private String title;
    private String subtitle;
    private Isbn isbn;
    private List<Person> authors;
    
    public Book() {
    }
    
    public Book(Isbn isbn, String title, String subtitle) {
        this(isbn, title, subtitle, New.<Person>arrayList());
    }
    
    public Book(Isbn isbn, String title, String subtitle, List<Person> authors) {
        super();
        this.isbn = isbn;
        this.title = title;
        this.subtitle = subtitle;
        this.authors = authors;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }
    public String getSubtitle() {
        return subtitle;
    }
    public Isbn getIsbn() {
        return isbn;
    }
    public List<Person> getAuthors() {
        return authors;
    }
}
