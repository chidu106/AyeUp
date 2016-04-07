var express = require('express');
var prerender = require('prerender-node');

var app = express();
app.use(prerender);
app.use(express.static('./'));

app.listen(8080);

console.log('Listening on port 8080');
