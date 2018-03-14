package com.hubspot.assertj.algebra.api;

import com.hubspot.algebra.Result;

public class Assertions {
  private Assertions() {
    throw new AssertionError("Utils classes are not to be instantiated.");
  }

  public static <T, E> ResultAssert<T, E> assertThat(final Result<T, E> actual) {
    return new ResultAssert<>(actual);
  }
}
