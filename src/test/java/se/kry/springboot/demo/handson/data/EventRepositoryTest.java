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
import se.kry.springboot.demo.handson.domain.EventDefaults;

@DataMongoTest
@Testcontainers
class EventRepositoryTest {

  @Container
  private static final MongoDBContainer mongoDb = new MongoDBContainer("mongo:4.0.10");

  @Autowired
  private ReactiveMongoTemplate template;

  @Autowired
  private EventRepository repository;

  @DynamicPropertySource
  static void mongoProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDb::getReplicaSetUrl);
  }

  @Test
  void find_event_by_id() {

    // Given
    template.insert(
            new Event(EventDefaults.ID, EventDefaults.TITLE,
                EventDefaults.START_TIME, EventDefaults.END_TIME,
                EventDefaults.PARTICIPANT_IDS,
                EventDefaults.CREATED_DATE, EventDefaults.LAST_MODIFIED_DATE))

        // When
        .then(repository.findById(EventDefaults.ID))

        // Then
        .as(StepVerifier::create).assertNext(actual -> {
          assertThat(actual.id()).isEqualTo(EventDefaults.ID);
          assertThat(actual.title()).isEqualTo(EventDefaults.TITLE);
          assertThat(actual.startTime()).hasToString("2001-01-01T12:00");
          assertThat(actual.endTime()).hasToString("2001-01-01T13:00");
        }).verifyComplete();
  }

  @Test
  void find_all_events_by_pageable() {

    // Given
    template.insertAll(IntStream.range(0, 50)
            .mapToObj(i -> Event.from("Event" + i,
                EventDefaults.START_TIME.plusDays(i),
                EventDefaults.START_TIME.plusDays(i).plusHours(1)))
            .toList())

        // When
        .thenMany(repository.findBy(Pageable.ofSize(20)))

        // Then
        .collectList()
        .as(StepVerifier::create)
        .assertNext(events ->
            assertThat(events).hasSize(20)
        ).verifyComplete();
  }

  @Test
  void save_event() {

    // Given
    var event = Event.from(EventDefaults.TITLE, EventDefaults.START_TIME, EventDefaults.END_TIME);

    // When
    repository.save(event)

        // Then
        .as(StepVerifier::create)
        .assertNext(actual -> {
          assertThat(actual).isNotNull();
          assertThat(actual.id()).isNotNull();
          assertThat(actual.title()).isEqualTo(EventDefaults.TITLE);
          assertThat(actual.startTime()).hasToString("2001-01-01T12:00");
          assertThat(actual.endTime()).hasToString("2001-01-01T13:00");
        }).verifyComplete();
  }

}