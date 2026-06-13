import { useState } from "react";
import "../styles/login.css";
import { Eye, EyeOff } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async () => {
    setError("");
     if (!email) {
    setError("Email kiritish majburiy");
    return;
      }
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        setError("Email formati noto'g'ri");
        return;
      }
      if (!password) {
        setError("Parol kiritish majburiy");
        return;
      }
      if (password.length < 8) {
        setError("Parol kamida 8 ta belgi bo'lishi kerak");
        return;
      }
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
            if (role === "ROLE_SUPER_ADMIN" || role === "ROLE_ADMIN") {
              navigate("/role-selector");
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
        <h2 className="login-title">CLINIC</h2>
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

        
        <div style={{ textAlign: "center", margin: "12px 0", color: "#94a3b8", fontSize: "13px" }}>
  yoki
</div>

<button
  onClick={() => window.location.href = "http://localhost:8080/oauth2/authorization/google"}
  className="google-login-btn"
>
  <img 
    src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" 
    width={18} 
    alt="Google"
    style={{ marginRight: 8 }}
  />
  Google bilan kirish
</button>
<p style={{ textAlign: "center", marginTop: 16, fontSize: 14, color: "#64748b" }}>
  Akkauntingiz yo'qmi?{" "}
  <Link to="/register" style={{ color: "#0D7377", fontWeight: 600 }}>
    Ro'yxatdan o'tish
  </Link>
</p>

      </div>
      
    </div>
    
  );
}

export default Login;