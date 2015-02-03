package eu.europa.ec.cipa.test;

import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.helger.commons.io.IReadableResource;
import com.helger.commons.locale.country.CountryCache;

import eu.europa.ec.cipa.test.error.TestResource;

public class TestFilesTest {
  @Test
  public void testExist () {
    final Locale [] aLocales = new Locale [] { CountryCache.getInstance ().getCountry ("AT") };
    for (final ETestFileType e : ETestFileType.values ()) {
      for (final IReadableResource r : TestFiles.getSuccessFiles (e))
        assertTrue (r.getPath (), r.exists ());
      for (final Locale aLocale : aLocales)
        for (final IReadableResource r : TestFiles.getSuccessFiles (e, aLocale))
          assertTrue (r.getPath (), r.exists ());
      for (final TestResource r : TestFiles.getErrorFiles (e))
        assertTrue (r.getResource ().getPath (), r.getResource ().exists ());
      for (final Locale aLocale : aLocales)
        for (final TestResource r : TestFiles.getErrorFiles (e, aLocale))
          assertTrue (r.getResource ().getPath (), r.getResource ().exists ());
    }
  }
}
