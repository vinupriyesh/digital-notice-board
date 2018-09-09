 
function submitForm() {
  var errorNode = document.getElementById("error");
  var resultNode = document.getElementById("result");
  errorNode.innerHTML = "";
  resultNode.innerHTML = "";  
  if(!validate(errorNode)) {
	  return;
  }      
  
  var files = document.getElementById('image').files;
  if (files.length > 0) {
    doBase64Post(files[0]);
  } 
}

function validate(errorNode) {
	var name = document.getElementById("name").value;
	if(name=="") {
		  errorNode.innerHTML += "Please enter your name";
		  return false;
	}
	var files = document.getElementById('image').files;
	if(files.length==0) {
		errorNode.innerHTML += "Please provide image file";
		  return false;
	}
	return true;
}

function doPost(name, desc, file) {
	var data = {};
	data.name = name;
	data.desc = desc;
	data.file = file;
	
	var json = JSON.stringify(data);
	var url = "/backend-1.0/rest/board/upload";
	var xhr = new XMLHttpRequest(); 
	
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-Type", "application/json");
	xhr.onload = function () {
		if (xhr.readyState == 4 && xhr.status == "200") {
		var resObj = JSON.parse(xhr.response);
		    if(resObj.status == "SUCCESS")
			    document.getElementById("result").innerHTML = "Image uploaded successfully with ref no.: "+resObj.message;
			else
			    document.getElementById("error").innerHTML = "Image uploaded failed, "+resObj.message;
		} else {
			document.getElementById("error").innerHTML = "Image upload failed"
		}
	}
	xhr.send(json);
}

function doBase64Post(file) {
   var reader = new FileReader();
   reader.readAsDataURL(file);
   reader.onload = function () {
     doPost(document.getElementById("name").value,
    		document.getElementById("desc").value,
    		reader.result);
   };
   reader.onerror = function (error) {
     console.log('Error: ', error);
   };
}