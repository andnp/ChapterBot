<?php

$callback = file_get_contents("php://input");
$json = json_decode($callback, true);

$text = $json['text'];
$name = $json['name'];

$data = $name . ": " . $text;

$sock = socket_create(AF_INET, SOCK_STREAM, 0);
socket_connect($sock, "127.0.0.1", 2000);
socket_send($sock, $data, strlen($data), 0);
socket_close($socket);
?>