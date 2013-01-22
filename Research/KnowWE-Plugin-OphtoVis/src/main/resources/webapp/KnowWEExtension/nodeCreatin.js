function createKnoten(name, startx, starty,stringID,parentID){	
var listEntry= document.createElement('li');
frameDiv = document.createElement('div');
  newdiv = document.createElement('div');
  subDiv = document.createElement('div'); 
frameDiv.setAttribute('id', stringID);
newdiv.setAttribute('onclick', "location='Wiki.jsp?page="+name+"'");
//attributes for nodes are stored in css class node
frameDiv.className="window ui-draggable node";
newdiv.innerHTML="<p><nobr>"+name+"</nobr></p>";
//attributes are stored in css class
subDiv.className="button blue";
subDiv.setAttribute("hid","false");
subDiv.setAttribute('onclick', "event.stopPropagation(); bottonClick("+stringID+",this)");
frameDiv.appendChild(subDiv);
frameDiv.appendChild(newdiv);
listEntry.appendChild(frameDiv);
var test ="#ul"+parentID;
$(test).append(listEntry);
return listEntry;

}
function bottonClick(id, instance){
var str = "#ul"+ id;
if(("#ul"+ id).length>0){
$($("#"+id).children()[0]).toggleClass('blue');
$($("#"+id).children()[0]).toggleClass('red');}
if(!instance.hid){
$(str).hide();
instance.hid=true;
instance.style.backgroundColor='red';
jsPlumb.select({source: id+"" }).detach();
jsPlumb.repaintEverything();
}else{
var found = $(str).show();
instance.hid=false;
instance.style.backgroundColor='green';
restoreKnoten(id);
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
var contain = '#ul'+start;
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

