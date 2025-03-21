package com.hubspot.algebra;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
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

  /**
   * Performs a conversion operation that aggregates a collection of Results into a single Result.
   * <p>
   * If any of the input Results are errors, all errors are returned in a new Result of type Result&lt;List&lt;SUCCESS_TYPE>, List&lt;ERROR_TYPE>>.
   * If all input Results are successful, a new Result containing the list of unwrapped success values is returned.
   *
   * @param results A Collection of Result instances
   * @param <SUCCESS_TYPE> The success type of the Results
   * @param <ERROR_TYPE> The error type of the Results
   * @return A Result containing either a list of success values or a list of error values
   */
  public static <SUCCESS_TYPE, ERROR_TYPE> Result<List<SUCCESS_TYPE>, List<ERROR_TYPE>> all(
    Collection<Result<SUCCESS_TYPE, ERROR_TYPE>> results
  ) {
    List<ERROR_TYPE> errors = results
      .stream()
      .filter(Result::isErr)
      .map(Result::unwrapErrOrElseThrow)
      .collect(ImmutableList.toImmutableList());
    if (!errors.isEmpty()) {
      return Result.err(errors);
    }
    return Result.ok(
      results
        .stream()
        .filter(Result::isOk)
        .map(Result::unwrapOrElseThrow)
        .collect(ImmutableList.toImmutableList())
    );
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

  public void consume(
    Consumer<? super ERROR_TYPE> errConsumer,
    Consumer<? super SUCCESS_TYPE> okConsumer
  ) {
    ifOk(okConsumer);
    ifErr(errConsumer);
  }

  public <NEW_ERROR_TYPE> Result<SUCCESS_TYPE, NEW_ERROR_TYPE> mapErr(
    Function<ERROR_TYPE, NEW_ERROR_TYPE> mapper
  ) {
    return Results.<SUCCESS_TYPE, ERROR_TYPE, NEW_ERROR_TYPE>modErr(mapper).apply(this);
  }

  public <NEW_SUCCESS_TYPE> Result<NEW_SUCCESS_TYPE, ERROR_TYPE> mapOk(
    Function<SUCCESS_TYPE, NEW_SUCCESS_TYPE> mapper
  ) {
    return Results.<SUCCESS_TYPE, ERROR_TYPE, NEW_SUCCESS_TYPE>modOk(mapper).apply(this);
  }

  public <NEW_SUCCESS_TYPE> Result<NEW_SUCCESS_TYPE, ERROR_TYPE> flatMapOk(
    Function<SUCCESS_TYPE, Result<NEW_SUCCESS_TYPE, ERROR_TYPE>> mapper
  ) {
    Result<Result<NEW_SUCCESS_TYPE, ERROR_TYPE>, ERROR_TYPE> nestedResult = Results
      .<SUCCESS_TYPE, ERROR_TYPE, Result<NEW_SUCCESS_TYPE, ERROR_TYPE>>modOk(mapper)
      .apply(this);

    if (nestedResult.isErr()) {
      return err(nestedResult.unwrapErrOrElseThrow());
    }

    return nestedResult.unwrapOrElseThrow();
  }

  public <NEW_ERROR_TYPE> Result<SUCCESS_TYPE, NEW_ERROR_TYPE> flatMapErr(
    Function<ERROR_TYPE, Result<SUCCESS_TYPE, NEW_ERROR_TYPE>> mapper
  ) {
    Result<SUCCESS_TYPE, Result<SUCCESS_TYPE, NEW_ERROR_TYPE>> nestedResult = Results
      .<SUCCESS_TYPE, ERROR_TYPE, Result<SUCCESS_TYPE, NEW_ERROR_TYPE>>modErr(mapper)
      .apply(this);

    if (nestedResult.isOk()) {
      return ok(nestedResult.unwrapOrElseThrow());
    }

    return nestedResult.unwrapErrOrElseThrow();
  }

  public <X extends Throwable> SUCCESS_TYPE unwrapOrElseThrow(
    Supplier<? extends X> exceptionSupplier
  ) throws X {
    return Results.getOk(this).orElseThrow(exceptionSupplier);
  }

  public <X extends Throwable> SUCCESS_TYPE unwrapOrElseThrow(
    Function<ERROR_TYPE, ? extends X> exceptionMapper
  ) throws X {
    return unwrapOrElseThrow(() -> exceptionMapper.apply(Results.getErr(this).get()));
  }

  public SUCCESS_TYPE unwrapOrElseThrow() {
    return unwrapOrElseThrow(err -> new IllegalStateException(err.toString()));
  }

  public SUCCESS_TYPE expect(String message) {
    return unwrapOrElseThrow(() -> new IllegalStateException(message));
  }

  public <X extends Throwable> ERROR_TYPE unwrapErrOrElseThrow(
    Supplier<? extends X> exceptionSupplier
  ) throws X {
    return Results.getErr(this).orElseThrow(exceptionSupplier);
  }

  public <X extends Throwable> ERROR_TYPE unwrapErrOrElseThrow(
    Function<SUCCESS_TYPE, ? extends X> exceptionMapper
  ) throws X {
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
}
