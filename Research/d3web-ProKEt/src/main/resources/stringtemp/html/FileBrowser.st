<div id="fileBrowser" class="baseFileBrowser">
    <div class="row">
        <form action="FileUploadServlet" enctype="multipart/form-data" method="post" >
            <input name="uploadKB" type="file" id="origKBUploadField" onchange="writeToFakeField('origKBUploadField');"/>
            <input name="saveKB" type="submit" value="Upload File" id="origKBUploadButton" />
        </form>
        <input type="text" name="docfilename" id="docfilename" value="$kbuploadfieldlabel$" />
        <input type="button" name="docfilebutton" id="docfilebutton" class="button" value="Hochladen" /> 
        

    </div>

    <div class="row" id="specUploadRow">
        <form action="FileUploadServlet" enctype="multipart/form-data" method="post" >
            <input name="uploadSPEC" type="file" id="origSpecUploadField" onchange="writeToFakeField('origSpecUploadField');"/>
            <input name="saveSPEC" type="submit" value="$specuploadfieldlabel$" id="origSpecUploadButton" />
        </form>
        <input type="text" name=specfilename" id="specfilename" value="Choose XML Specification (.xml)" />
        <input type="button" name="specfilebutton" id="specfilebutton" class="button" value="Hochladen" /> 
 
    </div>

</div>