function createKnoten(name, startx, starty,stringID){
	var listEntry= document.createElement('li');
	var newdiv = document.createElement('div');
	newdiv.setAttribute('id', stringID);
	newdiv.setAttribute('onclick', "location='Wiki.jsp?page="+name+"'");
	newdiv.className="window ui-draggable";
	//newdiv.style.left ="400px";
	newdiv.style.width="100px";
	newdiv.style.top ="100px"; 
	newdiv.style.marginTop="100px";
	newdiv.innerHTML="<p>"+name+"</p>";
	newdiv.style.position = "relative"; 
	listEntry.appendChild(newdiv);
	$(knots).append(listEntry);
	return listEntry;

}
function createKnoten(name, startx, starty,stringID,parentID){
	var listEntry= document.createElement('li');
	var newdiv = document.createElement('div');
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
