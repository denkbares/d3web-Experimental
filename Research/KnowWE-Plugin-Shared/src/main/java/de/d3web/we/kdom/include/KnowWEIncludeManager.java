/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.kdom.include;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.plugin.PluginManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.event.ArticleCreatedEvent;
import de.d3web.we.event.Event;
import de.d3web.we.event.EventListener;
import de.d3web.we.event.KDOMCreatedEvent;
import de.d3web.we.event.UpdatingDependenciesEvent;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.RootType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.xml.AbstractXMLType;
import de.d3web.we.kdom.xml.XMLContent;
import de.knowwe.plugin.Plugins;

/**
 * This class manages all Includes. Keeps tack and links them to their target
 * Sections.
 * 
 * @author astriffler
 * 
 */
public class KnowWEIncludeManager implements EventListener {

	private final String web;

	/**
	 * This map stores for every Include the Section they are including. Key is
	 * the Include Section, value the included Section.
	 */
	private final Map<Section<Include>, List<Section<? extends Type>>> src2targets =
			new HashMap<Section<Include>, List<Section<? extends Type>>>();

	// /**
	// * This map stores for every Include the last Section they were including,
	// * if the target of the Include changes. Key is the Include Section, value
	// * the last included Section.
	// */
	// private final Map<Section<Include>, List<Section<? extends
	// Type>>> src2lastTargets =
	// new HashMap<Section<Include>, List<Section<? extends
	// Type>>>();

	/**
	 * This map stores for every title a set of Includes that include Sections
	 * from the article with the given title.
	 */
	private final Map<String, Set<Section<Include>>> targetArticle2src =
			new HashMap<String, Set<Section<Include>>>();

	private static KnowWEIncludeManager instance = null;

	public static KnowWEIncludeManager getInstance() {
		if (instance == null) {
			instance = (KnowWEIncludeManager) PluginManager.getInstance().getExtension(
					Plugins.EXTENDED_PLUGIN_ID,
					Plugins.EXTENDED_POINT_EventListener,
					"KnowWE-Plugin-Shared",
					KnowWEIncludeManager.class.getSimpleName()).getSingleton();
		}
		return instance;
	}

