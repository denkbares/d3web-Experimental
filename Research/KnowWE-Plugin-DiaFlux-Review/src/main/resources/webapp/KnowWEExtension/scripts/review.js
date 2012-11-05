if (typeof DiaFlux == "undefined" || !DiaFlux) {
    var DiaFlux = {};
}

DiaFlux.ReviewTool = (function(flow) {
	var panel;
	var flowDOM;
	
	var review;
	
	//if an old version of the page is shown, then disable editing
	var editable = true;
	var modState = false;
	var currentEdit = null;
	var currentSelection= null;
	var filters= "";
	
	var PRIORITIES = ['low', 'important', 'critical'];
	var PRIORITIES_SELECTOR = "." + PRIORITIES.join(', .');
	var PRIORITY_CLASSES = PRIORITIES.join(' ');
	var STATES = ['open', 'resolved'];
	
	return {
		
		init : function(){
			flowDOM = jq$(flow.dom);
			panel = jq$("<div>", {id: flow.kdomid + "-review" ,"class" : "reviewPanel"}).appendTo(flowDOM.parent());
			var pagever = KNOWWE.helper.gup('version');
			if (pagever && pagever != KNOWWE.helper.getPageVersion()){
				editable = false;
			}
			
			if (editable){
				this.createToolbar(panel);
			} else {
				jq$("<div>", {'class': 'information'}).text('An outdated version of this article is shown. You can not edit the review.').appendTo(panel);
			}
			this.getReview(flow, panel);
			
			this.addMarkingHandler();
			
			this.bindUnloadFunction(panel);

		},
		
		bindUnloadFunction : function(panel) {
			jq$(window).bind('beforeunload', function(){
				if (this.isModified())
					return "edit.areyousure".localize();
			}.bind(this));
		},
		
		createToolbar : function(panel){
			var toolbar = jq$("<div>", {"class":"reviewToolbar"}).appendTo(panel);
			jq$("<a>",{"class":"saveReview", title: "Save review"})
			.appendTo(toolbar)
			.on('click', 
				function(event){
					this.saveReview(review, flow.kdomid);
					this.setModified(false);
				}.bind(this)
			);
			
			jq$("<a>",{"class":"addItem", title: "Add new review item"})
			.appendTo(toolbar)
			.on('click', 
				function(event){
					this.addItem(review, panel);
				}.bind(this)
			);
			
//			var filter = jq$("<div>", {"class":"itemFilter"}).appendTo(panel);
//			this.createFilterPanel(filter, PRIORITIES);
//			this.createFilterPanel(filter, STATES, [false, true]);
			
		},
		
//		createFilterPanel : function(parent, buttons, states){
//			var panel = jq$("<div>", {"class":"filterPanel"}).appendTo(parent);
//			var tool = this;
//			for (var i = 0; i < buttons.length; i++) {
//				var classes = "filterButton " + buttons[i];
//				if (!states || (states && states[i])) {
//					classes += " pressed";
//					filters += " ." + buttons[i];
//				}
//				
//				jq$("<div>", {"class":  classes, title: buttons[i]}).appendTo(panel)
//				.on('click', function(e){
//					var button = jq$(this);
//					button.toggleClass('pressed');
//					if (button.hasClass(button.attr('title'))){
//						filters += " ." + button.attr('title');
//					} else {
//						filters.replace(" ." + button.attr('title'), "");
//					}
//					tool.refreshFilters();
//				});
//			}
//		},
//		
//		refreshFilters : function(){
//			panel.find('li').filter(filters).each(function(index, elem){
//				elem.hide();
//			});
//			
//		},
		
		addItem : function(review, panel){
			var item = new DiaFlux.ReviewItem(review.getNextId(), new Date(), KNOWWE.helper.getUsername());
			item.setPageRev(KNOWWE.helper.getPageVersion());
			review.addItem(item);
			this.setModified(true);
			this.renderItem(item, panel.find('ul'));
		},
		
		saveReview : function(review, kdomid){
			var params = {
				action : 'SaveReviewAction',
				SectionID: kdomid,
		        review: review.toXML()
			};
			
			var options = {
				url: KNOWWE.core.util.getURL( params )
				
		    };
			
			KNOWWE.core.util.updateProcessingState(1);
			try{
				new _KA(options).send();
				
			} catch(e) {}
			KNOWWE.core.util.updateProcessingState(-1);
			this.setModified(false);
		},
		
		addMarkingHandler : function(){
			flowDOM.find('.Node').on('click', 
				function(event) {
					if (this.isEditing()){
						var prio  = currentEdit.data('item').getPriority();
						this.mark(event.delegateTarget, prio, true);
						
					}
				}.bind(this)
			);
			flowDOM.find('.rule_selector').on('click', 
				function(event) {
					if (this.isEditing()){
						var prio  = currentEdit.data('item').getPriority();
						var set = jq$(event.delegateTarget).closest('.Rule'); 
						this.mark(set, prio, true);
					}
				}.bind(this)
			);
		},
		
		isEditing : function(){
			if (currentEdit)
				return true;
			else 
				return false;
		},
		
		isModified : function(){
			return modState;
		},
		
		setModified : function(modified){
			modState = modified;
			if (modState) {
				panel.find('a.saveReview').addClass('modified');
			} else {
				panel.find('a.saveReview').removeClass('modified');
			}
		},

		getReview : function(flow, panel){
			var params = {
				action : 'LoadReviewAction',
				SectionID: flow.kdomid
			};
			var that = this;
			
			var options = {
				url: KNOWWE.core.util.getURL( params ),
		        response : {
		            fn: function(){that.loadReview(this.responseXML, panel);}
		        }
		    };
		    new _KA(options).send();
			
		},
		
		loadReview : function(response, panel){	
			review = DiaFlux.Review.fromXML(response);
			review.checkItems(flow);
			this.showReview(review, panel);
		},
		
		showReview : function(review, panel){
			var list = jq$("<ul>", {"class":"reviewList"}).appendTo(panel);
			var items = review.getItems();
			
			for (var i = 0; i < items.length; i++){
				this.renderItem(items[i], list);
			}
			this.showAllMarkings();
			var itemToSelect = KNOWWE.helper.gup('item');
			if (itemToSelect) {
				this.selectById(itemToSelect);
			}
			
			
		},
		
		renderItem : function(item, list){
			var li = jq$("<li>", {id:flow.kdomid + "-item-" + item.getId()})
				.appendTo(list)
				.on('click', function(){this.handleSelection(li);}.bind(this))
				.on('dblclick', function() {if (editable)jq$(this).find('a.editItem').click();});
			li.data('item', item);
			this.renderItemContent(item, li);
		},
		
		renderItemContent : function(item, li){
			li.empty();
			var container = jq$("<div>", {"class" : "reviewItem"}).appendTo(li);
			var date = item.getDate();
			var info = "#" + item.getId() + " by " + item.getAuthor() + " at " + date.toLocaleString(); 
			var infoDiv = jq$("<div class='reviewItemInfo'>" + info + "</div>").appendTo(container);
			var statusDiv = jq$("<div>", {"class" : "reviewItemStatus"}).appendTo(infoDiv);
			var priorityDiv = jq$("<div>", {"class" : "reviewItemPriority"}).appendTo(statusDiv);
			var stateDiv = jq$("<div>", {"class" : "reviewItemState"}).appendTo(statusDiv);

			this.renderIcon(priorityDiv, PRIORITIES, item.getPriority());
			this.renderIcon(stateDiv, STATES, item.getState());
			jq$("<div class='reviewText'>" + DiaFlux.Review.escapeHTML(item.getDescription()) + "</div>").appendTo(container);
			if (editable){
				this.addItemActions(item, container);
			}
			
			if (item.isOutdated()){
				jq$('<div>', {'class': 'information'}).html('This review item can not be displayed properly in this version. ').appendTo(container)
				.append(jq$('<a>', {href: this.createLink(item)}).text('Open the according version of the article.'));
			}

		}, 
		
		createLink : function(item){
			var page = KNOWWE.helper.gup('page');
			return 'Wiki.jsp?page=' + page +'&version=' + item.getPageRev() + '&item=' +item.getId();
		},
			
		handleSelection : function(li){
			if (this.isEditing()) return;
			if (currentSelection == li) {
				this.deselect();
				this.showAllMarkings();
			} else {
				this.select(li);
			}
			
		},
		
		select : function(li){
			currentSelection = li;
			li.closest('ul').find('li').removeClass('selected');
			li.addClass('selected');
			this.showMarkings(li);
		},
		
		selectById : function(id){
			var li = panel.find('li#'+flow.kdomid + '-item-' + id);
			if (li) this.select(li);
		},
		
		deselect : function(){
			currentSelection.removeClass('selected');
			this.removeAllMarkings(currentSelection);
			currentSelection = null;
			
		},
		
		collectMarkings : function(li){
			return jq$.map(flowDOM.find(PRIORITIES_SELECTOR), function(val, key) {
				return val.getAttribute('id');
			});
			
		},
		
		showMarkings : function(li){
			this.removeAllMarkings();
			var item=li.data('item');
			var markings = item.getMarkings();
			
			for (var i = 0; i < markings.length; i++){
				this.markById(markings[i], item.getPriority(), false);
			}
		},
		
		showAllMarkings : function() {
			var allMarks = {};
			var items = review.getItems();
			for (var i = 0; i <items.length; i++) {
				var prio = items[i].getPriority()
				var markings = items[i].getMarkings();
				for (var j = 0; j < markings.length; j++){
					allMarks[markings[j]] = Math.max(allMarks[markings[j]] || 0, prio);
				}
			}
		
			this.removeAllMarkings();

			for (var id in allMarks) {
			  if (allMarks.hasOwnProperty(id)) {
				  this.markById(id,  allMarks[id], false);
			  }
			}
			
		},
		
		markById : function(id, prio, toggle){
			var flowElem = flow.findObject(id);
			if (flowElem) this.mark(flowElem.getDOM(), prio, false);
		},
		
		mark : function(elem, prio, toggle){
			var colorclass = PRIORITIES[prio];
			if (toggle) {
				elem.removeClass(PRIORITY_CLASSES).addClass(colorclass);
			} else {
				elem.addClass(colorclass);
			}
		},

		removeAllMarkings : function(){
			flowDOM.find(PRIORITIES_SELECTOR).removeClass(PRIORITY_CLASSES);
		},
		
		addItemActions : function(item, li){
			
			var actionsDiv = jq$("<div>", {"class":"reviewActions"}).appendTo(li);
			jq$("<a>", {"class":"editItem"}).appendTo(actionsDiv).on('click', this.editItem.bind(this));
			jq$("<a>", {"class":"deleteItem"}).appendTo(actionsDiv).on('click', this.deleteItem.bind(this));
			
		},
		
		editItem : function(event){
			if (currentEdit) return; //allow only one edit
			var itemEl = jq$(event.delegateTarget).closest('li');
			itemEl.addClass('editing');
			currentEdit = itemEl;
			this.select(itemEl);
			var item = itemEl.data('item');
			
			var prioDiv = itemEl.find('.reviewItemPriority');
			this.createImageDropdown(prioDiv, 'itemPrio', PRIORITIES, item.getPriority(), function(i){
				item.setPriority(i);
			});
			
			var stateDiv = itemEl.find('.reviewItemState');
			this.createImageDropdown(stateDiv, 'itemState', STATES, item.getState(), function(i){
				if (i == 0){
					item.setResolvedPageRev(-1);
				} else {
					var rev = KNOWWE.helper.getPageVersion();
					item.setResolvedPageRev(rev);
				}
			});
			
			var parent = itemEl.find('.reviewText').empty();
			var textArea = jq$('<textarea>', {rows: 5}).val(item.getDescription()).appendTo(parent).focus();
			
			var actionsDiv = jq$('<div>', { "class": "editActions"}).appendTo(parent);
			
			var endEdit = function(){
				currentEdit = null;
				itemEl.removeClass('editing');
				this.renderItemContent(item, itemEl);
			}.bind(this);
			
			jq$('<a>', {'class':'saveChanges'}).text('Ok ').on('click',
				function() {
					item.setDescription(textArea.val());
					item.setMarkings(this.collectMarkings(itemEl));
					item.setPriority(prioDiv.find('input[name=itemPrio]:checked').val());
					var state = stateDiv.find('input[name=itemState]:checked').val()
					if (state ==1) {
						item.setResolvedPageRev(KNOWWE.helper.getPageVersion());
					} else {
						item.setResolvedPageRev(-1);
					}
					this.setModified(true);
					endEdit();
				}.bind(this)
			).appendTo(actionsDiv);
			
			jq$('<a>',{'class':'discardChanges'}).text('Cancel').on('click', endEdit).appendTo(actionsDiv);
			event.preventDefault();
		},
		
		createImageDropdown : function(parent, name, options, checked, callback){
			var selectParent = jq$('<div>', {"class": 'image-dropdown'}).appendTo(parent);
			
			var tool = this;
			for (var i = 0; i < options.length; i++) {
				var config = {
						type: 'radio',
						id: options[i],
						name: name,
						value: i,
						checked : i == checked
				};
				jq$('<input>', config)
				.change(function(e){
					var $this = jq$(this);
					var index = $this.val();
					if ($this.attr('checked')){
						callback(index);
					}
					
					tool.renderIcon(parent, options, index);
					selectParent.fadeOut('fast');
					e.stopPropagation();
//					selectParent.slideToggle();
				}).appendTo(selectParent);
				selectParent.append(jq$('<label>', {'for': options[i]}).text(options[i]));
				
				
			}
			
			parent.on('click', function(e) {
				selectParent.fadeIn('fast');
//				e.stopPropagation();
			});
			this.renderIcon(parent, options, checked);
			
		},
		
		renderIcon : function(parent, options, checked){
			parent.find('.imageLabel').remove();
			jq$("<label>", {'class' :'imageLabel', 'for': options[checked], title : options[checked]}).prependTo(parent);
		},
		
		deleteItem : function(event){
			var li = jq$(event.delegateTarget).closest('li');
			var item = li.data('item');
			review.removeItem(item);
			li.remove();
			this.deselect();
			this.setModified(true);
			
		}
	}
});

