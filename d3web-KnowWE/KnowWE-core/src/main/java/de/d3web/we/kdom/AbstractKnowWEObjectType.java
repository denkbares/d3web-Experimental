/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.we.kdom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.d3web.report.Message;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.module.semantic.owl.IntermediateOwlObject;
import de.d3web.we.utils.KnowWEUtils;

public abstract class AbstractKnowWEObjectType implements KnowWEObjectType {
	
	
	/**
	 * This is just a (conventional) key under which messages can be 
	 * stored (and then be found again) in the sectionStore
	 * 
	 */
	public static final String MESSAGES_STORE_KEY = "messages";

	/**
	 * the children types of the type. Used to serve the getAllowedChildrenTypes of 
	 * the KnowWEObjectType interface
	 * @see KnowWEObjectType#getAllowedChildrenTypes()
	 * 
	 */
	protected List<KnowWEObjectType> childrenTypes = new ArrayList<KnowWEObjectType>();
	
	
	//protected List<KnowWEObjectType> priorityChildrenTypes = new ArrayList<KnowWEObjectType>();
	
	
	
	/**
	 * Manages the subtreeHandlers which are registered to this type
	 * @see ReviseSubTreeHandler
	 */
	protected List<ReviseSubTreeHandler> subtreeHandler = new ArrayList<ReviseSubTreeHandler>();
	
	
	
	
	/**
	 * types can be activated and decactivated in KnowWE
	 * this field is holding the current state
	 */
	protected boolean isActivated = true;
	
	/**
	 * a flag for the updating mechanism, which mananges translations to explicit knowledge formats
	 */
	private boolean isNotRecyclable = false;
	
	
	/**
	 * specifies whether there is a enumeration of siblings defined for this type
	 * (e.g., to add line numbers to line-based sections)
	 */
	protected boolean isNumberedType = false;
	
	
	public boolean isNumberedType() {
		return isNumberedType;
	}

	public void setNumberedType(boolean isNumberedType) {
		this.isNumberedType = isNumberedType;
	}

	
	/**
	 * The sectionFinder of this type, used to serve the getSectionFinder-method
	 * of the KnowWEObjectType interface
	 * @see KnowWEObjectType#getSectioner()
	 */
	protected SectionFinder sectionFinder;
	
	
	
	/**
	 * Allows to set a specific sectionFinder for this type
	 * 
	 * @param sectionFinder
	 */
	public void setSectionFinder(SectionFinder sectionFinder) {
		this.sectionFinder = sectionFinder;
	}


	/**
	 * allows to set a custom renderer for a type (at initialization)
	 * if a custom renderer is set, it is used to present the type content
	 * in the wiki view
	 */
	protected KnowWEDomRenderer customRenderer = null;

	/**
	 * constructor calling init() which is abstract
	 * 
	 */
	public AbstractKnowWEObjectType() {
		
		init();
	}
	
	
	/**
	 * Returns the list of the registered ReviseSubtreeHandlers
	 * 
	 * @return list of handlers
	 */
	public List<ReviseSubTreeHandler> getSubtreeHandler() {
		return subtreeHandler;
	}


	/**
	 * Can be used to register new ReviseSubtreeHandlers
	 * 
	 * @param handler
	 */
	public void addReviseSubtreeHandler(ReviseSubTreeHandler handler) {
		subtreeHandler.add(handler);
	}
	
	/**
	 * A mechanism to help clean up old information, when a new version of a page is saved
	 * 
	 * @param articleName
	 * @param clearedTypes
	 */
	public void clearTypeStoreRecursivly(String articleName, Set<KnowWEType> clearedTypes) {
		cleanStoredInfos(articleName);
		clearedTypes.add(this);
		
		for(KnowWEObjectType type : childrenTypes) {
			if(type instanceof AbstractKnowWEObjectType && !clearedTypes.contains(type)) {
				((AbstractKnowWEObjectType)type).clearTypeStoreRecursivly(articleName,clearedTypes);
			}
		}
	}
	
	
	/**
	 * Returns all the messages stored for this section
	 * 
	 * @param s
	 * @return
	 */
	public List<Message> getMessages(Section s) {
		return toMessages(KnowWEUtils.getStoredObject(KnowWEEnvironment.DEFAULT_WEB,
				s.getTitle(), s.getId(), MESSAGES_STORE_KEY), s);
		
	}
	
	/**
	 * Returns the messages old stored messages
	 * 
	 * @param s
	 * @return
	 */
	public List<Message> getOldMessages(Section s) {
		return toMessages(KnowWEUtils.getOldStoredObject(KnowWEEnvironment.DEFAULT_WEB,
				s.getTitle(), s.getId(), MESSAGES_STORE_KEY), s);
		
	}
	
	private List<Message> toMessages(Object o, Section s) {
		if(o instanceof List) {
			return (List<Message>) o;
		}
		if (o == null) {
			List<Message> msg = new ArrayList<Message>();
			storeMessages(s, msg);
			return msg;
		}
		return null;
	}
	
