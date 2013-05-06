$(document).ready(function($) {
$(".menue").click(function(){
console.log(this);
$(".menue").removeClass("active");
$(this).addClass("active");
});
$(".editor").click(function(){
console.log(this);
$(".editorDiv").toggleClass("hidden");
$(this).toggleClass("active");
var $e = $(this);
$e
.text($e.text() === "Editor▼" ? "Editor▲" : "Editor▼");

});  
$(".dropzone").droppable({
	drop: function( event, ui ) {console.log("dropped");
	var text= ui.draggable.text();
	console.log(text);
	$( this )
	.addClass( "ui-state-highlight" )
	.html(ui.draggable.text());
	}
	});
console.log("dropbar");
});
var test = $(".menue");
console.log("test" +test);