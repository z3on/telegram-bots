package com.github.z3on.telegram.util;

import java.util.function.Predicate;

public final class LambdaUtils {

  private LambdaUtils() {
  }

  public static <T> Predicate<T> not(Predicate<T> predicate) {
    return predicate.negate();
  }
}
