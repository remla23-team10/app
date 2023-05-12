function getSentiment(review){
    const req = new XMLHttpRequest();
    req.responseType = "json"
    const url="http://" + window.location.host + "/sentiment";
    req.open("POST", url);
    req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    req.send(JSON.stringify({review: review}));

    req.onload = (e) => {
        let response = req.response;
        console.log(response.sentiment)
        let display = "";
        if(response != undefined && response.sentiment != undefined){
            if(response.sentiment == "positive"){
                display = "&#128516";
            }else if(response.sentiment == "negative") {
                display = "&#128542";
            }else{
                display = "&#128533";
            }
        }

        document.getElementById("result").innerHTML = display
    }
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("review").addEventListener("keyup",(e) => getSentiment(e.target.value));
});