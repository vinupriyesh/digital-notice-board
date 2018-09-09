var token;
function postLoad() {
	token = sessionStorage.getItem('token');
	if(token) {
		document.getElementById("login").style.display = "none";
		getDetails();
	} else {
		document.getElementById("table_area").style.display = "none";
	}
}
function fnDelete(fileId) {
	modify("delete",fileId);
}
function fnAuthorize(fileId) {
	modify("authorize",fileId);
}
function modify(action,fileId){
	var data = {}
	data.fileId = fileId;
	
	var json = JSON.stringify(data);
	var url = "/backend-1.0/rest/board/"+action;
	var xhr = new XMLHttpRequest(); 
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-Type", "application/json");
	xhr.setRequestHeader("token", token);
		xhr.onload = function () {
		if (xhr.readyState == 4 && xhr.status == "200") {
		var resObj = JSON.parse(xhr.response);
			if(resObj.status == "SUCCESS") {
				postLoad();
			}
		} else {
			console.log(xhr.status);
		}
	}
	xhr.send(json);
}
function doLogin() {
	var data = {}
	data.username = document.getElementById("username").value;
	data.password = document.getElementById("password").value;
	
	var json = JSON.stringify(data);
	var url = "/backend-1.0/rest/board/login";
	var xhr = new XMLHttpRequest(); 
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-Type", "application/json");
	xhr.onload = function () {
		if (xhr.readyState == 4 && xhr.status == "200") {
		var resObj = JSON.parse(xhr.response);
			if(resObj.status == "SUCCESS") {
				token = resObj.token;
				sessionStorage.setItem('token', token);
				postLoad();
			}
		} else {
			console.log(xhr.status);
		}
	}
	xhr.send(json);
}
function getDetails() {
	var url = "/backend-1.0/rest/board/details";
	var xhr = new XMLHttpRequest(); 
	xhr.open("GET", url, true);
	xhr.setRequestHeader("token", token);
	xhr.onload = function () {
		if (xhr.readyState == 4 && xhr.status == "200") {
		var resObj = JSON.parse(xhr.response);
			console.log(xhr.response);
			createTable(resObj);
		} else {
			console.log(xhr.status);
		}
	}
	xhr.send();
}
function createTable(data) {
	var table = document.getElementById("table");
	var content = "<tr><td><b>Image</b></td><td><b>Name</b></td><td><b>Description</b></td></tr>";
	for(i = 0;i<data.length;i++) {
		content += "<tr>";
		content += "<td>";
		content += "<a target='_blank' href='\images\\"+data[i].file+"."+data[i].extension+"'>";
		content += "<img src='\images\\"+data[i].file+"."+data[i].extension+"'></td>";
		content += "</a></td>";
		content +="<td>"+data[i].name+"</td>";
		content +="<td>"+data[i].desc+"</td>";
		content += "<td>";
		if(!data[i].authorized) {
			content += "<button onclick=\"fnAuthorize('"+data[i].file+"')\">Authorize</button>"
		}
		content += "</td>";
		content += "<td><button onclick=\"fnDelete('"+data[i].file+"')\">Delete</button></td>";
		content += "</tr>";
	}
	table.innerHTML = content;
}
	