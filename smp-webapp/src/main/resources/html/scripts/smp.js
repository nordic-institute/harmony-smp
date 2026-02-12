(function () {
  "use strict";

  function setButtonState(button, isCollapsed) {
    button.setAttribute("aria-expanded", String(!isCollapsed));
  }

  function toggleToolbar(toolbar, content, button) {
    toolbar.classList.toggle("tb-collapsed");
    content.classList.toggle("tb-collapsed");
    setButtonState(button, toolbar.classList.contains("tb-collapsed"));
  }

  function initToolbarToggle() {
    const toolbar = document.getElementById("toolbar");
    const content = document.getElementById("content");
    const button = document.getElementById("toolbarToggleButton");

    if (!toolbar || !content || !button) {
      return;
    }

    setButtonState(button, toolbar.classList.contains("tb-collapsed"));
    button.addEventListener("click", function () {
      toggleToolbar(toolbar, content, button);
    });
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initToolbarToggle);
  } else {
    initToolbarToggle();
  }
})();
