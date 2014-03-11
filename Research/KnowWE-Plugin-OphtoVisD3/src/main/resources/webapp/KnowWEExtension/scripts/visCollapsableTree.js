function createTreeDiagonal() {

	visChange();

	d3.select("#vis").remove();
	d3.select("body").append("div").attr("id", "vis");

	d3.select("#vis").attr("class", "treeDiagonal");

	//JSON object with the data
	var treeData = {"name" : "A", "info" : "tst", "children" : [
		{"name" : "A1" },
		{"name" : "A2" },
		{"name" : "A3", "children" : [
			{"name" : "A31", "children" : [
				{"name" : "A311" },
				{"name" : "A312" }
			]}
		] }
	]};

	var url = KNOWWE.core.util.getURL({action : 'AjaxAction'});

	console.log("------------------");
	console.log(url);
	console.log("------------------");


	var tree = d3.layout.tree()
//    .size([1200,600]);
		.size([2700, 1500]);


	d3.json(url, function(error, root) {

		var nodes = tree.nodes(root.root),
			links = tree.links(nodes);
		console.log(root);


		// Create a svg canvas
		var vis = d3.select("#vis").append("svg:svg")
//      .attr("width", 1200)
//      .attr("height", 1600)
			.attr("width", 2700 + 160)
			.attr("height", 3000)
			.append("svg:g")
//      .attr("transform", "translate(160, 100)"); // shift everything to the right
			.attr("transform", "translate(160, 50)"); // shift everything to the right

		// Create a tree "canvas"


		var diagonal = d3.svg.diagonal()
			// change x and y (for the left to right tree)
			//   .projection(function(d) { return [d.y, d.x]; });
			.projection(function(d) {
				return [d.y, d.x];
			});

		// Preparing the data for the tree layout, convert data into an array of nodes
//      var nodes = tree.nodes(treeData);
		// Create an array with all the links
//     var links = tree.links(nodes);

//      console.log(treeData)
//      console.log(nodes);
//      console.log(links);

		var link = vis.selectAll("pathlink")
			.data(links)
			.enter().append("svg:path")
			.attr("class", "link")
			.attr("d", diagonal);

		var node = vis.selectAll("g.node")
			.data(nodes)
			.enter().append("svg:g")
			.attr("transform", function(d) {
				return "translate(" + d.y + "," + d.x + ")";
			});


		// Add the dot at every node
		node.append("svg:circle")
			.attr("r", 3.5);

		// place the name atribute left or right depending if children
		node.append("svg:text")
			.attr("dx", function(d) {
				return d.children[0] ? -8 : 8;
			})
			.attr("dy", 3)
			.attr("text-anchor", function(d) {
				console.log(d.children[0]);
				return d.children[0] ? "end" : "start";
			})
			.text(function(d) {
				return d.data.name;
			});

	});//json

}


//Reingold-Tilford Tree

