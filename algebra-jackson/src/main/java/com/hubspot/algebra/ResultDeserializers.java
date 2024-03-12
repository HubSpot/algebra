package com.hubspot.algebra;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.Deserializers;

public class ResultDeserializers extends Deserializers.Base {

  @Override
  public JsonDeserializer<?> findBeanDeserializer(
    JavaType type,
    DeserializationConfig config,
    BeanDescription beanDesc
  ) throws JsonMappingException {
    if (type.hasRawClass(Result.class)) {
      return new ResultDeserializer(type);
    }

    return null;
  }
}
