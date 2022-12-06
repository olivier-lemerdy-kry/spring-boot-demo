package se.kry.springboot.demo.services;

import static se.kry.springboot.demo.util.ReactivePreconditions.requireNonNull;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.kry.springboot.demo.data.Event;
import se.kry.springboot.demo.data.EventRepository;
import se.kry.springboot.demo.domain.EventCreationRequest;
import se.kry.springboot.demo.domain.EventParticipantsUpdateRequest;
import se.kry.springboot.demo.domain.EventResponse;
import se.kry.springboot.demo.domain.EventUpdateRequest;
import se.kry.springboot.demo.data.PersonRepository;
import se.kry.springboot.demo.domain.PersonResponse;
import se.kry.springboot.demo.util.ReactivePreconditions;

@Service
public class EventService {

  private final EventRepository eventRepository;

  private final PersonRepository personRepository;

  public EventService(EventRepository eventRepository,
                      PersonRepository personRepository) {
    this.eventRepository = eventRepository;
    this.personRepository = personRepository;
  }

  @Transactional
  public Mono<EventResponse> createEvent(@NotNull EventCreationRequest eventCreationRequest) {
    return ReactivePreconditions.requireNonNull(eventCreationRequest).flatMap(p ->
        eventRepository.save(EventFunctions.newEventFromCreationRequest(eventCreationRequest))
            .map(EventFunctions::responseFromEvent));
  }

  public Mono<Page<EventResponse>> getEvents(@NotNull Pageable pageable) {
    return ReactivePreconditions.requireNonNull(pageable).flatMap(p ->
        Mono.zip(
            eventRepository.count(),
            eventRepository.findBy(pageable).collectList(),
            (count, list) -> new PageImpl<>(list, pageable, count).map(EventFunctions::responseFromEvent)));
  }

  public Mono<EventResponse> getEvent(@NotNull UUID id) {
    return ReactivePreconditions.requireNonNull(id)
        .flatMap(p -> eventRepository.findById(id))
        .map(EventFunctions::responseFromEvent);
  }

  public Flux<PersonResponse> getEventParticipants(@NotNull UUID id) {
    return ReactivePreconditions.requireNonNull(id).flatMapMany(p ->
        eventRepository.findById(id)
            .map(Event::participantIds)
            .flatMapMany(personRepository::findAllById)
            .map(PersonFunctions::responseFromPerson));
  }

  @Transactional
  public Mono<EventResponse> updateEvent(@NotNull UUID id, @NotNull EventUpdateRequest eventUpdateRequest) {
    return ReactivePreconditions.requireNonNull(id, eventUpdateRequest).flatMap(p ->
        eventRepository.findById(id)
            .map(event -> EventFunctions.updateEventFromUpdateRequest(event, eventUpdateRequest))
            .flatMap(eventRepository::save)
            .map(EventFunctions::responseFromEvent));
  }

  @Transactional
  public Flux<PersonResponse> updateEventParticipants(UUID eventId, EventParticipantsUpdateRequest request) {
    return ReactivePreconditions.requireNonNull(eventId, request).flatMapMany(p ->
        eventRepository.findById(eventId)
            .map(event -> EventFunctions.updateEventParticipantsFromUpdateRequest(event, request))
            .flatMap(eventRepository::save)
            .map(Event::participantIds)
            .flatMapMany(personRepository::findAllById)
            .map(PersonFunctions::responseFromPerson));
  }

  @Transactional
  public Mono<Void> deleteEvent(@NotNull UUID id) {
    return ReactivePreconditions.requireNonNull(id).flatMap(p ->
        eventRepository.deleteById(id));
  }
}
