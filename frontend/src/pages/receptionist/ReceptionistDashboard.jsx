import { useEffect, useState } from "react";
import { getToken, handleUnauthorized } from "../../api/apiClient";
import Layout from "../../components/layout/Layout";
import "../../styles/receptionist-dashboard.css";

const statusLabel = { PENDING: "Kutilmoqda", CONFIRMED: "Tasdiqlangan", COMPLETED: "Yakunlandi", CANCELLED: "Bekor qilindi" };
const statusColor = { PENDING: "badge-pending", CONFIRMED: "badge-confirmed", COMPLETED: "badge-completed", CANCELLED: "badge-cancelled" };
const emptyForm = { patientId: "", doctorId: "", serviceId: "", appointmentTime: "", notes: "" };

export default function ReceptionistDashboard() {
  const [appointments, setAppointments] = useState([]);
  const [patients, setPatients]   = useState([]);
  const [doctors, setDoctors]     = useState([]);
  const [services, setServices]   = useState([]);
  const [loading, setLoading]     = useState(true);
  const [activeTab, setActiveTab] = useState("today");
  const [showModal, setShowModal] = useState(false);
  const [form, setForm]           = useState(emptyForm);
  const [error, setError]         = useState("");

  useEffect(() => { fetchAll(); }, []);

  const authHeader = () => ({ Authorization: `Bearer ${getToken()}` });

  async function fetchAll() {
    setLoading(true);
    try {
      const [apptRes, patRes, docRes, svcRes] = await Promise.all([
        fetch("/api/appointments", { headers: authHeader() }),
        fetch("/api/patients",     { headers: authHeader() }),
        fetch("/api/doctors",      { headers: authHeader() }),
        fetch("/api/services",     { headers: authHeader() }),
      ]);
      if (apptRes.status === 401) { handleUnauthorized(); return; }
      const [appt, pat, doc, svc] = await Promise.all([
        apptRes.json(), patRes.json(), docRes.json(), svcRes.json()
      ]);
      setAppointments(appt.data || []);
      setPatients(pat.data || []);
      setDoctors(doc.data || []);
      setServices(svc.data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }

  async function handleBook() {
    if (!form.patientId || !form.doctorId || !form.serviceId || !form.appointmentTime) {
      setError("Barcha majburiy maydonlarni to'ldiring");
      return;
    }
    try {
      const res = await fetch("/api/appointments", {
        method: "POST",
        headers: { "Content-Type": "application/json", ...authHeader() },
        body: JSON.stringify({
          patientId: Number(form.patientId),
          doctorId:  Number(form.doctorId),
          serviceId: Number(form.serviceId),
          appointmentTime: form.appointmentTime,
          notes: form.notes,
        }),
      });
      if (!res.ok) { const e = await res.json(); setError(e.message || "Xato"); return; }
      setShowModal(false);
      fetchAll();
    } catch { setError("Server bilan aloqa yo'q"); }
  }

  async function handleStatus(id, status) {
    try {
      await fetch(`/api/appointments/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json", ...authHeader() },
        body: JSON.stringify({ status }),
      });
      fetchAll();
    } catch (err) { console.error(err); }
  }

  const today = appointments.filter(a => {
    const d = new Date(a.appointmentTime);
    return d.toDateString() === new Date().toDateString();
  });

  const displayed = activeTab === "today" ? today : appointments;

  return (
    <Layout>
      <div className="page-header">
        <div>
          <h2 className="dashboard-title">Qabulxona</h2>
          <p className="page-subtitle">{new Date().toLocaleDateString("uz-UZ", { weekday: "long", year: "numeric", month: "long", day: "numeric" })}</p>
        </div>
        <button className="btn-add" onClick={() => { setForm(emptyForm); setError(""); setShowModal(true); }}>
          + Qabul qo'shish
        </button>
      </div>

      {/* Stats */}
      <div className="doc-stats-grid">
        <div className="doc-stat-card">
          <p className="stat-label">Bugungi qabullar</p>
          <p className="stat-value">{today.length}</p>
          <p className="stat-sub">Bugun</p>
        </div>
        <div className="doc-stat-card">
          <p className="stat-label">Kutilayotgan</p>
          <p className="stat-value">{today.filter(a => a.status === "PENDING").length}</p>
          <p className="stat-sub">Bugun</p>
        </div>
        <div className="doc-stat-card">
          <p className="stat-label">Tasdiqlangan</p>
          <p className="stat-value">{today.filter(a => a.status === "CONFIRMED").length}</p>
          <p className="stat-sub">Bugun</p>
        </div>
        <div className="doc-stat-card">
          <p className="stat-label">Jami bemorlar</p>
          <p className="stat-value">{patients.length}</p>
          <p className="stat-sub">Ro'yxatda</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="doc-tabs">
        <button className={`doc-tab ${activeTab === "today" ? "doc-tab-active" : ""}`} onClick={() => setActiveTab("today")}>
          Bugungi ({today.length})
        </button>
        <button className={`doc-tab ${activeTab === "all" ? "doc-tab-active" : ""}`} onClick={() => setActiveTab("all")}>
          Barcha ({appointments.length})
        </button>
      </div>

      {loading ? <p className="loading">Yuklanmoqda...</p> : (
        <div className="table-wrapper">
          {displayed.length === 0 ? (
            <p className="empty-text">Qabullar mavjud emas</p>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>#</th>
                  <th>Bemor</th>
                  <th>Shifokor</th>
                  <th>Xizmat</th>
                  <th>Vaqt</th>
                  <th>Status</th>
                  <th>Amallar</th>
                </tr>
              </thead>
              <tbody>
                {displayed.map((a, i) => (
                  <tr key={a.id}>
                    <td>{i + 1}</td>
                    <td style={{ fontWeight: 500 }}>{a.patientName}</td>
                    <td>{a.doctorName}</td>
                    <td>{a.serviceName}</td>
                    <td>{new Date(a.appointmentTime).toLocaleString("uz-UZ")}</td>
                    <td>
                      <span className={`status-badge ${statusColor[a.status] || ""}`}>
                        {statusLabel[a.status] || a.status}
                      </span>
                    </td>
                    <td>
                      {a.status === "PENDING" && (
                        <button className="btn-confirm" onClick={() => handleStatus(a.id, "CONFIRMED")}>
                          Tasdiqlash
                        </button>
                      )}
                      {(a.status === "PENDING" || a.status === "CONFIRMED") && (
                        <button className="btn-cancel-sm" onClick={() => handleStatus(a.id, "CANCELLED")}>
                          Bekor
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}

      {/* Modal */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Qabul qo'shish</h3>
            {error && <p className="error-msg">{error}</p>}
            <div className="form-group">
              <label>Bemor *</label>
              <select value={form.patientId} onChange={e => setForm({ ...form, patientId: e.target.value })}>
                <option value="">— Tanlang —</option>
                {patients.map(p => <option key={p.id} value={p.id}>{p.fullName} ({p.phone})</option>)}
              </select>
            </div>
            <div className="form-group">
              <label>Shifokor *</label>
              <select value={form.doctorId} onChange={e => setForm({ ...form, doctorId: e.target.value })}>
                <option value="">— Tanlang —</option>
                {doctors.map(d => <option key={d.id} value={d.id}>{d.fullName} — {d.specialization}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label>Xizmat *</label>
              <select value={form.serviceId} onChange={e => setForm({ ...form, serviceId: e.target.value })}>
                <option value="">— Tanlang —</option>
                {services.map(s => <option key={s.id} value={s.id}>{s.name} — {s.price?.toLocaleString()} so'm</option>)}
              </select>
            </div>
            <div className="form-group">
              <label>Qabul vaqti *</label>
              <input type="datetime-local" value={form.appointmentTime} onChange={e => setForm({ ...form, appointmentTime: e.target.value })} />
            </div>
            <div className="form-group">
              <label>Izoh</label>
              <input type="text" placeholder="Qo'shimcha izoh..." value={form.notes} onChange={e => setForm({ ...form, notes: e.target.value })} />
            </div>
            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setShowModal(false)}>Bekor qilish</button>
              <button className="btn-save" onClick={handleBook}>Saqlash</button>
            </div>
          </div>
        </div>
      )}
    </Layout>
  );
}