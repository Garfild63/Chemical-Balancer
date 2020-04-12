package com.garfild63;

public class Main {

  public static void main(String[] args) {
    // H2O=H2+O2
    // HCl+Ca(OH)2=CaCl2+H2O
    // C+HNO3=CO2+NO+H2O
    // C6H12O6+KMnO4+H2SO4=CO2+MnSO4+K2SO4+H2O
    String st = "";
    try {
      st = Balancer.balance("H2SO4+NH3*H2O=(NH4)2SO4+H2O");
    } catch (Exception e) {
      st = "Invalid equation";
    } finally {
      System.out.println(st);
    }
  }
}
