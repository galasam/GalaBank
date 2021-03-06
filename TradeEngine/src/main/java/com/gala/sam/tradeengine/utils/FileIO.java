package com.gala.sam.tradeengine.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileIO {

  private FileIO() {
  }

  public static boolean fileExists(String filename) {
    return new java.io.File(filename).exists();
  }

  public static List<String> readTestFile(String filename) throws IOException {
    return Files.readAllLines(Paths.get(filename));
  }

  public static void writeTestFile(String filename, List<String> contents)
      throws FileNotFoundException, UnsupportedEncodingException {
    try (PrintWriter writer = new PrintWriter(filename, "UTF-8")) {
      contents.forEach(writer::println);
    }
  }

}
