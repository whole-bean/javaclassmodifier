package com.example.javaclassmodifier.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileModifier {

  public static void modifyFile(File file) throws IOException {
    String content = new String(Files.readAllBytes(file.toPath()));

    // 기존 getter와 setter 제거
    content = removeExistingGettersAndSetters(content);

    // 정규식을 사용하여 새로운 getter와 setter 추가
    Pattern pattern = Pattern.compile("private (\\w+|List<\\w+>) (\\w+);");
    Matcher matcher = pattern.matcher(content);

    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      String type = matcher.group(1);
      String name = matcher.group(2);
      String capitalized = Character.toUpperCase(name.charAt(0)) + name.substring(1);

      // 보안적 getter와 setter 생성
      String getter = generateSecureGetter(type, name, capitalized);
      String setter = generateSecureSetter(type, name, capitalized);

      matcher.appendReplacement(sb, matcher.group() + "\n\n    " + getter + "\n\n    " + setter);
    }
    matcher.appendTail(sb);

    Files.write(file.toPath(), sb.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
  }

  private static String removeExistingGettersAndSetters(String content) {
    // getter와 setter를 찾기 위한 패턴
    String getterSetterPattern = "(public \\w+ get\\w+\\(\\) \\{[^}]+\\})|(public void set\\w+\\(\\w+ \\w+\\) \\{[^}]+\\})";
    return content.replaceAll(getterSetterPattern, "");
  }

  private static String generateSecureGetter(String type, String name, String capitalized) {
    if (type.startsWith("List")) {
      return "public " + type + " get" + capitalized + "() {\n"
          + "        return " + name + " == null ? null : new ArrayList<>(" + name + ");\n"
          + "    }";
    } else {
      return "public " + type + " get" + capitalized + "() {\n"
          + "        return " + name + ";\n"
          + "    }";
    }
  }

  private static String generateSecureSetter(String type, String name, String capitalized) {
    String validation = "";
    if (type.equals("String")) {
      validation = "        if (" + name + " == null || " + name + ".trim().isEmpty()) {\n"
          + "            throw new IllegalArgumentException(\"" + name + " cannot be null or empty\");\n"
          + "        }\n";
    } else if (type.equals("BigDecimal")) {
      validation = "        if (" + name + " == null || " + name + ".compareTo(BigDecimal.ZERO) < 0) {\n"
          + "            throw new IllegalArgumentException(\"" + name + " cannot be null or negative\");\n"
          + "        }\n";
    } else if (type.startsWith("List")) {
      validation = "        if (" + name + " == null) {\n"
          + "            throw new IllegalArgumentException(\"" + name + " cannot be null\");\n"
          + "        }\n";
    } else {
      validation = "        if (" + name + " == null) {\n"
          + "            throw new IllegalArgumentException(\"" + name + " cannot be null\");\n"
          + "        }\n";
    }

    return "public void set" + capitalized + "(" + type + " " + name + ") {\n"
        + validation
        + "        this." + name + " = " + name + ";\n"
        + "    }";
  }
}
