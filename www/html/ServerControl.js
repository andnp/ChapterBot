$(document).ready(function(){
 $('#offbutton').on('click', function(){
    $.post('power.php', {power: "0"});
    setTimeout(function(){updateStatus();}, 1000);
 });
});

$(document).ready(function(){
 $('#onbutton').on('click', function(){
    $.post('power.php', {power: "1"});
    setTimeout(function(){updateStatus();}, 1000);
 });
});

$(document).ready(
function(){
 setInterval(function(){updateStatus();}, 2000);
});

function updateStatus(){
 var ajaxRequest;  // The variable that makes Ajax possible!
	
 try{
  // Opera 8.0+, Firefox, Safari
  ajaxRequest = new XMLHttpRequest();
 } catch (e){
  // Internet Explorer Browsers
  try{
   ajaxRequest = new ActiveXObject("Msxml2.XMLHTTP");
  } catch (e) {
   try{
    ajaxRequest = new ActiveXObject("Microsoft.XMLHTTP");
   } catch (e){
    // Something went wrong
    alert("Your browser broke!");
    return false;
   }
  }
 }
 // Create a function that will receive data sent from the server
 ajaxRequest.onreadystatechange = function(){
  if(ajaxRequest.readyState == 4){
   document.getElementById("FilterStatus").innerHTML = ajaxRequest.responseText;
  }
 }
 ajaxRequest.open("GET", "FilterStatus.php", true);
 ajaxRequest.send(); 
}
