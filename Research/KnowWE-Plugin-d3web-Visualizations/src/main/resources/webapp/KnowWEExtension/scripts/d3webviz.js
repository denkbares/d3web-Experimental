if (typeof KNOWWE == "undefined" || !KNOWWE) {
    var KNOWWE = {};
}

if (typeof KNOWWE.d3webViz == "undefined" || !KNOWWE.d3webViz) {
	KNOWWE.d3webViz = {};
}

KNOWWE.d3webViz.createDependencyGraph = function(kdomid){
	
	KNOWWE.d3webViz.loadJSON('DependencyGraphAction', kdomid, renderGraph);

	function renderGraph(jsonObject){
		
	  var svg = d3.select("#graph" + kdomid)
	  .append("svg:svg")
      .attr("width", "100%")
      .attr("height", "100%");
	  
	  svg.append("svg:defs").selectAll("marker")
	  .data(["arrowhead"])
	  .enter().append("svg:marker")
	  .attr("id", String)
	  .attr("viewBox", "0 0 10 10")
	  .attr("refX", 8)
	  .attr("refY", 5)
	  .attr("markerWidth", 8)
	  .attr("markerHeight", 5)
	  .attr("orient", "auto")
	  .append("svg:path")
	  .attr("d", "M0,0L10,5L0,10");
	  
	  
	  var svgGroup = svg.append("g").attr("transform", "translate(5, 5)");

	  var zoom = d3.behavior.zoom()
	    .scale(0.2)
	    .on("zoom", function () {
	      var t = d3.event.translate;
	      var s = d3.event.scale;
	      svgGroup.attr("transform", "translate(" + t + ") scale(" + s + ")");
	  });
	  
	  
	  svg.call(zoom);
  
  var nodeData = jsonObject.nodes;
  var edgeData = jsonObject.links;
  
  var graph = dagre.graph(); 

  nodeData.forEach(function(node){
	  graph.addNode(node.id, node);
  });
  
  
  edgeData.forEach(function(link) {
	  link.id = link.source +" -> " + link.target;
	  link.source = nodeData[link.source]; // || (nodeData[link.source] = {name: link.source});
	  link.target = nodeData[link.target]; // || (nodeData[link.target] = {name: link.target});
	  graph.addEdge(link.id, link.source.id, link.target.id, link);
	});
  
	// `nodes` is center positioned for easy layout later
	var nodes = svgGroup
	.selectAll("g .node")
	.data(nodeData)
	.enter()
	.append("g")
	.attr("class", "node")
	.on("click", nodeEnter);
//	.on("mouseout", nodeLeave);
	
	var edges = svgGroup
	.selectAll("path .edge")
	.data(edgeData)
	.enter()
	.append("path")
	.attr("class", "edge")
	.attr("marker-end", "url(#arrowhead)");
	
	// Append rectangles to the nodes. We do this before laying out the text
	// because we want the text above the rectangle.
	var rects = nodes.append("rect");
//  .style("fill", function(d) { return colors(d.group); });
	
	// Append text
	var labels = nodes
	.append("text")
	.attr("text-anchor", "middle")
	.attr("x", 0);
	
	labels
	.append("tspan")
	.attr("x", 0)
	.attr("dy", "1em")
	.text(function(d) { return d.id; });
	
	var nodePadding = 10;
	
	// We need width and height for layout.
	labels.each(function(d) {
		var bbox = this.getBBox();
		d.bbox = bbox;
		d.width = bbox.width + 2 * nodePadding;
		d.height = bbox.height + 2 * nodePadding;
	});
	
	rects
	.attr("x", function(d) { return -(d.bbox.width / 2 + nodePadding); })
	.attr("y", function(d) { return -(d.bbox.height / 2 + nodePadding); })
	.attr("width", function(d) { return d.width; })
	.attr("height", function(d) { return d.height; });
	
	labels
	.attr("x", function(d) { return -d.bbox.width / 2; })
	.attr("y", function(d) { return -d.bbox.height / 2; });
	
	dagre.layout()
	.nodes(nodeData)
	.edges(edgeData)
	.rankSep(250)
	.edgeSep(50)
	.run();
	
	nodes.attr("transform", function(d) { return "translate(" + d.dagre.x + "," + d.dagre.y +")"; });
	
	edges.attr("d", function(e) {
		var points = e.dagre.points;
		var source = dagre.util.intersectRect(e.source.dagre, points.length > 0 ? points[0] : e.target.dagre);
		var target = dagre.util.intersectRect(e.target.dagre, points.length > 0 ? points[points.length - 1] : e.source.dagre);
		points.unshift(source);
		points.push(target);
		return spline(points);
	});
	
	svgGroup.attr("transform", "translate(5, 5) scale(0.2)");
	
	function nodeEnter(d,i){
		var allEdges = [];
		nodes.classed("faded", function(p, id) {
			var over = d==p 
			if (over) {
				var inoutedges = graph.edges(p.id);
				
				for (var j = 0; j < inoutedges.length; j++){
					var e = graph.edge(inoutedges[j]);
					allEdges.push(e);
				}
			}
			return !over;
		});
		
		
		svgGroup
		.selectAll("path.edge")
		.classed("faded", true);
		
		var unfadeEdges = svgGroup
		.selectAll("path.edge")
		.data(allEdges);
		
		unfadeEdges.classed("faded", false);
		
		
		var blubb = edges;
	}
	
	function nodeLeave() {
		nodes.classed("faded", false);
		edges.classed("faded", false);
	}
	}
	
  function spline(points) {
    return d3.svg.line()
      .x(function(d) { return d.x; })
      .y(function(d) { return d.y; })
      .interpolate("linear")(points);
  }
	
};


KNOWWE.d3webViz.loadJSON = function(action, kdomid, f){
	var params = {
			action : action,
			SectionID: kdomid
		};
	  	
	  	var options = {
			url: KNOWWE.core.util.getURL( params ),
	        response : {
	            action: 'none',
	            fn: function(){
	            	var json = jq$.parseJSON(this.response);
	            	f(json);
	            	
	            }
	        }
	    };
		
		
		KNOWWE.core.util.updateProcessingState(1);
		try{
			new _KA(options).send();
			
		} catch(e) {}
		KNOWWE.core.util.updateProcessingState(-1);  
	
}

KNOWWE.d3webViz.createDiaFluxHierarchy = function(kdomid){

	KNOWWE.d3webViz.loadJSON('DiaFluxHierarchyAction',kdomid, renderHierarchy);
	
	function renderHierarchy(json){
	
	var width = 960,
    height = 960,
    format = d3.format(",d");

var pack = d3.layout.pack()
    .size([width - 4, height - 4])
    .value(function(d) { return d.size; });

var vis = d3.select("#diafluxHierarchy" + kdomid).append("svg")
    .attr("width", width)
    .attr("height", height)
    .attr("class", "pack")
  .append("g")
    .attr("transform", "translate(2, 2)");


  var node = vis.data([json]).selectAll("g.node")
      .data(pack.nodes)
    .enter().append("g")
      .attr("class", function(d) { return d.children ? "node" : "leaf node"; })
      .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

  node.append("title")
      .text(function(d) { return d.name + (d.children ? "" : ": " + format(d.size)); });

  node.append("circle")
      .attr("r", function(d) { return d.r; });

  node.filter(function(d) { return !d.children; }).append("text")
      .attr("text-anchor", "middle")
      .attr("dy", ".3em")
      .text(function(d) { return d.name.substring(0, d.r / 3); });

	}

	
}
