import { useState } from "react";
import Layout from "../components/layout/Layout";
import { changePassword } from "../api/settings";
import "../styles/settings.css";

const emptyForm = { oldPassword: "", newPassword: "", confirmPassword: "" };

export default function Settings() {
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = async () => {
    setError("");
    setSuccess("");

    if (!form.oldPassword || !form.newPassword || !form.confirmPassword) {
      setError("Barcha maydonlarni to'ldiring");
      return;
    }

    if (form.newPassword !== form.confirmPassword) {
      setError("Yangi parollar mos kelmadi");
      return;
    }

    if (form.newPassword.length < 6) {
      setError("Yangi parol kamida 6 ta belgidan iborat bo'lishi kerak");
      return;
    }

    setLoading(true);
    try {
      const res = await changePassword({
        oldPassword: form.oldPassword,
        newPassword: form.newPassword,
      });

      if (res.success) {
        setSuccess("Parol muvaffaqiyatli o'zgartirildi");
        setForm(emptyForm);
      } else {
        setError(res.message || "Xato yuz berdi");
      }
    } catch {
      setError("Serverga ulanib bo'lmadi");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout title="Sozlamalar">
      <h2 className="settings-title">Sozlamalar</h2>

      <div className="settings-grid">
        <div className="settings-card">
          <h3>Profil ma'lumotlari</h3>
          <div className="profile-row">
            <span className="profile-label">Ism Familiya</span>
            <span className="profile-value">{user?.fullName || "—"}</span>
          </div>
          <div className="profile-row">
            <span className="profile-label">Email</span>
            <span className="profile-value">{user?.email || "—"}</span>
          </div>
          <div className="profile-row">
            <span className="profile-label">Rol</span>
            <span className="profile-value role-badge">{user?.role || "—"}</span>
          </div>
        </div>

        <div className="settings-card">
          <h3>Parolni o'zgartirish</h3>

          {error && <p className="settings-error">{error}</p>}
          {success && <p className="settings-success">{success}</p>}

          <div className="form-group">
            <label>Joriy parol</label>
            <input
              type="password"
              value={form.oldPassword}
              onChange={e => setForm({ ...form, oldPassword: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label>Yangi parol</label>
            <input
              type="password"
              value={form.newPassword}
              onChange={e => setForm({ ...form, newPassword: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label>Yangi parolni tasdiqlang</label>
            <input
              type="password"
              value={form.confirmPassword}
              onChange={e => setForm({ ...form, confirmPassword: e.target.value })}
            />
          </div>

          <button
            className="btn-save-password"
            onClick={handleChange}
            disabled={loading}
          >
            {loading ? "Saqlanmoqda..." : "Parolni o'zgartirish"}
          </button>
        </div>
      </div>
    </Layout>
  );
}