function createReingold() {

	visChange();

	breadcrumb(globalStartConcept);


	var diameter = 1960;

	var tree = d3.layout.tree()
		.size([360, diameter / 2 - 120])
		.separation(function(a, b) {
			return (a.parent == b.parent ? 1 : 2) / a.depth;
		});

	var diagonal = d3.svg.diagonal.radial()
		.projection(function(d) {
			return [d.y, d.x / 180 * Math.PI];
		});


	d3.select("#vis").remove();
	d3.select("#center-container").append("div").attr("id", "vis");


	var svg = d3.select("#vis").append("svg")
		.attr("width", diameter)
		.attr("height", diameter)
		.append("g")
		.attr("transform", "translate(" + diameter / 2 + "," + diameter / 2 + ")");

	var url = KNOWWE.core.util.getURL({action : 'AjaxAction'});
	d3.json(url, function(error, root) {

		var nodes = tree.nodes(root.root),
			links = tree.links(nodes);

		var link = svg.selectAll(".link")
			.data(links)
			.enter().append("path")
			.attr("class", "link")
			.attr("d", diagonal);

		var node = svg.selectAll(".node")
			.data(nodes)
			.enter().append("g")
			.attr("class", "node")
			.attr("transform", function(d) {
				return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")";
			})
			.on("click", function(d) {
				d.data ? breadcrumb(d.data.name) : "";
			});

		node.append("circle")
			.on("click", function(d) {
				clickTextEventHandler(d)
			})
			.attr("r", 4.5);


		function dragmove(d) {
			d3.select(this)
				.attr("x", -d3.event.x)
				.attr("y", -d3.event.y);
		}

		function dragend(d) {
			d3.select(this)
				.attr("x", 0)
				.attr("y", 0);

		}

		var drag = d3.behavior.drag()
			.on("drag", dragmove)
			.on("dragend", dragend);


		node.append("text")
			.attr("y", "0")
			.attr("x", "0")
			.attr("text-anchor", function(d) {
				return d.x < 180 ? "start" : "end";
			})
			.attr("transform", function(d) {
				return d.x < 180 ? "translate(8)" : "rotate(180)translate(-8)";
			})
			.text(function(d) {
				return d.data ? d.data.name : "";
			})
			.on("click.info", function(d) {
				clickTextEventHandler(d)
			})
			.on("click.breadcrmb", function(d) {
				d.data ? breadcrumb(d.data.name) : "";
			})
		;


	});

//d3.select(self.frameElement).style("height", diameter - 150 + "px");

}

//Reingold-Teilford Tree End


//ReingoldCollapsable

function createReingoldCollapsible() {

	visChange();

	var diameter = 1960;

	var tree = d3.layout.tree()
		.size([360, diameter / 2 - 120])
		.separation(function(a, b) {
			return (a.parent == b.parent ? 1 : 2) / a.depth;
		});

	var diagonal = d3.svg.diagonal.radial()
		.projection(function(d) {
			return [d.y, d.x / 180 * Math.PI];
		});


	d3.select("#vis").remove();
	d3.select("body").append("div").attr("id", "vis");


	var svg = d3.select("#vis").append("svg")
		.attr("width", diameter)
		.attr("height", diameter)
		.append("g")
		.attr("transform", "translate(" + diameter / 2 + "," + diameter / 2 + ")");

	var url = KNOWWE.core.util.getURL({action : 'AjaxAction'});
	d3.json(url, function(error, root) {

		var nodes = tree.nodes(root.root),
			links = tree.links(nodes);

		var link = svg.selectAll(".link")
			.data(links)
			.enter().append("path")
			.attr("class", "link")
			.attr("d", diagonal);

		var node = svg.selectAll(".node")
			.data(nodes)
			.enter().append("g")
			.attr("class", "node")
			.attr("transform", function(d) {
				return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")";
			});

		node.append("circle")
			.on("click", function(d) {
				clickTextEventHandler(d)
			})
			.attr("r", 4.5);


		function dragmove(d) {
			d3.select(this)
				.attr("x", -d3.event.x)
				.attr("y", -d3.event.y);
		}

		function dragend(d) {
			d3.select(this)
				.attr("x", 0)
				.attr("y", 0);

		}

		var drag = d3.behavior.drag()
			.on("drag", dragmove)
			.on("dragend", dragend);


		node.append("text")
			.attr("y", "0")
			.attr("x", "0")
			.attr("text-anchor", function(d) {
				return d.x < 180 ? "start" : "end";
			})
			.attr("transform", function(d) {
				return d.x < 180 ? "translate(8)" : "rotate(180)translate(-8)";
			})
			.text(function(d) {
				return d.data.name;
			})
			.on("click", function(d) {
				clickTextEventHandler(d)
			})
			.call(drag);

		//

		//


	});

//d3.select(self.frameElement).style("height", diameter - 150 + "px");

}


//ReingoldCollapsable Ende


