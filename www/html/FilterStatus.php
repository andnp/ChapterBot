<?php
$json_file = file_get_contents("/home/andy/Projects/ChapterBot/botstatus.json");
$json = json_decode($json_file, true);

$sock = socket_create(AF_INET, SOCK_STREAM, 0);
socket_connect($sock, "127.0.0.1", 1999);
if(socket_send($sock, "ping", strlen("ping"), 0) == FALSE){
 $server_status = "Off";
} else {
 $server_status = "On";
}

if($json['Filter Power'] == "true" && $server_status == "On"){
 $curse_status = "On";
} else {
 $curse_status = "Off";
}

socket_close($sock);

echo "Server Status: " . $server_status . PHP_EOL;
echo "CurseFilter Status: " . $curse_status . PHP_EOL;
echo "Discussion Kick Count: " . $json['Discussion Filter Kick Count'] . PHP_EOL;
echo "Test Kick Count: " . $json['Test Filter Kick Count'] . PHP_EOL;
//print_r(error_get_last());
?>