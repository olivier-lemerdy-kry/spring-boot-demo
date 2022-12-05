package se.kry.springboot.demo.handson.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;
import se.kry.springboot.demo.handson.domain.PersonDefaults;

@DataMongoTest
@Testcontainers
class PersonRepositoryTest {

  @Container
  private static final MongoDBContainer mongoDb = new MongoDBContainer("mongo:5.0");

  @Autowired
  private ReactiveMongoTemplate template;

  @Autowired
  private PersonRepository repository;

  @DynamicPropertySource
  static void mongoProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDb::getReplicaSetUrl);
  }

  @Test
  void save_person() {
    repository.save(Person.from(PersonDefaults.NAME))
        .as(StepVerifier::create)
        .assertNext(person -> {
          assertThat(person.id()).isNotNull();
          assertThat(person.name()).isEqualTo(PersonDefaults.NAME);
        })
        .verifyComplete();
  }

  @Test
  void find_all_people_by_pageable() {

    // Given
    template.insertAll(IntStream.range(0, 50)
            .mapToObj(i -> Person.from(PersonDefaults.NAME + ' ' + i))
            .toList())

        // When
        .thenMany(repository.findBy(Pageable.ofSize(20)))

        // Then
        .collectList()
        .as(StepVerifier::create)
        .assertNext(people ->
            assertThat(people).hasSize(20)
        ).verifyComplete();
  }

}