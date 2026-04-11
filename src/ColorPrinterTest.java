import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColorPrinterTest {

  @Test
  void testPrintlnWithRedColorAndReset() {
    // Arrange: Capture the printed output
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream);

    ColorPrinter printer = new ColorPrinter(printStream);
    printer.setCurrentColor(ConsoleColor.RED);

    // Act: Print the message
    String message = "I speak for the trees";
    printer.println(message);


    String expectedOutput = ConsoleColor.RED + "I speak for the trees" + System.lineSeparator() + ConsoleColor.RESET;

    // Assert: Verify the printed output
    assertEquals(expectedOutput, outputStream.toString());
  }

  @Test
  void testPrintWithoutReset() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream);

    ColorPrinter printer = new ColorPrinter(printStream);
    printer.setCurrentColor(ConsoleColor.GREEN);

    String message = "Hello";
    printer.print(message, false);

    String expectedOutput = ConsoleColor.GREEN + "Hello";

    assertEquals(expectedOutput, outputStream.toString());
  }

  @Test
  void testPrintWithReset() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream);

    ColorPrinter printer = new ColorPrinter(printStream);
    printer.setCurrentColor(ConsoleColor.BLUE);

    String message = "Reset me";
    printer.print(message, true);

    String expectedOutput = ConsoleColor.BLUE + "Reset me" + ConsoleColor.RESET;

    assertEquals(expectedOutput, outputStream.toString());
  }

  @Test
  void testPrintlnWithDefaultColor() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream);

    ColorPrinter printer = new ColorPrinter(printStream);

    String message = "No color";
    printer.println(message);

    String expectedOutput = ConsoleColor.WHITE + "No color" + System.lineSeparator() + ConsoleColor.RESET;

    assertEquals(expectedOutput, outputStream.toString());
  }
}
