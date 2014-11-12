function registerUser() {
	var umcs = $$("#usermanager div");
	var umc = umcs[umcs.length - 1];

	var loginname = $ES('input[name=loginname]', umc)[0];
	var password = $ES('input[name=password]', umc)[0];
	var password2 = $ES('input[name=password2]', umc)[0];
	var fullname = $ES('input[name=fullname]', umc)[0];
	var email = $ES('input[name=email]', umc)[0];

	
	loginname.style.borderColor = "";
	password.style.borderColor = "";
	password2.style.borderColor = "";
	fullname.style.borderColor = "";
	
	if (loginname.value == "") {
		loginname.style.borderColor = "red";
		defiAlert("Bitte einen Benutzernamen eingeben.");
		return;
	}
	if (password.value == "") {
		password.style.borderColor = "red";
		defiAlert("Bitte ein Passwort eingeben.");
		return;
	}
	if (password.value != password2.value) {
		password.style.borderColor = "red";
		password2.style.borderColor = "red";
		defiAlert("Die Passwörter müssen übereinstimmen.");
		return;
	}
	if (fullname.value == "") {
		defiAlert("Bitte den vollen Namen eingeben.");
		return;
	}
	
	var params = {
			action : 'RegisterUserAction',
			loginname : loginname.value,
			password : password.value,
			fullname : fullname.value,
			email : email.value
	}
	
	var options = {
			url : KNOWWE.core.util.getURL(params),
			response : {
				action : '',
				ids : [ '' ],
				fn : function() {
					location.reload();
				}
			}
	}
	
	new _KA(options).send();
}