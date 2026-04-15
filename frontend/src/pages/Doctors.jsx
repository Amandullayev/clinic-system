import { useState, useEffect } from "react";
import Layout from "../components/layout/Layout";
import {
  getAllDoctors,
  registerDoctorUser,
  createDoctor,
  updateDoctor,
  deleteDoctor,
} from "../api/doctors";
import "../styles/doctors.css";

const DAYS = ["Du", "Se", "Ch", "Pa", "Ju", "Sh", "Ya"];

const SPECIALIZATIONS = [
  "Terapevt", "Kardiolog", "Neyrologiya", "Pediatr", "Jarroh",
  "Ginekolog", "Oftalmolog", "Stomatolog", "Ortoped", "Urolog",
  "Dermatolog", "Endokrinolog", "Psixiatr", "LOR", "Radiolog",
];

function generateDoctorId() {
  return String(Math.floor(10000000 + Math.random() * 90000000));
}

function generatePassword() {
  const chars = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
  return Array.from({ length: 8 }, () => chars[Math.floor(Math.random() * chars.length)]).join("");
}

const emptyForm = () => ({
  fullName: "",
  doctorId: generateDoctorId(),
  password: generatePassword(),
  specialization: "",
  phone: "",
  licenseNumber: "",
  experienceYears: "",
  workStartTime: "",
  workEndTime: "",
  workingDays: [],
  status: "ACTIVE",
});

function getInitials(name) {
  if (!name) return "?";
  return name.split(" ").map(n => n[0]).join("").slice(0, 2).toUpperCase();
}

function Stars({ rating }) {
  const r = Math.round(rating || 0);
  return (
    <div className="stars">
      {[1, 2, 3, 4, 5].map(i => (
        <span key={i} className={i <= r ? "star-filled" : "star-empty"}>★</span>
      ))}
    </div>
  );
}

const statusLabel = { ACTIVE: "Faol", BUSY: "Band", OFFLINE: "Offline" };
const statusClass  = { ACTIVE: "badge-active", BUSY: "badge-busy", OFFLINE: "badge-offline" };

