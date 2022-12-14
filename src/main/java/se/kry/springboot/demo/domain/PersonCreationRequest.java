package se.kry.springboot.demo.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PersonCreationRequest(@NotBlank @Size(max = PersonConstants.Sizes.NAME) String name) {
}
