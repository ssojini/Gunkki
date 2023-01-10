function addFreeBoard() {
	$.ajax({
		url: "/health/addFreeBoard",
		method: "post",
		data: {
			"bname": $("#bname").val(),
			"title": $("#title").val(),
			"author": $("#author").val(),
			"contents": $("#contents").val()
		},
		dataType: "json",
		cache: false,
		success: function(res) {
			if (res.result) {
				uploadFiles(parseInt(res.fbnum));
			} else {
				alert("저장 실패");
			}
		},
		error: function(xhs, status, err) {
			alert(err);
		}
	});
}

function uploadFiles(fbnum) {
	var data = getFormData();
	data.append("fbnum",fbnum);
	if (data != null) {
	$.ajax({
		url: "/health/uploadFiles",
		method: "post",
		enctype: "multipart/form-data",
		data: data,
		cache: false,
		dataType: "json",
		processData: false,
		contentType: false,
		timeout: 600000,
		success: function(res) {
			alert(res.result?"저장 성공":"저장 실패");
		},
		error: function(xhs, status, err) {
			alert(err);
		}
	});
	}
}

function getFormData() {
	const files = $("#files")[0];
	// files라는 객체에 담긴다
	console.log("files: ", files.files);
	console.log("files.length: ", files.files.length);

	if (files.files.length === 0) {
		return null;
	}
	const formData = new FormData();
	for (var i = 0; i < files.files.length; i++) {
		formData.append("files", files.files[i]);
	}
	console.log("formData: ", formData);
	for (var key of formData.keys()) {
		console.log("key: ", key);
	}
	for (var value of formData.values()) {
		console.log("value: ", value);
	}
	return formData;
}