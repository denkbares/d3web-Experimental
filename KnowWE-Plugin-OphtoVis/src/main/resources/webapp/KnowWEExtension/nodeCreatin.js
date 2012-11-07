function createKnoten(name, startx, starty,stringID){
	
	var neuerLink = document.createElement("a");  
	neuerLink.href = "Wiki.jsp?page="+name;


	var newdiv = document.createElement('div');
	newdiv.setAttribute('id', stringID);
	newdiv.setAttribute('href', "www.google.de");
	newdiv.className="window ui-draggable";
	newdiv.style.left = startx +"px";
	newdiv.style.top = starty + "px"; 
	newdiv.innerHTML="<strong><p>"+name+"</p></strong><br><br>";
	newdiv.style.position = "absolute"; 
	neuerLink.appendChild(newdiv);
	$(knots).append(neuerLink);
	return neuerLink;

}
function connectKnoten(start,end){
	jsPlumb.bind("ready", function() {
		jsPlumb.connect({
			source:start,
			target:end
		});
	});
}

function ajax() {

	var params = {
			action : 'ExampleAction',
	}

	var options = {
			url : KNOWWE.core.util.getURL(params),
			loader : false,
			response : {
				fn : function() {

					var result = null;
					var rePattern = /<script>(.*)<\/script>/gi;
					while (result = rePattern.exec(this.responseText)) {
						var script = result[1];
						alert(script);
						eval(script);
					}
				}
			}



	}
	new _KA(options).send();
}