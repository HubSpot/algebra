package com.hubspot.algebra;

import java.util.Optional;

import org.immutables.value.Value.Check;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.hubspot.immutables.styles.HubSpotStyle;

@Immutable
@HubSpotStyle
public abstract class AbstractHttpResultWrapper<T, E> {
  public static <T, E> HttpResultWrapper<T, E> ok(T ok) {
    return HttpResultWrapper.<T, E>builder()
        .setOkResultMaybe(ok)
        .build();
  }

  public static <T, E> HttpResultWrapper<T, E> err(E err, int statusCode) {
    return HttpResultWrapper.<T, E>builder()
        .setErrResultMaybe(err)
        .setHttpStatusCode(statusCode)
        .build();
  }

  @JsonIgnore
  public Result<T, E> unwrap() {
    if (getOkResultMaybe().isPresent()) {
      return Result.ok(getOkResultMaybe().get());
    }

    if (getErrResultMaybe().isPresent()) {
      return Result.err(getErrResultMaybe().get());
    }

    throw new IllegalStateException("Shouldn't be possible to get here.");
  }

  public abstract Optional<T> getOkResultMaybe();
  public abstract Optional<E> getErrResultMaybe();

  @Default
  public int getHttpStatusCode() {
    return 200;
  }

  @Check
  void checkIsOkOrErr() {
    Preconditions.checkState(Boolean.logicalXor(getOkResultMaybe().isPresent(), getErrResultMaybe().isPresent()), "Exactly one of ok/err must be present");
  }
}
