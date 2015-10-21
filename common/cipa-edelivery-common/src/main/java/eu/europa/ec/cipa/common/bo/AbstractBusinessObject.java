package eu.europa.ec.cipa.common.bo;

import java.io.Serializable;

/**
 * Created by feriaad on 16/06/2015.
 */
public abstract class AbstractBusinessObject implements Serializable {

    public abstract boolean equals(Object o);

    public abstract int hashCode();
}
