package com.garfild63;

import java.util.Vector;

public class Matrix {
  private int[][] arr;
  private int width, height;
  
  public Matrix(int a, int b) {
    width = a;
    height = b;
    arr = new int[width][height];
  }
  
  public Matrix(int[][] array) {
    arr = array;
    width = array.length;
    height = array[0].length;
  }
  
  public void setSize(int a, int b) {
    int[][] array = new int[a][b];
    int mina = Math.min(a, width);
    int minb = Math.min(b, height);
    for (int i = 0; i < mina; i++) {
      System.arraycopy(arr[i], 0, array[i], 0, minb);
    }
    arr = array;
    width = a;
    height = b;
  }
  
  public void toSquareMatrix() {
    int max = Math.max(width, height);
    setSize(max, max);
  }
  
  public void toTriangleMatrix() {
    toSquareMatrix();
    for (int i = 0; i < width; i++) {
      if (arr[i][i] == 0) {
        for (int j = i + 1; j < width; j++) {
          if (arr[j][i] != 0) {
            swapLines(i, j);
            break;
          }
        }
      }
      for (int j = i + 1; j < width; j++) {
        if (arr[j][i] != 0) {
          int lcm = MathUtils.lcm(arr[i][i], arr[j][i]);
          multipleLine(i, lcm / arr[i][i]);
          multipleLine(j, -lcm / arr[j][i]);
          addLine(j, i);
        }
      }
    }
  }
  
  public void setValue(int x, int y, int n) {
    arr[x][y] = n;
  }
  
  public int getValue(int x, int y) {
    return arr[x][y];
  }
  
  public int[] getLine(int x) {
    return arr[x];
  }
  
  public void setLine(int x, int[] line) {
    System.arraycopy(line, 0, arr[x], 0, height);
  }
  
  public void swapLines(int x, int y) {
    int[] q = arr[x];
    arr[x] = arr[y];
    arr[y] = q;
  }
  
  public void multipleLine(int x, int n) {
    for (int i = 0; i < height; i++) {
      arr[x][i] *= n;
    }
  }
  
  public void addLine(int x, int y) {
    for (int i = 0; i < height; i++) {
      arr[x][i] += arr[y][i];
    }
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < arr.length; i++) {
      for (int j = 0; j < arr[i].length; j++) {
        sb.append(arr[i][j] + " ");
      }
      sb.append("\n");
    }
    return sb.toString();
  }
}
