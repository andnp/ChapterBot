<?php
$json_file = file_get_contents("/home/andy/Projects/ChapterBot/botstatus.json");
$json = json_decode($json_file, true);

if($json['Filter Power'] == "true"){
 $power = "On";
} else {
 $power = "Off";
}
echo "CurseFilter status: " . $power . PHP_EOL;
echo "Discussion Kick Count: " . $json['Discussion Filter Kick Count'] . PHP_EOL;
echo "Test Kick Count: " . $json['Test Filter Kick Count'] . PHP_EOL;
print_r(error_get_last());
?>