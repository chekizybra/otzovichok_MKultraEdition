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

async function loadComment() {
    const box = document.getElementById("comment-box");
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (!id) {
        box.innerHTML = '<div class="empty">Комментарий не найден</div>';
        return;
    }

    try {
        const res = await fetch("http://localhost:8080/comments/" + id, {
            method: "GET",
            credentials: "include"
        });

        if (!res.ok) {
            box.innerHTML = '<div class="empty">Не удалось загрузить комментарий</div>';
            return;
        }

        const c = await res.json();

        if (!c) {
            box.innerHTML = '<div class="empty">Комментарий не найден</div>';
            return;
        }

        const title = escapeHtml(safe(c.title, "Без названия"));
        const text = escapeHtml(safe(c.comment, "Нет текста"));
        const date = escapeHtml(safe(c.postDate, "Дата не указана"));
        const grade = safe(c.grade, "-");
        const plus = safe(c.plusGrade, 0);
        const minus = safe(c.minusGrade, 0);
        const category = c.category && c.category.category
            ? escapeHtml(c.category.category)
            : "Без категории";
        const categoryPath = escapeHtml(safe(c.categoryPath, category));

        box.innerHTML = `
            <div class="comment">
                <div class="comment-top">
                    <div>
                        <div class="comment-title">${title}</div>
                        <div class="comment-meta">Дата: ${date}</div>
                    </div>
                    <div class="badge">${category}</div>
                </div>

                <div class="comment-meta" style="margin-bottom: 12px;">Полный путь категории: ${categoryPath}</div>

                <div class="comment-text">${text}</div>

                <div class="comment-footer">
                    <div class="rates">
                        <div class="rate">Оценка: ${grade}</div>
                        <div class="rate">+ ${plus}</div>
                        <div class="rate">- ${minus}</div>
                    </div>
                </div>
            </div>
        `;
    } catch (e) {
        box.innerHTML = '<div class="empty">Ошибка соединения с сервером</div>';
    }
}

loadComment();