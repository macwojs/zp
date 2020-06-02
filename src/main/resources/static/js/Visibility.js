let old = null;

function hideVoting(name = "to_hide",butt_name="hide_button",info1="Pokaż",info2="ukryj") {
    let list = document.getElementsByClassName(name);
    let button = document.getElementById(butt_name);
    if (list.length>0) {
        if (old === null){
            old = list[0].style.display;
        }
        if (list[0].style.display!=="none") {
            for (let i = 0; i < list.length; i++) {
                list[i].style.display = "none";
            }
            button.innerText = info1;
        } else {
            for (let i = 0; i < list.length; i++) {
                list[i].style.display = old;
            }
            button.innerText = info2;
        }
    }

}
