/*
 * Copyright (c) 2007-2024, Arshan Dabirsiaghi, Jason Li
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. Neither the name of OWASP nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.owasp.validator.html.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.Test;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;

public class MessageBundleTest {
  @Test
  public void messageFilesAreValidUtf8Properties() throws Exception {
    try (Stream<Path> files = Files.list(Paths.get("src/main/resources"))) {
      Path[] bundles =
          files
              .filter(p -> p.getFileName().toString().matches("AntiSamy.*\\.properties"))
              .toArray(Path[]::new);
      assertTrue("No message bundles found", bundles.length > 0);
      for (Path bundle : bundles) {
        String name = bundle.getFileName().toString();
        Properties source = readProperties(Files.newInputStream(bundle));
        assertEquals(name, source, readBundle(name));
        for (String key : source.stringPropertyNames()) {
          assertFalse(name + " has a blank message key", key.trim().isEmpty());
          assertFalse(name + " starts with a byte order mark", key.startsWith("\uFEFF"));
        }
      }
    }
    assertTrue(
        readBundle("AntiSamy_zh_CN.properties")
            .getProperty("error.tag.removed")
            .contains("\u51fa\u4e8e\u5b89\u5168\u7684\u539f\u56e0"));
    assertTrue(
        readBundle("AntiSamy_de_DE.properties")
            .getProperty("error.tag.removed")
            .contains("Gr\u00fcnden"));
    assertTrue(
        readBundle("AntiSamy_it_IT.properties")
            .getProperty("error.tag.removed")
            .contains("non \u00e8 consentito"));
    assertTrue(
        readBundle("AntiSamy_it_IT.properties")
            .getProperty("error.css.import.exceeded")
            .contains("pi\u00f9 che"));
  }

  @Test
  public void scannersDecodeMessagesIndependentlyOfDefaultCharset() throws Exception {
    String classpath =
        System.getProperty("surefire.test.class.path", System.getProperty("java.class.path"));
    Path java = Paths.get(System.getProperty("java.home"), "bin", "java");
    if (Files.exists(java.resolveSibling("java.exe"))) {
      java = java.resolveSibling("java.exe");
    }
    for (String locale : new String[] {"zh_CN", "de_DE", "it_IT", "fr_FR"}) {
      String[] parts = locale.split("_");
      for (String charset : new String[] {"UTF-8", "windows-1252"}) {
        Path output = Files.createTempFile("antisamy-messages-", ".log");
        Process process = null;
        try {
          process =
              new ProcessBuilder(
                      java.toString(),
                      "-Duser.language=" + parts[0],
                      "-Duser.country=" + parts[1],
                      "-Dfile.encoding=" + charset,
                      "-cp",
                      classpath,
                      ScanProbe.class.getName(),
                      locale.equals("fr_FR") ? "en_US" : locale)
                  .redirectErrorStream(true)
                  .redirectOutput(output.toFile())
                  .start();
          assertTrue(locale + "/" + charset + " timed out", process.waitFor(30, TimeUnit.SECONDS));
          assertEquals(
              locale
                  + "/"
                  + charset
                  + ": "
                  + new String(Files.readAllBytes(output), StandardCharsets.UTF_8),
              0,
              process.exitValue());
        } finally {
          if (process != null && process.isAlive()) process.destroyForcibly().waitFor();
          Files.deleteIfExists(output);
        }
      }
    }
  }

  private static Properties readBundle(String name) throws Exception {
    InputStream input = MessageBundleTest.class.getResourceAsStream("/" + name);
    assertNotNull(name, input);
    return readProperties(input);
  }

  private static Properties readProperties(InputStream input) throws Exception {
    Properties result = new Properties();
    try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8.newDecoder())) {
      // Properties.load also rejects malformed Unicode escapes.
      result.load(reader);
    }
    return result;
  }

  // A fresh JVM is needed because scanners retain the default locale's bundle at class
  // initialization.
  public static class ScanProbe {
    public static void main(String[] args) throws Exception {
      Locale locale = Locale.getDefault();
      Properties expected = readBundle("AntiSamy_" + args[0] + ".properties");
      String message = MessageFormat.format(expected.getProperty("error.tag.removed"), "script");
      // A caller may have loaded this bundle before AntiSamy, including via Java 8's Latin-1
      // loader.
      if (!args[0].equals("en_US")) {
        ResourceBundle.getBundle("AntiSamy", locale);
      }
      Policy policy = Policy.getInstance(MessageBundleTest.class.getResource("/antisamy.xml"));
      for (int scanner : new int[] {AntiSamy.DOM, AntiSamy.SAX}) {
        CleanResults result =
            new AntiSamy().scan("<script>alert(1)</script><p>ok</p>", policy, scanner);
        assertTrue(
            result.getErrorMessages().toString(), result.getErrorMessages().contains(message));
        assertTrue(result.getCleanHTML(), result.getCleanHTML().contains("<p>ok</p>"));
      }
    }
  }
}
