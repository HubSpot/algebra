package com.hubspot.algebra;

public enum NullValue {
  // We name it this instead of INSTANCE for more human-readable JSON
  /**
   * @deprecated Call {@link NullValue#get()} instead of accessing this enum constant directly.
   */
  @Deprecated
  NULL_VALUE;

  public static NullValue get() {
    return NULL_VALUE;
  }
}
