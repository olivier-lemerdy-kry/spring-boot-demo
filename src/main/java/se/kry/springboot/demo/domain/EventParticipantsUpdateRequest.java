package se.kry.springboot.demo.domain;

import java.util.List;
import java.util.UUID;

public record EventParticipantsUpdateRequest(List<UUID> personIds) {
}
