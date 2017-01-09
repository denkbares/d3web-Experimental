/**
 * Created by Lea on 05.12.2016.
 */
function startParser (){
    readTextFile("samples/sample.csv", function(text){
        var csv = text;
        csv = CSVtoArray(csv);
        createSunburst(csv);
    });
}

init();
startParser();
render();

function CSVtoArray (csv){
    var csvArray = [];
    csvArray = csv.split("\r");
    while(csvArray[csvArray.length-1] == "\n")
       csvArray = csvArray.slice(0, csvArray.length-1);
    return csvArray;
}

function getChildrenRoot(csv){
    var x = 0;
    for(var i = 0; i < csv.length; i++){
        arr = csv[i].split(";");
        if(arr[0] == "" || arr[0] == "\n") x++;
    }
    console.log(x);
    return x;
}

function createSunburst(csv){
    var defaultColors_small = [new THREE.Color(0x98AFC7), new THREE.Color(0xC79926), new THREE.Color(0xC7B097), new THREE.Color(0xA18648), new THREE.Color(0x98FB98),  new THREE.Color(0xA9A9A9),  new THREE.Color(0xE9967A),  new THREE.Color(0x8B0000),  	 new THREE.Color(0xCD5C5C),  new THREE.Color(0xF0E68C)];
    var defaultColors_big = [new THREE.Color(0x610B0B), new THREE.Color(0x61210B), new THREE.Color(0x8A0808), new THREE.Color(0x8A2908), new THREE.Color(0x8A4B080), new THREE.Color(0x886A08), new THREE.Color(0x868A08), new THREE.Color(0x688A08), new THREE.Color(0x4B8A08), new THREE.Color(0x298A08), new THREE.Color(0x088A08), new THREE.Color(0x088A29), new THREE.Color(0x088A4B), new THREE.Color(0x088A68), new THREE.Color(0x088A85), new THREE.Color(0x086A87), new THREE.Color(0x084B8A), new THREE.Color(0x08298A), new THREE.Color(0x0B2161), new THREE.Color(0x0B0B61), new THREE.Color(0x210B61), new THREE.Color(0x380B61), new THREE.Color(0x4C0B5F), new THREE.Color(0x610B5E), new THREE.Color(0x610B4B), new THREE.Color(0x610B38), new THREE.Color(0x610B21), new THREE.Color(0x2E2E2E)];
   
    if(getChildrenRoot(csv) >= 10) var defaultColors = defaultColors_big;
    else var defaultColors = defaultColors_small;

    var index = 0;
    
    var root = new sunburstElement("", "Sunburst", new THREE.Color(0x2E2E2E));
    var elements = new Map();
    elements.set("", root);
    var arr = [];
    var parent, child, label, color,currElement, parentElement;
    for (var i = 0; i < csv.length; i++){
        arr = csv[i].split(";");
        parent = arr[0].replace("\n", "");
        child = arr[1];
        label = arr[2];

        parentElement = elements.get(parent);
        if(parentElement == undefined){
            parentElement = new sunburstElement(parent, parent, defaultColors[index]);
            index ++;
            if(index >= defaultColors.length){
                index = 0;
            }
            parentElement.addDependency(root);
            elements.set(parent, parentElement);
        }

        if(arr[3]) color = arr[3];
        else if (parent == ""){
            color = new THREE.Color();
            color.copy(defaultColors[index]);

            index ++;
            if(index >= defaultColors.length){
                index = 0;
            }
        }
        else{
            color = new THREE.Color();
            color.copy(parentElement.color);
            color.r += 0.1;
            color.b += 0.1;
            color.g += 0.1;
        }
        currElement = new sunburstElement(child, label, color);
        elements.set(child, currElement);
        currElement.addDependency(parentElement);
    }
    root.renderFirst();
    currScene = root.scene;
}