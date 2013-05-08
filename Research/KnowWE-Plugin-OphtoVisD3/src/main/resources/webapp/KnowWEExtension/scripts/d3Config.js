function createWheel(){
	console.log("Anfang wheel");
	d3.select("#vis").remove();
	d3.select("body").append("div").attr("id", "vis");
var width = 840,
    height = width,
    radius = width / 2,
    x = d3.scale.linear().range([0, 2 * Math.PI]),
    y = d3.scale.pow().exponent(1.3).domain([0, 1]).range([0, radius]),
    padding = 5,
    duration = 1000;

var div = d3.select("#vis");
//div.append("text").text("Hallo");
//terst
//div.select("img").remove();

var vis = div.append("svg")
    .attr("width", width + padding * 2)
    .attr("height", height + padding * 2)
  .append("g")
    .attr("transform", "translate(" + [radius + padding, radius + padding] + ")");



var partition = d3.layout.partition()
    .sort(null)
    .value(function(d) {console.log(d); return 5.8 - d.depth; });

var arc = d3.svg.arc()
    .startAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x))); })
    .endAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx))); })
    .innerRadius(function(d) { return Math.max(0, d.y ? y(d.y) : d.y); })
    .outerRadius(function(d) { return Math.max(0, y(d.y + d.dy)); });

