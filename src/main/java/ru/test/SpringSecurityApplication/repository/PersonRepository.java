package ru.test.SpringSecurityApplication.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.SpringSecurityApplication.model.entity.Person;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person,Long> {

    Optional<Person> findPersonByName(String name);
}
