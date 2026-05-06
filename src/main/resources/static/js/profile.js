let currentUser = null;

function safe(v, def = "") {
  return v === null || v === undefined ? def : v;
}

function escapeHtml(value) {
  return String(value)
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#039;");
}

function showTab(btn) {
  document.querySelectorAll(".tab-btn").forEach(x => x.classList.remove("active"));
  btn.classList.add("active");
}

async function loadProfile() {
  try {
      const res = await fetch("http://localhost:8080/profile", {
          method: "GET",
          credentials: "include",
          headers: {
              "X-Requested-With": "XMLHttpRequest"
          }
      });

      if (res.status === 401 || res.status === 403) {
          window.location.href = "login.html";
          return false;
      }

      if (!res.ok) {
          window.location.href = "login.html";
          return false;
      }

      const data = await res.json();
      currentUser = data;

      const fio = safe(data.fio, "Без имени");
      const mail = safe(data.mail, "-");

      document.getElementById("fio").innerText = fio;
      document.getElementById("mail").innerText = mail;
      document.getElementById("welcome-name").innerText = "Привет, " + fio;

      const avatarLetter = fio.trim().length > 0 ? fio.trim()[0].toUpperCase() : "?";
      document.getElementById("avatar").innerText = avatarLetter;

      return true;
  } catch (e) {
      window.location.href = "login.html";
      return false;
  }
}

async function loadMyComments() {
  const box = document.getElementById("my-comments");
  const sort = document.getElementById("profileSortSelect")?.value || "date_desc";

  try {
      const res = await fetch("http://localhost:8080/comments/my?sort=" + encodeURIComponent(sort), {
          method: "GET",
          credentials: "include"
      });

      if (!res.ok) {
          box.innerHTML = '<div class="empty">Не удалось загрузить комментарии</div>';
          return;
      }

      const data = await res.json();

      if (!Array.isArray(data)) {
          box.innerHTML = '<div class="empty">Неправильный формат данных</div>';
          return;
      }

      const myComments = data;

      let totalLikes = 0;
      let totalDislikes = 0;
      let gradeSum = 0;

      myComments.forEach(c => {
          totalLikes += Number(c.plusGrade || 0);
          totalDislikes += Number(c.minusGrade || 0);
          gradeSum += Number(c.grade || 0);
      });

      document.getElementById("count-comments").innerText = myComments.length;
      document.getElementById("count-likes").innerText = totalLikes;
      document.getElementById("count-dislikes").innerText = totalDislikes;
      document.getElementById("avg-grade").innerText =
          myComments.length ? (gradeSum / myComments.length).toFixed(1) : "0";

      if (myComments.length === 0) {
          box.innerHTML = '<div class="empty">У вас пока нет комментариев</div>';
          return;
      }

      box.innerHTML = myComments.map(c => {
          const title = escapeHtml(safe(c.title, "Без названия"));
          const text = escapeHtml(safe(c.comment, "Нет текста"));
          const date = escapeHtml(safe(c.postDate, "Дата не указана"));
          const grade = safe(c.grade, "-");
          const plus = safe(c.plusGrade, 0);
          const minus = safe(c.minusGrade, 0);
          const category = c.category && c.category.category
              ? escapeHtml(c.category.category)
              : "Без категории";

          return `
              <div class="comment">
                  <div class="comment-top">
                      <div>
                          <div class="comment-title">${title}</div>
                          <div class="comment-meta">Дата: ${date}</div>
                      </div>
                      <div class="badge">${category}</div>
                  </div>

                  <div class="comment-text">${text}</div>

                  <div class="comment-footer">
                      <div class="rates">
                          <div class="rate">Оценка: ${grade}</div>
                          <div class="rate">👍 ${plus}</div>
                          <div class="rate">👎 ${minus}</div>
                      </div>

                      <div class="comment-actions">
                          <button class="delete-btn" onclick="deleteComment(${c.id})">Удалить</button>
                      </div>
                  </div>
              </div>
          `;
      }).join("");
  } catch (e) {
      box.innerHTML = '<div class="empty">Ошибка соединения с сервером</div>';
  }
}

async function deleteComment(id) {
  const ok = confirm("Удалить комментарий? Это действие нельзя отменить.");

  if (!ok) {
      return;
  }

  try {
      const res = await fetch(`http://localhost:8080/comments/${id}`, {
          method: "DELETE",
          credentials: "include"
      });

      if (res.ok) {
          await loadMyComments();
      } else {
          alert("Не удалось удалить комментарий");
      }
  } catch (e) {
      alert("Ошибка при удалении");
  }
}

async function logout() {
  const ok = confirm("Выйти из аккаунта?");

  if (!ok) {
      return;
  }

  try {
      await fetch("http://localhost:8080/auth/logout", {
          method: "POST",
          credentials: "include"
      });
  } catch (e) {
  }

  window.location.href = "login.html";
}

async function init() {
  const ok = await loadProfile();
  if (ok) {
      await loadMyComments();
  }
}

init();
