// Hent al toej og vis det i garderoben
async function loadWardrobe() {
    const response = await fetch("/api/clothes");
    const clothes = await response.json();
    showClothes(clothes, "wardrobe");
}

// Laver et kort (billede + tekst) for hvert stykke toej
function showClothes(clothes, elementId) {
    const container = document.getElementById(elementId);
    container.innerHTML = "";
    for (const item of clothes) {
        const card = document.createElement("div");
        card.className = "item";

        const img = document.createElement("img");
        img.src = "/api/clothes/" + item.id + "/image";

        const title = document.createElement("b");
        title.textContent = item.description;

        const details = document.createElement("div");
        details.textContent = item.category + " · " + item.color;

        card.append(img, title, details);

        // Slet-knap (kun i garderoben, ikke i outfittet)
        if (elementId === "wardrobe") {
            const deleteBtn = document.createElement("button");
            deleteBtn.className = "delete";
            deleteBtn.textContent = "Slet";
            deleteBtn.onclick = async () => {
                await fetch("/api/clothes/" + item.id, {method: "DELETE"});
                loadWardrobe();
            };
            card.append(deleteBtn);
        }

        container.appendChild(card);
    }
}

// Upload et billede
document.getElementById("uploadBtn").onclick = async () => {
    const file = document.getElementById("imageInput").files[0];
    const msg = document.getElementById("uploadMsg");
    if (!file) {
        msg.textContent = "Vælg et billede først";
        return;
    }

    const formData = new FormData();
    formData.append("image", file);

    msg.textContent = "Et øjeblik …";
    const response = await fetch("/api/clothes", {method: "POST", body: formData});
    msg.textContent = response.ok ? "Gemt!" : "Noget gik galt – prøv igen";
    loadWardrobe();
};

// Find et outfit
document.getElementById("outfitBtn").onclick = async () => {
    const msg = document.getElementById("outfitMsg");
    msg.textContent = "Et øjeblik …";

    const response = await fetch("/api/outfit", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({occasion: document.getElementById("occasion").value})
    });
    if (!response.ok) {
        msg.textContent = "Noget gik galt – prøv igen";
        return;
    }

    const outfit = await response.json();
    msg.textContent = "";
    document.getElementById("reason").textContent = outfit.reason;
    showClothes(outfit.items, "outfit");
};

// Vis garderoben, naar siden aabnes
loadWardrobe();
