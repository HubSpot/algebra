package com.hubspot.assertj.algebra.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.hubspot.algebra.Result;
import com.hubspot.assertj.algebra.error.ResultShouldBeErr;
import com.hubspot.assertj.algebra.error.ResultShouldBeErrWithValue;
import com.hubspot.assertj.algebra.error.ResultShouldBeOk;
import com.hubspot.assertj.algebra.error.ResultShouldBeOkWithValue;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.internal.Failures;
import org.assertj.core.internal.Objects;

public class ResultAssert<T, E> extends AbstractAssert<ResultAssert<T, E>, Result<T, E>> {

  private Failures failures = Failures.instance();

  ResultAssert(final Result<T, E> actual) {
    super(actual, ResultAssert.class);
  }

  public ResultAssert<T, E> isOk() {
    Objects.instance().assertNotNull(info, actual);
    if (!actual.isOk()) {
      throw failures.failure(info, ResultShouldBeOk.shouldBeOk(actual));
    }
    return this;
  }

  public ResultAssert<T, E> isErr() {
    Objects.instance().assertNotNull(info, actual);
    if (!actual.isErr()) {
      throw failures.failure(info, ResultShouldBeErr.shouldBeErr(actual));
    }
    return this;
  }

  public ResultAssert<T, E> containsOk(Object value) {
    Objects.instance().assertNotNull(info, actual);
    if (!actual.isOk() || !actual.unwrapOrElseThrow().equals(value)) {
      throw failures.failure(
        info,
        ResultShouldBeOkWithValue.shouldBeOkWithValue(actual, value)
      );
    }
    return this;
  }

  public ResultAssert<T, E> containsErr(Object value) {
    Objects.instance().assertNotNull(info, actual);
    if (!actual.isErr() || !actual.unwrapErrOrElseThrow().equals(value)) {
      throw failures.failure(
        info,
        ResultShouldBeErrWithValue.shouldBeErrWithValue(actual, value)
      );
    }
    return this;
  }

  public AbstractObjectAssert<?, T> extractingOk() {
    isOk();
    return assertThat(actual.unwrapOrElseThrow());
  }

  public AbstractObjectAssert<?, E> extractingErr() {
    isErr();
    return assertThat(actual.unwrapErrOrElseThrow());
  }
}
