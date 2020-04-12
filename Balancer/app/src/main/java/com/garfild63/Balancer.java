package com.garfild63;

import java.util.Enumeration;
import java.util.Vector;


public class Balancer {
  public static String balance(String eq) {
    Parser p = new Parser(eq);
    Vector v = p.tokenize();
    Vector els = new Vector();
    for (int i = 0; i < v.size(); i++) {
      Substance s = (Substance) v.elementAt(i);
      Enumeration e = s.getElements();
      while (e.hasMoreElements()) {
        String el = (String) e.nextElement();
        if (!els.contains(el))
          els.addElement(el);
      }
    }
    int[][] arr = new int[els.size()][v.size()];
    for (int i = 0; i < v.size(); i++) {
      Substance s = (Substance) v.elementAt(i);
      Enumeration e = s.getElements();
      while (e.hasMoreElements()) {
        String el = (String) e.nextElement();
        arr[els.indexOf(el)][i] = s.isReactive() ? s.get(el) : -s.get(el);
      }
    }
    Matrix m = new Matrix(arr);
    m.toTriangleMatrix();
    m.setSize(els.size(), v.size());
    int size = v.size();
    int[] koefs = new int[size];
    koefs[size - 1] = 1;
    for (int i = size - 2; i >= 0; i--) {
      int sum = 0;
      for (int j = i + 1; j < size; j++) {
        sum -= koefs[j] * m.getValue(i, j);
      }
      int value = m.getValue(i, i);
      if (sum % value == 0) {
        koefs[i] = sum / value;
      } else {
        int lcm = MathUtils.lcm(sum, value);
        koefs[i] = lcm / value;
        for (int j = i + 1; j < size; j++) {
          koefs[j] *= lcm / sum;
        }
      }
    }
    StringBuffer sb = new StringBuffer();
    boolean flag = false;
    for (int i = 0; i < size; i++) {
      Substance s = (Substance) v.elementAt(i);
      if (flag && s.isReactive() || !flag && !s.isReactive())
        sb.append("+");
      if (flag && !s.isReactive())
        sb.append("=");
      if (koefs[i] != 1)
        sb.append(koefs[i]);
      sb.append(s.getName());
      flag = s.isReactive();
    }
    return sb.toString();
  }
}
