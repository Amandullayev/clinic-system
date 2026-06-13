import { useEffect, useState } from "react";
import { getPatientDetails } from "../api/patients";

function formatDate(dateStr) {
  if (!dateStr) return "—";
  return new Date(dateStr).toLocaleDateString("uz-UZ");
}

function formatDateTime(dateStr) {
  if (!dateStr) return "—";
  return new Date(dateStr).toLocaleString("uz-UZ", {
    day: "2-digit", month: "2-digit", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  });
}

function formatMoney(amount) {
  if (!amount && amount !== 0) return "0";
  return Number(amount).toLocaleString("uz-UZ");
}

function getInitials(name) {
  if (!name) return "?";
  return name.split(" ").map(n => n[0]).join("").slice(0, 2).toUpperCase();
}

function getStatusLabel(status) {
  if (status === "COMPLETED") return { label: "Bajarildi", color: "#38a169" };
  if (status === "PENDING")   return { label: "Kutilmoqda", color: "#d69e2e" };
  if (status === "CANCELLED") return { label: "Bekor qilindi", color: "#e53e3e" };
  return { label: status, color: "#718096" };
}

function getPaymentLabel(status) {
  if (status === "PAID")     return { label: "To'langan", color: "#38a169" };
  if (status === "PENDING")  return { label: "Kutilmoqda", color: "#d69e2e" };
  if (status === "REFUNDED") return { label: "Qaytarilgan", color: "#3182ce" };
  return { label: status, color: "#718096" };
}

