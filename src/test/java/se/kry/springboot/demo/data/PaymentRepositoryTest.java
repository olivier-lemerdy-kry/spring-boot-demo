package se.kry.springboot.demo.data;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.kry.springboot.demo.infra.data.postgres.PostgresDefaults;

@DataJpaTest
@Testcontainers
class PaymentRepositoryTest {

  @Container
  @ServiceConnection
  private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(PostgresDefaults.DOCKER_IMAGE_NAME);

  @Autowired
  private PaymentRepository repository;

  @Test
  void test() {
  }
}
