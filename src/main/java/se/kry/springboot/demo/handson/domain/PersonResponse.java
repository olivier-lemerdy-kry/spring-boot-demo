package se.kry.springboot.demo.handson.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record PersonResponse(@NotNull UUID id,
                             @NotBlank @Size(max = PersonConstants.Sizes.NAME) String name) {
}
