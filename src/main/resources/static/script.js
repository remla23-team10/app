function getSentiment(review){
    const req = new XMLHttpRequest();
    req.responseType = "json"
    const url="http://" + window.location.host + "/sentiment?review=" + review;
    req.open("POST", url);
    req.send({review: review});

    req.onload = (e) => {
        let response = req.response;
        console.log(response.sentiment)
        let display = "";
        if(response != undefined && response.sentiment != undefined){
            if(response.sentiment == "positive"){
                display = "&#128516"
            }
        }

        document.getElementById("result").innerHTML = display
    }
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("review").addEventListener("keyup",(e) => getSentiment(e.target.value));
});