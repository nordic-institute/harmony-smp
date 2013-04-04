package eu.europa.ec.cipa.webgui.app.login;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import com.phloc.appbasics.security.user.IUser;
import com.phloc.ubl.AbstractUBLDocumentMarshaller;
import com.phloc.ubl.UBL20DocumentMarshaller;

import eu.europa.ec.cipa.peppol.utils.ConfigFile;
import eu.europa.ec.cipa.webgui.app.InvoiceBean;
import eu.europa.ec.cipa.webgui.app.validator.ValidatorHandler;

public class UserDirManager extends UserFolderManager <File> {
  UserFolder <File> userDir;
  String fileSep = System.getProperty ("file.separator");
  String mainDirString = "users";
  String rootFolder = "";
  FilenameFilter filter = new FilenameFilter () {
    public boolean accept (final File directory, final String fileName) {
      return fileName.endsWith (".payload");
    }
  };

  public UserDirManager (final IUser user, final String context) {
    super (user, context);
    readPropertiesFile ();
  }

  public void readPropertiesFile () {
    final ConfigFile s_aConf = new ConfigFile ("private-guiconfig.properties", "guiconfig.properties");
    rootFolder = s_aConf.getString ("rootFolder");
    System.out.println ("root folder is: " + rootFolder);
    /*
     * Properties prop = new Properties(); try { //load a properties file
     * prop.load(new FileInputStream("config.prop")); rootFolder =
     * prop.getProperty("rootFolder");
     * System.out.println("root folder is: "+rootFolder); } catch (IOException
     * e) { e.printStackTrace(); }
     */
  }

  public void createUserDir () throws Exception {
    // userDir.setFolder(new File(mainDirString + fileSep +
    // user.getEmailAddress()));
    userDir = new UserFolder <File> (new File (rootFolder + fileSep + mainDirString + fileSep + user.getEmailAddress ()),
                                     "Root");
    if (!userDir.getFolder ().exists ()) {
      if (!userDir.getFolder ().mkdirs ()) {
        // userDir = null;
        throw new Exception ("Could not create user directory '" +
                             userDir.getFolder ().getAbsolutePath () +
                             "'. Check privildges.");
      }
    }
  }

  private void create (final File dir) throws Exception {
    if (!dir.exists ()) {
      if (!dir.mkdirs ()) {
        // dir = null;
        throw new Exception ("Could not create directory '" + dir.getAbsolutePath () + "'. Check privildges.");
      }
    }
  }

  @Override
  public void createUserFolders () {
    try {
      createUserDir ();
      final String userDirPath = userDir.getFolder ().getPath ();
      // inbox.setFolder(new File(userDirPath+fileSep+context+fileSep+"inbox"));
      inbox = new UserFolder <File> (new File (userDirPath + fileSep + context + fileSep + "inbox"), "INBOX");
      outbox = new UserFolder <File> (new File (userDirPath + fileSep + context + fileSep + "outbox"), "OUTBOX");
      drafts = new UserFolder <File> (new File (userDirPath + fileSep + context + fileSep + "drafts"), "DRAFTS");

      create (inbox.getFolder ());
      create (outbox.getFolder ());
      create (drafts.getFolder ());
    }
    catch (final Exception e) {
      e.printStackTrace ();
    }
  }

  @Override
  public UserFolder <File> getUserRootFolder () {
    return userDir;
  }

  @Override
  public void storeDocumentToUserFolder (final InvoiceType doc, final UserFolder <File> space) {

  }

  @Override
  public InvoiceType getDocumentFromUserFolder (final String docID, final UserFolder <File> space) {
    return null;
  }

  public List <String> getDocumentsListFromUserSpace (final File box) {
    final FilenameFilter filter = new FilenameFilter () {
      public boolean accept (final File directory, final String fileName) {
        return fileName.endsWith (".xml");
      }
    };

    if (box != null)
      return new ArrayList <String> (Arrays.asList (box.list (filter)));

    return null;
  }

  @Override
  public UserFolder <File> getInbox () {
    return inbox;
  }

  @Override
  public UserFolder <File> getOutbox () {
    return outbox;
  }

  @Override
  public UserFolder <File> getDrafts () {
    return drafts;
  }

  @Override
  public int countItemsInSpace (final UserFolder <File> box) {
    int ret = 0;

    if (box != null)
      ret = box.getFolder ().list (filter).length;

    return ret;
  }

  @Override
  public List <InvoiceBean> getInvoicesFromUserFolder (final UserFolder <File> folder) {
    final List <InvoiceBean> list = new ArrayList <InvoiceBean> ();
    if (folder != null) {
      final String [] filenames = folder.getFolder ().list (filter);
      final ValidatorHandler vh = new ValidatorHandler ();
      AbstractUBLDocumentMarshaller.setGlobalValidationEventHandler (vh);
      for (final String filename : filenames) {
        try {
          vh.clearErrors ();
          final String fullPath = folder.getFolder ().getAbsolutePath () + fileSep + filename;
          final InvoiceType inv = UBL20DocumentMarshaller.readInvoice (new StreamSource (new FileInputStream (new File (fullPath))),
                                                                       vh);
          if (inv != null) {
            final InvoiceBean bean = new InvoiceBean (inv);
            bean.setFolderEntryID (fullPath);
            list.add (bean);
          }
        }
        catch (final IOException e) {
          e.printStackTrace ();
        }
        catch (final Exception e) {
          e.printStackTrace ();
        }
      }
      return list;
    }

    return null;
  }

  @Override
  public void moveInvoice (final InvoiceBean inv, final UserFolder <File> source, final UserFolder <File> dest) {
    final File sourceFile = new File (source.getFolder ().getAbsolutePath () + fileSep + inv.getFolderEntryID ());
    final File destFile = new File (dest.getFolder ().getAbsolutePath () + fileSep + inv.getFolderEntryID ());
    try {
      final FileInputStream inStream = new FileInputStream (sourceFile);
      final FileOutputStream outStream = new FileOutputStream (destFile);

      final byte [] buffer = new byte [1024];
      int length;
      while ((length = inStream.read (buffer)) > 0) {
        outStream.write (buffer, 0, length);
      }
      inStream.close ();
      outStream.close ();
      sourceFile.delete ();
    }
    catch (final FileNotFoundException e) {
      e.printStackTrace ();
    }
    catch (final IOException e) {
      e.printStackTrace ();
    }

  }
}
