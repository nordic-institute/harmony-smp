package at.peppol.webgui.app.login;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.stream.StreamSource;

import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

import at.peppol.commons.utils.ConfigFile;
import at.peppol.webgui.app.InvoiceBean;
import at.peppol.webgui.app.validator.ValidatorHandler;

import com.phloc.appbasics.security.user.IUser;
import com.phloc.ubl.AbstractUBLDocumentMarshaller;
import com.phloc.ubl.UBL20DocumentMarshaller;

public class UserDirManager extends UserFolderManager<File> {
	UserFolder<File> userDir;
	String fileSep = System.getProperty("file.separator");
	String mainDirString = "users";
	String rootFolder = "";
	FilenameFilter filter = new FilenameFilter() {
		public boolean accept(File directory, String fileName) {
		    return fileName.endsWith(".payload");
		}
	};
	
	public UserDirManager(IUser user, String context) {
		super(user, context);
		readPropertiesFile();
	}
	
	public void readPropertiesFile() {
		ConfigFile s_aConf = new ConfigFile ("private-guiconfig.properties", "guiconfig.properties");
		rootFolder = s_aConf.getString ("rootFolder");
		System.out.println("root folder is: "+rootFolder);
		/*Properties prop = new Properties();
		try {
			//load a properties file
			prop.load(new FileInputStream("config.prop"));
			rootFolder = prop.getProperty("rootFolder");
			System.out.println("root folder is: "+rootFolder);
    	} catch (IOException e) {
    		e.printStackTrace();
        }*/
	}
	
	public void createUserDir() throws Exception {
		//userDir.setFolder(new File(mainDirString + fileSep + user.getEmailAddress()));
		userDir = new UserFolder<File>(new File(rootFolder+fileSep+mainDirString + fileSep + user.getEmailAddress()), "Root");
		if (!userDir.getFolder().exists()) {
			if (!userDir.getFolder().mkdirs()) {
				//userDir = null;
				throw new Exception("Could not create user directory '"+userDir.getFolder().getAbsolutePath()+"'. Check privildges.");
			}
		}
	}
	
	private void create(File dir) throws Exception {
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				//dir = null;
				throw new Exception("Could not create directory '"+dir.getAbsolutePath()+"'. Check privildges.");
			}
		}
	}
	
	@Override
	public void createUserFolders() {
		try {
			createUserDir();
			String userDirPath = userDir.getFolder().getPath();
			//inbox.setFolder(new File(userDirPath+fileSep+context+fileSep+"inbox"));
			inbox = new UserFolder<File>(new File(userDirPath+fileSep+context+fileSep+"inbox"), "INBOX");
			outbox = new UserFolder<File>(new File(userDirPath+fileSep+context+fileSep+"outbox"), "OUTBOX");
			drafts = new UserFolder<File>(new File(userDirPath+fileSep+context+fileSep+"drafts"), "DRAFTS");
	
			create(inbox.getFolder());
			create(outbox.getFolder());
			create(drafts.getFolder());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public UserFolder<File> getUserRootFolder() {
		return userDir;
	}
	
	@Override
	public void storeDocumentToUserFolder(InvoiceType doc, UserFolder<File> space) {
		
	}
	
	@Override
	public InvoiceType getDocumentFromUserFolder(String docID, UserFolder<File> space) {
		return null;
	}
	
	public List<String> getDocumentsListFromUserSpace(File box) {
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File directory, String fileName) {
			    return fileName.endsWith(".xml");
			}
		};
		
		if (box != null)
			return new ArrayList<String>(Arrays.asList(box.list(filter))); 
		
		return null;
	}

	@Override
	public UserFolder<File> getInbox() {
		return inbox;
	}

	@Override
	public UserFolder<File> getOutbox() {
		return outbox;
	}

	@Override
	public UserFolder<File> getDrafts() {
		return drafts;
	}
	
	@Override
	public int countItemsInSpace(UserFolder<File> box) {
		int ret = 0;
		
		if (box != null)
			ret = box.getFolder().list(filter).length;
		
		return ret;
	}

	@Override
	public List<InvoiceBean> getInvoicesFromUserFolder(UserFolder<File> folder) {
		List<InvoiceBean> list = new ArrayList<InvoiceBean>();
		if (folder != null) {
			String[] filenames = folder.getFolder().list(filter);
			ValidatorHandler vh = new ValidatorHandler();
	        AbstractUBLDocumentMarshaller.setGlobalValidationEventHandler (vh);
	        for (int i=0;i<filenames.length;i++) {
				try {
					vh.clearErrors();
					String fullPath = folder.getFolder().getAbsolutePath()+fileSep+filenames[i];
					InvoiceType inv = UBL20DocumentMarshaller.readInvoice(new StreamSource(
								new FileInputStream(new File(fullPath))), vh);
					if (inv != null) {
						InvoiceBean bean = new InvoiceBean(inv);
						bean.setFolderEntryID(fullPath);
						list.add(bean);
					}
				}catch (IOException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			return list;
		}
		
		return null;
	}

	@Override
	public void moveInvoice(InvoiceBean inv, UserFolder<File> source, UserFolder<File> dest) {
		File sourceFile = new File(source.getFolder().getAbsolutePath()+fileSep+inv.getFolderEntryID());
		File destFile = new File(dest.getFolder().getAbsolutePath()+fileSep+inv.getFolderEntryID());
		try {
			FileInputStream inStream = new FileInputStream(sourceFile);
			FileOutputStream  outStream = new FileOutputStream(destFile);
	
		    byte[] buffer = new byte[1024];
		    int length;
		    while ((length = inStream.read(buffer)) > 0){
		    	outStream.write(buffer, 0, length);
		    }
		    inStream.close();
		    outStream.close();
		    sourceFile.delete();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
