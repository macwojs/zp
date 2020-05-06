CandidateNum=1;


function addElem(){
    const Div = document.createElement('div');
    Div.className = 'form-group';
    Div.id = 'Candidate' + ++CandidateNum;
    const input = document.createElement('input');
    input.className='form-control'
    input.name = 'optionName' + CandidateNum;
    Div.appendChild(input);
    const a = document.createElement("a");
    a.href='#';
    const para = CandidateNum
    a.onclick=function() {deleteElem(para)};
    const img = document.createElement("img");
    img.src="/img/close.png"
    img.height=14;
    img.width=14;
    a.appendChild(img);
    Div.appendChild(a);
    document.getElementById('toAdd').appendChild(Div);
}

function deleteElem(number){
    const line = document.getElementById("Candidate" + number);
    document.getElementById('toAdd').removeChild(line);
    line.remove();
}