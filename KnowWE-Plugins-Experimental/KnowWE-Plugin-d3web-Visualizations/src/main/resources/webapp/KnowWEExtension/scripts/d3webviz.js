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

KNOWWE.d3webViz.createDiaFluxCouplingMatrix = function(kdomid){

	KNOWWE.d3webViz.loadJSON('DiaFluxCouplingMatrixAction', kdomid, renderCouplingMatrix);
	
	function renderCouplingMatrix(json){
		

		var margin = {top: 200, right: 0, bottom: 10, left: 200},
		    width = 720,
		    height = 720;

		var x = d3.scale.ordinal().rangeBands([0, width]),
		    z = d3.scale.linear().domain([0, 4]).clamp(true),
		    c = d3.scale.category10().domain(d3.range(10));

		var parent=d3.select("#couplingMatrix" + kdomid);
		var svg = parent.append("svg")
		    .attr("width", width + margin.left + margin.right)
		    .attr("height", height + margin.top + margin.bottom)
		    .style("margin-left", margin.left + "px")
		  .append("g")
		    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

		  var matrix = [],
		      nodes = json.nodes,
		      n = nodes.length;

		  // Compute index per node.
		  nodes.forEach(function(node, i) {
		    node.index = i;
		    node.count = 0;
		    matrix[i] = d3.range(n).map(function(j) { return {x: j, y: i, z: 0, objects:[]}; });
		  });

		  // Convert links to matrix; count character occurrences.
		  json.links.forEach(function(link) {
		    matrix[link.source][link.target].z = link.value;
//		    matrix[link.target][link.source].z += link.value;
//		    matrix[link.source][link.source].z += link.value;
//		    matrix[link.target][link.target].z += link.value;
		    
		    matrix[link.source][link.target].objects = link.objects;
		    
		    nodes[link.source].count += link.value;
		    nodes[link.target].count += link.value;
		  });

		  // Precompute the orders.
		  var orders = {
		    name: d3.range(n).sort(function(a, b) { return d3.ascending(nodes[a].name, nodes[b].name); }),
		    count: d3.range(n).sort(function(a, b) { return nodes[b].count - nodes[a].count; }),
		    group: d3.range(n).sort(function(a, b) { return nodes[b].group - nodes[a].group; })
		  };

		  // The default sort order.
//		  x.domain(orders.name);
		  x.domain(orders.count);

		  svg.append("rect")
		      .attr("class", "background")
		      .attr("width", width)
		      .attr("height", height);

		  var row = svg.selectAll(".row")
		      .data(matrix)
		    .enter().append("g")
		      .attr("class", "row")
		      .attr("transform", function(d, i) { return "translate(0," + x(i) + ")"; })
		      .each(row);

		  row.append("line")
		      .attr("x2", width);

		  row.append("text")
		      .attr("x", -6)
		      .attr("y", x.rangeBand() / 2)
		      .attr("dy", ".32em")
		      .attr("text-anchor", "end")
		      .text(function(d, i) { return nodes[i].name; });

		  var column = svg.selectAll(".column")
		      .data(matrix)
		    .enter().append("g")
		      .attr("class", "column")
		      .attr("transform", function(d, i) { return "translate(" + x(i) + ")rotate(-90)"; });

		  column.append("line")
		      .attr("x1", -width);

		  column.append("text")
		      .attr("x", 6)
		      .attr("y", x.rangeBand() / 2)
		      .attr("dy", ".32em")
		      .attr("text-anchor", "start")
		      .text(function(d, i) { return nodes[i].name; });

		  function row(row) {
		    var cell = d3.select(this).selectAll(".cell")
		        .data(row.filter(
		        		function(d) {
		        			if (d.x != d.y)
		        				return d.z;
		        			else 
		        				return undefined;
		        			}))
		      .enter().append("rect")
		        .attr("class", "cell")
		        .attr("x", function(d) { return x(d.x); })
		        .attr("width", x.rangeBand())
		        .attr("height", x.rangeBand())
		        .attr("title", function(d){
		        	if (matrix[d.x][d.y].objects)
		        	return matrix[d.x][d.y].objects.toString()
		        	else return "";
		        	})
		        .style("fill-opacity", function(d) { return d.z; })
		        .style("fill", function(d) { return nodes[d.x].group == nodes[d.y].group ? c(nodes[d.x].group) : null; })
		        .on("mouseover", mouseover)
		        .on("mouseout", mouseout);
//		        .append("text")
//		        .text(function(d){return matrix[d.x][d.y].z})
//		        .style("fill","rgb(255,255,255)");
		  }

		  function mouseover(p) {
		    d3.selectAll(".row text").classed("active", function(d, i) { return i == p.y; });
		    d3.selectAll(".column text").classed("active", function(d, i) { return i == p.x; });
		  }

		  function mouseout() {
		    d3.selectAll("text").classed("active", false);
		  }

		  
		  
		  
//		  d3.select("#order").on("change", function() {
//		    clearTimeout(timeout);
//		    order(this.value);
//		  });
//
//		  function order(value) {
//		    x.domain(orders[value]);
//
//		    var t = svg.transition().duration(2500);
//
//		    t.selectAll(".row")
//		        .delay(function(d, i) { return x(i) * 4; })
//		        .attr("transform", function(d, i) { return "translate(0," + x(i) + ")"; })
//		      .selectAll(".cell")
//		        .delay(function(d) { return x(d.x) * 4; })
//		        .attr("x", function(d) { return x(d.x); });
//
//		    t.selectAll(".column")
//		        .delay(function(d, i) { return x(i) * 4; })
//		        .attr("transform", function(d, i) { return "translate(" + x(i) + ")rotate(-90)"; });
//		  }
//
//		  var timeout = setTimeout(function() {
//		    order("group");
//		    d3.select("#order").property("selectedIndex", 2).node().focus();
//		  }, 5000);
		  
		}
		
}