async function reg() {
    const msg = document.getElementById("msg");
    msg.innerText = "";

    const fio = document.getElementById("fio").value.trim();
    const mail = document.getElementById("mail").value.trim();
    const pasword = document.getElementById("pasword").value;

    if (!fio || !mail || !pasword) {
      msg.innerText = "Заполните все поля";
      return;
    }

    const emailPattern = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/;

    if (!emailPattern.test(mail)) {
      msg.innerText = "Неверный формат почты";
      return;
    }

    if (pasword.length < 6) {
      msg.innerText = "Пароль должен быть не короче 6 символов";
      return;
    }

    try {
      const res = await fetch("http://localhost:8080/auth/register", {
          method: "POST",
          headers: {
              "Content-Type": "application/json"
          },
          credentials: "include",
          body: JSON.stringify({ fio, mail, pasword })
      });

      const text = await res.text();

      if (text === "ok") {
          msg.innerText = "Регистрация прошла успешно";
          setTimeout(() => {
              window.location.href = "login.html";
          }, 700);
          return;
      }

      msg.innerText = text || "Ошибка регистрации";
    } catch (e) {
      msg.innerText = "Ошибка соединения с сервером";
    }
}
