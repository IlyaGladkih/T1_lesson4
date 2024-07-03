package ru.test.SpringSecurityApplication.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.test.SpringSecurityApplication.exception.UserAlreadyExistException;
import ru.test.SpringSecurityApplication.model.entity.Person;
import ru.test.SpringSecurityApplication.repository.PersonRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PersonRepository repository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Person createUser(Person person){
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        Optional<Person> personByName = repository.findPersonByName(person.getName());
        if (personByName.isEmpty()) return repository.save(person);
        else throw new UserAlreadyExistException("User with this name already exists");
    }

    @Transactional
    public Optional<Person> getUserById(Long id){
        return repository.findById(id);
    }

    @Transactional
    public Optional<Person> getUserByName(String name){
        return repository.findPersonByName(name);
    }
}
