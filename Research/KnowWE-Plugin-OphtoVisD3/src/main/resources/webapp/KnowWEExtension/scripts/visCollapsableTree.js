function createTreeDiagonal(){
    
    visChange();
    
    d3.select("#vis").remove();
    d3.select("body").append("div").attr("id", "vis");
    
    d3.select("#vis").attr("class", "treeDiagonal");

     //JSON object with the data
      var treeData = {"name" : "A", "info" : "tst", "children" : [
            {"name" : "A1" },
            {"name" : "A2" },
            {"name" : "A3", "children": [
                  {"name" : "A31", "children" :[
            {"name" : "A311" },
            {"name" : "A312" }
    ]}] }
      ]};
      
      var url = KNOWWE.core.util.getURL({action : 'AjaxAction'});
    
        console.log("------------------");
                console.log(url);
                        console.log("------------------");
                        
                        
    var tree = d3.layout.tree()
    .size([1200,600]);
    
    
d3.json( url, function(error, root) {

  var nodes = tree.nodes(root.root),
      links = tree.links(nodes);
            console.log(root);
      

 
      // Create a svg canvas
      var vis = d3.select("#vis").append("svg:svg")
      .attr("width", 1200)
      .attr("height", 1600)
      .append("svg:g")
      .attr("transform", "translate(160, 100)"); // shift everything to the right
 
      // Create a tree "canvas"

 
      var diagonal = d3.svg.diagonal()
      // change x and y (for the left to right tree)
   //   .projection(function(d) { return [d.y, d.x]; });
            .projection(function(d) { return [d.y, d.x]; });
 
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
      .attr("d", diagonal)
 
      var node = vis.selectAll("g.node")
      .data(nodes)
      .enter().append("svg:g")
      .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; })
 
 
 
 
      // Add the dot at every node
      node.append("svg:circle")
      .attr("r", 3.5);
 
      // place the name atribute left or right depending if children
      node.append("svg:text")
      .attr("dx", function(d) { return d.children[0] ? -8 : 8; })
      .attr("dy", 3)
      .attr("text-anchor", function(d) { console.log(d.children[0]); return d.children[0] ? "end" : "start"; })
      .text(function(d) { return d.data.name; });
      
         });//json
    
}








function createTreeCollapsable(){
    
    visChange();
    
    d3.select("#vis").remove();
    d3.select("body").append("div").attr("id", "vis");
    
    d3.select("#vis").attr("class", "treeCollapsable");
    
    var m = [20, 120, 20, 120],
    w = 1280 - m[1] - m[3],
    h = 800 - m[0] - m[2],
    i = 0,
    root;

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
    .projection(function(d) { return [d.y, d.x]; });

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
  nodes.forEach(function(d) { d.y = d.depth * 180; });

  // Update the nodes…
  var node = vis.selectAll("g.node")
      .data(nodes, function(d) { return d.id || (d.id = ++i); });

  // Enter any new nodes at the parent's previous position.
  var nodeEnter = node.enter().append("svg:g")
      .attr("class", "node")
      .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
      .on("click", function(d) { 
          console.log("------------------on click");
          console.log(d.data.name);
                    console.log("------------------on click children");
          console.log(d.children == null);
                    console.log(d._children == null);
          toggle(d); update(d); });

  nodeEnter.append("svg:circle")
      .attr("r", 1e-6)
     .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

      

  nodeEnter.append("svg:text")

     .attr("x", function(d) { 
         console.log("------------------nodeEnter d");
                console.log(d);
                console.log("------------------nodeenter d.data.name");
                console.log(d.data.name);
                console.log("------------------");
                console.log("------------------nodeenter d.children");
                console.log(d.children);
                console.log("------------------");
                console.log("------------------nodeenter d._children");
                console.log(d._children);
                console.log("------------------xxxxxxxxxxx")
                
                if( ( (d._children === undefined)||(d._children === null) ) )
    {return -15}
     else 
    {return d._children.length>>0 ? -15 : 15; };})
         
//         return d.children || d._children ? -15 : 15; })

      .attr("dy", ".35em")
//      .attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
      .attr("text-anchor", function(d) { if( ( (d._children === undefined)||(d._children === null) ) )
    {return "end"}
     else 
    {return d._children.length>>0 ? "end" : "start"; };})
      .text(function(d) { return d.data.name; })
      .style("fill-opacity", 1e-6);
  // Transition nodes to their new position.
  var nodeUpdate = node.transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

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
.style("fill", function(d) { return  ( ( (d._children === undefined)||(d._children === null) ) )  ? "#fff" :  d._children.length>>0 ? "lightsteelblue" : "#fff"; });
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
  var link = vis.selectAll("path.link")
      .data(tree.links(nodes), function(d) { return d.target.id; });

  // Enter any new links at the parent's previous position.
  link.enter().insert("svg:path", "g")
      .attr("class", "link")
      .attr("d", function(d) {
        var o = {x: source.x0, y: source.y0};
        return diagonal({source: o, target: o});
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


function visChange(){
    $("#infolist").removeClass('unhidden');
    $("#infolist").addClass('hidden');
}





