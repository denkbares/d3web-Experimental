function createKnoten(name, startx, starty,stringID){
	// var listEntry= document.createElement('li');
	// var newdiv = document.createElement('div');
	// newdiv.setAttribute('id', stringID);
	// newdiv.setAttribute('onclick', "location='Wiki.jsp?page="+name+"'");
	// newdiv.className="window ui-draggable";
	// //newdiv.style.left ="400px";
	// newdiv.style.width="100px";
	// newdiv.style.top ="100px"; 
	// newdiv.style.marginTop="100px";
	// newdiv.innerHTML="<p>"+name+"</p>";
	// newdiv.style.position = "relative"; 
	// listEntry.appendChild(newdiv);
	// $(knots).append(listEntry);
	// return listEntry;


}
function createKnoten(name, startx, starty,stringID,parentID){
	var listEntry= document.createElement('li');
	   newdiv = document.createElement('div');
	   subDiv = document.createElement('div'); 
	newdiv.setAttribute('id', stringID);
	newdiv.setAttribute('onclick', "location='Wiki.jsp?page="+name+"'");
	newdiv.className="window ui-draggable";
	//newdiv.style.left ="800px";
	newdiv.style.width="200px";
	newdiv.style.top ="100px"; 
	newdiv.style.marginRight="100px";
	newdiv.style.marginTop="10px";
	newdiv.innerHTML="<p>"+name+"</p>";
	newdiv.style.position = "relative";
	
	subDiv.style.top ="10px";
	subDiv.style.left="10px";
	subDiv.setAttribute("hid","false");
	subDiv.setAttribute('onclick', "event.stopPropagation();if(!this.hid){$(ul"+stringID+").hide();this.hid=true;this.style.backgroundColor='red';jsPlumb.select({source:\"" + stringID + "\"}).setPaintStyle({ strokeStyle:\"white\"});}else{$(ul"+stringID + " ).show();this.hid=false;this.style.backgroundColor='green';jsPlumb.select({source:\"" + stringID + "\"}).setPaintStyle({ strokeStyle:\"black\"});}");
	subDiv.style.width="200px";
	subDiv.style.height="50px";
	subDiv.style.backgroundColor="green";
	newdiv.appendChild(subDiv);
	listEntry.appendChild(newdiv);
	var test ="#ul"+parentID;
	$(test).append(listEntry);
	return listEntry;

}
function createUL(name,parent){
	var list= document.createElement('ul');
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
	list.style.left =xcor;
	list.style.position = "absolute"; 
	list.style.top =ycor; 
	$(knots).append(list);	
}
function connectKnoten(start,end){
	jsPlumb.bind("ready", function() {
		 jsPlumb.connect({
		 source:start,
		target:end
		 });
	 });



}
function connectKnotenTemporal(start,end){
	jsPlumb.bind("ready", function() {
		jsPlumb.connect({
			source:start,
			target:end
			//paintStyle:{ strokeStyle:"red", lineWidth:2 },
			//anchors:["RightMiddle", "LeftMiddle" ],
			//endpointStyles:[{ fillStyle:"yellow" , radius: 2}, { fillStyle:"yellow" , radius: 2 }]

		});
	});
}
