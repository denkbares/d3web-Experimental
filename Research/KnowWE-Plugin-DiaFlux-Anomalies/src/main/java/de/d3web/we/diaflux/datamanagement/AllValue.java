package de.d3web.we.diaflux.datamanagement;

import java.util.List;


public interface AllValue {

	public boolean intersects(AllValue v);

	public boolean containsValue(AllValue v);

	public AllValue intersectWith(AllValue v);

	public List<? extends AllValue> negate();

	public AllValue mergeWith(AllValue v);
	
	public List<? extends AllValue> substract(AllValue v);
	
	public boolean isEmpty();
}
