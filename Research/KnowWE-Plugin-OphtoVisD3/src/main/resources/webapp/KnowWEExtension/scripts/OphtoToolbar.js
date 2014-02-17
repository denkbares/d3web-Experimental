jq$(document).ready(function(jq$) {
jq$(".menue").click(function(){
jq$(".menue").removeClass("active");
jq$(this).addClass("active");
});
jq$(".editor").click(function(){
jq$(".chart").toggleClass("hidden");
jq$(this).toggleClass("active");
var jq$e = jq$(this);
jq$e
.text(jq$e.text() === "Editor▼" ? "Editor▲" : "Editor▼");

});  
jq$(".dropzone").droppable({
	drop: function( event, ui ) {
	var text= ui.draggable.text();
	jq$( this )
	.addClass( "ui-state-highlight" )
	.html(ui.draggable.text());
	}
	});
});