/**
 * Review class
 * Stores items
 */
DiaFlux.Review = function(flowName, idCounter, items){
	
	return {
		
		getFlowName : function() {
			return flowName;
		},

		getItems : function() {
			return items;
		},

		addItem : function(item) {
			items.push(item);
		},

		removeItem : function(item) {
			var index = items.indexOf(item);
			if (index != -1)
			items.splice(index, 1);
		},

		getIdCounter : function() {
			return idCounter;
		},

		getNextId : function() {
			return ++idCounter;
		},
		
		checkItems : function(flow) {
			for(var i = 0; i < items.length; i++) {
				items[i].check(flow);
			}
		},

		toXML : function() {
			var xml = '<?xml version="1.0" encoding="UTF-8"?>\n';
			xml += '<review idCounter="' + this.getIdCounter() +'" flowName="' + this.getFlowName() + '">\n';

			for (var i = 0; i < items.length; i++){
				xml += items[i].toXML();
			}
			
			xml += '</review>\n';
			return xml;
			
		}
		
	}
}

DiaFlux.Review.escapeHTML = function(string) {
	return jq$('<div>').text(string).html();
}


DiaFlux.Review.fromXML = function(xml){
	var idCounter = parseInt(xml.getElementsByTagName('review')[0].getAttribute('idCounter'));
	var flow = xml.getElementsByTagName('review')[0].getAttribute('flowName');
	var xmlItems = xml.getElementsByTagName('item');
	var items = [];
	
	for (var i = 0; i < xmlItems.length; i++){
		items.push(DiaFlux.ReviewItem.fromXML(xmlItems[i]));
	}
	
	return new DiaFlux.Review(flow, idCounter, items);
}


