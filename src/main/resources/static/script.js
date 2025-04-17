

function openForm() {
	document.getElementById("myForm").style.display = "block";
}

function openModal() {

	document.getElementById('add_modal').style.display = 'flex';
}

function openisbnModal() {

	document.getElementById('isbn_modal').style.display = 'flex';
}

function openUpdateModal(bookId) {

	fetch(`/book/get/${bookId}`)
		.then(response => {
			if (!response.ok) {
				throw new Error('Erreur lors du chargement du livre');
			}
			return response.json();
		})
		.then(book => {
			document.getElementById('bookForm').action = `/book/edit/${book.id}`;
			document.getElementById('serialNumber').textContent = book.serialNumber;
			document.getElementById('title').value = book.title;
			document.getElementById('description').value = book.description;
			document.getElementById('numberOfCopies').value = book.numberOfCopies;
			document.getElementById('author').value = book.authorName;
			document.getElementById('genre').value = book.genreName;
			//document.getElementById('thumbnailPath').value = book.thumbnailPath;


			if (book.thumbnailPath) {
				document.getElementById('update-img').src = book.thumbnailPath;
			}
			document.getElementById('update_modal').style.display = 'flex';
		})
		.catch(error => {
			alert(error.message);
		});
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

function readURL(input, imgId) {
	if (input.files && input.files[0]) {
		const reader = new FileReader();
		reader.onload = function (e) {
			document.getElementById(imgId).src = e.target.result;
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

// Optional: Attach click listeners on page load
document.addEventListener("DOMContentLoaded", function () {
	const buttons = document.querySelectorAll(".print-qrs-btn");

	buttons.forEach(button => {
		button.addEventListener("click", function () {
			const serialNumber = this.getAttribute("data-serial");
			const title = this.getAttribute("data-title");
			const numberOfCopies = parseInt(this.getAttribute("data-copies"));
			printQRCodes(serialNumber, title, numberOfCopies);
		});
	});
});

document.addEventListener("DOMContentLoaded", () => {
	document.querySelectorAll(".print-qrs-btn").forEach(button => {
		button.addEventListener("click", () => {
			const serial = button.getAttribute("data-serial");
			const title = button.getAttribute("data-title");
			const copies = parseInt(button.getAttribute("data-copies"));
			printQRCodes(serial, title, copies);
		});
	});
});



// script.js
window.printQRCodes = function (serialNumber, title, numberOfCopies) {
	const printWindow = window.open('', '_blank');
	printWindow.document.write('<html><head><title>QR Codes</title></head><body>');
	printWindow.document.write(`<h2>${title}</h2>`);

	for (let i = 0; i < numberOfCopies; i++) {
		printWindow.document.write(`
		    <div style="border: 2px dotted black; padding: 10px; display: inline-block; text-align: center; margin: 10px;">
		        <img src="/book/qr/${serialNumber}" style="width: 100px; height: 100px; display: block; margin: 0 auto;">
		        <span style="display: block; margin-top: 8px;">${serialNumber}</span>
		    </div>
		`);
	}

	printWindow.document.write('</body></html>');
	printWindow.document.close();
	printWindow.focus();

	printWindow.onload = function () {
		printWindow.print();
		printWindow.close();
	};
};

function handleUpdateClick(button) {
	const id = button.getAttribute('data-id');
	const registerNumber = button.getAttribute('data-register-number');
	const name = button.getAttribute('data-name');
	const firstName = button.getAttribute('data-first-name');
	const gender = button.getAttribute('data-gender');
	const phone = button.getAttribute('data-phone');
	const dob = button.getAttribute('data-dob');
	const status = button.getAttribute('data-status');

	// Fill the modal fields
	document.getElementById("modalMemberId").textContent = registerNumber;
	document.getElementById("updateName").value = name;
	document.getElementById("updateFirstName").value = firstName;
	document.getElementById("updateGender").value = gender;
	document.getElementById("updatePhone").value = phone;
	document.getElementById("updateDob").value = dob;
	document.getElementById("updateStatus").value = status;

	// Set the form action
	document.getElementById("updateForm").action = "/member/update/" + id;

	// Show modal
	document.getElementById("update_modal").style.display = "block";
}

function openUpdateModal(id, matricule, name, firstName, gender, phone, dob) {
	// Set field values
	document.getElementById('updateId').value = id;
	document.getElementById('updateMatricule').textContent = matricule;
	document.getElementById('updateName').value = name;
	document.getElementById('updateFirstName').value = firstName;
	document.getElementById('updateGender').value = gender;
	document.getElementById('updatePhone').value = phone;
	document.getElementById('updateDateOfBirth').value = dob;


	// Show modal
	document.getElementById('update_modal').style.display = 'flex';
}

function closeUpdateMember() {
	document.getElementById('update_modal').style.display = 'none';
}


function openPrintModal(fullName, phoneNumber, dateOfBirth, registerNumber) {
	document.getElementById('cardFullName').textContent = fullName;
	document.getElementById('cardPhone').textContent = phoneNumber;
	document.getElementById('cardDOB').textContent = formatDate(dateOfBirth);
	document.getElementById('cardRegisterNumber').textContent = registerNumber;
	document.getElementById('qrCodeImage').src = `/member/qrcode/${registerNumber}`;

	document.getElementById('print_card_modal').style.display = 'block';
}

function closePrintModal() {
	document.getElementById('print_card_modal').style.display = 'none';
}

function printMemberCard() {
	let printContents = document.getElementById('member_card_content').innerHTML;
	let printWindow = window.open('', '', 'height=600,width=800');
	printWindow.document.write('<html><head><title>Carte de Membre</title></head><body>');
	printWindow.document.write(printContents);
	printWindow.document.write('</body></html>');
	printWindow.document.close();
	printWindow.print();
}

function formatDate(inputDate) {
	const date = new Date(inputDate);
	const day = ('0' + date.getDate()).slice(-2);
	const month = ('0' + (date.getMonth() + 1)).slice(-2);
	const year = date.getFullYear();
	return `${day}-${month}-${year}`;
}

setTimeout(() => {
	const success = document.getElementById("successMsg");
	const error = document.getElementById("errorMsg");

	if (success) success.style.display = "none";
	if (error) error.style.display = "none";
}, 2000);


setTimeout(() => {
	document.querySelectorAll(".message").forEach(div => div.style.display = 'none');
}, 2000);

function fetchBookPreview(input, previewId) {
	const serial = input.value.trim();
	const previewBox = document.getElementById(previewId);

	if (serial === "") {
		previewBox.innerHTML = "";
		return;
	}

	fetch(`/book/preview/${serial}`)
		.then(res => {
			if (!res.ok) throw new Error("Book not found");
			return res.json();
		})
		.then(book => {
			previewBox.innerHTML = `
				                     <img src="${book.thumbnailPath}" style="width: 100px;">
				                     <p>${book.title} (${book.serialNumber})</p>
				                 `;
		})
		.catch(() => {
			previewBox.innerHTML = `<p style="color: red;">Livre introuvable</p>`;
		});
}

function openUpdateUser(id) {
	// Fetch the user data from a hidden input or a global JS map (preloaded with Thymeleaf)
	const user = userMap[id]; // userMap should be a global JS object
	document.getElementById("updateUserId").value = user.idPerson;
	document.getElementById("updateUserName").value = user.namePerson;
	// ... Set other fields
	document.getElementById("update_modal").style.display = "block";
  }


  function openModal() {
        document.getElementById('add_modal').style.display = 'block';
    }

    function closeModal() {
        document.getElementById('add_modal').style.display = 'none';
    }

    function openUpdateUser(id) {
        fetch(`/user/edit/${id}`)
            .then(response => response.json())
            .then(user => {
				document.getElementById("edit_id").value = user.idPerson;
                document.getElementById("edit_username").textContent = user.username;
                document.getElementById('edit_name').value = user.namePerson;
                document.getElementById('edit_firstname').value = user.firstNamePerson;
                document.getElementById('edit_role').value = user.role;
                document.getElementById('edit_email').value = user.email;
                document.getElementById('edit_phone').value = user.phoneNumber;
                document.getElementById('update_modal').style.display = 'flex';
            })
            .catch(error => console.error('Erreur de chargement utilisateur:', error));
    }

    function closeUpdateUser() {
        document.getElementById('update_modal').style.display = 'none';
    }

/////
function openCreateModal() {
	document.getElementById('add_modal').style.display = 'flex';
}

function closeCreateModal() {
	document.getElementById('add_modal').style.display = 'none';
}

function openUpdateUser(id) {
	fetch(`/user/edit/${id}`)
		.then(response => response.json())
		.then(user => {
			document.getElementById('edit_id').value = user.idPerson;
			document.getElementById('edit_name').value = user.namePerson;
			document.getElementById('edit_firstname').value = user.firstNamePerson;
			document.getElementById('edit_role').value = user.role;
			document.getElementById('edit_email').value = user.email;
			document.getElementById('edit_phone').value = user.phoneNumber;
			document.getElementById('update_modal').style.display = 'flex';
		})
		.catch(error => console.error('Erreur de chargement utilisateur:', error));
}

function closeUpdateModal() {
	document.getElementById('update_modal').style.display = 'none';
}







