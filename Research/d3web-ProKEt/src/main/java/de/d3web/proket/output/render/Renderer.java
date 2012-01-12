/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.proket.output.render;

import java.util.Vector;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.data.Answer;
import de.d3web.proket.data.DialogTree;
import de.d3web.proket.data.IDialogObject;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.ClassUtils;
import de.d3web.proket.utils.TemplateUtils;

/**
 * General renderer. This renderer is handling the basic things. Overload it if
 * you need more specific handling.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 */
public class Renderer implements IRenderer {

	// maximum possible rating value
	protected double maxRating = 100;

	/**
	 * TODO: leave that way?!?! gets the renderer appropriate for this given
	 * dialogOBject according to its virtual class name
	 */
	public static IRenderer getRenderer(IDialogObject dialogObject) {

		IRenderer renderer = (IRenderer) ClassUtils.getBestObject(
				dialogObject.getVirtualClassName(),
				"de.d3web.proket.output.render", "Renderer");
		return renderer;
	}

	/**
	 * Fill the given String Template with values depending on the dialogObject
	 * TODO: formerly here also a cc was used. Not needed?!
	 * 
	 * @created 09.10.2010
	 * @param dialogObject
	 * @param st
	 */
	protected void fillTemplate(IDialogObject dialogObject, StringTemplate st) {

		// fill template by fields and getters of the dialog
		// object and of the inheritable attributes
		TemplateUtils.fillByFields(st, dialogObject);
		TemplateUtils.fillByGetters(st, dialogObject);
		TemplateUtils.fillByFields(st,
				dialogObject.getInheritableAttributes());
		TemplateUtils.fillByGetters(st,
				dialogObject.getInheritableAttributes());

		// Calculate the presentation of the rating of the dialogobject if
		// any rating value is given
		Double rating = dialogObject.getRating();
		if (rating != null) {
			st.removeAttribute("rating");
			st.setAttribute("rating",
					String.format("%.0f%%", rating / maxRating * 100));
			String ratingClass = D3webUtils.discretizeRating(rating);
			st.setAttribute("ratingClass", ratingClass);
		}

		// if dialog object has a parent, set the attribute parentFullId of the
		// child with the value of the parent
		if (dialogObject.getParent() != null) {
			st.setAttribute("parentFullId", dialogObject.getParent()
					.getFullId());
		}

		if (dialogObject instanceof Answer) {
			if (((Answer) dialogObject).getAbstraction()) {
				// hier d3web abfragen und füllen!
				System.out.println("Abstraction!");
				st.setAttribute("selection", "ABSTR");
			}
		}
	}

	/**
	 * Fetches the css attribute from a dialog object and writes the css command
	 * into the css code container or loads an external stylesheet into the
	 * container
	 * 
	 * @created 09.10.2010
	 * @param cc the container collection
	 * @param dialogObject the object containing probably some css
	 */
	protected void handleCss(ContainerCollection cc, IDialogObject dialogObject) {
		// css code
		String css = dialogObject.getCss();
		if (css != null) {
			// file reference or inline css?
			// regex prüft ob der css-String was in der Form
			// "file1, file2, file3" ist
			if (css.matches("[\\w-,\\s]*")) {
				String[] parts = css.split(",");
				for (String partCSS : parts) {

					// reference to stile sheet files
					StringTemplate stylesheet =
							// replace whitespace characters with empty string
							// and then get the corresponding css file
							TemplateUtils.getStringTemplate(partCSS.replaceAll("\\s", ""), "css");

					// if not at the end of stylesheet string
					if (stylesheet != null) {

						// assign object id to the css and write css into
						// codecontainer
						stylesheet.setAttribute("id", "#" + dialogObject.getFullId());
						cc.css.add(stylesheet.toString());
					}
				}
			} else {
				// inline: just write css command into the code container
				cc.css.addStyle(css, "#" + dialogObject.getFullId());
			}
		}
	}

