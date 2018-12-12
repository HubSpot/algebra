package com.hubspot.algebra;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NullValue {
  /**
   * @deprecated Call {@link NullValue#get()} instead of accessing this enum constant directly.
   */
  @Deprecated
  INSTANCE;

  public static NullValue get() {
    return INSTANCE;
  }

  @JsonValue
  Object getJsonValue() {
    return null;
  }
}
