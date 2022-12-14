package se.kry.springboot.demo.services;

import jakarta.validation.constraints.NotNull;
import se.kry.springboot.demo.data.Person;
import se.kry.springboot.demo.domain.PersonCreationRequest;
import se.kry.springboot.demo.domain.PersonResponse;
import se.kry.springboot.demo.domain.PersonUpdateRequest;

enum PersonFunctions {
  ;

  static Person newPersonFromCreationRequest(@NotNull PersonCreationRequest personCreationRequest) {
    return Person.from(personCreationRequest.name());
  }

  static PersonResponse responseFromPerson(Person person) {
    return new PersonResponse(person.id(), person.name());
  }

  static Person updatePersonFromUpdateRequest(Person person, PersonUpdateRequest personUpdateRequest) {
    return person.withName(personUpdateRequest.name().orElse(person.name()));
  }
}
