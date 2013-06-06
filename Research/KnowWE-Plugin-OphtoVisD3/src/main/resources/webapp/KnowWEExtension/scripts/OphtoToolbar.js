$(document).ready(function($) {
$(".menue").click(function(){
$(".menue").removeClass("active");
$(this).addClass("active");
});
$(".editor").click(function(){
$(".chart").toggleClass("hidden");
$(this).toggleClass("active");
var $e = $(this);
$e
.text($e.text() === "Editor▼" ? "Editor▲" : "Editor▼");

});  
$(".dropzone").droppable({
	drop: function( event, ui ) {
	var text= ui.draggable.text();
	$( this )
	.addClass( "ui-state-highlight" )
	.html(ui.draggable.text());
	}
	});
});
