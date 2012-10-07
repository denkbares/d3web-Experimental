
function submitTable(kdomid,user,tableid,versions) {
	
	var tableElement = _KS('#'+kdomid);
	var inputs = _KS('textarea',tableElement);
	var text = ''+versions+'#';
	 var len = inputs.length;
	 var input;
     for(i = 0; i < len; i++){
    	 input = inputs[i].value;
    	 while (input.contains("\n\n")) {
    		 input = input.replace("\n\n", "\n");
    	 }
    	 while (input.substring(input.length-1, input.length) == "\n") {
    		 input = input.substring(0, input.length-1);
    	 }
    	 text += 'Input'+i+':'+ input +";;";
     }
    var params = {
            action : 'SubmitTableContentAction',
            user : user,
            tableid: tableid,
            daten : text
        }

        var options = {
            url : KNOWWE.core.util.getURL(params),
            response : {
                action : 'insert',
                ids : [ 'tableSubmit_'+tableid ],
                fn : function(){ setTimeout ( 'document.location.reload()', 5000 ); }
            }
        }

        new _KA(options).send();
        
}

function additionalTable(kdomid,user,tableid) {
    var params = {
            action : 'InsertAdditionalTableVersionAction',
            user : user,
            tableid: tableid
        }

        var options = {
            url : KNOWWE.core.util.getURL(params),
            response : {
                action : '',
                ids : [ '' ],
                fn : function(){ setTimeout ( 'document.location.reload()', 100 ); }
            }
        }

        new _KA(options).send();

}