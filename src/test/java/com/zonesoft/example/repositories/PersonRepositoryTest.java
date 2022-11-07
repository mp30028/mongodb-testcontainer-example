package com.zonesoft.example.repositories;

import static com.zonesoft.example.data_generators.GeneratorCore.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.zonesoft.example.entities.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Testcontainers()
@DataMongoTest
class PersonRepositoryTest{
	private static final Logger LOGGER = LoggerFactory.getLogger(PersonRepositoryTest.class);
	
	private static final String IMAGE_NAME = "mongo:4.4.3";

    private static final MongoDBContainer MONGODB_CONTAINER;
    static {
    	LOGGER.debug("From MONGODB_CONTAINER initialisation");
    	MONGODB_CONTAINER = new MongoDBContainer(DockerImageName.parse(IMAGE_NAME));
        MONGODB_CONTAINER.start();
    }

	
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {		
		registry.add("spring.data.mongodb.uri", MONGODB_CONTAINER::getReplicaSetUrl);  
	}
	
	@Autowired
	private PersonRepository personRepository;
	
	@Test
	void testRepositoryGetsAutowired() {
		assertNotNull(personRepository);
	}

	
	@Test
	void testInsertSomeDataAndThenDoFindAll() {
		final int MAX_PERSONS = 7;
		final int MIN_PERSONS = 2;
		int numberOfPersons = generateRandomInt(MIN_PERSONS, MAX_PERSONS);
		for (int j=0; j < numberOfPersons; j++) {
			createPersonData().block(); // Wait until person is created
		}
		Flux<Person> searchResultsFlux = personRepository.findAll();
		List<Person> result = searchResultsFlux.collectList().block(); // Wait to collect all results
		LOGGER.debug("searchResults ={}", result);
	}

	
	
	private Mono<Person> createPersonData() {
		Person person = new Person(generateNickname(), generateFirstName(generateGender()), generateLastName());
		Mono<Person> createdPerson =  personRepository.insert(person);
		return createdPerson;
	}
}