WebMatePort = 3000;

function pushWebMateData(data, callback) {
  if (typeof(callback) !== "function") callback = function(data) {};
	jQuery.ajax({
		url: 'http://localhost:'+WebMatePort+'/',
		type: 'GET',
		dataType: 'jsonp',
		data: {webmatedata: data}
	}).done(callback(data));
}
