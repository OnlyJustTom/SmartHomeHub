import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import './Homepage.css'

function App() {
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")
  const [buttonDisabled, setButtonDisabled] = useState(true)
  const navigate = useNavigate()

  useEffect(() => {
    handleUserOrPasswordChange()
  }, [username, password])

  function handleUserOrPasswordChange() {
    document.title = "Smart Home - Login"

    if (!username.trim() || !password.trim()) {
      setButtonDisabled(true)
    }
    else {
      setButtonDisabled(false)
    }
  }
  const login = async (e) => {
    e.preventDefault()

    if (!username.trim() || !password.trim()) {
      setError("Valid Username and password are required")
      console.log("Username and password are required")
      return
    }

    setError("")

    try {
      const response = await fetch('http://localhost:8080/user', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username,
          password
        })
      })
      
      let data = null

      if(response.ok){
        data = await response.json()
        if(localStorage.getItem("user")){
          localStorage.removeItem("user")
        }
        localStorage.setItem("user", JSON.stringify(data))
        console.log(JSON.stringify(data))
        navigate("/dashboard")
      }
      else{
        data = await response.text()
        setError(data)
      }
      
      console.log(data)
    } catch (error) {
      console.error('Error sending login info:', error)
    }
  }

  const signup = async (e) => {
    e.preventDefault()

    if (!username.trim() || !password.trim()) {
      setError("Username and password are required")
      console.log("Username and password are required")
      return
    }

    setError("")

    try {
      const response = await fetch('http://localhost:8080/user', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username,
          password
        })
      })

      let data = null

      if(response.ok){
        data = await response.json()
      }
      else{
        data = await response.text()
        setError(data)
      }
      
      console.log(data)
    } catch (error) {
      console.error('Error sending signup info:', error)
    }
  }

  return (
    <div className="bg">
      <div className="loginContainer">
        <h1 className="title">Welcome to Smart Home</h1>

        <form className="loginForm">
          <div>
            <label>Username</label>
            <input
              type="username"
              name="username"
              required
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>

          <div>
            <label>Password</label>
            <input
              type="password"
              name="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          {error && <p style={{ color: "red" }}>{error}</p>}

          <button
            type="button"
            disabled={buttonDisabled}
            onClick={login}
          >
            Login
          </button>

          <button
            type="button"
            disabled={buttonDisabled}
            onClick={signup}
          >
            Sign Up
          </button>
        </form>
      </div>
    </div>
  )
}

export default App