export default function PatientDrawer({ patientId, onClose }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!patientId) return;
    setLoading(true);
    getPatientDetails(patientId)
      .then(setData)
      .finally(() => setLoading(false));
  }, [patientId]);

  // ESC tugmasi bilan yopish
  useEffect(() => {
    const handler = (e) => { if (e.key === "Escape") onClose(); };
    document.addEventListener("keydown", handler);
    return () => document.removeEventListener("keydown", handler);
  }, [onClose]);

  const p = data?.patient;

  return (
    <>
      {/* Orqa fon overlay */}
      <div
        onClick={onClose}
        style={{
          position: "fixed", inset: 0,
          backgroundColor: "rgba(0,0,0,0.4)",
          zIndex: 300,
          animation: "fadeIn 0.2s ease",
        }}
      />

      {/* Drawer panel */}
      <div style={{
        position: "fixed", top: 0, right: 0,
        width: "420px", height: "100vh",
        backgroundColor: "var(--card-bg)",
        borderLeft: "1px solid var(--border)",
        zIndex: 301,
        display: "flex", flexDirection: "column",
        animation: "slideIn 0.25s ease",
        overflowY: "auto",
      }}>

        {/* Header */}
        <div style={{
          padding: "20px 24px",
          borderBottom: "1px solid var(--border)",
          display: "flex", alignItems: "center", justifyContent: "space-between",
          position: "sticky", top: 0,
          backgroundColor: "var(--card-bg)",
          zIndex: 1,
        }}>
          <h3 style={{ color: "var(--text-primary)", fontSize: "17px", fontWeight: 600, margin: 0 }}>
            Bemor kartochkasi
          </h3>
          <button
            onClick={onClose}
            style={{
              width: "32px", height: "32px", borderRadius: "50%",
              border: "1px solid var(--border)",
              backgroundColor: "var(--input-bg)",
              cursor: "pointer", fontSize: "18px", color: "var(--text-secondary)",
              display: "flex", alignItems: "center", justifyContent: "center",
            }}
          >×</button>
        </div>

        {loading ? (
          <div style={{ padding: "40px", textAlign: "center", color: "var(--text-secondary)" }}>
            Yuklanmoqda...
          </div>
        ) : !data ? (
          <div style={{ padding: "40px", textAlign: "center", color: "var(--text-secondary)" }}>
            Ma'lumot topilmadi
          </div>
        ) : (
          <div style={{ padding: "24px", display: "flex", flexDirection: "column", gap: "20px" }}>

            {/* Bemor profil */}
            <div style={{ display: "flex", alignItems: "center", gap: "16px" }}>
              <div style={{
                width: "64px", height: "64px", borderRadius: "50%",
                backgroundColor: "#0D7377", color: "white",
                display: "flex", alignItems: "center", justifyContent: "center",
                fontSize: "22px", fontWeight: "bold", flexShrink: 0,
              }}>
                {getInitials(p?.fullName)}
              </div>
              <div>
                <p style={{ color: "var(--text-primary)", fontSize: "18px", fontWeight: 600, margin: 0 }}>
                  {p?.fullName || "—"}
                </p>
                <p style={{ color: "var(--text-secondary)", fontSize: "13px", margin: "4px 0 0" }}>
                  {p?.gender === "MALE" ? "♂ Erkak" : p?.gender === "FEMALE" ? "♀ Ayol" : "—"}
                  {p?.birthDate ? ` • ${formatDate(p.birthDate)}` : ""}
                </p>
              </div>
            </div>

            {/* Kontakt ma'lumotlar */}
            <div style={{
              backgroundColor: "var(--bg)", borderRadius: "10px", padding: "16px",
              display: "flex", flexDirection: "column", gap: "10px",
            }}>
              <InfoRow icon="📞" label="Telefon" value={p?.phone || "—"} />
              <InfoRow icon="✉️" label="Email" value={p?.email || "—"} />
              <InfoRow icon="📍" label="Manzil" value={p?.address || "—"} />
              <InfoRow icon="📅" label="Ro'yxat sanasi" value={formatDate(p?.createdAt)} />
            </div>

            {/* Statistika */}
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "12px" }}>
              <StatCard label="Jami tashriflar" value={p?.totalVisits ?? 0} color="#0D7377" />
              <StatCard label="Oxirgi tashrif" value={formatDate(p?.lastVisitDate)} color="#3182ce" />
            </div>

            {/* Oxirgi navbatlar */}
            <Section title="📋 Oxirgi navbatlar">
              {!data.recentAppointments?.length ? (
                <EmptyText text="Navbatlar mavjud emas" />
              ) : (
                data.recentAppointments.map(a => {
                  const st = getStatusLabel(a.status);
                  return (
                    <div key={a.id} style={{
                      padding: "10px 0",
                      borderBottom: "1px solid var(--border)",
                      display: "flex", justifyContent: "space-between", alignItems: "flex-start",
                    }}>
                      <div>
                        <p style={{ color: "var(--text-primary)", fontSize: "14px", fontWeight: 500, margin: 0 }}>
                          {a.doctorName || "—"}
                        </p>
                        <p style={{ color: "var(--text-secondary)", fontSize: "12px", margin: "3px 0 0" }}>
                          {a.serviceName || "—"} • {formatDateTime(a.appointmentTime)}
                        </p>
                      </div>
                      <span style={{
                        fontSize: "11px", fontWeight: 600, padding: "3px 8px",
                        borderRadius: "20px", backgroundColor: st.color + "20", color: st.color,
                        whiteSpace: "nowrap", marginLeft: "8px",
                      }}>
                        {st.label}
                      </span>
                    </div>
                  );
                })
              )}
            </Section>

            {/* Oxirgi to'lovlar */}
            <Section title="💳 Oxirgi to'lovlar">
              {!data.recentPayments?.length ? (
                <EmptyText text="To'lovlar mavjud emas" />
              ) : (
                data.recentPayments.map(pay => {
                  const st = getPaymentLabel(pay.status);
                  return (
                    <div key={pay.id} style={{
                      padding: "10px 0",
                      borderBottom: "1px solid var(--border)",
                      display: "flex", justifyContent: "space-between", alignItems: "center",
                    }}>
                      <div>
                        <p style={{ color: "var(--text-primary)", fontSize: "14px", fontWeight: 500, margin: 0 }}>
                          {formatMoney(pay.amount)} so'm
                        </p>
                        <p style={{ color: "var(--text-secondary)", fontSize: "12px", margin: "3px 0 0" }}>
                          {pay.serviceName || "—"} • {pay.paymentMethod || ""}
                        </p>
                      </div>
                      <span style={{
                        fontSize: "11px", fontWeight: 600, padding: "3px 8px",
                        borderRadius: "20px", backgroundColor: st.color + "20", color: st.color,
                        whiteSpace: "nowrap", marginLeft: "8px",
                      }}>
                        {st.label}
                      </span>
                    </div>
                  );
                })
              )}
            </Section>

          </div>
        )}
      </div>

      <style>{`
        @keyframes slideIn {
          from { transform: translateX(100%); opacity: 0; }
          to   { transform: translateX(0);    opacity: 1; }
        }
        @keyframes fadeIn {
          from { opacity: 0; }
          to   { opacity: 1; }
        }
      `}</style>
    </>
  );
}

function InfoRow({ icon, label, value }) {
  return (
    <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
      <span style={{ fontSize: "15px", width: "20px", textAlign: "center" }}>{icon}</span>
      <span style={{ color: "var(--text-secondary)", fontSize: "13px", width: "80px", flexShrink: 0 }}>{label}</span>
      <span style={{ color: "var(--text-primary)", fontSize: "13px" }}>{value}</span>
    </div>
  );
}

function StatCard({ label, value, color }) {
  return (
    <div style={{
      backgroundColor: color + "15", borderRadius: "10px",
      padding: "14px", textAlign: "center",
      border: `1px solid ${color}30`,
    }}>
      <p style={{ color: color, fontSize: "22px", fontWeight: 700, margin: 0 }}>{value}</p>
      <p style={{ color: "var(--text-secondary)", fontSize: "12px", margin: "4px 0 0" }}>{label}</p>
    </div>
  );
}

function Section({ title, children }) {
  return (
    <div>
      <p style={{ color: "var(--text-primary)", fontSize: "14px", fontWeight: 600, marginBottom: "8px" }}>
        {title}
      </p>
      {children}
    </div>
  );
}

function EmptyText({ text }) {
  return (
    <p style={{ color: "var(--text-secondary)", fontSize: "13px", textAlign: "center", padding: "12px 0" }}>
      {text}
    </p>
  );
}