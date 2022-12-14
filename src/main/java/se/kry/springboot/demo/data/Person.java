package se.kry.springboot.demo.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;
import lombok.With;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import se.kry.springboot.demo.domain.PersonConstants;

@Document
@With
public record Person(@MongoId UUID id,
                     @NotBlank @Size(max = PersonConstants.Sizes.NAME) String name,
                     @CreatedDate Instant createdDate,
                     @LastModifiedDate Instant lastModifiedDate) implements Persistable<UUID> {

  public static Person from(@NotBlank @Size(max = PersonConstants.Sizes.NAME) String name) {
    return Person.from(UUID.randomUUID(), name);
  }

  public static Person from(@NotNull UUID id,
                            @NotBlank @Size(max = PersonConstants.Sizes.NAME) String name) {
    return new Person(id, name, null, null);
  }

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public boolean isNew() {
    return createdDate == null;
  }
}
