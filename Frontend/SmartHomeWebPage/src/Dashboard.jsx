import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'

function Dashboard() {
  const [user, setUser] = useState(null)
  const navigate = useNavigate()

  useEffect(() => {
    const savedUser = localStorage.getItem("user")

    if (!savedUser) {
      navigate("/")
    }

    try {
      const parsedUser = JSON.parse(savedUser)
      setUser(parsedUser)
    } catch (error) {
      console.error("Failed to parse user from localStorage:", error)
      localStorage.removeItem("user")
      navigate("/")
    }
  }, [navigate])

  const logout = () => {
    localStorage.removeItem("user")
    navigate("/")
  }

  if (!user) {
    return <p>Loading...</p>
  }

  return (
    <div style={{ padding: "2rem" }}>
      <h1>Dashboard</h1>
      <p>Welcome, {user.username}</p>

      <p>User ID: {user.id}</p>

      <button onClick={logout}>Logout</button>
    </div>
  )
}

export default Dashboard