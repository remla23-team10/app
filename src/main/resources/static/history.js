function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}


$(document).ready(function () {
    var ip = window.location.host;
    $("#home-link").attr("href", "http://" + ip + "/");

    let versionText = getCookie("ce-version") === "v1" ? "Experimental": "Stable";
    $("#versionText").text(versionText);
    
    $('#history').DataTable({
        ajax: {
            url: 'sentiment/history',
            dataSrc: ''
        },
        columns: [
            { data: 'review' },
            {
                data: 'sentiment',
                render: function (data, type, row) {
                    if (data == "positive") {
                        return '<span class="badge text-bg-success"> Positive &#128516 </span>';
                    } else {
                        return '<span class="badge text-bg-danger"> Negative &#128542 </span>';
                    }
                }
            },
            {
                data: 'correct',
                render: function (data, type, row) {
                    if (data == true) {
                        return '<span class="badge text-bg-success"> Correct prediction </span>';
                    } else {
                        return '<span class="badge text-bg-danger"> Incorrect prediction </span>';
                    }
                }
            }
        ],

    });
});