package com.hubspot.algebra;

import java.util.function.Consumer;
import java.util.function.Function;
import org.derive4j.Data;
import org.derive4j.Derive;
import org.derive4j.Visibility;

/**
 * @deprecated use {@link Result#nullOk()} instead.
 */
@Deprecated
@Data(@Derive(withVisibility = Visibility.Package))
public abstract class VoidResult<ERROR_TYPE> extends Result<Void, ERROR_TYPE> {

  public static <E> VoidResult<E> ok() {
    return VoidResults.ok(null);
  }

  public static <ERROR_TYPE> VoidResult<ERROR_TYPE> error(ERROR_TYPE error) {
    return VoidResults.err(error);
  }

  VoidResult() {}

  @Override
  public boolean isOk() {
    return !super.isErr();
  }

  @Override
  public void ifOk(Consumer<? super Void> consumer) {
    consumer.accept(null);
  }

  @Override
  public <NEW_SUCCESS_TYPE> Result<NEW_SUCCESS_TYPE, ERROR_TYPE> mapOk(
    Function<Void, NEW_SUCCESS_TYPE> mapper
  ) {
    return this.match(Results::err, ok -> Results.ok(mapper.apply(null)));
  }

  @Override
  public <NEW_SUCCESS_TYPE> Result<NEW_SUCCESS_TYPE, ERROR_TYPE> flatMapOk(
    Function<Void, Result<NEW_SUCCESS_TYPE, ERROR_TYPE>> mapper
  ) {
    Result<Result<NEW_SUCCESS_TYPE, ERROR_TYPE>, ERROR_TYPE> nestedResult =
      this.mapOk(mapper);

    if (nestedResult.isErr()) {
      return err(nestedResult.unwrapErrOrElseThrow());
    }

    return nestedResult.unwrapOrElseThrow();
  }

  public abstract <R> R match(Function<ERROR_TYPE, R> err, Function<Void, R> ok);
}
