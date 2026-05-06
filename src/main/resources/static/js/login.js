async function login() {
    const msg = document.getElementById("msg");
    msg.innerText = "";

    const mail = document.getElementById("mail").value.trim();
    const pasword = document.getElementById("pasword").value;

    if (!mail || !pasword) {
        msg.innerText = "Заполните все поля";
        return;
    }

    const body = new URLSearchParams();
    body.append("username", mail);
    body.append("password", pasword);

    try {
        const res = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            credentials: "include",
            body: body
        });

        if (res.ok) {
            window.location.href = "profile.html";
        }
    } catch (e) {
        msg.innerText = "Ошибка соединения с сервером";
    }
}