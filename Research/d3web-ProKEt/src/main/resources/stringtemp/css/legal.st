$basis()$

body,div {
	padding: 0px;
	margin: 0px;
	font-family: Arial;
	font-size: 10pt;
	text-color: black;
}

#middle{
	margin-left: auto;
    margin-right: auto;
    width: 85%; 
}

.question {
	background-color: transparent;
	clear: left;
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
	-moz-box-shadow: 0 0 3.5px black;
	-webkit-box-shadow: 0 0 3.5px black;
	box-shadow: 0 0 3.5px black;
}

.question{
	margin-top: 0px;
}

.question .title {
	min-height: 16px;
	padding-left: 90px;
	line-height: 1.75em;
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

.color-even {
	background-color: #ddd;
}

.color-odd {
	background-color: #eee;
}

.question-yes {
	background-color: #C3DAA8;
}

.question-no {
	background-color: #E0B59E;
}

.question-undecided{
	background-color: #F4F4C7;
}

.question-unanswered {
	
}

/* rating colors */
.rating-high, .rating-medium, .rating-low { border: 0px solid black; }
.rating-high > .title { background-color: #C3DAA8; border: none; }
.rating-medium > .title { background-color: #F4F4C7; border: none; }
.rating-low > .title { background-color: #E0B59E; border: none; }

.question-imagebox {
	line-height: 1.5em;
	padding: 4px;
	float: left;
}

#content {float: none;margin-left: auto; margin-right: auto; width: 85%;}

#right {display: none;}

#left {
	display: none;
}

#head{
	font-size: 1.5em;
	font-variant: small-caps;
	text-align: center;
	background: #e7e7e7;
	background-image: none;
	padding: 5px;
}


table{
	margin-top: 10px;
	width: 100%;
}


tr, td{
	width: 100%;
}