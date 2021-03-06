function createSidebarTree(con) {
	var url = KNOWWE.core.util.getURL({action : 'PartTreeAction', concept : con});
	var w = 300,
		h = 800,
		i = 0,
		barHeight = 20,
		barWidth = w * .7,
		duration = 400,
		root = "";

	var tree = d3.layout.tree()
		.size([h, 30]);

	var diagonal = d3.svg.diagonal()
		.projection(function(d) {
			return [d.y, d.x];
		});

	var vis = d3.select("#chart").append("svg:svg")
		.attr("width", w)
		.attr("height", h)
		.append("svg:g")
		.attr("transform", "translate(10,10)");


	d3.json(url, function(json) {
		json.x0 = 0;
		json.y0 = 0;
		update(root = json.root);
	});

	function update(source) {

		// Compute the flattened node list. TODO use d3.layout.hierarchy.

		var nodes = tree.nodes(root);

		// Compute the "layout".
		var high = 0;
		nodes.forEach(function(n, i) {
			n.x = i * barHeight;
			high++;
		});
		jQuery("svg").attr("height", high * barHeight + 10);
		// Update the nodes…
		var node = vis.selectAll("g.node")
			.data(nodes, function(d) {
				return d.id || (d.id = ++i);
			});

		var nodeEnter = node.enter().append("svg:g")
			.attr("class", "node")
			.attr("transform", function(d) {
				return "translate(" + source.y0 + "," + source.x0 + ")";
			})
			.style("opacity", 1e-6);

		// Enter any new nodes at the parent's previous position.
		nodeEnter.append("svg:rect")
			.attr("y", -barHeight / 2)
			.attr("height", barHeight)
			.attr("width", barWidth)
			.style("fill", color)
			.on("click", click);

		nodeEnter.append("svg:text")
			.attr("dy", 3.5)
			.attr("dx", 5.5)
			.text(function(d) {
				return d.data.name;
			});

		// Transition nodes to their new position.
		nodeEnter.transition()
			.duration(duration)
			.attr("transform", function(d) {
				return "translate(" + d.y + "," + d.x + ")";
			})
			.style("opacity", 1);

		node.transition()
			.duration(duration)
			.attr("transform", function(d) {
				return "translate(" + d.y + "," + d.x + ")";
			})
			.style("opacity", 1)
			.select("rect")
			.style("fill", color);

		// Transition exiting nodes to the parent's new position.
		node.exit().transition()
			.duration(duration)
			.attr("transform", function(d) {
				return "translate(" + source.y + "," + source.x + ")";
			})
			.style("opacity", 1e-6)
			.remove();

		// Update the links…
		var link = vis.selectAll("path.link")
			.data(tree.links(nodes), function(d) {
				return d.target.id;
			});

		// Enter any new links at the parent's previous position.
		link.enter().insert("svg:path", "g")
			.attr("class", "link")
			.attr("d", function(d) {
				var o = {x : source.x, y : source.y};
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

	function color(d) {
		return d.data.highlighted ? "#0855DD" : d._children ? "#232323" : d.children.length > 0 ? "#ababab" : "#888888";
	}

}
function renderConnections(url) {
	d3.json(url, function(json) {
		jQuery('#infolist').html('');
		d3.select("#infolist").append("div").attr("class", "connectionInfo2");

		d3.select(".connectionInfo2").append("ul").attr("id", "outgoing").text("Ausgehende Relationen");
		for (var i = 0; i < json.outgoing.length; i++) {
			var such = json.outgoing[i][0];
			if ((d3.select("#outgoing").select('#' + such)[0][0]) == null) {
				d3.select("#outgoing").append("ul").attr("id", json.outgoing[i][0]).text("\"" + json.outgoing[i][0] + "\"");
			}
		}
		for (var i = 0; i < json.outgoing.length; i++) {
			var kat = json.outgoing[i][0];
			d3.select("#outgoing").select('#' + kat)
				.append("li")
				.html(" <a href=\"Wiki.jsp?page=" + json.outgoing[i][1] + "\" > " + json.outgoing[i][1] + " </a> ");


		}
		d3.select(".connectionInfo2").append("ul").attr("id", "incoming").text("Eingehende Relationen");
		for (var i = 0; i < json.incoming.length; i++) {
			var such = json.incoming[i][0];
			if ((d3.select("#incoming").select('#' + such)[0][0]) == null) {
				d3.select("#incoming").append("ul").attr("id", json.incoming[i][0]).text("\"" + json.incoming[i][0] + "\"");
			}
		}
		for (var i = 0; i < json.incoming.length; i++) {
			var kat = json.incoming[i][0];
			d3.select("#incoming").select('#' + kat).append("li")
				.html(" <a href=\"Wiki.jsp?page=" + json.incoming[i][1] + "\" > " + json.incoming[i][1] + " </a> ");
		}

	});

}

function collapseInfobox() {
	jQuery("#chart").toggleClass("hidden");
	jQuery(".colapser").text(jQuery(".colapser").text() === "Infobox  ▲" ? "Infobox  ▼" : "Infobox  ▲");


}