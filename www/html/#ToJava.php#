<?php
function sendDataToJava($data, $port){
 $sock = socket_create(AF_INET, SOCK_STREAM, 0);
 socket_connect($sock, "127.0.0.1", $port);
 socket_send($sock, $data, strlen($data), 0);
 socket_close($sock);
}
?>