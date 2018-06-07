package com.hubspot.algebra;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResultModuleTest {

  private static ObjectMapper objectMapper;

  @BeforeClass
  public static void setupClass() {
    objectMapper = new ObjectMapper().registerModule(new ResultModule());
  }

  private static final String EXPECTED_OK = "{\"value\":\"test\",\"@result\":\"OK\"}";
  private static final String EXPECTED_ERR = "{\"name\":\"ERROR\",\"@result\":\"ERR\"}";
  private static final String EXPECTED_RAW_ERR = "{\"@error\":\"ERROR\",\"@result\":\"ERR\"}";
  private static final String EXPECTED_STRING_OK = "{\"@ok\":\"test\",\"@result\":\"OK\"}";
  private static final String EXPECTED_STRING_ERR = "{\"@error\":\"ERROR\",\"@result\":\"ERR\"}";
  private static final String EXPECTED_LIST_OK = "{\"@ok\":[\"val0\",\"val1\"],\"@result\":\"OK\"}";
  private static final String EXPECTED_LIST_ERR = "{\"@error\":[\"err0\",\"err1\"],\"@result\":\"ERR\"}";

  @Test
  public void itSerializesOk() throws Exception {
    Result<TestBean, TestError> result = Result.ok(new TestBean("test"));

    assertThat(objectMapper.writeValueAsString(result)).isEqualTo(EXPECTED_OK);
  }

  @Test
  public void itSerializesErr() throws Exception {
    Result<TestBean, TestError> result = Result.err(TestError.ERROR);

    assertThat(objectMapper.writeValueAsString(result)).isEqualTo(EXPECTED_ERR);
  }

  @Test
  public void itSerializesRawErr() throws Exception {
    Result<TestBean, RawError> result = Result.err(RawError.ERROR);

    assertThat(objectMapper.writeValueAsString(result)).isEqualTo(EXPECTED_RAW_ERR);
  }

  @Test
  public void itSerializesStringOk() throws Exception {
    Result<String, String> result = Result.ok("test");
    assertThat(objectMapper.writeValueAsString(result)).isEqualTo(EXPECTED_STRING_OK);
  }

  @Test
  public void itSerializesStringErr() throws Exception {
    Result<String, String> result = Result.err("ERROR");
    assertThat(objectMapper.writeValueAsString(result)).isEqualTo(EXPECTED_STRING_ERR);
  }

  @Test
  public void itSerializesListOk() throws Exception {
    Result<List<String>, List<String>> result = Result.ok(Arrays.asList("val0", "val1"));
    assertThat(objectMapper.writeValueAsString(result)).isEqualTo(EXPECTED_LIST_OK);
  }

  @Test
  public void itSerializesListErr() throws Exception {
    Result<List<String>, List<String>> result = Result.err(Arrays.asList("err0", "err1"));
    assertThat(objectMapper.writeValueAsString(result)).isEqualTo(EXPECTED_LIST_ERR);
  }

  @Test
  public void itDeserializesOk() throws Exception {
    Result<TestBean, TestError> result = objectMapper.readValue(EXPECTED_OK, new TypeReference<Result<TestBean, TestError>>(){});

    assertThat(result.isOk()).isTrue();
    assertThat(result.unwrapOrElseThrow().getValue()).isEqualTo("test");
  }

  @Test
  public void itDeserializesErr() throws Exception {
    Result<TestBean, TestError> result = objectMapper.readValue(EXPECTED_ERR, new TypeReference<Result<TestBean, TestError>>(){});

    assertThat(result.isErr()).isTrue();
    assertThat(result.unwrapErrOrElseThrow()).isEqualTo(TestError.ERROR);
  }

  @Test
  public void itDeserializesRawErr() throws Exception {
    Result<TestBean, RawError> result = objectMapper.readValue(EXPECTED_RAW_ERR, new TypeReference<Result<TestBean, RawError>>(){});

    assertThat(result.isErr()).isTrue();
    assertThat(result.unwrapErrOrElseThrow()).isEqualTo(RawError.ERROR);
  }

  @Test
  public void itDeserializesStringOk() throws Exception {
    Result<String, String> actual = objectMapper.readValue(EXPECTED_STRING_OK, new TypeReference<Result<String, String>>(){});
    assertThat(actual).isEqualTo(Result.ok("test"));
  }

  @Test
  public void itDeserializesStringErr() throws Exception {
    Result<String, String> actual = objectMapper.readValue(EXPECTED_STRING_ERR, new TypeReference<Result<String, String>>(){});
    assertThat(actual).isEqualTo(Result.err("ERROR"));
  }

  @Test
  public void itDeserializesListOk() throws Exception {
    Result<List<String>, List<String>> actual = objectMapper.readValue(EXPECTED_LIST_OK, new TypeReference<Result<List<String>, List<String>>>(){});
    assertThat(actual).isEqualTo(Result.ok(Arrays.asList("val0", "val1")));
  }

  @Test
  public void itDeserializesListErr() throws Exception {
    Result<List<String>, List<String>> actual = objectMapper.readValue(EXPECTED_LIST_ERR, new TypeReference<Result<List<String>, List<String>>>(){});
    assertThat(actual).isEqualTo(Result.err(Arrays.asList("err0", "err1")));
  }

  static class TestBean {
    private String value;

    public TestBean(String value) {
      this.value = value;
    }

    public TestBean() {
    }

    public String getValue() {
      return value;
    }

    public TestBean setValue(String value) {
      this.value = value;
      return this;
    }
  }

  @JsonFormat(shape = Shape.OBJECT)
  enum TestError {
    ERROR;

    public String getName() {
      return name();
    }

    @JsonCreator
    static TestError fromJson(JsonNode node) {
      String name = node.findValue("name").asText();
      return TestError.valueOf(name);
    }
  }

  enum RawError {
    ERROR;
  }
}
