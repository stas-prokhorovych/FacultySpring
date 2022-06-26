window.onload = function () {
    let isValid = false;
    const form = document.getElementById("form")
    const captchaError = document.getElementById("captcha-error")

    form.addEventListener("submit", function (event){
        event.preventDefault();
        const response = grecaptcha.getResponse();
        console.log(response);
        if(response) {
            form.submit();
        } else {
            captchaError.innerHTML = "Please check captcha";
        }
    })
}