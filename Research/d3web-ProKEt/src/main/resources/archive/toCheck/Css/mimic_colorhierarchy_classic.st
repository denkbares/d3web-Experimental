$basis()$

body,table,div {
	padding: 0px;
	margin: 0px;
	font-family: Arial;
	font-size: 11pt;
	text-color: black;
}


/* questions */
.question {
	background-color: transparent;
	clear: left;
}
.question-closed {
	padding-left: 30px;
	background: transparent url(images/closed_folder.png) no-repeat top
		left;
}
.question-open {
	padding-left: 30px;
	background: transparent url(images/open_folder.png) no-repeat top left;
}



/* title elements */
.title {
	padding: 4px;
	vertical-align: middle;
	font-weight: bolder;
	background-color: #e0e0e0;
}
.question .title {
	min-height: 16px;
	padding-left: 90px;
	line-height: 1.8em;
}



/* tooltips */
.tooltip {
	z-index: 100;
	background-color: rgb(255, 255, 66);
	background-color: rgba(255, 255, 66, 0.925);
	padding: 5px;
	-moz-border-radius-bottomright: 5px;
	-webkit-border-bottom-right-radius: 5px;
	-moz-box-shadow: 3px 3px 3px #888;
	-webkit-box-shadow: 3px 3px 3px #888;
	border: 1px solid black;
	font-weight: normal;
}


/* alternating colors */
.color-even { background-color: #ddd; }
.color-odd { background-color: #eee; }


/* rating colors */
.rating-high, .rating-medium, .rating-low { border: 0px solid black; }
.rating-high > .title { background-color: #aee8af; border: 1px solid #376a38; }
.rating-medium > .title { background-color: #fffac0; border: 1px solid #a0983d; }
.rating-low > .title { background-color: #e8aeb4; border: 1px solid #a03d48; } 


.question-imagebox {
	line-height: 1.8em;
	padding: 4px;
	float: left;
}


#bonus {
	padding: 4px;
	background: rgb(120, 120, 220);
	width: 200px;
	min-height: 100px;
	font-size: 0.6em;
	overflow: auto;
	margin-top: 5px;
}