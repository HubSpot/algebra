package com.hubspot.algebra;

public enum NullValue {
  // We name it this instead of INSTANCE for more human-readable JSON
  NULL_VALUE;

  public static NullValue get() {
    return NULL_VALUE;
  }
}
