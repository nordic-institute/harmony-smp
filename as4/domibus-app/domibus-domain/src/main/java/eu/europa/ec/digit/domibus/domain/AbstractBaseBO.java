package eu.europa.ec.digit.domibus.domain;

import java.io.Serializable;

/**
 * Created by feriaad on 16/06/2015.
 */
public abstract class AbstractBaseBO implements Serializable {

	/* ---- Constants ---- */

	private static final long serialVersionUID = 201511042302L;

	/* ---- Business Methods ---- */

	public abstract boolean equals(Object o);

    public abstract int hashCode();
}
