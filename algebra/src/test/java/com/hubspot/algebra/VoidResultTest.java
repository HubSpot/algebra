package com.hubspot.algebra;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class VoidResultTest {

  private static final VoidResult<String> OK_RESULT = VoidResult.ok();
  private static final VoidResult<String> ERR_RESULT = VoidResult.error("ERROR");

  @Test
  public void itHandlesOk() throws Exception {
    assertThat(OK_RESULT.isOk()).isTrue();
    assertThat(OK_RESULT.isErr()).isFalse();
  }

  @Test
  public void itHandlesErr() throws Exception {
    assertThat(ERR_RESULT.isOk()).isFalse();
    assertThat(ERR_RESULT.isErr()).isTrue();
  }
}