var url = KNOWWE.core.util.getURL({action : 'AjaxAction'});
	d3.json(url, function(json) {
		console.log(json);
  var nodes = partition.nodes({children: json.children});
  nodes= nodes[0].children;
  console.log("jetzt");
  console.log(nodes);
	var color = d3.scale.category20();
  var path = vis.selectAll("path").data(nodes);
  path.enter().append("path")
      .attr("id", function(d, i) { return "path-" + i; })
      .attr("d", arc)
      .style("fill",function(d, i) { return color(i); })
      .on("click", click);

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
          return (e.depth<2) ? null : d3.select(this).style("visibility", "hidden");
        })
      .on("click", click);
      console.log("läuft");
  textEnter.append("tspan")
      .attr("x", 0)
      .text(function(d) {console.log(d); return d.depth ? d.data.name.split(" ")[0] : ""; });
  textEnter.append("tspan")
      .attr("x", 0)
      .attr("dy", "1em")
      .text(function(d) { return d.depth ? d.data.name.split(" ")[1] || "" : ""; });

  function click(d) {
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
        .style("fill-opacity", function(e) { return isParentOf(d, e) ? 1 : 1e-6; })
        .each("end", function(e) {
          d3.select(this).style("visibility", ((isParentOf(d, e)) && (d.depth-e.depth) >-2) ? null : "hidden");
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

function colour(d) {
  if (d.children) {
    // There is a maximum of two children!
    var colours = d.children.map(colour),
        a = d3.hsl(colours[0]),
        b = d3.hsl(colours[1]);
    // L*a*b* might be better here...
    return d3.hsl((a.h + b.h) / 2, a.s * 1.2, a.l / 1.2);
  }
  return d.colour || "#fff";
}

// Interpolate the scales!
function arcTween(d) {
  var my = maxY(d),
      xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
      yd = d3.interpolate(y.domain(), [d.y, my]),
      yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
  return function(d) {
    return function(t) { x.domain(xd(t)); y.domain(yd(t)).range(yr(t)); return arc(d); };
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
function createKnoten(){	
	var w = 1280,
    h = 800,
    r = 720,
    x = d3.scale.linear().range([0, r]),
    y = d3.scale.linear().range([0, r]),
    node,
    root;

var pack = d3.layout.pack()
    .size([r, r])
    .value(function(d) { return d.size; })

var vis = d3.select("body").insert("svg:svg", "h2")
    .attr("width", w)
    .attr("height", h)
  .append("svg:g")
    .attr("transform", "translate(" + (w - r) / 2 + "," + (h - r) / 2 + ")");
var url = KNOWWE.core.util.getURL({action : 'AjaxAction'});
d3.json( url , function(data) {
  node = root = data;

  var nodes = pack.nodes(root);

  vis.selectAll("circle")
      .data(nodes)
    .enter().append("svg:circle")
      .attr("class", function(d) { return d.children ? "parent" : "child"; })
      .attr("cx", function(d) { return d.x; })
      .attr("cy", function(d) { return d.y; })
      .attr("r", function(d) { return d.r; })
      .on("click", function(d) { return zoom(node == d ? root : d); });

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

function createBubble(){
	
	d3.select("#vis").remove();
	d3.select("body").append("div").attr("id", "vis");
var diameter = 960,
    format = d3.format(",d");

var pack = d3.layout.pack()
    .size([ diameter - 4, diameter - 4] )
    .value(function(d) {  console.log(d); return (d.data.data)*200 });

var svg = d3.select("#vis").append("svg")
    .attr("width", diameter)
    .attr("height", diameter+30)
  .append("g")
    .attr("transform", "translate(2,2)");

var params = { 
 action : 'AjaxAction' ,  
 type : 'bubble'};
var url = KNOWWE.core.util.getURL(params);
d3.json(url, function(error, root) {


  var node = svg.datum(root).selectAll(".node")
      .data(pack.nodes)
    .enter().append("g")
      .attr("class", function(d) { console.log(d.children);  return d.children.length>0 ? "bubbleNode" : "leaf bubbleNode"; })
      .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
      .attr("oldx" , function(d) { return d.x; })
      .attr("oldy", function(d) { return d.y ; });
  
  
    
  node.append("title")
      .text(function(d) { return d.data.name  });

  function dragmove(d) {
	    d3.select(this)
	      .attr("x", d3.event.x)
	      .attr("y", d3.event.y);
	}
	function dragend(d){
	d3.select(this)
	      .attr("x", 0)
	      .attr("y", 0);
	      
	}
	var drag = d3.behavior.drag()
	  .on("drag", dragmove)
		.on("dragend", dragend);
  node.append("circle")
      .attr("r", function(d) {  return (d.r); }).call(drag);

  node.filter(function(d) { return !d.children.length>0; }).append("text")
      .attr("dy", ".3em")
      .style("text-anchor", "middle")
      .text(function(d) { return d.data.name; }).call(drag);

});

d3.select(self.frameElement).style("height", diameter + "px");

  
}
function createTree(){


var diameter = 960;

var tree = d3.layout.tree()
    .size([360, diameter / 2 - 120])
    .separation(function(a, b) { return (a.parent == b.parent ? 1 : 2) / a.depth; });

var diagonal = d3.svg.diagonal.radial()
    .projection(function(d) { return [d.y, d.x / 180 * Math.PI]; });


d3.select("#vis").remove();
d3.select("body").append("div").attr("id", "vis");
console.log(vis);

var svg = d3.select("#vis").append("svg")
    .attr("width", diameter)
    .attr("height", diameter - 150)
  .append("g")
    .attr("transform", "translate(" + diameter / 2 + "," + diameter / 2 + ")");

var url = KNOWWE.core.util.getURL({action : 'AjaxAction'});
d3.json( url, function(error, root) {
  var nodes = tree.nodes(root),
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
      .attr("transform", function(d) { return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")"; })

  node.append("circle")
      .attr("r", 4.5);
 
  
  function dragmove(d) {
	  console.log(this);
	    d3.select(this)
	      .attr("x", -d3.event.x)
	      .attr("y", -d3.event.y);
	}
	function dragend(d){
		console.log(d3.event);
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
	  .attr("text-anchor", function(d) { return d.x < 180 ? "start" : "end"; })
	  .attr("transform", function(d) { return d.x < 180 ? "translate(8)" : "rotate(180)translate(-8)"; })
	  .text(function(d) {console.log("ready"); return d.data.name; })
	  .call(drag);

	
 
});	


//d3.select(self.frameElement).style("height", diameter - 150 + "px");





}

