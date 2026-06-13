import { useState, useEffect } from "react";
import Layout from "../../components/layout/Layout";
import {
  getMyAppointments,
  getMyPatients,
  updateAppointmentStatus,
  writeDiagnosis,
} from "../../api/doctorPanel";
import "../../styles/doctor-dashboard.css";

const STATUS_LABELS = {
  PENDING:   { label: "Kutmoqda",     cls: "badge-pending"   },
  CONFIRMED: { label: "Tasdiqlangan", cls: "badge-confirmed" },
  COMPLETED: { label: "Bajarildi",    cls: "badge-completed" },
  CANCELLED: { label: "Bekor",        cls: "badge-cancelled" },
};

function Avatar({ name, size = 34 }) {
  return (
    <div className="patient-avatar" style={{ width: size, height: size, fontSize: size * 0.38 }}>
      {name?.[0]?.toUpperCase() || "?"}
    </div>
  );
}

export default function DoctorDashboard() {
  const [appointments, setAppointments] = useState([]);
  const [patients, setPatients]         = useState([]);
  const [tab, setTab]                   = useState("appointments");
  const [loading, setLoading]           = useState(true);
  const [updatingId, setUpdatingId]     = useState(null);

  // Tashxis modal
  const [diagnoseModal, setDiagnoseModal] = useState(null); // { appointment }
  const [diagnoseForm, setDiagnoseForm]   = useState({ diagnosis: "", prescription: "", notes: "" });
  const [saving, setSaving]               = useState(false);
  const [error, setError]                 = useState("");

  useEffect(() => {
    Promise.all([getMyAppointments(), getMyPatients()])
      .then(([appts, pats]) => { setAppointments(appts); setPatients(pats); })
      .finally(() => setLoading(false));
  }, []);

  const handleStatus = async (id, status) => {
    setUpdatingId(id);
    try {
      const updated = await updateAppointmentStatus(id, status);
      setAppointments(prev => prev.map(a => a.id === id ? { ...a, ...updated } : a));
    } finally {
      setUpdatingId(null);
    }
  };

  const openDiagnose = (appt) => {
    setDiagnoseForm({
      diagnosis:    appt.diagnosis    || "",
      prescription: appt.prescription || "",
      notes:        appt.notes        || "",
    });
    setError("");
    setDiagnoseModal(appt);
  };

  const saveDiagnosis = async () => {
    if (!diagnoseForm.diagnosis.trim()) { setError("Tashxis kiritish majburiy!"); return; }
    setSaving(true);
    try {
      const updated = await writeDiagnosis(diagnoseModal.id, diagnoseForm);
      setAppointments(prev => prev.map(a => a.id === diagnoseModal.id ? { ...a, ...updated } : a));
      setDiagnoseModal(null);
    } catch {
      setError("Saqlashda xato yuz berdi");
    } finally {
      setSaving(false);
    }
  };

  const today = new Date().toDateString();
  const todayAppts     = appointments.filter(a => new Date(a.appointmentTime).toDateString() === today);
  const pendingCount   = appointments.filter(a => a.status === "PENDING").length;
  const completedCount = appointments.filter(a => a.status === "COMPLETED").length;

  const formatTime = (dt) => {
    if (!dt) return "—";
    const d = new Date(dt);
    return d.toLocaleDateString("uz-UZ") + " " + d.toLocaleTimeString("uz-UZ", { hour: "2-digit", minute: "2-digit" });
  };

  if (loading) return <Layout title="Shifokor paneli"><p className="loading">Yuklanmoqda...</p></Layout>;

  return (
    <Layout title="Shifokor paneli">

      {/* Sarlavha */}
      <div className="page-header">
        <div>
          <h2 className="dashboard-title">Shifokor paneli</h2>
          <p className="page-subtitle">Bugungi navbatlar va bemorlaringiz</p>
        </div>
      </div>

      {/* Stat kartalar */}
      <div className="doc-stats-grid">
        <div className="doc-stat-card">
          <p className="stat-label">Bugungi navbatlar</p>
          <p className="stat-value">{todayAppts.length}</p>
          <p className="stat-sub">bugun</p>
        </div>
        <div className="doc-stat-card">
          <p className="stat-label">Jami bemorlar</p>
          <p className="stat-value">{patients.length}</p>
          <p className="stat-sub">barcha vaqt</p>
        </div>
        <div className="doc-stat-card">
          <p className="stat-label">Bajarildi</p>
          <p className="stat-value">{completedCount}</p>
          <p className="stat-sub">jami navbatlardan</p>
        </div>
        <div className="doc-stat-card">
          <p className="stat-label">Kutmoqda</p>
          <p className="stat-value">{pendingCount}</p>
          <p className="stat-sub">tasdiqlash kerak</p>
        </div>
      </div>

      {/* Tablar */}
      <div className="doc-tabs">
        <button className={`doc-tab ${tab === "appointments" ? "doc-tab-active" : ""}`} onClick={() => setTab("appointments")}>
          📋 Navbatlar ({appointments.length})
        </button>
        <button className={`doc-tab ${tab === "patients" ? "doc-tab-active" : ""}`} onClick={() => setTab("patients")}>
          👥 Bemorlar ({patients.length})
        </button>
      </div>

      {/* Navbatlar jadvali */}
      {tab === "appointments" && (
        <div className="table-wrapper">
          {appointments.length === 0 ? (
            <p className="empty-text">Navbatlar mavjud emas</p>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>#</th>
                  <th>Bemor</th>
                  <th>Xizmat</th>
                  <th>Vaqt</th>
                  <th>Holat</th>
                  <th>Tashxis</th>
                  <th>Amal</th>
                </tr>
              </thead>
              <tbody>
                {appointments.map((a, i) => {
                  const s = STATUS_LABELS[a.status] || { label: a.status, cls: "" };
                  return (
                    <tr key={a.id}>
                      <td style={{ color: "var(--text-secondary)" }}>{i + 1}</td>
                      <td>
                        <div className="patient-name-cell">
                          <Avatar name={a.patientName} />
                          <span>{a.patientName}</span>
                        </div>
                      </td>
                      <td>{a.serviceName || "—"}</td>
                      <td style={{ color: "var(--text-secondary)", fontSize: "13px" }}>{formatTime(a.appointmentTime)}</td>
                      <td><span className={`status-badge ${s.cls}`}>{s.label}</span></td>
                      <td>
                        {a.diagnosis ? (
                          <span style={{ color: "#16A34A", fontSize: "12px" }}>✓ Yozilgan</span>
                        ) : (
                          <span style={{ color: "var(--text-secondary)", fontSize: "12px" }}>—</span>
                        )}
                      </td>
                      <td>
                        <div style={{ display: "flex", gap: "6px", flexWrap: "wrap" }}>
                          {a.status === "PENDING" && (
                            <>
                              <button onClick={() => handleStatus(a.id, "CONFIRMED")} disabled={updatingId === a.id}
                                style={{ padding: "5px 10px", background: "#DCFCE7", color: "#16A34A", border: "none", borderRadius: "6px", cursor: "pointer", fontSize: "12px", fontWeight: 500 }}>
                                ✓ Tasdiqlash
                              </button>
                              <button onClick={() => handleStatus(a.id, "CANCELLED")} disabled={updatingId === a.id}
                                style={{ padding: "5px 10px", background: "#FEF2F2", color: "#EF4444", border: "none", borderRadius: "6px", cursor: "pointer", fontSize: "12px" }}>
                                ✗ Bekor
                              </button>
                            </>
                          )}
                          {a.status === "CONFIRMED" && (
                            <button onClick={() => handleStatus(a.id, "COMPLETED")} disabled={updatingId === a.id}
                              style={{ padding: "5px 12px", background: "#EFF6FF", color: "#2563EB", border: "none", borderRadius: "6px", cursor: "pointer", fontSize: "12px", fontWeight: 500 }}>
                              ✓ Yakunlash
                            </button>
                          )}
                          {(a.status === "CONFIRMED" || a.status === "COMPLETED") && (
                            <button onClick={() => openDiagnose(a)}
                              style={{ padding: "5px 10px", background: "#F5F3FF", color: "#8B5CF6", border: "none", borderRadius: "6px", cursor: "pointer", fontSize: "12px", fontWeight: 500 }}>
                              🩺 {a.diagnosis ? "Tahrirlash" : "Tashxis"}
                            </button>
                          )}
                          {a.status === "CANCELLED" && (
                            <span style={{ color: "var(--text-secondary)", fontSize: "12px" }}>—</span>
                          )}
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          )}
        </div>
      )}

      {/* Bemorlar jadvali */}
      {tab === "patients" && (
        <div className="table-wrapper">
          {patients.length === 0 ? (
            <p className="empty-text">Bemorlar mavjud emas</p>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>#</th>
                  <th>Bemor</th>
                  <th>Telefon</th>
                  <th>Tug'ilgan sana</th>
                  <th>So'nggi tashrif</th>
                  <th>Jami tashriflar</th>
                </tr>
              </thead>
              <tbody>
                {patients.map((p, i) => (
                  <tr key={p.id}>
                    <td style={{ color: "var(--text-secondary)" }}>{i + 1}</td>
                    <td>
                      <div className="patient-name-cell">
                        <Avatar name={p.fullName} />
                        <div>
                          <div>{p.fullName}</div>
                          {p.email && <div style={{ fontSize: "12px", color: "var(--text-secondary)" }}>{p.email}</div>}
                        </div>
                      </div>
                    </td>
                    <td>{p.phone || "—"}</td>
                    <td style={{ color: "var(--text-secondary)" }}>{p.birthDate || "—"}</td>
                    <td style={{ color: "var(--text-secondary)" }}>{p.lastVisitDate || "—"}</td>
                    <td>
                      <span style={{ backgroundColor: "#EFF6FF", color: "#2563EB", padding: "3px 10px", borderRadius: "12px", fontSize: "12px", fontWeight: 600 }}>
                        {p.totalVisits ?? 0} ta
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}

      {/* Tashxis Modal */}
      {diagnoseModal && (
        <div className="modal-overlay" onClick={() => setDiagnoseModal(null)}>
          <div className="modal" style={{ width: "520px" }} onClick={e => e.stopPropagation()}>
            <h3>🩺 Tashxis yozish</h3>
            <p style={{ color: "var(--text-secondary)", fontSize: "13px", marginTop: "-12px", marginBottom: "20px" }}>
              Bemor: <strong style={{ color: "var(--text-primary)" }}>{diagnoseModal.patientName}</strong>
              &nbsp;·&nbsp; Xizmat: <strong style={{ color: "var(--text-primary)" }}>{diagnoseModal.serviceName}</strong>
            </p>

            {error && <p className="error-msg">{error}</p>}

            <div className="form-group">
              <label>Tashxis *</label>
              <input
                value={diagnoseForm.diagnosis}
                onChange={e => { setDiagnoseForm(f => ({ ...f, diagnosis: e.target.value })); setError(""); }}
                placeholder="Masalan: O'tkir bronxit, J20.9"
              />
            </div>

            <div className="form-group">
              <label>Dori-darmon (retsept)</label>
              <textarea
                value={diagnoseForm.prescription}
                onChange={e => setDiagnoseForm(f => ({ ...f, prescription: e.target.value }))}
                placeholder={"Masalan:\n1. Amoksitsillin 500mg — 3 marta, 7 kun\n2. Paratsetamol 500mg — zarur bo'lganda"}
                rows={5}
                style={{
                  width: "100%", padding: "10px 12px",
                  border: "1px solid var(--border)", borderRadius: "8px",
                  fontSize: "14px", boxSizing: "border-box",
                  background: "var(--input-bg)", color: "var(--text-primary)",
                  resize: "vertical", fontFamily: "inherit",
                }}
              />
            </div>

            <div className="form-group">
              <label>Izoh</label>
              <input
                value={diagnoseForm.notes}
                onChange={e => setDiagnoseForm(f => ({ ...f, notes: e.target.value }))}
                placeholder="Qo'shimcha izohlar..."
              />
            </div>

            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setDiagnoseModal(null)}>Bekor</button>
              <button className="btn-save" onClick={saveDiagnosis} disabled={saving}>
                {saving ? "Saqlanmoqda..." : "💾 Saqlash"}
              </button>
            </div>
          </div>
        </div>
      )}

    </Layout>
  );
}