package com.hubspot.algebra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

public class ResultModuleTest {

  private static final Result<TestBean, TestError> BEAN_OK = Result.ok(new TestBean("test"));
  private static final String BEAN_OK_JSON = "{\"value\":\"test\",\"@result\":\"OK\"}";
  private static final Result<TestBean, TestBean> BEAN_ERR = Result.err(new TestBean("ERROR"));
  private static final String BEAN_ERR_JSON = "{\"value\":\"ERROR\",\"@result\":\"ERR\"}";

  private static final Result<TestBean, TestError> CUSTOM_ENUM_ERR =  Result.err(TestError.ERROR);
  private static final String CUSTOM_ENUM_ERR_JSON = "{\"name\":\"ERROR\",\"@result\":\"ERR\"}";
  private static final Result<TestBean, RawError> RAW_ENUM_ERR = Result.err(RawError.ERROR);
  private static final String RAW_ENUM_ERR_JSON = "{\"@error\":\"ERROR\",\"@result\":\"ERR\"}";

  private static final Result<String, String> STRING_OK = Result.ok("test");
  private static final String STRING_OK_JSON = "{\"@ok\":\"test\",\"@result\":\"OK\"}";
  private static final Result<String, String> STRING_ERR = Result.err("ERROR");
  private static final String STRING_ERR_JSON = "{\"@error\":\"ERROR\",\"@result\":\"ERR\"}";

  private static final Result<List<String>, List<String>> LIST_OK = Result.ok(Arrays.asList("val0", "val1"));
  private static final String LIST_OK_JSON = "{\"@ok\":[\"val0\",\"val1\"],\"@result\":\"OK\"}";
  private static final Result<List<String>, List<String>> LIST_ERR = Result.err(Arrays.asList("err0", "err1"));
  private static final String LIST_ERR_JSON = "{\"@error\":[\"err0\",\"err1\"],\"@result\":\"ERR\"}";

  private static final Result<List<TestBean>, List<TestError>> BEAN_LIST_OK = Result.ok(Arrays.asList(new TestBean("test")));
  private static final String BEAN_LIST_OK_JSON = "{\"@ok\":[{\"value\":\"test\"}],\"@result\":\"OK\"}";
  private static final Result<List<TestBean>, List<TestError>> BEAN_LIST_ERR = Result.err(Arrays.asList(TestError.ERROR));
  private static final String BEAN_LIST_ERR_JSON = "{\"@error\":[{\"name\":\"ERROR\"}],\"@result\":\"ERR\"}";

  private static final Result<Map<String, String>, Map<String, String>> MAP_OK = Result.ok(Collections.singletonMap("key", "value"));
  private static final String MAP_OK_JSON = "{\"key\":\"value\",\"@result\":\"OK\"}";
  private static final Result<Map<String, String>, Map<String, String>> MAP_ERR = Result.err(Collections.singletonMap("key", "value"));
  private static final String MAP_ERR_JSON = "{\"key\":\"value\",\"@result\":\"ERR\"}";

  private static final Result<Multimap<String, String>, Multimap<String, String>> MULTIMAP_OK = Result.ok(
      ImmutableMultimap.<String, String> builder().putAll("key", "val0", "val1").build());
  private static final String MULTIMAP_OK_JSON = "{\"key\":[\"val0\",\"val1\"],\"@result\":\"OK\"}";
  private static final Result<Multimap<String, String>, Multimap<String, String>> MULTIMAP_ERR = Result.err(
      ImmutableMultimap.<String, String> builder().putAll("key", "err0", "err1").build());
  private static final String MULTIMAP_ERR_JSON = "{\"key\":[\"err0\",\"err1\"],\"@result\":\"ERR\"}";

  private static final Result<Table<String, String, String>, Table<String, String, String>> TABLE_OK = Result.ok(
      ImmutableTable.<String, String, String> builder().put("row", "column", "value").build());
  private static final String TABLE_OK_JSON = "{\"row\":{\"column\":\"value\"},\"@result\":\"OK\"}";
  private static final Result<Table<String, String, String>, Table<String, String, String>> TABLE_ERR = Result.err(
      ImmutableTable.<String, String, String> builder().put("row", "column", "value").build());
  private static final String TABLE_ERR_JSON = "{\"row\":{\"column\":\"value\"},\"@result\":\"ERR\"}";

