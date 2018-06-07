package com.hubspot.algebra;

import static com.hubspot.algebra.ResultModule.CASE_FIELD_NAME;
import static com.hubspot.algebra.ResultModule.ERROR_FIELD_NAME;
import static com.hubspot.algebra.ResultModule.OK_FIELD_NAME;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.hubspot.algebra.ResultModule.Case;

public class ResultSerializer extends StdSerializer<Result<?, ?>> {
  ResultSerializer(JavaType type) {
    super(type);
  }

  @Override
  public void serialize(Result<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeStartObject();

    if (value.isErr()) {
      Object err = value.unwrapErrOrElseThrow();
      serializeValue(ERROR_FIELD_NAME, err, gen, provider);
      gen.writeStringField(CASE_FIELD_NAME, Case.ERR.name());
    } else {
      Object ok = value.unwrapOrElseThrow();
      serializeValue(OK_FIELD_NAME, ok, gen, provider);
      gen.writeStringField(CASE_FIELD_NAME, Case.OK.name());
    }

    gen.writeEndObject();
  }

  private static void serializeValue(
      String fieldName,
      Object value,
      JsonGenerator gen,
      SerializerProvider provider
  ) throws IOException {
    JsonSerializer<Object> serializer = provider.findTypedValueSerializer(value.getClass(), true, null)
                                                .unwrappingSerializer(null);
    if (!serializer.isUnwrappingSerializer()) {
      gen.writeFieldName(fieldName);
    }
    serializer.serialize(value, gen, provider);
  }
}
