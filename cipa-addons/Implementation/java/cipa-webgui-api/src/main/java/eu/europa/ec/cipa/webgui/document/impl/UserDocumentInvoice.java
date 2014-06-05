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
package eu.europa.ec.cipa.webgui.document.impl;

import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.annotation.WillClose;
import javax.xml.transform.stream.StreamResult;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.phloc.commons.ValueEnforcer;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.commons.io.streams.StreamUtils;
import com.phloc.commons.state.ESuccess;
import com.phloc.ubl.UBL20Writer;

import eu.europa.ec.cipa.webgui.document.AbstractUserDocument;
import eu.europa.ec.cipa.webgui.document.EDocumentType;

/**
 * Represents a single invoice document
 * 
 * @author philip
 */
public final class UserDocumentInvoice extends AbstractUserDocument {
  private final InvoiceType m_aInvoice;

  public UserDocumentInvoice (@Nonnull final InvoiceType aInvoice) {
    this (GlobalIDFactory.getNewPersistentStringID (), aInvoice);
  }

  public UserDocumentInvoice (@Nonnull @Nonempty final String sID, @Nonnull final InvoiceType aInvoice) {
    super (sID, EDocumentType.INVOICE);
    m_aInvoice = ValueEnforcer.notNull (aInvoice, "Invoice");
  }

  @Nonnull
  public InvoiceType getInvoice () {
    return m_aInvoice;
  }

  @Nonnull
  public ESuccess writeToStream (@Nonnull @WillClose final OutputStream aOS) {
    try {
      return UBL20Writer.writeInvoice (m_aInvoice, new StreamResult (aOS));
    }
    finally {
      StreamUtils.close (aOS);
    }
  }
}
