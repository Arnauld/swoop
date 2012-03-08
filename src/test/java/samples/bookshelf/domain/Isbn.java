package samples.bookshelf.domain;

public class Isbn {
    private String code;
    
    public Isbn() {
    }
    
    public Isbn(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }
    @Override
    public String toString() {
        return "Isbn@"+code;
    }
    @Override
    public int hashCode() {
        return code==null?0:code.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Isbn other = (Isbn) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        return true;
    }
}
