package ru.test.SpringSecurityApplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.test.SpringSecurityApplication.exception.UserAlreadyExistException;
import ru.test.SpringSecurityApplication.model.entity.Person;
import ru.test.SpringSecurityApplication.repository.PersonRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private PasswordEncoder encoder;
    private PersonRepository repository;
    private UserService service;

    @BeforeEach
    public void setup(){
        repository = Mockito.mock(PersonRepository.class);
        encoder = new BCryptPasswordEncoder();
        service = new UserService(repository,encoder);
    }

    @Test
    public void creteUserTestAndExpectSuccess(){
        Person test = Person.builder().id(1).name("test").password(encoder.encode("pass")).build();

        Mockito.when(repository.findPersonByName(Mockito.anyString())).thenReturn(Optional.ofNullable(null));
        Mockito.when(repository.save(Mockito.any())).thenReturn(test);

        Person user = service.createUser(test);

        assertEquals(test, user);
    }

    @Test
    public void creteUserTestAndExpectException(){
        Person test = Person.builder().id(1).name("test").password(encoder.encode("pass")).build();

        Mockito.when(repository.findPersonByName(Mockito.anyString())).thenReturn(Optional.ofNullable(test));

        assertThrows(UserAlreadyExistException.class, ()->service.createUser(test));

    }


    @Test
    public void getUserByIdTest(){
        Person test = Person.builder().id(1).name("test").password(encoder.encode("pass")).build();
        Optional<Person> optional = Optional.ofNullable(test);

        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(optional);

        Optional<Person> userById = service.getUserById(test.getId());

        assertEquals(optional, userById);
    }

    @Test
    public void getUserByNameTest(){
        Person test = Person.builder().id(1).name("test").password(encoder.encode("pass")).build();
        Optional<Person> optional = Optional.ofNullable(test);

        Mockito.when(repository.findPersonByName(Mockito.anyString())).thenReturn(optional);

        Optional<Person> userById = service.getUserByName(test.getName());

        assertEquals(optional, userById);
    }

}