package unit;

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
    //Given: path to file that exists
    final String pathToFile = Paths.get(absoluteTestResourcesDirPath, "file").toString();
    //When: function tests if file exists
    boolean result = FileIO.fileExists(pathToFile);
    //Then: it will return true
    Assert.assertTrue("Should be able to detect that a file exists", result);
  }

  @Test
  public void canDetectFileDoesNotExist() {
    //Given: path to file that does not exist
    final String pathToFile = Paths.get(absoluteTestResourcesDirPath, "notThere").toString();
    //When: function tests if file exists
    boolean result = FileIO.fileExists(pathToFile);
    //Then: it will return false
    Assert.assertFalse("Should be able to detect that a file does not exist", result);
  }

  @Test
  public void canReadFromFile() throws IOException {
    //Given: path to file
    final String pathToFile = Paths.get(absoluteTestResourcesDirPath, "file").toString();
    //When: function tries to read from file
    List<String> lines = FileIO.readTestFile(pathToFile);
    //Then: read contents is correct
    Assert.assertTrue("Should be able to read from file",
        lines.size() == 1 && lines.get(0).equals(fileContents));
  }

  @Test
  public void canWriteToFile() throws IOException {
    //Given: path to file and some contents
    final String pathToFile = Paths.get(absoluteTestResourcesDirPath, "output").toString();
    List<String> lines = new LinkedList<>();
    lines.add("test");
    //When: function tries to write to file
    FileIO.writeTestFile(pathToFile, lines);
    //Then: file contents are correct
    List<String> linesTest = Files.readAllLines(Paths.get(pathToFile));
    Assert.assertEquals("Should be able to write to file", lines, linesTest);
    Files.delete(Paths.get(pathToFile));
  }

}
