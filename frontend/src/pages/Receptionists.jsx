import { useState, useEffect } from "react";
import Layout from "../components/layout/Layout";
import {
  getAllReceptionists,
  registerReceptionistUser,
  deleteReceptionist,
} from "../api/receptionists";
import "../styles/doctors.css";

function generateId() {
  return String(Math.floor(10000000 + Math.random() * 90000000));
}
function generatePassword() {
  const chars = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
  return Array.from({ length: 8 }, () =>
    chars[Math.floor(Math.random() * chars.length)]
  ).join("");
}
function getInitials(name) {
  if (!name) return "?";
  return name.split(" ").map((n) => n[0]).join("").slice(0, 2).toUpperCase();
}

const emptyForm = () => ({
  fullName: "",
  rcId: generateId(),
  password: generatePassword(),
  phone: "",
  shift: "KUNDUZGI",
});

export default function Receptionists() {
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState(emptyForm());
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);
  const [credentials, setCredentials] = useState(null);

  useEffect(() => { fetchAll(); }, []);

  const fetchAll = async () => {
    setLoading(true);
    try {
      const data = await getAllReceptionists();
      setList(data);
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    if (!form.fullName || !form.phone) {
      setError("Ism va telefon majburiy");
      return;
    }
    setSaving(true);
    setError("");
    try {
      const email = `RC${form.rcId}@cliniq.uz`;
      const regRes = await registerReceptionistUser(form.fullName, email, form.password);
      if (!regRes.success) {
        setError(regRes.message || "Xodim yaratishda xato");
        return;
      }
      setShowModal(false);
      setCredentials({ fullName: form.fullName, email, password: form.password });
      fetchAll();
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Rostdan ham o'chirasizmi?")) return;
    await deleteReceptionist(id);
    fetchAll();
  };

  return (
    <Layout title="Qabulxona xodimlari">
      <div className="page-header">
        <div>
          <h2>Qabulxona xodimlari</h2>
          <p className="page-subtitle">Jami {list.length} ta xodim</p>
        </div>
        <button className="btn-add" onClick={() => { setForm(emptyForm()); setError(""); setShowModal(true); }}>
          + Xodim qo'shish
        </button>
      </div>

      {loading ? (
        <p className="loading">Yuklanmoqda...</p>
      ) : list.length === 0 ? (
        <p className="empty-text">Xodim topilmadi</p>
      ) : (
        <div className="doctors-grid">
          {list.map((r) => (
            <div key={r.id} className="doctor-card">
              <div className="doctor-card-top">
                <div className="doctor-card-avatar">{getInitials(r.fullName)}</div>
                <div className="doctor-card-info">
                  <p className="doctor-card-name">{r.fullName}</p>
                  <p className="doctor-card-spec">Qabulxona xodimi</p>
                </div>
              </div>
              <div className="doctor-card-details">
                <div className="doctor-detail-row">
                  <span className="detail-label">Email:</span>
                  <span className="detail-value">{r.email || "—"}</span>
                </div>
              </div>
              <div className="doctor-card-actions">
                <button className="btn-delete" onClick={() => handleDelete(r.id)}>O'chirish</button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Yangi qabulxona xodimi</h3>
            {error && <p className="error-msg">{error}</p>}

            <div className="credentials-preview">
              <p className="credentials-title">Tizimga kirish ma'lumotlari (avtomatik)</p>
              <div className="credential-row">
                <span className="credential-label">Login:</span>
                <span className="credential-value">RC{form.rcId}@cliniq.uz</span>
              </div>
              <div className="credential-row">
                <span className="credential-label">Parol:</span>
                <span className="credential-value">{form.password}</span>
              </div>
            </div>

            <div className="form-group">
              <label>Ism Familiya</label>
              <input
                value={form.fullName}
                onChange={(e) => setForm({ ...form, fullName: e.target.value })}
                placeholder="Karimova Dilnoza"
              />
            </div>
            <div className="form-group">
              <label>Telefon</label>
              <input
                value={form.phone}
                onChange={(e) => setForm({ ...form, phone: e.target.value })}
                placeholder="+998 90 123 45 67"
              />
            </div>
            <div className="form-group">
              <label>Smena</label>
              <select value={form.shift} onChange={(e) => setForm({ ...form, shift: e.target.value })}>
                <option value="KUNDUZGI">Kunduzgi</option>
                <option value="KECHKI">Kechki</option>
                <option value="TUNGI">Tungi</option>
              </select>
            </div>

            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setShowModal(false)}>Bekor qilish</button>
              <button className="btn-save" onClick={handleSave} disabled={saving}>
                {saving ? "Saqlanmoqda..." : "Saqlash"}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Credentials modal */}
      {credentials && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>✅ Xodim yaratildi!</h3>
            <p style={{ color: "#64748b", marginBottom: 16 }}>
              Quyidagi ma'lumotlarni xodimga bering:
            </p>
            <div className="credentials-preview">
              <div className="credential-row">
                <span className="credential-label">Ism:</span>
                <span className="credential-value">{credentials.fullName}</span>
              </div>
              <div className="credential-row">
                <span className="credential-label">Login:</span>
                <span className="credential-value">{credentials.email}</span>
              </div>
              <div className="credential-row">
                <span className="credential-label">Parol:</span>
                <span className="credential-value">{credentials.password}</span>
              </div>
            </div>
            <div className="modal-actions">
              <button className="btn-save" onClick={() => setCredentials(null)}>Tushunarli</button>
            </div>
          </div>
        </div>
      )}
    </Layout>
  );
}