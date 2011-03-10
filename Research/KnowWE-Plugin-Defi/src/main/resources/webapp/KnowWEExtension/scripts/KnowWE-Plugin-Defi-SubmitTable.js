
function submitTable(kdomid,user,tableid) {
	
	var tableElement = _KS('#'+kdomid);
	var inputs = _KS('input',tableElement);
	var text = '';
	 var len = inputs.length;
     for(i = 0; i < len-1; i++){
    	 text += 'Input'+i+':'+inputs[i].value+";";
     }
	
     //alert(text+ "  \nuser:"+ user + "  \ntable-kdomid: "+kdomid+ "  \ntable-id: "+tableid);

	
	
	
    var params = {
            action : 'SubmitTableContentAction',
            user : user,
            tableid: tableid,
            data : text
        }

        var options = {
            url : KNOWWE.core.util.getURL(params),
            response : {
                action : '',
                ids : [ '' ]
            }
        }

        new _KA(options).send();
        
}