package unit.tradeEngine.utils;

import com.gala.sam.tradeEngine.utils.FileIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class FileOperationTests {

    private static final String relativeTestResourcesDirPath = "src/test/resources";
    private static final String absoluteTestResourcesDirPath = Paths
        .get(System.getProperty("user.dir"), relativeTestResourcesDirPath).toString();

    private static final String fileContents = "example";

@Test
public void canDetectFileExists() {
    final String pathToFile = Paths.get(absoluteTestResourcesDirPath, "file").toString();
    Assert.assertTrue("Should be able to detect that a file exists", FileIO.fileExists(pathToFile));
}

@Test
public void canDetectFileDoesNotExist() {
    final String pathToFile = Paths.get(absoluteTestResourcesDirPath, "notThere").toString();
    Assert.assertTrue("Should be able to detect that a file does not exist", !FileIO.fileExists(pathToFile));
}

@Test
public void canReadFromFile() throws IOException {
    final String pathToFile = Paths.get(absoluteTestResourcesDirPath, "file").toString();
    List<String> lines = FileIO.readTestFile(pathToFile);
    Assert.assertTrue("Should be able to read from file", lines.size() == 1 && lines.get(0).equals(fileContents));
}

@Test
public void canWriteToFile() throws IOException {
    final String pathToFile = Paths.get(absoluteTestResourcesDirPath, "output").toString();
    List<String> lines = new LinkedList<>();
    lines.add("test");
    FileIO.writeTestFile(pathToFile, lines);
    List<String> linesTest = Files.readAllLines(Paths.get(pathToFile));
    Assert.assertEquals("Should be able to write to file", lines, linesTest);
    Files.delete(Paths.get(pathToFile));
}

}
