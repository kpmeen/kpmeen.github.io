if (document.referrer.match(/^https:\/\/www\.youtube\.com/)){
  hide("theater_overlayfull");
}else{
  show("theater_overlayfull");
}

const node = document.getElementById("path");
node.addEventListener("keyup", function(event) {
  if (event.key === "Enter") {
    //show("loader");
    location.href = node.value;
  }
});

function goFullScreen() {
  if (document.cookie == 'noshow'){
    goFullScreenYoutube();
  } else {
    show("theater_overlayfullscreen");
    setTimeout(() => { goFullScreenYoutube() }, 5000);
  }
}

function goFullScreenYoutube() {
    location.href='https://www.youtube.com/redirect?q=https%3A%2F%2Fscalytica.net%2F%23theater';
    setTimeout(() => { hide("theater_overlayfullscreen") }, 5000);
}
function showLoader() {
  show("theater_loader");
  setTimeout(() => { hide("theater_loader") }, 5000);
}
function showOverlay() {
  show("theater_overlay");
  node.focus();
  node.setSelectionRange(200, 200);
}
function hide(e){
  document.getElementById(e).style.display = "none";
}
function show(e){
  document.getElementById(e).style.display = "block";
}