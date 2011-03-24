/**
 * M. Freiberg, 2011 JS login dialog specification and required functionalities.
 */

/* creating and configuring the jquery Login dialog */

$(function() {

	var opts = {
		position : top,
		width : 470,
		height : 350,
		minWidth : 470,
		minHeight : 350,
		draggable : false,
		resizable : false,
		// to force first login
		autoOpen : false,
		modal : true,
		// do NOT close dialog when hitting escape
		closeOnEscape : false,
		// for NOT showing the default close button/cross of the dialog
		open : function(event, ui) {
			$(".ui-dialog-titlebar-close").hide();
		},
		// two custom buttons
		buttons : [{
			id: "loginOk",
			text: "OK",
			click: function(){
					sendData();
				}
			},
			{
			id: "loginCancel",
			text: "Abbrechen",
			click: function(){
					showDenyMessage();
				}
			}]
	};
	$("#jqLoginDialog").dialog(opts);
	
	// register some jquery dialog buttons here to "react" per default on
	// enter clicks in the dialogs: login-OK, saveCase-Speichern, loadCase.
	$().ready(function() {

		$(document).keypress(function(e) {
			if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
				if ($('#jqLoginDialog').dialog('isOpen'))
					$('[aria-labelledby$=jqLoginDialog]').find(":button:contains('OK')").click();
				if ($('#jqConfirmDialog').dialog('isOpen'))
					$('[aria-labelledby$=jqConfirmDialog]').find(":button:contains('Speichern')").click();
				if ($('#jqLoadCaseDialog').dialog('isOpen'))
					$('[aria-labelledby$=jqLoadCaseDialog]').find(":button:contains('OK')").click();
				return false;
            }
        });
	});

});

/*buttons: [{
    id:"btn-accept",
    text: "Accept",
    click: function() {
            $(this).dialog("close");
    }
},{
    id:"btn-cancel",
    text: "Cancel",
    click: function() {
            $(this).dialog("close");
    }
}]*/



/**
 * Definition of an Ajax call that sends user login data to LoginServlet, and,
 * according to results of login check there: closes the Login Dialog (if
 * successful) and otherwise leaves Login Dialog open (if not successful logged
 * in). Thus, the underlying system can only be used, in case login was
 * successful, as Login Dialog is modal.
 */
function sendData() {

	var usr = $('#usrInput').val();
	var pw = $('#pwInput').val();
	// var seed = generateSeed();
	// var p = encrypt(encrypt(pw) + seed);

	// also login is called from dialogservlet, as login is only a modal overlay
	var dialoglink = 
		$.query.set("action", "show").toString();
	
	// link = window.location.href.replace(window.location.search, "") + link;
	var querylink = $.query.set("u", usr).set("p", encrypt(pw)).toString();
	var servletPart = window.location.href.substring(window.location.href
			.indexOf("d3web-ProKEt"), window.location.href.length);

	var servletPartNew = "d3web-ProKEt/LoginServlet" + querylink;

	// link = window.location.href.replace(servletPart, servletPartNew);
	var link = $.query.set("action", "login").set("u", usr).set("p", encrypt(pw)).toString();
	link = window.location.href.replace(window.location.search, "") + link;

	
	var jxhr = $.get(link, function(html) {
		
		// not successfully logged in
		if (html == "nosuccess"){
			$('#loginError').html("<font color=\"red\">Login erfolglos. Bitte versuchen Sie es noch einmal.</font>");
			
		} else {
			// successful login: remove error message
			$('#loginError').html("");
			
			// just close login dialog, but NOT reset session
			$("#jqLoginDialog").dialog("close");	
			
			// in case another user than before logged in successfully
			if(html=="newUser"){
				
				// TODO for safety reasons, autosave old case for old user
				
				// ... and finally reset the dialog
				d3web_sessionForNewUser();
				
			} else {
				
				// otherwise just show previous dialog mask/link
				d3web_show();
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


function checkedIn(){
	
	// check wether user already logged in
	if(document.cookie){
		
		c = document.cookie;
		cookiename = c.substring(0, c.indexOf('='));
		if(c.indexOf(';') != -1){
			cookiewert = c.substring(c.indexOf('=')+1,c.indexOf(';'));
		}
		else{
			cookiewert = c.substr(c.indexOf('=')+1,c.length);
		}
		if(cookiename == "loggedin"){
			return new Boolean(true);
		}
	}
	return new Boolean(false);
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
	$('#loginError').html("<font color=\"red\">Sie müssen sich einloggen um das System nutzen zu können.</font>");
}

function getUrlVars()
{
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}