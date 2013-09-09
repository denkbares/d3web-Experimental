function redirectSearch(termnames, url, contextPath) {
	var searchedTerm = jq$('#query').val();
	var availableTags = termnames;
	
	if (contains(availableTags, searchedTerm)) {
		jq$('#searchForm').attr("action", url + searchedTerm);
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