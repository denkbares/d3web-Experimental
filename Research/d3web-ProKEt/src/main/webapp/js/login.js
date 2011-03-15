/**
 * M. Freiberg, 2011 JS login dialog specification and required functionalities.
 */

/* creating and configuring the jquery Login dialog */
$(function() {

	var opts = {
		autoOpen : true,
		position : top,
		modal : true,
		width : 300,
		height : 175,
		minWidth : 300,
		minHeight : 175,
		buttons : {
			"OK." : sendData,
			"Abbrechen." : showDenyMessage
		}
	};

	$("#jqLoginDialog").dialog(opts);

});

/**
 * Definition of an Ajax call that sends user login data to LoginServlet, and,
 * according to results of login check there: closes the Login Dialog (if
 * successful) and otherwise leaves Login Dialog open (if not successful logged
 * in).
 * Thus, the underlying system can only be used, in case login was successful,
 * as Login Dialog is modal.
 */
function sendData() {

	var usr = $('#usrInput').val();
	var pw = $('#pwInput').val();
	// var seed = generateSeed();
	// var p = encrypt(encrypt(pw) + seed);

	var querylink = $.query.set("u", usr).set("p", pw).set("tryagain",
			window.location.href).toString();
	var servletPart = window.location.href.substring(window.location.href
			.indexOf("d3web-ProKEt"), window.location.href.length);

	var servletPartNew = "d3web-ProKEt/LoginServlet" + querylink;

	link = window.location.href.replace(servletPart, servletPartNew);

	$.ajax({
		type : "GET",
		async : false,
		url : link,
		success : function(writernote) {
			alert(writernote);
			if (writernote == "success") {
				$("#jqLoginDialog").dialog("close");
			} else {
				alert("Please try again");
			}
		}
	});
}

/**
 * Encrypt the given text according to encryption.js
 * 
 * @param text
 *            The text to be encrypted
 * @returns The encrypted text.
 */
function encrypt(text) {
	return encode(text);
}

/*
 * TODO SOMETIME Momentan wird nur das Passwort in md5 umgerechnet und dann
 * verglichen. Besser wäre eine Lösung, mit einem Zufallsseed die weiter
 * verschlüsselt. Dann brauchen wir aber javaseitig wieder eine md5 codierung...
 */

/**
 * Generate a 5-character random seed for additional encrypting the user data.
 * 
 * @returns {String} The random seed.
 */
function generateSeed() {

	var s = "";

	for ( var i = 0; i < 5; i++) {
		seeds = new Array(3);
		seeds[0] = "%" + rand(48, 57);
		seeds[1] = "%" + rand(65, 90);
		seeds[2] = "%" + rand(97, 122);
		s += seeds[rand(0, 2)];
	}

	return s;
}

/**
 * Create random number between a minimum and maximum value.
 * 
 * @param min
 * @param max
 * @returns The random number.
 */
function rand(min, max) {
	if (min > max) {
		return (-1);
	}
	if (min == max) {
		return (min);
	}
	return (min + parseInt(Math.random() * (max - min + 1)));
}

function showDenyMessage() {
	alert("Sie müssen sich einloggen, um das System zu nutzen!");
}