package eu.europa.ec.cipa.bdmsl.common.bo;

import eu.europa.ec.cipa.common.bo.AbstractBusinessObject;

/**
 * Created by feriaad on 12/06/2015.
 */
public class PageRequestBO extends AbstractBusinessObject {

    private String smpId;
    private String page;

    public String getSmpId() {
        return smpId;
    }

    public void setSmpId(String smpId) {
        this.smpId = smpId;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PageRequestBO that = (PageRequestBO) o;

        if (smpId != null ? !smpId.equals(that.smpId) : that.smpId != null) return false;
        return !(page != null ? !page.equals(that.page) : that.page != null);

    }

    @Override
    public int hashCode() {
        int result = smpId != null ? smpId.hashCode() : 0;
        result = 31 * result + (page != null ? page.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PageRequestBO{" +
                "smpId='" + smpId + '\'' +
                ", page=" + page +
                '}';
    }
}