export default function Doctors() {
  const [doctors, setDoctors]           = useState([]);
  const [filtered, setFiltered]         = useState([]);
  const [search, setSearch]             = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [loading, setLoading]           = useState(true);
  const [showModal, setShowModal]       = useState(false);
  const [editId, setEditId]             = useState(null);
  const [form, setForm]                 = useState(emptyForm());
  const [error, setError]               = useState("");
  const [saving, setSaving]             = useState(false);
  const [credentials, setCredentials]   = useState(null);

  useEffect(() => { fetchAll(); }, []);

  useEffect(() => {
    let result = doctors;
    if (statusFilter !== "ALL") {
      result = result.filter(d => d.status === statusFilter);
    }
    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter(d =>
        d.fullName?.toLowerCase().includes(q) ||
        d.specialization?.toLowerCase().includes(q) ||
        d.phone?.includes(q)
      );
    }
    setFiltered(result);
  }, [search, statusFilter, doctors]);

  const fetchAll = async () => {
    setLoading(true);
    try {
      const data = await getAllDoctors();
      setDoctors(data);
      setFiltered(data);
    } finally {
      setLoading(false);
    }
  };

  const toggleDay = (day) => {
    setForm(prev => ({
      ...prev,
      workingDays: prev.workingDays.includes(day)
        ? prev.workingDays.filter(d => d !== day)
        : [...prev.workingDays, day],
    }));
  };

  const openAdd = () => {
    setForm(emptyForm());
    setEditId(null);
    setError("");
    setShowModal(true);
  };

  const openEdit = (d) => {
    setForm({
      fullName: d.fullName || "",
      doctorId: "",
      password: "",
      specialization: d.specialization || "",
      phone: d.phone || "",
      licenseNumber: d.licenseNumber || "",
      experienceYears: d.experienceYears || "",
      workStartTime: d.workStartTime || "",
      workEndTime: d.workEndTime || "",
      workingDays: d.workingDays
        ? d.workingDays.split(",").map(s => s.trim()).filter(Boolean)
        : [],
      status: d.status || "ACTIVE",
    });
    setEditId(d.id);
    setError("");
    setShowModal(true);
  };

  const handleSave = async () => {
    if (!form.fullName || !form.specialization || !form.phone) {
      setError("Ism, mutaxassislik va telefon majburiy");
      return;
    }

    setSaving(true);
    setError("");

    try {
      if (!editId) {
        // 1. User ro'yxatdan o'tkazish
        const regRes = await registerDoctorUser(
      form.fullName,
      `DR${form.doctorId}@cliniq.uz`,
      form.password
    );

        const userId = regRes.data?.id;
        if (!userId) {
          setError("Foydalanuvchi ID si qaytarilmadi");
          return;
        }

        // 2. Doctor profil yaratish
        const doctorRes = await createDoctor({
          userId,
          specialization: form.specialization,
          phone: form.phone,
          licenseNumber: form.licenseNumber,
          experienceYears: form.experienceYears ? Number(form.experienceYears) : null,
          workingDays: form.workingDays.join(", "),
          workStartTime: form.workStartTime || null,
          workEndTime: form.workEndTime || null,
          status: form.status,
        });

        if (!doctorRes.success) {
          setError(doctorRes.message || "Shifokor yaratishda xato");
          return;
        }

        // 3. Muvaffaqiyat — login ma'lumotlarini ko'rsat
        setShowModal(false);
        setCredentials({
          fullName: form.fullName,
          email: `DR${form.doctorId}@cliniq.uz`,
          password: form.password,
        });
        fetchAll();

      } else {
        // Tahrirlash
        const res = await updateDoctor(editId, {
          specialization: form.specialization,
          phone: form.phone,
          licenseNumber: form.licenseNumber,
          experienceYears: form.experienceYears ? Number(form.experienceYears) : null,
          workingDays: form.workingDays.join(", "),
          workStartTime: form.workStartTime || null,
          workEndTime: form.workEndTime || null,
          status: form.status,
        });

        if (res.success) {
          setShowModal(false);
          fetchAll();
        } else {
          setError(res.message || "Xato yuz berdi");
        }
      }
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Rostdan ham o'chirasizmi?")) return;
    await deleteDoctor(id);
    fetchAll();
  };

  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text);
  };

  return (
    <Layout title="Shifokorlar">

      <div className="page-header">
        <div>
          <h2>Shifokorlar</h2>
          <p className="page-subtitle">Jami {doctors.length} ta shifokor</p>
        </div>
        <button className="btn-add" onClick={openAdd}>+ Shifokor qo'shish</button>
      </div>

      <div className="doctors-toolbar">
        <input
          placeholder="Ism, mutaxassislik yoki telefon..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
        <select value={statusFilter} onChange={e => setStatusFilter(e.target.value)}>
          <option value="ALL">Barcha holat</option>
          <option value="ACTIVE">Faol</option>
          <option value="BUSY">Band</option>
          <option value="OFFLINE">Offline</option>
        </select>
      </div>

      {loading ? (
        <p className="loading">Yuklanmoqda...</p>
      ) : filtered.length === 0 ? (
        <p className="empty-text">Shifokor topilmadi</p>
      ) : (
        <div className="doctors-grid">
          {filtered.map(d => (
            <div key={d.id} className="doctor-card">
              <div className="doctor-card-top">
                <div className="doctor-card-avatar">{getInitials(d.fullName)}</div>
                <div className="doctor-card-info">
                  <p className="doctor-card-name">{d.fullName}</p>
                  <p className="doctor-card-spec">{d.specialization}</p>
                </div>
                <span className={`doctor-status-badge ${statusClass[d.status] || "badge-offline"}`}>
                  {statusLabel[d.status] || d.status}
                </span>
              </div>

              <div className="doctor-rating">
                <Stars rating={d.rating} />
                <span className="rating-value">
                  {d.rating ? Number(d.rating).toFixed(1) : "0.0"}
                </span>
              </div>

              <div className="doctor-card-details">
                <div className="doctor-detail-row">
                  <span className="detail-label">Telefon:</span>
                  <span className="detail-value">{d.phone || "—"}</span>
                </div>
                <div className="doctor-detail-row">
                  <span className="detail-label">Tajriba:</span>
                  <span className="detail-value">
                    {d.experienceYears ? `${d.experienceYears} yil` : "—"}
                  </span>
                </div>
                <div className="doctor-detail-row">
                  <span className="detail-label">Ish vaqti:</span>
                  <span className="detail-value">
                    {d.workStartTime && d.workEndTime
                      ? `${d.workStartTime} – ${d.workEndTime}`
                      : "—"}
                  </span>
                </div>
                <div className="doctor-detail-row">
                  <span className="detail-label">Ish kunlari:</span>
                  <span className="detail-value">{d.workingDays || "—"}</span>
                </div>
              </div>

              <div className="doctor-card-actions">
                <button className="btn-edit" onClick={() => openEdit(d)}>Tahrirlash</button>
                <button className="btn-delete" onClick={() => handleDelete(d.id)}>O'chirish</button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Qo'shish / Tahrirlash modali */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>{editId ? "Shifokorni tahrirlash" : "Yangi shifokor qo'shish"}</h3>
            {error && <p className="error-msg">{error}</p>}

            {/* Faqat yangi qo'shishda ko'rinadi */}
            {!editId && (
              <>
                <div className="form-group">
                  <label>Ism Familiya</label>
                  <input
                    value={form.fullName}
                    onChange={e => setForm({ ...form, fullName: e.target.value })}
                    placeholder="Karimov Jasur"
                  />
                </div>

                <div className="credentials-preview">
                  <p className="credentials-title">Tizimga kirish ma'lumotlari (avtomatik)</p>
                  <div className="credential-row">
                    <span className="credential-label">Login:</span>
                    <span className="credential-value">DR{form.doctorId}@cliniq.uz</span>
                  </div>
                  <div className="credential-row">
                    <span className="credential-label">Parol:</span>
                    <span className="credential-value">{form.password}</span>
                  </div>
                </div>
              </>
            )}

            <div className="form-group">
              <label>Mutaxassislik</label>
              <select
                value={form.specialization}
                onChange={e => setForm({ ...form, specialization: e.target.value })}
              >
                <option value="">Tanlang...</option>
                {SPECIALIZATIONS.map(s => (
                  <option key={s} value={s}>{s}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Telefon</label>
              <input
                value={form.phone}
                onChange={e => setForm({ ...form, phone: e.target.value })}
                placeholder="+998 90 123 45 67"
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Litsenziya raqami</label>
                <input
                  value={form.licenseNumber}
                  onChange={e => setForm({ ...form, licenseNumber: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label>Tajriba (yil)</label>
                <input
                  type="number"
                  value={form.experienceYears}
                  onChange={e => setForm({ ...form, experienceYears: e.target.value })}
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Ish boshlanishi</label>
                <input
                  type="time"
                  value={form.workStartTime}
                  onChange={e => setForm({ ...form, workStartTime: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label>Ish tugashi</label>
                <input
                  type="time"
                  value={form.workEndTime}
                  onChange={e => setForm({ ...form, workEndTime: e.target.value })}
                />
              </div>
            </div>

            <div className="form-group">
              <label>Ish kunlari</label>
              <div className="days-picker">
                {DAYS.map(day => (
                  <button
                    key={day}
                    type="button"
                    className={`day-btn ${form.workingDays.includes(day) ? "day-btn-active" : ""}`}
                    onClick={() => toggleDay(day)}
                  >
                    {day}
                  </button>
                ))}
              </div>
            </div>

            <div className="form-group">
              <label>Holat</label>
              <select
                value={form.status}
                onChange={e => setForm({ ...form, status: e.target.value })}
              >
                <option value="ACTIVE">Faol</option>
                <option value="BUSY">Band</option>
                <option value="OFFLINE">Offline</option>
              </select>
            </div>

            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setShowModal(false)} disabled={saving}>
                Bekor qilish
              </button>
              <button className="btn-save" onClick={handleSave} disabled={saving}>
                {saving ? "Saqlanmoqda..." : "Saqlash"}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Muvaffaqiyat — login ma'lumotlari modali */}
      {credentials && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="credentials-success-icon">✓</div>
            <h3 className="credentials-success-title">Shifokor muvaffaqiyatli qo'shildi!</h3>
            <p className="credentials-success-sub">
              Quyidagi ma'lumotlarni shifokorga bering. Parolni keyinchalik o'zgartirishi mumkin.
            </p>

            <div className="credentials-box">
              <p className="credentials-box-label">Shifokor: <strong>{credentials.fullName}</strong></p>

              <div className="credentials-copy-row">
                <div>
                  <span className="credential-label">Login (Email):</span>
                  <span className="credential-value">{credentials.email}</span>
                </div>
                <button className="btn-copy" onClick={() => copyToClipboard(credentials.email)}>
                  Nusxa
                </button>
              </div>

              <div className="credentials-copy-row">
                <div>
                  <span className="credential-label">Parol:</span>
                  <span className="credential-value">{credentials.password}</span>
                </div>
                <button className="btn-copy" onClick={() => copyToClipboard(credentials.password)}>
                  Nusxa
                </button>
              </div>
            </div>

            <div className="modal-actions">
              <button className="btn-save" onClick={() => setCredentials(null)}>
                Tushunarli
              </button>
            </div>
          </div>
        </div>
      )}

    </Layout>
  );
}