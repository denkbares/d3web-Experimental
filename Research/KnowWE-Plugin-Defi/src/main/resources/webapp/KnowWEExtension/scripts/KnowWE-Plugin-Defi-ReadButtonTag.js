function getReadButtonValue(user,page) {
	var form = document.readbuttonform;
	var checked;
	for (i = 0; i < 4; i++) {
		
		if (form.elements[i].checked) {
			checked = form.elements[i].value;
		}
	}
	
	alert(checked + " - " + user + " - " + page);
}