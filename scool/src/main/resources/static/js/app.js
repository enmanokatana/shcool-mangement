document.getElementById("login-button").addEventListener("click", function () {
    alert("Login clicked!");
    this.classList.add("hidden");
    document.getElementById("logout-button").classList.remove("hidden");
});

document.getElementById("logout-button").addEventListener("click", function () {
    alert("Logout clicked!");
    this.classList.add("hidden");
    document.getElementById("login-button").classList.remove("hidden");
});
