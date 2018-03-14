package com.hubspot.algebra;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.concurrent.NotThreadSafe;

import org.derive4j.Data;

@Data
public abstract class Result<SUCCESS_TYPE, ERROR_TYPE> {
  public static <SUCCESS_TYPE, E> Result<SUCCESS_TYPE, E> ok(SUCCESS_TYPE success) {
    return Results.ok(success);
  }

  public static <T, ERROR_TYPE> Result<T, ERROR_TYPE> err(ERROR_TYPE error) {
    return Results.err(error);
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

  public <NEW_ERROR_TYPE> Result<SUCCESS_TYPE, NEW_ERROR_TYPE> mapErr(Function<ERROR_TYPE, NEW_ERROR_TYPE> mapper) {
    return Results.<SUCCESS_TYPE, ERROR_TYPE, NEW_ERROR_TYPE>modErr(mapper).apply(this);
  }

  public <NEW_SUCCESS_TYPE> Result<NEW_SUCCESS_TYPE, ERROR_TYPE> mapOk(Function<SUCCESS_TYPE, NEW_SUCCESS_TYPE> mapper) {
    return Results.<SUCCESS_TYPE, ERROR_TYPE, NEW_SUCCESS_TYPE>modOk(mapper).apply(this);
  }

  public <NEW_SUCCESS_TYPE> Result<NEW_SUCCESS_TYPE, ERROR_TYPE> flatMapOk(Function<SUCCESS_TYPE, Result<NEW_SUCCESS_TYPE, ERROR_TYPE>> mapper) {
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
  public String toString() {
    if (isOk()) {
      return "Ok[" + unwrapOrElseThrow().toString() + "]";
    }

    return "Err[" + unwrapErrOrElseThrow().toString() + "]";
  }
}
