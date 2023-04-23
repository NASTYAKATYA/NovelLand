let optionsDiv = document.getElementById('options');
let addMoreOption = document.getElementById('addMoreOption');

/*
    <div th:each="option : ${novelNode.getOptions()}">
      <div th:id="old${option.getId()}">Убрать выбор</div>
      <input type="hidden" name="optionIds[]" th:value="${option.getId()}" required>
      <input th:id="old${option.getId()}" type="text" name="optionValues[]" th:value="${option.getValue()}" required>
    </div>

* */


addMoreOption.addEventListener('click', function (event) {
    let optionDiv = document.createElement('div');
    let deleteDiv = document.createElement('div');
    let input = document.createElement('input');
    let newId = `new${makeId(5)}`;

    optionDiv.id = newId;
    deleteDiv.onclick = removeOption;
    deleteDiv.style.cssText = 'cursor: pointer;';
    deleteDiv.textContent = 'Убрать выбор'
    input.type = 'text';
    input.name='optionValues[]';
    input.value='';
    input.required = true;
    optionDiv.appendChild(deleteDiv);
    optionDiv.appendChild(input);
    optionsDiv.appendChild(optionDiv);
});

function removeOption(event) {
    let optionDiv = event.target.parentNode;
    if (optionsDiv.children.length > 2) {
        optionsDiv.removeChild(optionDiv);
    }
}


function makeId(length) {
    let result = '';
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const charactersLength = characters.length;
    let counter = 0;
    while (counter < length) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
        counter += 1;
    }
    return result;
}