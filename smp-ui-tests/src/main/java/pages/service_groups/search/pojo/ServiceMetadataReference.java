package pages.service_groups.search.pojo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("ServiceMetadataReference")
public class ServiceMetadataReference {

	@XStreamAsAttribute
	String href;

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public String toString() {
		return "ServiceMetadataReference{" +
				"href='" + href + '\'' +
				'}';
	}
}
