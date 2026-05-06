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

async function loadComments() {
    const box = document.getElementById("comments-list");
    const guestActions = document.getElementById("guest-actions");
    const writeCommentBtn = document.getElementById("writeCommentHref");

    const isAuth = await checkAuth();
    console.log("isAuth =", isAuth);
    if (guestActions) guestActions.style.display = isAuth ? "none" : "flex";
    if (writeCommentBtn) writeCommentBtn.style.display = isAuth ? "inline-block" : "none";

    try {
 const title = document.getElementById("searchTitle")?.value.trim() || "";
 const sort = document.getElementById("sortSelect")?.value || "date_desc";

 const params = new URLSearchParams();
 if (title) params.append("title", title);
 params.append("sort", sort);

const res = await fetch("http://localhost:8080/comments?" + params.toString(), {
    method: "GET",
    credentials: "include"
});
       if (!res.ok) {
           box.innerHTML = '<div class="empty">Не удалось загрузить комментарии</div>';
           return;
       }
       const data = await res.json();
       if (!Array.isArray(data) || data.length === 0) {
           box.innerHTML = '<div class="empty">Комментариев пока нет</div>';
           document.getElementById("count-comments").innerText = "0";
           document.getElementById("count-likes").innerText = "0";
           document.getElementById("count-dislikes").innerText = "0";
           return;
       }
       let likes = 0;
       let dislikes = 0;
       data.forEach(c => {
           likes += Number(c.plusGrade || 0);
           dislikes += Number(c.minusGrade || 0);
       });
       document.getElementById("count-comments").innerText = data.length;
       document.getElementById("count-likes").innerText = likes;
       document.getElementById("count-dislikes").innerText = dislikes;
       box.innerHTML = data.map(c => {
           const title = escapeHtml(safe(c.title, "Без названия"));
           const text = escapeHtml(safe(c.comment, "Нет текста"));
           const date = escapeHtml(safe(c.postDate, "Дата не указана"));
           const grade = safe(c.grade, "-");
           const plus = safe(c.plusGrade, 0);
           const minus = safe(c.minusGrade, 0);
           const category = c.category && c.category
               ? escapeHtml(c.category.category)
               : "Без категории";
           const actionsHtml = isAuth
               ? `
                   <div class="comment-actions">
                       <button class="action-btn" onclick="upvote(${c.id})">Лайк</button>
                       <button class="action-btn dislike" onclick="downvote(${c.id})">Дизлайк</button>
                   </div>
               `
               : "";
           return `
               <div class="comment">
                   <div class="comment-top">
                       <div>
                           <a class="comment-title-link" href="comment.html?id=${c.id}">
                               <div class="comment-title">${title}</div>
                           </a>
                           <div class="comment-meta">Дата: ${date}</div>
                       </div>
                       <div class="badge">${category}</div>
                   </div>
                   <div class="comment-text">${text}</div>
                   <div class="comment-footer">
                       <div class="rates">
                           <div class="rate">Оценка: ${grade}</div>
                           <div class="rate"> + ${plus}</div>
                           <div class="rate"> - ${minus}</div>
                       </div>
                       ${actionsHtml}
                   </div>
               </div>
           `;
       }).join("");
   } catch (e) {
       box.innerHTML = '<div class="empty">Ошибка соединения с сервером</div>';
   }
}

async function upvote(id) {
    try {
        const res = await fetch(`http://localhost:8080/comments/${id}/upvote`, {
            method: "POST",
            credentials: "include"
        });

        if (res.ok) {
            loadComments();
        } else {
            alert("Нужно войти в аккаунт");
        }
    } catch (e) {
        alert("Ошибка при лайке");
    }
}

async function downvote(id) {
    try {
        const res = await fetch(`http://localhost:8080/comments/${id}/downvote`, {
            method: "POST",
            credentials: "include"
        });

        if (res.ok) {
            loadComments();
        } else {
            alert("Нужно войти в аккаунт");
        }
    } catch (e) {
        alert("Ошибка дизлайка");
    }
}

async function checkAuth() {
    try {
        const res = await fetch("http://localhost:8080/profile", {
            method: "GET",
            credentials: "include",
            headers: {
                "Accept": "application/json",
                "X-Requested-With": "XMLHttpRequest"
            }
        });

        return res.status === 200;
    } catch (e) {
        return false;
    }
}

const searchTitle = document.getElementById("searchTitle");
const clearSearchBtn = document.getElementById("clearSearchBtn");

if (searchTitle && clearSearchBtn) {
searchTitle.addEventListener("input", () => {
    clearSearchBtn.style.display = searchTitle.value ? "block" : "none";
});

clearSearchBtn.addEventListener("click", () => {
    searchTitle.value = "";
    clearSearchBtn.style.display = "none";
    searchTitle.focus();
    loadComments();
});
}

loadComments();
checkAuth();