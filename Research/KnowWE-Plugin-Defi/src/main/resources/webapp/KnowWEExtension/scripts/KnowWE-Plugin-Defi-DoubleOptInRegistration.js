function registerUserDoubleOptIn() {
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
		return false;
	}
	if (password.value == "") {
		password.style.borderColor = "red";
		defiAlert("Bitte ein Passwort eingeben.");
		return false;
	}
	if (password.value != password2.value) {
		password.style.borderColor = "red";
		password2.style.borderColor = "red";
		defiAlert("Die Passwörter müssen übereinstimmen.");
		return false;
	}
	if (password.value.length < 8 ||
		!(/.*[A-Z].*/.test(password.value) // at least one upper case letter
		&& /.*[a-z].*/.test(password.value) // at least one lower case letter
		&& /.*\d.*/.test(password.value))) { // at least one digit
		defiAlert("Das Passwort muss Groß- und Kleinbuchstaben sowie Zahlen enthalten und mindestens 8 Zeichen umfassen.");
		return false;
	}
	if (fullname.value == "") {
		defiAlert("Bitte den vollen Namen eingeben.");
		return false;
	}
	var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	if (!re.test(email.value)) {
		defiAlert("Bitte gültige E-Mail Adresse eingeben.");
		return false;
	}

	var params = {
		action : 'DoubleOptInRegisterUserAction',
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
				location = "Login.jsp?tab=success";
			},
			onError : function() {
				defiAlert(this.responseText);
			}
		}
	}

	new _KA(options).send();
}