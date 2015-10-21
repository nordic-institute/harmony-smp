package eu.europa.ec.cipa.common.logging;

/**
 * Created by feriaad on 18/06/2015.
 */
public interface ILogEvent {

    String CATEGORY_SECURITY = "SECURITY";
    String CATEGORY_BUSINESS = "BUSINESS";

    String getMessage(String code);
}
