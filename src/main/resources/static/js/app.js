document.addEventListener("DOMContentLoaded", () => {
    ui('theme', '#5C9B00');
    const mode = window.matchMedia('(prefers-color-scheme: dark)');
    ui('mode', mode.matches ? 'dark' : 'light');
});

function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const icon = document.getElementById('sidebar-toggle-icon');
    if (!sidebar) return;
    sidebar.classList.toggle('max');
    if (icon) {
        icon.textContent = sidebar.classList.contains('max') ? 'menu_open' : 'menu';
    }
}
