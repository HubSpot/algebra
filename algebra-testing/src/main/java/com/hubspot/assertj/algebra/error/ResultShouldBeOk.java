package com.hubspot.assertj.algebra.error;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

import com.hubspot.algebra.Result;

public class ResultShouldBeOk extends BasicErrorMessageFactory {
  private ResultShouldBeOk(String format, Object... arguments) {
    super(format, arguments);
  }

  public static <T, E> ErrorMessageFactory shouldBeOk(Result<T, E> actual) {
    return new ResultShouldBeOk("Expecting Result to be Ok but was Err containing <%s>", actual.unwrapErrOrElseThrow());
  }
}
