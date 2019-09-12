package com.hubspot.algebra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class ResultTest {
  private static final String SAMPLE_STRING = "Hello";
  private static final Result<String, SampleError> OK_RESULT = Result.ok(SAMPLE_STRING);
  private static final Result<String, SampleError> ERR_RESULT = Result.err(SampleError.TEST_ERROR);

  private enum SampleError {
    TEST_ERROR,
    TEST_ERROR_TWO,
    ;
  }

  private static class SampleErrorException extends Exception {
    SampleErrorException(SampleError error) {
      super(error.name());
    }
  }

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

  @Test
  public void itMapsOk() throws Exception {
    Result<Integer, SampleError> mappedOk = OK_RESULT.mapOk(String::length);
    assertThat(mappedOk.unwrapOrElseThrow()).isEqualTo(SAMPLE_STRING.length());

    Result<Integer, SampleError> mappedErr = ERR_RESULT.mapOk(String::length);
    assertThat(mappedErr.unwrapErrOrElseThrow()).isEqualTo(ERR_RESULT.unwrapErrOrElseThrow());
  }

  @Test
  public void itMapsErr() throws Exception {
    Result<String, String> mappedErr = ERR_RESULT.mapErr(SampleError::name);
    assertThat(mappedErr.unwrapErrOrElseThrow()).isEqualTo(SampleError.TEST_ERROR.name());

    Result<String, String> mappedOk = OK_RESULT.mapErr(SampleError::name);
    assertThat(mappedOk.unwrapOrElseThrow()).isEqualTo(OK_RESULT.unwrapOrElseThrow());
  }

  @Test
  public void itProperlyExpectsOk() throws Exception {
    assertThat(OK_RESULT.expect("should not throw this")).isEqualTo(SAMPLE_STRING);
    assertThat(OK_RESULT.unwrapOrElseThrow()).isEqualTo(SAMPLE_STRING);
    assertThat(OK_RESULT.unwrapOrElseThrow(SampleErrorException::new)).isEqualTo(SAMPLE_STRING);
  }

  @Test
  public void itProperlyExpectsErr() throws Exception {
    assertThat(ERR_RESULT.expectErr("should not throw this")).isEqualTo(SampleError.TEST_ERROR);
    assertThat(ERR_RESULT.unwrapErrOrElseThrow()).isEqualTo(SampleError.TEST_ERROR);
    assertThat(ERR_RESULT.unwrapErrOrElseThrow(ok -> new RuntimeException(ok))).isEqualTo(SampleError.TEST_ERROR);
  }

  @Test
  public void itThrowsWhenExpectIsCalledOnErr() throws Exception {
    String message = "this should throw";
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> ERR_RESULT.expect(message))
        .withMessage(message);
  }

  @Test
  public void itThrowsWhenExpectErrIsCalledOnOk() throws Exception {
    String message = "this should throw";
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> OK_RESULT.expectErr(message))
        .withMessage(message);
  }

  @Test(expected = IllegalStateException.class)
  public void itThrowsWhenUnwrapIsCalledOnErr() throws Exception {
    ERR_RESULT.unwrapOrElseThrow();
  }

  @Test(expected = IllegalStateException.class)
  public void itThrowsWhenUnwrapErrIsCalledOnOk() throws Exception {
    OK_RESULT.unwrapErrOrElseThrow();
  }

  @Test
  public void itThrowsTheCorrectExceptionWhenUnwrapping() throws Exception {
    assertThatExceptionOfType(SampleErrorException.class)
        .isThrownBy(() -> ERR_RESULT.unwrapOrElseThrow(SampleErrorException::new))
        .withMessage(SampleError.TEST_ERROR.name());
  }

  @Test
  public void itThrowsTheCorrectExceptionWhenUnwrappingErr() throws Exception {
    assertThatExceptionOfType(RuntimeException.class)
        .isThrownBy(() -> OK_RESULT.unwrapErrOrElseThrow(ok -> new RuntimeException(ok)))
        .withMessage(SAMPLE_STRING);
  }

  @Test
  public void itCallsConsumerWhenOkPresent() {
    List<String> results = new ArrayList<>();
    OK_RESULT.ifOk(results::add);
    assertThat(results.contains(SAMPLE_STRING));
  }

  @Test
  public void itDoesNotCallConsumerWhenOkAbsent() {
    List<String> results = new ArrayList<>();
    ERR_RESULT.ifOk(results::add);
    assertThat(results.isEmpty());
  }

  @Test
  public void itCallsConsumerWhenErrPresent() {
    List<SampleError> results = new ArrayList<>();
    ERR_RESULT.ifErr(results::add);
    assertThat(results.contains(SampleError.TEST_ERROR));
  }

  @Test
  public void itDoesNotCallConsumerWhenErrAbsent() {
    List<SampleError> results = new ArrayList<>();
    OK_RESULT.ifErr(results::add);
    assertThat(results.isEmpty());
  }

  @Test
  public void itAllowsConsumerOfOkSuperType() {
    List<String> check = new ArrayList<>();
    Consumer<List<String>> consumer = l -> check.add("I was here.");
    Result<ImmutableList<String>, SampleError> result = Result.ok(ImmutableList.of(SAMPLE_STRING));
    result.ifOk(consumer);
    assertThat(!check.isEmpty());
  }

  @Test
  public void itAllowsConsumerOfErrSuperType() {
    List<String> check = new ArrayList<>();
    Consumer<List<String>> consumer = l -> check.add("I was here.");
    Result<Integer, ImmutableList<String>> result = Result.err(ImmutableList.of(SAMPLE_STRING));
    result.ifErr(consumer);
    assertThat(!check.isEmpty());
  }

  @Test
  public void itMapsErrorTypeViaErrorProjection() {
    Result<String, Integer> error = Results.err(1);
    Result<String, Boolean> mapped = error.error().map(i -> i % 2 == 0).result();
    assertThat(mapped.isErr()).isTrue();
    assertThat(mapped.unwrapErrOrElseThrow()).isFalse();
  }

  @Test
  public void itSkipsMappingErrorTypeViaErrorProjectionWhenNotError() {
    Result<String, Integer> ok = Results.ok("ok");
    Result<String, Boolean> mapped = ok.error().map(i -> i % 2 == 0).result();
    assertThat(mapped.isOk()).isTrue();
    assertThat(mapped.unwrapOrElseThrow()).isEqualTo("ok");
  }

  @Test
  public void itFlatMapsErrorTypeToSuccessViaErrorProjection() {
    Result<String, Integer> error = Results.err(2);
    Result<String, Boolean> mapped = error.error()
        .flatMap(i -> i % 2 == 0 ? Results.ok("ok") : Results.err(false))
        .result();
    assertThat(mapped.isOk()).isTrue();
    assertThat(mapped.unwrapOrElseThrow()).isEqualTo("ok");
  }

  @Test
  public void itFlatMapsErrorTypeToErrorViaErrorProjection() {
    Result<String, Integer> error = Results.err(1);
    Result<String, Boolean> mapped = error.error()
        .flatMap(i -> i % 2 == 0 ? Results.ok("ok") : Results.err(false))
        .result();
    assertThat(mapped.isErr()).isTrue();
    assertThat(mapped.unwrapErrOrElseThrow()).isFalse();
  }

  @Test
  public void itSkipFlatsMappingErrorTypeViaErrorProjectionWhenNotError() {
    Result<String, Integer> ok = Results.ok("ok");
    Result<String, Boolean> mapped = ok.error()
        .flatMap(i -> i % 2 == 0 ? Results.ok("changed") : Results.err(false))
        .result();
    assertThat(mapped.isOk()).isTrue();
    assertThat(mapped.unwrapOrElseThrow()).isEqualTo("ok");
  }

  @Test
  public void itConvertsErrorProjectionToNonEmptyOptionalWhenItIsAnError() {
    Result<String, Integer> error = Results.err(1);
    assertThat(error.error().toOptional()).hasValue(1);
  }

  @Test
  public void itConvertsErrorProjectionToEmptyOptionalWhenItIsNotAnError() {
    Result<String, Integer> error = Results.ok("success");
    assertThat(error.error().toOptional()).isEmpty();
  }

  @Test
  public void itReturnsFalseForExistsOnOkErrorProjection() {
    Result<String, Integer> ok = Results.ok("success");
    assertThat(ok.error().exists(i -> i > 0)).isFalse();
  }

  @Test
  public void itReturnsResultOfPredicateForExistsOnErrorredErrorProjection() {
    Result<String, Integer> error = Results.err(-1);
    assertThat(error.error().exists(i -> i > 0)).isFalse();
    assertThat(error.error().exists(i -> i < 0)).isTrue();
  }
}
