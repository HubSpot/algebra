package com.hubspot.assertj.algebra.error;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

import com.hubspot.algebra.Result;

public class ResultShouldBeErrWithValue extends BasicErrorMessageFactory {
  private ResultShouldBeErrWithValue(String format, Object... arguments) {
    super(format, arguments);
  }

  public static <T, E> ErrorMessageFactory shouldBeOkWithValue(Result<T, E> actual, Object value) {
    if (actual.isErr()) {
      return new ResultShouldBeErrWithValue("Expecting Result to be Err containing <%s> but contained <%s>", value, actual.unwrapErrOrElseThrow());
    } else {
      return new ResultShouldBeErrWithValue("Expecting Result to be Err containing <%s> but was Ok containing <%s>", value, actual.unwrapOrElseThrow());
    }
  }
}