function createTreeCollapsable() {

	visChange();

	breadcrumb(globalStartConcept);

	d3.select("#vis").remove();
	d3.select("#center-container").append("div").attr("id", "vis");

	d3.select("#vis").attr("class", "treeCollapsable");

//    var m = [20, 120, 20, 120],
//    w = 1280 - m[1] - m[3],
//    h = 800 - m[0] - m[2],
//    i = 0,
//    root;
	//drucken
	var m = [20, 120, 20, 120],
		w = 1720 - m[1] - m[3],
		h = 1000 - m[0] - m[2],
		i = 0,
		root;
	//drucken
	var tree = d3.layout.tree()
		.size([h, w]);


	var url = KNOWWE.core.util.getURL({action : 'AjaxAction'});

	console.log("------------------");
	console.log(url);
	console.log("------------------");


	d3.json(url, function(json) {

		root = json.root;

		console.log(root);

		root.x0 = h / 2;
		root.y0 = 0;

		function toggleAll(d) {
			if (d.children) {
				d.children.forEach(toggleAll);
				toggle(d);
			}
		}

		// Initialize the display to show a few nodes.
//  console.log(root);
		root.children.each(toggleAll);
//  toggle(root.children[1]);
//  toggle(root.children[1].children[2]);
//  toggle(root.children[9]);
//  toggle(root.children[9].children[0]);

		update(root);
	});


	var diagonal = d3.svg.diagonal()
		.projection(function(d) {
			return [d.y, d.x];
		});

	var vis = d3.select("#vis").append("svg:svg")
		.attr("width", w + m[1] + m[3])
		.attr("height", h + m[0] + m[2])
		.append("svg:g")
		.attr("transform", "translate(" + m[3] + "," + m[0] + ")");


	function update(source) {
		var duration = d3.event && d3.event.altKey ? 5000 : 500;

		// Compute the new tree layout.
		var nodes = tree.nodes(root).reverse();

		// Normalize for fixed-depth.
		nodes.forEach(function(d) {
			d.y = d.depth * 180;
		});

		// Update the nodes…
		var node = vis.selectAll("g.node")
			.data(nodes, function(d) {
				return d.id || (d.id = ++i);
			});

		// Enter any new nodes at the parent's previous position.
		var nodeEnter = node.enter().append("svg:g")
				.attr("class", "node")
				.attr("transform", function(d) {
					return "translate(" + source.y0 + "," + source.x0 + ")";
				})
				.on("click", function(d) {
					d.data ? breadcrumb(d.data.name) : "";
				})
			;

		nodeEnter.append("svg:circle")
			.attr("r", 1e-6)
			.style("fill", function(d) {
				return d._children ? "lightsteelblue" : "#fff";
			})
			.on("click", function(d) {
				console.log("------------------on click");
				console.log(d.data.name);
				console.log("------------------on click children");
				console.log(d.children == null);
				console.log(d._children == null);
				toggle(d);
				update(d);
			});


		nodeEnter.append("svg:text")

			.attr("x", function(d) {
				console.log("------------------nodeEnter d");
				//              console.log(d);
				//             console.log("------------------nodeenter d.data.name");
				// console.log(d.data.name);
				// console.log("------------------");
				// console.log("------------------nodeenter d.children");
				// console.log(d.children);
				// console.log("------------------");
				// console.log("------------------nodeenter d._children");
				// console.log(d._children);
				// console.log("------------------xxxxxxxxxxx")

				if (( (d._children === undefined) || (d._children === null) )) {
					return -15
				}
				else
					return d._children.length > 0 ? -15 : 15;
			})
//         return d.children || d._children ? -15 : 15; })

			.attr("dy", ".35em")
//      .attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
			.attr("text-anchor", function(d) {
				if (( (d._children === undefined) || (d._children === null) )) {
					return "end"
				}
				else {
					return d._children.length > 0 ? "end" : "start";
				}
			})
			.text(function(d) {
				return d.data ? d.data.name : "";
			})
			.style("fill-opacity", 1e-6)
			//Breadcrumb

		;
		// Transition nodes to their new position.
		var nodeUpdate = node.transition()
			.duration(duration)
			.attr("transform", function(d) {
				return "translate(" + d.y + "," + d.x + ")";
			});

		nodeUpdate.select("circle")
			.attr("r", 4.5)
//----------------------------------------------------------------------------------------------      
// if clause d3js amateur style :) @constin @chris
//---------------------------------------------------------------------------------------------- 
//.style("fill", function(d) {if( ( (d._children === undefined)||(d._children === null) ) )
//     {return "#fff"}
//     else 
//     {return d._children.length>>0 ? "lightsteelblue" : "#fff"; };});
//----------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------      
// if clause d3js superguru style :) @constin @chris
//---------------------------------------------------------------------------------------------- 
			.style("fill", function(d) {
				return  ( ( (d._children === undefined) || (d._children === null) ) ) ? "#fff" : d._children.length >> 0 ? "lightsteelblue" : "#fff";
			});
		nodeUpdate.select("text")
			.style("fill-opacity", 1);

		// Transition exiting nodes to the parent's new position.
		var nodeExit = node.exit().transition()
			.duration(duration)
			.attr("transform", function(d) {
				return "translate(" + source.y + "," + source.x + ")";
			})
			.remove();

		nodeExit.select("circle")
			.attr("r", 1e-6);

		nodeExit.select("text")
			.style("fill-opacity", 1e-6);

		// Update the links…
		var link = vis.selectAll("path.link")
			.data(tree.links(nodes), function(d) {
				return d.target.id;
			});

		// Enter any new links at the parent's previous position.
		link.enter().insert("svg:path", "g")
			.attr("class", "link")
			.attr("d", function(d) {
				var o = {x : source.x0, y : source.y0};
				return diagonal({source : o, target : o});
			})
			.transition()
			.duration(duration)
			.attr("d", diagonal);

		// Transition links to their new position.
		link.transition()
			.duration(duration)
			.attr("d", diagonal);

		// Transition exiting nodes to the parent's new position.
		link.exit().transition()
			.duration(duration)
			.attr("d", function(d) {
				var o = {x : source.x, y : source.y};
				return diagonal({source : o, target : o});
			})
			.remove();

		// Stash the old positions for transition.
		nodes.forEach(function(d) {
			d.x0 = d.x;
			d.y0 = d.y;
		});
	}

// Toggle children.
	function toggle(d) {
		console.log("-------------------------------------------");
		console.log(d);
//  if (d !== undefined){

		if (d.children) {
			d._children = d.children;
			d.children = null;
		} else {
			d.children = d._children;
			d._children = null;
		}

//  }
	}


}


