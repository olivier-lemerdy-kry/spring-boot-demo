package se.kry.springboot.demo.handson.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record EventCreationRequest(
    @NotBlank @Size(max = EventConstants.Sizes.TITLE) String title,
    @NotNull LocalDateTime startTime,
    @NotNull LocalDateTime endTime) {

  public EventCreationRequest {
    if (startTime.isAfter(endTime)) {
      throw new StartIsAfterEndException(startTime, endTime);
    }
  }
}
