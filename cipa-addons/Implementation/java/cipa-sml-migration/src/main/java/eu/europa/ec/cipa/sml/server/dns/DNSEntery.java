package eu.europa.ec.cipa.sml.server.dns;

public class DNSEntery {

	private String name;
	private String host;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public String toString()
	{
		return "host : "+this.getHost()+"\n name : "+this.getName()+"\n";
	}
}
