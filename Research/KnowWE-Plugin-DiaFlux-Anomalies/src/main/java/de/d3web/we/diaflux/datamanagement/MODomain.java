package de.d3web.we.diaflux.datamanagement;

import java.util.LinkedList;
import java.util.List;


public class MODomain implements Domain{
	
	private List<MOValue> list;
	
	public MODomain() {
		list = new LinkedList<MOValue>();
	}

	@Override
	public boolean intersects(Domain d) {
		if (!(d instanceof MODomain)) {
			return false;
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Domain d) {
		if (!(d instanceof MODomain)) {
			return false;
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Domain substract(Domain d) {
		if (!(d instanceof MODomain)) {
			return this;
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(Value v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Domain negate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addAll(Domain d) {
		if (!(d instanceof MODomain)) {
			return false;
		}
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean intersectWith(Domain d) {
		if (!(d instanceof MODomain)) {
			return false;
		}
		// TODO Auto-generated method stub
		return true;
	}

//	@Override
//	public boolean add(List<MOValue> listV) {
//		// TODO Auto-generated method stub
//		return false;
//	}

	@Override
	public List<MOValue> getList() {
		return list;
	}

}
