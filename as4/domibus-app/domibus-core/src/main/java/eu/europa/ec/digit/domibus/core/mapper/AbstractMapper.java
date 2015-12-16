package eu.europa.ec.digit.domibus.core.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractMapper<S, T> implements Mapper<S, T> {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */

	@Override
	public Set<T> mapTo(Collection<S> sourceObjects) {
		Set<T> resultObjects = new HashSet<T>();
		for (S sourceObject : sourceObjects) {
			resultObjects.add(this.mapTo(sourceObject));
		}
		return resultObjects;
	}

	@Override
	public Collection<S> mapFrom(Set<T> sourceObjects) {
		Collection<S> resultObjects = new ArrayList<>();
		for (T sourceObject : sourceObjects) {
			resultObjects.add(this.mapFrom(sourceObject));
		}
		return resultObjects;
	}



	/* ---- Getters and Setters ---- */

}
