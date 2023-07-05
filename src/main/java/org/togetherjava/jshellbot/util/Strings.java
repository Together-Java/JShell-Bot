package org.togetherjava.jshellbot.util;

public class Strings {

  /**
   * Limits the size of a string to the given maximum length.
   *
   * @param input the input to limit
   * @param max the maximum length
   * @return the abbreviated string
   */
  public static String limitSize(String input, int max) {
    if (input.length() <= max) {
      return input;
    }
    return input.substring(0, max - 3) + "...";
  }
}
