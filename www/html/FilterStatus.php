<?php
$json_file = file_get_contents("/home/andy/Projects/ChapterBot/botstatus.json");
$json = json_decode($json_file, true);

if($json['Filter Power'] == "true"){
 $power = "On";
} else {
 $power = "Off";
}

$sock = socket_create(AF_INET, SOCK_STREAM, 0);
socket_connect($sock, "127.0.0.1", 1999);
//socket_send($sock, "ping", strlen("ping"), 0);
$ping_result = "";
while($client = socket_accept($sock)){
 $ping_result = socket_read($client, 1024);
}
if($ping_result == "hi"){
 $server_status = "On";
} else {
 $server_status = "Off";
}
socket_close($sock);

echo "Server Status: " . $server_status . PHP_EOL;
echo "CurseFilter Status: " . $power . PHP_EOL;
echo "Discussion Kick Count: " . $json['Discussion Filter Kick Count'] . PHP_EOL;
echo "Test Kick Count: " . $json['Test Filter Kick Count'] . PHP_EOL;
print_r(error_get_last());
?>