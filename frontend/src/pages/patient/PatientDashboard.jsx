import { useState, useEffect } from "react";
import Layout from "../../components/layout/Layout";
import { getMyAppointments, bookAppointment, cancelAppointment, getAvailableSlots } from "../../api/patientPanel";
import { getAllDoctors } from "../../api/doctors";
import { getAllServices } from "../../api/services";
import "../../styles/patient-dashboard.css";

const STATUS_LABELS = {
  PENDING:   { label: "Kutmoqda",     cls: "badge-pending"   },
  CONFIRMED: { label: "Tasdiqlangan", cls: "badge-confirmed" },
  COMPLETED: { label: "Bajarildi",    cls: "badge-completed" },
  CANCELLED: { label: "Bekor",        cls: "badge-cancelled" },
};

const FILTERS = [
  { key: "ALL",       label: "Barchasi"     },
  { key: "PENDING",   label: "Kutmoqda"     },
  { key: "CONFIRMED", label: "Tasdiqlangan" },
  { key: "COMPLETED", label: "Bajarildi"    },
  { key: "CANCELLED", label: "Bekor"        },
];

const MONTH_NAMES = ["Yanvar","Fevral","Mart","Aprel","May","Iyun",
                     "Iyul","Avgust","Sentabr","Oktabr","Noyabr","Dekabr"];
const DAY_HEADERS = ["Du","Se","Ch","Pa","Ju","Sh","Ya"];

function BookingCalendar({ doctor, onDateSelect, selectedDate }) {
  const [calDate, setCalDate] = useState(new Date());

  const isWorkingDay = (date) => {
    if (!doctor?.workingDays) return true;
    const nums = doctor.workingDays.split(",")
      .map(d => parseInt(d.trim())).filter(n => !isNaN(n));
    if (nums.length === 0) return true;
    const jsDay  = date.getDay();
    const javaDay = jsDay === 0 ? 7 : jsDay;
    return nums.includes(javaDay);
  };

  const today = new Date(); today.setHours(0, 0, 0, 0);
  const year  = calDate.getFullYear();
  const month = calDate.getMonth();
  const daysInMonth  = new Date(year, month + 1, 0).getDate();
  const firstDayOfMonth = new Date(year, month, 1);
  const startOffset  = (firstDayOfMonth.getDay() + 6) % 7; // 0=Mon

  const cells = [];
  for (let i = 0; i < startOffset; i++) cells.push(null);
  for (let d = 1; d <= daysInMonth; d++) cells.push(new Date(year, month, d));

  const navBtn = {
    background: "none", border: "1px solid var(--border)",
    borderRadius: "6px", cursor: "pointer", color: "var(--text-primary)",
    width: "28px", height: "28px", display: "flex",
    alignItems: "center", justifyContent: "center", fontSize: "16px",
  };

  return (
    <div style={{ userSelect: "none" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "10px" }}>
        <button style={navBtn} onClick={() => setCalDate(new Date(year, month - 1, 1))}>‹</button>
        <span style={{ fontWeight: 600, color: "var(--text-primary)", fontSize: "14px" }}>
          {MONTH_NAMES[month]} {year}
        </span>
        <button style={navBtn} onClick={() => setCalDate(new Date(year, month + 1, 1))}>›</button>
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: "2px", marginBottom: "6px" }}>
        {DAY_HEADERS.map(d => (
          <div key={d} style={{ textAlign: "center", fontSize: "11px",
            color: "var(--text-secondary)", fontWeight: 700, padding: "3px 0" }}>
            {d}
          </div>
        ))}
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: "3px" }}>
        {cells.map((date, i) => {
          if (!date) return <div key={`e-${i}`} />;
          const isPast      = date < today;
          const isWorking   = isWorkingDay(date);
          const isSelected  = selectedDate?.toDateString() === date.toDateString();
          const isToday     = date.toDateString() === today.toDateString();

          let bg, color, border, cursor;
          if (isSelected) {
            bg = "#0D7377"; color = "white"; border = "2px solid #0D7377"; cursor = "pointer";
          } else if (isPast) {
            bg = "transparent"; color = "var(--text-secondary)";
            border = "1px solid transparent"; cursor = "not-allowed"; 
          } else if (!isWorking) {
            bg = "rgba(245,158,11,0.12)"; color = "#F59E0B";
            border = "1px solid rgba(245,158,11,0.25)"; cursor = "not-allowed";
          } else {
            bg = "rgba(16,185,129,0.12)"; color = "#10B981";
            border = "1px solid rgba(16,185,129,0.25)"; cursor = "pointer";
          }

          return (
            <button
              key={i}
              disabled={isPast || !isWorking}
              onClick={() => onDateSelect(date)}
              style={{
                background: bg, color, border, cursor,
                borderRadius: "6px", padding: "7px 2px",
                fontSize: "12px", fontWeight: isToday ? 700 : 400,
                outline: "none", transition: "all 0.15s",
              }}
            >
              {date.getDate()}
            </button>
          );
        })}
      </div>

      <div style={{ display: "flex", gap: "14px", marginTop: "10px", fontSize: "11px", color: "var(--text-secondary)" }}>
        <span style={{ display: "flex", alignItems: "center", gap: "5px" }}>
          <span style={{ width: "10px", height: "10px", borderRadius: "3px",
            background: "rgba(16,185,129,0.3)", display: "inline-block" }}/>
          Bo'sh kunlar
        </span>
        <span style={{ display: "flex", alignItems: "center", gap: "5px" }}>
          <span style={{ width: "10px", height: "10px", borderRadius: "3px",
            background: "rgba(245,158,11,0.3)", display: "inline-block" }}/>
          Ish kuni emas
        </span>
      </div>
    </div>
  );
}

