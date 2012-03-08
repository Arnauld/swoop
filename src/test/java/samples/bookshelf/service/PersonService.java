package samples.bookshelf.service;

import samples.bookshelf.domain.Person;
import samples.bookshelf.domain.PersonRepository;
import samples.bookshelf.infra.BookshelfException;

public class PersonService {
    
    private PersonRepository repository;

    public PersonService(PersonRepository repository) {
        super();
        this.repository = repository;
    }

    public Person findByLogin(String login) {
        return repository.findByLogin(login);
    }
    
    public void modify(Person person) {
        String login = person.getLogin();
        if(login==null)
            throw new BookshelfException("Login is required to modify a person");
        Person existing = repository.findByLogin(login);
        if(existing==null)
            throw new BookshelfException("Nobody with this login exists");
        repository.save(person);
    }
    
    public void create(Person person) {
        String login = person.getLogin();
        if(login==null)
            throw new BookshelfException("Login is required to create a person");
        Person existing = repository.findByLogin(login);
        if(existing!=null)
            throw new BookshelfException("Person with the same login already exists");
        repository.save(person);
    }
}
