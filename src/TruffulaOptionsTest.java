import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TruffulaOptionsTest {

  @Test
  void testValidDirectoryIsSet(@TempDir File tempDir) throws FileNotFoundException {
    // Arrange: Prepare the arguments with the temp directory
    File directory = new File(tempDir, "subfolder");
    directory.mkdir();
    String directoryPath = directory.getAbsolutePath();
    String[] args = {"-nc", "-h", directoryPath};

    // Act: Create TruffulaOptions instance
    TruffulaOptions options = new TruffulaOptions(args);

    // Assert: Check that the root directory is set correctly
    assertEquals(directory.getAbsolutePath(), options.getRoot().getAbsolutePath());
    assertTrue(options.isShowHidden());
    assertFalse(options.isUseColor());
  }

  @Test
  void testDefaultFlagsOnlyPath(@TempDir File tempDir) throws Exception {
    File directory = new File(tempDir, "folder");
    directory.mkdir();

    String[] args = {directory.getAbsolutePath()};

    TruffulaOptions options = new TruffulaOptions(args);

    assertEquals(directory.getAbsolutePath(), options.getRoot().getAbsolutePath());
    assertFalse(options.isShowHidden()); // default false
    assertTrue(options.isUseColor());    // default true
  }

  @Test
  void testShowHiddenOnly(@TempDir File tempDir) throws Exception {
    File directory = new File(tempDir, "folder");
    directory.mkdir();

    String[] args = {"-h", directory.getAbsolutePath()};

    TruffulaOptions options = new TruffulaOptions(args);

    assertTrue(options.isShowHidden());
    assertTrue(options.isUseColor()); // default stays true
  }

  @Test
  void testNoColorOnly(@TempDir File tempDir) throws Exception {
    File directory = new File(tempDir, "folder");
    directory.mkdir();

    String[] args = {"-nc", directory.getAbsolutePath()};

    TruffulaOptions options = new TruffulaOptions(args);

    assertFalse(options.isUseColor());
    assertFalse(options.isShowHidden()); // default false
  }

  @Test
  void testInvalidDirectoryThrowsException() {
    String[] args = {"-h", "nonexistent_path_xyz_123"};

    assertThrows(FileNotFoundException.class, () -> {
      new TruffulaOptions(args);
    });
  }

  @Test
  void testUnknownFlagThrowsException(@TempDir File tempDir) throws Exception {
    File directory = new File(tempDir, "folder");
    directory.mkdir();

    String[] args = {"-x", directory.getAbsolutePath()};

    assertThrows(IllegalArgumentException.class, () -> {
      new TruffulaOptions(args);
    });
  }

  @Test
  void testFlagsOrderDoesNotMatter(@TempDir File tempDir) throws Exception {
    File directory = new File(tempDir, "folder");
    directory.mkdir();

    String[] args = {directory.getAbsolutePath(), "-h", "-nc"};
    assertThrows(IllegalArgumentException.class, () -> {
      new TruffulaOptions(args);
    });
  }
}
