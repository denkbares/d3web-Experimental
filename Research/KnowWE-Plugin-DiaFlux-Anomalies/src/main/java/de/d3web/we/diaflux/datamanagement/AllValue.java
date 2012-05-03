package de.d3web.we.diaflux.datamanagement;

import java.util.List;

public interface AllValue<T> {

	public boolean intersects(T v);

	public boolean containsValue(T v);

	public T intersectWith(T v);

	public List<T> negate();

	public T mergeWith(T v);

	public List<T> substract(T v);

	public boolean isEmpty();
}
