import { useState } from "react";
import "../styles/login.css";
import { Eye, EyeOff } from "lucide-react";
import { useNavigate } from "react-router-dom";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async () => {
    setError("");
    setLoading(true);
    try {
      const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });
      const data = await response.json();

      if (data?.data?.token) {
        localStorage.setItem("token", data.data.token);
        localStorage.setItem("user", JSON.stringify(data.data));
        try {
          const payload = JSON.parse(atob(data.data.token.split(".")[1]));
          const role = payload.role;
          if (role === "ROLE_SUPER_ADMIN") {
              navigate("/superadmin/dashboard");
          } else if (role === "ROLE_RECEPTIONIST") {
              navigate("/receptionist/dashboard");
          } else if (role === "ROLE_DOCTOR") {
              navigate("/doctor/dashboard");
          } else if (role === "ROLE_PATIENT") {
            navigate("/patient/dashboard");
          } else {
            navigate("/dashboard");
          }
        } catch {
          navigate("/dashboard");
        }
      } else {
        setError(data?.message || "Email yoki parol noto'g'ri");
      }
    } catch (err) {
      setError("Serverga ulanib bo'lmadi");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h2 className="login-title">CLINIQ</h2>
        <p className="login-subtitle">Tizimga kirish</p>

        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="login-input"
        />

        <div className="password-wrapper">
          <input
            type={showPassword ? "text" : "password"}
            placeholder="Parol"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="login-input password-input"
          />
          <span
            className="toggle-password"
            onClick={() => setShowPassword(!showPassword)}
          >
            {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
          </span>
        </div>

        {error && <p className="login-error">{error}</p>}

        <button
          onClick={handleLogin}
          disabled={loading}
          className="login-button"
        >
          {loading ? "Yuklanmoqda..." : "Kirish"}
        </button>
      </div>
    </div>
  );
}

export default Login;