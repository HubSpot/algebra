package com.hubspot.algebra;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;

public class ResultSerializers extends Serializers.Base {
  @Override
  public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
    final Class<?> raw = type.getRawClass();
    if (Result.class.isAssignableFrom(raw)) {
      return new ResultSerializer(type);
    }

    return null;
  }
}
