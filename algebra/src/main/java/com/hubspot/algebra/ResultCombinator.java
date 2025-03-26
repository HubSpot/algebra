package com.hubspot.algebra;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A type-accumulating builder for combining Results.
 * This approach builds up the type information through each step,
 * allowing type-safe access to all accumulated values.
 */
class ResultCombinator {

  /**
   * Start combining with one Result.
   */
  public static <A, E> R1<A, E> combine(Result<A, E> r1) {
    if (r1.isErr()) {
      return new R1<>(null, r1.coerceErr());
    }
    return new R1<>(r1.unwrapOrElseThrow(), null);
  }

  /**
   * Holder for one Result value.
   */
  public static class R1<A, E> {

    private final A value1;
    private final Result<?, E> error;

    private R1(A value1, Result<?, E> error) {
      this.value1 = value1;
      this.error = error;
    }

    /**
     * Add a second Result to be combined.
     */
    public <B> R2<A, B, E> and(Result<B, E> r2) {
      if (error != null) {
        return new R2<>(value1, null, error);
      }
      if (r2.isErr()) {
        return new R2<>(value1, null, r2.coerceErr());
      }
      return new R2<>(value1, r2.unwrapOrElseThrow(), null);
    }

    /**
     * Complete the combination with a mapping function for one value.
     */
    public <R> Result<R, E> map(Function<A, R> mapper) {
      if (error != null) {
        return error.coerceErr();
      }
      return Result.ok(mapper.apply(value1));
    }
  }

  /**
   * Holder for two Result values.
   */
  public static class R2<A, B, E> {

    private final A value1;
    private final B value2;
    private final Result<?, E> error;

    private R2(A value1, B value2, Result<?, E> error) {
      this.value1 = value1;
      this.value2 = value2;
      this.error = error;
    }

    /**
     * Add a third Result to be combined.
     */
    public <C> R3<A, B, C, E> and(Result<C, E> r3) {
      if (error != null) {
        return new R3<>(value1, value2, null, error);
      }
      if (r3.isErr()) {
        return new R3<>(value1, value2, null, r3.coerceErr());
      }
      return new R3<>(value1, value2, r3.unwrapOrElseThrow(), null);
    }

    /**
     * Complete the combination with a mapping function for two values.
     */
    public <R> Result<R, E> map(BiFunction<A, B, R> mapper) {
      if (error != null) {
        return error.coerceErr();
      }
      return Result.ok(mapper.apply(value1, value2));
    }
  }

  /**
   * Holder for three Result values.
   */
  public static class R3<A, B, C, E> {

    private final A value1;
    private final B value2;
    private final C value3;
    private final Result<?, E> error;

    private R3(A value1, B value2, C value3, Result<?, E> error) {
      this.value1 = value1;
      this.value2 = value2;
      this.value3 = value3;
      this.error = error;
    }

    /**
     * Add a fourth Result to be combined.
     */
    public <D> R4<A, B, C, D, E> and(Result<D, E> r4) {
      if (error != null) {
        return new R4<>(value1, value2, value3, null, error);
      }
      if (r4.isErr()) {
        return new R4<>(value1, value2, value3, null, r4.coerceErr());
      }
      return new R4<>(value1, value2, value3, r4.unwrapOrElseThrow(), null);
    }

    /**
     * Complete the combination with a mapping function for three values.
     */
    public <R> Result<R, E> map(com.hubspot.algebra.TriFunction<A, B, C, R> mapper) {
      if (error != null) {
        return error.coerceErr();
      }
      return Result.ok(mapper.apply(value1, value2, value3));
    }
  }

  /**
   * Holder for four Result values.
   */
  public static class R4<A, B, C, D, E> {

    private final A value1;
    private final B value2;
    private final C value3;
    private final D value4;
    private final Result<?, E> error;

    private R4(A value1, B value2, C value3, D value4, Result<?, E> error) {
      this.value1 = value1;
      this.value2 = value2;
      this.value3 = value3;
      this.value4 = value4;
      this.error = error;
    }

    /**
     * Add a fifth Result to be combined.
     */
    public <F> R5<A, B, C, D, F, E> and(Result<F, E> r5) {
      if (error != null) {
        return new R5<>(value1, value2, value3, value4, null, error);
      }
      if (r5.isErr()) {
        return new R5<>(value1, value2, value3, value4, null, r5.coerceErr());
      }
      return new R5<>(value1, value2, value3, value4, r5.unwrapOrElseThrow(), null);
    }

    /**
     * Complete the combination with a mapping function for four values.
     */
    public <R> Result<R, E> map(QuadFunction<A, B, C, D, R> mapper) {
      if (error != null) {
        return error.coerceErr();
      }
      return Result.ok(mapper.apply(value1, value2, value3, value4));
    }
  }

  /**
   * Holder for five Result values.
   */
  public static class R5<A, B, C, D, E, F> {

    private final A value1;
    private final B value2;
    private final C value3;
    private final D value4;
    private final E value5;
    private final Result<?, F> error;

    private R5(A value1, B value2, C value3, D value4, E value5, Result<?, F> error) {
      this.value1 = value1;
      this.value2 = value2;
      this.value3 = value3;
      this.value4 = value4;
      this.value5 = value5;
      this.error = error;
    }

    /**
     * Complete the combination with a mapping function for five values.
     */
    public <R> Result<R, F> map(QuintFunction<A, B, C, D, E, R> mapper) {
      if (error != null) {
        return error.coerceErr();
      }
      return Result.ok(mapper.apply(value1, value2, value3, value4, value5));
    }
  }
}
