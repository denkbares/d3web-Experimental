package de.d3web.we.diaflux.pathcoloring;

import de.knowwe.core.user.UserContext;
import de.knowwe.diaflux.DiaFluxDisplayEnhancement;
import de.knowwe.diaflux.DiaFluxTraceHighlight;


public class AnomaliesHighlight implements DiaFluxDisplayEnhancement {

	public static String[] SCRIPTS = new String[] { "AnomalyEXT/scripts/anomalieshighlight.js" };
	public static String[] CSSS = new String[] { "AnomalyEXT/css/anomalieshighlight.css" };

		public static final String ANOMALIES_HIGHLIGHT = "anomalie_coverage";

		@Override
		public boolean activate(UserContext user, String scope) {
				return DiaFluxTraceHighlight.checkForHighlight(user, ANOMALIES_HIGHLIGHT);
		}

		@Override
		public String[] getScripts() {
			return SCRIPTS;
		}

		@Override
		public String[] getStylesheets() {
			return CSSS;
		}

	}

