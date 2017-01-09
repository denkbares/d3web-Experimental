/**
 * Created by Lea on 23.11.2016.
 */
var currScene, renderer, camera, light, controls, firstScene, highlightedObj, raycaster, mouse, intersection, circles;
var scenes = [];

function init(){
    currScene = new THREE.Scene();

    camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 10, 10000);
    camera.position.z = 50;

    renderer = new THREE.WebGLRenderer({alpha: true});
    renderer.setSize(window.innerWidth, window.innerHeight);
    renderer.setClearColor(0xffffff, 0);
    document.body.appendChild(renderer.domElement);

    controls = new THREE.OrbitControls(camera, renderer.domElement);
    controls.enableDamping = true;
    controls.dampingFactor = 0.25;
    controls.enableZoom = true;
    controls.minDistance = 20;
    controls.enableRotate = false;

    raycaster = new THREE.Raycaster();

    mouse = new THREE.Vector2();

    circles = [];

    light = new THREE.HemisphereLight(0xFFFFFF, 1);
    light.position = camera.position;
    currScene.add(light);

    document.addEventListener('mousedown', onDocumentMouseDown, false);
    document.addEventListener( 'mousemove', onDocumentMouseMove, false );
    window.addEventListener('resize', onWindowResize, false);
}

function render() {
    requestAnimationFrame(render);
    controls.update();
    findIntersections();
    renderer.render(currScene, camera);
}

function onWindowResize(){
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize( window.innerWidth, window.innerHeight );
}

function onDocumentMouseMove (event){
    event.preventDefault();

    mouse.x = ( event.clientX / window.innerWidth ) * 2 - 1;
    mouse.y = - ( event.clientY / window.innerHeight ) * 2 + 1;
}

function findIntersections() {
    raycaster.setFromCamera( mouse, camera );
    var intersections = raycaster.intersectObjects(circles);
    if ( intersections.length > 0 ) {
        var i = intersections.length - 1;
        if ( intersection != intersections[0].object ) {
            intersection = intersections[0].object;
            Tip(intersections[0].object.label);
            highlight(intersections[0].object);
        }
    } else {
        intersection = null;
        removeHighlight();
        UnTip();
    }
}

function highlight(obj){
    removeHighlight();
    highlightedObj = obj;
    obj.material.color.r += 0.2;
    obj.material.color.g += 0.2;
    obj.material.color.b += 0.2;
}

function removeHighlight(){
    if(highlightedObj != undefined) {
        highlightedObj.material.color.r -= 0.2;
        highlightedObj.material.color.g -= 0.2;
        highlightedObj.material.color.b -= 0.2;
        highlightedObj = undefined;
    }
}

function onDocumentMouseDown (event){
    var raycaster = new THREE.Raycaster();
    var mouse3D = new THREE.Vector3(( (event.clientX / window.innerWidth ) * 2 - 1), ( -( event.clientY / window.innerHeight) * 2 + 1 ), 0.5);
    raycaster.setFromCamera(mouse3D, camera);
    var intersects = raycaster.intersectObjects(currScene.children);
    if (intersects.length > 0) {
        var obj = intersects[0].object;
        //animation(obj);
       // if(obj.object.scene == undefined || obj.scene == undefined) {
            obj.object.renderFirst();
       // }
        currScene = obj.scene;
        renderer.clear();
        render();
    }
}

function animation (obj){
    var animated;
    var thetaLength = obj.geometry.parameters.thetaLength;
    var thetaStart = obj.geometry.parameters.thetaStart;
    var radius = obj.geometry.parameters.radius;
    var geom;
    var material = obj.material.clone();
    while(thetaLength < (2*Math.PI)){
        if(animated != undefined) currScene.remove(animated);
        geom = new THREE.CircleGeometry(radius, 50, thetaStart, thetaLength);
        animated = new THREE.Mesh(geom, material);
        currScene.add(animated);
        thetaLength += 0.1;
        pauseBrowser(1000);
        render()
    }
    pauseBrowser(200);
    render();
}

function pauseBrowser(millis) {
    var date = Date.now();
    var curDate = null;
    do {
        curDate = Date.now();
    } while (curDate-date < millis);
}

function addCircle (radius, thetaStart, thetaLength, color, scene, ownScene, label, object){

    if(thetaStart == 0 && thetaLength ==(2*Math.PI)) {
        var geometry = new THREE.CircleGeometry(radius-0.1, 50, thetaStart, thetaLength);
        currScene = scene;
        if(firstScene == undefined){
            firstScene = scene;
        }
    }
    else {
        var geometry = new THREE.CircleGeometry(radius-0.1, 50, thetaStart-0.02, thetaLength-0.02);
    }
    var material = new THREE.MeshBasicMaterial();

    if(typeof color == "string"){
        material.color.setHex(color);
    }
    else
    {
        material.color = color;
    }
    var circle = new THREE.Mesh(geometry, material);
    circle.position.x = 0;
    circle.position.y = 0;
    circle.position.z = -(radius/10);
    circle.scene = ownScene;
    circle.label = label;
    scene.add(circle);
    circles.push(circle);

   circle.geometry.dynamic = true;
    circle.geometry.verticesNeedUpdate = true;
    circle.geometry.elementsNeedUpdate = true;
    circle.geometry.uvsNeedUpdate = true;
    circle.geometry.normalsNeedUpdate = true;
    circle.geometry.colorsNeedUpdate = true;
    circle.geometry.groupsNeedUpdate = true;
    circle.geometry.lineDistancesNeedUpdate = true;

    circle.object = object;

    //Add a surrounding white circle
    var surrGeometry = new THREE.CircleGeometry(radius, 50, thetaStart, thetaLength);
    var surrMaterial = new THREE.MeshBasicMaterial();
    surrMaterial.color.setHex("0xFFFFFF");
    var surrCircle = new THREE.Mesh(surrGeometry, surrMaterial);
    surrCircle.position.x = 0;
    surrCircle.position.y = 0;
    surrCircle.position.z = -((radius+0.02)/10);
    scene.add(surrCircle);
    
    return circle;
}