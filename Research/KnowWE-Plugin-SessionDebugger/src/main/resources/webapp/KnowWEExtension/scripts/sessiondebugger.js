var SessionDebugger = {};

SessionDebugger.send = function(sessionid, casedate) {
			var topic = KNOWWE.helper.gup('page')
			
            var params = {
        		action : 'ExecuteCasesAction',
       			KWiki_Topic : topic,
       			id : sessionid,
        		date : casedate
    		}
            
            var options = {
                url : KNOWWE.core.util.getURL(params),
                response : {
                	action : 'none',
                	fn : function(){
			        	try {
	                		KNOWWE.helper.observer.notify('update');
			        	}
			        	catch (e) { /*ignore*/ }
			        	KNOWWE.core.util.updateProcessingState(-1);
                	},
                    onError : function () {
			        	KNOWWE.core.util.updateProcessingState(-1);                    	
                    }
                }
            }
        	KNOWWE.core.util.updateProcessingState(1);
            new _KA( options ).send();         
        }
        
SessionDebugger.change = function(sessionid, selectedvalue) {
			var topic = KNOWWE.helper.gup('page')
			
            var params = {
        		action : 'TestCaseSelectorAction',
       			KWiki_Topic : topic,
       			id : sessionid,
       			value : selectedvalue
    		}
            
            var options = {
                url : KNOWWE.core.util.getURL(params),
                response : {
                	action : 'none',
                	fn : function(){
			        	try {
	                		KNOWWE.helper.observer.notify('update');
			        	}
			        	catch (e) { /*ignore*/ }
			        	KNOWWE.core.util.updateProcessingState(-1);
                	},
                    onError : function () {
			        	KNOWWE.core.util.updateProcessingState(-1);                    	
                    }
                }
            }
        	KNOWWE.core.util.updateProcessingState(1);
            new _KA( options ).send();       

}