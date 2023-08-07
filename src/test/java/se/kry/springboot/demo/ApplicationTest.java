package se.kry.springboot.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.kry.springboot.demo.infra.data.postgres.PostgresDefaults;

@SpringBootTest
@Testcontainers
class ApplicationTest {

  @Container
  @ServiceConnection
  private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(PostgresDefaults.DOCKER_IMAGE_NAME);

  @Test
  void contextLoads() {
  }

}