function createCollForce() {


	visChange();

	d3.select("#vis").remove();
	d3.select("#center-container").append("div").attr("id", "vis");

	d3.select("#vis").attr("class", "fdGraphLabeled");

	var w = 1720,
		h = 1000,
		root;

	var force = d3.layout.force()
		.linkDistance(80)
		.charge(-120)
		.gravity(.05)
		.size([w, h]);

	var vis = d3.select("#vis").append("svg:svg")
		.attr("width", w)
		.attr("height", h);

	var url = KNOWWE.core.util.getURL({action : 'AjaxAction'});

	console.log("------------------");
	console.log(url);
	console.log("------------------");

	d3.json(url, function(json) {
		root = json.root;
		updateDepth();
	});

	function update() {
		var nodes = flatten(root),
			links = d3.layout.tree().links(nodes);

		// Restart the force layout.
		force
			.nodes(nodes)
			.links(links)
			.start();

		// Update the links…
		var link = vis.selectAll("line.link")
			.data(links, function(d) {
				return d.target.id;
			});

		// Enter any new links.
		link.enter().insert("svg:line", ".node")
			.attr("class", "link")
			.attr("x1", function(d) {
				return d.source.x;
			})
			.attr("y1", function(d) {
				return d.source.y;
			})
			.attr("x2", function(d) {
				return d.target.x;
			})
			.attr("y2", function(d) {
				return d.target.y;
			});

		// Exit any old links.
		link.exit().remove();

		// Update the nodes…
		var node = vis.selectAll("g.node")
			.data(nodes, function(d) {
				return d.id;
			});

		node.select("circle")
			.style("fill", color);

		// Enter any new nodes.
		var nodeEnter = node.enter().append("svg:g")
			.attr("class", "node")
			.attr("transform", function(d) {
				return "translate(" + d.x + "," + d.y + ")";
			})
			.on("click", click)
			.call(force.drag);

		nodeEnter.append("svg:circle")
			.attr("r", function(d) {
				return Math.sqrt(d.size) / 20 || 16.5;
			})
			.style("fill", color);

		nodeEnter.append("svg:text")
			.attr("text-anchor", "middle")
			.attr("dy", ".35em")
			.text(function(d) {
				return d.data.name;
			});

		// Exit any old nodes.
		node.exit().remove();

		// Re-select for update.
		link = vis.selectAll("line.link");
		node = vis.selectAll("g.node");

		force.on("tick", function() {
			link.attr("x1", function(d) {
				return d.source.x;
			})
				.attr("y1", function(d) {
					return d.source.y;
				})
				.attr("x2", function(d) {
					return d.target.x;
				})
				.attr("y2", function(d) {
					return d.target.y;
				});

			node.attr("transform", function(d) {
				return "translate(" + d.x + "," + d.y + ")";
			});
		});
	}

//update to a specified depth

	function updateDepth() {
		var nodes = flatten(root),
			links = d3.layout.tree().links(nodes);

		// Restart the force layout.
		force
			.nodes(nodes)
			.links(links)
			.start();

		// Update the links…
		var link = vis.selectAll("line.link")
			.data(links, function(d) {
				return d.target.id;
			});

		// Enter any new links.
		link.enter().insert("svg:line", ".node")
			.attr("class", "link")
			.attr("x1", function(d) {
				return d.source.x;
			})
			.attr("y1", function(d) {
				return d.source.y;
			})
			.attr("x2", function(d) {
				return d.target.x;
			})
			.attr("y2", function(d) {
				return d.target.y;
			});

		// Exit any old links.
		link.exit().remove();

		// Update the nodes…
		var node = vis.selectAll("g.node")
			.data(nodes, function(d) {
				return d.id;
			});

		node.select("circle")
			.style("fill", color);

		// Enter any new nodes.
		var nodeEnter = node.enter().append("svg:g")
			.attr("class", "node")
			.attr("transform", function(d) {
				return "translate(" + d.x + "," + d.y + ")";
			})
			.on("click", click)
			.call(force.drag);

		nodeEnter.append("svg:circle")
			.attr("r", function(d) {
				return Math.sqrt(d.size) / 20 || 16.5;
			})
			.style("fill", color);

		nodeEnter.append("svg:text")
			.attr("text-anchor", "middle")
			.attr("dy", ".35em")
			.text(function(d) {
				return d.data ? d.data.name : "";
			});

		// Exit any old nodes.
		node.exit().remove();

		// Re-select for update.
		link = vis.selectAll("line.link");
		node = vis.selectAll("g.node");

		force.on("tick", function() {
			link.attr("x1", function(d) {
				return d.source.x;
			})
				.attr("y1", function(d) {
					return d.source.y;
				})
				.attr("x2", function(d) {
					return d.target.x;
				})
				.attr("y2", function(d) {
					return d.target.y;
				});

			node.attr("transform", function(d) {
				return "translate(" + d.x + "," + d.y + ")";
			});
		});
	}


// update to a specified depth


// Color leaf nodes orange, and packages white or blue.
	function color(d) {
		return d._children ? "#3182bd" : d.children ? "#c6dbef" : "#fd8d3c";
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
		update();
	}

// Returns a list of all nodes under the root.
	function flatten(root) {
		var nodes = [], i = 0;

		function recurse(node) {
			if (node.children) node.children.forEach(recurse);
			if (!node.id) node.id = ++i;
			nodes.push(node);
		}

		recurse(root);
		return nodes;
	}

}

