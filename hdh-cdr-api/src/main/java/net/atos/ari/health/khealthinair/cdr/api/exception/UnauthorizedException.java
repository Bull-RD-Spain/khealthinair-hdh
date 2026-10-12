package net.atos.ari.health.khealthinair.cdr.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Used for returning 401 in unauthorized requests
 *
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public UnauthorizedException(String message) {
    super(message);
  }
}
