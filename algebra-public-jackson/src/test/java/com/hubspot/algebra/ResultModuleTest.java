package com.hubspot.algebra;

import static org.assertj.core.api.Assertions.assertThat;

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
