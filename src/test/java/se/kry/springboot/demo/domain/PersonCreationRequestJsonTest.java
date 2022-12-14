package se.kry.springboot.demo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class PersonCreationRequestJsonTest {

  @Autowired
  private JacksonTester<PersonCreationRequest> jacksonTester;

  @Test
  void deserialize() throws IOException {
    var personCreationRequest = jacksonTester.readObject("PersonCreationRequest.json");

    assertThat(personCreationRequest).isNotNull();
    assertThat(personCreationRequest.name()).isEqualTo(PersonDefaults.NAME);
  }

  @Test
  void serialize() throws IOException {
    var jsonContent = jacksonTester.write(
        new PersonCreationRequest(PersonDefaults.NAME));

    assertThat(jsonContent).isEqualToJson("PersonUpdateRequest.json");
  }
}
