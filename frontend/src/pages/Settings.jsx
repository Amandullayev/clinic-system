import { useState, useEffect } from "react";
import Layout from "../components/layout/Layout";
import { changePassword, getSettings, updateSettings } from "../api/settings";
import "../styles/settings.css";

const emptyForm = { oldPassword: "", newPassword: "", confirmPassword: "" };

export default function Settings() {
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const isSuperAdmin = user?.role === "SUPER_ADMIN";
  

  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const [clinic, setClinic] = useState(null);
  const [clinicMsg, setClinicMsg] = useState("");
  const [clinicLoading, setClinicLoading] = useState(false);

  useEffect(() => {
    if (isSuperAdmin) {
      getSettings().then(setClinic);
    }
  }, []);

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

  const handleClinicSave = async () => {
    setClinicMsg("");
    setClinicLoading(true);
    try {
      await updateSettings(clinic);
      setClinicMsg("Sozlamalar muvaffaqiyatli saqlandi!");
    } catch {
      setClinicMsg("Xato yuz berdi");
    } finally {
      setClinicLoading(false);
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

      {isSuperAdmin && clinic && (
        <div className="settings-card" style={{ marginTop: "1.5rem" }}>
          <h3>Klinika sozlamalari</h3>

          {clinicMsg && (
            <p className={clinicMsg.includes("Xato") ? "settings-error" : "settings-success"}>
              {clinicMsg}
            </p>
          )}

          <div className="settings-grid">
            <div className="form-group">
              <label>Klinika nomi</label>
              <input
                value={clinic.clinicName || ""}
                onChange={e => setClinic({ ...clinic, clinicName: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>Telefon</label>
              <input
                value={clinic.phone || ""}
                onChange={e => setClinic({ ...clinic, phone: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>Email</label>
              <input
                value={clinic.email || ""}
                onChange={e => setClinic({ ...clinic, email: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>Veb-sayt</label>
              <input
                value={clinic.website || ""}
                onChange={e => setClinic({ ...clinic, website: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>Manzil</label>
              <input
                value={clinic.address || ""}
                onChange={e => setClinic({ ...clinic, address: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>Ish boshlanish vaqti</label>
              <input
                type="time"
                value={clinic.openTime || ""}
                onChange={e => setClinic({ ...clinic, openTime: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>Ish tugash vaqti</label>
              <input
                type="time"
                value={clinic.closeTime || ""}
                onChange={e => setClinic({ ...clinic, closeTime: e.target.value })}
              />
            </div>
          </div>

          <button
            className="btn-save-password"
            onClick={handleClinicSave}
            disabled={clinicLoading}
          >
            {clinicLoading ? "Saqlanmoqda..." : "Sozlamalarni saqlash"}
          </button>
        </div>
      )}
    </Layout>
  );
}