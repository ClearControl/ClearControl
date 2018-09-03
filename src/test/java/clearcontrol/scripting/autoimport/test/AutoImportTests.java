package clearcontrol.scripting.autoimport.test;

import java.io.IOException;

import clearcontrol.scripting.autoimport.AutoImport;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class AutoImportTests
{

  @Test
  public void test() throws IOException
  {
    final String lScriptText = IOUtils.toString(
                                                this.getClass()
                                                    .getResourceAsStream("script.txt"),
                                                "UTF-8");
    final String lGeneratedImportStatements =
                                            AutoImport.generateImportStatements(new String[]
                                            { "clearcontrol" }, lScriptText);

    System.out.println(lGeneratedImportStatements);
  }
}
