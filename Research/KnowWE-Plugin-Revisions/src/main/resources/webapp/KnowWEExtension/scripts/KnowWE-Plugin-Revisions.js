        var timeline;
        var data;
        
    	var defaultRevName = "Enter Name for new Revision";
    	var defaultRevComment = "optional Comment";
    	var defaultRevBtnTitle = "Add new Revision";
        
		window.onload=function drawVisualization() {

			// specify options
            var options = {
                'width':  '100%',
                'height': '200px',
                'editable': false,   // enable dragging and editing events
                'style': 'box',
				'showCustomTime': true,
				'showCurrentTime': true,
				'showButtonNew': true,
				'intervalMax':  631138519494, // 20 year if you trust google
				//'max': new Date(), //max date is actual date, future not needed
//				'end': new Date((new Date()).getTime() + (24 * 60 * 60 * 1000)) //show today in the timeline
				};
            // Instantiate our timeline object.
            timeline = new links.Timeline(document.getElementById('mytimeline'));
            function onRangeChanged(properties) {
                document.getElementById('info').innerHTML += 'rangechanged ' +
                        properties.start + ' - ' + properties.end + '<br>';
            }
            // attach an event listener using the links events handler
            links.events.addListener(timeline, 'rangechanged', onRangeChanged);
            // Draw our timeline with the created data and options
            timeline.draw(data, options);
			links.events.addListener(timeline, 'select', eventSelected);
			links.events.addListener(timeline, 'timechanged', lineDragged);
			links.events.addListener(timeline, 'change', eventChanged);
						
			if (document.getElementById('uploadedLink').innerHTML != '') {
				showUploadedRev();
			} else {
				showCurrentRev();
			}
		}

		function eventChanged() {
			showRevision(true);
		}
		
        function eventSelected() {
        	showRevision(false);
        }
        	
        function showRevision(changed) {
			var sel = timeline.getSelection();
			if (sel.length) {
				if (sel[0].row != undefined) {
					var row = sel[0].row;
					var start = timeline.getItem(row).start;
					var group = timeline.getItem(row).group;
//					if (group = 'Uploaded') {
//						var params = {
//								action : 'UploadedRevDetails'
//							}
//					} else {											
						if (timeline.getData()[row].editable == true) {
							// unsaved revision selected, so take the data from the form
							var params = {
									action : 'UnsavedRevDetails',
									rev : document.getElementById('newRevName').value,
									comment : document.getElementById('newRevComment').value,
									changed : changed,
									date : start.getTime()
								}
						} else {
							// saved revision selected, so take section id from data, id is 'uploaded' for uploaded revision
							var params = {
									action : 'WikiRevDetails',
									rev : timeline.getItem(row).content,
									id : data[row].id,
									date : start.getTime()
								}
						}
//					}
					var options = {
							url : KNOWWE.core.util.getURL(params),
							response : {
								action : 'insert',
								ids : [ 'revdetails' ]
//								fn : KNOWWE.core.util.addCollabsiblePluginHeader
							}
					}
					
					new _KA( options ).send();
				}
			}
			else {
	        	document.getElementById("revdetails").innerHTML = "";
			}
		}
		
        function lineDragged() {
			var time = timeline.getCustomTime().getTime();
			timeline.setSelection([]);

			var params = {
				action : 'WikiDateDetails',
				date : time
			}
			var options = {
					url : KNOWWE.core.util.getURL(params),
					response : {
						action : 'insert',
						ids : [ 'revdetails' ]
//						fn : KNOWWE.core.util.addCollabsiblePluginHeader
					}
			}
			new _KA( options ).send();
		}
        

        /**
         * Add a new event
         */
        function addRev() {
        	// compatibility if browser has no string trim function
            if(!String.prototype.trim) {
          	  String.prototype.trim = function () {
          	    return this.replace(/^\s+|\s+$/g,'');
          	  };
          	}
            document.getElementById("newRevName").value = document.getElementById("newRevName").value.trim();
            
            var name = document.getElementById("newRevName").value;

            if (name == defaultRevName || name == "") {
            	document.getElementById("newRevName").value = defaultRevName;
	        	document.getElementById("revdetails").innerHTML = "<p class=\"box error\">Please enter a valid name for the revision.</p>";
            } else {
                var date = timeline.getCustomTime().getTime();

	            timeline.addItem({
	                'start': date,
	                'content': name + "<a onclick=\"saveRev();\"> <img title='save' src='KnowWEExtension/images/fsp_established_opt2.png'></a> <a onclick=\"delRev();\"><img title='delete' src='KnowWEExtension/images/cross.png'></a>",
					'editable': true
	            });
	
		            var count = Object.keys(data).length;
		            timeline.setSelection([{
		                'row': count-1
		            }]);
					// action neu laden!
		            document.getElementById('addrevbtn').disabled = true;
		            document.getElementById('newRevName').disabled = true;
		            if (document.getElementById('newRevComment').value == defaultRevComment) {
		            	document.getElementById('newRevComment').value = "";
		            }
		            document.getElementById('newRevComment').disabled = true;
		            document.getElementById('addrevbtn').title = "Save the priviously created revision first.";
		            
		        	document.getElementById("revdetails").innerHTML = "<p class=\"box ok\">Added Revision '"+name+"' to timeline.</p>"
		        	+"<p class=\"box info\">If necessary, you can <b>drag</b> it to the correct position.</p>";
		        	document.getElementById("reverror").innerHTML = "<p class=\"box info\">Don't forget to <b>save the new revision</b> to persistence.</p>";
	            }
        }

        /**
         * Save the currently selected revision
         */
        function saveRev() {
            // retrieve the selected row
            var sel = timeline.getSelection();
            if (sel.length) {
                if (sel[0].row != undefined) {
					var row = sel[0].row;
					var start = timeline.getItem(row).start;
					var content = timeline.getItem(row).content;
					
					timeline.deleteItem(row, false);
					content = document.getElementById("newRevName").value;
					timeline.addItem({
		                'start': start,
		                'content': content,
						'editable': false
		            });

					var params = {
						action : 'SaveRevision',
						rev : content,
						date : start.getTime(),
		                comment: document.getElementById("newRevComment").value
					}
					var options = {
							url : KNOWWE.core.util.getURL(params),
							response : {
								action : 'insert',
								ids : [ 'revdetails' ],
//								fn : KNOWWE.core.util.addCollabsiblePluginHeader
							}
					}
					new _KA( options ).send();
					
		            document.getElementById('addrevbtn').removeAttribute("disabled");
		            document.getElementById('newRevName').removeAttribute("disabled");
		            document.getElementById('newRevComment').removeAttribute("disabled");
		            document.getElementById('addrevbtn').title = defaultRevBtnTitle;
		            document.getElementById('newRevName').value = defaultRevName;
		            document.getElementById('newRevComment').value = defaultRevComment;
		            document.getElementById("reverror").innerHTML = "";
				}
            }
        }

        /**
         * Delete the currently selected revision
         */
        function delRev() {
            // retrieve the selected row
            var sel = timeline.getSelection();
            if (sel.length) {
                if (sel[0].row != undefined) {
					var row = sel[0].row;
//					var start = timeline.getItem(row).start;
//					var content = timeline.getItem(row).content;
					
					timeline.deleteItem(row, false);
					
		            document.getElementById('addrevbtn').removeAttribute("disabled");
		            document.getElementById('newRevName').removeAttribute("disabled");
		            document.getElementById('newRevComment').removeAttribute("disabled");
		            document.getElementById('addrevbtn').title = defaultRevBtnTitle;
		            var title = document.getElementById('newRevName').value;
		            document.getElementById('revdetails').innerHTML = "<p class=\"box ok\">Revision '"+title+"' <b>removed</b></p>";
		            document.getElementById("reverror").innerHTML = "";
				}
            }            
        }
        
        
        /**
         * Restore the selected revision
         */
        function restoreRev(time) {
			var params = {
				action : 'RestoreRevision',
				date : time
			}
			var options = {
					url : KNOWWE.core.util.getURL(params),
					response : {
						action : 'insert',
						ids : [ 'revdetails' ]
//						fn : KNOWWE.core.util.addCollabsiblePluginHeader
					}
			}
			new _KA( options ).send();
			
		}
        
        /**
         * Show a text diff to current version
         * @param title is the title of the wiki page for the diff
         * @param version is the version to compare with
         */
        function showDiff(title,version) {
        	var diffTarget = 'diffdiv';
			var params = {
					action : 'SimpleTextDiff',
					title : title,
					version : version
				}
				var options = {
						url : KNOWWE.core.util.getURL(params),
						response : {
							action : 'insert',
							ids : [ diffTarget ]
//							fn : KNOWWE.core.util.addCollabsiblePluginHeader
						}
				}
				new _KA( options ).send();
        }
        
        
        function downloadRev(date) {
			window.location='action/DownloadRevisionZip?KWiki_Topic=Main&KWikiWeb=default_web&date='+date;
		}
        
        function showUploadedDiff(title,version) {
        	var diffTarget = 'diffdiv';
			var params = {
				action : 'UploadedTextDiff',
				title : title
			}
			var options = {
					url : KNOWWE.core.util.getURL(params),
					response : {
						action : 'insert',
						ids : [ diffTarget ]
//							fn : KNOWWE.core.util.addCollabsiblePluginHeader
					}
			}
			new _KA( options ).send();
        }
        
        function showCurrentRev() {
			var params = {
					action : 'CurrentRevDetails'
				}
				var options = {
						url : KNOWWE.core.util.getURL(params),
						response : {
							action : 'insert',
							ids : [ 'revdetails' ]
//								fn : KNOWWE.core.util.addCollabsiblePluginHeader
						}
				}
				new _KA( options ).send();
        }
        
        function showUploadedRev() {
			var params = {
					action : 'UploadedRevDetails'
				}
				var options = {
						url : KNOWWE.core.util.getURL(params),
						response : {
							action : 'insert',
							ids : [ 'revdetails' ]
//								fn : KNOWWE.core.util.addCollabsiblePluginHeader
						}
				}
				new _KA( options ).send();
        }