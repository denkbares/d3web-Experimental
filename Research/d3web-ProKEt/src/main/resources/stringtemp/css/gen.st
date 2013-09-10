$basis()$

body,table,div {
	padding: 0px;
	margin: 0px;
	font-family: Arial;
	font-size: 10pt;
	text-color: black;
}


/* GENERAL question */
.question .title {
	font-size: 11pt;
	background-color: #C9D6D8;
	-moz-box-shadow: none;
	-webkit-box-shadow: none;
	box-shadow: none;
}

/* GENERAL question */
.question {
	background-color: #E0EFF1;
	margin: 10px;
	-moz-box-shadow: 0 0 5px black;
	-webkit-box-shadow: 0 0 5px black;
	box-shadow: 0 0 5px black;
	padding: 2px;
}


/* CHOSEN (active) question title */
.question-c .title {
	background-color: #8EAABA;
	border: 1px solid #9FBED0;
}

/* CHOSEN (active) question */
.question-c {
	background-color: #9FBED0;
	border: 2px solid #680148;
	padding: 2px;
}


/* DONE (answered) question */
.question-d .title {
	background-color: #e7e7e7;
}

/* DONE (answered) question */
.question-d {
	background-color: #f7f7f7;
	padding: 2px;
}

.tooltip {
	z-index: 100;
	background-color: rgb(255, 255, 180);
	background-color: rgba(255, 255, 180, 0.525);
	padding: 5px;
	-moz-border-radius-bottomright: 5px;
	-webkit-border-bottom-right-radius: 5px;
	-moz-box-shadow: 3px 3px 3px #888;
	-webkit-box-shadow: 3px 3px 3px #888;
	box-shadow: 3px 3px 3px #888;
	border: 1px solid #BBB;
	font-weight: normal;
	color: #555;
}

.title {
	padding-left: 4px;
	padding-right: 4px;
	vertical-align: middle;
	background-color: #e0e0e0;
	/*-moz-box-shadow: 0 0 5px black;
	-webkit-box-shadow: 0 0 5px black;
	box-shadow: 0 0 5px black;*/
}

.title,.title td {
	font-weight: bolder;
	font-size: 1.0em;
}

.answer .title {
	width: 200px;
	background-color: transparent;
	font-size: 10pt;
	padding: 2px;
}

.ok-button {
	border: 1px solid black;
	text-align: center;
	cursor: pointer;
	-moz-box-shadow: 1px 1px 1px #888;
	-webkit-box-shadow: 1px 1px 1px #888;
	box-shadow: 1px 1px 1px #888;
}

.yn .title {
	padding: 3px 0px;
}

#head{
	font-size: 1.5em;
	font-variant: caps;
	text-align: center;
	background: #e7e7e7;
	background-image: none;
	padding: 5px;
}

 
#foot{
	display: none;
}

#content {
  margin: 0px 10px 0px 10px;
  width: 55%;
}

#page-content{
	margin-top: 15px;
	margin-bottom: 50px;
}


#right {
	margin-right: 10px;
	-moz-box-shadow: 0 0 5px black;
	-webkit-box-shadow: 0 0 5px black;
	box-shadow: 0 0 5px black;
	background:#f7f7f7;
	padding:2px;
	margin-top: 10px;
	width: 20%;
}

.righttop {
	font-weight: bolder;
 	background:#e7e7e7;
 	padding-top: 2px;
 	padding-bottom: 5px;
 	padding-left: 5px;
}

.rightsub{	
	padding-left:10px;
 	padding-bottom: 5px;
}

#left {
	margin-left: 10px;
	-moz-box-shadow: 0 0 5px black;
	-webkit-box-shadow: 0 0 5px black;
	box-shadow: 0 0 5px black;
	background: #f7f7f7;
	padding:2px;
	margin-top: 10px;
	width: 20%;
}

.lefttop {
	font-weight: bolder;
 	background:#e7e7e7;
 	padding-top: 2px;
 	padding-bottom: 5px;
 	padding-left: 5px;
}

.leftsub{	
	padding-left:10px;
 	padding-top: 5px;
 	padding-bottom: 5px;
}


/* Styling the link coloring in the left menu */

.x_nav_item > a{
	color:  #680148;
}

.x_nav_item-c > a{
	color: #8EAABA;
}

.x_nav_item {
	padding-left: 0px;
	background:transparent url(/images/normal.png) no-repeat center left;
}

.x_nav_item-c {
	padding-left: 0px;
	background:transparent url(/images/current.png) no-repeat center left;
}

.x_nav_item-d {
	padding-left: 0px;
	background:transparent url(/images/done.png) no-repeat center left;
}

#x_questionnairenav{display: none;}
