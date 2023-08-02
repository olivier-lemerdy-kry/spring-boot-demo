package se.kry.springboot.demo.infra.data.postgres;

public interface PostgresDefaults {

  String VERSION = "15";

  String DOCKER_IMAGE_NAME = "postgres:" + VERSION;
}
