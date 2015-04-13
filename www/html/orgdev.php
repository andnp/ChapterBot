<?php
require "ToJava.php";

$data = file_get_contents("php://input");

sendDataToJava($data, 2003);
?>