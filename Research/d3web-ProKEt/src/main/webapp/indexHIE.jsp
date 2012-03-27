
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-trans
itional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
</head>
<body>
<script type="text/javascript">
//<!--

//--------------------------------BROWSER CHECKING STUFF

/**
 * BROWSER DETECTION SCRIPT
 * Origin from http://www.quirksmode.org/js/detect.html
 * Needed for correctly detecting various browsers, including several IE versions
 *
 * NOTE: needs to be adapted from time to time
 */
var BrowserDetect = {
	init: function () {
		this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
		this.version = this.searchVersion(navigator.userAgent)
			|| this.searchVersion(navigator.appVersion)
			|| "an unknown version";
		this.OS = this.searchString(this.dataOS) || "an unknown OS";
	},
	searchString: function (data) {
		for (var i=0;i<data.length;i++)	{
			var dataString = data[i].string;
			var dataProp = data[i].prop;
			this.versionSearchString = data[i].versionSearch || data[i].identity;
			if (dataString) {
				if (dataString.indexOf(data[i].subString) != -1)
					return data[i].identity;
			}
			else if (dataProp)
				return data[i].identity;
		}
	},
	searchVersion: function (dataString) {
		var index = dataString.indexOf(this.versionSearchString);
		if (index == -1) return;
		return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
	},
	dataBrowser: [
		{
			string: navigator.userAgent,
			subString: "Chrome",
			identity: "Chrome"
		},
		{ 	string: navigator.userAgent,
			subString: "OmniWeb",
			versionSearch: "OmniWeb/",
			identity: "OmniWeb"
		},
		{
			string: navigator.vendor,
			subString: "Apple",
			identity: "Safari",
			versionSearch: "Version"
		},
		{
			prop: window.opera,
			identity: "Opera",
			versionSearch: "Version"
		},
		{
			string: navigator.vendor,
			subString: "iCab",
			identity: "iCab"
		},
		{
			string: navigator.vendor,
			subString: "KDE",
			identity: "Konqueror"
		},
		{
			string: navigator.userAgent,
			subString: "Firefox",
			identity: "Firefox"
		},
		{
			string: navigator.vendor,
			subString: "Camino",
			identity: "Camino"
		},
		{		// for newer Netscapes (6+)
			string: navigator.userAgent,
			subString: "Netscape",
			identity: "Netscape"
		},
		{
			string: navigator.userAgent,
			subString: "MSIE",
			identity: "Explorer",
			versionSearch: "MSIE"
		},
		{
			string: navigator.userAgent,
			subString: "Gecko",
			identity: "Mozilla",
			versionSearch: "rv"
		},
		{ 		// for older Netscapes (4-)
			string: navigator.userAgent,
			subString: "Mozilla",
			identity: "Netscape",
			versionSearch: "Mozilla"
		}
	],
	dataOS : [
		{
			string: navigator.platform,
			subString: "Win",
			identity: "Windows"
		},
		{
			string: navigator.platform,
			subString: "Mac",
			identity: "Mac"
		},
		{
			   string: navigator.userAgent,
			   subString: "iPhone",
			   identity: "iPhone/iPod"
	    },
		{
			string: navigator.platform,
			subString: "Linux",
			identity: "Linux"
		}
	]

};
BrowserDetect.init();	

/* when site has loaded, immediately perform browser handling */
window.onload = handleUnsupportedBrowsers();

/*
 * Browser detection and handling: if browser is IE and NOT version 9, display
 * a static message-page to the user so he can't go on
 */
function handleUnsupportedBrowsers() {
    
    var bro = BrowserDetect.browser;
    var ver = BrowserDetect.version;
    
    	if(bro == "Explorer" && ver != "9"){

		var message = "<div id=\"BROWSERINFO\" style='width: 700px; margin-left:auto; margin-right:auto; margin-top:50px; font-size:1.5em; color: red'>";
		message = message + "You are currently using <b>" + bro + " " + ver ;
		message = message + ".</b><br /><br />This website does NOT fully support this browser/version! <br /><br />";
		message = message + "Please use instead one of the following suggestions:";
        message = message + "<ul>";
        message = message + "<li>";
        message = message + "<a href='http://www.mozilla-europe.org/de/'>Mozilla Firefox</a>";
        message = message + "</li>";
        message = message + "<li>";
        message = message + "<a href='http://www.google.com/chrome/'>Google Chrome</a>";
        message = message + "</li>";
        message = message + "<li>";
        message = message + "<a href='http://windows.microsoft.com/de-DE/internet-explorer/products/ie-9/features'>Internet Explorer <b>9.0</b></a>";
        message = message + "</li>";
        message = message + "</ul>";
        message = message + "</div>";
		document.body.innerHTML = message;
	} else {
            var link = window.location + "Dialog?src=Study1HIE.xml";
            window.location = link;    
        }
}
//-->
</script>

</body>
</html>
