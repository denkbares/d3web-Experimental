
function submitTable(kdomid,user,tableid) {
	
	var tableElement = _KS('#'+kdomid);
	var inputs = _KS('textarea',tableElement);
	var text = '';
	 var len = inputs.length;
     for(i = 0; i < len; i++){
    	 text += 'Input'+i+':'+inputs[i].value+";";
     }
    var params = {
            action : 'SubmitTableContentAction',
            user : user,
            tableid: tableid,
            data : text
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
	

}