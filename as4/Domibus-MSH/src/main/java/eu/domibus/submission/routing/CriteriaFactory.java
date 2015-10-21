package eu.domibus.submission.routing;

/**
 * Created by walcz01 on 05.08.2015.
 */
public interface CriteriaFactory {
    IRoutingCriteria getInstance();

    public String getName();
}
