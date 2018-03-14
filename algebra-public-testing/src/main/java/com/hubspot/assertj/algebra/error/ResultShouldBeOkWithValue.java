package com.hubspot.assertj.algebra.error;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

import com.hubspot.algebra.Result;

public class ResultShouldBeOkWithValue extends BasicErrorMessageFactory {
  private ResultShouldBeOkWithValue(String format, Object... arguments) {
    super(format, arguments);
  }

  public static <T, E> ErrorMessageFactory shouldBeOkWithValue(Result<T, E> actual, Object value) {
    if (actual.isOk()) {
      return new ResultShouldBeOkWithValue("Expecting Result to be Ok containing <%s> but contained <%s>", value, actual.unwrapOrElseThrow());
    } else {
      return new ResultShouldBeOkWithValue("Expecting Result to be Ok containing <%s> but was Err containing <%s>", value, actual.unwrapErrOrElseThrow());
    }
  }
}
