$basis()$

body,div {
	padding: 0px;
	margin: 0px;
	font-family: Arial;
	font-size: 10pt;
	text-color: black;
}

#content {float: none;margin-left: auto; margin-right: auto; width: 100%;
    clear: both; padding-top: 35px;}

#right {display: none;}

#left {display: none;}

#head{
    background: #e7e7e7;
    background-image: none;
    clear: both;
    min-height: 35px;
    width: 100%;
    position: fixed;
}

#headertext{
	font-size: 1.5em;
	font-variant: small-caps;
	text-align: center;
	margin-right: 10%;
        margin-left: 10%;
	padding: 5px;
        position: fixed;
        width: 70%;
}

#intro{ 
    font-family: Arial;
	font-size: 9pt;
	text-color: darkgrey;
        position: fixed; 
        background-color: white;
    padding: 10px;
    top: 35px;
    line-height: 1.3em;
}

#dialog{
    float: left;
    margin-top: 25px;
}


.auxpanelRight{
    background-color: #fffff0;
    width: 20%;
    -moz-box-shadow: 0 0 3.5px black;
	-webkit-box-shadow: 0 0 3.5px black;
	box-shadow: 0 0 3.5px black;
    position: fixed;
    right: 20px;
    padding: 10px;
    margin-top: 110px;
    height: 75%;
    overflow: auto; /* for scrolling */
}

.auxpanelBottom{
    background-color: #fffff0;
    width: 99%;
    -moz-box-shadow: 0 0 3.5px black;
	-webkit-box-shadow: 0 0 3.5px black;
	box-shadow: 0 0 3.5px black;
    position: fixed;
    left: 0px;
    right: 0px;
    bottom: 0px;
    padding: 10px;
    height: 25%;
    overflow: auto;
}


.auxpanelButton{
    width: 75px; height: 16px; padding-top: 1px; border: 1px solid #696969; text-align: center; cursor: pointer; 
	background-color: #dddddd; font-weight: normal; color: 	#8b4513; -moz-box-shadow: 0 0 5px black;	
	-webkit-box-shadow: 0 0 5px black; box-shadow: 0 0 5px black; margin-bottom: 10px;
}

table{
	margin-top: 10px;
	width: 100%;
}


.question {
	background-color: transparent;
        margin-top: 10px;
        margin-left: 20px;
}


