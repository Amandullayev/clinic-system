import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";

export default function OAuth2Callback() {
  const [params] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const token = params.get("token");
    if (token) {
      try {
        localStorage.setItem("token", token);
        const base64 = token.split(".")[1].replace(/-/g, '+').replace(/_/g, '/');
        const payload = JSON.parse(atob(base64));
        const role = payload.role;
        if (role === "ROLE_SUPER_ADMIN" || role === "ROLE_ADMIN") {
          navigate("/role-selector");
        } else if (role === "ROLE_RECEPTIONIST") {
          navigate("/receptionist/dashboard");
        } else if (role === "ROLE_DOCTOR") {
          navigate("/doctor/dashboard");
        } else {
          navigate("/patient/dashboard");
        }
      } catch (e) {
        navigate("/login");  // xato bo'lsa login ga qaytadi
      }
    } else {
      navigate("/login");
    }
}, []); 

  return <div style={{ padding: 40, textAlign: "center" }}>Yuklanmoqda...</div>;
}