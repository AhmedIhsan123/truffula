import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TruffulaPrinterTest {

    /**
     * Checks if the current operating system is Windows.
     *
     * This method reads the "os.name" system property and checks whether it
     * contains the substring "win", which indicates a Windows-based OS.
     *
     * You do not need to modify this method.
     *
     * @return true if the OS is Windows, false otherwise
     */
    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }

    /**
     * Creates a hidden file in the specified parent folder.
     *
     * The filename MUST start with a dot (.).
     *
     * On Unix-like systems, files prefixed with a dot (.) are treated as hidden.
     * On Windows, this method also sets the DOS "hidden" file attribute.
     *
     * You do not need to modify this method, but you SHOULD use it when creating hidden files
     * for your tests. This will make sure that your tests work on both Windows and UNIX-like systems.
     *
     * @param parentFolder the directory in which to create the hidden file
     * @param filename the name of the hidden file; must start with a dot (.)
     * @return a File object representing the created hidden file
     * @throws IOException if an I/O error occurs during file creation or attribute setting
     * @throws IllegalArgumentException if the filename does not start with a dot (.)
     */
    private static File createHiddenFile(File parentFolder, String filename) throws IOException {
        if (!filename.startsWith(".")) {
            throw new IllegalArgumentException("Hidden files/folders must start with a '.'");
        }
        File hidden = new File(parentFolder, filename);
        hidden.createNewFile();
        if (isWindows()) {
            Path path = Paths.get(hidden.toURI());
            Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
        }
        return hidden;
    }

    private static String runPrintTree(File root, boolean showHidden) {
        TruffulaOptions options = new TruffulaOptions(root, showHidden, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        TruffulaPrinter printer = new TruffulaPrinter(options, ps);
        printer.printTree();
        return baos.toString();
    }

    @Test
    void testPrintTree_PrintsRootAndVisibleFiles(@TempDir File tempDir) throws Exception {
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());

        new File(root, "a.txt").createNewFile();
        new File(root, "b.txt").createNewFile();

        String output = runPrintTree(root, false);

        assertTrue(output.contains("root/"));
        assertTrue(output.contains("   a.txt"));
        assertTrue(output.contains("   b.txt"));
    }

    @Test
    void testPrintTree_PrintsNestedDirectoriesAndFiles(@TempDir File tempDir) throws Exception {
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());

        File sub = new File(root, "sub");
        assertTrue(sub.mkdir());

        File inner = new File(sub, "inner");
        assertTrue(inner.mkdir());

        new File(inner, "file.txt").createNewFile();

        String output = runPrintTree(root, false);

        assertTrue(output.contains("root/"));
        assertTrue(output.contains("   sub/"));
        assertTrue(output.contains("      inner/"));
        assertTrue(output.contains("         file.txt"));
    }

    @Test
    void testPrintTree_EmptyDirectory(@TempDir File tempDir) {
        File root = new File(tempDir, "empty");
        assertTrue(root.mkdir());

        String output = runPrintTree(root, false);

        assertTrue(output.contains("empty/"));
    }

    @Test
    void testPrintTree_HiddenFileExcluded_WhenShowHiddenFalse(@TempDir File tempDir) throws Exception {
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());

        new File(root, "visible.txt").createNewFile();
        createHiddenFile(root, ".secret.txt");

        String output = runPrintTree(root, false);

        assertTrue(output.contains("root/"));
        assertTrue(output.contains("   visible.txt"));
        assertFalse(output.contains(".secret.txt"));
    }

    @Test
    void testPrintTree_HiddenFileIncluded_WhenShowHiddenTrue(@TempDir File tempDir) throws Exception {
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());

        new File(root, "visible.txt").createNewFile();
        createHiddenFile(root, ".secret.txt");

        String output = runPrintTree(root, true);

        assertTrue(output.contains("root/"));
        assertTrue(output.contains("   visible.txt"));
        assertTrue(output.contains("   .secret.txt"));
    }

    @Test
    void testPrintTree_HiddenFileExcluded_InNestedDirectory_WhenShowHiddenFalse(@TempDir File tempDir) throws Exception {
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());

        File docs = new File(root, "docs");
        assertTrue(docs.mkdir());

        new File(docs, "notes.txt").createNewFile();
        createHiddenFile(docs, ".draft.txt");

        String output = runPrintTree(root, false);

        assertTrue(output.contains("root/"));
        assertTrue(output.contains("   docs/"));
        assertTrue(output.contains("      notes.txt"));
        assertFalse(output.contains(".draft.txt"));
    }

    @Test
    void testPrintTree_HiddenDirectoryShouldBeExcluded_WhenShowHiddenFalse(@TempDir File tempDir) throws Exception {
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());

        File hiddenDir = new File(root, ".hiddenFolder");
        assertTrue(hiddenDir.mkdir());

        if (isWindows()) {
            Path path = Paths.get(hiddenDir.toURI());
            Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
        }

        new File(hiddenDir, "inside.txt").createNewFile();

        String output = runPrintTree(root, false);

        // This test describes the correct behavior:
        // when showHidden is false, hidden directories should not appear,
        // and their contents should not appear either.
        assertFalse(output.contains(".hiddenFolder/"));
        assertFalse(output.contains("inside.txt"));
    }

    @Test
    void testPrintTree_HiddenDirectoryIncluded_WhenShowHiddenTrue(@TempDir File tempDir) throws Exception {
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());

        File hiddenDir = new File(root, ".hiddenFolder");
        assertTrue(hiddenDir.mkdir());

        if (isWindows()) {
            Path path = Paths.get(hiddenDir.toURI());
            Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
        }

        new File(hiddenDir, "inside.txt").createNewFile();

        String output = runPrintTree(root, true);

        assertTrue(output.contains("root/"));
        assertTrue(output.contains("   .hiddenFolder/"));
        assertTrue(output.contains("      inside.txt"));
    }

    @Test
    void testPrintTree_ChildrenOfRoot_UseSameFirstLevelColor(@TempDir File tempDir) throws Exception {
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());

        new File(root, "a.txt").createNewFile();
        new File(root, "b.txt").createNewFile();

        String output = runPrintTree(root, false);

        String expected =
                ConsoleColor.WHITE + "root/\n" + ConsoleColor.RESET +
                ConsoleColor.WHITE + "   a.txt\n" + ConsoleColor.RESET +
                ConsoleColor.WHITE + "   b.txt\n" + ConsoleColor.RESET;

        assertEquals(expected, output);
    }

    @Test
    void testPrintTree_SecondLevelEntries_UseSecondColor(@TempDir File tempDir) throws Exception {
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());

        File docs = new File(root, "docs");
        assertTrue(docs.mkdir());

        new File(docs, "a.txt").createNewFile();
        new File(docs, "b.txt").createNewFile();

        String output = runPrintTree(root, false);

        String expected =
                ConsoleColor.WHITE + "root/\n" + ConsoleColor.RESET +
                ConsoleColor.WHITE + "   docs/\n" + ConsoleColor.RESET +
                ConsoleColor.PURPLE + "      a.txt\n" + ConsoleColor.RESET +
                ConsoleColor.PURPLE + "      b.txt\n" + ConsoleColor.RESET;

        assertEquals(expected, output);
    }

    @Test
    void testPrintTree_ThirdLevelEntries_UseThirdColor(@TempDir File tempDir) throws Exception {
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());

        File docs = new File(root, "docs");
        assertTrue(docs.mkdir());

        File images = new File(docs, "images");
        assertTrue(images.mkdir());

        new File(images, "Cat.png").createNewFile();
        new File(images, "Dog.png").createNewFile();

        String output = runPrintTree(root, false);

        String expected =
                ConsoleColor.WHITE + "root/\n" + ConsoleColor.RESET +
                ConsoleColor.WHITE + "   docs/\n" + ConsoleColor.RESET +
                ConsoleColor.PURPLE + "      images/\n" + ConsoleColor.RESET +
                ConsoleColor.YELLOW + "         Cat.png\n" + ConsoleColor.RESET +
                ConsoleColor.YELLOW + "         Dog.png\n" + ConsoleColor.RESET;

        assertEquals(expected, output);
    }

    @Test
    void testPrintTree_ColorSequenceWrapsByDirectoryLevel(@TempDir File tempDir) throws Exception {
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());

        File level1 = new File(root, "level1");
        assertTrue(level1.mkdir());

        File level2 = new File(level1, "level2");
        assertTrue(level2.mkdir());

        File level3 = new File(level2, "level3");
        assertTrue(level3.mkdir());

        new File(level3, "deep.txt").createNewFile();

        String output = runPrintTree(root, false);

        String expected =
                ConsoleColor.WHITE + "root/\n" + ConsoleColor.RESET +
                ConsoleColor.WHITE + "   level1/\n" + ConsoleColor.RESET +
                ConsoleColor.PURPLE + "      level2/\n" + ConsoleColor.RESET +
                ConsoleColor.YELLOW + "         level3/\n" + ConsoleColor.RESET +
                ConsoleColor.WHITE + "            deep.txt\n" + ConsoleColor.RESET;

        assertEquals(expected, output);
    }

    @Test
    void testPrintTree_SiblingsAtSameDepth_HaveSameColor(@TempDir File tempDir) throws Exception {
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());

        File alpha = new File(root, "alpha");
        File beta = new File(root, "beta");
        assertTrue(alpha.mkdir());
        assertTrue(beta.mkdir());

        new File(alpha, "a.txt").createNewFile();
        new File(beta, "b.txt").createNewFile();

        String output = runPrintTree(root, false);

        String expected =
                ConsoleColor.WHITE + "root/\n" + ConsoleColor.RESET +
                ConsoleColor.WHITE + "   alpha/\n" + ConsoleColor.RESET +
                ConsoleColor.PURPLE + "      a.txt\n" + ConsoleColor.RESET +
                ConsoleColor.WHITE + "   beta/\n" + ConsoleColor.RESET +
                ConsoleColor.PURPLE + "      b.txt\n" + ConsoleColor.RESET;

        assertEquals(expected, output);
    }

    @Test
    void testPrintTree_WhenColorDisabled_AllOutputUsesDefaultPrinterBehavior(@TempDir File tempDir) throws Exception {
        File root = new File(tempDir, "root");
        assertTrue(root.mkdir());

        File docs = new File(root, "docs");
        assertTrue(docs.mkdir());
        new File(docs, "notes.txt").createNewFile();

        TruffulaOptions options = new TruffulaOptions(root, false, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        TruffulaPrinter printer = new TruffulaPrinter(options, ps);

        printer.printTree();

        String output = baos.toString();

        String expected =
                ConsoleColor.WHITE + "root/\n" + ConsoleColor.RESET +
                ConsoleColor.RESET + "   docs/\n" + ConsoleColor.RESET +
                ConsoleColor.RESET + "      notes.txt\n" + ConsoleColor.RESET;

        assertEquals(expected, output);
    }
}