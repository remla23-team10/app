$(document).ready(function () {
    var ip = window.location.host;
    $("#home-link").attr("href", "http://" + ip + "/");

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