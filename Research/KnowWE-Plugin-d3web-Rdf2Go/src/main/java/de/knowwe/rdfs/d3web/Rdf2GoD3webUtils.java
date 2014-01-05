package de.knowwe.rdfs.d3web;

import java.util.ArrayList;
import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.session.Session;
import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.d3web.we.knowledgebase.D3webCompiler;
import de.knowwe.core.compile.PackageCompiler;
import de.knowwe.core.compile.packaging.PackageCompileType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.rdf2go.Rdf2GoCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class Rdf2GoD3webUtils {

	public static URI getSessionIdURI(Rdf2GoCore core, Session session) {
		return core.createlocalURI(Strings.encodeURL(session.getId()));
	}

	public static URI getFactURI(Rdf2GoCore core) {
		return core.createlocalURI("Fact");
	}

	public static URI getHasFactURI(Rdf2GoCore core) {
		return core.createlocalURI("hasFact");
	}

	public static URI getHasValueURI(Rdf2GoCore core) {
		return core.createlocalURI("hasValue");
	}

	public static URI getHasTerminologyObjectURI(Rdf2GoCore core) {
		return core.createlocalURI("hasTerminologyObject");
	}

	public static String getIdentifierExternalForm(NamedObject namedObject) {
		Identifier termIdentifier;
		if (namedObject instanceof Choice) {
			termIdentifier = new Identifier(((Choice) namedObject).getQuestion().getName(),
					namedObject.getName());

		}
		else {
			termIdentifier = new Identifier(namedObject.getName());
		}
		String externalForm = Rdf2GoUtils.getCleanedExternalForm(termIdentifier);
		return externalForm;
	}

	public static Collection<Rdf2GoCompiler> getRdf2GoCompilers(D3webCompiler compiler, Section<?> section) {
		Collection<Section<? extends PackageCompileType>> compileSections = compiler.getPackageManager().getCompileSections();
		Collection<Rdf2GoCompiler> compilers = new ArrayList<Rdf2GoCompiler>();
		for (Section<? extends PackageCompileType> compileSection : compileSections) {
			Collection<PackageCompiler> packageCompilers = compileSection.get().getPackageCompilers(
					compileSection);
			for (PackageCompiler packageCompiler : packageCompilers) {
				if (packageCompiler instanceof Rdf2GoCompiler) {
					compilers.add((Rdf2GoCompiler) packageCompiler);
				}
			}
		}
		return compilers;
	}

}
