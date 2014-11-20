package eu.eCODEX.submission.validation.impl;

import eu.eCODEX.submission.validation.Validator;

/**
 * Implementation of a validator with empty validation rule which will accept every message.
 */
public class EmptyValidator implements Validator {

    @Override
    public void validate(Object message) {

    }
}
