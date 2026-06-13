export const getToken = () => localStorage.getItem("token");

export const headers = () => {
  if (isTokenExpired()) {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    window.location.href = "/login";
    return {};
  }
  const token = localStorage.getItem("token");
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export const authHeaders = () => ({
  "Content-Type": "application/json",
  ...headers(),
});

export function handleUnauthorized(res) {
  if (res.status === 401 || res.status === 403) {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    window.location.href = "/login";
    throw new Error("Sessiya muddati tugadi");
  }
}

export const isTokenExpired = () => {
  const token = localStorage.getItem("token");
  if (!token) return true;
  try {
    // base64url → base64 ga o'girish
    const base64 = token.split(".")[1]
      .replace(/-/g, "+")
      .replace(/_/g, "/");
    const payload = JSON.parse(atob(base64));
    return payload.exp * 1000 < Date.now();
  } catch {
    return true;
  }
};

