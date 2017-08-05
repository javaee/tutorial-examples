function open_win(url_add)
{
    window.open(url_add,'orderDetail','width=300,height=200,menubar=yes,status=no,location=no,toolbar=no,scrollbars=yes,resize=no');
   
}

var _validFileExtensions = [".jpg", ".jpeg", ".bmp", ".gif", ".png"];

function validateUpload(oForm) {
    var arrInputs = oForm.getElementsByTagName("input");
    for (var i = 0; i < arrInputs.length; i++) {
        var oInput = arrInputs[i];
        if (oInput.type == "file") {
            var sFileName = oInput.value;
            if (sFileName.length > 0) {
                var blnValid = false;
                for (var j = 0; j < _validFileExtensions.length; j++) {
                    var sCurExtension = _validFileExtensions[j];
                    if (sFileName.substr(sFileName.length - sCurExtension.length, sCurExtension.length).toLowerCase() == sCurExtension.toLowerCase()) {
                        blnValid = true;
                        break;
                    } 
                } 
            
                if (!blnValid) {
                    alert("Sorry, " + sFileName + " is invalid, allowed extensions are: " + _validFileExtensions.join(", "));
                    return false;
                }
            } else if (confirm("Do want to proceed without an image ?")) {
                return true;
            } else {
                return false;
            }
        }
    }

    return true;
}