if (typeof DiaFlux == "undefined" || !DiaFlux) {
    var DiaFlux = {};
}

DiaFlux.ReviewTool = {};

DiaFlux.ReviewTool.addReviewPanel = function(){
	var flow = this.flow;
	var flowDOM = jq$(flow.dom);
	var panel = jq$("<div>", {id: flow.kdomid + "-review" ,"class" : "reviewPanel"}).appendTo(flowDOM);
	DiaFlux.ReviewTool.addToolbar(panel);
	panel.data('flow', flow);
	DiaFlux.ReviewTool.getReview(flow, panel);
	
	DiaFlux.ReviewTool.removeNodeLinks(flowDOM);
	DiaFlux.ReviewTool.addMarkingHandler(flowDOM);
	
}

DiaFlux.ReviewTool.addToolbar = function(panel){
	var toolbar = jq$("<div>", {"class":"reviewToolbar"}).appendTo(panel);
	jq$("<a>",{"class":"saveReview"})
	.appendTo(toolbar)
	.on('click', 
		function(event){
			DiaFlux.ReviewTool.saveReview(panel.data('review'), panel.data('flow').kdomid);
		}
	);
	
}

DiaFlux.ReviewTool.saveReview = function(review, kdomid){
	var params = {
		action : 'SaveReviewAction',
        kdomid: kdomid,
        review: review.toXML()
	};
	
	var options = {
		url: KNOWWE.core.util.getURL( params )
		
    };
    new _KA(options).send();
	
}

DiaFlux.ReviewTool.removeNodeLinks = function(flowDOM){
//TODO	
}

DiaFlux.ReviewTool.addMarkingHandler = function(flowDOM){
	flowDOM.find('.Node').on('click', 
		function(event) {
			if (DiaFlux.ReviewTool.isEditing(flowDOM.find('.reviewList'))){
				DiaFlux.ReviewTool.mark(this, true);
				
			}
		}
	);
	flowDOM.find('.rule_selector').on('click', 
		function(event) {
			if (DiaFlux.ReviewTool.isEditing(flowDOM.find('.reviewList'))){
				var set = jq$(this).closest('.Rule'); 
				DiaFlux.ReviewTool.mark(set, true);
			}
		}
	);
}
DiaFlux.ReviewTool.isEditing = function(list){
	var editItem = list.data('currentEdit');
	if (editItem)
		return true;
	else 
		return false;
}

DiaFlux.ReviewTool.mark= function(node, toggle){
	if (toggle) {
		node.toggleClass('reviewMarked');
	} else {
		node.addClass('reviewMarked');
	}
}


DiaFlux.ReviewTool.getReview = function(flow, panel){
	
	var params = {
		action : 'LoadReviewAction',
        kdomid: flow.kdomid
	};
	
	var options = {
		url: KNOWWE.core.util.getURL( params ),
        response : {
            fn: function(){DiaFlux.ReviewTool.loadReview(this.responseXML, panel);}
        }
    };
    new _KA(options).send();
	
}

DiaFlux.ReviewTool.loadReview = function(response, panel){	
	var review = DiaFlux.Review.fromXML(response);
	panel.data('review', review);
	DiaFlux.ReviewTool.showReview(review, panel);
		
}

DiaFlux.ReviewTool.showReview = function(review, panel){
	var list = jq$("<ul>", {"class":"reviewList"}).appendTo(panel);
	var items = review.getItems();
	
	for (var i = 0; i < items.length; i++){
		DiaFlux.ReviewTool.renderItem(items[i], list);
	}
	
//	list.sortable({
//		axis : 'y', 
//		containment : 'window'
//	});
	
}

DiaFlux.ReviewTool.renderItem = function(item, list){
	var li = jq$("<li>", {id:"item-" + item.getId(), "class" : "reviewItem"})
		.appendTo(list)
		.on('click', function(){DiaFlux.ReviewTool.handleSelection(li);})
		.on('dblclick', function() {jq$(this).find('a.editItem').click();});
	li.data(item);
	DiaFlux.ReviewTool.renderItemContent(item, li);
}

