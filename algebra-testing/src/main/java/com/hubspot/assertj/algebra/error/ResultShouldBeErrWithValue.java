package com.hubspot.assertj.algebra.error;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

import com.hubspot.algebra.Result;

public class ResultShouldBeErrWithValue extends BasicErrorMessageFactory {
  private ResultShouldBeErrWithValue(String format, Object... arguments) {
    super(format, arguments);
  }

  public static <T, E> ErrorMessageFactory shouldBeErrWithValue(Result<T, E> actual, Object error) {
    if (actual.isErr()) {
      E actualError = actual.unwrapErrOrElseThrow();
      return new ResultShouldBeErrWithValue("Expecting Result to be Err containing <%s> but contained <%s>. expected:<%s> but was:<%s>", error, actualError, error, actualError);
    } else {
      return new ResultShouldBeErrWithValue("Expecting Result to be Err containing <%s> but was Ok containing <%s>", error, actual.unwrapOrElseThrow());
    }
  }
}

