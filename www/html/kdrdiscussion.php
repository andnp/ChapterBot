<?php
require "ToJava.php";

$callback = file_get_contents("php://input");
$json = json_decode($callback, true);

$text = $json['text'];
$name = $json['name'];

$data = $name . ": " . $text;

sendDataToJava($data, 2001);
?>