if (typeof DiaFlux == "undefined" || !DiaFlux) {
    var DiaFlux = {};
}

DiaFlux.ReviewTool = (function(flow) {
	var panel;
	var flowDOM;
	
	var review;
	
	var modState = false;
	var currentEdit = null;
	var currentSelection= null;
	
	var PRIORITIES = ['low', 'important', 'critical'];
	
	return {
		
		init : function(){
			flowDOM = jq$(flow.dom);
			panel = jq$("<div>", {id: flow.kdomid + "-review" ,"class" : "reviewPanel"}).appendTo(flowDOM);
			this.addToolbar(panel);
			this.getReview(flow, panel);
			
			this.bindUnloadFunction(panel);
			
			this.addMarkingHandler();
			
		},
		
		bindUnloadFunction : function(panel) {
			jq$(window).bind('beforeunload', function(){
				if (this.isModified())
					return "edit.areyousure".localize();
			}.bind(this));
		},
		
		addToolbar : function(panel){
			var toolbar = jq$("<div>", {"class":"reviewToolbar"}).appendTo(panel);
			jq$("<a>",{"class":"saveReview"})
			.appendTo(toolbar)
			.on('click', 
				function(event){
					this.saveReview(review, flow.kdomid);
					this.setModified(false);
				}.bind(this)
			);
			
			jq$("<a>",{"class":"addItem"})
			.appendTo(toolbar)
			.on('click', 
				function(event){
					this.addItem(review, panel);
				}.bind(this)
			);
			
		},
		
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
					if (this.isEditing(flowDOM.find('.reviewList'))){
						this.mark(event.delegateTarget, true);
						
					}
				}.bind(this)
			);
			flowDOM.find('.rule_selector').on('click', 
				function(event) {
					if (this.isEditing(flowDOM.find('.reviewList'))){
						var set = jq$(event.delegateTarget).closest('.Rule'); 
						this.mark(set, true);
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
		
		mark : function(node, toggle){
			if (toggle) {
				node.toggleClass('reviewMarked');
			} else {
				node.addClass('reviewMarked');
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
			this.showReview(review, panel);
		},
		
		showReview : function(review, panel){
			var list = jq$("<ul>", {"class":"reviewList"}).appendTo(panel);
			var items = review.getItems();
			
			for (var i = 0; i < items.length; i++){
				this.renderItem(items[i], list);
			}
			
		},
		
		renderItem : function(item, list){
			var li = jq$("<li>", {"class" : "reviewItem"})
				.appendTo(list)
				.on('click', function(){this.handleSelection(li);}.bind(this))
				.on('dblclick', function() {jq$(this).find('a.editItem').click();});
			li.data('item', item);
			this.renderItemContent(item, li);
		},
		
		renderItemContent : function(item, li){
			li.empty();
			var container = jq$("<div>").appendTo(li);
			var date = item.getDate();
			var info = "#" + item.getId() + " by " + item.getAuthor() + " at " + date.toLocaleString(); 
			var infoDiv = jq$("<div class='reviewItemInfo'>" + info + "</div>").appendTo(container);
			var statusDiv = jq$("<div>", {"class" : "reviewItemStatus"}).appendTo(infoDiv);
			var priorityDiv = jq$("<div>", {"class" : "reviewItemPriority"}).appendTo(statusDiv);
			jq$("<label for='" + PRIORITIES[item.getPriority()] + "' alt='" + PRIORITIES[item.getPriority()] + "'></label>").appendTo(priorityDiv);
			
			jq$("<div class='reviewText'>" + item.getDescription() + "</div>").appendTo(container);
			
			this.addItemActions(item, li);

		},
		
		createImageDropdown : function(parent, name, options, checked){
			parent.addClass('image-dropdown');
			for (var i = 0; i < options.length; i++) {
				var state = "";
				if (i == checked){
					state = "checked='checked'";
				}
				jq$('<input type="radio" id="' + options[i] + '" name="' + name + '" value="' + i + '" ' + state + '/><label for="' + options[i] + '"></label>').appendTo(parent);
					
			}
			
		},
		
		handleSelection : function(li){
			if (this.isEditing()) return;
			if (currentSelection == li) {
				this.deselect();
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
		
		deselect : function(){
			currentSelection.removeClass('selected');
			this.removeAllMarkings(currentSelection);
			currentSelection = null;
			
		},
		
		collectMarkings : function(li){
			return jq$.map(flowDOM.find('.reviewMarked'), function(val, key) {
				return val.getAttribute('id');
			});
			
		},
		
		showMarkings : function(li){
			this.removeAllMarkings(li);
			var markings = li.data('item').getMarkings();
			
			for (var i = 0; i < markings.length; i++){
				var flowElem = flow.findObject(markings[i]);
				if (flowElem) this.mark(flowElem.getDOM(), false);
			}
		},

		removeAllMarkings : function(elem){
			flowDOM.find('.reviewMarked').removeClass('reviewMarked');
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
			
			var prioDiv = itemEl.find('.reviewItemPriority').empty();
			this.createImageDropdown(prioDiv, 'itemPrio', PRIORITIES, item.getPriority());
			
			var parent = itemEl.find('.reviewText').empty();
			var textArea = jq$('<textarea>', {rows: 5}).val(item.getDescription()).appendTo(parent).focus();
			
			var actionsDiv = jq$('<div>', { "class": "editActions"}).appendTo(parent);
			
			var endEdit = function(){
				currentEdit = null;
				itemEl.removeClass('editing');
				this.renderItemContent(item, itemEl);
			}.bind(this);
			
			jq$('<a class="saveChanges">Ok</a> ').on('click',
				function() {
					item.setDescription(textArea.val());
					item.setMarkings(this.collectMarkings(itemEl));
					item.setPriority(prioDiv.find('input[name=itemPrio]:checked').val());
					this.setModified(true);
					endEdit();
				}.bind(this)
			).appendTo(actionsDiv);
			
			jq$('<a class="discardChanges">Cancel</a>').on('click', endEdit).appendTo(actionsDiv);
			event.preventDefault();
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
		
		setPageRev : function(pageRev) {
			pageRev = pageRev;
		},
		
		getPageRev : function() {
			return pageRev;
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
		
		setMarkings : function(marks) {
			markings = marks;
		},
		
		getMarkings : function() {
			return markings;
		},
		
		toXML : function() {
			var xml = '<item id="' + this.getId() +'" date="' + this.getDate() + '" author="' + this.getAuthor() +'" pageRev="' + this.getPageRev() +'" priority="' + this.getPriority() + '">\n';
			xml += '<description>' + this.getDescription() + '</description>\n';
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
	var priority = itemXML.getAttribute('priority');
	var descriptionTag = itemXML.getElementsByTagName('description')[0];
	var description = "";
	if (descriptionTag.firstChild){
		description = descriptionTag.firstChild.nodeValue
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
	item.setPriority(priority);
	
	return item; 
	
}

KNOWWE.helper.observer.subscribe("flowchartrendered", function(){
	new DiaFlux.ReviewTool(this.flow).init();
});

KNOWWE.helper.observer.subscribe("flowchartlinked", function(){
	var nodes = jq$(this.flow.dom).find('.Node a>div:first-child').unwrap();
});

