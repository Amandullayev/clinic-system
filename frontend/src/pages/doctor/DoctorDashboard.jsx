import { useEffect, useState } from "react";
import { getToken, handleUnauthorized } from "../../api/apiClient";
import Layout from "../../components/layout/Layout";
import "../../styles/doctor-dashboard.css";

const statusLabel = { PENDING: "Kutilmoqda", CONFIRMED: "Tasdiqlangan", COMPLETED: "Yakunlandi", CANCELLED: "Bekor qilindi" };
const statusColor = { PENDING: "badge-pending", CONFIRMED: "badge-confirmed", COMPLETED: "badge-completed", CANCELLED: "badge-cancelled" };

export default function DoctorDashboard() {
  const [appointments, setAppointments] = useState([]);
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState("appointments");

  useEffect(() => { fetchData(); }, []);

  async function fetchData() {
    try {
      const [apptRes, patRes] = await Promise.all([
        fetch("/api/doctor/my-appointments", { headers: { Authorization: `Bearer ${getToken()}` } }),
        fetch("/api/doctor/my-patients",     { headers: { Authorization: `Bearer ${getToken()}` } }),
      ]);
      if (apptRes.status === 401 || patRes.status === 401) { handleUnauthorized(); return; }
      const apptData = await apptRes.json();
      const patData  = await patRes.json();
      setAppointments(apptData.data || []);
      setPatients(patData.data || []);
    } catch (err) {
      console.error("Xatolik:", err);
    } finally {
      setLoading(false);
    }
  }

  const today = appointments.filter(a => {
    const d = new Date(a.appointmentTime);
    const now = new Date();
    return d.toDateString() === now.toDateString();
  });

  return (
    <Layout>
      <div className="page-header">
        <div>
          <h2 className="dashboard-title">Shifokor paneli</h2>
          <p className="page-subtitle">{new Date().toLocaleDateString("uz-UZ", { weekday: "long", year: "numeric", month: "long", day: "numeric" })}</p>
        </div>
      </div>

      {/* Stats */}
      <div className="doc-stats-grid">
        <div className="doc-stat-card">
          <p className="stat-label">Jami qabullar</p>
          <p className="stat-value">{appointments.length}</p>
          <p className="stat-sub">Barcha vaqt</p>
        </div>
        <div className="doc-stat-card">
          <p className="stat-label">Bugungi qabullar</p>
          <p className="stat-value">{today.length}</p>
          <p className="stat-sub">Bugun</p>
        </div>
        <div className="doc-stat-card">
          <p className="stat-label">Mening bemorlarim</p>
          <p className="stat-value">{patients.length}</p>
          <p className="stat-sub">Jami</p>
        </div>
        <div className="doc-stat-card">
          <p className="stat-label">Yakunlangan</p>
          <p className="stat-value">{appointments.filter(a => a.status === "COMPLETED").length}</p>
          <p className="stat-sub">Qabullar</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="doc-tabs">
        <button
          className={`doc-tab ${activeTab === "appointments" ? "doc-tab-active" : ""}`}
          onClick={() => setActiveTab("appointments")}
        >
          Qabullarim ({appointments.length})
        </button>
        <button
          className={`doc-tab ${activeTab === "patients" ? "doc-tab-active" : ""}`}
          onClick={() => setActiveTab("patients")}
        >
          Bemorlarim ({patients.length})
        </button>
      </div>

      {loading ? (
        <p className="loading">Yuklanmoqda...</p>
      ) : (
        <div className="table-wrapper">
          {activeTab === "appointments" ? (
            appointments.length === 0 ? (
              <p className="empty-text">Qabullar mavjud emas</p>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Bemor</th>
                    <th>Xizmat</th>
                    <th>Vaqt</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {appointments.map((a, i) => (
                    <tr key={a.id}>
                      <td>{i + 1}</td>
                      <td style={{ fontWeight: 500 }}>{a.patientName}</td>
                      <td>{a.serviceName}</td>
                      <td>{new Date(a.appointmentTime).toLocaleString("uz-UZ")}</td>
                      <td>
                        <span className={`status-badge ${statusColor[a.status] || ""}`}>
                          {statusLabel[a.status] || a.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )
          ) : (
            patients.length === 0 ? (
              <p className="empty-text">Bemorlar mavjud emas</p>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Ism</th>
                    <th>Telefon</th>
                    <th>Email</th>
                  </tr>
                </thead>
                <tbody>
                  {patients.map((p, i) => (
                    <tr key={p.id}>
                      <td>{i + 1}</td>
                      <td>
                        <div className="patient-name-cell">
                          <div className="patient-avatar">{p.fullName?.charAt(0)}</div>
                          {p.fullName}
                        </div>
                      </td>
                      <td>{p.phone || "—"}</td>
                      <td>{p.email || "—"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )
          )}
        </div>
      )}
    </Layout>
  );
}