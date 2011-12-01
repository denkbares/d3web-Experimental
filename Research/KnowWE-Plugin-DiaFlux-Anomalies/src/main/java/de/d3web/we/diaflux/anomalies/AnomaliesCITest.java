package de.d3web.we.diaflux.anomalies;

import de.d3web.we.ci4ke.testing.AbstractCITest;
import de.d3web.we.ci4ke.testing.CITestResult;
import de.d3web.we.ci4ke.testing.CITestResult.Type;

public class AnomaliesCITest extends AbstractCITest {

	@Override
	public CITestResult call() throws Exception {

		return new CITestResult(Type.FAILED);

	}

}