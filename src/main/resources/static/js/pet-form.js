function previewPetPhoto(event) {
    const [file] = event.target.files;

    if (!file) {
        return;
    }

    const avatar = document.getElementById("fotoAvatar");

    if (!avatar) {
        return;
    }

    const imageUrl = URL.createObjectURL(file);
    const image = document.createElement("img");

    image.src = imageUrl;
    image.alt = "Prévia da foto do pet";

    image.addEventListener(
        "load",
        () => URL.revokeObjectURL(imageUrl),
        { once: true }
    );

    avatar.replaceChildren(image);
}

function toggleMedicationField(value) {
    const field = document.getElementById("medicamentosField");
    const input = document.getElementById("medicamentos");

    if (!field || !input) {
        return;
    }

    const usesMedication = value === "true";

    field.hidden = !usesMedication;
    input.disabled = !usesMedication;

    if (!usesMedication) {
        input.value = "";
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const select = document.getElementById("usaMedicamento");

    if (select) {
        toggleMedicationField(select.value);
    }
});
