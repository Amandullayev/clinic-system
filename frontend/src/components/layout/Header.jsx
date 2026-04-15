import { useTheme } from "../../hooks/useTheme";

export default function Header({ title }) {
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const { theme, toggleTheme } = useTheme();

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
    }}>
      <h1 style={{ fontSize: "20px", fontWeight: "600", color: "var(--text-primary)" }}>
        {title}
      </h1>

      <div style={{ display: "flex", alignItems: "center", gap: "14px" }}>
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
            fontSize: "15px",
          }}>
            {user.fullName ? user.fullName[0].toUpperCase() : "A"}
          </div>
          <span style={{ color: "var(--text-secondary)", fontSize: "14px" }}>
            {user.fullName || "Admin"}
          </span>
        </div>
      </div>
    </div>
  );
}