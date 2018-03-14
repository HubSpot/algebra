package com.hubspot.algebra;

import static com.hubspot.algebra.ResultModule.CASE_FIELD_NAME;
import static com.hubspot.algebra.ResultModule.ERROR_FIELD_NAME;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.EnumSerializer;
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
      JsonSerializer<?> errorSerializer = provider.findTypedValueSerializer(err.getClass(), true, null);

      if (errorSerializer instanceof EnumSerializer) {
        EnumSerializer enumSerializer = ((EnumSerializer) errorSerializer);
        Enum<?> enumErr = ((Enum<?>) err);

        gen.writeFieldName(ERROR_FIELD_NAME);
        enumSerializer.serialize(enumErr, gen, provider);
      } else {
        JsonSerializer<Object> objectErrorSerializer = ((JsonSerializer<Object>) errorSerializer);
        objectErrorSerializer.unwrappingSerializer(null).serialize(err, gen, provider);
      }

      gen.writeStringField(CASE_FIELD_NAME, Case.ERR.name());
    } else {
      Object ok = value.unwrapOrElseThrow();

      JsonSerializer<Object> okSerializer = provider.findTypedValueSerializer(ok.getClass(), true, null);
      okSerializer.unwrappingSerializer(null).serialize(ok, gen, provider);
      gen.writeStringField(CASE_FIELD_NAME, Case.OK.name());
    }

    gen.writeEndObject();
  }
}
