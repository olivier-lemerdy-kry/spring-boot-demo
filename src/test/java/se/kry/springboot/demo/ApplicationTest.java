package se.kry.springboot.demo;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;
import se.kry.springboot.demo.data.EventRepository;
import se.kry.springboot.demo.data.PersonRepository;
import se.kry.springboot.demo.domain.EventDefaults;
import se.kry.springboot.demo.domain.PersonDefaults;
import se.kry.springboot.demo.infra.data.mongo.MongoDefaults;

@SpringBootTest
@Testcontainers
@AutoConfigureWebTestClient
class ApplicationTest {

  @Container
  private static final MongoDBContainer mongoDb = new MongoDBContainer(MongoDefaults.DOCKER_IMAGE_NAME);

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private Logger logger;

  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private PersonRepository personRepository;

  @DynamicPropertySource
  static void mongoProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDb::getReplicaSetUrl);
  }

  @Test
  void scenario() throws IOException {
    var eventId = step1_create_event();
    step2_read_events();
    step3_update_event(eventId);
    step4_read_event(eventId);
    var personId = step5_create_person();
    step6_read_people();
    step7_update_person(personId);
    step8_read_person(personId);
    step9_update_event_participants(eventId, personId);
    step10_read_event_participants(eventId);
    step11_update_event_participants_to_empty(eventId);
    step12_delete_person(personId);
    step13_delete_event(eventId);
  }

  UUID step1_create_event() throws IOException {
    logger.info("Starting step1: create event");

    assertEventRepositoryCountIs(0);

    var payload = objectMapper.createObjectNode()
        .put("title", EventDefaults.TITLE)
        .put("startTime", EventDefaults.START_TIME_STRING)
        .put("endTime", EventDefaults.END_TIME_STRING)
        .toString();

    var result = webTestClient.post().uri("/api/v1/events")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payload)
        .exchange()
        .expectStatus().isCreated()
        .expectBody()
        .jsonPath("$.title").isEqualTo(EventDefaults.TITLE)
        .jsonPath("$.startTime").isEqualTo(EventDefaults.START_TIME_STRING)
        .jsonPath("$.endTime").isEqualTo(EventDefaults.END_TIME_STRING)
        .returnResult();

    assertEventRepositoryCountIs(1);

    try (InputStream stream = new ByteArrayInputStream(requireNonNull(result.getResponseBodyContent()))) {
      return UUID.fromString(JsonPath.read(stream, "$.id"));
    } finally {
      logger.info("Ending step1: create event");
    }
  }

  void step2_read_events() {
    logger.info("Starting step2: read events");

    assertEventRepositoryCountIs(1);

    webTestClient.get().uri("/api/v1/events")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.content").isArray()
        .jsonPath("$.content[0].title").isEqualTo(EventDefaults.TITLE)
        .jsonPath("$.content[0].startTime").isEqualTo(EventDefaults.START_TIME_STRING)
        .jsonPath("$.content[0].endTime").isEqualTo(EventDefaults.END_TIME_STRING);

    logger.info("Ending step2: read events");
  }

  void step3_update_event(UUID id) {
    logger.info("Starting step3: update event");

    assertEventRepositoryCountIs(1);

    var payload = objectMapper.createObjectNode()
        .put("title", EventDefaults.OTHER_TITLE)
        .put("startTime", EventDefaults.OTHER_START_TIME_STRING)
        .put("endTime", EventDefaults.OTHER_END_TIME_STRING)
        .toString();

    webTestClient.patch().uri("/api/v1/events/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payload)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.title").isEqualTo(EventDefaults.OTHER_TITLE)
        .jsonPath("$.startTime").isEqualTo(EventDefaults.OTHER_START_TIME_STRING)
        .jsonPath("$.endTime").isEqualTo(EventDefaults.OTHER_END_TIME_STRING);

    logger.info("Ending step3: update event");
  }

  void step4_read_event(UUID id) {
    logger.info("Starting step4: read event");

    assertEventRepositoryCountIs(1);

    webTestClient.get().uri("/api/v1/events/{id}", id)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.title").isEqualTo(EventDefaults.OTHER_TITLE)
        .jsonPath("$.startTime").isEqualTo(EventDefaults.OTHER_START_TIME_STRING)
        .jsonPath("$.endTime").isEqualTo(EventDefaults.OTHER_END_TIME_STRING);

    logger.info("Ending step4: read event");
  }

  UUID step5_create_person() throws IOException {
    logger.info("Starting step5: create person");

    assertPersonRepositoryCountIs(0);

    var payload = objectMapper.createObjectNode()
        .put("name", PersonDefaults.NAME)
        .toString();

    var result = webTestClient.post().uri("/api/v1/people")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payload)
        .exchange()
        .expectStatus().isCreated()
        .expectBody()
        .jsonPath("$.name").isEqualTo(PersonDefaults.NAME)
        .returnResult();

    assertPersonRepositoryCountIs(1);

    try (InputStream stream = new ByteArrayInputStream(requireNonNull(result.getResponseBodyContent()))) {
      return UUID.fromString(JsonPath.read(stream, "$.id"));
    } finally {
      logger.info("Ending step5: create person");
    }
  }

  void step6_read_people() {
    logger.info("Starting step6: read people");

    assertPersonRepositoryCountIs(1);

    webTestClient.get().uri("/api/v1/people")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.content").isArray()
        .jsonPath("$.content[0].name").isEqualTo(PersonDefaults.NAME);

    logger.info("Ending step6: read people");
  }

  void step7_update_person(UUID id) {
    logger.info("Starting step7: update person");

    assertPersonRepositoryCountIs(1);

    var payload = objectMapper.createObjectNode()
        .put("name", PersonDefaults.OTHER_NAME)
        .toString();

    webTestClient.patch().uri("/api/v1/people/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payload)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.name").isEqualTo(PersonDefaults.OTHER_NAME);

    logger.info("Ending step7: update person");
  }

  void step8_read_person(UUID id) {
    logger.info("Starting step8: read person");

    assertPersonRepositoryCountIs(1);

    webTestClient.get().uri("/api/v1/people/{id}", id)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.name").isEqualTo(PersonDefaults.OTHER_NAME);

    logger.info("Ending step8: read person");
  }

  void step9_update_event_participants(UUID eventId, UUID personId) {
    logger.info("Starting step9: update event participants");

    assertEventRepositoryCountIs(1);
    assertPersonRepositoryCountIs(1);

    var payload = objectMapper.createObjectNode()
        .set("personIds", objectMapper.createArrayNode().add(personId.toString()));

    webTestClient.put().uri("/api/v1/events/{id}/participants", eventId)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payload)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$").isArray()
        .jsonPath("$[0].id").isEqualTo(personId.toString())
        .jsonPath("$[0].name").isEqualTo(PersonDefaults.OTHER_NAME);

    logger.info("Ending step9: update event participants");
  }

  void step10_read_event_participants(UUID eventId) {
    logger.info("Starting step10: read event participants");

    webTestClient.get().uri("/api/v1/events/{id}/participants", eventId)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$").isArray()
        .jsonPath("$[0].id").isNotEmpty()
        .jsonPath("$[0].name").isEqualTo(PersonDefaults.OTHER_NAME);

    logger.info("Ending step10: read event participants");
  }

  void step11_update_event_participants_to_empty(UUID eventId) {
    logger.info("Starting step11: update event participants to empty");

    assertEventRepositoryCountIs(1);
    assertPersonRepositoryCountIs(1);

    var payload = objectMapper.createObjectNode()
        .set("personIds", objectMapper.createArrayNode());

    webTestClient.put().uri("/api/v1/events/{id}/participants", eventId)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payload)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$").isArray()
        .jsonPath("$").isEmpty();

    logger.info("Ending step11: update event participants to empty");
  }

  void step12_delete_person(UUID id) {
    logger.info("Starting step12: delete person");

    assertPersonRepositoryCountIs(1);

    webTestClient.delete().uri("/api/v1/people/{id}", id)
        .exchange()
        .expectStatus().isOk();

    assertPersonRepositoryCountIs(0);

    logger.info("Ending step12: delete person");
  }

  void step13_delete_event(UUID id) {
    logger.info("Starting step13: delete event");

    assertEventRepositoryCountIs(1);

    webTestClient.delete().uri("/api/v1/events/{id}", id)
        .exchange()
        .expectStatus().isOk();

    assertEventRepositoryCountIs(0);

    logger.info("Ending step13: delete event");
  }

  private void assertEventRepositoryCountIs(long value) {
    eventRepository.count()
        .as(StepVerifier::create)
        .assertNext(count -> assertThat(count).isEqualTo(value))
        .verifyComplete();
  }

  private void assertPersonRepositoryCountIs(long value) {
    personRepository.count()
        .as(StepVerifier::create)
        .assertNext(count -> assertThat(count).isEqualTo(value))
        .verifyComplete();
  }
}
