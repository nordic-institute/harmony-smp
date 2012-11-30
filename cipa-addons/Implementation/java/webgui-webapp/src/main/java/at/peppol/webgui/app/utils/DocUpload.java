package at.peppol.webgui.app.utils;


import java.io.File;
import java.io.FileInputStream;
import com.vaadin.ui.*;

@SuppressWarnings("serial")
public class DocUpload extends Upload implements Upload.SucceededListener,
           												  Upload.FailedListener {

	String uploadDir;
	String filename;
	String mimeType;
	byte[] byteArray;
	
	public DocUpload(String dir) {
		super(null, new ReceiverClass());
		setImmediate(true);
		setButtonCaption("Attach file (optional)");
		uploadDir = dir;
		if (!uploadDir.endsWith("/"))
			uploadDir = uploadDir + "/";
		
		((ReceiverClass)getReceiver()).setUploadDir(uploadDir);
		
		addListener((Upload.SucceededListener) this);
		addListener((Upload.FailedListener) this);
		
	}
	
	public void uploadSucceeded(Upload.SucceededEvent event) {
		File file = new File(uploadDir+event.getFilename());
		this.filename = event.getFilename();
		//this.mimeType = event.getMIMEType();
		this.mimeType = ((ReceiverClass)this.getReceiver()).getMimeType();
		getByteArrayFromFile(file);
		
	}
	
	public void uploadFailed(Upload.FailedEvent event) {}
	
	public void getByteArrayFromFile(File file) {
		byteArray = new byte[(int) file.length()];
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(byteArray);
			fileInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	public byte[] getByteArray() {
		return byteArray;
	}

}