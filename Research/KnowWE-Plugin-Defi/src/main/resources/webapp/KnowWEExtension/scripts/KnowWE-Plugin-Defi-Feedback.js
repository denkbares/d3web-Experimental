function checkAndSubmit() {
	var warning = "Sie haben nicht alle Felder ausgefüllt.\nTrotzdem fortfahren?"
	var pass = true;
	
	var table, boxes, empty, topic;
	var entries = "";
	for (var i = 1; i <= 26; i++) {
		// check table
		empty = true;
		table = document.getElementById('q'+i);
		topic = table.getElementsByTagName('td')[0].innerHTML;
		entries += topic;
		
		boxes = table.getElementsByTagName('input');   
		if (boxes.length > 0) {
			for (var j = 0; j < boxes.length; j++) {
				if (boxes[j].checked)  {
					entries += "###" + boxes[j].id + "---" + boxes[j].value;
					empty = false;
				}
			}
		// check textarea
		} 
		
		boxes = table.getElementsByTagName('textarea')[0];
		if(boxes != null) {
			if (boxes.value != "") {
				entries += "###" + boxes.id + "---" + boxes.value;
				empty = false;
			}
		}
		entries += ":::";
		
		// mark table if not answered
		if(empty) {table.style.border = "1px solid red";pass = false;}
		else table.style.border = "0";
	}
	entries = entries.substring(0, entries.length-3);
	
	if (!pass) {
		if (!confirm("Es wurden noch nicht alle Fragen beantwortet.\nTrotzdem speichern?"))
			return;
	}
	
	var params = {
			action : 'FeedbackSaveAction',
			entries : entries
		}

		var options = {
			url : KNOWWE.core.util.getURL(params),
			response : {
				action : '',
				ids : [ '' ],
				fn : function() {
					document.location = "Wiki.jsp?page=BefragungAbgeschlossen";
				}
			}
	}
	
	
	new _KA(options).send();
}

function fillForm() {

	var params = {
		action : 'FeedbackFillAction'
	}
	var url = KNOWWE.core.util.getURL(params);
	var options = {
		url : url,
		response : {
			action : 'none',
			fn : function() {
				checkAnswers(this.responseText);
			}
			
		}
	}
	new _KA(options).send();
}

function checkAnswers(answers) {
	var box;
	var answer = answers.split(":::");
	for (var i = 0; i < answer.length; i++) {
		box = document.getElementById(answer[i].split("###")[0]);
		
		if (box.tagName == 'INPUT') {
			box.checked = true;
			if (box.id.indexOf("q5") == 0 || box.id.indexOf("q6") == 0)
				box.parentNode.style.backgroundColor = 'lightgreen';
			else
				box.parentNode.parentNode.style.backgroundColor = 'lightgreen';
		} else if (box.tagName == 'TEXTAREA')  {
			box.value = answer[i].split("###")[1];
		}
	}
}

function prepare25() {
	var rb1 = document.getElementById('q25_1');
	var rb2 = document.getElementById('q25_2');
	var rb3 = document.getElementById('q25_7');
	var cb3 = document.getElementById('q25_5');
	
	rb1.onclick = setDisableFor25;
	rb2.onclick = setDisableFor25;
	rb3.onclick = setDisableFor25;
	cb3.onclick = setDisableFor25;

	setDisableFor25();
}

function setDisableFor25() {
	var rb2 = document.getElementById('q25_2');
	var cb1 = document.getElementById('q25_3');
	var cb2 = document.getElementById('q25_4');
	var cb3 = document.getElementById('q25_5');
	var ta = document.getElementById('q25_6');
	
	cb1.disabled = !rb2.checked;
	if (!rb2.checked)
		cb1.checked = false;
	cb2.disabled = !rb2.checked;
	if (!rb2.checked)
		cb2.checked = false;
	cb3.disabled = !rb2.checked;
	if (!rb2.checked)
		cb3.checked = false;
	ta.disabled = !cb3.checked;
	
}