package eu.europa.ec.smp.api;

import eu.europa.ec.smp.api.exceptions.XsdInvalidException;

/**
 * Created by migueti on 25/01/2017.
 */
public interface IServiceMetadataValidator {

    boolean validateXSD(String xmlBody) throws XsdInvalidException;
}
