let state = true;

function hideVoting(name = "to_hide",butt_name="hide_button",info1="Pokaż",info2="ukryj") {
    let list = document.getElementsByClassName(name);
    let button = document.getElementById(butt_name);
    if (state) {
        for (let i = 0; i < list.length; i++) {
            list[i].style.display = "none";
        }
        button.innerText = info1;
    }
    else {
        for (let i = 0; i < list.length; i++) {
            list[i].style.display = "block";
        }
        button.innerText = info2;
    }
    state = !state;
}
