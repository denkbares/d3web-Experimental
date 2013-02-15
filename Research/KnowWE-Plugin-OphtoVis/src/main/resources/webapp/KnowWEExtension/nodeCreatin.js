function createKnoten(name, startx,stringID,parentID){	
var listEntry= document.createElement('li');
frameDiv = document.createElement('div');
  newdiv = document.createElement('div');
  subDiv = document.createElement('div'); 
frameDiv.setAttribute('id', stringID);
newdiv.setAttribute('onclick', "location='Wiki.jsp?page="+name+"'");
//attributes for nodes are stored in css class node
frameDiv.className="window node";
newdiv.innerHTML="<p><nobr>"+name+"</nobr></p>";
//attributes are stored in css class
subDiv.className="buttons back";
$(subDiv).attr("hid",true);
subDiv.setAttribute('onclick', "event.stopPropagation(); bottonClick("+stringID+",this)");
frameDiv.appendChild(subDiv);
frameDiv.appendChild(newdiv);
listEntry.appendChild(frameDiv);
var test ="#ul"+parentID;
$(test).append(listEntry);
return listEntry;

}


//create leaf node
// subDiv Button not included in FrameDiv
function createLeafNode(name, startx,stringID,parentID){	
var listEntry= document.createElement('li');
frameDiv = document.createElement('div');
  newdiv = document.createElement('div');
frameDiv.setAttribute('id', stringID);
newdiv.setAttribute('onclick', "location='Wiki.jsp?page="+name+"'");
//attributes for nodes are stored in css class node
frameDiv.className="window node";
newdiv.innerHTML="<p><nobr>"+name+"</nobr></p>";
frameDiv.appendChild(newdiv);
listEntry.appendChild(frameDiv);
var test ="#ul"+parentID;
$(test).append(listEntry);
return listEntry;

}



function bottonClick(id, instance){
	blockScreen();
	var str = "#ul"+ id;
	$($("#"+id).children()[0]).toggleClass('fwd');
	$($("#"+id).children()[0]).toggleClass('back');
	var hidden = $(instance).attr("hid");
	if(hidden=="false"){
		alert("sichtbar");
		
		var zusammen = "ul" +(id+1);
		alert("going to hide" + zusammen);
		$(window[zusammen]).hide();
		//$(str).hide();
		//$(instance).children().closest("ul").hide();
		var connections =jsPlumb.select({source: id+""}).getParameter();
		for(var i=0,j=connections.length; i<j; i++){
  			if(connections[i][1].getPaintStyle().strokeStyle=="black"){
  				jsPlumb.detach(connections[i][1]);
  			}
		};
		jsPlumb.repaintEverything();
		unBlockScreen();
		$(instance).attr("hid",true);
		alert("wieder hidden");
	}else if(hidden=="true"){
		alert("hidden");
		$(instance).attr("hid",false);
		alert("nimmer hidden");
		if (typeof(window[str]) == "undefined"){
			var parentString =$("#"+id).parent().parent().attr("id");
			var parent = parentString.substring(2);
 			getChilds($("#"+id).children().children().text(),id,parent);
		}else{
			var found = $(window[str]).show();
			restoreKnoten(id);
}

}
}
function restoreKnoten(id){
var str = "#ul"+ id;
var listElement = $(str).children();
var listElements =$(listElement).children();
for(var i=0,j=listElements.length; i<j; i++){
	if(listElements[i].id.length>0){
  	connectKnoten(id+"",listElements[i].id+"");
  }
  	var str2 = "#ul"+ listElements[i].id;
  	if($(str2).length>0){
  		jsPlumb.repaintEverything();
  		var str = "#ul"+ listElements[i].id ;
  		$(str).show();
  		restoreKnoten(listElements[i].id);
  	}
};
jsPlumb.repaintEverything();
unBlockScreen();
}

function blockScreen(){
	// $.blockUI();
}
function unBlockScreen(){
	
	//$.unblockUI();
}

function createUL(name,parent){
var list= document.createElement('ul');
list.style.listStyleType = "none";
list.setAttribute('id', 'ul' + name);
if(parent!=knots){
$("#ul" + parent).append(list);
}else{
$(knots).append(list);	
}
}
function createULAjax(name,parent,father){
var list= document.createElement('ul');
list.style.listStyleType = "none";
list.setAttribute('id', 'ul' + name);
$(list).insertAfter($("#"+father));
jsPlumb.repaintEverything();
}
function createULRelative(name,parent,xcor,ycor){
var list= document.createElement('ul');
list.setAttribute('id', 'ul' + name);
list.style.listStyleType = "none";

list.style.left =xcor;
list.style.position = "absolute"; 
list.style.top =ycor; 
$(knots).append(list);	
}
function connectKnoten(start,end){
jsPlumb.bind("ready", function() {
jsPlumb.connect({
source:start+"",
target:end+""
});
});



}
function connectKnotenTemporal(start,end){
var contain = '#ule'+start;
jsPlumb.bind("ready", function() {
jsPlumb.connect({
source:start,
target:end,
paintStyle:{ strokeStyle:"red", lineWidth:2 },
anchors:["RightMiddle", "LeftMiddle" ],
endpointStyles:[{ fillStyle:"yellow" , radius: 2}, { fillStyle:"yellow" , radius: 2 }],
container:$(contain)
});
});
}

 	var fontSize = 2;
	function zoomIn() {
		fontSize += 0.1;
		document.body.style.fontSize = fontSize + "em";
		jsPlumb.repaintEverything();
	}
	function zoomOut() {
		fontSize -= 0.1;
		document.body.style.fontSize = fontSize + "em";
		jsPlumb.repaintEverything();
	}

function testAjax() {
	
		var params = {
			action : 'AjaxAction',
			node   : 'Kapselsackveränderung',
		}
	
		var options = {
			url : KNOWWE.core.util.getURL(params),
			loader : false,
			response : {
				fn : function() {
					alert(this.responseText);
					eval(this.responseText);
				},
				onError : onErrorBehavior,
			}
		}
	
		new _KA(options).send();
	
	}
	function getChilds(nodeName,nodeId,parentId) {
		//alert("Ajax on its way");
	
		var params = {
			action 		: 'AjaxAction',
			node   		:  nodeName,
			id			: nodeId ,
			parent		: parentId
		}
	
		var options = {
			url : KNOWWE.core.util.getURL(params),
			loader : false,
			response : {
				fn : function() {
					eval(this.responseText);
					 $(document).ready(function() {
						jsPlumb.repaintEverything();
					});
					
				},
				onError : onErrorBehavior,
			}
		}
	
		new _KA(options).send();
	
	}


function onErrorBehavior() {
	if (this.status == null)
		return;
	switch (this.status) {
	case 0:
		// server not running, do nothing.
		break;
	case 409:
		alert("There already is a build running for this dashbaord. Please abort the running build before starting a new one.");
		break;
	case 404:
		alert("This page no longer exists. Please reload.");
		break;
	default:
		// alert("Error " + this.status + ". Please reload the page.");
		break;
	}
}