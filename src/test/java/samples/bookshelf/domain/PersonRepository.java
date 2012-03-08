package samples.bookshelf.domain;

import java.util.Map;

import samples.bookshelf.infra.BookshelfException;
import swoop.util.New;

public class PersonRepository {

    private Map<String,Person> personPerLogin = New.concurrentHashMap();
    
    public Person findByLogin(String login) {
        return personPerLogin.get(login);
    }
    
    public void save(Person person) {
        String login = person.getLogin();
        if(login==null)
            throw new BookshelfException("Login is required to save a person");
        personPerLogin.put(login, person);
    }

}
