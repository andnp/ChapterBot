<?php
require "ToJava.php";

//power = 0 means off, power = 1 means on
$power = $_REQUEST["power"];
sendDataToJava($power, 1999);
print_r(error_get_last());
?>