	/**
	 * Creates a table surrounding for the dialogObject, with a given container
	 * collection and a result StringBuilder that already contains the
	 * representation of the dialogObject
	 * 
	 * @created 09.10.2010
	 * @param dialogObject the dialogObject
	 * @param cc the container collection
	 * @param result the string builder representing the dialog object
	 */
	protected void makeTables(IDialogObject dialogObject,
			ContainerCollection cc, StringBuilder result) {

		// get the parent. If not existent, return
		IDialogObject parent = dialogObject.getParent();
		if (parent == null) {
			return;
		}

		// parent id
		String id = parent.getFullId();

		// get colspan from attributes
		int colspan = dialogObject.getInheritableAttributes().getColspan();

		// insert table cell opening string before the content of result which
		// is the content/rendering of the dialog object itself
		result.insert(0, cc.tc.getNextCellOpeningString(id, colspan));

		// append table cell closing
		result.append(cc.tc.getNextCellClosingString(id, colspan));

		// add to the table container
		cc.tc.addNextCell(id, colspan);
	}


	/**
	 * Render the child elements of a dialog object
	 * 
	 * @created 09.10.2010
	 * @param st the stringtemplate so far
	 * @param cc the containercollection
	 * @param dialogObject the object
	 * @param force flag if a re-rendering(?) should be forced
	 */
	protected void renderChildren(StringTemplate st, ContainerCollection cc,
			IDialogObject dialogObject, boolean force) {
		StringBuilder childrenHTML = new StringBuilder();

		/* multicolumn stuff (does not affect other types) */
		// number of columns that is to be set for this element
		int columns = dialogObject.getInheritableAttributes().getColumns();

		// if dialogobject is a question, get the answer-columns attribute
		// instead
		if (dialogObject instanceof Question) {
			columns = dialogObject.getInheritableAttributes()
					.getAnswerColumns();
		}

		if (dialogObject instanceof Answer) {
			if (((Answer) dialogObject).getAbstraction()) {
				System.out.println("ABSTRACTION: " + dialogObject.getText());
			}
		}

		// make it work also for answers, but an answer is alway 1-column so far
		if (dialogObject instanceof Answer) {
			columns = 1;
		}

		// if more than one column is required, get open-table tag from
		// TableContainer
		// and append it to the HTML
		if (columns > 1) {
			String tableOpening =
					cc.tc.openTable(dialogObject.getFullId(), columns);
			childrenHTML.append(tableOpening);
		}

		// for each of the child elements
		Vector<IDialogObject> children = dialogObject.getChildren();
		for (IDialogObject child : children) {

			// get the matching renderer
			IRenderer childRenderer = Renderer.getRenderer(child);

			// receive the matching HTML from the Renderer and append
			String childHTML =
					childRenderer.renderDialogObject(cc, child, false, force);
			if (childHTML != null) {
				childrenHTML.append(childHTML);
			}
		}

		// multicolumn again: close the table that had been opened for
		// multicolumn cases
		if (columns > 1) {
			String tableClosing = cc.tc.closeTable(dialogObject.getFullId());
			childrenHTML.append(tableClosing);
		}


		if (children.size() > 0) {
			st.setAttribute("children", childrenHTML.toString());
		}
		

	}

	@Override
	/**
	 * Render dialog object
	 * with a given container collection and set standard case of NOT
	 * excluding children
	 */
	public String renderDialogObject(ContainerCollection cc,
			IDialogObject dialogObject) {
		return renderDialogObject(cc, dialogObject, false);
	}

	@Override
	/**
	 * Render dialog object
	 * with given cc, given excludeChildren-flag, and set forceToOverwrite 
	 * as standard false
	 */
	public String renderDialogObject(ContainerCollection cc,
			IDialogObject dialogObject, boolean excludeChildren) {
		return renderDialogObject(cc, dialogObject, excludeChildren, false);
	}

