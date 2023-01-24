package com.hubspot.assertj.algebra.api;

import com.hubspot.algebra.Result;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.hubspot.assertj.algebra.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ResultAssertTest {
  private void assertThatAssertionErrorIsThrown(ThrowingCallable throwingCallable, String expectedMessage, Object... messageParameters) {
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(throwingCallable)
        .withMessage(expectedMessage, messageParameters);
  }

  @Test
  public void itPassesWhenAssertingOkOnOk() throws Exception {
    assertThat(Result.ok("OK")).isOk();
  }

  @Test
  public void itPassesWhenAssertingErrOnErr() throws Exception {
    assertThat(Result.err(1)).isErr();
  }

  @Test
  public void itPassesWhenAssertingOkValueOnOk() throws Exception {
    assertThat(Result.ok("OK")).containsOk("OK");
  }

  @Test
  public void itPassesWhenAssertingErrValueOnErr() throws Exception {
    assertThat(Result.err(1)).containsErr(1);
  }

  @Test
  public void itPassesWhenExtractingOkValueOnOk() throws Exception {
    assertThat(Result.ok("OK")).extractingOk().isEqualTo("OK");
  }

  @Test
  public void itPassesWhenExtractingErrValueOnErr() throws Exception {
    assertThat(Result.err(1)).extractingErr().isEqualTo(1);
  }

  @Test
  public void itPassesWhenConsumingOkOnIfOkOrElseErr() throws Exception {
    List<String> okResults = new ArrayList<>();
    List<String> errorResults = new ArrayList<>();
    Result<String, String> okResult = Result.ok("test");
    okResult.ifOkOrElseError(okResults::add, errorResults::add);
    assertThat(okResults).contains(okResult.unwrapOrElseThrow());
    assertThat(errorResults).isEmpty();
  }

  @Test
  public void itPassesWhenConsumingErrorOnIfOkOrElseErr() throws Exception {
    List<String> okResults = new ArrayList<>();
    List<String> errorResults = new ArrayList<>();
    Result<String, String> errorResult = Result.err("test");
    errorResult.ifOkOrElseError(okResults::add, errorResults::add);
    assertThat(okResults).isEmpty();
    assertThat(errorResults).contains(errorResult.unwrapErrOrElseThrow());
  }

  @Test
  public void itFailsWhenAssertingOkOnErr() throws Exception {
    assertThatAssertionErrorIsThrown(() -> assertThat(Result.err(1)).isOk(),
                                     "Expecting Result to be Ok but was Err containing <%s>",
                                     1);
  }

  @Test
  public void itFailsWhenAssertingErrOnOk() throws Exception {
    assertThatAssertionErrorIsThrown(() -> assertThat(Result.ok("Ok")).isErr(),
                                     "Expecting Result to be Err but was Ok containing <\"%s\">",
                                     "Ok");
  }

  @Test
  public void itFailsWhenAssertingOkValueOnOkWithDifferentValue() throws Exception {
    assertThatAssertionErrorIsThrown(() -> assertThat(Result.ok("Bad")).containsOk("Ok"),
                                     "Expecting Result to be Ok containing <\"%s\"> but contained <\"%s\">",
                                     "Ok", "Bad");
  }

  @Test
  public void itFailsWhenAssertingErrValueOnErrWithDifferentValue() throws Exception {
    assertThatAssertionErrorIsThrown(() -> assertThat(Result.err(0)).containsErr(1),
                                     "Expecting Result to be Err containing <%s> but contained <%s>",
                                     1, 0);
  }

  @Test
  public void itFailsWhenAssertingOkValueOnErr() throws Exception {
    assertThatAssertionErrorIsThrown(() -> assertThat(Result.err(1)).containsOk("Ok"),
                                     "Expecting Result to be Ok containing <\"%s\"> but was Err containing <%s>",
                                     "Ok", 1);
  }

  @Test
  public void itFailsWhenAssertingErrValueOnOk() throws Exception {
    assertThatAssertionErrorIsThrown(() -> assertThat(Result.ok("Ok")).containsErr(1),
                                     "Expecting Result to be Err containing <%s> but was Ok containing <\"%s\">",
                                     1, "Ok");
  }

  @Test
  public void itFailsWhenExtractingOkValueOnErr() throws Exception {
    assertThatAssertionErrorIsThrown(() -> assertThat(Result.err(1)).extractingOk().isEqualTo(1),
                                     "Expecting Result to be Ok but was Err containing <%s>",
                                     1);
  }

  @Test
  public void itFailsWhenExtractingErrValueOnOk() throws Exception {
    assertThatAssertionErrorIsThrown(() -> assertThat(Result.ok("Ok")).extractingErr().isEqualTo("Ok"),
                                     "Expecting Result to be Err but was Ok containing <\"%s\">",
                                     "Ok");
  }
}
