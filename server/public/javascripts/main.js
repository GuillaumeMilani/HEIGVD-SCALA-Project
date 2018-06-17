/**
 * Javascript array shuffle function, source:
 * https://stackoverflow.com/questions/2450954/how-to-randomize-shuffle-a-javascript-array
 * Shuffles array in place.
 * @param {Array} a items An array containing the items.
 */
function shuffle(a) {
    var j, x, i;
    for (i = a.length - 1; i > 0; i--) {
        j = Math.floor(Math.random() * (i + 1));
        x = a[i];
        a[i] = a[j];
        a[j] = x;
    }
    return a;
}

function getNewPuzzle() {
    var y = document.getElementById('boxWidth').value;
    var x = document.getElementById('boxHeight').value;
    console.log(x)
    console.log(y)

    if (x < 1 || y < 1 || y > 20 || y > 20) {
        alert('Please enter valid dimensions for puzzle');
        return
    }

    var image_id_array = []
    for (var i = 0; i < x * y; ++i) {
        image_id_array.push(i);
    }
    shuffle(image_id_array);

    var result = '';
    for (var i = 0; i < x; ++i) {
        for (var j = 0; j < y; ++j) {
            result += ' <img src="images/' + image_id_array[i * y + j] + '.jpg" alt="Hello lol" class="notClicked" height="50" width="50" onclick="onImageClick(event)">'
        }
        result += '<br>'
    }

    document.getElementById('imageBox').innerHTML = result;
}

function onImageClick(e) {
    console.log(e)
    var element = e.target;
    if (element.className == 'clicked') {
        element.className = 'notClicked';
    } else {
        element.className = 'clicked'
    }
}

function postPuzzle(keyword) {
    var all = document.getElementsByTagName("img");
    clicked = [];
    notClicked = [];
    for (var i = 0, max = all.length; i < max; i++) {
        if (all[i].id !== "") {
            if (all[i].className === 'clicked') {
                clicked.push(all[i].id)
            } else {
                notClicked.push(all[i].id)
            }
        }
    }

    const router = jsRoutes.controllers.HomeController.scorePuzzle();
    console.log("SENDING STUFF");
    console.log("Keyword is " + typeof parseInt($('#keywordId').val()));

    $.ajax({
        method: router.method,
        url: router.url,
        contentType: 'application/json',
        data: JSON.stringify({
            clicked: clicked,
            notClicked: notClicked,
            keyword: parseInt($('#keywordId').val())
        })
    }).done(function (response) {
        $("body").replaceWith(response)
    })
}