DiaFlux.ReviewTool.handleSelection = function(li){
	var list = li.closest('ul');
	if (DiaFlux.ReviewTool.isEditing(list)) return;
	var selectedItem = list.data('selectedItem');
	if (selectedItem == li) {
		DiaFlux.ReviewTool.deselect(li);
	} else {
		DiaFlux.ReviewTool.select(li);
	}
	
}

DiaFlux.ReviewTool.select = function(li){
	var list = li.closest('ul');
	list.data('selectedItem', li);
	list.find('li').removeClass('selected');
	li.addClass('selected');
	DiaFlux.ReviewTool.showMarkings(li);
}

DiaFlux.ReviewTool.deselect = function(li){
	var list = li.closest('ul');
	list.removeData('selectedItem');
	li.removeClass('selected');
	DiaFlux.ReviewTool.removeAllMarkings(li);
	
}
DiaFlux.ReviewTool.collectMarkings = function(li){
	var markings = [];
	var flow = li.closest('.reviewPanel').data('flow');
	
	return jq$.map(jq$(flow.dom).find('.reviewMarked'), function(val, key) {
		return val.getAttribute('id');
	});
	
}

DiaFlux.ReviewTool.showMarkings = function(li){
	DiaFlux.ReviewTool.removeAllMarkings(li);
	var markings = li.data().getMarkings();
	var flow = li.closest('.reviewPanel').data('flow');
	
	for (var i = 0; i < markings.length; i++){
		var flowElem = flow.findObject(markings[i]);
		if (flowElem) DiaFlux.ReviewTool.mark(flowElem.getDOM(), false);
		
	}
	
}

DiaFlux.ReviewTool.removeAllMarkings = function(elem){
	jq$(elem.closest('.reviewPanel').data('flow').dom).find('.reviewMarked').removeClass('reviewMarked');
	
}

DiaFlux.ReviewTool.renderItemContent = function(item, li){
	li.empty();
	var textDiv = jq$("<div class='reviewText'>" + item.getDescription() + "</div>")
	.appendTo(li);
	
	DiaFlux.ReviewTool.addItemActions(item, li);

}

DiaFlux.ReviewTool.addItemActions = function(item, li){
	
	var actionsDiv = jq$("<div>",{"class":"reviewActions"}).appendTo(li);
	jq$("<a>",{"class":"editItem"}).appendTo(actionsDiv).on('click', DiaFlux.ReviewTool.editItem);
	jq$("<a>",{"class":"deleteItem"}).appendTo(actionsDiv).on('click', DiaFlux.ReviewTool.deleteItem);
	
}

DiaFlux.ReviewTool.editItem = function(event){
	var itemEl = jq$(this).closest('li');
	var list = jq$(this).closest('ul');
	if (list.data('currentEdit')) return; //allow only one edit
	DiaFlux.ReviewTool.select(itemEl);
	var item = itemEl.data();
	list.data('currentEdit', item);
	
	var parent = itemEl.find('.reviewText').empty();
//	parent;
	var textArea = jq$('<textarea>', {rows: 5}).val(item.getDescription()).appendTo(parent);
	
	var actionsDiv = jq$('<div>', { "class": "editActions"}).appendTo(parent);
	
	var endEdit = function(){
		list.data('currentEdit', null);
		DiaFlux.ReviewTool.renderItemContent(item, itemEl);
	}
	
	jq$('<a class="saveChanges">Save</a>').on('click',
		function() {
			item.setDescription(textArea.val());
			item.setMarkings(DiaFlux.ReviewTool.collectMarkings(itemEl));
			endEdit();
		}
	).appendTo(actionsDiv);
	
	jq$('<a class="discardChanges">Cancel</a>').on('click', endEdit).appendTo(actionsDiv);
	
}

	
DiaFlux.ReviewTool.deleteItem = function(event){
	var itemEl = jq$(this).closest('li');
	var item = itemEl.data();
	itemEl.closest('.reviewPanel').data('review').removeItem(item);
	itemEl.remove();
	
}