  private static final Result<NullValue, String> NULL_OK = Result.nullOk();
  private static final String NULL_OK_JSON = "{\"@ok\":null,\"@result\":\"OK\"}";
  private static final Result<String, NullValue> NULL_ERR = Result.nullErr();
  private static final String NULL_ERR_JSON = "{\"@error\":null,\"@result\":\"ERR\"}";

  private static ObjectMapper objectMapper;

  @BeforeClass
  public static void setupClass() {
    objectMapper = new ObjectMapper().registerModules(new ResultModule(), new GuavaModule());
  }

  @Test
  public void itSerializesBeanOk() throws Exception {
    itSerializes(BEAN_OK, BEAN_OK_JSON);
  }

  @Test
  public void itSerializesBeanErr() throws Exception {
    itSerializes(BEAN_ERR, BEAN_ERR_JSON);
  }

  @Test
  public void itSerializesCustomEnumErr() throws Exception {
    itSerializes(CUSTOM_ENUM_ERR, CUSTOM_ENUM_ERR_JSON);
  }

  @Test
  public void itSerializesRawErr() throws Exception {
    itSerializes(RAW_ENUM_ERR, RAW_ENUM_ERR_JSON);
  }

  @Test
  public void itSerializesStringOk() throws Exception {
    itSerializes(STRING_OK, STRING_OK_JSON);
  }

  @Test
  public void itSerializesStringErr() throws Exception {
    itSerializes(STRING_ERR, STRING_ERR_JSON);
  }

  @Test
  public void itSerializesListOk() throws Exception {
    itSerializes(LIST_OK, LIST_OK_JSON);
  }

  @Test
  public void itSerializesListBeanOk() throws Exception {
    itSerializes(BEAN_LIST_OK, BEAN_LIST_OK_JSON);
  }

  @Test
  public void itSerializesListBeanErr() throws Exception {
    itSerializes(BEAN_LIST_ERR, BEAN_LIST_ERR_JSON);
  }

  @Test
  public void itSerializesListErr() throws Exception {
    itSerializes(LIST_ERR, LIST_ERR_JSON);
  }

  @Test
  public void itSerializesMapOk() throws Exception {
    itSerializes(MAP_OK, MAP_OK_JSON);
  }

  @Test
  public void itSerializesMapErr() throws Exception {
    itSerializes(MAP_ERR, MAP_ERR_JSON);
  }

  @Test
  public void itSerializesMultimapOk() throws Exception {
    itSerializes(MULTIMAP_OK, MULTIMAP_OK_JSON);
  }

  @Test
  public void itSerializesMultimapErr() throws Exception {
    itSerializes(MULTIMAP_ERR, MULTIMAP_ERR_JSON);
  }

  @Test
  public void itSerializesTableOk() throws Exception {
    itSerializes(TABLE_OK, TABLE_OK_JSON);
  }

  @Test
  public void itSerializesTableErr() throws Exception {
    itSerializes(TABLE_ERR, TABLE_ERR_JSON);
  }

  @Test
  public void itSerializesNullOk() throws Exception {
    itSerializes(NULL_OK, NULL_OK_JSON);
  }

  @Test
  public void itSerializesNullErr() throws Exception {
    itSerializes(NULL_ERR, NULL_ERR_JSON);
  }

  @Test
  public void itDeserializesBeanOk() throws Exception {
    itDeserializes(
        BEAN_OK_JSON,
        new TypeReference<Result<TestBean, TestError>>(){},
        BEAN_OK
    );
  }

  @Test
  public void itDeserializesBeanErr() throws Exception {
    itDeserializes(
        BEAN_ERR_JSON,
        new TypeReference<Result<TestBean, TestBean>>(){},
        BEAN_ERR
    );
  }

  @Test
  public void itDeserializesCustomEnumErr() throws Exception {
    itDeserializes(
        CUSTOM_ENUM_ERR_JSON,
        new TypeReference<Result<TestBean, TestError>>(){},
        CUSTOM_ENUM_ERR
    );
  }

  @Test
  public void itDeserializesRawEnumErr() throws Exception {
    itDeserializes(
        RAW_ENUM_ERR_JSON,
        new TypeReference<Result<TestBean, RawError>>(){},
        RAW_ENUM_ERR
    );
  }

  @Test
  public void itDeserializesStringOk() throws Exception {
    itDeserializes(
        STRING_OK_JSON,
        new TypeReference<Result<String, String>>(){},
        STRING_OK
    );
  }

