import Sidebar from "./Sidebar";
import Header from "./Header";

export default function Layout({ children, title }) {
  return (
    <div style={{ display: "flex", minHeight: "100vh", backgroundColor: "var(--bg)" }}>
      
      {/* Chap sidebar */}
      <Sidebar />

      {/* O'ng qism */}
      <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
        
        {/* Yuqori header */}
        <Header title={title} />

        {/* Sahifa content */}
        <main style={{ flex: 1, padding: "24px", overflowY: "auto" }}>
          {children}
        </main>

      </div>
    </div>
  );
}