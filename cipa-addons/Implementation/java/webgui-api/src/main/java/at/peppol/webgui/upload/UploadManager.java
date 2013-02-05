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
package at.peppol.webgui.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;

import com.phloc.appbasics.app.io.WebFileIO;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.scopes.web.singleton.GlobalWebSingleton;

/**
 * Handle uploaded resources and clean them up when the application is shut
 * down.
 * 
 * @author philip
 */
public final class UploadManager extends GlobalWebSingleton {
  private final Lock m_aLock = new ReentrantLock ();
  private final List <UploadedResource> m_aUploads = new ArrayList <UploadedResource> ();

  @Deprecated
  @UsedViaReflection
  public UploadManager () {}

  @Nonnull
  public static UploadManager getInstance () {
    return getGlobalSingleton (UploadManager.class);
  }

  @Nonnull
  public UploadedResource createManagedResource (@Nonnull @Nonempty final String sOriginalFilename) {
    m_aLock.lock ();
    try {
      final UploadedResource aRes = new UploadedResource (sOriginalFilename);
      m_aUploads.add (aRes);
      return aRes;
    }
    finally {
      m_aLock.unlock ();
    }
  }

  @Override
  protected void onDestroy () {
    m_aLock.lock ();
    try {
      // Delete all created temporary files
      for (final IUploadedResource aRes : m_aUploads) {
        final File aTempFile = aRes.getTemporaryFile ();
        if (aTempFile.exists ())
          WebFileIO.getFileOpMgr ().deleteFile (aTempFile);
      }
      m_aUploads.clear ();
    }
    finally {
      m_aLock.unlock ();
    }
  }
}
