<div id="fileBrowser" class="baseFileBrowser">
    <div class="row">
        <form action="FileUploadServlet" enctype="multipart/form-data" method="post" >
            <input name="uploadKB" type="file" id="origKBUploadField" onchange="writeToFakeField('origKBUploadField');"/>
            <input name="saveKB" type="submit" value="Upload File" id="origKBUploadButton" />
        </form>
        <input type="text" name="docfilename" id="docfilename" value="$kbuploadfieldlabel$" />
        <input type="button" name="docfilebutton" id="docfilebutton" class="button" value="Hochladen" /> 
        

    </div>

</div>