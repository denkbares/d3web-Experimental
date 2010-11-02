/**
 * Startup code, executed on page load, ONLY on the very first,
 * initial page load!
 */
$(function() {

	// on page load, init the JS binding to the dialog elements
	d3web_init();
	
	// make navigation work on d3web level
	//$(".navigation-item").each(function() {
		//var id = $(this).attr('id');
		//$(this).children("a").bind("click", function() {
			//d3web_selectQuestionnaire(id.replace(/^nav-/, ""));
		//});
	//});
});


/**
 * Set a click function to ever OK button and every form element in a question
 * without OK button. That function is taking care for setting that fact in the
 * system by calling d3web_addfact().
 */
function d3web_init(){

	//alle question-c holen, first .focus()
	//var child = $(".question-c:first > div  > div > input");
	//child.focus();
	
	// bind "get facts" method to radio buttons, checkbuttons
	$('[type=radio]').unbind('click').click(function() {
		get_selected_facts($(this));
	});
	
	$('[type=checkbox]').unbind('click').click(function() {
		get_selected_facts($(this));
	});
	
	$('[type=text]').unbind('click').click(function() {
		var thisEl = $(this);
		thisEl.bind('keydown', function(e) {
			var code = (e.keyCode ? e.keyCode : e.which);
			if(code == 13) {
				get_selected_facts(thisEl);
			}
		});
	});
	
	$('[type=textarea]').unbind('click').click(function() {
		var thisEl = $(this);
		thisEl.bind('keydown', function(e) {
			var code = (e.keyCode ? e.keyCode : e.which);
			if(code == 13) {
				get_selected_facts(thisEl);
			}
		});
		
		/*$(this).typing({
			start : function() {
			},
			stop : function() {
				get_selected_facts(thisElement);				
			},
			delay : 1000
		});*/	
	});
	
	$('#reset').unbind('click').click(function(){
		d3web_resetSession();
	});
	
}


/**
 * Use an ajax call for requesting the nextForm in the dialog flow from d3web.
 * The old form is being replaced by the new content without reloading the whole
 * page. Important: resetup the click functions on the OK buttons and form
 * elements for sending the facts.
 */
function d3web_nextform() {
	
	// changes the querystring part of action into nextform
	// eg: formerly D3webDialog?src=HeartMed2010&action=show
	// --> &action=nextform
	var link = $.query.set("action", "show").toString();
	
	// assemble the new link; replace the parts of the query string after ? with
	// nothing ("") and add the action nextform string part. result e.g.:
	// http://localhost:8080/d3web-ProKEt/D3webDialog?src=HeartMed2010&action=nextform
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		async : false,
		url : link,
		success : function(html) {			// compare resulting html to former
			if (html != "same") {			// replace target id of content if not the same
				window.location.reload();
				d3web_init();
			}
		}
	});
	d3web_show_solutions("right"); // show solutions
}

/**
 * Transfer a selected or removed fact to the d3web dialog via an ajax call.
 */
function d3web_addfact(qid, pos) {
	
	var link = $.query.set("action", "addFact").set("qid", qid).set("pos", pos)
			.toString();
	link = window.location.href.replace(window.location.search, "") + link;
	
	$.ajax({
		type : "GET",
		async : false,
		url : link,
		success : function(html) {
			d3web_nextform();
		}
	});
}

/**
 * Select a questionnaire in d3web.
 */
function d3web_selectQuestionnaire(qid) {
	var link = $.query.set("action", "selectQuestionnaire").set("qid", qid)
			.toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		async : false,
		url : link,
		success : function(html) {
//			d3web_nextform();
		}
	});
}

/**
 * Transfer children from d3web.
 */
function d3web_getChildren(pid) {
	var link = $.query.set("action", "getChildren").set("pid", pid)
			.toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		async : false,
		url : link,
		success : function(html) {
			$("#sub-" + pid).html(html);
		}
	});
}

/**
 * Get new ratings.
 */
function d3web_getRatings(list_of_ids) {
	var link = $.query.set("action", "getRatings").set("ids", list_of_ids)
			.toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		async : false,
		url : link,
		success : function(html) {
			var elements = html.split(";");
			for(i = 0; i < elements.length; i++){
				var elementId = elements[i].split(",")[0];
				var clazz = elements[i].split(",")[1];
				element = $("[id='" + elementId + "']");
				element.removeClass("rating-high rating-medium rating-low");
				if (clazz !== "remove") {
					element.addClass(clazz);
				}
			}
		}
	});
}



/**
 * Calculating the set facts for the parent question of a clicked element. pos
 * is a comma-separated list of the selected answer indexes.
 */
function get_selected_facts(clickedItem) {
	
	// get parent question "^=" means begins with
 	question = clickedItem.closest('[id^="q_"]');
	
 	// for getting answers
	var counter = 0, pos = "";

	/* radio buttons */
	items = question.find('[type=radio]');
	items.each(function() {
		if ($(this).attr('checked')) {
			pos = pos + "," + counter;
		}
		counter++;
	});

	/* checkboxes */
	if (pos == "") {
		items = question.find(":checkbox");
		items.each(function() {
			if ($(this).attr('checked')) {
				pos = pos + "," + counter;
			}
			counter++;
		});
	}

	/* text */
	if (pos == "") {
		items = question.find(":text,textarea").filter(":first");
		if (items.size() == 1) {
			pos = "," + items.attr('value'); 
		}
	}

	// nothing set anymore, take the fact back on d3web side
	if (pos == "") {
		pos = ",[empty]"; // , will be cut in the next line (substring)
	}

	// transfer via ajax
	d3web_addfact(question.attr('id'), pos.substring(1, pos.length));
	
}

/**
 * Gets the solutions currently indicated by the underlying d3web dialog via an
 * ajax call to the D3WebDialog servlet
 */
function d3web_show_solutions(target_id) {
	var link = $.query.set("action", "solutions").toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		// async : false,
		url : link,
		success : function(html) {
			$('#' + target_id).html(html).fadeIn(3000);
		}
	});
}

	function d3web_resetSession() {
		var link = $.query.set("action", "show").set("reset", "true").toString();
		link = window.location.href.replace(window.location.search, "") + link;

		$.ajax({
			type : "GET",
			url : link,
			success : function(html) {
				window.location.reload(true);
				d3web_init();
			}
		});
	
}

