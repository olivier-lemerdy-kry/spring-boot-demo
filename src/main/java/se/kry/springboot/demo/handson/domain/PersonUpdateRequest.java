package se.kry.springboot.demo.handson.domain;

import jakarta.validation.constraints.Size;
import java.util.Optional;

public record PersonUpdateRequest(Optional<@Size(max = PersonConstants.Sizes.NAME) String> name) {
}
