package com.hubspot.algebra;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class ResultModule extends Module {

  static final String CASE_FIELD_NAME = "@result";
  static final String OK_FIELD_NAME = "@ok";
  static final String ERROR_FIELD_NAME = "@error";

  enum Case {
    OK,
    ERR,
  }

  @Override
  public String getModuleName() {
    return "ResultModule";
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }

  @Override
  public void setupModule(SetupContext context) {
    context.addSerializers(new ResultSerializers());
    context.addDeserializers(new ResultDeserializers());
  }
}