const EMPTY_FORM = { doctorId: "", serviceId: "", appointmentTime: "", notes: "" };

export default function PatientDashboard() {
  const [appointments, setAppointments] = useState([]);
  const [doctors, setDoctors]           = useState([]);
  const [services, setServices]         = useState([]);
  const [loading, setLoading]           = useState(true);
  const [showModal, setShowModal]       = useState(false);
  const [form, setForm]                 = useState(EMPTY_FORM);
  const [saving, setSaving]             = useState(false);
  const [error, setError]               = useState("");
  const [cancelingId, setCancelingId]   = useState(null);
  const [filter, setFilter]             = useState("ALL");
  const [viewAppt, setViewAppt]         = useState(null);
  const [selectedDate, setSelectedDate] = useState(null);
  const [availableSlots, setAvailableSlots] = useState([]);
  const [selectedSlot, setSelectedSlot]     = useState(null);

  useEffect(() => {
    Promise.all([getMyAppointments(), getAllDoctors(), getAllServices()])
      .then(([appts, docs, svcs]) => {
        setAppointments(appts);
        setDoctors(docs.filter ? docs.filter(d => d.active !== false) : docs);
        setServices(svcs.filter ? svcs.filter(s => s.active !== false) : svcs);
      })
      .finally(() => setLoading(false));
  }, []);

  const openModal = () => {
    setForm(EMPTY_FORM);
    setError("");
    setSelectedDate(null);
    setAvailableSlots([]);
    setSelectedSlot(null);
    setShowModal(true);
  };

  const handleDoctorChange = (doctorId) => {
    setForm(f => ({ ...f, doctorId, appointmentTime: "" }));
    setSelectedDate(null);
    setAvailableSlots([]);
    setSelectedSlot(null);
    setError("");
  };

  const handleDateSelect = async (date) => {
  setSelectedDate(date);
  setSelectedSlot(null);
  setAvailableSlots(null);
  setForm(f => ({ ...f, appointmentTime: "" }));
  if (!form.doctorId) return;
  try {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");
    const slots = await getAvailableSlots(form.doctorId, `${y}-${m}-${d}`);
    setAvailableSlots(slots || []);
  } catch (e) {
    console.error("Slots xatosi:", e);
    setAvailableSlots([]);
  }
};

  const handleSlotSelect = (slot) => {
    setSelectedSlot(slot);
    const d = selectedDate;
    const dateStr = `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,"0")}-${String(d.getDate()).padStart(2,"0")}`;
    setForm(f => ({ ...f, appointmentTime: `${dateStr}T${slot}` }));
  };

  const handleBook = async () => {
    if (!form.doctorId)        { setError("Shifokorni tanlang");    return; }
    if (!form.serviceId)       { setError("Xizmatni tanlang");      return; }
    if (!selectedDate)         { setError("Sanani tanlang");        return; }
    if (!selectedSlot)         { setError("Vaqtni tanlang");        return; }
    setSaving(true);
    try {
      const newAppt = await bookAppointment({
        doctorId:        Number(form.doctorId),
        serviceId:       Number(form.serviceId),
        appointmentTime: form.appointmentTime,
        notes:           form.notes || null,
      });
      setAppointments(prev => [newAppt, ...prev]);
      setShowModal(false);
    } catch (e) {
      setError(e.message || "Xato yuz berdi");
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = async (id) => {
    if (!window.confirm("Navbatni bekor qilmoqchimisiz?")) return;
    setCancelingId(id);
    try {
      await cancelAppointment(id);
      setAppointments(prev => prev.map(a => a.id === id ? { ...a, status: "CANCELLED" } : a));
    } finally {
      setCancelingId(null);
    }
  };

  const total     = appointments.length;
  const pending   = appointments.filter(a => a.status === "PENDING").length;
  const completed = appointments.filter(a => a.status === "COMPLETED").length;
  const cancelled = appointments.filter(a => a.status === "CANCELLED").length;
  const filtered  = filter === "ALL" ? appointments : appointments.filter(a => a.status === filter);

  const formatTime = (dt) => {
    if (!dt) return "—";
    const d = new Date(dt);
    return d.toLocaleDateString("uz-UZ") + " " + d.toLocaleTimeString("uz-UZ", { hour: "2-digit", minute: "2-digit" });
  };

  if (loading) return <Layout title="Bemor paneli"><p className="loading">Yuklanmoqda...</p></Layout>;

  return (
    <Layout title="Bemor paneli">

      <div className="page-header">
        <div>
          <h2 className="dashboard-title">Bemor paneli</h2>
          <p className="page-subtitle">Navbatlaringiz va tarixingiz</p>
        </div>
        <button className="btn-add" onClick={openModal}>+ Navbat olish</button>
      </div>

      <div className="doc-stats-grid">
        <div className="doc-stat-card">
          <p className="stat-label">Jami navbatlar</p>
          <p className="stat-value">{total}</p>
          <p className="stat-sub">barcha vaqt</p>
        </div>
        <div className="doc-stat-card">
          <p className="stat-label">Kutmoqda</p>
          <p className="stat-value">{pending}</p>
          <p className="stat-sub">tasdiqlash kutilmoqda</p>
        </div>
        <div className="doc-stat-card">
          <p className="stat-label">Bajarildi</p>
          <p className="stat-value">{completed}</p>
          <p className="stat-sub">muvaffaqiyatli</p>
        </div>
        <div className="doc-stat-card">
          <p className="stat-label">Bekor qilingan</p>
          <p className="stat-value">{cancelled}</p>
          <p className="stat-sub">bekor qilingan</p>
        </div>
      </div>

      <div className="doc-tabs" style={{ marginBottom: "16px" }}>
        {FILTERS.map(f => (
          <button
            key={f.key}
            className={`doc-tab ${filter === f.key ? "doc-tab-active" : ""}`}
            onClick={() => setFilter(f.key)}
          >
            {f.label}
            {f.key !== "ALL" && (
              <span style={{
                marginLeft: "6px", fontSize: "11px",
                background: filter === f.key ? "#0D7377" : "var(--border)",
                color: filter === f.key ? "white" : "var(--text-secondary)",
                padding: "1px 7px", borderRadius: "10px",
              }}>
                {appointments.filter(a => a.status === f.key).length}
              </span>
            )}
          </button>
        ))}
      </div>

      <div className="table-wrapper">
        {filtered.length === 0 ? (
          <div className="empty-text">
            <p>{filter === "ALL" ? "Hali navbat yo'q" : "Bu statusda navbat yo'q"}</p>
            {filter === "ALL" && (
              <button className="btn-add" onClick={openModal} style={{ marginTop: "12px" }}>
                + Birinchi navbatni olish
              </button>
            )}
          </div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Shifokor</th>
                <th>Xizmat</th>
                <th>Vaqt</th>
                <th>Holat</th>
                <th>Tashxis</th>
                <th>Amal</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((a, i) => {
                const s = STATUS_LABELS[a.status] || { label: a.status, cls: "" };
                return (
                  <tr key={a.id}>
                    <td style={{ color: "var(--text-secondary)" }}>{i + 1}</td>
                    <td style={{ fontWeight: 500 }}>{a.doctorName || "—"}</td>
                    <td>{a.serviceName || "—"}</td>
                    <td style={{ color: "var(--text-secondary)", fontSize: "13px" }}>
                      {formatTime(a.appointmentTime)}
                    </td>
                    <td><span className={`status-badge ${s.cls}`}>{s.label}</span></td>
                    <td>
                      {a.diagnosis ? (
                        <button onClick={() => setViewAppt(a)} style={{
                          background: "none", border: "none", cursor: "pointer",
                          color: "#0D7377", fontSize: "13px", padding: 0, textAlign: "left",
                        }}>
                          <span style={{ fontWeight: 500 }}>✓ Ko'rish</span>
                          <span style={{ display: "block", color: "var(--text-secondary)", fontSize: "11px" }}>
                            {a.diagnosis.substring(0, 25)}{a.diagnosis.length > 25 ? "..." : ""}
                          </span>
                        </button>
                      ) : (
                        <span style={{ color: "var(--text-secondary)", fontSize: "12px" }}>—</span>
                      )}
                    </td>
                    <td>
                      {(a.status === "PENDING" || a.status === "CONFIRMED") ? (
                        <button onClick={() => handleCancel(a.id)}
                          disabled={cancelingId === a.id} className="btn-delete">
                          {cancelingId === a.id ? "..." : "Bekor qilish"}
                        </button>
                      ) : (
                        <span style={{ color: "var(--text-secondary)", fontSize: "12px" }}>—</span>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>

      {/* Tashxis ko'rish modali */}
      {viewAppt && (
        <div className="modal-overlay" onClick={() => setViewAppt(null)}>
          <div className="modal" style={{ width: "500px" }} onClick={e => e.stopPropagation()}>
            <h3>🩺 Tashxis ma'lumotlari</h3>
            <div style={{ display: "flex", gap: "12px", marginBottom: "20px", flexWrap: "wrap" }}>
              <span style={{ background: "var(--hover-bg)", padding: "4px 12px", borderRadius: "20px", fontSize: "13px", color: "var(--text-secondary)" }}>
                👨‍⚕️ {viewAppt.doctorName}
              </span>
              <span style={{ background: "var(--hover-bg)", padding: "4px 12px", borderRadius: "20px", fontSize: "13px", color: "var(--text-secondary)" }}>
                📋 {viewAppt.serviceName}
              </span>
              <span style={{ background: "var(--hover-bg)", padding: "4px 12px", borderRadius: "20px", fontSize: "13px", color: "var(--text-secondary)" }}>
                📅 {formatTime(viewAppt.appointmentTime)}
              </span>
            </div>
            <div style={{ marginBottom: "16px" }}>
              <p style={{ fontSize: "12px", color: "var(--text-secondary)", marginBottom: "6px", fontWeight: 600, textTransform: "uppercase" }}>Tashxis</p>
              <div style={{ background: "var(--hover-bg)", padding: "12px 16px", borderRadius: "8px", fontSize: "14px", borderLeft: "3px solid #0D7377" }}>
                {viewAppt.diagnosis}
              </div>
            </div>
            {viewAppt.prescription && (
              <div style={{ marginBottom: "16px" }}>
                <p style={{ fontSize: "12px", color: "var(--text-secondary)", marginBottom: "6px", fontWeight: 600, textTransform: "uppercase" }}>💊 Retsept</p>
                <div style={{ background: "var(--hover-bg)", padding: "12px 16px", borderRadius: "8px", fontSize: "14px", whiteSpace: "pre-line", borderLeft: "3px solid #8B5CF6" }}>
                  {viewAppt.prescription}
                </div>
              </div>
            )}
            {viewAppt.notes && (
              <div style={{ marginBottom: "16px" }}>
                <p style={{ fontSize: "12px", color: "var(--text-secondary)", marginBottom: "6px", fontWeight: 600, textTransform: "uppercase" }}>Izoh</p>
                <div style={{ background: "var(--hover-bg)", padding: "12px 16px", borderRadius: "8px", fontSize: "13px", borderLeft: "3px solid #F59E0B" }}>
                  {viewAppt.notes}
                </div>
              </div>
            )}
            <div className="modal-actions">
              <button className="btn-save" onClick={() => setViewAppt(null)}>Yopish</button>
            </div>
          </div>
        </div>
      )}

      {/* Navbat olish modali */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" style={{ maxWidth: "520px", width: "100%" }} onClick={e => e.stopPropagation()}>
            <h3>📅 Navbat olish</h3>
            {error && <p className="error-msg">{error}</p>}

            {/* 1. Shifokor */}
            <div className="form-group">
              <label>Shifokor *</label>
              <select value={form.doctorId} onChange={e => handleDoctorChange(e.target.value)}>
                <option value="">— Shifokorni tanlang —</option>
                {doctors.map(d => (
                  <option key={d.id} value={d.id}>
                    {d.fullName} {d.specialization ? `(${d.specialization})` : ""}
                  </option>
                ))}
              </select>
              {form.doctorId && (() => {
                const doc = doctors.find(d => String(d.id) === String(form.doctorId));
                return doc?.workStartTime ? (
                  <div style={{ background: "var(--hover-bg)", borderRadius: "8px", padding: "8px 12px", fontSize: "12px", color: "var(--text-secondary)", marginTop: "6px" }}>
                    ⏰ Ish vaqti: <strong style={{ color: "var(--text-primary)" }}>{doc.workStartTime} — {doc.workEndTime}</strong>
                    <span style={{ color: "#F59E0B", marginLeft: "8px" }}>· Oxirgi navbat: {doc.workEndTime ? (() => {
                      const [h, m] = doc.workEndTime.split(":").map(Number);
                      const total = h * 60 + m - 30;
                      return `${String(Math.floor(total/60)).padStart(2,"0")}:${String(total%60).padStart(2,"0")}`;
                    })() : ""}</span>
                  </div>
                ) : null;
              })()}
            </div>

            {/* 2. Xizmat */}
            <div className="form-group">
              <label>Xizmat *</label>
              <select value={form.serviceId} onChange={e => { setForm(f => ({ ...f, serviceId: e.target.value })); setError(""); }}>
                <option value="">— Xizmatni tanlang —</option>
                {services.map(s => (
                  <option key={s.id} value={s.id}>
                    {s.name} {s.price ? `— ${Number(s.price).toLocaleString()} so'm` : ""}
                  </option>
                ))}
              </select>
            </div>

            {/* 3. Kalendar */}
            {form.doctorId && (
              <div className="form-group">
                <label>Sana tanlang *</label>
                <div style={{ background: "var(--hover-bg)", borderRadius: "10px", padding: "14px" }}>
                  <BookingCalendar
                    doctor={doctors.find(d => String(d.id) === String(form.doctorId))}
                    onDateSelect={handleDateSelect}
                    selectedDate={selectedDate}
                  />
                </div>
              </div>
            )}

            {/* 4. Vaqt slotlari */}
            {selectedDate && (
              <div className="form-group">
                <label>
                  Vaqt tanlang *
                  <span style={{ fontWeight: 400, color: "var(--text-secondary)", marginLeft: "8px", fontSize: "12px" }}>
                    {selectedDate.toLocaleDateString("uz-UZ")}
                  </span>
                </label>
                {availableSlots === null ? (
                  <p style={{ color: "var(--text-secondary)", fontSize: "13px" }}>⏳ Vaqtlar yuklanmoqda...</p>
                ) : availableSlots.length === 0 ? (
                  <div style={{
                    background: "rgba(239,68,68,0.08)", border: "1px solid rgba(239,68,68,0.2)",
                    borderRadius: "8px", padding: "10px 14px", fontSize: "13px", color: "#EF4444"
                  }}>
                    Bu kunda barcha vaqtlar band yoki mavjud emas
                  </div>
                ) : (
                  <div style={{ display: "flex", flexWrap: "wrap", gap: "8px" }}>
                    {availableSlots.map(slot => (
                      <button
                        key={slot}
                        onClick={() => handleSlotSelect(slot)}
                        style={{
                          padding: "6px 14px", borderRadius: "8px", fontSize: "13px",
                          cursor: "pointer", fontWeight: selectedSlot === slot ? 600 : 400,
                          background: selectedSlot === slot ? "#0D7377" : "var(--hover-bg)",
                          color: selectedSlot === slot ? "white" : "var(--text-primary)",
                          border: selectedSlot === slot ? "2px solid #0D7377" : "1px solid var(--border)",
                          transition: "all 0.15s",
                        }}
                      >
                        {slot}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* 5. Izoh */}
            <div className="form-group">
              <label>Izoh (ixtiyoriy)</label>
              <textarea
                rows={2}
                placeholder="Qo'shimcha ma'lumot..."
                value={form.notes}
                onChange={e => setForm(f => ({ ...f, notes: e.target.value }))}
                style={{ resize: "vertical" }}
              />
            </div>

            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setShowModal(false)}>Bekor</button>
              <button className="btn-save" onClick={handleBook} disabled={saving}>
                {saving ? "Saqlanmoqda..." : "✓ Navbat olish"}
              </button>
            </div>
          </div>
        </div>
      )}
    </Layout>
  );
}