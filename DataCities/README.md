#DataCities

This project is data-visualization in form of a city. It either displays hierarchies by the use of districts and buildings or tables by using the districts as rows.
The based data is given in form of a JSON-document.

##The HTML

The html-document needs to include the following files **in the body**:
1. "wz_tooltip.js"
2. "js/three.js"
3. "js/OrbitControls.js"
4. "lowlevel.js"
5. "DataImporter.js"
6. "dataCityAPI.js"
7. "JSONParser.js"

Furthermore there needs to be a "json"-variable declared in the html (even if you want to use a external json-file!).

##The JSON
The JSON must either be defined as a file or as a variable in the html-document. 
If it's defined as a file it needs to be included in the "startParser"-method of "JSONParser.js". In this case it's not possible to use dataCities via Chrome (will throw 
Otherwise a variable named "json" containing the json-object needs to be declared in the html-document. 

In this document the buildings, districts and the city are defined. It's structured hierarchically as the city-object contains each other object.

Each object needs a **"label"**- and a **"color"**-attribute. 
Labels have to be objects containing a **"text"**-attribute (shown as a tooltip) and optional an **"url"**-attribute (opened on right click). In the text-attribute html can be used.
Colors need to be given as  html color codes (e.g. "#FFFFFF" or "0xFFFFFF").

###Buildings
Building are the smallest units of a DataCity. They directly represent the data.
Data is display in 4 ways:
1. **"height"** - a number greater than 0 (default: 1)
2. **"width"**  - a number greater than 0 (default: 1)
3. **"depth"**  - a number greater than 0 (default: 1)
4. **"color"**  - default: light grey

Buildings can be split in floors with different labels and colors. In this case height, color and label need to be arrays.
 
###Districts
Districts represent the hierarchy among data or the rows of a table. They either contain buildings or further districts (not both!)
Districts are defined in the **"Districts"**-array. Each one needs a **"Buildings"**- or a **"Districts"**-array.
Depth and width don't need to be defined because they are calculated by depth and width of the contained objects.

###Options
A city has different render-options which are defined in the city-object.
* **"sorted"**  (boolean) - The districts and buildings may be sorted by a given order which is defined by a "sortKey"-attribute for every object. Starting with the smallest the objects are rendered from the back left to the front right.
* **"distanceBuildings"** - a number greater than 0 (default: 5) that defines the distance between the single buildings
* **"distanceDistricts"** - a number greater than 0 (default: 10) that defines the distance between the single districts
* **"isTable"** (boolean) - A table-city additionally needs a "columns"-array with the column-labels. Rows are defined as districts and the respective district-label is used as the row-label. In this case buildings display cells. If a cell is meant to be empty you need to define a building with the parameters "depth": 0, "width": 0, "height": 0. If there are empty cells at the end of a row you can simply miss them out. 