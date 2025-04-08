

function openForm() {
  document.getElementById("myForm").style.display = "block";
}

function openModal() {
  
  document.getElementById('add_modal').style.display = 'flex';
}

function openisbnModal() {
  
  document.getElementById('isbn_modal').style.display = 'flex';
}

function openUpdateModal(id, serialNumber, title, description,thumbnailPath, numberOfCopies,genre,author) {
  
	
	
	document.getElementById('bookModalTitle').innerText = 'Modifier Livre'+serialNumber;
  document.getElementById('bookForm').action = 'book/edit/' + id;
  document.getElementById('bookId').value = id;
  document.getElementById('serialNumber').value = serialNumber;
  document.getElementById('numberOfCopies').value = numberOfCopies;
  document.getElementById('book_modal').style.display = 'flex';
}

function closeModal() {
  document.getElementById('book_modal').style.display = 'none';
}



function openDeleteModal() {

  document.getElementById('delete_modal').style.display = 'flex';
}

function openResetModal() {

  document.getElementById('reset_modal').style.display = 'flex';
}

function closeResetModal() {
  document.getElementById('reset_modal').style.display = "none";
}


function closeDeleteModal() {
  document.getElementById('delete_modal').style.display = "none";
}

function closeModal() {
  document.getElementById('add_modal').style.display = "none";
}

function closeisbnModal() {
  document.getElementById('isbn_modal').style.display = "none";
}

function closeUpdateModal() {
  document.getElementById('update_modal').style.display = "none";
}

window.addEventListener("DOMContentLoaded", () => {
        const successMessage = document.getElementById("successMessage");
        if (successMessage) {
            setTimeout(() => {
                successMessage.style.display = "none";
            }, 1500); // 1.5 seconds
        }
    });

function goToConfirmation() {
  document.getElementById('add_modal').style.display = 'none';
  document.getElementById('confirmation').style.display = 'flex';
}

function readURL(input) {
  if (input.files && input.files[0]) {

    var reader = new FileReader();
    reader.onload = function (e) {
      document.querySelector("#img").setAttribute("src", e.target.result);
    };

    reader.readAsDataURL(input.files[0]);
  }
}

function filterBooks() {
  const filter = document.getElementById('myInput').value.toUpperCase();
  const rows = myTable.getElementsByTagName('tr');

  for (const row of rows) {
    const cells = row.getElementsByTagName('td');
    const match = Array.from(cells).some(cell => cell.textContent.toUpperCase().includes(filter));
    row.style.display = match ? '' : 'none';
  }
}