/**
 * Review class
 * Stores items
 */
DiaFlux.Review = function(flowName, idCounter, items){
	this.flowName = flowName;
	this.idCounter = idCounter;
	this.items = items;
}

DiaFlux.Review.prototype.getFlowName = function() {
	return this.flowName;
}

DiaFlux.Review.prototype.getItems = function() {
	return this.items;
}

DiaFlux.Review.prototype.addItem = function(item) {
	this.items.push(item);
}

DiaFlux.Review.prototype.removeItem = function(item) {
	var index = this.items.indexOf(item);
	if (index != -1)
	this.items.splice(index, 1);
}

DiaFlux.Review.prototype.getIdCounter = function() {
	return this.idCounter;
}

DiaFlux.Review.prototype.getNextId = function() {
	return ++this.idCounter;
}

DiaFlux.Review.prototype.toXML = function(){
	var xml = '<?xml version="1.0" encoding="UTF-8"?>\n';
	xml += '<review idCounter="' + this.getIdCounter() +'" flowName="' + this.getFlowName() + '">\n';

	var items = this.getItems();
	for (var i = 0; i < items.length; i++){
		xml += items[i].toXML();
	}
	
	xml += '</review>\n';
	return xml;
	
}


DiaFlux.Review.fromXML = function(xml){
	var idCounter = parseInt(xml.getElementsByTagName('review')[0].getAttribute('idCounter'));
	var flow = xml.getElementsByTagName('review')[0].getAttribute('flowName');
	var xmlItems = xml.getElementsByTagName('item');
	var items = [];
	
	for (var i = 0; i < xmlItems.length; i++){
		items.push(DiaFlux.Review.Item.fromXML(xmlItems[i]));
	}
	
	return new DiaFlux.Review(flow, idCounter, items);
}


/**
 * Review item class
 * Stores one remark
 */
DiaFlux.Review.Item = function(id, date, author){
	this.id = id;
	this.date = date;
	this.author = author;
	this.description = "";
	this.markings = [];
}

DiaFlux.Review.Item.fromXML = function(itemXML){
	var id = parseInt(itemXML.getAttribute('id'));
	var date = itemXML.getAttribute('date');
	var author = itemXML.getAttribute('author');
	var pageRev = itemXML.getAttribute('pageRev');
	var description = itemXML.getElementsByTagName('description')[0].firstChild.nodeValue;
	var markXML = itemXML.getElementsByTagName('markings')[0].getElementsByTagName('mark');
	var markings = [];
	
	for (var i = 0; i < markXML.length;i++){
		markings.push(markXML[i].getAttribute('id'));
	}
	
	var item = new DiaFlux.Review.Item(id, date, author);
	item.setDescription(description);
	item.setMarkings(markings);
	item.setPageRev(pageRev);
	
	return item; 
	
}
DiaFlux.Review.Item.prototype.toXML = function(){
	var xml = '<item id="' + this.getId() +'" date="' + this.getDate() + '" author="' + this.getAuthor() +'" pageRev="' + this.getPageRev() +'">\n';
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

DiaFlux.Review.Item.prototype.getId = function(){
	return this.id;
}
DiaFlux.Review.Item.prototype.getDate = function(){
	return this.date;
}
DiaFlux.Review.Item.prototype.getAuthor = function(){
	return this.author;
}

DiaFlux.Review.Item.prototype.setPageRev= function(pageRev){
	this.pageRev = pageRev;
}

DiaFlux.Review.Item.prototype.getPageRev = function(){
	return this.pageRev;
}

DiaFlux.Review.Item.prototype.setDescription = function(description){
	this.description = description;
}

DiaFlux.Review.Item.prototype.getDescription = function(){
	return this.description;
}

DiaFlux.Review.Item.prototype.setMarkings = function(markings){
	this.markings = markings;
}

DiaFlux.Review.Item.prototype.getMarkings = function(){
	return this.markings;
}


KNOWWE.helper.observer.subscribe("flowchartrendered", DiaFlux.ReviewTool.addReviewPanel);