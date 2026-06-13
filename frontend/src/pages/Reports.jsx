import { useState, useEffect } from "react";
import Layout from "../components/layout/Layout";
import { getReports } from "../api/reports";
import {
  BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend
} from "recharts";
import "../styles/reports.css";

const COLORS = ["#0D7377", "#2563EB", "#EF4444", "#F59E0B", "#8B5CF6", "#EC4899"];

function formatMoney(val) {
  return Number(val ?? 0).toLocaleString("uz-UZ") + " so'm";
}

function StatCard({ label, value, icon, color }) {
  return (
    <div style={{
      backgroundColor: "var(--card-bg)",
      border: "1px solid var(--border)",
      borderRadius: "12px",
      padding: "20px 24px",
      borderLeft: `4px solid ${color}`,
    }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
        <div>
          <p style={{ color: "var(--text-secondary)", fontSize: "13px", margin: "0 0 8px" }}>{label}</p>
          <p style={{ color: "var(--text-primary)", fontSize: "22px", fontWeight: 700, margin: 0 }}>{value}</p>
        </div>
        <span style={{
          fontSize: "24px", backgroundColor: color + "20",
          width: "48px", height: "48px", borderRadius: "10px",
          display: "flex", alignItems: "center", justifyContent: "center",
        }}>{icon}</span>
      </div>
    </div>
  );
}

function ChartCard({ title, children }) {
  return (
    <div style={{
      backgroundColor: "var(--card-bg)",
      border: "1px solid var(--border)",
      borderRadius: "12px",
      padding: "20px 24px",
    }}>
      <h3 style={{ color: "var(--text-primary)", fontSize: "15px", fontWeight: 600, margin: "0 0 20px" }}>
        {title}
      </h3>
      {children}
    </div>
  );
}

const tooltipStyle = {
  contentStyle: {
    backgroundColor: "var(--card-bg)",
    border: "1px solid var(--border)",
    borderRadius: "8px",
    fontSize: "13px",
  },
  labelStyle: { color: "var(--text-primary)", fontWeight: 600 },
  itemStyle: { color: "var(--text-secondary)" },
};

export default function Reports() {
  const today = new Date().toISOString().split("T")[0];
  const firstDay = new Date(new Date().getFullYear(), new Date().getMonth(), 1)
    .toISOString().split("T")[0];

  const [from, setFrom] = useState(firstDay);
  const [to, setTo] = useState(today);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);

  const load = () => {
    setLoading(true);
    getReports(from, to)
      .then(setData)
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  return (
    <Layout title="Hisobotlar">

      {/* Sarlavha + filtr */}
      <div style={{
        display: "flex", alignItems: "center", justifyContent: "space-between",
        flexWrap: "wrap", gap: "12px", marginBottom: "24px",
      }}>
        <div>
          <h2 style={{ color: "var(--text-primary)", margin: 0, fontSize: "20px", fontWeight: 700 }}>
            Hisobotlar
          </h2>
          <p style={{ color: "var(--text-secondary)", fontSize: "13px", margin: "4px 0 0" }}>
            Davr bo'yicha statistika va tahlil
          </p>
        </div>

        <div style={{ display: "flex", alignItems: "center", gap: "10px", flexWrap: "wrap" }}>
          <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
            <span style={{ color: "var(--text-secondary)", fontSize: "13px" }}>Dan:</span>
            <input
              type="date" value={from}
              onChange={e => setFrom(e.target.value)}
              style={{
                padding: "7px 12px", borderRadius: "8px",
                border: "1px solid var(--border)",
                backgroundColor: "var(--input-bg)",
                color: "var(--text-primary)", fontSize: "13px",
              }}
            />
          </div>
          <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
            <span style={{ color: "var(--text-secondary)", fontSize: "13px" }}>Gacha:</span>
            <input
              type="date" value={to}
              onChange={e => setTo(e.target.value)}
              style={{
                padding: "7px 12px", borderRadius: "8px",
                border: "1px solid var(--border)",
                backgroundColor: "var(--input-bg)",
                color: "var(--text-primary)", fontSize: "13px",
              }}
            />
          </div>
          <button
            onClick={load}
            disabled={loading}
            style={{
              padding: "8px 20px", borderRadius: "8px",
              backgroundColor: "#0D7377", color: "white",
              border: "none", cursor: loading ? "not-allowed" : "pointer",
              fontSize: "13px", fontWeight: 600,
              opacity: loading ? 0.7 : 1,
            }}
          >
            {loading ? "Yuklanmoqda..." : "🔍 Ko'rish"}
          </button>
        </div>
      </div>

      {!data ? (
        <div style={{ textAlign: "center", padding: "60px", color: "var(--text-secondary)" }}>
          Yuklanmoqda...
        </div>
      ) : (
        <div style={{ display: "flex", flexDirection: "column", gap: "20px" }}>

          {/* Stat kartalar */}
          <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: "16px" }}>
            <StatCard label="Jami bemorlar"  value={data.totalPatients ?? 0}         icon="👥" color="#0D7377" />
            <StatCard label="Jami tashriflar" value={data.totalVisits ?? 0}           icon="📋" color="#2563EB" />
            <StatCard label="Jami daromad"   value={formatMoney(data.totalRevenue)}   icon="💰" color="#16A34A" />
            <StatCard label="O'rtacha narx"  value={formatMoney(data.avgPrice)}       icon="📊" color="#F59E0B" />
          </div>

          {/* 2 ta grafik yonma-yon */}
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "20px" }}>

            {/* Oylik daromad */}
            <ChartCard title="📈 Oylik daromad">
              <ResponsiveContainer width="100%" height={220}>
                <LineChart data={data.monthlyRevenue || []}>
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" vertical={false} />
                  <XAxis dataKey="month" tick={{ fill: "var(--text-secondary)", fontSize: 12 }} axisLine={false} tickLine={false} />
                  <YAxis tick={{ fill: "var(--text-secondary)", fontSize: 12 }} axisLine={false} tickLine={false} />
                  <Tooltip {...tooltipStyle} formatter={v => [formatMoney(v), "Daromad"]} />
                  <Line type="monotone" dataKey="revenue" stroke="#0D7377" strokeWidth={2.5} dot={{ fill: "#0D7377", r: 4 }} />
                </LineChart>
              </ResponsiveContainer>
            </ChartCard>

            {/* Haftalik tashriflar */}
            <ChartCard title="📅 Hafta bo'yicha tashriflar">
              <ResponsiveContainer width="100%" height={220}>
                <BarChart data={data.weekdayVisits || []}>
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" vertical={false} />
                  <XAxis dataKey="day" tick={{ fill: "var(--text-secondary)", fontSize: 12 }} axisLine={false} tickLine={false} />
                  <YAxis tick={{ fill: "var(--text-secondary)", fontSize: 12 }} axisLine={false} tickLine={false} />
                  <Tooltip {...tooltipStyle} formatter={v => [v, "Tashriflar"]} />
                  <Bar dataKey="count" fill="#2563EB" radius={[6, 6, 0, 0]} maxBarSize={50} />
                </BarChart>
              </ResponsiveContainer>
            </ChartCard>
          </div>

          {/* Mashhur xizmatlar */}
          {data.popularServices?.length > 0 && (
            <ChartCard title="🏆 Eng mashhur xizmatlar">
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "20px", alignItems: "center" }}>
                <ResponsiveContainer width="100%" height={220}>
                  <PieChart>
                    <Pie
                      data={data.popularServices}
                      dataKey="count"
                      nameKey="name"
                      cx="50%" cy="50%"
                      innerRadius={55}
                      outerRadius={90}
                      paddingAngle={3}
                    >
                      {data.popularServices.map((_, i) => (
                        <Cell key={i} fill={COLORS[i % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip {...tooltipStyle} formatter={v => [v, "Qabullar"]} />
                  </PieChart>
                </ResponsiveContainer>
                <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
                  {data.popularServices.map((s, i) => (
                    <div key={i} style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                      <div style={{ width: "12px", height: "12px", borderRadius: "50%", backgroundColor: COLORS[i % COLORS.length], flexShrink: 0 }} />
                      <div style={{ flex: 1 }}>
                        <div style={{ display: "flex", justifyContent: "space-between" }}>
                          <span style={{ color: "var(--text-primary)", fontSize: "13px" }}>{s.name}</span>
                          <span style={{ color: "var(--text-secondary)", fontSize: "13px" }}>{s.count} ta</span>
                        </div>
                        <div style={{ height: "4px", backgroundColor: "var(--border)", borderRadius: "2px", marginTop: "4px" }}>
                          <div style={{
                            height: "100%", borderRadius: "2px",
                            backgroundColor: COLORS[i % COLORS.length],
                            width: `${(s.count / data.popularServices[0].count) * 100}%`,
                          }} />
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </ChartCard>
          )}

          {/* Shifokorlar samaradorligi */}
          <ChartCard title="👨‍⚕️ Shifokorlar samaradorligi">
            {!data.doctorEfficiency?.length ? (
              <p style={{ color: "var(--text-secondary)", textAlign: "center", padding: "20px" }}>Ma'lumot yo'q</p>
            ) : (
              <table style={{ width: "100%", borderCollapse: "collapse" }}>
                <thead>
                  <tr style={{ borderBottom: "2px solid var(--border)" }}>
                    {["Shifokor", "Qabullar", "Ish kunlari", "Daromad", "O'rtacha"].map(h => (
                      <th key={h} style={{
                        padding: "10px 12px", textAlign: "left",
                        color: "var(--text-secondary)", fontSize: "12px",
                        fontWeight: 600, textTransform: "uppercase", letterSpacing: "0.5px",
                      }}>{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {data.doctorEfficiency.map((d, i) => (
                    <tr key={i} style={{ borderBottom: "1px solid var(--border)" }}>
                      <td style={{ padding: "12px", color: "var(--text-primary)", fontWeight: 500 }}>
                        <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                          <div style={{
                            width: "32px", height: "32px", borderRadius: "50%",
                            backgroundColor: "#0D7377", color: "white",
                            display: "flex", alignItems: "center", justifyContent: "center",
                            fontSize: "13px", fontWeight: 700, flexShrink: 0,
                          }}>
                            {d.doctorName?.[0] || "D"}
                          </div>
                          {d.doctorName}
                        </div>
                      </td>
                      <td style={{ padding: "12px", color: "var(--text-primary)", fontWeight: 600 }}>{d.appointmentCount}</td>
                      <td style={{ padding: "12px", color: "var(--text-secondary)" }}>{d.workDays}</td>
                      <td style={{ padding: "12px", color: "#16A34A", fontWeight: 600 }}>{formatMoney(d.revenue)}</td>
                      <td style={{ padding: "12px", color: "var(--text-secondary)" }}>{formatMoney(d.avgRevenue)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </ChartCard>

        </div>
      )}
    </Layout>
  );
}