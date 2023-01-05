package se.kry.springboot.demo.services;

import static se.kry.springboot.demo.services.PersonFunctions.newPersonFromCreationRequest;
import static se.kry.springboot.demo.services.PersonFunctions.updatePersonFromUpdateRequest;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import se.kry.springboot.demo.data.PersonRepository;
import se.kry.springboot.demo.domain.PersonCreationRequest;
import se.kry.springboot.demo.domain.PersonResponse;
import se.kry.springboot.demo.domain.PersonUpdateRequest;
import se.kry.springboot.demo.util.ReactivePreconditions;

@Service
@RequiredArgsConstructor
public class PersonService {

  private final PersonRepository repository;

  @Transactional
  public Mono<PersonResponse> createPerson(@NotNull PersonCreationRequest personCreationRequest) {
    return ReactivePreconditions.requireNonNull(personCreationRequest).flatMap(p ->
        repository.save(newPersonFromCreationRequest(personCreationRequest))
            .map(PersonFunctions::responseFromPerson));
  }

  public Mono<Page<PersonResponse>> getPeople(@NotNull Pageable pageable) {
    return ReactivePreconditions.requireNonNull(pageable).flatMap(p ->
        Mono.zip(
            repository.count(),
            repository.findBy(pageable).collectList(),
            (count, list) -> new PageImpl<>(list, pageable, count).map(PersonFunctions::responseFromPerson)));
  }

  public Mono<PersonResponse> getPerson(@NotNull UUID id) {
    return ReactivePreconditions.requireNonNull(id)
        .flatMap(p -> repository.findById(id))
        .map(PersonFunctions::responseFromPerson);
  }

  @Transactional
  public Mono<PersonResponse> updatePerson(@NotNull UUID id, @NotNull PersonUpdateRequest personUpdateRequest) {
    return ReactivePreconditions.requireNonNull(id, personUpdateRequest).flatMap(p ->
        repository.findById(id)
            .map(person -> updatePersonFromUpdateRequest(person, personUpdateRequest))
            .flatMap(repository::save)
            .map(PersonFunctions::responseFromPerson));
  }

  @Transactional
  public Mono<Void> deletePerson(@NotNull UUID id) {
    return ReactivePreconditions.requireNonNull(id).flatMap(p -> repository.deleteById(id));
  }
}
