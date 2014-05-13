var oldSearchTerm;

function searchInVis(searchterm) {

	if (searchterm.length >= 1) {
		undoHighlighting();

		oldSearchTerm = searchterm;

		d3.selectAll("text").filter(function(d, i) {
			return (this.textContent.indexOf(searchterm) >= 0);
		}).style("fill", "red");
	}
	else {
		undoHighlighting();
		oldSearchTerm = "";
	}

}
function undoHighlighting() {


	d3.selectAll("text").filter(function(d, i) {
		return (this.textContent.indexOf(oldSearchTerm) >= 0);
	}).style("fill", "black");


}