	/**
	 * Render dialog object with given cc, given excludeChildren, given force
	 * and set Session to null so it is not considered in the non-d3web case
	 */
	@Override
	public String renderDialogObject(ContainerCollection cc,
			IDialogObject dialogObject, boolean excludeChildren, boolean force) {
		return renderDialogObject(cc, dialogObject, excludeChildren, force,
				null);
	}

	/**
	 * The final dialog object render method that is called by all above ones.
	 * 
	 * @created 09.10.2010
	 * @param cc the ContainerCollection
	 * @param dialogObject the object that is to be rendered
	 * @param excludeChildren flag
	 * @param force flag
	 * @param session potentially a d3web-Session
	 * @return the String representing the render-representation (HTML, JS...)
	 *         of the DialogTree
	 */
	protected String renderDialogObject(ContainerCollection cc, IDialogObject dialogObject,
			boolean excludeChildren, boolean force, Session session) {

		// TODO maybe null is not such a good idea here?
		// already rendered somewhere? If yes, and if render-force is not set
		// then return null, i.e. no representation
		if (dialogObject.isRendered() && !force) {
			return null;
		}

		StringBuilder result = new StringBuilder();

		// get the HTML template for the dialog object
		StringTemplate st = TemplateUtils.getStringTemplate(
				dialogObject.getVirtualClassName(), "html");
                
		// get the inh.attributes of this objects and inherit missing
		// attributes where needed from parents
		dialogObject.getInheritableAttributes().compileInside();

		// fill the stringtemplate with attributes/getters from the object
		fillTemplate(dialogObject, st);

		// include css styles
		handleCss(cc, dialogObject);

		// children: if they are not to be excluded, just render them forcedly
		if (!excludeChildren) {
			renderChildren(st, cc, dialogObject, force);
		}

		// append filled template to result string
		result.append(st.toString());

		// optional tables
		makeTables(dialogObject, cc, result);

		// visible?
		if (!dialogObject.isVisible()) {
			cc.css.addStyle("display: none;", "#" + dialogObject.getFullId());
		}

		// mark as rendered
		dialogObject.setRendered(true);

		// save to output
		return result.toString();
	}

	@Override
	/**
	 * Render the tree
	 * in the simple case with no cc given and no constraints
	 */
	public ContainerCollection renderRoot(DialogTree dialogTree) {
		ContainerCollection cc = new ContainerCollection();
		renderRoot(dialogTree, cc);
		return cc;
	}

	@Override
	/**
	 * Render the tree
	 * with a given container collection
	 */
	public void renderRoot(DialogTree dialogTree, ContainerCollection cc) {
		renderRoot(dialogTree, cc, false);
	}

	@Override
	/**
	 * Render the tree
	 * with a given container collection and a constraint whether to exclude
	 * children rendering or not (standard case: do not exclude)
	 */
	public void renderRoot(DialogTree dialogTree, ContainerCollection cc,
			boolean excludeChildren) {

		// mark as unrendered
		dialogTree.setUnrendered();

		// get the root object
		IDialogObject dialogObject = dialogTree.getRoot();

		// call the render method on the root
		String result = renderDialogObject(cc, dialogObject, excludeChildren);

		// if something was written as html, add this to HTML container
		// collection
		if (result != null) {
			cc.html.add(result);
		}
	}

	/**
	 * Render the tree in the d3web case by calling render only on the root
	 */
	@Override
	public void renderRootD3web(DialogTree dialogTree, ContainerCollection cc,
			Session session) {

		// set the complete tree as "unrendered"
		dialogTree.setUnrendered();

		// get the root
		IDialogObject dialogObject = dialogTree.getRoot();

		// initiate rendering, do not exclude children and force overwriting
		String result =
				renderDialogObject(cc, dialogObject, false, true, session);

		// if something was written as html, append it to HTML container
		if (result != null) {
			cc.html.add(result);
		}
	}

}
