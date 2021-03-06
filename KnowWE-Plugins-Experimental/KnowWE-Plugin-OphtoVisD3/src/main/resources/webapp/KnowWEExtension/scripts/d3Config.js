var globalStartConcept;

function createWheel() {

	visChange();

	breadcrumb(globalStartConcept);

	d3.select("#vis").remove();
	d3.select("#center-container").append("div").attr("id", "vis");
	var width = 840,
		height = width,
		radius = width / 2,
		x = d3.scale.linear().range([0, 2 * Math.PI]),
		y = d3.scale.pow().exponent(1.3).domain([0, 1]).range([0, radius]),
		padding = 5,
		duration = 1000;

	var div = d3.select("#vis");


	var vis = div.append("svg")
		.attr("width", width + padding * 2)
		.attr("height", height + padding * 2)
		.append("g")
		.attr("transform", "translate(" + [radius + padding, radius + padding] + ")");


	var partition = d3.layout.partition()
		.sort(null)
		.value(function(d) {
			return 5.8 - d.depth;
		});

	var arc = d3.svg.arc()
		.startAngle(function(d) {
			return Math.max(0, Math.min(2 * Math.PI, x(d.x)));
		})
		.endAngle(function(d) {
			return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx)));
		})
		.innerRadius(function(d) {
			return Math.max(0, d.y ? y(d.y) : d.y);
		})
		.outerRadius(function(d) {
			return Math.max(0, y(d.y + d.dy));
		});

	var url = KNOWWE.core.util.getURL({action : 'AjaxAction'});
	d3.json(url, function(json) {
		var nodes = partition.nodes({children : json.root.children});

		var color = d3.scale.category20();
		var path = vis.selectAll("path").data(nodes);
		path.enter().append("path")
			.attr("id", function(d, i) {
				return "path-" + i;
			})
			.attr("d", arc)
			.style("fill", function(d) {
				return colour(d);
			})
			.on("click", function(d) {
				d.data ? breadcrumb(d.data.name) : "";
			});
		//.on("click", click);

		var text = vis.selectAll("text").data(nodes);
		var textEnter = text.enter().append("text")
			.style("fill-opacity", 1)
			.attr("text-anchor", function(d) {
				return x(d.x + d.dx / 2) > Math.PI ? "end" : "start";
			})
			.attr("dy", ".2em")
			.attr("transform", function(d) {
				var multiline = (d.name || "").split(" ").length > 1,
					angle = x(d.x + d.dx / 2) * 180 / Math.PI - 90,
					rotate = angle + (multiline ? -.5 : 0);
				return "rotate(" + rotate + ")translate(" + (y(d.y) + padding) + ")rotate(" + (angle > 90 ? -180 : 0) + ")";
			})
			.attr("visibility", function(e) {
				return (e.depth < 2) ? null : d3.select(this).style("visibility", "hidden");
			})
			.on("click.info", function(d) {
				clickTextEventHandler(d)
			})
			.on("click.breadcrmb", function(d) {
				d.data ? breadcrumb(d.data.name) : "";
			});

		textEnter.append("tspan")
			.attr("x", 0)
			.text(function(d) {
				return d.depth ? d.data.label.split(" ")[0] : "";
			});
		textEnter.append("tspan")
			.attr("x", 0)
			.attr("dy", "1em")
			.text(function(d) {
				return d.depth ? d.data.label.split(" ")[1] || "" : "";
			});

		function click(d) {
			clickTextEventHandler(d);
			path.transition()
				.duration(duration)
				.attrTween("d", arcTween(d));

			// Somewhat of a hack as we rely on arcTween updating the scales.
			text.style("visibility", function(e) {
				return isParentOf(d, e) ? d3.select(this).style("visibility", "hidden") : d3.select(this).style("visibility", "hidden");
			})
				.transition()
				.duration(duration)
				.attrTween("text-anchor", function(d) {
					return function() {
						return x(d.x + d.dx / 2) > Math.PI ? "end" : "start";
					};
				})
				.attrTween("transform", function(d) {
					var multiline = (d.data.name || "").split(" ").length > 1;
					return function() {
						var angle = x(d.x + d.dx / 2) * 180 / Math.PI - 90,
							rotate = angle + (multiline ? -.5 : 0);
						return "rotate(" + rotate + ")translate(" + (y(d.y) + padding) + ")rotate(" + (angle > 90 ? -180 : 0) + ")";
					};
				})
				.style("fill-opacity", function(e) {
					return isParentOf(d, e) ? 1 : 1e-6;
				})
				.each("end", function(e) {
					d3.select(this).style("visibility", ((isParentOf(d, e)) && (d.depth - e.depth) > -2) ? null : "hidden");
				});
		}
	});

	function isParentOf(p, c) {
		if (p === c) return true;
		if (p.children) {
			return p.children.some(function(d) {
				return isParentOf(d, c);
			});
		}
		return false;
	}

	function colour(daten) {
		/*  if (d.children) {
		 // There is a maximum of two children!
		 var colours = d.children.map(colour),
		 a = d3.hsl(colours[0]),
		 b = d3.hsl(colours[1]);
		 // L*a*b* might be better here...
		 return d3.hsl((a.h + b.h) / 2, a.s * 1.2, a.l / 1.2);
		 }
		 return d.colour || "#fff";*/

		return  daten.children ? daten.children[0] ? (daten.children.some(function(c) {
			return c.children[0];
		})
			? "#395277"
			: "#577aae" )
			: "#90a7ca"
			: "#90a7ca";
	}

