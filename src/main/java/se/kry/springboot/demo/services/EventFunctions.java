package se.kry.springboot.demo.services;

import jakarta.validation.constraints.NotNull;
import se.kry.springboot.demo.data.Event;
import se.kry.springboot.demo.domain.EventCreationRequest;
import se.kry.springboot.demo.domain.EventParticipantsUpdateRequest;
import se.kry.springboot.demo.domain.EventResponse;
import se.kry.springboot.demo.domain.EventUpdateRequest;

public enum EventFunctions {
  ;

  static Event newEventFromCreationRequest(@NotNull EventCreationRequest eventCreationRequest) {
    return Event.from(
        eventCreationRequest.title(),
        eventCreationRequest.startTime(),
        eventCreationRequest.endTime());
  }

  static Event updateEventFromUpdateRequest(@NotNull Event event, @NotNull EventUpdateRequest updateRequest) {
    var builder = event.toBuilder();
    updateRequest.title().ifPresent(builder::title);
    updateRequest.startTime().ifPresent(builder::startTime);
    updateRequest.endTime().ifPresent(builder::endTime);
    return builder.build();
  }

  static Event updateEventParticipantsFromUpdateRequest(@NotNull Event event,
                                                        @NotNull EventParticipantsUpdateRequest updateRequest) {
    return event.toBuilder().participantIds(updateRequest.personIds()).build();
  }

  static EventResponse responseFromEvent(Event event) {
    return new EventResponse(event.id(), event.title(), event.startTime(), event.endTime());
  }
}
