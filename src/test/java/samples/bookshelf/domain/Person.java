package samples.bookshelf.domain;

public class Person {
    private String login, password;
    private String firstname, lastname;
    
    public Person() {
    }
    
    public Person(String login, String password, String firstname, String lastname) {
        super();
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }
    
    public String getLogin() {
        return login;
    }
    
    public String getPassword() {
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
    
}
