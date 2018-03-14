package com.hubspot.assertj.algebra.error;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

import com.hubspot.algebra.Result;

public class ResultShouldBeErr extends BasicErrorMessageFactory {
  private ResultShouldBeErr(String format, Object... arguments) {
    super(format, arguments);
  }

  public static <T, E> ErrorMessageFactory shouldBeErr(Result<T, E> actual) {
    return new ResultShouldBeErr("Expecting Result to be Err but was Ok containing <%s>", actual.unwrapOrElseThrow());
  }
}
