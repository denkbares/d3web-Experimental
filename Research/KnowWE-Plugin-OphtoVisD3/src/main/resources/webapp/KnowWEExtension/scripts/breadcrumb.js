function breadcrumb(conceptA) {
	
	
	
	
	var url = KNOWWE.core.util.getURL({action : 'BreadcrumbAction', concept:  conceptA , type: 'unterkonzept'});
	
	
/*	jq$.getJSON( url, renderBreadCrumbs(json));*/
	url ="/KnowWE/"+url;
	console.log(url);
	
	jq$.getJSON( url, function( json ) {
		
		console.log(json);
		
		
					var $bc = $('.crumbs');
					
					$bc.empty();
		
	/*	jq$.each(json, function(i, item){alert(item)});*/
			jq$.each(json, function(i, item){
				
				
					
				 
				/*	var $a = jq$(item).children('a').clone();*/
				
					var $bc = $('.crumbs');
					
				/*	$( ".crumbs" ).prepend('<a href="#home">'+item+'</a>');*/
				/*	$bc.prepend(' > ', '<a href="javascript:show('+item+');">'+item+'</a>');*/
					
					
					$bc.prepend(' > ', '<a href="http://localhost:8080/KnowWE/Wiki.jsp?page='+item+'">'+item+'</a>');
							
			
			});
		
		
		
		/*		jq$('.items a').on('click', function() {
											  var jq$this = jq$(this),
												  jq$bc = jq$('<div class="items"></div>');

											  jq$this.parents('li').each(function(n, li) {
												  var $a = jq$(li).children('a').clone();
												  jq$bc.prepend(' > ', jq$a);
											  });
												jq$('.breadcrumb').html( $bc.prepend('<a href="#home">Visualisierung</a>') );
												return false;
										}) */
});
	
	
}

function show(item) {
	
	alert(item);
}
/*
function renderBreadCrumbs(data){
	
				jq$('.items a').on('click', function() {
											  var jq$this = jq$(this),
												  jq$bc = jq$('<div class="items"></div>');

											  jq$this.parents('li').each(function(n, li) {
												  var jq$a = jq$(li).children('a').clone();
												  jq$bc.prepend(' > ', jq$a);
											  });
												jq$('.breadcrumb').html( jq$bc.prepend('<a href="#home">Visualisierung</a>') );
												return false;
											}) 
}
*/