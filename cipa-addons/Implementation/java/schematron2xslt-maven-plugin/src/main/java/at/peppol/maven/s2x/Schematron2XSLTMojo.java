/**
 * Copyright (C) 2010 Bundesrechenzentrum GmbH
 * http://www.brz.gv.at
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.peppol.maven.s2x;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

import javax.xml.transform.ErrorListener;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

import at.peppol.validation.schematron.xslt.ISchematronXSLTProvider;
import at.peppol.validation.schematron.xslt.SchematronResourceSCHCache;

import com.phloc.commons.error.IResourceError;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.file.FilenameHelper;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.serialize.XMLWriterSettings;
import com.phloc.commons.xml.transform.AbstractTransformErrorListener;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Converts one or more Schematron schema files into XSLT scripts.
 * 
 * @goal convert
 * @phase generate-resources
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class Schematron2XSLTMojo extends AbstractMojo {
  /**
   * The Maven Project.
   * 
   * @parameter property="project"
   * @required
   * @readonly
   */
  @SuppressFBWarnings ({ "NP_UNWRITTEN_FIELD", "UWF_UNWRITTEN_FIELD" })
  private MavenProject project;

  /**
   * The directory where the Schematron files reside.
   * 
   * @parameter property="schematronDirectory"
   *            default="${basedir}/src/main/schematron"
   */
  private File m_aSchematronDirectory;

  /**
   * A pattern for the Schematron files. Can contain Ant-style wildcards and
   * double wildcards. All files that match the pattern will be converted. Files
   * in the schematronDirectory and its subdirectories will be considered.
   * 
   * @parameter property="schematronPattern" default-value="**\/*.sch"
   */
  private String m_sSchematronPattern;

  /**
   * The directory where the XSLT files will be saved.
   * 
   * @required
   * @parameter property="xsltDirectory" default="${basedir}/src/main/xslt"
   */
  private File m_aXSLTDirectory;

  /**
   * The file extension of the XSLT files.
   * 
   * @parameter property="xsltExtension" default-value=".xslt"
   */
  private String m_sXSLTExtension;

  /**
   * Overwrite existing Schematron files without notice?
   * 
   * @parameter property="overwrite" default-value="true"
   */
  private boolean m_bOverwriteWithoutQuestion = true;

  public void setSchematronDirectory (final File aDir) {
    m_aSchematronDirectory = aDir;
    if (!m_aSchematronDirectory.isAbsolute ())
      m_aSchematronDirectory = new File (project.getBasedir (), aDir.getPath ());
    getLog ().debug ("Searching Schematron files in the directory '" + m_aSchematronDirectory + "'");
  }

  public void setSchematronPattern (final String sPattern) {
    m_sSchematronPattern = sPattern;
    getLog ().debug ("Setting Schematron pattern to '" + sPattern + "'");
  }

  public void setXsltDirectory (final File aDir) {
    m_aXSLTDirectory = aDir;
    if (!m_aXSLTDirectory.isAbsolute ())
      m_aXSLTDirectory = new File (project.getBasedir (), aDir.getPath ());
    getLog ().debug ("Writing XSLT files into directory '" + m_aXSLTDirectory + "'");
  }

  public void setXsltExtension (final String sExt) {
    m_sXSLTExtension = sExt;
    getLog ().debug ("Setting XSLT file extension to '" + sExt + "'");
  }

  public void setOverwrite (final boolean bOverwrite) {
    m_bOverwriteWithoutQuestion = bOverwrite;
    if (m_bOverwriteWithoutQuestion)
      getLog ().debug ("Overwriting XSLT files without notice");
    else
      getLog ().debug ("Ignoring existing Schematron files");
  }

  public void execute () throws MojoExecutionException, MojoFailureException {
    if (m_aSchematronDirectory == null)
      throw new MojoExecutionException ("No Schematron directory specified!");
    if (m_aSchematronDirectory.exists () && !m_aSchematronDirectory.isDirectory ())
      throw new MojoExecutionException ("The specified Schematron directory " +
                                        m_aSchematronDirectory +
                                        " is not a directory!");
    if (m_sSchematronPattern == null || m_sSchematronPattern.isEmpty ()) {
      throw new MojoExecutionException ("No Schematron pattern specified!");
    }
    if (m_aXSLTDirectory == null)
      throw new MojoExecutionException ("No XSLT directory specified!");
    if (m_aXSLTDirectory.exists () && !m_aXSLTDirectory.isDirectory ())
      throw new MojoExecutionException ("The specified XSLT directory " + m_aXSLTDirectory + " is not a directory!");
    if (m_sXSLTExtension == null || m_sXSLTExtension.length () == 0 || !m_sXSLTExtension.startsWith ("."))
      throw new MojoExecutionException ("The XSLT extension '" + m_sXSLTExtension + "' is invalid!");

    if (!m_aXSLTDirectory.exists () && !m_aXSLTDirectory.mkdirs ())
      throw new MojoExecutionException ("Failed to create the XSLT directory " + m_aXSLTDirectory);

    // for all Schematron files that match the pattern
    final DirectoryScanner aScanner = new DirectoryScanner ();
    aScanner.setBasedir (m_aSchematronDirectory);
    aScanner.setIncludes (new String [] { m_sSchematronPattern });
    aScanner.setCaseSensitive (true);
    aScanner.scan ();
    final String [] aFilenames = aScanner.getIncludedFiles ();
    if (aFilenames != null) {
      for (final String sFilename : aFilenames) {
        final File aFile = new File (m_aSchematronDirectory, sFilename);

        // 1. build XSLT file name (outputdir + localpath with new extension)
        final File aXSLTFile = new File (m_aXSLTDirectory, FilenameHelper.getWithoutExtension (sFilename) +
                                                           m_sXSLTExtension);

        getLog ().info ("Converting Schematron file '" +
                        aFile.getPath () +
                        "' to XSLT file '" +
                        aXSLTFile.getPath () +
                        "'");

        // 2. The Schematron resource
        final IReadableResource aSchematronResource = new FileSystemResource (aFile);

        // 3. Check if the XSLT file already exists
        if (aXSLTFile.exists () && !m_bOverwriteWithoutQuestion) {
          // 3.1 Not overwriting the existing file
          getLog ().debug ("Skipping XSLT file '" + aXSLTFile.getPath () + "' because it already exists!");
        }
        else {
          // 3.2 Create the directory, if necessary
          final File aXsltFileDirectory = aXSLTFile.getParentFile ();
          if (aXsltFileDirectory != null && !aXsltFileDirectory.exists ()) {
            getLog ().debug ("Creating directory '" + aXsltFileDirectory.getPath () + "'");
            if (!aXsltFileDirectory.mkdirs ()) {
              final String message = "Failed to convert '" +
                                     aFile.getPath () +
                                     "' because directory '" +
                                     aXsltFileDirectory.getPath () +
                                     "' could not be created";
              getLog ().error (message);
              throw new MojoFailureException (message);
            }
          }
          // 3.3 Okay, write the XSLT file
          try {
            // Custom error listener to log to the Mojo logger
            final ErrorListener aMojoErrorListener = new AbstractTransformErrorListener (null) {
              @Override
              protected void internalLog (final IResourceError aResError) {
                if (aResError.isError ())
                  getLog ().error (aResError.getAsString (Locale.US), aResError.getLinkedException ());
                else
                  getLog ().warn (aResError.getAsString (Locale.US), aResError.getLinkedException ());
              }
            };

            // Custom error listener but no custom URI resolver
            final ISchematronXSLTProvider aXsltProvider = SchematronResourceSCHCache.createSchematronXSLTProvider (aSchematronResource,
                                                                                                                   aMojoErrorListener,
                                                                                                                   null);
            if (aXsltProvider != null) {
              // Write the resulting XSLT file to disk
              XMLWriter.writeToStream (aXsltProvider.getXSLTDocument (),
                                       new FileOutputStream (aXSLTFile),
                                       XMLWriterSettings.DEFAULT_XML_SETTINGS);
            }
            else {
              final String message = "Failed to convert '" + aFile.getPath () + "': the Schematron resource is invalid";
              getLog ().error (message);
              throw new MojoFailureException (message);
            }
          }
          catch (final MojoFailureException up) {
            throw up;
          }
          catch (final Exception ex) {
            final String message = "Failed to convert '" +
                                   aFile.getPath () +
                                   "' to XSLT file '" +
                                   aXSLTFile.getPath () +
                                   "'";
            getLog ().error (message, ex);
            throw new MojoFailureException (message, ex);
          }
        }
      }
    }
  }
}
