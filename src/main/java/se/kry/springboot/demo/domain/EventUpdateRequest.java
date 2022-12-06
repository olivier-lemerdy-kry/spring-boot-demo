package se.kry.springboot.demo.domain;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Optional;

public record EventUpdateRequest(
    Optional<@Size(max = EventConstants.Sizes.TITLE) String> title,
    Optional<LocalDateTime> startTime,
    Optional<LocalDateTime> endTime) {

  public EventUpdateRequest {
    startTime.ifPresent(s -> endTime.ifPresent(e -> {
      if (s.isAfter(e)) {
        throw new StartIsAfterEndException(s, e);
      }
    }));
  }
}