//

//----------------------------------------------------------------------------------------------------------------------------------
function createCollForceTest() {


	visChange();

	d3.select("#vis").remove();
	d3.select("#center-container").append("div").attr("id", "vis");

	d3.select("#vis").attr("class", "fdGraphLabeled");

	var w = 1720,
		h = 1000,
		root;

	var force = d3.layout.force()
		.linkDistance(80)
		.charge(-120)
		.gravity(.05)
		.size([w, h]);

	var vis = d3.select("#vis").append("svg:svg")
		.attr("width", w)
		.attr("height", h);

	var url = KNOWWE.core.util.getURL({action : 'AjaxAction'});

	console.log("------------------");
	console.log(url);
	console.log("------------------");

	d3.json(url, function(json) {
		root = json.root;
		updateDepth();
	});

	function update() {
		var nodes = flatten(root),
			links = d3.layout.tree().links(nodes);

		// Restart the force layout.
		force
			.nodes(nodes)
			.links(links)
			.start();

		// Update the links…
		var link = vis.selectAll("line.link")
			.data(links, function(d) {
				return d.target.id;
			});

		// Enter any new links.
		link.enter().insert("svg:line", ".node")
			.attr("class", "link")
			.attr("x1", function(d) {
				return d.source.x;
			})
			.attr("y1", function(d) {
				return d.source.y;
			})
			.attr("x2", function(d) {
				return d.target.x;
			})
			.attr("y2", function(d) {
				return d.target.y;
			});

		// Exit any old links.
		link.exit().remove();

		// Update the nodes…
		var node = vis.selectAll("g.node")
			.data(nodes, function(d) {
				return d.id;
			});

		node.select("circle")
			.style("fill", color);

		// Enter any new nodes.
		var nodeEnter = node.enter().append("svg:g")
			.attr("class", "node")
			.attr("transform", function(d) {
				return "translate(" + d.x + "," + d.y + ")";
			})
			.on("click", click)
			.call(force.drag);

		nodeEnter.append("svg:circle")
			.attr("r", function(d) {
				return Math.sqrt(d.size) / 20 || 16.5;
			})
			.style("fill", color);

		nodeEnter.append("svg:text")
			.attr("text-anchor", "middle")
			.attr("dy", ".35em")
			.text(function(d) {
				return d.data.name;
			});

		// Exit any old nodes.
		node.exit().remove();

		// Re-select for update.
		link = vis.selectAll("line.link");
		node = vis.selectAll("g.node");

		force.on("tick", function() {
			link.attr("x1", function(d) {
				return d.source.x;
			})
				.attr("y1", function(d) {
					return d.source.y;
				})
				.attr("x2", function(d) {
					return d.target.x;
				})
				.attr("y2", function(d) {
					return d.target.y;
				});

			node.attr("transform", function(d) {
				return "translate(" + d.x + "," + d.y + ")";
			});
		});
	}

//update to a specified depth

	function updateDepth() {
		var nodes = flatten(root),
			links = d3.layout.tree().links(nodes);

		// Restart the force layout.
		force
			.nodes(nodes)
			.links(links)
			.start();

		// Update the links…
		var link = vis.selectAll("line.link")
			.data(links, function(d) {
				return d.target.id;
			});

		// Enter any new links.
		link.enter().insert("svg:line", ".node")
			.attr("class", "link")
			.attr("x1", function(d) {
				return d.source.x;
			})
			.attr("y1", function(d) {
				return d.source.y;
			})
			.attr("x2", function(d) {
				return d.target.x;
			})
			.attr("y2", function(d) {
				return d.target.y;
			});

		// Exit any old links.
		link.exit().remove();

		// Update the nodes…
		var node = vis.selectAll("g.node")
			.data(nodes, function(d) {
				return d.id;
			});

		node.select("circle")
			.style("fill", color);

		// Enter any new nodes.
		var nodeEnter = node.enter().append("svg:g")
			.attr("class", "node")
			.attr("transform", function(d) {
				return "translate(" + d.x + "," + d.y + ")";
			})
			.on("click", click)
			.call(force.drag);

		nodeEnter.append("svg:circle")
			.attr("r", function(d) {
				return Math.sqrt(d.size) / 20 || 16.5;
			})
			.style("fill", color);

		nodeEnter.append("svg:text")
			.attr("text-anchor", "middle")
			.attr("dy", ".35em")
			.text(function(d) {
				return d.data.name;
			});

		// Exit any old nodes.
		node.exit().remove();

		// Re-select for update.
		link = vis.selectAll("line.link");
		node = vis.selectAll("g.node");

		force.on("tick", function() {
			link.attr("x1", function(d) {
				return d.source.x;
			})
				.attr("y1", function(d) {
					return d.source.y;
				})
				.attr("x2", function(d) {
					return d.target.x;
				})
				.attr("y2", function(d) {
					return d.target.y;
				});

			node.attr("transform", function(d) {
				return "translate(" + d.x + "," + d.y + ")";
			});
		});
	}


// update to a specified depth


// Color leaf nodes orange, and packages white or blue.
	function color(d) {
		return d._children ? "#3182bd" : d.children ? "#c6dbef" : "#fd8d3c";
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
		update();
	}

// Returns a list of all nodes under the root.
	function flatten(root) {
		var nodes = [], i = 0;

		function recurse(node) {
			if (node.children) node.children.forEach(recurse);
			if (!node.id) node.id = ++i;
			nodes.push(node);
		}

		recurse(root);
		return nodes;
	}

}

