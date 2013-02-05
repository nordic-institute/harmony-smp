package at.peppol.webgui.app.login;

public class UserFolder<T> {
	
	T folder;
	String name;

	public UserFolder() {
		folder = null;
		name = null;
	};
	
	public UserFolder(T folder, String name) {
		this.folder = folder;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getFolder() {
		return folder;
	}

	public void setFolder(T folder) {
		this.folder = folder;
	}
}
