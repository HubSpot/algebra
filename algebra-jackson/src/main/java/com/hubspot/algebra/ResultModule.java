package com.hubspot.algebra;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.google.inject.Inject;

public class ResultModule extends Module {
  static final String CASE_FIELD_NAME = "@result";
  static final String ERROR_FIELD_NAME = "@error";

  enum Case {
    OK,
    ERR;
  }

  @Inject
  public ResultModule() {}

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
