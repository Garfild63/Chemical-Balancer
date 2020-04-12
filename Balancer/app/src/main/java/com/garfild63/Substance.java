package com.garfild63;

import java.util.Enumeration;
import java.util.Hashtable;

public class Substance {
  private final Hashtable els;
  private String name = null;
  private final boolean flag;
  
  public Substance() {
    this(true);
  }
  
  public Substance(boolean isReactive) {
    els = new Hashtable();
    flag = isReactive;
  }
  
  public Substance(String name, boolean isReactive) {
    this(isReactive);
    this.name = name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public boolean isReactive() {
    return flag;
  }
  
  public Enumeration getElements() {
    return els.keys();
  }
  
  public int get(String el) {
    if (!els.containsKey(el))
      return 0;
    return ((Integer) els.get(el)).intValue();
  }
  
  public void set(String el, int n) {
    els.put(el, new Integer(n));
  }
  
  public Substance addElement(String el, int k) {
    int n = get(el);
    n += k;
    set(el, n);
    return this;
  }
  
  public Substance add(Substance s) {
    Enumeration e = s.getElements();
    while (e.hasMoreElements()) {
      String el = (String) e.nextElement();
      int i = s.get(el);
      addElement(el, i);
    }
    return this;
  }
  
  public Substance multiple(int n) {
    Enumeration e = getElements();
    while (e.hasMoreElements()) {
      String el = (String) e.nextElement();
      int i = get(el);
      set(el, i * n);
    }
    return this;
  }
  
  public String toString() {
    Enumeration e = getElements();
    StringBuffer sb = new StringBuffer();
    while (e.hasMoreElements()) {
      String el = (String) e.nextElement();
      int i = get(el);
      sb.append(el + i);
    }
    return sb.toString();
  }
}
