package se.kry.springboot.demo.data;

import static java.util.Collections.emptyList;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import se.kry.springboot.demo.domain.EventConstants;

@Document
@Builder(toBuilder = true)
public record Event(@MongoId UUID id,
                    @NotBlank @Size(max = EventConstants.Sizes.TITLE) String title,
                    @NotNull LocalDateTime startTime,
                    @NotNull LocalDateTime endTime,
                    @NotNull List<UUID> participantIds,
                    @CreatedDate Instant createdDate,
                    @LastModifiedDate Instant lastModifiedDate) implements Persistable<UUID> {

  public static Event from(@NotBlank @Size(max = EventConstants.Sizes.TITLE) String title,
                           @NotNull LocalDateTime start,
                           @NotNull LocalDateTime end) {
    return Event.from(UUID.randomUUID(), title, start, end);
  }

  public static Event from(@NotNull UUID id,
                           @NotBlank @Size(max = EventConstants.Sizes.TITLE) String title,
                           @NotNull LocalDateTime start,
                           @NotNull LocalDateTime end) {
    return new Event(id, title, start, end, emptyList(), null, null);
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
