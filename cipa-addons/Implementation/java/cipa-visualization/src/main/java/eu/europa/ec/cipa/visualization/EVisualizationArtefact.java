/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.europa.ec.cipa.visualization;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


import com.phloc.commons.GlobalDebug;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.charset.CCharset;
import com.phloc.commons.id.IHasID;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;

import eu.europa.ec.cipa.commons.cenbii.profiles.ETransaction;
import eu.europa.ec.cipa.visualization.index.ArtefactIndex;
import eu.europa.ec.cipa.visualization.index.ArtefactResource;

/**
 * Contains the available visualization artefacts.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public enum EVisualizationArtefact implements IHasID <String> {
  ORDER_IS (ETransaction.T01, "IS", CCharset.CHARSET_ISO_8859_1_OBJ),
  INVOICE_IS (ETransaction.T10, "IS", CCharset.CHARSET_ISO_8859_1_OBJ),
  INVOICE_NO (ETransaction.T10, "NO", CCharset.CHARSET_UTF_8_OBJ),
  CREDITNOTE_IS (ETransaction.T14, "IS", CCharset.CHARSET_ISO_8859_1_OBJ);

  private final ETransaction m_eTransaction;
  private final String m_sBaseDir;
  private final String m_sID;
  private final ArtefactIndex m_aIndex;
  private final Charset m_aCharset;

  private void _validateResources () {
    if (!m_aIndex.getStylesheetResource (m_sBaseDir).exists ())
      throw new IllegalStateException ("Stylesheet file does not exist!");
    for (final ArtefactResource aResource : m_aIndex.getAllResources ())
      if (!aResource.getResource (m_sBaseDir).exists ())
        throw new IllegalStateException ("Resource file '" + aResource.getFilename () + "' does not exist!");
  }

  private EVisualizationArtefact (@Nonnull final ETransaction eTransaction,
                                  @Nonnull @Nonempty final String sLocalID,
                                  @Nonnull final Charset aCharset) {
    m_eTransaction = eTransaction;
    m_sBaseDir = eTransaction.getID () + "/" + sLocalID + "/";
    m_sID = eTransaction.getID () + "-" + sLocalID;
    m_aIndex = ArtefactIndex.createFromXML (new ClassPathResource (m_sBaseDir + "index.xml"));
    m_aCharset = aCharset;
    if (GlobalDebug.isDebugMode ())
      _validateResources ();
  }

  /**
   * @return The transaction of this artefact. Never <code>null</code>.
   */
  @Nonnull
  public ETransaction getTransaction () {
    return m_eTransaction;
  }

  /**
   * @return The charset of the HTML files, this visualization artefact creates.
   */
  @Nonnull
  public Charset getCharset () {
    return m_aCharset;
  }

  @Nonnull
  @Nonempty
  public String getID () {
    return m_sID;
  }

  /**
   * @return The structured index information of this artefact. Never
   *         <code>null</code>.
   */
  @Nonnull
  public ArtefactIndex getIndex () {
    return m_aIndex;
  }

  /**
   * @return The readable resource of the stylesheet XSL. Never
   *         <code>null</code>.
   */
  @Nonnull
  public IReadableResource getStylesheetResource () {
    return m_aIndex.getStylesheetResource (m_sBaseDir);
  }

  /**
   * @return A set of all readable resources that are referenced by the
   *         stylesheet outcome. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <IReadableResource> getAllResources () {
    final Set <IReadableResource> aAll = new HashSet <IReadableResource> ();
    for (final ArtefactResource aRes : m_aIndex.getAllResources ())
      aAll.add (aRes.getResource (m_sBaseDir));
    return aAll;
  }

  /**
   * @return A set of all transactions for which artefacts are present.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static Set <ETransaction> getAllSupportedTransactions () {
    final Set <ETransaction> ret = new HashSet <ETransaction> ();
    for (final EVisualizationArtefact eArtefact : values ())
      ret.add (eArtefact.getTransaction ());
    return ret;
  }

  /**
   * Get all visualization artefacts that match the specified transaction.
   * 
   * @param eTransaction
   *        The transaction to be queried. May be <code>null</code>.
   * @return A non-<code>null</code> set with all matching artefacts.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static Set <EVisualizationArtefact> getAllArtefactsOfTransaction (@Nullable final ETransaction eTransaction) {
    final Set <EVisualizationArtefact> ret = new HashSet <EVisualizationArtefact> ();
    for (final EVisualizationArtefact eArtefact : values ())
      if (eArtefact.getTransaction ().equals (eTransaction))
        ret.add (eArtefact);
    return ret;
  }
}