	/**
	 * Stores a list of messages under to message-store-key
	 * 
	 * @param s
	 * @param messages
	 */
	public void storeMessages(Section s, List<Message> messages) {
		KnowWEUtils.storeSectionInfo(KnowWEEnvironment.DEFAULT_WEB,
				s.getTitle(), s.getId(), MESSAGES_STORE_KEY, messages);
	}

	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.KnowWEObjectType#deactivateType()
	 */
	public void deactivateType() {
		isActivated = false;
	}
	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.KnowWEObjectType#activateType()
	 */
	public void activateType() {
		isActivated = true;
	}	
	
	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.KnowWEObjectType#getActivationStatus()
	 */
	public boolean getActivationStatus() {
		return isActivated;
	}
	
	public void findTypeInstances(Class clazz, List<KnowWEObjectType> instances) {
		if (this.getClass().equals(clazz)) {
			instances.add(this);
		}
		for (KnowWEObjectType knowWEObjectType : childrenTypes) {
			knowWEObjectType.findTypeInstances(clazz, instances);
		}
	}

	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.KnowWEObjectType#getName()
	 */
	@Override
	public String getName() {
		return this.getClass().getName().substring(
				this.getClass().getName().lastIndexOf('.') + 1);
	}

	protected abstract void init();

	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.KnowWEObjectType#getSectioner()
	 */
	@Override
	public SectionFinder getSectioner() {
		if (isActivated) {
			return sectionFinder;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.KnowWEObjectType#getAllowedChildrenTypes()
	 */
	@Override
	public List<KnowWEObjectType> getAllowedChildrenTypes() {
		return this.childrenTypes;
	}
	

	@Override
	@Deprecated
	public Collection<Section> getAllSectionsOfType() {
		return null;
	}


//	public static String spanColorTitle(String text, String color, String title) {
//		return KnowWEEnvironment.HTML_ST + "span title='" + title
//				+ "' style='background-color:" + color + ";'"
//				+ KnowWEEnvironment.HTML_GT + text + KnowWEEnvironment.HTML_ST
//				+ "/span" + KnowWEEnvironment.HTML_GT;
//	}


	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.KnowWEObjectType#reviseSubtree(de.d3web.we.kdom.Section)
	 */
	@Override
	public final void reviseSubtree(Section s) {
		for(ReviseSubTreeHandler handler : subtreeHandler) {
			handler.reviseSubtree(s);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.KnowWEType#getRenderer()
	 */
	@Override
	public KnowWEDomRenderer getRenderer() {
		if (customRenderer != null)
			return customRenderer;

		return getDefaultRenderer();
	}

	private KnowWEDomRenderer getDefaultRenderer() {
		return DelegateRenderer.getInstance();
	}

	/**
	 * Allows to set a custom renderer for this type
	 * 
	 * @param renderer
	 */
	public void setCustomRenderer(KnowWEDomRenderer renderer) {
		this.customRenderer = renderer;
	}

	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.KnowWEObjectType#getOwl(de.d3web.we.kdom.Section)
	 */
	public IntermediateOwlObject getOwl(Section s) {
		IntermediateOwlObject io = new IntermediateOwlObject();
		
		for (Section cur : s.getChildren()) {
			if (cur.getObjectType() instanceof AbstractKnowWEObjectType) {
				AbstractKnowWEObjectType handler = (AbstractKnowWEObjectType) cur
						.getObjectType();
				io.merge(handler.getOwl(cur));
			}
		}
		return io;
	}
	
	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.KnowWEType#isAssignableFromType(java.lang.Class)
	 */
	@Override
	public boolean isAssignableFromType(Class<? extends KnowWEObjectType> clazz) {
		return clazz.isAssignableFrom(this.getClass());
	}

	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.KnowWEType#isType(java.lang.Class)
	 */
	@Override
	public boolean isType(Class<? extends KnowWEObjectType> clazz) {
		return clazz.equals(this.getClass());
	}
	
	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.KnowWEObjectType#isLeafType()
	 */
	@Override
	public boolean isLeafType() {
		List<KnowWEObjectType> types = getAllowedChildrenTypes();
		return types == null || types.size() == 0;
	}
	
	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.KnowWEObjectType#isNotRecyclable()
	 */
	@Override
	public boolean isNotRecyclable() {
		return isNotRecyclable;
	}
	
	/* (non-Javadoc)
	 * @see de.d3web.we.kdom.KnowWEObjectType#setNotRecyclable(boolean)
	 */
	@Override
	public final void setNotRecyclable(boolean notRecyclable) {
		this.isNotRecyclable = notRecyclable;
		for (KnowWEObjectType type:childrenTypes) {
			type.setNotRecyclable(notRecyclable);
		}
		
	}
}
