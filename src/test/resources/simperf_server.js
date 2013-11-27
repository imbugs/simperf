var net = require('net');
var emitLines = require('../utils/emitLines');

port = 20122;

var server = net.createServer(function(socket) {
	var remoteAddress = socket.remoteAddress;
	var remotePort = socket.remotePort;

	console.log('[Simperf] connected : ' + remoteAddress +':'+ remotePort);
	socket.write("{ cmd: 'message', param: '=== Simperf Server Welcome! ==='}\n");
	// param为空时是查询session信息, param不为空则设置session
	socket.write("{ cmd: 'session', param: ''}\n");

	socket.on('line', function (line) {
		var request = JSON.parse(line);
		if (request.type === 'session') {
			// session info
			console.log('[Simperf] client session ' + request.data);
		} else if (request.type === 'result') {
			console.log(request.data);
		};
	});

	emitLines(socket)

	socket.on('end', function() {
		console.log('[Simperf] close : ' + remoteAddress +':'+ remotePort);
	});

	socket.on('error', function(err) {
		console.log('[Simperf] error : ' + err);
	});

	setTimeout(function(){
		socket.write('{cmd: "start", param: ""}\n');
	}, 3000);
	setTimeout(function(){
		socket.write('{cmd: "stop", param: ""}\n');
	}, 10000);
});

server.on('listening', function() {
	console.log('[Simperf] Server listening on ' + server.address().address +':'+server.address().port);
});

server.listen(port);
