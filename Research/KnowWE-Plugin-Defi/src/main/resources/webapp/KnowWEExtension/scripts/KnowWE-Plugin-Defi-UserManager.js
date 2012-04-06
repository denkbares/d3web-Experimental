function registerUser() {
	var umcs = $$("#usermanager div");
	var umc = umcs[umcs.length - 1];

	var loginname = $ES('input[name=loginname]', umc)[0];
	var password = $ES('input[name=password]', umc)[0];
	var password2 = $ES('input[name=password2]', umc)[0];
	var fullname = $ES('input[name=fullname]', umc)[0];
	var email = $ES('input[name=email]', umc)[0];
	var admin = "false";
	var radiobttns = $ES('input[name=admin]', umc);
	for(var i = 0; i < radiobttns.length; i++) {
		if (radiobttns[i].checked)
			admin = radiobttns[i].value;
	}
	
	loginname.style.borderColor = "";
	password.style.borderColor = "";
	password2.style.borderColor = "";
	fullname.style.borderColor = "";
	
	if (loginname.value == "") {
		loginname.style.borderColor = "red";
		alert("Bitte einen Benutzernamen eingeben.");
		return;
	}
	if (password.value == "") {
		password.style.borderColor = "red";
		alert("Bitte ein Passwort eingeben.");
		return;
	}
	if (password.value != password2.value) {
		password.style.borderColor = "red";
		password2.style.borderColor = "red";
		alert("Die Passwörter müssen übereinstimmen.");
		return;
	}
	if (fullname.value == "") {
		alert("Bitte den vollen Namen eingeben.");
		return;
	}
	
	var params = {
			action : 'RegisterUserAction',
			loginname : loginname.value,
			password : password.value,
			fullname : fullname.value,
			email : email.value,
			admin : admin
	}
	
	var options = {
			url : KNOWWE.core.util.getURL(params),
			response : {
				action : '',
				ids : [ '' ],
				fn : function() {
					alert(this.responseText);
					document.location.reload();
				}
			}
	}
	
	new _KA(options).send();
}

function deleteUser(username) {
	if (!confirm(username + ' wirklich löschen?'))
		return;
	
	var params = {
			action : 'DeleteUserAction',
			username : username
	}

	var options = {
		url : KNOWWE.core.util.getURL(params),
		response : {
			action : '',
			ids : [ '' ],
			fn : function() {
				alert(this.responseText);
				document.location.reload();
			}
		}
	}
	
	new _KA(options).send();
}

function showEditUser(i) {
	var umcs = $$('#usermanager div');
	var umc = umcs[i]
	var inputs = $ES('input', umc);
	// clear input-fields
	for (var x = 0; x < inputs.length; x++) {
		if (inputs[x].type != "button" && inputs[x].type != "radio") {
			inputs[x].value = "";
			inputs[x].style.borderColor = "";
		}
	}
	// display form-div
	if (umc.style.display == 'none') {
		for (var x = 0; x < umcs.length; x++) {
			umcs[x].style.display = 'none';
		}
		umc.style.display = 'block';
	}
	else
		umc.style.display = 'none';
}

function editUser(i, username) {
	var umcs = $$("#usermanager div");
	var umc = umcs[i];

	var password = $ES('input[name=password]', umc)[0];
	var password2 = $ES('input[name=password2]', umc)[0];
	var admin = "false";
	var radiobttns = $ES('input[name=admin]', umc);
	for(var i = 0; i < radiobttns.length; i++) {
		if (radiobttns[i].checked)
			admin = radiobttns[i].value;
	}
	
	if (password.value != password2.value) {
		alert("Die Passwörter müssen übereinstimmen.");
		password.style.borderColor = "red";
		password2.style.borderColor = "red";
		return;
	}
	
	var params = {
			action : 'EditUserAction',
			username : username,
			password : password.value,
			admin : admin
	}
	
	var options = {
			url : KNOWWE.core.util.getURL(params),
			response : {
				action : '',
				ids : [ '' ],
				fn : function() {
					alert(this.responseText);
					document.location.reload();
				}
			}
	}
	
	new _KA(options).send();
}