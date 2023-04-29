 let gCheck = document.getElementById('gCheck');
  let selectedOptionsCount = 0;
  $(function () {
    $('#genres').on('optionselected', function(e) {
      selectedOptionsCount++;
      gCheck.value = selectedOptionsCount;
      document.getElementById("error").style.display = "none";
    });
    $('#genres').on('optiondeselected', function(e) {
      selectedOptionsCount--;
      if (selectedOptionsCount == 0) {
        gCheck.value = '';
        document.getElementById("error").style.display = "block";
      }
    });
  });