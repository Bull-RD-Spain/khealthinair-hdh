package net.atos.ari.health.khealthinair.cdr.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Used for returning 404 in requests including non existing Subject
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SubjectNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public SubjectNotFoundException(String message) {
    super(message);
  }
}
