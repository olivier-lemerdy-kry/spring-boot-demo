package se.kry.springboot.demo.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public record EventResponse(@NotNull UUID id,
                            @NotBlank @Size(max = EventConstants.Sizes.TITLE) String title,
                            @NotNull LocalDateTime startTime,
                            @NotNull LocalDateTime endTime) {
}