//

//----------------------------------------------------------------------------------------------------------------------------------

//-----------------------------------------------------
function createFixRootTree() {

	visChange();

	d3.select("#vis").remove();
	d3.select("body").append("div").attr("id", "vis");

	d3.select("#vis").attr("class", "fixedRootTree");


	var w = 1280,
		h = 800;

	var force = d3.layout.force()
		.gravity(0)
		.charge(-60);

	var svg = d3.select("#vis").append("svg:svg")
		.attr("width", w)
		.attr("height", h);

	svg.append("svg:rect")
		.attr("width", w)
		.attr("height", h);

	var url = KNOWWE.core.util.getURL({action : 'AjaxAction'});

	console.log("------------------");
	console.log(url);
	console.log("------------------");

	d3.json(url, function(json) {

		root = json.root;
		var nodes = flatten(root),
			links = d3.layout.tree().links(nodes);

		root.fixed = true;
		root.x = w / 2;
		root.y = 120;

		force
			.nodes(nodes)
			.links(links)
			.start();

		var link = svg.selectAll("line")
			.data(links)
			.enter().insert("svg:line");

		var node = svg.selectAll("circle.node")
			.data(nodes)
			.enter().append("svg:circle")
			.attr("r", 4.5)
			.call(force.drag);

		force.on("tick", function(e) {

			var kx = .4 * e.alpha, ky = 1.4 * e.alpha;
			links.forEach(function(d, i) {
				d.target.x += (d.source.x - d.target.x) * kx;
				d.target.y += (d.source.y + 80 - d.target.y) * ky;
			});

			link.attr("x1", function(d) {
				return d.source.x;
			})
				.attr("y1", function(d) {
					return d.source.y;
				})
				.attr("x2", function(d) {
					return d.target.x;
				})
				.attr("y2", function(d) {
					return d.target.y;
				});

			node.attr("cx", function(d) {
				return d.x;
			})
				.attr("cy", function(d) {
					return d.y;
				});
		});
	});

	function flatten(root) {
		var nodes = [];

		function recurse(node) {
			if (node.children) node.children.forEach(recurse);
			nodes.push(node);
		}

		recurse(root);
		return nodes;
	}
}
//
function visChange() {
	jq$("#infolist").removeClass('unhidden');
	jq$("#infolist").addClass('hidden');
}





