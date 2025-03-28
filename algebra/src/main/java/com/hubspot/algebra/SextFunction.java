package com.hubspot.algebra;

@FunctionalInterface
public interface SextFunction<A, B, C, D, E, F, R> {
  R apply(A a, B b, C c, D d, E e, F f);
}
