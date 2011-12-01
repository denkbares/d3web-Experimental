/**
* General basic CSS statements that should apply for most dialogs.
*
* @author	Johannes Mitlmeier
*/

/* general settings */
body,table,div {
	padding: 0px;
	margin: 0px;
	font-family: Arial;
	font-size: 8pt;
	text-color: black;
}


/* basic layout (header, footer, main content) */
#head, #foot {
	clear: both;
	width: 99%;
	min-height: 25px;
	/* gradient background color */
	background-color: #ccc; /* fallback (Opera, IE) */
    background-image: -moz-linear-gradient(left, #ccc, #fff); /* Firefox */
    background-image: -webkit-gradient(linear,
                left top, right top, from(#ccc), to(#fff)); /* Chrome, Safari */
}

#head {
	margin: 0px;
	text-align: center;
	margin-bottom: 10px;
}

#foot {
	padding: 5px;
	margin-top: 10px;
	padding-top: 10px;
}

#left {
	clear: both;
	width: 15%;
}

#right {
	width: 15%;
}

#content {
	width: 67%;
 	margin: 0px 1% 0px 1%;
}

#left, #content, #right {
	float: left;
	margin-bottom: 10px;
}


/* element content's styles */
.dialog-content { margin-bottom: 5px; }
.question-content {	padding: 4px; }


/* title elements */
#dialog-title {
	font-size: 2em;
	font-variant: small-caps;
	text-align: center;
}


/* tables used for the multicolumn layout */
.multicol {
	height: 1;
	width: 100%;
}


/* questionnaire navigation elements (next button) */
#x_questionnairenav {
	clear: both;
	width: 99%;
  	text-align: center;
  	padding: 3px;
}

#x_questionnairenav_next {
	background-image: url(images/next.gif);
	background-repeat: no-repeat;
	height: 25px;
	width: 70px;
}


/* tooltip image */
.tooltip-trigger {
	width: 1em;
	height: 1em;
	cursor: help;
	background-image: url(img/hint.png);
	background-repeat: no-repeat;
}


/* ratings */
.rating-box { padding: 1px 2px; }
.rating-high { border: 2px dashed green; }
.rating-medium { border: 2px dashed yellow; }
.rating-low { border: 2px dashed red; }