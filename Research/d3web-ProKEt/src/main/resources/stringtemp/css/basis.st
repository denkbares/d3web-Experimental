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

#headertext{margin-left: auto; margin-right: auto; width: 500px;
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

.errorRed {color: red;}


/* STANDARD OWN STYLED BUTTONS */ 
.caseButton{ width: 120px; height: 20px; padding-top: 3px; border: 1px solid #696969; text-align: center; cursor: pointer; 
	background-color: #dddddd; font-weight: bold; color: #224172; -moz-box-shadow: 0 0 5px black;	
	-webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; }

.baseButtonOneLetter{width: 24px; height: 20px; padding-top: 3px; border: 1px solid #696969; text-align: center; cursor: pointer; 
	background-color: #dddddd; font-weight: bold; color: darkred; -moz-box-shadow: 0 0 5px black;	
	-webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; margin-top: 5px;}

.baseButtonEndStudySession{width: 100px; height: 20px; padding-top: 3px; border: 1px solid #696969; text-align: center; cursor: pointer; 
	background-color: #dddddd; font-weight: bold; color: darkred; -moz-box-shadow: 0 0 5px black;	
	-webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; margin-top: 5px; }

.rightButton { float: right; margin-right: 10px; }

.leftButton { float: left; margin-right: 5px;}



.textAlignRight {text-align: right; padding-right: 10px;}

.longTextfield {width: 258px; color: #4C4C4C;}

.longTextfieldInputColor{color: black;}

.longTextarea {width:258px; resize: none; color: #4C4C4C;}

.longTextareaInputColor {color: black;}

.margBott {margin-bottom: 10px;}

.hidden {display:none;}

.visible {display:inline;}

.smallTd{width: 60px;}

.topSpace{padding-top: 15px;}
 
.topText{vertical-align: top;}

.leftPadd{padding-left: 15px;}

.tableNoTopMargin{margin-top: 0px;}

.fontOnePointTwo{font-size: 1.2em;}