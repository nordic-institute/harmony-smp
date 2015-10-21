package eu.europa.ec.cipa.bdmsl.common.bo;

import eu.europa.ec.cipa.common.bo.AbstractBusinessObject;

import java.util.List;

/**
 * Created by feriaad on 12/06/2015.
 */
public class ParticipantListBO extends AbstractBusinessObject {

    private List<ParticipantBO> participantBOList;

    private String nextPage;

    public List<ParticipantBO> getParticipantBOList() {
        return participantBOList;
    }

    public void setParticipantBOList(List<ParticipantBO> participantBOList) {
        this.participantBOList = participantBOList;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipantListBO)) return false;

        ParticipantListBO that = (ParticipantListBO) o;

        if (participantBOList != null ? !participantBOList.equals(that.participantBOList) : that.participantBOList != null)
            return false;
        return !(nextPage != null ? !nextPage.equals(that.nextPage) : that.nextPage != null);

    }

    @Override
    public int hashCode() {
        int result = participantBOList != null ? participantBOList.hashCode() : 0;
        result = 31 * result + (nextPage != null ? nextPage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ParticipantListBO{" +
                "participantBOList=" + participantBOList +
                ", nextPage=" + nextPage +
                '}';
    }
}