/**
 * Review item class
 * Stores one remark
 */
DiaFlux.ReviewItem = function(id, date, author){
	var description = "";
	var markings = [];
	var pageRev = -1;
	var priority = 1;
	var state = 0;
	var resolvedPageRev = -1;
	var outdated = false;

	return {
		
		getId : function() {
			return id;
		},
		
		getDate : function() {
			return date;
		},
		
		getAuthor : function() {
			return author;
		},
		
		setPageRev : function(newPageRev) {
			pageRev = newPageRev;
		},
		
		getPageRev : function() {
			return pageRev;
		},
		
		setResolvedPageRev : function(rev) {
			resolvedPageRev = rev;
			if (resolvedPageRev != -1) {
				state = 1;
			}
		},
		
		getResolvedPageRev : function() {
			return resolvedPageRev;
		},
		
		setDescription : function(desc) {
			description = desc;
		},
		
		getDescription : function() {
			return description;
		},
		
		setPriority : function(prio) {
			priority = prio;
		},
		
		getPriority : function() {
			return priority;
		},
		
		getState : function() {
			return state;
		},
		
		setMarkings : function(marks) {
			markings = marks;
		},
		
		getMarkings : function() {
			return markings;
		},

		check : function(flow){
			for (var i = 0; i < markings.length; i++){
				if (!flow.findObject(markings[i])){
					outdated = true;
				}
			}
		},
		
		isOutdated : function() {
			return outdated;
		},
		
		toXML : function() {
			var xml = '<item id="' + this.getId() + '" ';
			xml += 'date="' + this.getDate() + '" ';
			xml += 'author="' + this.getAuthor() +'" ';
			xml += 'pageRev="' + this.getPageRev() +'" ';
			xml += 'priority="' + this.getPriority() + '" ';
			xml += 'resolvedPageRev="' + this.getResolvedPageRev() + '" ';
			xml += '>\n';
			xml += '<description>' + DiaFlux.Review.escapeHTML(this.getDescription()) + '</description>\n';
			xml += '<markings>\n';
			
			var markings = this.getMarkings();
			
			for (var i = 0; i < markings.length; i++){
				xml += '<mark id="' + markings[i] + '"/>\n'
			}
			xml += '</markings>\n';
			xml += '</item>\n';
			return xml;
		}
	}
}

