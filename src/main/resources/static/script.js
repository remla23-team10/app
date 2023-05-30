var buttonCorrect;
var buttonIncorrect;
var currentSentiment;
var currentReview;

function appendAlert(message, type) {
    const alertPlaceholder = document.getElementById('liveAlertPlaceholder')
    const wrapper = document.createElement('div')
    wrapper.innerHTML = [
        `<div class="alert alert-${type} alert-dismissible" role="alert">`,
        `   <div>${message}</div>`,
        '   <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>',
        '</div>'
    ].join('')

    alertPlaceholder.append(wrapper)
}

function getSentiment(review){
    if (review == undefined || review === ""){
        buttonCorrect.disabled = true;
        buttonIncorrect.disabled = true;
        return;
    }

    buttonCorrect.disabled = false;
    buttonIncorrect.disabled = false;

    const req = new XMLHttpRequest();
    req.responseType = "json"
    const url="http://" + window.location.host + "/sentiment";
    req.open("POST", url);
    req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    req.send(JSON.stringify({review: review}));

    req.onload = (e) => {
        let response = req.response;
        let display = "";
        if(response != undefined && response.sentiment != undefined){
            if(response.sentiment == "positive"){
                display = "Positive &#128516";
            }else if(response.sentiment == "negative") {
                display = "Negative &#128542";
            }else{
                display = "There was a problem &#128533";
            }
            currentSentiment = response.sentiment;
        }

        currentReview = review;
        document.getElementById("result").innerHTML = display
    }
}

function submitFeedback(correct) {
    const req = new XMLHttpRequest();
    req.responseType = "json"
    const url = "http://" + window.location.host + "/sentiment/feedback";
    req.open("POST", url);
    req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    req.send(JSON.stringify({
        review: currentReview,
        sentiment: currentSentiment,
        correct: correct
    }));

    req.onload = (e) => {
        if (req.status == 201) {
            appendAlert("Thank you for your feedback", "success");
        }else {
            appendAlert("Something went wrong", "danger");
        }
    }
}

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}


document.addEventListener("DOMContentLoaded", () => {

    var ip=window.location.host;
    $("#history-link").attr("href", "http://"+ip+"/history");

    let versionText = getCookie("ce-version") === "v1" ? "Experimental": "Stable";
    $("#versionText").text(versionText);

    document.getElementById("review").addEventListener("keyup",(e) => getSentiment(e.target.value));
    buttonCorrect = document.getElementById("correct");
    buttonIncorrect = document.getElementById("incorrect");
    buttonCorrect.addEventListener("click", () => submitFeedback(true));
    buttonIncorrect.addEventListener("click", () => submitFeedback(false));
});