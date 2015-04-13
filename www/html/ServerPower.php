#!/usr/bin/php
<?php
shell_exec("java -jar ../../ChapterBot.jar &");
print_r(error_get_last());
?>