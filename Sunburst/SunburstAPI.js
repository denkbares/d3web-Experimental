/**
 * Created by Lea on 23.11.2016.
 */
function sunburstElement (id, label, color) {
    this.id = id;
    this.contains = [];
    this.parent;
    this.geometry;
    this.size = 0;
    this.label = label;
    if(typeof color == "string") {
        this.color = color.replace("#", "0x");
    }
    else this.color = color;
    this.scene = new THREE.Scene();
    this.scene.label = this.label;
}

sunburstElement.prototype = {
    
    getSize: function (){
        if(this.contains.length == 0) {
            this.size = 1;
            return 1;
        }
        else {
            var sum = 0;
            for (var i = 0; i < this.contains.length; i++) {
                sum += this.contains[i].getSize();
            }
            this.size = sum;
            return sum;
        }
    },
    
    addDependency: function (dependency){
        this.parent = dependency;
        dependency.contains.push(this);
    },

    renderFirst: function (){
        this.getSize();
        this.geometry = addCircle(6, 0, (2*Math.PI), this.color, this.scene, this.scene, this.label, this);
        this.geometry.scene = this.scene;
        var smallestPart = (2*Math.PI) / this.size;
        var parts;
        var next = 0;
        for(var i = 0; i < this.contains.length; i++){
            parts = this.contains[i].size * smallestPart;
            this.contains[i].render(parts, next, 10, this.scene);
            next += parts;
        }

        if(this.parent != undefined) addCircle(2, 0, (2*Math.PI), narrator.color, this.scene, narrator.scene, narrator.label, narrator);
    },

    render: function(length, start, radius, scene){
            this.geometry = addCircle(radius, start, length, this.color, scene, this.scene, this.label, this);
            radius += 3;
            var parts;
            var next = start;
            var smallestPart = length / this.size;
            for (var i = 0; i < this.contains.length; i++) {
                parts = this.contains[i].size * smallestPart;
                this.contains[i].render(parts, next, radius, scene);
                next += parts;
            }
    },
}
    


