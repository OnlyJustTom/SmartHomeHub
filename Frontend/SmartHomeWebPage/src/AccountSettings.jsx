import React, { useState } from "react";
import { useNavigate } from 'react-router-dom'
import './AccountSettings.css'

function AccountSettings({username}) {
    const [password, setPassword] = useState('');
    const navigate = useNavigate()

    const handleAccountUpdate = async () =>  {
        const payload = {
            username: username,
            password: password
        };
        try {
            const response = await fetch("http://localhost:8080/user", {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            if (response.ok) {
                console.log("Account updated successfully! (HTTP 200 OK)");
            } else if (response.status === 400) {
                console.log("Bad request (HTTP 400 Bad Request)");
            } else {
                console.log(`Unexpected error: HTTP ${response.status}`);
            }
        } catch (error) {
            console.log("Failed to update account: " + error);
        }
    };

    const handleAccountDelete = async () => {
        const payload = {
            username: username,
            password: password
        };
        try {
            const response = await fetch("http://localhost:8080/user", {
                method: "DELETE",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            if (response.ok) {
                console.log("Account deleted successfully! (HTTP 200 OK)");
                localStorage.removeItem("user")
                navigate("/");
            } else if (response.status === 400) {
                console.log("Bad request (HTTP 400 Bad Request)");
            } else {
                console.log(`Unexpected error: HTTP ${response.status}`);
            }
        } catch (error) {
            console.log("Failed to delete account: " + error);
        }
    };


    return (
        <div className="accountSettings">
            <h1>Account Settings</h1>
            <form className="accountSettingsForm" onSubmit={handleAccountUpdate}>
                <div className="accountFieldGroup">
                    <label htmlFor="accountPassword">Password</label>
                    <input
                        id="accountPassword"
                        type="password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        placeholder="Enter new password"
                    />
                </div>

                <div className="accountActions">
                    <button type="submit">Save Changes</button>
                    <button type="button" onClick={handleAccountDelete}>Delete Account</button>
                </div>
            </form>
        </div>
    );
}

export default AccountSettings;