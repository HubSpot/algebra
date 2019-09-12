package com.hubspot.algebra;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.derive4j.Data;
import org.derive4j.Derive;
import org.derive4j.Visibility;

@Data(@Derive(withVisibility = Visibility.Package))
public abstract class Result<SUCCESS_TYPE, ERROR_TYPE> {
  public static <SUCCESS_TYPE, E> Result<SUCCESS_TYPE, E> ok(SUCCESS_TYPE success) {
    return Results.ok(success);
  }

  public static <T, ERROR_TYPE> Result<T, ERROR_TYPE> err(ERROR_TYPE error) {
    return Results.err(error);
  }

  public static <ERROR_TYPE> Result<NullValue, ERROR_TYPE> nullOk() {
    return Results.ok(NullValue.get());
  }

  public static <SUCCESS_TYPE> Result<SUCCESS_TYPE, NullValue> nullErr() {
    return Results.err(NullValue.get());
  }

  Result() {}

  public boolean isOk() {
    return Results.getOk(this).isPresent();
  }

  public void ifOk(Consumer<? super SUCCESS_TYPE> consumer) {
    Results.getOk(this).ifPresent(consumer);
  }

  public boolean isErr() {
    return Results.getErr(this).isPresent();
  }

  public void ifErr(Consumer<? super ERROR_TYPE> consumer) {
    Results.getErr(this).ifPresent(consumer);
  }

  /**
   *
   * @deprecated Use error().map(...).result() instead
   */
  @Deprecated
  public <NEW_ERROR_TYPE> Result<SUCCESS_TYPE, NEW_ERROR_TYPE> mapErr(Function<ERROR_TYPE, NEW_ERROR_TYPE> mapper) {
    return error().map(mapper).result;
  }

  /**
   *
   * @deprecated Use map instead as map always implies mapping over the Ok case
   */
  @Deprecated
  public <NEW_SUCCESS_TYPE> Result<NEW_SUCCESS_TYPE, ERROR_TYPE> mapOk(Function<SUCCESS_TYPE, NEW_SUCCESS_TYPE> mapper) {
    return map(mapper);
  }

  public <NEW_SUCCESS_TYPE> Result<NEW_SUCCESS_TYPE, ERROR_TYPE> map(Function<SUCCESS_TYPE, NEW_SUCCESS_TYPE> mapper) {
    return Results.<SUCCESS_TYPE, ERROR_TYPE, NEW_SUCCESS_TYPE>modOk(mapper).apply(this);
  }

  /**
   *
   * @deprecated Use flatMap instead as flatMap always implies mapping over the Ok case
   */
  @Deprecated
  public <NEW_SUCCESS_TYPE> Result<NEW_SUCCESS_TYPE, ERROR_TYPE> flatMapOk(Function<SUCCESS_TYPE, Result<NEW_SUCCESS_TYPE, ERROR_TYPE>> mapper) {
    return flatMap(mapper);
  }

  public <NEW_SUCCESS_TYPE> Result<NEW_SUCCESS_TYPE, ERROR_TYPE> flatMap(Function<SUCCESS_TYPE, Result<NEW_SUCCESS_TYPE, ERROR_TYPE>> mapper) {
    Result<Result<NEW_SUCCESS_TYPE, ERROR_TYPE>, ERROR_TYPE> nestedResult = Results.<SUCCESS_TYPE, ERROR_TYPE, Result<NEW_SUCCESS_TYPE, ERROR_TYPE>>modOk(mapper)
        .apply(this);

    if (nestedResult.isErr()) {
      return err(nestedResult.unwrapErrOrElseThrow());
    }

    return nestedResult.unwrapOrElseThrow();
  }

  public <X extends Throwable> SUCCESS_TYPE unwrapOrElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
    return Results.getOk(this)
        .orElseThrow(exceptionSupplier);
  }

  public <X extends Throwable> SUCCESS_TYPE unwrapOrElseThrow(Function<ERROR_TYPE, ? extends X> exceptionMapper) throws X {
    return unwrapOrElseThrow(() -> exceptionMapper.apply(Results.getErr(this).get()));
  }

  public SUCCESS_TYPE unwrapOrElseThrow() {
    return unwrapOrElseThrow(err -> new IllegalStateException(err.toString()));
  }

  public SUCCESS_TYPE expect(String message) {
    return unwrapOrElseThrow(() -> new IllegalStateException(message));
  }

  public <X extends Throwable> ERROR_TYPE unwrapErrOrElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
    return Results.getErr(this)
        .orElseThrow(exceptionSupplier);
  }

  public <X extends Throwable> ERROR_TYPE unwrapErrOrElseThrow(Function<SUCCESS_TYPE, ? extends X> exceptionMapper) throws X {
    return unwrapErrOrElseThrow(() -> exceptionMapper.apply(Results.getOk(this).get()));
  }

  public ERROR_TYPE unwrapErrOrElseThrow() {
    return unwrapErrOrElseThrow(ok -> new IllegalStateException(ok.toString()));
  }

  public ERROR_TYPE expectErr(String message) {
    return unwrapErrOrElseThrow(() -> new IllegalStateException(message));
  }

  public abstract <R> R match(Function<ERROR_TYPE, R> err, Function<SUCCESS_TYPE, R> ok);

  @Override
  public abstract int hashCode();
  @Override
  public abstract boolean equals(Object obj);

  @Override
  public String toString() {
    if (isOk()) {
      return "Ok[" + unwrapOrElseThrow().toString() + "]";
    }

    return "Err[" + unwrapErrOrElseThrow().toString() + "]";
  }

  public ErrorProjection<SUCCESS_TYPE, ERROR_TYPE> error() {
    return new ErrorProjection<>(this);
  }

  public static final class ErrorProjection<SUCC_TYPE, ERR_TYPE> {
    private final Result<SUCC_TYPE, ERR_TYPE> result;

    private ErrorProjection(Result<SUCC_TYPE, ERR_TYPE> result) {
      this.result = result;
    }

    public Result<SUCC_TYPE, ERR_TYPE> result() {
      return result;
    }

    public <RERR_TYPE> ErrorProjection<SUCC_TYPE, RERR_TYPE> map(Function<ERR_TYPE, RERR_TYPE> mapper) {
      return new ErrorProjection<>(Results.<SUCC_TYPE, ERR_TYPE, RERR_TYPE>modErr(mapper).apply(result));
    }

    public <RERR_TYPE> ErrorProjection<SUCC_TYPE, RERR_TYPE> flatMap(Function<ERR_TYPE, Result<SUCC_TYPE, RERR_TYPE>> mapper) {
      Result<SUCC_TYPE, Result<SUCC_TYPE, RERR_TYPE>> nestedResult = Results.<SUCC_TYPE, ERR_TYPE, Result<SUCC_TYPE, RERR_TYPE>>modErr(mapper).apply(result);
      Result<SUCC_TYPE, RERR_TYPE> unwrapped = nestedResult.isErr()
          ? nestedResult.unwrapErrOrElseThrow()
          : Results.ok(nestedResult.unwrapOrElseThrow());
      return new ErrorProjection<>(unwrapped);
    }

    public Optional<ERR_TYPE> toOptional() {
      if (result.isErr()) {
        return Optional.of(result.unwrapErrOrElseThrow());
      } else {
        return Optional.empty();
      }
    }

    public boolean exists(Predicate<ERR_TYPE> predicate) {
      return toOptional().filter(predicate).isPresent();
    }
  }
}
