
function generateSqlFile() {
		var params = {
				action : 'WriteDB',
		}
		var options = {
				url : KNOWWE.core.util.getURL(params),
		        response : {
		            action : 'insert',
		            ids : [ 'medresult' ]
		        }
		}
		new _KA( options ).send();
}

function checkIfTableExists() {
	var params = {
			action : 'TableExistCheckAction',
	}
	var options = {
			url : KNOWWE.core.util.getURL(params),
	        response : {
	            action : 'insert',
	            ids : [ 'medresult' ]
	        }
	}
	new _KA( options ).send();
}

function cancelExport() {
	document.getElementById('medresult').innerHTML = '';
}

function readFromDB() {
		var params = {
				action : 'ReadDB',
		}
		var options = {
				url : KNOWWE.core.util.getURL(params),
		        response : {
		            action : 'insert',
		            ids : [ 'medresult' ]
		        }
		}
		new _KA( options ).send();
}