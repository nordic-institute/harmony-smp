package pages.domain;

public class DomainRow{

	private String domainCode;
	private String smlDomain;
	private String smlSmpID;
	private String clientCertHeader;
	private String clientCertAlias;
	private String signatureCertAlias;
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		DomainRow row = (DomainRow) o;
		
		if (domainCode != null ? !domainCode.equals(row.domainCode) : row.domainCode != null) return false;
		if (smlDomain != null ? !smlDomain.equals(row.smlDomain) : row.smlDomain != null) return false;
		if (smlSmpID != null ? !smlSmpID.equals(row.smlSmpID) : row.smlSmpID != null) return false;
		if (clientCertHeader != null ? !clientCertHeader.equals(row.clientCertHeader) : row.clientCertHeader != null)
			return false;
		if (clientCertAlias != null ? !clientCertAlias.equals(row.clientCertAlias) : row.clientCertAlias != null)
			return false;
		return signatureCertAlias != null ? signatureCertAlias.equals(row.signatureCertAlias) : row.signatureCertAlias == null;
	}
	
	
	@Override
	public String toString() {
		return "DomainRow{" +
				"domainCode='" + domainCode + '\'' +
				", smlDomain='" + smlDomain + '\'' +
				", smlSmpID='" + smlSmpID + '\'' +
				", clientCertHeader='" + clientCertHeader + '\'' +
				", clientCertAlias='" + clientCertAlias + '\'' +
				", signatureCertAlias='" + signatureCertAlias + '\'' +
				'}';
	}
	
	public String getDomainCode() {
		return domainCode;
	}
	
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	
	public String getSmlDomain() {
		return smlDomain;
	}
	
	public void setSmlDomain(String smlDomain) {
		this.smlDomain = smlDomain;
	}
	
	public String getSmlSmpID() {
		return smlSmpID;
	}
	
	public void setSmlSmpID(String smlSmpID) {
		this.smlSmpID = smlSmpID;
	}
	
	public String getClientCertHeader() {
		return clientCertHeader;
	}
	
	public void setClientCertHeader(String clientCertHeader) {
		this.clientCertHeader = clientCertHeader;
	}
	
	public String getClientCertAlias() {
		return clientCertAlias;
	}
	
	public void setClientCertAlias(String clientCertAlias) {
		this.clientCertAlias = clientCertAlias;
	}
	
	public String getSignatureCertAlias() {
		return signatureCertAlias;
	}
	
	public void setSignatureCertAlias(String signatureCertAlias) {
		this.signatureCertAlias = signatureCertAlias;
	}
}
