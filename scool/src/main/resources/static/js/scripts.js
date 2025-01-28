// static/js/scripts.js
document.addEventListener('DOMContentLoaded', function () {
    const themeToggle = document.getElementById('theme-toggle');
    const body = document.body;

    // Check for saved theme in localStorage
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme) {
        body.classList.add(savedTheme);
    }

    // Toggle theme
    themeToggle.addEventListener('click', function () {
        body.classList.toggle('dark');
        const isDarkMode = body.classList.contains('dark');
        localStorage.setItem('theme', isDarkMode ? 'dark' : 'light');
    });
});