package com.hubspot.algebra;

@FunctionalInterface
public interface TriFunction<A, B, C, R> {
  R apply(A a, B b, C c);
}
