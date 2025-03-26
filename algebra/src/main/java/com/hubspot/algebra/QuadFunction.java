package com.hubspot.algebra;

@FunctionalInterface
public interface QuadFunction<A, B, C, D, R> {
  R apply(A a, B b, C c, D d);
}
