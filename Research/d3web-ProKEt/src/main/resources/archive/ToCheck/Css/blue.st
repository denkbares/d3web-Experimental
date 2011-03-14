$basis()$


/* title elements */
.dialog > .title, .dialog > .title td {	font-size: 1.3em; }
.questionnaire > .title, .questionnaire > .title td { font-size: 1.15em; }
.question .title, #solutions .title, #navigation .title { font-size: 1.05em; }
.answer .title { min-height: 0px; }
.title {
	min-height: 22px; 
	vertical-align: middle;
	padding-left: 15px;
	padding-right: 15px;
	vertical-align: middle;
	background-color: rgb(255, 255, 255);
	background-color: rgba(255, 255, 255, 0.5);
	border-bottom: 1px solid white;
	border-top-left-radius: 12px;
	border-top-right-radius: 12px;
	-moz-border-radius-topright: 12px;
	-webkit-border-top-right-radius: 12px;
	-moz-border-radius-topleft: 12px;
	-webkit-border-top-left-radius: 12px;
}
.title,.title td { font-weight: bolder; }
.title-c { color: black; border-bottom: 1px solid #111540; }
.title-d { background-color: rgba(0, 0, 0, 0.2); border-bottom: 1px solid #111540; }
.answer .title {
	width: 200px;
	background-color: transparent;
	border: 0px;
	padding: 2px;
}


/* spacing in multicolumn tables */
.multicol td {
	border-spacing: 5px;
	padding-bottom: 7px;
	vertical-align: top;
}
.multicol td .question {
	height: 97%;
}
.multicol .question > .title td, .multicol .answer > .title td { padding-bottom: 0px; }


/* style questions */
.question {
	color: #111540;
	background-color: #a1cbf6;
	-moz-border-radius-bottomright: 15px;
	-webkit-border-bottom-right-radius: 15px;
	-moz-border-radius-bottomleft: 15px;
	-webkit-border-bottom-left-radius: 15px;
	-moz-border-radius-topright: 15px;
	-webkit-border-top-right-radius: 15px;
	-moz-border-radius-topleft: 15px;
	-webkit-border-top-left-radius: 15px;
	border-bottom-left-radius: 15px;
	border-bottom-right-radius: 15px;
	border-top-left-radius: 15px;
	border-top-right-radius: 15px;
	border: 1px solid black;
	padding: 2px;
	margin: 5px;
	-moz-box-shadow: 3px 3px 3px #888;
	-webkit-box-shadow: 3px 3px 3px #888;
	box-shadow: 3px 3px 3px #888;
}
.question-c {
	color: white;
	background-color: #8888bf;
	border: 1px solid #111540;
}
.question-d {
	color: #111540;
	background-color: #f7f7f7;
	border: 1px solid gray;
}


/* tooltips */
.tooltip {
	z-index: 100;
	background-color: rgb(0, 0, 0);
	background-color: rgba(0, 0, 0, 0.525);
	padding: 5px;
	color: white;
	-moz-border-radius-bottomright: 5px;
	-webkit-border-bottom-right-radius: 5px;
	-moz-box-shadow: 3px 3px 3px #888;
	-webkit-box-shadow: 3px 3px 3px #888;
	border: 1px solid black;
	font-weight: normal;
	-moz-border-radius-bottomright: 5px;
}


/* style side boxes commonly */
#solutions, #navigation {
	color: #111540;
	background-color: white;
	-moz-border-radius-bottomright: 15px;
	-webkit-border-bottom-right-radius: 15px;
	-moz-border-radius-bottomleft: 15px;
	-webkit-border-bottom-left-radius: 15px;
	-moz-border-radius-topright: 15px;
	-webkit-border-top-right-radius: 15px;
	-moz-border-radius-topleft: 15px;
	-webkit-border-top-left-radius: 15px;
	border-bottom-left-radius: 15px;
	border-bottom-right-radius: 15px;
	border-top-left-radius: 15px;
	border-top-right-radius: 15px;
	border: 1px solid black;
	-moz-box-shadow: 3px 3px 3px #888;
	-webkit-box-shadow: 3px 3px 3px #888;
	box-shadow: 3px 3px 3px #888;
	padding-bottom: 10px;
	margin-right: 6px;
	margin-left: 3px;
	min-height: 35px;
}


/* navigation specific styles */
#navigation-list {
	background: transparent;
	padding-bottom: 10px;
	overflow: hidden;
}


/* solution specific stlyes */
#solutions ul {
	padding: 3px;
	margin: 0px;
	text-indent: 0px;
}
#solutions li { list-style-type: none; }


/* button for sending data to the server */
.ok-button {
	border: 1px solid black;
	text-align: center;
	cursor: pointer;
	-moz-box-shadow: 1px 1px 1px #888;
	-webkit-box-shadow: 1px 1px 1px #888;
	box-shadow: 1px 1px 1px #888;
}


/* text input form elements */
input[type=text] {
	border: 1px solid #111540;
	background-color: #eee;
}


/* highlighting colors for the navigation box */
.navigation-item { background-color: #a1cbf6 !important; }
.navigation-item-d { background-color: #f7f7f7 !important; } /* order is */
.navigation-item-c { background-color: #8888bf !important; } /* important here */