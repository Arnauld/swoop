package samples.bookshelf.domain;

import java.util.Collection;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import swoop.util.New;

public class IsbnCollection {
    private List<Isbn> isbns;
    
    public IsbnCollection() {
    }

    public IsbnCollection(Collection<Isbn> isbns) {
        super();
        this.isbns = New.arrayList(isbns);
    }

    public List<Isbn> getIsbns() {
        return isbns;
    }

    public int size() {
        return isbns.size();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return isbns.isEmpty();
    }
    
}