// Interpolate the scales!
	function arcTween(d) {
		var my = maxY(d),
			xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
			yd = d3.interpolate(y.domain(), [d.y, my]),
			yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
		return function(d) {
			return function(t) {
				x.domain(xd(t));
				y.domain(yd(t)).range(yr(t));
				return arc(d);
			};
		};
	}

	function maxY(d) {
		return d.children ? Math.max.apply(Math, d.children.map(maxY)) : d.y + d.dy;
	}

// http://www.w3.org/WAI/ER/WD-AERT/#color-contrast
	function brightness(rgb) {
		return rgb.r * .299 + rgb.g * .587 + rgb.b * .114;
	}
}
function createKnoten() {
	var w = 1280,
		h = 800,
		r = 720,
		x = d3.scale.linear().range([0, r]),
		y = d3.scale.linear().range([0, r]),
		node,
		root;

	var pack = d3.layout.pack()
		.size([r, r])
		.value(function(d) {
			return d.size;
		});

	var vis = d3.select("body").insert("svg:svg", "h2")
		.attr("width", w)
		.attr("height", h)
		.append("svg:g")
		.attr("transform", "translate(" + (w - r) / 2 + "," + (h - r) / 2 + ")");
	var url = KNOWWE.core.util.getURL({action : 'AjaxAction'});
	d3.json(url, function(data) {
		node = root = data;

		var nodes = pack.nodes(root);

		vis.selectAll("circle")
			.data(nodes)
			.enter().append("svg:circle")
			.attr("class", function(d) {
				return d.children ? "parent" : "child";
			})
			.attr("cx", function(d) {
				return d.x;
			})
			.attr("cy", function(d) {
				return d.y;
			})
			.attr("r", function(d) {
				return d.r;
			})
			.on("click", function(d) {
				return zoom(node == d ? root : d);
			});

  vis.selectAll("text")
      .data(nodes)
    .enter().append("svg:text")
      .attr("class", function(d) { return d.children ? "parent" : "child"; })
      .attr("x", function(d) { return (d.x+100); })
      .attr("y", function(d) { return d.y; })
      .attr("dy", ".35em")
      .attr("text-anchor", "middle")
      .style("opacity", function(d) { return d.r > 20 ? 1 : 0; })
      .text(function(d) { return d.name; });

  d3.select(window).on("click", function() { zoom(root); });
});

function zoom(d, i) {
  var k = r / d.r / 2;
  x.domain([d.x - d.r, d.x + d.r]);
  y.domain([d.y - d.r, d.y + d.r]);

  var t = vis.transition()
      .duration(d3.event.altKey ? 7500 : 750);

  t.selectAll("circle")
      .attr("cx", function(d) { return x(d.x); })
      .attr("cy", function(d) { return y(d.y); })
      .attr("r", function(d) { return k * d.r; });

  t.selectAll("text")
      .attr("x", function(d) { return x(d.x); })
      .attr("y", function(d) { return y(d.y); })
      .style("opacity", function(d) { return k * d.r > 20 ? 1 : 0; });

  node = d;
  d3.event.stopPropagation();
}
}
function createForce(){
		//Width and height
			var w = 500;
			var h = 300;

			//Original data
			var params = { 
 							action : 'AjaxAction' ,  
							type : 'force'};
			var url = KNOWWE.core.util.getURL(params);
			d3.json(url, function(error, root) {
		
			//Initialize a default force layout, using the nodes and edges in dataset
			var force = d3.layout.force()
								 .nodes(root.nodes)
								 .links(root.edges)
								 .size([w, h])
								 .linkDistance([50])
								 .charge([-100])
								 .start();

	
			var colors = d3.scale.category10();

			//Create SVG element
			var svg = d3.select("body")
						.append("svg")
						.attr("width", w)
						.attr("height", h);

			//Create edges as lines
			var edges = svg.selectAll("line")
				.data(root.edges)
				.enter()
				.append("line")
				.style("stroke", "#ccc")
				.style("stroke-width", 1);

			//Create nodes as circles
			var nodes = svg.selectAll("circle")
				.data(root.nodes)
				.enter()
				.append("circle")
				.attr("r", 10)
				.style("fill", function(d, i) {
					return colors(i);
				})
				.call(force.drag);


			//Every time the simulation "ticks", this will be called
			force.on("tick", function() {

				edges.attr("x1", function(d) { return d.source.x; })
					 .attr("y1", function(d) { return d.source.y; })
					 .attr("x2", function(d) { return d.target.x; })
					 .attr("y2", function(d) { return d.target.y; });

				nodes.attr("cx", function(d) { return d.x; })
					 .attr("cy", function(d) { return d.y; });

			});

	}  )  ; 
	

}

