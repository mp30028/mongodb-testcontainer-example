package com.zonesoft.example.repositories;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.zonesoft.example.entities.Person;

@Repository
public interface PersonRepository extends ReactiveMongoRepository<Person, String> {

}
