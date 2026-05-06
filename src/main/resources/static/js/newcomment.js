const form = document.getElementById('commentForm');
const categoryContainer = document.getElementById('categoryContainer');
const formMessage = document.getElementById('formMessage');
const starRating = document.getElementById('starRating');
const ratingValue = document.getElementById('ratingValue');
const submitBtn = document.querySelector('.submit-btn');

let selectedGrade = 0;
let selectedCategoryId = null;

function setMessage(text, type) {
    formMessage.textContent = text;
    formMessage.className = 'form-message';
    if (type) {
        formMessage.classList.add(type);
    }
}

function renderRating() {
    starRating.innerHTML = '';

    for (let i = 1; i <= 10; i++) {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'star-btn' + (i <= selectedGrade ? ' active' : '');
        btn.textContent = i;

        btn.addEventListener('click', () => {
            selectedGrade = i;
            ratingValue.textContent = 'Оценка: ' + i + ' / 10';
            renderRating();
        });

        starRating.appendChild(btn);
    }
}

async function loadRootCategories() {
    try {
        const response = await fetch('http://localhost:8080/categories/roots', {
            method: 'GET',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Не удалось загрузить категории');
        }

        const data = await response.json();
        categoryContainer.innerHTML = '';
        selectedCategoryId = null;

        if (data.length > 0) {
            createCategorySelect(data, 0);
        }
    } catch (e) {
        setMessage('Ошибка загрузки категорий', 'error');
    }
}

function removeSelectsAfter(level) {
    const selects = categoryContainer.querySelectorAll('select');
    selects.forEach(select => {
        const currentLevel = Number(select.dataset.level);
        if (currentLevel > level) {
            select.remove();
        }
    });
}

function createCategorySelect(categories, level) {
    const select = document.createElement('select');
    select.dataset.level = String(level);

    const firstOption = document.createElement('option');
    firstOption.value = '';
    firstOption.textContent = 'Выберите категорию';
    select.appendChild(firstOption);

    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category.id;
        option.textContent = category.category;
        select.appendChild(option);
    });

    select.addEventListener('change', async () => {
        removeSelectsAfter(level);

        if (!select.value) {
            selectedCategoryId = null;
            return;
        }

        selectedCategoryId = Number(select.value);

        try {
            const response = await fetch('http://localhost:8080/categories/' + select.value + '/children', {
                method: 'GET',
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error('Ошибка загрузки подкатегорий');
            }

            const children = await response.json();

            if (children.length > 0) {
                createCategorySelect(children, level + 1);
            }
        } catch (e) {
            setMessage('Не удалось загрузить подкатегории', 'error');
        }
    });

    categoryContainer.appendChild(select);
}

form.addEventListener('submit', async e => {
    e.preventDefault();
    setMessage('', '');

    const title = document.getElementById('title').value.trim();
    const comment = document.getElementById('comment').value.trim();

    if (!title) {
        setMessage('Введите название товара', 'error');
        return;
    }

    if (!selectedCategoryId) {
        setMessage('Выберите категорию', 'error');
        return;
    }

    if (!comment) {
        setMessage('Введите текст комментария', 'error');
        return;
    }

    if (selectedGrade < 1 || selectedGrade > 10) {
        setMessage('Поставьте оценку от 1 до 10', 'error');
        return;
    }

    const payload = {
        categoryId: selectedCategoryId,
        title: title,
        comment: comment,
        grade: selectedGrade
    };

    submitBtn.disabled = true;

    try {
        const response = await fetch('http://localhost:8080/comments', {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            form.reset();
            selectedGrade = 0;
            selectedCategoryId = null;
            renderRating();
            await loadRootCategories();
            ratingValue.textContent = 'Оценка не выбрана';
            window.location.href = "index.html";
            return;
        }

        if (response.status === 401 || response.status === 403) {
            setMessage('Только авторизованный пользователь может оставить отзыв', 'error');
            return;
        }

        const text = await response.text();
        setMessage(text || 'Ошибка при отправке отзыва', 'error');
    } catch (e) {
        setMessage('Сервер недоступен или произошла ошибка', 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Опубликовать отзыв';
    }
});

renderRating();
loadRootCategories();
