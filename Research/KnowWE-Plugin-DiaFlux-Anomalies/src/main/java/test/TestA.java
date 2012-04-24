package test;

import org.junit.Assert;
import org.junit.Test;

import de.d3web.we.diaflux.datamanagement.Domain;
import de.d3web.we.diaflux.datamanagement.NumValue;


public class TestA {

	@Test
	public void testContains() {
		NumValue i1 = new NumValue(0, 5, true, true);
		NumValue i2 = new NumValue(0, 5, true, true);
		NumValue i3 = new NumValue(1, 6, false, false);
		NumValue i4 = new NumValue(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false, false);

		NumValue i5 = new NumValue(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false, false);

		System.out.println(i4.contains(Double.NEGATIVE_INFINITY));
		System.out.println(Double.NEGATIVE_INFINITY <= Double.NEGATIVE_INFINITY);
		Assert.assertTrue(i1.containsValue(i2));
		Assert.assertFalse(i1.containsValue(i3));
		Assert.assertTrue(i1.contains(0.));
		Assert.assertTrue(i1.contains(5.));
		i1.setMinClosed(false);
		Assert.assertFalse(i1.contains(0.));

	}

	@Test
	public void testIntersects() {
		NumValue i1 = new NumValue(0, 5, true, true);
		NumValue i2 = new NumValue(3, 7, true, true);
		NumValue i3 = new NumValue(-3,0,true, false);
		
		Assert.assertFalse(i1.intersects(i3));

		Assert.assertTrue(i1.intersects(i2));
		Assert.assertTrue(i1.intersects(i2));
		i1.setMaxClosed(false);
		i1.setMax(3);
		i2.setMinClosed(false);
		Assert.assertFalse(i1.intersects(i2));

	}
	
	@Test
	public void testSubstract() {
		NumValue i1 = new NumValue(0, 5, true, true);
		NumValue i2 = new NumValue(3, 7, true, true);
		NumValue i3 = new NumValue(0, 5, true, true);

//		i1.substract(i2);
		i1.substract(i3);
//		System.out.println(i1.substract(i2));
//		System.out.println(i1.substract(i3));
		
	}

	@Test
	public void domMerging() {
		NumValue i1 = new NumValue(0, 5, true, true);
		NumValue i2 = new NumValue(3, 7, true, true);
		NumValue i3 = new NumValue(0, 5, true, true);
		
		Domain<NumValue> domain = new Domain<NumValue>();
		domain.add(i1);
		domain.add(i3);
//		System.out.println(domain);
	}
}