/* TOOLTIPS, ADD STUFF */
.tooltip { z-index: 100; background-color: #fffacd;
	padding: 2px; -moz-border-radius-bottomright: 5px; -webkit-border-bottom-right-radius: 5px;
	-moz-box-shadow: 3px 3px 3px #888; -webkit-box-shadow: 3px 3px 3px #888; box-shadow: 3px 3px 3px #888;
	border: 1px solid #BBB; font-weight: normal; color: #555; font-size: 1.3em; }

.tooltipProp{font-size: 0.8em; line-height: 1.1em;}

.title {
	vertical-align: middle;
	background-color: #e0e0e0;
	-moz-box-shadow: 0 0 3.5px black;
	-webkit-box-shadow: 0 0 3.5px black;
	box-shadow: 0 0 3.5px black;
}

.question .title {
	float: none;
    line-height: 1.75em;
    margin-left: 10px;
    min-height: 23px;
}


/* initial coloring of questions: alternating greytones */
.color-even {
	background-color: #ddd;
}
.color-odd {
	background-color: #eee;
}


/* rating colors for question backgrounds */
/*.rating-high, .rating-medium, .rating-low { 
    border: 0px solid black; 
}
.rating-high > .title { 
    background-color: #C3DAA8; border: none; 
}
.rating-medium > .title { 
    background-color: #F4F4C7; border: none; 
}
.rating-low > .title { 
    background-color: #E0B59E; border: none; 
}

.ynbutton.rating-high { 
    background-color: #C3DAA8; border: none; 
}
.ynbutton.rating-medium { 
    background-color: #F4F4C7; border: none; 
}
.ynbutton.rating-low { 
    background-color: #E0B59E; border: none; 
}

.ynbutton.rating-none { 
    background-color: #DCDFF3; border: none; 
}*/


.rating-high, .rating-medium, .rating-low { 
    border: 0px solid black; 
}
.rating-high > .title { 
    background-color: #B0C4DE; border: none; 
}
.rating-medium > .title { 
    background-color: #FAF0E6; border: none; 
}
.rating-low > .title { 
    background-color: #ffdead; border: none; 
}

.ynbutton.rating-high { 
    background-color: #B0C4DE; border: none; 
}
.ynbutton.rating-medium { 
    background-color: #FAF0E6; border: none; 
}
.ynbutton.rating-low { 
    background-color: #ffdead; border: none; 
}

.ynbutton.rating-none { 
    background-color: #DDDDDD; border: none; 
}


/* Styles the box containing the button panel */
.question-imagebox { 
    float: left;
    line-height: 1em;
    padding: 2px;
    width: 130px;
}


/* Style the appearance of area fields, as in the button panel */
area {
    border: none;
}


/* Styles the div that displays hints of the readflow, e.g. "und/and, oder/or"...*/
.readflow{
    float: left; clear: both; margin-top: 7px;
}


/* Styling of the div within questions that displays the currently propagated value */
.propagation{
    display: block; float: right;
    height: 23px;
    margin-top: -28px;
    width: 25%;
}
.propagation.rating-high { 
    background-color: #C3DAA8; border: 1px solid grey; 
}
.propagation.rating-medium { 
    background-color: #F4F4C7; border: 1px solid grey; 
}
.propagation.rating-low { 
    background-color: #E0B59E; border: 1px solid grey; 
}

.question-toggleImg { 
    line-height: 1.5em; padding: 2px; float: left; margin-left: 15px; margin-right: 5px;
}

/* style the num input area of hierarchical num questions */
.floatingnum {line-height: 1.5em; padding: 2px; float: left; margin-right: 15px;}

.floatingnum input {height: 10px; margin-top: 1px; margin-left: 17px;}


/* style the dropdown area of hierarchical oc questions */ 
.floatingCDrop {line-height: 1.5em; padding: 3px; float: left; margin-right: 15px;}

.floatingCDrop select{width: 54px; margin-left: 17px;}


.dialogCompleteWidth{
    width: 99%;
}

.dialog75Width{
    width: 75%;
}

.auxPropagationInfo{color: #800000; font-size: 1.1em;}

.hide{display: none;}

.show{display: block;}

#solutionboxtextInTree{
    font-size: 0.7em;
    font-variant: small-caps;
    font-weight: normal;
    margin-left: 5px;
}

.solutiontitle{
float: none;
}

.ynbutton {
    box-shadow: 0 0 2px black;
    cursor: pointer;
    float: left;
    font-size: 0.6em;
    font-weight: bold;
    height: 12px;
    margin-right: 3px;
    margin-top: 3px;
    text-align: center;
    width: 20px;
}

img{
    float: left;
    margin-right: 5px;
}

.solution{
    box-shadow: 0 0 3.5px black;
    clear: both;
    font-size: 1.5em;
    font-weight: bold;
    margin-left: auto;
    margin-right: auto;
    margin-top: 105px;
    padding: 30px;
    width: 65%;
}

.solRating {
      background-color: #DEDEDE;
    float: right;
    font-weight: bold;
    padding: 2px 15px;
    margin-top: -16px;
    margin-right: -20px;
    min-height: 32px;
}

/*.solRating.rating-high { 
    background-color: #C3DAA8; 
}
.solRating.rating-medium { 
    background-color: #F4F4C7; 
}
.solRating.rating-low { 
    background-color: #E0B59E;  
}*/

.solRating.rating-high { 
    background-color: #B0C4DE; 
}
.solRating.rating-medium { 
    background-color: #FAF0E6; 
}
.solRating.rating-low { 
    background-color: #ffdead;  
}


#solutionboxtext{
    font-size: 0.6em;
    font-variant: small-caps;
    font-weight: normal;
    margin-left: -25px;
    margin-top: -25px;
}

#solutiontext{
     float: left;
    padding: 2px 0;
}

.solutiontitle{
    float: none;
    min-height: 16px;
	line-height: 1.75em;
        margin-left: 15px;
        margin-bottom: 5px;
}

.prop1and3{
    background: url('/img/1and3.png');
}

.prop1and2{
    background: url('/img/1and2.png');
}

.prop2and3{
    background: url('/img/2and3.png');
}