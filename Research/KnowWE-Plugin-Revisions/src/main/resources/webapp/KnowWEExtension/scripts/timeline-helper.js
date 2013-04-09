        var timeline;
        var data;
        
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
			links.events.addListener(timeline, 'select', onselect);
			links.events.addListener(timeline, 'timechanged', ondragged);
			links.events.addListener(timeline, 'change', ondragged);

		}

        function onselect() {
			var sel = timeline.getSelection();
			if (sel.length) {
				if (sel[0].row != undefined) {
					var row = sel[0].row;
					var start = timeline.getItem(row).start;
					var content = timeline.getItem(row).content;

					var params = {
						action : 'TimelineSelectAction',
						rev : content,
						date : start.getTime()
					}
					var options = {
							url : KNOWWE.core.util.getURL(params),
							response : {
								action : 'insert',
								ids : [ 'revdetails' ],
								fn : KNOWWE.core.util.addCollabsiblePluginHeader
							}
					}
					new _KA( options ).send();
				}
			}
		}
		
        function ondragged() {
			var time = timeline.getCustomTime().getTime();

			var params = {
				action : 'TimelineSelectAction',
				rev : time,
				date : time
			}
			var options = {
					url : KNOWWE.core.util.getURL(params),
					response : {
						action : 'insert',
						ids : [ 'revdetails' ],
						fn : KNOWWE.core.util.addCollabsiblePluginHeader
					}
			}
			new _KA( options ).send();
		}
        

        /**
         * Add a new event
         */
        function addRev() {
            var start = timeline.getCustomTime().getTime();
            var content = document.getElementById("txtContent").value;

            content += "<br/><a href=\"#\" onclick=\"saveRev();\">save</a> <a href=\"#\" onclick=\"delRev();\">delete</a>";
            
            timeline.addItem({
                'start': start,
                'content': content,
				'editable': true
            });

            var count = Object.keys(data).length;
            timeline.setSelection([{
                'row': count-1
            }]);
			// action neu laden!
        }

        /**
         * Change the content of the currently selected event
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
					content = content.substring(0,content.length-89)
					timeline.addItem({
		                'start': start,
		                'content': content,
						'editable': false
		            });

					var params = {
						action : 'TimelineSaveAction',
						rev : content,
						date : start.getTime()
					}
					var options = {
							url : KNOWWE.core.util.getURL(params),
							response : {
								action : 'insert',
								ids : [ 'revdetails' ],
								fn : KNOWWE.core.util.addCollabsiblePluginHeader
							}
					}
					new _KA( options ).send();
					
				}
            }
        }

        /**
         * Delete the currently selected event
         */
        function delRev() {
            // retrieve the selected row
            var sel = timeline.getSelection();
            if (sel.length) {
                if (sel[0].row != undefined) {
					var row = sel[0].row;
					var start = timeline.getItem(row).start;
					var content = timeline.getItem(row).content;
					
					timeline.deleteItem(row, false);
				}
            }
        }
		