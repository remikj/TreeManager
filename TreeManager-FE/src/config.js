
console.log(process.env)

const config = {
    BACKEND_URL: process.env.REACT_APP_BACKEND_URL !== undefined ? process.env.REACT_APP_BACKEND_URL : "http://localhost:8080",
}

export {
    config
}