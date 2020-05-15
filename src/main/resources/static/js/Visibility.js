let state = true;

function hideVoting() {
    let list = document.getElementsByClassName("closed-voting");
    let button = document.getElementById("hide_button");
    if (state) {
        for (let i = 0; i < list.length; i++) {
            list[i].style.display = "none";
        }
        button.innerText = "Pokaż nieaktywne glosowania";
    }
    else {
        for (let i = 0; i < list.length; i++) {
            list[i].style.display = "block";
        }
        button.innerText = "Ukryj nieaktywne glosowania";
    }
    state = !state;
}