	public KnowWEIncludeManager() {
		this.web = KnowWEEnvironment.DEFAULT_WEB;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public String getWeb() {
		return web;
	}

	/**
	 * Registers the Include Section to the IncludeManager and links them to
	 * their target.
	 */
	public void registerInclude(Section<Include> src) {

		if (src.get() instanceof Include) {

			IncludeAddress address = Include.getIncludeAddress(src);

			// this is the Section the Include Section wants to include
			List<Section<? extends Type>> targets =
					new ArrayList<Section<? extends Type>>();

			if (address == null) {
				targets.add(getNoValidAddressErrorSection(src));

			}
			else if (KnowWEEnvironment.getInstance().getArticleManager(web)
					.getSectionizingArticles().contains(address.getTargetArticle())) {
				// check for include loops
				// (This algorithm later initializes articles, if they are not
				// yet build but the Include Sections wants to include from
				// it. If these initializing Articles directly or indirectly
				// include
				// this article, which isn't completely build itself, we got a
				// loop.)
				targets.add(getIncludeLoopErrorSection(src));

			}
			else {
				// no loops found then, get the targeted article
				KnowWEArticle art = KnowWEEnvironment.getInstance()
						.getArticle(KnowWEEnvironment.DEFAULT_WEB,
								address.getTargetArticle());

				if (art == null) {
					// check if the targeted article exists but is not yet build
					if (KnowWEEnvironment.getInstance().getWikiConnector()
							.doesPageExist(address.getTargetArticle())) {
						String artSrc = KnowWEEnvironment.getInstance()
								.getWikiConnector().getArticleSource(
										address.getTargetArticle());
						if (artSrc != null) {
							// build the targeted article
							art = KnowWEArticle.createArticle(artSrc, address.getTargetArticle(),
									KnowWEEnvironment.getInstance().getRootType(), web);
							KnowWEEnvironment.getInstance()
									.getArticleManager(web)
									.registerArticle(art);
						}
					}
				}

				if (art != null) {
					targets = findTargets(art, src);
				}
				else {
					targets.add(new IncludeErrorSection("Error: Article '"
							+ address.getTargetArticle()
							+ "' not found.", src, src.getArticle()));
				}
			}

			// add Include Section and target Section to their maps
			if (address != null) {
				getIncludingSectionsForArticle(address.getTargetArticle()).add(src);
			}
			src2targets.put(src, targets);
			src.setChildren(targets);
			src.setHasSharedChildren(true);
		}
	}

	/**
	 * Finds the target of the Include in the given Article
	 */
	private List<Section<? extends Type>> findTargets(KnowWEArticle art, Section<Include> includeSec) {

		IncludeAddress address = Include.getIncludeAddress(includeSec);
		List<Section<? extends Type>> targets = new ArrayList<Section<? extends Type>>();

		if (address == null) {
			targets.add(getNoValidAddressErrorSection(includeSec));
			return targets;
		}

		// search node in Article

		List<Section<? extends Type>> matchingObjectTypeNameSections = new ArrayList<Section<? extends Type>>();
		List<Section<? extends Type>> matchingIdEndSections = new ArrayList<Section<? extends Type>>();
		Section<? extends Type> matchingIDSection = null;

		String typeName = address.isContentSectionTarget() ? address.getTargetSection().substring(
				0,
						address.getTargetSection().indexOf(XMLContent.CONTENT_SUFFIX))
				: address.getTargetSection();

		if (address.getTargetSection() != null) {
			for (Section<?> node : art.getAllNodesPreOrder()) {
				// included Section -> skip
				if (node.getArticle() != art) continue;

				// if the complete ID is given
				if (node.getID().equalsIgnoreCase(address.getOriginalAddress())) {
					matchingIDSection = node;
					break;
				}
				// if only the last part of the ID is given
				if ((node.getID().length() > address.getTargetSection().length() && node.getID().substring(
										node.getID().length() - address.getTargetSection().length()).equalsIgnoreCase(
										address.getTargetSection()))) {
					matchingIdEndSections.add(node);
					if (!address.isWildcardSectionTarget() && matchingIdEndSections.size() > 1) {
						break;
					}
				}
				// or the ObjectType
				if (node.get().getClass().getSimpleName()
							.compareToIgnoreCase(typeName) == 0) {
					matchingObjectTypeNameSections.add(node);
					if (!address.isWildcardSectionTarget()
							&& matchingObjectTypeNameSections.size() > 1) {
						break;
					}
				}
			}
		}

		// check the Lists if matching Sections were found
		if (matchingIDSection != null) {
			targets.add(matchingIDSection);

		}
		else if (!address.isWildcardSectionTarget()
				&& (matchingObjectTypeNameSections.size() > 1 || matchingIdEndSections.size() > 1)) {
			targets.add(new IncludeErrorSection("Error: Include '"
					+ address.getOriginalAddress() + "' is ambiguous. Try IDs.",
					includeSec, includeSec.getArticle()));

		}
		else if (!matchingObjectTypeNameSections.isEmpty()) {

			for (Section<? extends Type> locatedNode : matchingObjectTypeNameSections) {
				// get XMLContent if necessary
				if (address.isContentSectionTarget()
						&& !(locatedNode.get() instanceof XMLContent)) {
					if (locatedNode.get() instanceof AbstractXMLType
							&& Sections.findChildOfType(locatedNode, XMLContent.class) != null) {
						targets.add(Sections.findChildOfType(locatedNode, XMLContent.class));
					}
					else {
						targets.add(new IncludeErrorSection(
								"Error: No content Section found for Include '"
										+ address.getOriginalAddress() + "'.", includeSec,
								includeSec.getArticle()));
					}
				}
				else {
					targets.add(locatedNode);
				}
			}

		}
		else if (!matchingIdEndSections.isEmpty()) {
			targets.addAll(matchingIdEndSections);

		}
		else if (address.getTargetSection() == null) {
			Section<? extends RootType> root = Sections.findChildOfType(art.getSection(),
					RootType.class);
			if (root != null) {
				List<Section<? extends Type>> children = root.getChildren();
				if (!children.isEmpty()) {

					// Checks the given List of children if it contains Sections
					// that were already included in the article in another
					// place.
					Set<Section<? extends Type>> candidates =
							new HashSet<Section<? extends Type>>(children);

					List<Section<?>> potentialDuplicates =
							new ArrayList<Section<? extends Type>>();

					for (Section<Include> inc : getActiveIncludesForArticle(includeSec.getArticle())) {
						if (inc != includeSec) {
							Sections.getAllNodesPreOrderToDepth(inc, potentialDuplicates, 2);
						}
					}

					for (Section<?> pd : potentialDuplicates) {
						// Found duplicates get removed.
						candidates.remove(pd);
					}

					if (candidates.isEmpty()) {
						targets.add(new IncludeErrorSection("Error: All Sections at the address '"
								+ address.getOriginalAddress()
								+ "' are already added in other Includes.",
								includeSec, includeSec.getArticle()));
					}
					else {
						// restore order
						for (Section<? extends Type> sec : children) {
							if (candidates.contains(sec)) {
								targets.add(sec);
							}
						}
					}
				}
			}

		}
		else {
			targets.add(new IncludeErrorSection("Error: Include '" + includeSec.getOriginalText()
					+ "' not found.", includeSec, includeSec.getArticle()));
		}
		// check if the included Section originates from the requesting article,
		// but isn't directly included from it -> causes update loops
		// (auto includes are allowed, but not via other articles)
		boolean loop = false;
		for (Section<? extends Type> tar : targets) {
			if (!(tar instanceof IncludeErrorSection)
					&& !address.getTargetArticle().equals(includeSec.getTitle())
					&& tar.getTitle().equals(includeSec.getTitle())) {
				loop = true;
				break;
			}
		}
		if (loop) {
			targets.clear();
			targets.add(getIncludeLoopErrorSection(includeSec));
		}
		return targets;
	}

	private IncludeErrorSection getNoValidAddressErrorSection(Section<Include> src) {
		return new IncludeErrorSection("Error: No valid address found in '" +
				src.getOriginalText().trim() + "'.", src, src.getArticle());
	}

	private IncludeErrorSection getIncludeLoopErrorSection(Section<Include> src) {
		return new IncludeErrorSection("Error: Include loop detected!",
				src, src.getArticle());
	}

	/**
	 * Updates Includes to the given <tt>article</tt>. This method needs to get
	 * called after an article has changed.
	 */
	private void updateIncludesToArticle(KnowWEArticle article) {
		Set<String> reviseArticles = new HashSet<String>();
		Set<Section<Include>> includes = new HashSet<Section<Include>>(
				getIncludingSectionsForArticle(article.getTitle()));
		for (Section<Include> inc : includes) {
			// check if the target of the Include Section has changed
			List<Section<?>> targets = findTargets(article, inc);
			List<Section<?>> lastTargets = src2targets.get(inc);

			if (!targets.equals(lastTargets)) {
				if (lastTargets != null) {
					// since the targets have changed, the including article
					// doesn't reuse some or all of the last targets
					Set<Section<?>> diff = new HashSet<Section<?>>(lastTargets);
					diff.removeAll(targets);
					for (Section<? extends Type> unusedLastTar : diff) {
						unusedLastTar.setReusedByRecursively(inc.getTitle(), false);
					}
					// articles that reuse the include besides the owner of the
					// include also do not reuse these last targets
					Set<String> reusedBy = inc.getReusedBySet();
					for (String title : reusedBy) {
						for (Section<? extends Type> unusedLastTar : diff) {
							unusedLastTar.setReusedByRecursively(title, false);
						}
					}
				}
				// overwrite the last target with the new
				src2targets.put(inc, targets);
				inc.setChildren(targets);
				inc.setHasSharedChildren(true);
				// // put the last target in the according map to make it
				// available
				// // for destruction of the stuff produced by its
				// SubtreeHandlers
				// src2lastTargets.put(inc, lastTargets);
				// inc.setLastChildren(lastTargets);
				if (Include.getIncludeAddress(inc) != null) {
					getIncludingSectionsForArticle(
							Include.getIncludeAddress(inc).getTargetArticle()).add(inc);
				}
				// don't revise the article that is currently revised again
				// and don't revise if the originalText hasn't changed
				if (!inc.getTitle().equals(article.getTitle())) {
					if (lastTargets != null && targets.size() == lastTargets.size()) {
						for (int i = 0; i < targets.size(); i++) {
							if (!targets.get(i).getOriginalText().equals(
									lastTargets.get(i).getOriginalText())) {
								reviseArticles.add(inc.getTitle());
								break;
							}
						}
					}
					else {
						reviseArticles.add(inc.getTitle());
					}
				}
			}
			else {
				// // if there are last targets for the given Include from a
				// // previous update, they are not longer up to date and need
				// to
				// // be removed
				// src2lastTargets.remove(inc);

				// if the targets are the same, but because of an update of the
				// targets article, there are some new, not reused successors
				// because of changes to the Includes of the target article, the
				// article of this Include also needs to be updated
				for (Section<?> tar : targets) {
					if (tar.isOrHasSuccessorNotReusedBy(inc.getTitle())) {
						reviseArticles.add(inc.getTitle());
						break;
					}
				}
			}
		}
		// rebuild the articles
		// there will be no changes to the KDOM, but maybe
		// changes to the Knowledge... the update mechanism will take care of
		// that
		KnowWEEnvironment env = KnowWEEnvironment.getInstance();

		for (String title : reviseArticles) {
			if (env.getArticleManager(web).getSectionizingArticles().contains(title)
					|| env.getArticleManager(web).getUpdatingArticles().contains(title)) {
				continue;
			}
			KnowWEArticle newArt = KnowWEArticle.createArticle(
					env.getArticle(article.getWeb(), title).getSection().getOriginalText(), title,
					env.getRootType(), web, false);

			env.getArticleManager(web).registerArticle(newArt);
		}
	}

	// /**
	// * @returns the children respectively the target of the Include
	// */
	// public List<Section<? extends Type>>
	// getChildrenForSection(Section<Include> src) {
	// List<Section<? extends Type>> children =
	// src2targets.get(src);
	// if (children == null) {
	// children = new ArrayList<Section<? extends Type>>(0);
	// }
	// if (children.isEmpty()) {
	// children.add(new IncludeErrorSection("Section " + src.toString()
	// + " is not registered as an including Section", src, src.getArticle()));
	// }
	// return children;
	// }
	//
	// /**
	// * <b>IF THERE IS NO LAST SECTION, THIS RETUNS NULL... CHECK FOR NULL!</b>
	// *
	// * @returns the last children respectively the last target of the Include
	// */
	// public List<Section<? extends Type>>
	// getLastChildrenForSection(Section<Include> src) {
	// return src2lastTargets.get(src);
	// }

	/**
	 * Gets all Sections that include from the given Article
	 */
	private Set<Section<Include>> getIncludingSectionsForArticle(String title) {
		Set<Section<Include>> includingSections = targetArticle2src.get(title);
		if (includingSections == null) {
			includingSections = new HashSet<Section<Include>>();
			targetArticle2src.put(title, includingSections);
		}
		return includingSections;
	}

	/**
	 * Returns all inactive Includes for the article with the given title.
	 * 
	 * @created 29.05.2010
	 * @param title is the title of the article you want the inactive Includes
	 *        from.
	 * @return a List with all inactive Includes for the article with the given
	 *         title. Returns an empty list, if there are no inactive Includes
	 *         still registered to the KnowWEIncludeManager.
	 */
	private List<Section<Include>> getInactiveIncludesForArticle(KnowWEArticle article) {
		List<Section<Include>> inactiveIncludes = new ArrayList<Section<Include>>();
		// get all registered Includes (from all articles)
		List<Section<Include>> allIncludes = new ArrayList<Section<Include>>(src2targets.keySet());

		for (Section<Include> inc : allIncludes) {
			// if an Include is from the article with the given title but not in
			// the active Includes of this article, it is out of use and
			// inactive
			if (inc.getTitle().equals(article.getTitle()) && inc.getArticle() != article) {
				inactiveIncludes.add(inc);
			}
		}
		return inactiveIncludes;
	}

	private List<Section<Include>> getActiveIncludesForArticle(KnowWEArticle article) {
		List<Section<Include>> activeIncludes = new ArrayList<Section<Include>>();
		// get all registered Includes (from all articles)
		List<Section<Include>> allIncludes = new ArrayList<Section<Include>>(src2targets.keySet());

		for (Section<Include> inc : allIncludes) {
			// if an Include is from the article with the given title but not in
			// the active Includes of this article, it is out of use and
			// inactive
			if (inc.getTitle().equals(article.getTitle()) && inc.getArticle() == article) {
				activeIncludes.add(inc);
			}
		}
		return activeIncludes;
	}

	// /**
	// * Cleans the maps from Include Sections that are not longer in present in
	// * the article
	// */
	// public void removeInactiveIncludesForArticle(String title,
	// List<Section<Include>> activeIncludes) {
	// // get all registered Includes (from all articles)
	// List<Section> allIncludes = new ArrayList<Section>(src2target.keySet());
	//
	// for (Section inc : allIncludes) {
	// // if an Include is from the article with the given title but not in
	// // the active Includes of this article, it is out of use
	// if (inc.getTitle().equals(title) && !activeIncludes.contains(inc)) {
	// List<Section<? extends Type>> targetSections =
	// src2target.get(inc);
	// // since the target has changed, the including article doesn't
	// // reuse the last target
	// for (Section<? extends Type> tar : targetSections) {
	// tar.setReusedStateRecursively(inc.getTitle(), false);
	// }
	// // remove from map...
	// src2target.remove(inc);
	// // also delete the Include from the set of Includes of
	// // the target article
	// if (inc.getIncludeAddress() != null) {
	// getIncludingSectionsForArticle(inc.getIncludeAddress().getTargetArticle()).remove(
	// inc);
	// }
	// }
	// }
	// }

	/**
	 * Sets the reused states of Sections that were included before, but are not
	 * included any longer in the article with the given title to false. Of
	 * course both lists need to contain the inactive, respectively the active
	 * Includes of one and the same article, the article specified by the
	 * parameter <tt>title</tt>.
	 * 
	 * @created 29.05.2010
	 * @param title is the title of the article you want to set the reused
	 *        states of the inactive Includes false for.
	 * @param inactiveIncludes are the currently inactive Includes (you get them
	 *        with <tt>getInactiveIncludesForArticle(...)</tt> of the same
	 *        article you need the active Includes from.
	 * @param activeIncludes are the active Includes
	 */
	private void resetReusedStateOfInactiveIncludeTargets(String title,
			Collection<Section<Include>> inactiveIncludes,
			Collection<Section<Include>> activeIncludes) {

		Set<Section<?>> inactiveIncludeTargets = new HashSet<Section<?>>();
		Set<Section<?>> activeIncludeTargets = new HashSet<Section<?>>();

		for (Section<Include> inaInc : inactiveIncludes) {
			if (inaInc.getTitle().equals(title) && src2targets.containsKey(inaInc)) {
				inactiveIncludeTargets.addAll(src2targets.get(inaInc));
			}
		}
		for (Section<Include> aInc : activeIncludes) {
			if (aInc.getTitle().equals(title) && src2targets.containsKey(aInc)) {
				activeIncludeTargets.addAll(src2targets.get(aInc));
			}
		}

		inactiveIncludeTargets.removeAll(activeIncludeTargets);

		for (Section<?> inaIncTar : inactiveIncludeTargets) {
			inaIncTar.setReusedByRecursively(title, false);
		}
	}

	/**
	 * Unregisters, respectively removes, the given Includes from the
	 * KnowWEIncludeManager.
	 * 
	 * @created 29.05.2010
	 * @param includes are the Includes you want to unregister/remove.
	 */
	private void unregisterIncludes(Collection<Section<Include>> includes) {

		for (Section<Include> inc : includes) {
			// remove from maps...
			src2targets.remove(inc);
			// src2lastTargets.remove(inc);
			// also delete the Include from the set of Includes of
			// the target article
			if (Include.getIncludeAddress(inc) != null) {
				getIncludingSectionsForArticle(
						Include.getIncludeAddress(inc).getTargetArticle()).remove(inc);
			}
		}

	}

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		ArrayList<Class<? extends Event>> events = new ArrayList<Class<? extends Event>>(3);
		events.add(KDOMCreatedEvent.class);
		events.add(ArticleCreatedEvent.class);
		events.add(UpdatingDependenciesEvent.class);
		return events;
	}

	@Override
	public void notify(Event event) {
		if (event instanceof KDOMCreatedEvent) {
			KDOMCreatedEvent ev = (KDOMCreatedEvent) event;
			resetReusedStateOfInactiveIncludeTargets(ev.getArticle().getTitle(),
					getInactiveIncludesForArticle(ev.getArticle()),
					getActiveIncludesForArticle(ev.getArticle()));
		}
		else if (event instanceof ArticleCreatedEvent) {
			ArticleCreatedEvent ev = (ArticleCreatedEvent) event;
			unregisterIncludes(getInactiveIncludesForArticle(ev.getArticle()));
		}
		else if (event instanceof UpdatingDependenciesEvent) {
			UpdatingDependenciesEvent ev = (UpdatingDependenciesEvent) event;
			updateIncludesToArticle(ev.getArticle());
		}
	}

}
