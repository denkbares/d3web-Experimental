function drawTree(size, jsonsource, sectionID) {
	
	// define layout 
	
	var margin = {top: 20, right: 120, bottom: 20, left: 20};
    	
	var i = 0,
    	duration = 750,
    	root;
	
	 root = jsonsource;
	 root.x0 = height / 2;
	 root.y0 = 10;

	 // compute left margin based on rootlabel length 
	 margin.left = margin.left + root.concept.length * 5;
	
	 var width = 960 - margin.right - margin.left,
    	height = 800 - margin.top - margin.bottom;
   
	if(size != null) {
		width = size - margin.right - margin.left;
	}
	
	 
	var tree = d3.layout.tree()
    	.size([height, width]);

	var diagonal = d3.svg.diagonal()
    	.projection(function(d) { return [d.y, d.x]; });

	var div = d3.select("#d3" + sectionID);
	
	var svg = div.append("svg")
				.attr("id", "svg"+sectionID)
				.attr("min-width", width + margin.right + margin.left)
				.attr("height", height + margin.top + margin.bottom)
				.append("g")
				.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
	// initialize zoom behaviour
	
	 d3.select("#svg"+sectionID)
     .call(d3.behavior.zoom()
     .scaleExtent([0.5, 5])
     .on("zoom", zoom))
	 .on("mouseclick.zoom", null);
	
	
	// create buttons
	
	var expandButton = div.append("input")
			.attr("type", "button")
			.attr("id", "expandButton"+sectionID)
			.attr("class", "d3treeButton")
			.attr("value", "Expand all")
			.on("click", function (d) {
				if(root.children) {
				click(root);
				}
				expandAllChildren(root);
											 
			});
	
	var collapseButton = div.append("input")
			.attr("type" , "button")
			.attr("id", "collapseButton"+sectionID)
			.attr("class", "d3treeButton")
			.attr("value", "Collapse all")
			.on("click", function (d){
				collapse(root);
				update(root);
			})
	
	var increaseTreeSizeButton = div.append("input")
			.attr("type" , "button")
			.attr("id", "zoomInButton"+sectionID)
			.attr("class", "d3treeButton")
			.attr("value", "Increase tree size")
			.on("click", function (d){
				
				tree.size([tree.size()[0]*1.5, tree.size()[1]*1.5]);
				update(root);
			})
			
	var decreaseTreeSizeButton = div.append("input")
			.attr("type" , "button")
			.attr("id", "zoomInButton"+sectionID)
			.attr("class", "d3treeButton")
			.attr("value", "Decrease tree size")
			.on("click", function (d){
				tree.size([tree.size()[0]/1.5, tree.size()[1]/1.5]);
				update(root);
			})
	
	var increaseLabelsButton = div.append("input")
			.attr("type" , "button")
			.attr("id", "zoomInButton"+sectionID)
			.attr("class", "d3treeButton")
			.attr("value", "Increase label size")
			.on("click", function (d){
				
				
				var rootnode = svg.select("g.node")[0];
				var text = jq$(rootnode).find("text")[0];
				var fontsize = window.getComputedStyle(text).fontSize;
				fontsize = parseFloat(fontsize);
				fontsize *= 1.5;
				svg.selectAll("text")
					.style("font-size", fontsize+"px");
				
				update(root);
			})
			
	var decreaseLabelsButton = div.append("input")
			.attr("type" , "button")
			.attr("id", "zoomInButton"+sectionID)
			.attr("class", "d3treeButton")
			.attr("value", "Decrease label size")
			.on("click", function (d){
				
				var rootnode = svg.select("g.node")[0];
				var text = jq$(rootnode).find("text")[0];
				var fontsize = window.getComputedStyle(text).fontSize;
				fontsize = parseFloat(fontsize);
				fontsize /= 1.5;
				svg.selectAll("text")
					.style("font-size", fontsize+"px");
				
				update(root);
			})
			
	// collapse node and all of its children - update source afterwards
	 function collapse(d) {
	    if (d.children) {
	      d._children = d.children;
	      d._children.forEach(collapse);
	      d.children = null;
	    }
	  }

	// compute new layout
	 function update(source) {

		  // Compute the new tree layout.
		  var nodes = tree.nodes(root).reverse(),
		      links = tree.links(nodes);

		  // Normalize for fixed-depth.
		  nodes.forEach(function(d) { d.y = d.depth * 180; });

		  // Update the nodes…
		  var node = svg.selectAll("g.node")
		      .data(nodes, function(d) { return d.id || (d.id = ++i); });

		  // Enter any new nodes at the parent's previous position.
		  var nodeEnter = node.enter().append("g")
		      .attr("class", "node")
		      .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
		      .on("click", click);

		  nodeEnter.append("circle")
		      .attr("r", 1e-6)
		      .attr("class", function(d) { return d._children? "coloured" : null});

		  // Adding the links 
		  var $a = nodeEnter.append("svg:a")
		  	.attr("xlink:href", function(d) { return decodeURIComponent(d.conceptUrl);})
		  		  
		  // Creating Node text labels
		  $a.append("text")
		  	.attr("x", function(d) { return d.children || d._children ? -10 : 10; })
		  	.attr("dy", ".35em")
		  	.attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
		  	.text(function(d) { return d.concept; })
		  	.style("fill-opacity", 1e-6)
		  	.style("pointer-events", "auto");

		  // Transition nodes to their new position.
		  var nodeUpdate = node.transition()
		      .duration(duration)
		      .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

		  nodeUpdate.select("circle")
	      		.attr("r", 4.5)
	      		.attr("class", function(d) { return d._children? "coloured" : null});


		  nodeUpdate.select("text")
		      .style("fill-opacity", 1);

		  // Transition exiting nodes to the parent's new position.
		  var nodeExit = node.exit().transition()
		      .duration(duration)
		      .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
		      .remove();

		  nodeExit.select("circle")
		      .attr("r", 1e-6);

		  nodeExit.select("text")
		      .style("fill-opacity", 1e-6);

		  // Update the links…
		  var link = svg.selectAll("path.link")
		      .data(links, function(d) { return d.target.id; });

		  // Enter any new links at the parent's previous position.
		  link.enter().insert("path", "g")
		      .attr("class", "link")
		      .attr("d", function(d) {
		        var o = {x: source.x0, y: source.y0};
		        return diagonal({source: o, target: o});
		      });

		  // Transition links to their new position.
		  link.transition()
		      .duration(duration)
		      .attr("d", diagonal);

		  // Transition exiting nodes to the parent's new position.
		  link.exit().transition()
		      .duration(duration)
		      .attr("d", function(d) {
		        var o = {x: source.x, y: source.y};
		        return diagonal({source: o, target: o});
		      })
		      .remove();

		  // Stash the old positions for transition.
		  nodes.forEach(function(d) {
		    d.x0 = d.x;
		    d.y0 = d.y;
		  });
		  
		  
		 
		  
		}
	 
	// Toggle children on click.
	 function click(d) {
		  if (d.children) {
		    d._children = d.children;
		    d.children = null;
		  } else {
		    d.children = d._children;
		    d._children = null;
		  }
		  update(d);
		}

	// expand node and all of its children 
	 function expandAllChildren(node) {
			
			
			if(node._children) {
				click(node);
				node.children.forEach(expandAllChildren);
			} else if(node.children){
				node.children.forEach(expandAllChildren);
			}
			
}

	 function zoom() {
		 
		 var scale = d3.event.scale,
		        translation = d3.event.translate,
		        tbound = -height * scale,
		        bbound = height * scale,
		        lbound = (-width + margin.right) * scale,
		        rbound = (width - margin.left) * scale;
		    // limit translation to thresholds
		    translation = [
		        Math.max(Math.min(translation[0], rbound), lbound),
		        Math.max(Math.min(translation[1], bbound), tbound)
		    ];
		    
		   
		    svg.attr("transform", "translate(" + translation + ")" + " scale(" + scale + ")");
		    
		}
	

	
	
	 root.children.forEach(collapse);
	 update(root);
	
	d3.select(self.frameElement).style("height", "800px");
	

		
	
}