function initDesign(){
jsPlumb.bind(
		
"ready", function() 
{

	jsPlumb.importDefaults({
		// default to blue at one end and green at the other
		EndpointStyles : [{ fillStyle:"yellow" , radius: 2}, { fillStyle:"yellow" , radius: 2 }],
		// blue endpoints 7 px; green endpoints 11.
//		Endpoints : [ [ "Dot", {radius:7} ], [ "Dot", { radius:11 } ]],
		// the overlays to decorate each connection with.  note that the label overlay uses a function to generate the label text; in this
		// case it returns the 'labelText' member that we set on each connection in the 'init' method below.
		
		Connector : [ "Flowchart",{stub:1} ],
		//Connector:[ "StateMachine", { curviness:20 } ],
//	Anchors:["Continuous", "Continuous"],
		
	Anchors:["BottomCenter", "LeftMiddle" ],
	//				Anchors:["Continuous", "LeftMiddle" ],
//Anchors:["AutoDefault","AutoDefault"];
		


		
		PaintStyle:{ strokeStyle:"black", lineWidth:2 },
		
				//	  	overlays:[ [ "Label", { label:"part of", location:50}]],
		
	ConnectionOverlays : [
			[ "Arrow", { location:1.0 , width:10} ],
//			[ "Label", { 
//				location:0.4,
//				id:"label",
//				cssClass:"aLabel"
//			}]
	]
	});
	
}

);

}


//
//
//------------------------
//String verbindungen = "jsPlumb.bind(\"ready\", function() {\r\n"
//	+
//	"jsPlumb.importDefaults({"
//	+
//	"EndpointStyles : [{ fillStyle:\"yellow\" , radius: 2}, { fillStyle:\"yellow\" , radius: 2 }],"
//	+
//	"Connector : [ \"Flowchart\",{stub:1} ]," +
//	// "Anchors:[\"Continuous\", \"LeftMiddle\"]," +
//	"Anchors:[\"BottomCenter\", \"LeftMiddle\"]," +
//	"PaintStyle:{ strokeStyle:\"black\", lineWidth:2 }," +
//	"ConnectionOverlays : [" +
//	"[ \"Arrow\", { location:1.0 , width:10} ]," +
//	"]" +
//	"});\r\n"
//	+
//	"createKnoten(\"blabla\",100,100);\r\n"
//
//;