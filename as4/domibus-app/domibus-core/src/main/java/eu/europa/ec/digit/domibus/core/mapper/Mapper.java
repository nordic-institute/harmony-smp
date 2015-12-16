package eu.europa.ec.digit.domibus.core.mapper;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Vincent Dijkstra
 *
 * @param <S> class of the source object
 * @param <T> class of the target object
 */
public interface Mapper<S, T> {

	/**
	 * Map an object of type <S> to an object of type <T>.
	 * @param sourceObject to map
	 * @return targetObject mapped object
	 */
	public T mapTo(S sourceObject);

	/**
	 * Map an object of type <T> to an object of type <S>.
	 * @param sourceObject to map
	 * @return targetObject mapped object
	 */
	public S mapFrom(T sourceObject);

	/**
	 * Maps the source object collection into a target object collection.
	 *
	 * @param sourceObjects source collection to map
	 * @return target object collection
	 */
	public Set<T> mapTo(Collection<S> sourceObjects);

	/**
	 * Maps the source object collection into a target object collection
	 * @param sourceObjects source collection to map
	 * @return target object collection
	 */
	public Collection<S> mapFrom(Set<T> sourceObjects);

}
