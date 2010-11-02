$basis()$


/* handle multicolumn layout helper tables */
.multicol {
	height: 1;
	width: 100%;
}
.multicol td {
	border-spacing: 5px;
	padding: 0px;
	vertical-align: top;
}
.multicol .question > .title td, .multicol .answer > .title td {
	padding-bottom: 0px;
}
.multicol td .question {
	height: 99%;
}


/* title elements */
.dialog > .title, .dialog > .title td { font-size: 16pt; }
.questionnaire > .title, .questionnaire > .title td {
	background: transparent;
}
.title, .title td {
	font-size: 1.5em;
	vertical-align: middle;
	background-color: #dddddd;
	font-weight: bolder;
}
#navigation .title, #navigation .title td { background: transparent; }
.answer .title {
	width: 200px;
	background-color: transparent;
	font-size: 10pt;
}
.answer-yn .title { padding: 3px 0px; }
.question-zc > .title td { background-color: transparent; }


/* style side boxes commonly */
#solutions, #navigation {
	background-color: #aae;
	padding: 0px;
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
#solutions li {
	list-style-type: none;
}


/* style questions */
.question {
	background-color: #dcfddc;
	border: 0px solid gray;
	margin: 0px;
}
.question-c {
	background-color: #ffffc8;
	border: 1px solid red;
}
.question-d {
	background-color: #f6f6f6;
	border: 0px solid gray;
}
.question-zc {
	background-color: #eee;
	border: 0px solid black;
	-moz-border-radius-bottomright: 0px;
	-webkit-border-bottom-right-radius: 0px;
	-moz-box-shadow: 0px 0px 0px #888;
	-webkit-box-shadow: 0px 0px 0px #888;
	box-shadow: 0px 0px 0px #888;
}
.question-images img { border: 0px solid black; }

#head{
	font-size: 1.5em;
	font-variant: small-caps;
	text-align: center;
	background: #e7e7e7;
	background-image: none;
	padding: 5px;
}
#content {
  margin: 0px 10px 0px 10px;
  width: 53%;
}


.title .td{font-size: 1.0em;}


/* tooltips */
.tooltip {
	z-index: 100;
	background-color: rgb(255, 255, 66);
	background-color: rgba(255, 255, 66, 0.925); /* attempt transparency */
	padding: 5px;
	border: 1px solid black;
	font-weight: normal;
	border: 1px solid #ccc;
}
.tooltip-trigger {
	background-image: url(images/info.gif);
	background-repeat: no-repeat;
}


/* button for sending data to the server */
.ok-button {
	border: 1px solid black;
	text-align: center;
	cursor: pointer;
	-moz-box-shadow: 1px 1px 1px #888;
	-webkit-box-shadow: 1px 1px 1px #888;
	box-shadow: 1px 1px 1px #888;
}

#left {width: 20%; margin-left: 10px;}

#right {
	margin-right: 10px;
	background:#B9BBD9;
	padding:2px;
	width: 20%;
}

.righttop {
	font-weight: bolder;
 	background:#AAAAEE;
 	padding-top: 2px;
 	padding-bottom: 5px;
 	padding-left: 5px;
}

.rightsub{	
	padding-left:10px;
 	padding-bottom: 5px;
}

/* highlighting colors for the navigation box */
.navigation-item { background-color: #dcfddc !important; }
.navigation-item-d { background-color: #f6f6f6 !important; } /* order is */
.navigation-item-c { background-color: #ffffc8 !important; border: 1px solid red !important; } /* important here */