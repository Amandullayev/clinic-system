export const getToken = () => localStorage.getItem("token");

export const headers = () => ({
  Authorization: `Bearer ${getToken()}`,
});

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