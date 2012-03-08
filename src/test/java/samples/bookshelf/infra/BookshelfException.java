package samples.bookshelf.infra;

@SuppressWarnings("serial")
public class BookshelfException extends RuntimeException {

    public BookshelfException() {
        super();
    }

    public BookshelfException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookshelfException(String message) {
        super(message);
    }

    public BookshelfException(Throwable cause) {
        super(cause);
    }

}
