package test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.d3web.we.diaflux.datamanagement.Domain;
import de.d3web.we.diaflux.datamanagement.MOValue;


public class TestB {
	@Test
	public void testContains() {
		Set<String> possibleValues = new HashSet<String>();
		Set<String> actualValues = new HashSet<String>();
		
		possibleValues.add("A");
		possibleValues.add("B");
		possibleValues.add("C");
		
		Domain<MOValue> domain = new Domain<MOValue>();
		actualValues.add("A");
		
		MOValue m1 = new MOValue(possibleValues, actualValues, false);
		MOValue m2 = new MOValue(possibleValues, actualValues, false);
		
		domain.add(m1);
//		System.out.println(domain);
		domain.add(m1);
//		System.out.println(domain);
		domain.add(m1);
		System.out.println(domain);
	}
}
