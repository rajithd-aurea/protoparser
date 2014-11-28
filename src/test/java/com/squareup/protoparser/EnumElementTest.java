package com.squareup.protoparser;

import org.junit.Test;

import static com.squareup.protoparser.EnumElement.Value;
import static com.squareup.protoparser.TestUtils.NO_OPTIONS;
import static com.squareup.protoparser.TestUtils.NO_VALUES;
import static com.squareup.protoparser.TestUtils.list;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

public class EnumElementTest {
  @Test public void emptyToString() {
    EnumElement element = EnumElement.create("Enum", "", "", NO_OPTIONS, NO_VALUES);
    String expected = "enum Enum {}\n";
    assertThat(element.toString()).isEqualTo(expected);
  }

  @Test public void simpleToString() {
    Value one = Value.create("ONE", 1, "", NO_OPTIONS);
    Value two = Value.create("TWO", 2, "", NO_OPTIONS);
    Value six = Value.create("SIX", 6, "", NO_OPTIONS);
    EnumElement element = EnumElement.create("Enum", "", "", NO_OPTIONS, list(one, two, six));
    String expected = ""
        + "enum Enum {\n"
        + "  ONE = 1;\n"
        + "  TWO = 2;\n"
        + "  SIX = 6;\n"
        + "}\n";
    assertThat(element.toString()).isEqualTo(expected);
  }

  @Test public void simpleWithOptionsToString() {
    Value one = Value.create("ONE", 1, "", NO_OPTIONS);
    Value two = Value.create("TWO", 2, "", NO_OPTIONS);
    Value six = Value.create("SIX", 6, "", NO_OPTIONS);
    OptionElement kitKat = OptionElement.create("kit", "kat", false);
    EnumElement element = EnumElement.create("Enum", "", "", list(kitKat), list(one, two, six));
    String expected = ""
        + "enum Enum {\n"
        + "  option kit = \"kat\";\n"
        + "\n"
        + "  ONE = 1;\n"
        + "  TWO = 2;\n"
        + "  SIX = 6;\n"
        + "}\n";
    assertThat(element.toString()).isEqualTo(expected);
  }

  @Test public void simpleWithDocumentationToString() {
    Value one = Value.create("ONE", 1, "", NO_OPTIONS);
    Value two = Value.create("TWO", 2, "", NO_OPTIONS);
    Value six = Value.create("SIX", 6, "", NO_OPTIONS);
    EnumElement element = EnumElement.create("Enum", "", "Hello", NO_OPTIONS, list(one, two, six));
    String expected = ""
        + "// Hello\n"
        + "enum Enum {\n"
        + "  ONE = 1;\n"
        + "  TWO = 2;\n"
        + "  SIX = 6;\n"
        + "}\n";
    assertThat(element.toString()).isEqualTo(expected);
  }

  @Test public void fieldToString() {
    Value value = Value.create("NAME", 1, "", NO_OPTIONS);
    String expected = "NAME = 1;\n";
    assertThat(value.toString()).isEqualTo(expected);
  }

  @Test public void fieldWithDocumentationToString() {
    Value value = Value.create("NAME", 1, "Hello", NO_OPTIONS);
    String expected = ""
        + "// Hello\n"
        + "NAME = 1;\n";
    assertThat(value.toString()).isEqualTo(expected);
  }

  @Test public void fieldWithOptions() {
    Value value = Value.create("NAME", 1, "",
        list(OptionElement.create("kit", "kat", true), OptionElement.create("tit", "tat", false)));
    String expected = "NAME = 1 [\n"
        + "  (kit) = \"kat\",\n"
        + "  tit = \"tat\"\n"
        + "];\n";
    assertThat(value.toString()).isEqualTo(expected);
  }

  @Test public void duplicateValueTagThrows() {
    Value value1 = Value.create("VALUE1", 1, "", NO_OPTIONS);
    Value value2 = Value.create("VALUE2", 1, "", NO_OPTIONS);
    try {
      EnumElement.create("Enum1", "example.Enum", "", NO_OPTIONS, list(value1, value2));
      fail("Duplicate tags not allowed.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Duplicate tag 1 in example.Enum");
    }
  }

  @Test public void duplicateValueTagWithAllowAlias() {
    OptionElement option1 = OptionElement.create("allow_alias", true, false);
    Value value1 = Value.create("VALUE1", 1, "", NO_OPTIONS);
    Value value2 = Value.create("VALUE2", 1, "", NO_OPTIONS);
    EnumElement element =
        EnumElement.create("Enum1", "example.Enum", "", list(option1), list(value1, value2));

    String expected = ""
        + "enum Enum1 {\n"
        + "  option allow_alias = true;\n"
        + "\n"
        + "  VALUE1 = 1;\n"
        + "  VALUE2 = 1;\n"
        + "}\n";

    assertThat(element.toString()).isEqualTo(expected);
  }
}