function visChange(){
	jq$("#infolist").removeClass('unhidden');
	jq$("#infolist").addClass('hidden');
}
//TODO Infobar anzeigen
function clickTextEventHandler(d){
	
	var clickedName = d.data.name;

	jq$("#infolist").addClass('unhidden');
		
	var url = KNOWWE.core.util.getURL({action : 'ConnectionsAction', concept : clickedName});
	renderConnections(url);
	
	
}


function createBubble(concept) {

	if (concept) {
		globalStartConcept = concept
	} else {
		globalStartConcept = "Praeop Vorbereitung"
	}
	visChange();

	breadcrumb(globalStartConcept);

	d3.select("#vis").remove();
	d3.select("#center-container").append("div").attr("id", "vis");

	var w = 1720,
		h = 1000,
		r = 720,
		x = d3.scale.linear().range([0, r]),
		y = d3.scale.linear().range([0, r]),
		node,
		root;

	var pack = d3.layout.pack()
		.size([r, r])
		.value(function(d) {
			return d.data.data;
		});

	var vis = d3.select("#vis").insert("svg:svg", "h2")
		.attr("width", w)
		.attr("height", h)
		.append("svg:g")
		.attr("transform", "translate(" + (w - r) / 2 + "," + (h - r) / 2 + ")");
	var params = {
		action : 'AjaxAction',
		type : 'bubble'};
	var url = KNOWWE.core.util.getURL(params);
	d3.json(url, function(data) {
		node = root = data.root;

		var nodes = pack.nodes(root);

		vis.selectAll("circle")
			.data(nodes)
			.enter().append("svg:circle")
			.attr("class", function(d) {
				return d.data ? d.data.highlighted ? "highlighted" : d.children ? "parent" : "child" : "";
			})
			.attr("cx", function(d) {
				return d.x;
			})
			.attr("cy", function(d) {
				return d.y;
			})
			.attr("r", function(d) {
				return d.r;
			})
			.on("click.zoom", function(d) {
				return zoom(node == d ? root : d);
			})
			.on("click.breadcrmb", function(d) {
				d.data ? breadcrumb(d.data.name) : "";
			})
		;

		vis.selectAll("text")
			.data(nodes)
			.enter().append("svg:text")
			.attr("class", function(d) {
				return d.children ? "parent" : "child";
			})
			.attr("x", function(d) {
				return d.x;
			})
			.attr("y", function(d) {
				return (d.children[0]) ? (d.y + 0.75 * d.r) : d.y;
			})
			.attr("dy", ".35em")
			.attr("text-anchor", "middle")
			.style("opacity", function(d) {
				return d.r > 20 ? 1 : 0;
			})
			// if d.data ein Attribut in js, then return d.data label, else return ""
			.text(function(d) {
				return d.data ? d.data.label : "";
			});

		d3.select(window).on("click", function() {
			zoom(root);
		});
	});

	function zoom(d, i) {
		var k = r / d.r / 2;
		x.domain([d.x - d.r, d.x + d.r]);
		y.domain([d.y - d.r, d.y + d.r]);

		var t = vis.transition()
			.duration(d3.event.altKey ? 7500 : 750);

		t.selectAll("circle")
			.attr("cx", function(d) {
				return x(d.x);
			})
			.attr("cy", function(d) {
				return y(d.y);
			})
			.attr("r", function(d) {
				return k * d.r;
			});

		t.selectAll("text")
			.attr("x", function(d) {
				return x(d.x);
			})
			.attr("y", function(d) {
				return (d.children[0]) ? (y(d.y) + 0.75 * d.r) : y(d.y);
			})
			.style("opacity", function(d) {
				return k * d.r > 20 ? 1 : 0;
			});

		node = d;
		d3.event.stopPropagation();
	}
	
	window.addEventListener('load', function(e){
jq$(document).ready(function() {
$(window).keydown(function(event){
if(event.keyCode == 13) {
event.preventDefault();
return false;
}
});
});
});
	
}
	


