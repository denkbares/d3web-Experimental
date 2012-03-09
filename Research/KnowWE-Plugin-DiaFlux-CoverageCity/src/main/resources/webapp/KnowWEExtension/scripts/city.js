var yaw = 0;
var pitch = 0;
var dist = 100;

SceneJS.createScene({

    /* ID that we'll access the scene by when we want to render it
     */
    id: "theScene",

    /* Bind scene to a WebGL canvas:
     */
    canvasId: "theCanvas",

    nodes: [

        /* Viewing transform
         */
        {
            type: "lookAt",
			id: "theLookAt",
            eye : { x: 0.0, y: 10.0, z: dist },
            look : { y:0.0 },
            up : { y: -1.0 },

            nodes: [

                /* Projection
                 */
                {
                    type: "camera",
                    optics: {
                        type: "perspective",
                        fovy : 25.0,
                        aspect : 1.47,
                        near : 0.10,
                        far : 3000.0
                    },

                    nodes: [

                        /* Renderer node to set BG colour
                         */
                        {
                            type: "renderer",
                            clearColor: { r: 0.3, g: 0.3, b: 0.6 },
                            clear: {
                                depth : true,
                                color : true
                            },

                            nodes: [

                                /* Point lights
                                 */
                                {
                                    type: "light",
                                    mode:                   "dir",
                                    color:                  { r: 1.0, g: 1.0, b: 1.0 },
                                    diffuse:                true,
                                    specular:               true,
                                    dir:                    { x: 1.0, y: -0.5, z: -1.0 }
                                },
                                {
                                    type: "light",
                                    mode:                   "dir",
                                    color:                  { r: 1.0, g: 1.0, b: 0.8 },
                                    diffuse:                true,
                                    specular:               false,
                                    dir:                    { x: 0.0, y: -0.5, z: -1.0 }
                                },
                                /* Modelling transforms - note the IDs, "pitch" and "yaw"
                                 */
                                {
                                    type: "rotate",
                                    id: "pitch",
                                    angle: 0.0,
                                    x : 1.0,

                                    nodes: [
                                        {
                                            type: "rotate",
                                            id: "yaw",
                                            angle: 0.0,
                                            y : 1.0,

                                            nodes: [

                                                /* Ambient, diffuse and specular surface properties
                                                 */
                                                {
                                                    type: "material",
                                                    emit: 0,
                                                    baseColor:      { r: 0.5, g: 0.5, b: 0.6 },
                                                    specularColor:  { r: 0.9, g: 0.9, b: 0.9 },
                                                    specular:       1.0,
                                                    shine:          70.0,
													alpha: 0.5,
													flags: {
														transparent: true
													},

                                                    nodes: city
                                                    
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
});


/* Get handles to some nodes
 */
var scene = SceneJS.scene("theScene");
var yawNode = scene.findNode("yaw");
var pitchNode = scene.findNode("pitch");

/* As mouse drags, we'll update the rotate nodes
 */

var lastX;
var lastY;
var eyedX;
var eyedY;
var dragging = false;
var rightclick;

var newInput = false;


var canvas = document.getElementById("theCanvas");

function mouseDown(event) {
    lastX = event.clientX;
    lastY = event.clientY;
    dragging = true;
    
	if (!event) event = window.event;
	if (event.which) rightclick = (event.which == 3);
	else if (event.button) rightclick = (event.button == 2);
}

function mouseUp(event) {
    dragging = false;
    
}

function mouseMove(event) {
    if (dragging) {
    	if (!rightclick){
    		yaw += (event.clientX - lastX) * 0.5;
    		pitch += (event.clientY - lastY) * 0.5;
    		
    		lastX = event.clientX;
    		lastY = event.clientY;
    	} else {
    		eyedX = (event.clientX - lastX);
    		eyedY = (event.clientY - lastY)
    		
    	}

        newInput = true;
    } else {

        var coords = clickCoordsWithinElement(event);

        var hit = scene.pick(coords.x, coords.y, { rayPick: true });

        if (hit) {          	
        	document.getElementById("pickResult").innerHTML = hit.name;

        } else { // Nothing picked
        }
    }
}

function mouseWheel(event) {
    var delta = 0;
    if (!event) event = window.event;
    if (event.wheelDelta) {
        delta = event.wheelDelta / 120;
        if (window.opera) delta = -delta;
    } else if (event.detail) {
        delta = -event.detail / 3;
    }
    if (delta) {
        if (delta < 0) {
            dist -= 2.0;
        } else {
            dist += 2.0;
        }
    }
    if (event.preventDefault)
        event.preventDefault();
    event.returnValue = false;
    newInput = true;
}

canvas.addEventListener('mousedown', mouseDown, true);
canvas.addEventListener('mousemove', mouseMove, true);
canvas.addEventListener('mouseup', mouseUp, true);
canvas.addEventListener('mousewheel', mouseWheel, true);
canvas.addEventListener('DOMMouseScroll', mouseWheel, true);


/* Start the scene rendering
 */

scene.start({
    idleFunc: function() {
        if (newInput) {
            yawNode.set("angle", yaw);
            pitchNode.set("angle", pitch);
			
			this.findNode("theLookAt").set({ eye: { x: eyedX, y: eyedY, z: dist } });
			newInput = false;
        
        }
    }
});


function clickCoordsWithinElement(event) {
    var coords = { x: 0, y: 0};
    if (!event) {
        event = window.event;
        coords.x = event.x;
        coords.y = event.y;
    } else {
        var element = event.target ;
        var totalOffsetLeft = 0;
        var totalOffsetTop = 0 ;

        while (element.offsetParent)
        {
            totalOffsetLeft += element.offsetLeft;
            totalOffsetTop += element.offsetTop;
            element = element.offsetParent;
        }
        coords.x = event.pageX - totalOffsetLeft;
        coords.y = event.pageY - totalOffsetTop;
    }
    return coords;
}

