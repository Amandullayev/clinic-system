import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Patients from "./pages/Patients";
import Doctors from "./pages/Doctors";
import Appointments from "./pages/Appointments";
import Services from "./pages/Services";
import Payments from "./pages/Payments";
import Reports from "./pages/Reports";
import Settings from "./pages/Settings";
import Medications from "./pages/Medications";
import SuperAdminDashboard from "./pages/SuperAdminDashboard";
import DoctorDashboard from "./pages/doctor/DoctorDashboard";
import PatientDashboard from "./pages/patient/PatientDashboard";
import ReceptionistDashboard from "./pages/receptionist/ReceptionistDashboard";

function isTokenValid() {
  const token = localStorage.getItem("token");
  if (!token) return false;
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.exp * 1000 > Date.now();
  } catch {
    return false;
  }
}

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

function getHomeByRole() {
  const role = getRole();
  if (role === "ROLE_SUPER_ADMIN")  return "/superadmin/dashboard";
  if (role === "ROLE_RECEPTIONIST") return "/receptionist/dashboard";
  if (role === "ROLE_DOCTOR")       return "/doctor/dashboard";
  if (role === "ROLE_PATIENT")      return "/patient/dashboard";
  return "/dashboard";
}

function PrivateRoute({ children, allowedRoles }) {
  if (!isTokenValid()) return <Navigate to="/login" />;
  if (allowedRoles && !allowedRoles.includes(getRole())) {
    return <Navigate to={getHomeByRole()} />;
  }
  return children;
}

function PublicRoute({ children }) {
  return isTokenValid() ? <Navigate to={getHomeByRole()} /> : children;
}

const SUPER_ADMIN_ONLY = ["ROLE_SUPER_ADMIN"];
const ADMIN_ROLES      = ["ROLE_SUPER_ADMIN", "ROLE_ADMIN"];
const STAFF_ROLES      = ["ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_RECEPTIONIST"];
const MEDICAL_ROLES    = ["ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_RECEPTIONIST", "ROLE_DOCTOR"];

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<PublicRoute><Login /></PublicRoute>} />

        <Route
          path="/"
          element={isTokenValid() ? <Navigate to={getHomeByRole()} /> : <Navigate to="/login" />}
        />

        {/* Super Admin */}
        <Route path="/superadmin/dashboard" element={<PrivateRoute allowedRoles={SUPER_ADMIN_ONLY}><SuperAdminDashboard /></PrivateRoute>} />

        {/* Admin */}
        <Route path="/dashboard"    element={<PrivateRoute allowedRoles={ADMIN_ROLES}><Dashboard /></PrivateRoute>} />
        <Route path="/patients"     element={<PrivateRoute allowedRoles={STAFF_ROLES}><Patients /></PrivateRoute>} />
        <Route path="/appointments" element={<PrivateRoute allowedRoles={STAFF_ROLES}><Appointments /></PrivateRoute>} />
        <Route path="/services"     element={<PrivateRoute allowedRoles={MEDICAL_ROLES}><Services /></PrivateRoute>} />
        <Route path="/payments"     element={<PrivateRoute allowedRoles={STAFF_ROLES}><Payments /></PrivateRoute>} />
        <Route path="/medications"  element={<PrivateRoute allowedRoles={MEDICAL_ROLES}><Medications /></PrivateRoute>} />
        <Route path="/doctors"      element={<PrivateRoute allowedRoles={ADMIN_ROLES}><Doctors /></PrivateRoute>} />
        <Route path="/reports"      element={<PrivateRoute allowedRoles={ADMIN_ROLES}><Reports /></PrivateRoute>} />
        <Route path="/settings"     element={<PrivateRoute><Settings /></PrivateRoute>} />

        {/* Receptionist */}
        <Route path="/receptionist/dashboard" element={<PrivateRoute allowedRoles={["ROLE_RECEPTIONIST"]}><ReceptionistDashboard /></PrivateRoute>} />

        {/* Doctor */}
        <Route path="/doctor/dashboard" element={<PrivateRoute allowedRoles={["ROLE_DOCTOR"]}><DoctorDashboard /></PrivateRoute>} />

        {/* Patient */}
        <Route path="/patient/dashboard" element={<PrivateRoute allowedRoles={["ROLE_PATIENT"]}><PatientDashboard /></PrivateRoute>} />

        <Route path="*" element={<Navigate to={isTokenValid() ? getHomeByRole() : "/login"} />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;