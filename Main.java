package com.garfild63;

import java.util.Vector;


public class Main {

  public static void main(String[] args) {
    Parser p = new Parser("NH4Cl+NaOH=NaCl+NH3*H2O");
    Vector v = p.tokenize();
    for (int i = 0; i < v.size(); i++) {
      Substance s = (Substance) v.elementAt(i);
      System.out.println(s);
    }
  }
}
