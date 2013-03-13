window.addEvent( 'domready',
		function(){

var elem = jq$("#parseme");
var srcc = elem.text();
var graph = jq$.parseJSON(srcc);

var links = graph.links;

var nodes = {};

links.forEach(function(link) {
  link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
  link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});
});

var w = 1000,
    h = 600;


var force = d3.layout.force()
    .nodes(d3.values(nodes))
    .links(links)
    .size([w, h])
    .linkDistance(60)
    .charge(-500)
    .on("tick", tick)
    .start();

var x = d3.scale.linear()
.domain([-w / 2, w / 2])
.range([0, w]);

var y = d3.scale.linear()
.domain([-h / 2, h / 2])
.range([h, 0]);

var svg = d3.select("#chart")
	.append("svg:svg")
    .attr("width", w)
    .attr("height", h)
    .append("svg:g")
    .attr("pointer-events", "all")
    .call(d3.behavior.zoom()
    		.scaleExtent([0.1 * h, 10 * h])
    		.scale(h)
    		.on("zoom", function(){
    	svg.attr("transform",
    		      "translate(" + d3.event.translate + ")"
    		      + " scale(" + d3.event.scale + ")");
    }));

var symbol = d3.svg.symbol();

svg.append("svg:defs").selectAll("marker")
    .data(["arrow"])
  .enter().append("svg:marker")
    .attr("id", String)
    .attr("viewBox", "0 -5 10 10")
    .attr("refX", 15)
    .attr("refY", -1.5)
    .attr("markerWidth", 6)
    .attr("markerHeight", 6)
    .attr("orient", "auto")
  .append("svg:path")
    .attr("d", "M0,-5L10,0L0,5");

var path = svg.append("svg:g").selectAll("path")
    .data(force.links())
  .enter().append("svg:path")
    .attr("class", function(d) { return "link " + d.type; })
    .attr("marker-end", function(d) { return "url(#arrow)"; });

var circle = svg.append("svg:g").selectAll("path")
    .data(force.nodes())
  .enter().append("svg:path")
  	.attr("d", symbol.type("circle"))	
    .call(force.drag);

var text = svg.append("svg:g").selectAll("g")
    .data(force.nodes())
  .enter().append("svg:g");

text.append("svg:text")
    .attr("x", 8)
    .attr("y", ".31em")
    .attr("class", "shadow")
    .text(function(d) { return d.name; });

text.append("svg:text")
    .attr("x", 8)
    .attr("y", ".31em")
    .text(function(d) { return d.name; });

function tick() {
  path.attr("d", function(d) {
    var dx = d.target.x - d.source.x,
        dy = d.target.y - d.source.y,
        dr = Math.sqrt(dx * dx + dy * dy);
    return "M" + d.source.x + "," + d.source.y + " " + d.target.x + "," + d.target.y;
  });

  circle.attr("transform", function(d) {
    return "translate(" + d.x + "," + d.y + ")";
  });

  text.attr("transform", function(d) {
    return "translate(" + d.x + "," + d.y + ")";
  });
}
});