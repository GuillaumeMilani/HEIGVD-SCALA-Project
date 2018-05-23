
document.getElementById('getNewPuzzle').addEventListener('click', getNewPuzzle);



/**
 * Javascript array shuffle function, source:
 * https://stackoverflow.com/questions/2450954/how-to-randomize-shuffle-a-javascript-array
 * Randomize array element order in-place.
 * Using Durstenfeld shuffle algorithm.
 */
function shuffleArray(array) {
    for (var i = array.length - 1; i > 0; i--) {
        var j = Math.floor(Math.random() * (i + 1));
        var temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}

function getNewPuzzle() {
    var y = document.getElementById('boxWidth').value;
    var x = document.getElementById('boxHeight').value;
    console.log(x)
    console.log(y)
    
    if(x < 1 || y < 1 || y > 20 || y > 20){
        alert('Please enter valid dimensions for puzzle');
        return
    }

    var image_id_array = []
    for (var i = 0; i < x*y; ++i){
        image_id_array.push(i);
    }
    shuffleArray(image_id_array);
    
    var result = '';
    for(var i = 0; i < x; ++i){
        for(var j = 0; j < y; ++j){
            result += ' <img src="images/'+ (i*x+j) +'.jpg" alt="Hello lol" class="notClicked" height="50" width="50" onclick="onImageClick(event)">'
        }
        result += '<br>'
    }

    document.getElementById('imageBox').innerHTML = result;
}

function onImageClick(e){
    console.log(e)
    var element = e.target;
    if(element.className == 'clicked'){
        element.className = 'notClicked';
    } else {
        element.className = 'clicked'
    }
}
