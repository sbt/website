document.addEventListener("DOMContentLoaded", () => {
  const toggle = document.querySelector(".navbar__toggle");
  const menu = document.getElementById("navbar-menu");
  if (!toggle || !menu) return;

  const setOpen = (open) => {
    toggle.setAttribute("aria-expanded", open ? "true" : "false");
    menu.classList.toggle("is-open", open);
    toggle.classList.toggle("is-open", open);
  };

  toggle.addEventListener("click", () => {
    const open = toggle.getAttribute("aria-expanded") !== "true";
    setOpen(open);
  });

  menu.querySelectorAll(".navbar__link").forEach((link) => {
    link.addEventListener("click", () => setOpen(false));
  });

  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") setOpen(false);
  });

  window.matchMedia("(min-width: 997px)").addEventListener("change", (event) => {
    if (event.matches) setOpen(false);
  });
});
