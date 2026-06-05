document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll("[data-tabs]").forEach((tabs) => {
    const buttons = tabs.querySelectorAll("[role=tab]");
    const panels = tabs.querySelectorAll("[role=tabpanel]");

    buttons.forEach((button) => {
      button.addEventListener("click", () => {
        const target = button.getAttribute("data-tab");

        buttons.forEach((b) => b.setAttribute("aria-selected", "false"));
        panels.forEach((p) => p.setAttribute("aria-hidden", "true"));

        button.setAttribute("aria-selected", "true");
        const panel = tabs.querySelector(`#tab-${target}`);
        if (panel) {
          panel.setAttribute("aria-hidden", "false");
        }
      });
    });
  });
});
