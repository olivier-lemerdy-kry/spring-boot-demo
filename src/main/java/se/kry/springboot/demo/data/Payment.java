package se.kry.springboot.demo.data;

import jakarta.persistence.Entity;
import java.util.UUID;
import org.springframework.data.jpa.domain.AbstractAuditable;

@Entity
public class Payment extends AbstractAuditable<String, UUID> {
}
