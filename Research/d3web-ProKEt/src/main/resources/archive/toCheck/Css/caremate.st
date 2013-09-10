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


/* CHOSEN (active) question */
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
	background-color: rgb(255, 255, 66);
	background-color: rgba(255, 255, 66, 0.925);
	padding: 5px;
	-moz-border-radius-bottomright: 5px;
	-webkit-border-bottom-right-radius: 5px;
	-moz-box-shadow: 3px 3px 3px #888;
	-webkit-box-shadow: 3px 3px 3px #888;
	box-shadow: 3px 3px 3px #888;
	border: 1px solid black;
	font-weight: normal;
}

.title {
	padding-left: 4px;
	padding-right: 4px;
	vertical-align: middle;
	background-color: #e0e0e0;
}

.title,.title td {
	font-weight: bolder;
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
	font-size: 2em;
	font-variant: small-caps;
	text-align: center;
	background: #e7e7e7;
	background-image: none;
}

 
#foot{
	position: fixed;
	bottom: 0;
	left:0;
	width:100%;
	background: #e7e7e7;
	background-image: none;
}

#content {
  margin: 0px 190px 0px 190px;
}

#page-content{
	margin-top: 15px;
	margin-bottom: 50px;
}


#right {
	right: 8px;
	-moz-box-shadow: 0 0 5px black;
	-webkit-box-shadow: 0 0 5px black;
	box-shadow: 0 0 5px black;
	background:#f7f7f7;
	padding:2px;
}

.righttop {
	font-weight: bolder;
 	background:#e7e7e7;
 	padding-top: 2px;
 	padding-bottom: 2px;
 	padding-left: 5px;
}

.rightsub{	
	padding-left:10px;
 	padding-top: 2px;
 	padding-bottom: 2px;
}

#left {
	left: 8px;
	-moz-box-shadow: 0 0 5px black;
	-webkit-box-shadow: 0 0 5px black;
	box-shadow: 0 0 5px black;
	background: #f7f7f7;
	padding:2px;
}

.lefttop {
	font-weight: bolder;
 	background:#e7e7e7;
 	padding-top: 2px;
 	padding-bottom: 2px;
 	padding-left: 5px;
}

.leftsub{	
	padding-left:10px;
 	padding-top: 2px;
 	padding-bottom: 2px;
}

.ynbutton{
	width: 20px;
	height: 10px;
	background: red;
}