  @Test
  public void itDeserializesStringErr() throws Exception {
    itDeserializes(
        STRING_ERR_JSON,
        new TypeReference<Result<String, String>>(){},
        STRING_ERR
    );
  }

  @Test
  public void itDeserializesListOk() throws Exception {
    itDeserializes(
        LIST_OK_JSON,
        new TypeReference<Result<List<String>, List<String>>>(){},
        LIST_OK
    );
  }

  @Test
  public void itDeserializesBeanListOk() throws Exception {
    itDeserializes(
        BEAN_LIST_OK_JSON,
        new TypeReference<Result<List<TestBean>, List<TestError>>>(){},
        BEAN_LIST_OK
    );
  }

  @Test
  public void itDeserializesBeanListErr() throws Exception {
    itDeserializes(
        BEAN_LIST_ERR_JSON,
        new TypeReference<Result<List<TestBean>, List<TestError>>>(){},
        BEAN_LIST_ERR
    );
  }

  @Test
  public void itDeserializesListErr() throws Exception {
    itDeserializes(
        LIST_ERR_JSON,
        new TypeReference<Result<List<String>, List<String>>>(){},
        LIST_ERR
    );
  }

  @Test
  public void itDeserializesMapOk() throws Exception {
    itDeserializes(
        MAP_OK_JSON,
        new TypeReference<Result<Map<String, String>, Map<String, String>>>(){},
        MAP_OK
    );
  }

  @Test
  public void itDeserializesMapErr() throws Exception {
    itDeserializes(
        MAP_ERR_JSON,
        new TypeReference<Result<Map<String, String>, Map<String, String>>>(){},
        MAP_ERR
    );
  }

  @Test
  public void itDeserializesMultimapOk() throws Exception {
    itDeserializes(
        MULTIMAP_OK_JSON,
        new TypeReference<Result<Multimap<String, String>, Multimap<String, String>>>() {
        },
        MULTIMAP_OK
    );
  }

  @Test
  public void itDeserializesMultimapErr() throws Exception {
    itDeserializes(
        MULTIMAP_ERR_JSON,
        new TypeReference<Result<Multimap<String, String>, Multimap<String, String>>>(){},
        MULTIMAP_ERR
    );
  }

  @Test
  public void itDeserializesTableOk() throws Exception {
    assertThatThrownBy(() -> itDeserializes(
        TABLE_OK_JSON,
        new TypeReference<Result<Table<String, String, String>, Table<String, String, String>>>(){},
        TABLE_OK
    )).isInstanceOf(JsonMappingException.class)
      .hasMessageStartingWith("Can not construct instance of com.google.common.collect.Table");
  }

  @Test
  public void itDeserializesTableErr() throws Exception {
    assertThatThrownBy(() -> itDeserializes(
        TABLE_ERR_JSON,
        new TypeReference<Result<Table<String, String, String>, Table<String, String, String>>>(){},
        TABLE_ERR
    )).isInstanceOf(JsonMappingException.class)
      .hasMessageStartingWith("Can not construct instance of com.google.common.collect.Table");
  }

  @Test
  public void itDeserializesNullOk() throws Exception {
    itDeserializes(
        NULL_OK_JSON,
        new TypeReference<Result<NullValue, String>>(){},
        NULL_OK
    );
  }

  @Test
  public void itDeserializesNullErr() throws Exception {
    itDeserializes(
        NULL_ERR_JSON,
        new TypeReference<Result<String, NullValue>>(){},
        NULL_ERR
    );
  }

  private void itSerializes(Result<?, ?> result, String expectedJson) throws JsonProcessingException {
    assertThat(objectMapper.writeValueAsString(result)).isEqualTo(expectedJson);
  }

  private <OK, ERR> void itDeserializes(
      String inputJson,
      TypeReference<Result<OK, ERR>> type,
      Result<OK, ERR> expected
  ) throws IOException {
    Result<OK, ERR> actual = objectMapper.readValue(inputJson, type);
    assertThat(actual).isEqualTo(expected);
  }

  static class TestBean {
    private final String value;

    @JsonCreator
    public TestBean(@JsonProperty("value") String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if (obj == null || this.getClass() != obj.getClass()) {
        return false;
      }
      TestBean that = (TestBean) obj;
      return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }

    @Override
    public String toString() {
      return new StringBuilder("TestBean{")
          .append("value='").append(value).append('\'')
          .append('}')
          .toString();
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
