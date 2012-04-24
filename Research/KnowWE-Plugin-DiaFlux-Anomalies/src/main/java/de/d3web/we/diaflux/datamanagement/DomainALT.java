package de.d3web.we.diaflux.datamanagement;

import java.util.List;

public interface Domain {

	public boolean add(Value v);

//	public boolean add(List<? extends Value> listV);

	public boolean addAll(Domain d);

	public boolean intersects(Domain d);

	public boolean contains(Domain d);

	public boolean isEmpty();

	public Domain negate();

	public List<? extends Value> getList();

	public String toString();

	public Domain substract(Domain d);

	public boolean intersectWith(Domain d);

}
