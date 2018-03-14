package com.hubspot.algebra;

import static com.hubspot.algebra.ResultModule.CASE_FIELD_NAME;
import static com.hubspot.algebra.ResultModule.ERROR_FIELD_NAME;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hubspot.algebra.ResultModule.Case;

public class ResultDeserializer extends StdDeserializer<Result<?, ?>> {
  private final Class<?> okClass;
  private final Class<?> errClass;

  public ResultDeserializer(JavaType valueType) {
    super(valueType);

    this.okClass = valueType.getBindings().getBoundType(0).getRawClass();
    this.errClass = valueType.getBindings().getBoundType(1).getRawClass();
  }

  @Override
  public Result<?, ?> deserialize(JsonParser p,
                                  DeserializationContext ctxt) throws IOException, JsonProcessingException {
    ObjectMapper objectMapper = ((ObjectMapper) p.getCodec());
    ObjectNode node = objectMapper.readTree(p);
    String resultCase = node.findValue(CASE_FIELD_NAME).textValue();
    node.remove(CASE_FIELD_NAME);

    if (resultCase.equalsIgnoreCase(Case.ERR.toString())) {
      if (node.has(ERROR_FIELD_NAME)) {
        Object err = objectMapper.treeToValue(node.findValue(ERROR_FIELD_NAME), errClass);
        return Results.err(err);
      }

      Object err = objectMapper.treeToValue(node, errClass);
      return Results.err(err);
    } else {
      Object ok = objectMapper.treeToValue(node, okClass);
      return Results.ok(ok);
    }
  }
}
