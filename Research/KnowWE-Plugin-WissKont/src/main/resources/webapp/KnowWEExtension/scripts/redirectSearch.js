function redirectSearch(termnames, url, contextPath) {
	var searchedTerm = jq$('#query').val();
	var availableTags = termnames;
	
	if (contains(availableTags, searchedTerm)) {
		var page = getPagename(searchedTerm);
		jq$('#searchForm').attr("action", url + page);
		return false;
	} else {
		jq$('#searchForm').attr("action", contextPath + '/Search.jsp');
		return true;
	}
}

function contains(a, obj) {
    var i = a.length;
    while (i--) {
       if (a[i] === obj) {
           return true;
       }
    }
    return false;
}



function getPagename(termname) {
	var page = "";
	var params = {
			action : 'GetTermPageAction',
			term   : termname
    }; 
	var options = {
		url : KNOWWE.core.util.getURL(params),
		async : false,
		 response : {
			 fn : function(){
				 page = this.response;
			 }
		 },
	};
	
	 new _KA(options).send();
	 return page;
}