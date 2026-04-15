import { useState, useEffect } from "react";
import Layout from "../components/layout/Layout";
import { getDashboardData } from "../api/dashboard";
import "../styles/dashboard.css";

function getInitials(name) {
  if (!name) return "?";
  return name.split(" ").map(n => n[0]).join("").slice(0, 2).toUpperCase();
}

function formatTime(dateStr) {
  if (!dateStr) return "";
  return new Date(dateStr).toLocaleTimeString("uz-UZ", {
    hour: "2-digit", minute: "2-digit",
  });
}

function formatMoney(amount) {
  if (!amount && amount !== 0) return "0";
  return Number(amount).toLocaleString("uz-UZ");
}

export default function Dashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getDashboardData()
      .then(d => setData(d))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <Layout title="Dashboard"><p className="loading">Yuklanmoqda...</p></Layout>;
  }

  const today = new Date().toLocaleDateString("uz-UZ", {
    year: "numeric", month: "long", day: "numeric", weekday: "long",
  });

  return (
    <Layout title="Dashboard">

      {/* Sarlavha */}
      <div className="dashboard-header">
        <h2 className="dashboard-title">Dashboard</h2>
        <span className="dashboard-date">{today}</span>
      </div>

      {/* Statistika kartalar */}
      <div className="stats-grid">
        <div className="stat-card">
          <p className="stat-label">Bugungi bemorlar</p>
          <p className="stat-value">{data.todayPatients ?? 0}</p>
          <p className="stat-sub">Bugungi kun uchun</p>
        </div>
        <div className="stat-card blue">
          <p className="stat-label">Kutilayotgan navbatlar</p>
          <p className="stat-value">{data.pendingAppointments ?? 0}</p>
          <p className="stat-sub">Bugungi kun uchun</p>
        </div>
        <div className="stat-card green">
          <p className="stat-label">Oylik daromad</p>
          <p className="stat-value">{formatMoney(data.monthlyRevenue)} so'm</p>
          <p className="stat-sub">Joriy oy</p>
        </div>
        <div className="stat-card orange">
          <p className="stat-label">Yangi bemorlar</p>
          <p className="stat-value">{data.newPatientsToday ?? 0}</p>
          <p className="stat-sub">Bugun ro'yxatdan o'tgan</p>
        </div>
      </div>

      {/* O'rta qism */}
      <div className="middle-grid">

        {/* So'nggi to'lovlar */}
        <div className="recent-card">
          <h3>So'nggi to'lovlar</h3>
          {!data.recentPayments?.length ? (
            <p className="empty-text">To'lovlar mavjud emas</p>
          ) : (
            <table className="payment-table">
              <thead>
                <tr>
                  <th>Bemor</th>
                  <th>Xizmat</th>
                  <th>Miqdor</th>
                  <th>Holat</th>
                </tr>
              </thead>
              <tbody>
                {data.recentPayments.map(p => (
                  <tr key={p.id}>
                    <td>{p.patientName}</td>
                    <td className="payment-service">{p.serviceName || "—"}</td>
                    <td className="payment-amount">{formatMoney(p.amount)} so'm</td>
                    <td>
                      <span className={`payment-badge ${
                        p.status === "PAID" ? "badge-paid" :
                        p.status === "REFUNDED" ? "badge-refunded" : "badge-pending"
                      }`}>
                        {p.status === "PAID" ? "To'langan" :
                         p.status === "REFUNDED" ? "Qaytarilgan" : "Kutilmoqda"}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* O'ng ustun */}
        <div className="right-column">

          {/* Bugungi navbatlar */}
          <div className="recent-card">
            <h3>Bugungi navbatlar</h3>
            {!data.todayAppointments?.length ? (
              <p className="empty-text">Bugun navbat yo'q</p>
            ) : (
              data.todayAppointments.slice(0, 5).map(a => (
                <div key={a.id} className="today-appointment">
                  <span className="appt-time">{formatTime(a.appointmentTime)}</span>
                  <div>
                    <p className="appt-patient">{a.patientName}</p>
                    <p className="appt-doctor">{a.doctorName}</p>
                    <p className="appt-service">{a.serviceName}</p>
                  </div>
                </div>
              ))
            )}
          </div>

          {/* Dori-darmonlar */}
          <div className="recent-card">
            <h3>Dori-darmonlar tugab qolmoqda</h3>
            {!data.lowStockMedications?.length ? (
              <p className="empty-text">Hamma dorilar yetarli</p>
            ) : (
              data.lowStockMedications.map(m => (
                <div key={m.id} className="medication-item">
                  <div className="medication-row">
                    <span className="medication-name">{m.name}</span>
                    <span className="medication-qty">{m.quantity} {m.unit || "dona"}</span>
                  </div>
                  <p className="medication-min">Minimal: {m.minQuantity} {m.unit || "dona"}</p>
                </div>
              ))
            )}
          </div>

        </div>
      </div>

      {/* Shifokorlar holati */}
      <div className="recent-card">
        <h3>Shifokorlar holati</h3>
        <div className="doctors-grid">
          {data.doctors?.map(d => (
            <div key={d.id} className="doctor-status-card">
              <div className="doctor-status-header">
                <div className="doctor-avatar">{getInitials(d.fullName)}</div>
                <div>
                  <p className="doctor-status-name">{d.fullName}</p>
                  <p className="doctor-status-spec">{d.specialization}</p>
                </div>
              </div>
              <span className={`status-badge-doctor ${
                d.status === "ACTIVE" ? "badge-active" :
                d.status === "BUSY" ? "badge-busy" : "badge-offline"
              }`}>
                {d.status === "ACTIVE" ? "Faol" :
                 d.status === "BUSY" ? "Band" : "Offline"}
              </span>
            </div>
          ))}
        </div>
      </div>

    </Layout>
  );
}