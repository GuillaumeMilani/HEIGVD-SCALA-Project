
document.getElementById('getNewPuzzle').addEventListener('click', getNewPuzzle);

function getNewPuzzle() {
    var x = document.getElementById('boxWidth').value;
    var y = document.getElementById('boxHeight').value;
    console.log(x)
    console.log(y)
    
    if(x < 0 || y < 0 || y > 20 || y > 20){
        alert('Please enter valid dimensions for puzzle');
        return
    }
    document.getElementById('getNewPuzzle');
    console.log('Not implemented');
}
