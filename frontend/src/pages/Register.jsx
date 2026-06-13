import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Eye, EyeOff } from "lucide-react";
import "../styles/login.css";

export default function Register() {
  const [step, setStep] = useState(1);
  const [form, setForm] = useState({ fullName: "", email: "", password: "", confirmPassword: "" });
  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPass, setShowPass] = useState(false);
  const navigate = useNavigate();

  const handleSendOtp = async () => {
    setError("");
    if (!form.fullName) { setError("Ism kiritish majburiy"); return; }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) { setError("Email formati noto'g'ri"); return; }
    if (form.password.length < 6) { setError("Parol kamida 6 ta belgi bo'lishi kerak"); return; }
    if (form.password !== form.confirmPassword) { setError("Parollar mos kelmadi"); return; }

    setLoading(true);
    try {
      const res = await fetch(`/api/auth/send-otp?email=${encodeURIComponent(form.email)}`, {
        method: "POST",
      });
      const data = await res.json();
      if (data.success) {
        setStep(2);
      } else {
        setError(data.message || "Kod jo'natishda xato");
      }
    } catch {
      setError("Serverga ulanib bo'lmadi");
    } finally {
      setLoading(false);
    }
  };

  const handleVerify = async () => {
    setError("");
    if (otp.length !== 5) { setError("5 xonali kodni kiriting"); return; }

    setLoading(true);
    try {
      const res = await fetch(`/api/auth/verify-otp?otp=${otp}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          fullName: form.fullName,
          email: form.email,
          password: form.password,
        }),
      });
      const data = await res.json();
      if (data?.data?.token) {
        localStorage.setItem("token", data.data.token);
        localStorage.setItem("user", JSON.stringify(data.data));
        navigate("/patient/dashboard");
      } else {
        setError(data.message || "Kod noto'g'ri");
      }
    } catch {
      setError("Serverga ulanib bo'lmadi");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h2 className="login-title">CLINIC</h2>
        <p className="login-subtitle">
          {step === 1 ? "Ro'yxatdan o'tish" : "Emailni tasdiqlang"}
        </p>

        {step === 1 ? (
          <>
            <input
              type="text"
              placeholder="Ism Familiya"
              value={form.fullName}
              onChange={e => setForm({ ...form, fullName: e.target.value })}
              className="login-input"
            />
            <input
              type="email"
              placeholder="Email"
              value={form.email}
              onChange={e => setForm({ ...form, email: e.target.value })}
              className="login-input"
            />
            <div className="password-wrapper">
              <input
                type={showPass ? "text" : "password"}
                placeholder="Parol"
                value={form.password}
                onChange={e => setForm({ ...form, password: e.target.value })}
                className="login-input password-input"
              />
              <span className="toggle-password" onClick={() => setShowPass(!showPass)}>
                {showPass ? <EyeOff size={20} /> : <Eye size={20} />}
              </span>
            </div>
            {form.password && (
              <p style={{ fontSize: 12, marginTop: -8, marginBottom: 8, color:
                form.password.length < 6 ? "#ef4444" :
                form.password.length < 10 ? "#f59e0b" : "#059669" }}>
                Parol kuchi: {form.password.length < 6 ? "Zaif" : form.password.length < 10 ? "O'rtacha" : "Kuchli"}
              </p>
            )}
            <input
              type="password"
              placeholder="Parolni tasdiqlang"
              value={form.confirmPassword}
              onChange={e => setForm({ ...form, confirmPassword: e.target.value })}
              className="login-input"
            />
            {form.confirmPassword && (
              form.password === form.confirmPassword
                ? <p style={{ color: "#059669", fontSize: 13, marginTop: -8, marginBottom: 8 }}>✓ Parollar mos keldi</p>
                : <p style={{ color: "#ef4444", fontSize: 13, marginTop: -8, marginBottom: 8 }}>✗ Parollar mos kelmadi</p>
            )}

            {error && <p className="login-error">{error}</p>}

            <button onClick={handleSendOtp} disabled={loading} className="login-button">
              {loading ? "Kod jo'natilmoqda..." : "Tasdiqlash kodi olish"}
            </button>
          </>
        ) : (
          <>
            <p style={{ textAlign: "center", color: "#64748b", fontSize: 14, marginBottom: 16 }}>
              <strong>{form.email}</strong> ga 5 xonali kod jo'natildi
            </p>
            <input
              type="text"
              placeholder="- - - - -"
              maxLength={5}
              value={otp}
              onChange={e => setOtp(e.target.value.replace(/\D/g, ""))}
              className="login-input"
              style={{ textAlign: "center", fontSize: 24, letterSpacing: 8 }}
            />

            {error && <p className="login-error">{error}</p>}

            <button onClick={handleVerify} disabled={loading} className="login-button">
              {loading ? "Tekshirilmoqda..." : "Tasdiqlash"}
            </button>

            <button
              onClick={() => { setStep(1); setOtp(""); setError(""); }}
              style={{ background: "none", border: "none", color: "#0D7377", cursor: "pointer", width: "100%", marginTop: 8, fontSize: 14 }}
            >
              ← Orqaga qaytish
            </button>
          </>
        )}

        <p style={{ textAlign: "center", marginTop: 16, fontSize: 14, color: "#64748b" }}>
          Akkauntingiz bormi?{" "}
          <Link to="/login" style={{ color: "#0D7377", fontWeight: 600 }}>Kirish</Link>
        </p>
      </div>
    </div>
  );
}