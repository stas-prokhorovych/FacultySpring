const login = document.getElementById('login')
const password = document.getElementById('password')
const form = document.getElementById('form')

const loginErrorElement = document.getElementById('login-error')
const passwordErrorElement = document.getElementById('password-error')

form.addEventListener('submit', (e) => {
    let loginError = []
    if(login.value === '' || login.value == null) {
        loginError.push('*name is required')
    }
    if(loginError.length > 0) {
        e.preventDefault()
        loginErrorElement.innerText = loginError.join(', ')
    }

    let passwordError = []
    if(password.value === '' || password.value == null) {
        passwordError.push('*password is required')
    }

    if(password.value.length === 2 || password.value.length === 1) {
        passwordError.push('*password must be longer than 2 characters')
    }

    if(password.value.length >= 64) {
        passwordError.push('*password must be less than 64 characters')
    }

    if(passwordError.length > 0) {
        e.preventDefault()
        passwordErrorElement.innerText = passwordError.join(', ')
    }
})