package eu.europa.ec.cipa.webgui.app.login;

public class UserFolder <T> {

  T folder;
  String name;

  public UserFolder () {
    folder = null;
    name = null;
  }

  public UserFolder (final T folder, final String name) {
    this.folder = folder;
    this.name = name;
  }

  public String getName () {
    return name;
  }

  public void setName (final String name) {
    this.name = name;
  }

  public T getFolder () {
    return folder;
  }

  public void setFolder (final T folder) {
    this.folder = folder;
  }
}
