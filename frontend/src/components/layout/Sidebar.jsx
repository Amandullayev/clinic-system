import { Link, useLocation, useNavigate } from "react-router-dom";
import {
  LayoutDashboard,
  Calendar,
  Users,
  UserRound,
  Stethoscope,
  CreditCard,
  BarChart2,
  Settings,
  LogOut,
  Pill,
} from "lucide-react";

function getRole() {
  const token = localStorage.getItem("token");
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.role || null;
  } catch {
    return null;
  }
}

const SUPER_ADMIN_MENU = [
  { path: "/superadmin/dashboard", icon: Users,         label: "Foydalanuvchilar" },
  { path: "/dashboard",            icon: LayoutDashboard,label: "Bosh sahifa" },
  { path: "/appointments",         icon: Calendar,       label: "Qabullar" },
  { path: "/patients",             icon: Users,          label: "Bemorlar" },
  { path: "/doctors",              icon: UserRound,      label: "Shifokorlar" },
  { path: "/services",             icon: Stethoscope,    label: "Xizmatlar" },
  { path: "/payments",             icon: CreditCard,     label: "To'lovlar" },
  { path: "/medications",          icon: Pill,           label: "Dorilar" },
  { path: "/reports",              icon: BarChart2,      label: "Hisobotlar" },
  { path: "/settings",             icon: Settings,       label: "Sozlamalar" },
];

const ADMIN_MENU = [
  { path: "/dashboard",    icon: LayoutDashboard, label: "Bosh sahifa" },
  { path: "/appointments", icon: Calendar,        label: "Qabullar" },
  { path: "/patients",     icon: Users,           label: "Bemorlar" },
  { path: "/doctors",      icon: UserRound,       label: "Shifokorlar" },
  { path: "/services",     icon: Stethoscope,     label: "Xizmatlar" },
  { path: "/payments",     icon: CreditCard,      label: "To'lovlar" },
  { path: "/medications",  icon: Pill,            label: "Dorilar" },
  { path: "/reports",      icon: BarChart2,       label: "Hisobotlar" },
  { path: "/settings",     icon: Settings,        label: "Sozlamalar" },
];

const RECEPTIONIST_MENU = [
  { path: "/receptionist/dashboard", icon: LayoutDashboard, label: "Bosh sahifa" },
  { path: "/appointments",           icon: Calendar,        label: "Qabullar" },
  { path: "/patients",               icon: Users,           label: "Bemorlar" },
  { path: "/services",               icon: Stethoscope,     label: "Xizmatlar" },
  { path: "/payments",               icon: CreditCard,      label: "To'lovlar" },
  { path: "/settings",               icon: Settings,        label: "Sozlamalar" },
];

const DOCTOR_MENU = [
  { path: "/doctor/dashboard", icon: LayoutDashboard, label: "Mening panelim" },
  { path: "/medications",      icon: Pill,            label: "Dorilar" },
  { path: "/settings",         icon: Settings,        label: "Sozlamalar" },
];

const PATIENT_MENU = [
  { path: "/patient/dashboard", icon: LayoutDashboard, label: "Mening panelim" },
  { path: "/settings",          icon: Settings,        label: "Sozlamalar" },
];

function getMenuByRole(role) {
  if (role === "ROLE_SUPER_ADMIN") return SUPER_ADMIN_MENU;
  if (role === "ROLE_ADMIN")       return ADMIN_MENU;
  if (role === "ROLE_RECEPTIONIST") return RECEPTIONIST_MENU;
  if (role === "ROLE_DOCTOR")      return DOCTOR_MENU;
  if (role === "ROLE_PATIENT")     return PATIENT_MENU;
  return [];
}
export default function Sidebar() {
  const location = useLocation();
  const navigate = useNavigate();
  const role = getRole();
  const menuItems = getMenuByRole(role);

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  return (
    <div style={{
      width: "240px",
      minHeight: "100vh",
      backgroundColor: "var(--sidebar-bg)",
      color: "white",
      display: "flex",
      flexDirection: "column",
      padding: "20px 0",
    }}>
      <div style={{ padding: "0 20px 30px", fontSize: "22px", fontWeight: "bold", color: "#0D7377" }}>
        CLINIQ
      </div>

      <nav style={{ flex: 1 }}>
        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = location.pathname === item.path;
          return (
            <Link
              key={item.path}
              to={item.path}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "12px",
                padding: "12px 20px",
                color: isActive ? "#0D7377" : "#94A3B8",
                backgroundColor: isActive ? "#1E293B" : "transparent",
                textDecoration: "none",
                borderLeft: isActive ? "3px solid #0D7377" : "3px solid transparent",
              }}
            >
              <Icon size={20} />
              <span>{item.label}</span>
            </Link>
          );
        })}
      </nav>

      <button
        onClick={handleLogout}
        style={{
          display: "flex",
          alignItems: "center",
          gap: "12px",
          padding: "12px 20px",
          color: "#EF4444",
          background: "none",
          border: "none",
          cursor: "pointer",
          width: "100%",
        }}
      >
        <LogOut size={20} />
        <span>Chiqish</span>
      </button>
    </div>
  );
}