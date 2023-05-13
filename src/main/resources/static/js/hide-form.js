  function openForm() {
    document.getElementById("myForm").style.display = "flex";
    document.getElementById("myClose").style.display = "flex";
    document.getElementById("myOpen").style.display = "none";
  }

  function closeForm() {
    document.getElementById("myForm").style.display = "none";
    document.getElementById("myClose").style.display = "none";
    document.getElementById("myOpen").style.display = "flex";
  }