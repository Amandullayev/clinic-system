import { useState } from "react";
import { useTheme } from "../../hooks/useTheme";

function getInitials(name) {
  if (!name) return "A";
  return name.split(" ").map(n => n[0]).join("").slice(0, 2).toUpperCase();
}

function getRoleLabel(role) {
  if (role === "ROLE_SUPER_ADMIN")  return "Super Admin";
  if (role === "ROLE_ADMIN")        return "Admin";
  if (role === "ROLE_RECEPTIONIST") return "Registrator";
  if (role === "ROLE_DOCTOR")       return "Shifokor";
  if (role === "ROLE_PATIENT")      return "Bemor";
  return role || "";
}

export default function Header({ title }) {
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const { theme, toggleTheme } = useTheme();
  const [showNotif, setShowNotif] = useState(false);
  const [search, setSearch] = useState("");

  const role = (() => {
    const token = localStorage.getItem("token");
    if (!token) return null;
    try {
      return JSON.parse(atob(token.split(".")[1])).role || null;
    } catch { return null; }
  })();

  return (
    <div style={{
      height: "64px",
      backgroundColor: "var(--header-bg)",
      borderBottom: "1px solid var(--border)",
      display: "flex",
      alignItems: "center",
      justifyContent: "space-between",
      padding: "0 24px",
      transition: "background-color 0.2s",
      position: "sticky",
      top: 0,
      zIndex: 100,
    }}>

      {/* Chap: sarlavha */}
      <h1 style={{ fontSize: "20px", fontWeight: "600", color: "var(--text-primary)", minWidth: 0 }}>
        {title}
      </h1>

      {/* O'ng: qidiruv + amallar */}
      <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>

        {/* Qidiruv */}
        <div style={{ position: "relative" }}>
          <span style={{
            position: "absolute", left: "10px", top: "50%", transform: "translateY(-50%)",
            color: "var(--text-secondary)", fontSize: "15px", pointerEvents: "none",
          }}>🔍</span>
          <input
            type="text"
            placeholder="Qidirish..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            style={{
              paddingLeft: "32px",
              paddingRight: "12px",
              height: "36px",
              borderRadius: "8px",
              border: "1px solid var(--border)",
              backgroundColor: "var(--input-bg)",
              color: "var(--text-primary)",
              fontSize: "14px",
              width: "200px",
              outline: "none",
              transition: "border-color 0.2s",
            }}
            onFocus={e => e.target.style.borderColor = "#0D7377"}
            onBlur={e => e.target.style.borderColor = "var(--border)"}
          />
        </div>

        {/* Notification bell */}
        <div style={{ position: "relative" }}>
          <button
            onClick={() => setShowNotif(v => !v)}
            title="Bildirishnomalar"
            style={{
              width: "38px",
              height: "38px",
              borderRadius: "50%",
              border: "1px solid var(--border)",
              backgroundColor: "var(--input-bg)",
              cursor: "pointer",
              fontSize: "18px",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              position: "relative",
            }}
          >
            🔔
            {/* Badge */}
            <span style={{
              position: "absolute",
              top: "4px",
              right: "4px",
              width: "8px",
              height: "8px",
              borderRadius: "50%",
              backgroundColor: "#e53e3e",
              border: "2px solid var(--header-bg)",
            }} />
          </button>

          {/* Dropdown */}
          {showNotif && (
            <div style={{
              position: "absolute",
              top: "46px",
              right: 0,
              width: "280px",
              backgroundColor: "var(--card-bg)",
              border: "1px solid var(--border)",
              borderRadius: "10px",
              boxShadow: "0 8px 24px rgba(0,0,0,0.12)",
              zIndex: 200,
              overflow: "hidden",
            }}>
              <div style={{
                padding: "12px 16px",
                borderBottom: "1px solid var(--border)",
                fontWeight: "600",
                color: "var(--text-primary)",
                fontSize: "14px",
              }}>
                Bildirishnomalar
              </div>
              <div style={{ padding: "16px", color: "var(--text-secondary)", fontSize: "13px", textAlign: "center" }}>
                Hozircha yangi bildirishnoma yo'q
              </div>
            </div>
          )}
        </div>

        {/* Tema toggle */}
        <button
          onClick={toggleTheme}
          title={theme === "light" ? "Tungi rejim" : "Kunduzgi rejim"}
          style={{
            width: "38px",
            height: "38px",
            borderRadius: "50%",
            border: "1px solid var(--border)",
            backgroundColor: "var(--input-bg)",
            cursor: "pointer",
            fontSize: "18px",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          {theme === "light" ? "🌙" : "☀️"}
        </button>

        {/* Foydalanuvchi */}
        <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
          <div style={{
            width: "36px",
            height: "36px",
            borderRadius: "50%",
            backgroundColor: "#0D7377",
            color: "white",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            fontWeight: "bold",
            fontSize: "14px",
            flexShrink: 0,
          }}>
            {getInitials(user.fullName)}
          </div>
          <div style={{ lineHeight: 1.3 }}>
            <div style={{ color: "var(--text-primary)", fontSize: "14px", fontWeight: "500" }}>
              {user.fullName || "Foydalanuvchi"}
            </div>
            <div style={{ color: "var(--text-secondary)", fontSize: "12px" }}>
              {getRoleLabel(role)}
            </div>
          </div>
        </div>

      </div>

      {/* Notif dropdown yopish uchun overlay */}
      {showNotif && (
        <div
          onClick={() => setShowNotif(false)}
          style={{ position: "fixed", inset: 0, zIndex: 199 }}
        />
      )}
    </div>
  );
}