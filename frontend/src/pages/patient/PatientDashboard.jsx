import { useEffect, useState } from "react";
import { getToken, handleUnauthorized } from "../../api/apiClient";
import Layout from "../../components/layout/Layout";
import "../../styles/patient-dashboard.css";

const statusLabel = { PENDING: "Kutilmoqda", CONFIRMED: "Tasdiqlangan", COMPLETED: "Yakunlandi", CANCELLED: "Bekor qilindi" };
const statusColor = { PENDING: "badge-pending", CONFIRMED: "badge-confirmed", COMPLETED: "badge-completed", CANCELLED: "badge-cancelled" };

const emptyForm = { doctorId: "", serviceId: "", appointmentTime: "" };

export default function PatientDashboard() {
  const [appointments, setAppointments] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");

  useEffect(() => { fetchAll(); }, []);

  async function fetchAll() {
    try {
      const [apptRes, docRes, svcRes] = await Promise.all([
        fetch("/api/patient/my-appointments", { headers: { Authorization: `Bearer ${getToken()}` } }),
        fetch("/api/doctors",                 { headers: { Authorization: `Bearer ${getToken()}` } }),
        fetch("/api/services",                { headers: { Authorization: `Bearer ${getToken()}` } }),
      ]);
      if (apptRes.status === 401) { handleUnauthorized(); return; }
      const apptData = await apptRes.json();
      const docData  = await docRes.json();
      const svcData  = await svcRes.json();
      setAppointments(apptData.data || []);
      setDoctors(docData.data || []);
      setServices(svcData.data || []);
    } catch (err) {
      console.error("Xatolik:", err);
    } finally {
      setLoading(false);
    }
  }

  async function cancelAppointment(id) {
    if (!window.confirm("Qabulni bekor qilmoqchimisiz?")) return;
    try {
      const res = await fetch(`/api/patient/appointments/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${getToken()}` },
      });
      if (res.status === 401) { handleUnauthorized(); return; }
      fetchAll();
    } catch (err) {
      console.error("Xatolik:", err);
    }
  }

  async function bookAppointment() {
    if (!form.doctorId || !form.serviceId || !form.appointmentTime) {
      setError("Barcha maydonlar majburiy");
      return;
    }
    try {
      const res = await fetch("/api/patient/appointments", {
        method: "POST",
        headers: { "Content-Type": "application/json", Authorization: `Bearer ${getToken()}` },
        body: JSON.stringify({
          doctorId: Number(form.doctorId),
          serviceId: Number(form.serviceId),
          appointmentTime: form.appointmentTime,
        }),
      });
      if (res.status === 401) { handleUnauthorized(); return; }
      if (!res.ok) {
        const err = await res.json();
        setError(err.message || "Xato yuz berdi");
        return;
      }
      setShowModal(false);
      fetchAll();
    } catch (err) {
      setError("Server bilan aloqa yo'q");
    }
  }

  const upcoming = appointments.filter(a => a.status === "PENDING" || a.status === "CONFIRMED");
  const past     = appointments.filter(a => a.status === "COMPLETED" || a.status === "CANCELLED");

  return (
    <Layout>
      <div className="page-header">
        <div>
          <h2 className="dashboard-title">Mening qabullarim</h2>
          <p className="page-subtitle">Jami {appointments.length} ta qabul</p>
        </div>
        <button className="btn-add" onClick={() => { setForm(emptyForm); setError(""); setShowModal(true); }}>
          + Qabul olish
        </button>
      </div>

      {/* Stats */}
      <div className="doc-stats-grid">
        <div className="doc-stat-card">
          <p className="stat-label">Kutilayotgan</p>
          <p className="stat-value">{upcoming.length}</p>
          <p className="stat-sub">Aktiv qabullar</p>
        </div>
        <div className="doc-stat-card">
          <p className="stat-label">Yakunlangan</p>
          <p className="stat-value">{appointments.filter(a => a.status === "COMPLETED").length}</p>
          <p className="stat-sub">Jami</p>
        </div>
        <div className="doc-stat-card">
          <p className="stat-label">Bekor qilingan</p>
          <p className="stat-value">{appointments.filter(a => a.status === "CANCELLED").length}</p>
          <p className="stat-sub">Jami</p>
        </div>
        <div className="doc-stat-card">
          <p className="stat-label">Jami qabullar</p>
          <p className="stat-value">{appointments.length}</p>
          <p className="stat-sub">Barcha vaqt</p>
        </div>
      </div>

      {loading ? (
        <p className="loading">Yuklanmoqda...</p>
      ) : appointments.length === 0 ? (
        <div className="table-wrapper">
          <p className="empty-text">Hali qabullar mavjud emas. "Qabul olish" tugmasini bosing!</p>
        </div>
      ) : (
        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Shifokor</th>
                <th>Xizmat</th>
                <th>Vaqt</th>
                <th>Status</th>
                <th>Amal</th>
              </tr>
            </thead>
            <tbody>
              {appointments.map((a, i) => (
                <tr key={a.id}>
                  <td>{i + 1}</td>
                  <td style={{ fontWeight: 500 }}>{a.doctorName}</td>
                  <td>{a.serviceName}</td>
                  <td>{new Date(a.appointmentTime).toLocaleString("uz-UZ")}</td>
                  <td>
                    <span className={`status-badge ${statusColor[a.status] || ""}`}>
                      {statusLabel[a.status] || a.status}
                    </span>
                  </td>
                  <td>
                    {a.status === "PENDING" && (
                      <button className="btn-delete" onClick={() => cancelAppointment(a.id)}>
                        Bekor qilish
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Modal */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Qabul olish</h3>
            {error && <p className="error-msg">{error}</p>}
            <div className="form-group">
              <label>Shifokor</label>
              <select value={form.doctorId} onChange={e => setForm({ ...form, doctorId: e.target.value })}>
                <option value="">— Tanlang —</option>
                {doctors.map(d => (
                  <option key={d.id} value={d.id}>{d.fullName} — {d.specialization}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Xizmat</label>
              <select value={form.serviceId} onChange={e => setForm({ ...form, serviceId: e.target.value })}>
                <option value="">— Tanlang —</option>
                {services.map(s => (
                  <option key={s.id} value={s.id}>{s.name} — {s.price?.toLocaleString()} so'm</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Qabul vaqti</label>
              <input
                type="datetime-local"
                value={form.appointmentTime}
                onChange={e => setForm({ ...form, appointmentTime: e.target.value })}
              />
            </div>
            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setShowModal(false)}>Bekor qilish</button>
              <button className="btn-save" onClick={bookAppointment}>Qabul olish</button>
            </div>
          </div>
        </div>
      )}
    </Layout>
  );
}