function checkAndSubmitForm(id) {
	var warning = "Sie haben nicht alle Felder ausgefüllt.\nTrotzdem fortfahren?"
	var form = document.getElementById(id);
	var pass = true;
	
	//check question 1
	var empty = true;
	var q = document.getElementsByName('FB1');
	var h = document.getElementById('frage1');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 2
	empty = true;
	q = document.getElementsByName('FB2');
	h = document.getElementById('frage2');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 3
	empty = true;
	q = document.getElementsByName('FB3');
	h = document.getElementById('frage3');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 4
	empty = true;
	q = document.getElementsByName('FB4');
	h = document.getElementById('frage4');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 5 - units
	empty = true;
	var qs;
	h = document.getElementById('frage5');
	for (var i = 1; i <= 6; i++) {
		for (var j = 1; j <= 6; j++) {
			qs = document.getElementsByName("FB5-"+i+"-" + j + "");
			if (qs.length > 0 && qs[0].checked)
				empty = false;
		}
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 6 - units
	empty = true;
	h = document.getElementById('frage6');
	for (var i = 1; i <= 6; i++) {
		for (var j = 1; j <= 6; j++) {
			qs = document.getElementsByName("FB6-"+i+"-" + j + "");
			if (qs.length > 0 && qs[0].checked)
				empty = false;
		}
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 7
	empty = true;
	q = document.getElementsByName('FB7');
	h = document.getElementById('frage7');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 8 - textarea
	empty = true;
	q = document.getElementsByName('FB8');
	h = document.getElementById('frage8');
	if(q[0].value != "") empty = false;
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 9
	empty = true;
	q = document.getElementsByName('FB9');
	h = document.getElementById('frage9');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 10
	empty = true;
	q = document.getElementsByName('FB10');
	h = document.getElementById('frage10');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 11
	empty = true;
	q = document.getElementsByName('FB11');
	h = document.getElementById('frage11');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 12
	empty = true;
	q = document.getElementsByName('FB12');
	h = document.getElementById('frage12');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 13
	empty = true;
	q = document.getElementsByName('FB13');
	h = document.getElementById('frage13');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 14
	empty = true;
	q = document.getElementsByName('FB14');
	h = document.getElementById('frage14');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 15
	empty = true;
	q = document.getElementsByName('FB15');
	h = document.getElementById('frage15');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 16
	empty = true;
	q = document.getElementsByName('FB16');
	h = document.getElementById('frage16');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 17 - textarea
	empty = true;
	q = document.getElementsByName('FB17');
	h = document.getElementById('frage17');
	if(q[0].value != "") empty = false;
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 18
	empty = true;
	q = document.getElementsByName('FB18');
	h = document.getElementById('frage18');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 19
	empty = true;
	q = document.getElementsByName('FB19');
	h = document.getElementById('frage19');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 20
	empty = true;
	q = document.getElementsByName('FB20');
	h = document.getElementById('frage20');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 21
	empty = true;
	q = document.getElementsByName('FB21');
	h = document.getElementById('frage21');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 22
	empty = true;
	q = document.getElementsByName('FB22');
	h = document.getElementById('frage22');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 23
	empty = true;
	q = document.getElementsByName('FB23');
	h = document.getElementById('frage23');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 24 - textarea
	empty = true;
	q = document.getElementsByName('FB24');
	h = document.getElementById('frage24');
	if(q[0].value != "") empty = false;
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 25
	empty = true;
	q = document.getElementsByName('FB25');
	h = document.getElementById('frage25');
	for (var i = 0; i < q.length; i++) {
		if (q[i].checked)
			empty = false;
	}
	
	for (var i = 1; i <= 3; i++) {
		qs = document.getElementsByName("FB25-"+ i + "");
		if (qs.length > 0 && qs[0].checked)
			empty = false;
	}
	
	q = document.getElementsByName('FB25-4');
	if(q[0].value != "") empty = false;
	
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	// question 26 - textarea
	empty = true;
	q = document.getElementsByName('FB26');
	h = document.getElementById('frage26');
	if(q[0].value != "") empty = false;
	if(empty) {h.style.border = "1px solid red";pass = false;}
	else h.style.border = "0";
	
	if (!pass) {
		if (confirm("Es wurden noch nicht alle Fragen beantwortet.\nTrotzdem speichern?"))
			form.submit();
		else
			return;
	} else 
		form.submit();
}