DiaFlux.ReviewItem.fromXML = function(itemXML){
	var id = parseInt(itemXML.getAttribute('id'));
	var date = new Date(itemXML.getAttribute('date'));
	var author = itemXML.getAttribute('author');
	var pageRev = itemXML.getAttribute('pageRev');
	var resolvedPageRev = itemXML.getAttribute('resolvedPageRev');
	var priority = itemXML.getAttribute('priority');
	var descriptionTag = itemXML.getElementsByTagName('description')[0];
	var description = "";
	if (descriptionTag.firstChild){
		description = descriptionTag.firstChild.nodeValue;
	} 
		
	var markXML = itemXML.getElementsByTagName('markings')[0].getElementsByTagName('mark');
	var markings = [];
	
	for (var i = 0; i < markXML.length;i++){
		markings.push(markXML[i].getAttribute('id'));
	}
	
	var item = new DiaFlux.ReviewItem(id, date, author);
	item.setDescription(description);
	item.setMarkings(markings);
	item.setPageRev(pageRev);
	item.setResolvedPageRev(resolvedPageRev);
	item.setPriority(priority);
	
	return item; 
	
}

KNOWWE.helper.observer.subscribe("flowchartrendered", function(){
	new DiaFlux.ReviewTool(this.flow).init();
});

KNOWWE.helper.observer.subscribe("flowchartlinked", function(){
	var nodes = jq$(this.flow.dom).find('.Node a>div:first-child').unwrap();
});

