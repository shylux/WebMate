WebMatePort = 3000;

function pushWebMateData(par) {
	jQuery.ajax({
		url: 'http://localhost:'+WebMatePort+'/',
		type: 'GET',
		dataType: 'jsonp',
		data: {webmatedata: par}
	});
}
