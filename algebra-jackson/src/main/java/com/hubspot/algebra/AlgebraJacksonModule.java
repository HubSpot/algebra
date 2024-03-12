package com.hubspot.algebra;

import com.fasterxml.jackson.databind.Module;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class AlgebraJacksonModule extends AbstractModule {

  @Override
  protected void configure() {
    Multibinder
      .newSetBinder(binder(), Module.class)
      .addBinding()
      .toInstance(new ResultModule());
  }

  @Override
  public boolean equals(Object o) {
    return o != null && getClass().equals(o.getClass());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
