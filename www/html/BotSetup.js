$(document).ready(function(){
 $('#offbutton').on('click', function(){
    $.post('power.php', {power: "0"});
 });
});

$(document).ready(function(){
 $('#onbutton').on('click', function(){
    $.post('power.php', {power: "1"});
 });
});
