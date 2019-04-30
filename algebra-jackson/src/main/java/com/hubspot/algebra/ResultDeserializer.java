package com.hubspot.algebra;

import static com.hubspot.algebra.ResultModule.CASE_FIELD_NAME;
import static com.hubspot.algebra.ResultModule.ERROR_FIELD_NAME;
import static com.hubspot.algebra.ResultModule.OK_FIELD_NAME;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hubspot.algebra.ResultModule.Case;

public class ResultDeserializer extends StdDeserializer<Result<?, ?>> {
  private final JavaType okType;
  private final JavaType errType;

  public ResultDeserializer(JavaType valueType) {
    super(valueType);

    this.okType = valueType.getBindings().getBoundType(0);
    this.errType = valueType.getBindings().getBoundType(1);
  }

  @Override
  public Result<?, ?> deserialize(JsonParser p,
                                  DeserializationContext ctxt) throws IOException {
    ObjectCodec codec = p.getCodec();
    ObjectNode node = codec.readTree(p);
    JsonNode caseNode = node.findValue(CASE_FIELD_NAME);

    if (caseNode == null) {
      throw new JsonMappingException(p, String.format("Could not deserialize input as a Result. The required %s field is missing.", CASE_FIELD_NAME));
    }

    String resultCase = caseNode.textValue();
    node.remove(CASE_FIELD_NAME);

    if (resultCase.equalsIgnoreCase(Case.ERR.toString())) {
      Object err = deserializeValue(codec, node, ERROR_FIELD_NAME, errType);
      return Results.err(err);
    } else {
      Object ok = deserializeValue(codec, node, OK_FIELD_NAME, okType);
      return Results.ok(ok);
    }
  }

  private static Object deserializeValue(
      ObjectCodec codec,
      ObjectNode node,
      String fieldName,
      JavaType type
  ) throws IOException {
    JsonNode valueNode = node.has(fieldName) ? node.findValue(fieldName) : node;
    if (type.getRawClass() == NullValue.class && valueNode.isNull()) {
      // Our version of Jackson doesn't allow custom deserialization of null
      return NullValue.get();
    } else {
      return codec.readValue(valueNode.traverse(codec), type);
    }
  }
}
