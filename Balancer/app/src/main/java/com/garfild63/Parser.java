package com.garfild63;

import java.util.Vector;

public class Parser {
  private final String input;
  private final int length;
  private final Vector tokens;
  private final StringBuffer buffer, sb;
  private int pos;

  public Parser(String input) {
    this.input = input;
    length = input.length();
    buffer = new StringBuffer();
    sb = new StringBuffer();
    tokens = new Vector();
  }

  public Vector tokenize() {
    boolean isLeft = true;
    while (true) {
      char current = peek(0);
      if (current == '+') {
        next();
      } else if (current == '=') {
        next();
        if (isLeft)
          isLeft = false;
        else
          break;
      } else if (current == '\0') {
        break;
      } else {
        sb.setLength(0);
        Substance s = tokenizeSubstance(isLeft);
        s.setName(sb.toString());
        tokens.addElement(s);
      }
    }
    return tokens;
  }

  private Substance tokenizeSubstance(boolean flag) {
    Substance cur = new Substance(flag);
    Substance tmp = null;
    while (true) {
      char current = peek(0);
      if (Character.isDigit(current)) {
        int n = tokenizeNumber();
        sb.append(n);
        tmp.multiple(n);
      } else if (Character.isUpperCase(current)) {
        String el = tokenizeElement();
        sb.append(el);
        if (tmp != null)
          cur.add(tmp);
        tmp = new Substance().addElement(el, 1);
      } else if (current == '(' || current == '[') {
        sb.append(current);
        next();
        if (tmp != null)
          cur.add(tmp);
        tmp = tokenizeSubstance(true);
      } else if (current == ')' || current == ']') {
        sb.append(current);
        next();
        if (tmp != null)
          cur.add(tmp);
        break;
      } else if (current == '+' || current == '=' || current == '\0') {
        if (tmp != null)
          cur.add(tmp);
        break;
      } else if (current == '*') {
        sb.append(current);
        next();
        int n = 1;
        try {
          n = tokenizeNumber();
          sb.append(n);
        } catch (NumberFormatException nfe) {
        }
        if (tmp != null)
          cur.add(tmp);
        tmp = tokenizeSubstance(true).multiple(n);
      } else {
        sb.append(current);
        next();
      }
    }
    return cur;
  }

  private String tokenizeElement() {
    clearBuffer();
    char current = peek(0);
    buffer.append(current);
    current = next();
    while (true) {
      if (!Character.isLowerCase(current)) {
        break;
      }
      buffer.append(current);
      current = next();
    }
    return buffer.toString();
  }

  private int tokenizeNumber() {
    clearBuffer();
    char current = peek(0);
    while (true) {
      if (!Character.isDigit(current)) {
        break;
      }
      buffer.append(current);
      current = next();
    }
    return Integer.parseInt(buffer.toString());
  }

  private void clearBuffer() {
    buffer.setLength(0);
  }

  private char next() {
    pos++;
    final char result = peek(0);
    return result;
  }

  private char peek(int relativePosition) {
    final int position = pos + relativePosition;
    if (position >= length) return '\0';
    return input.charAt(position);
  }
}
