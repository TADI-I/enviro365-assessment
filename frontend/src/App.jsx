import { useState, useEffect, useCallback } from "react";

// ── API service (inline for single-file submission) ──────────────────────────
const BASE = "http://localhost:8080/api";

const api = {
  get: async (path) => {
    const r = await fetch(`${BASE}${path}`);
    const d = await r.json();
    if (!d.success) throw new Error(d.message);
    return d.data;
  },
  post: async (path, body) => {
    const r = await fetch(`${BASE}${path}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    const d = await r.json();
    if (!d.success) throw new Error(d.message);
    return d.data;
  },
  downloadCsv: (investorId) => {
    const url = investorId
      ? `${BASE}/export/csv?investorId=${investorId}`
      : `${BASE}/export/csv`;
    const a = document.createElement("a");
    a.href = url;
    a.download = investorId ? `withdrawals_investor_${investorId}.csv` : "withdrawals_all.csv";
    a.click();
  },
};

// ── Colour tokens ─────────────────────────────────────────────────────────────
const C = {
  navy: "#0B2447",
  navyMid: "#19376D",
  blue: "#0D6EFD",
  gold: "#F4C430",
  white: "#FFFFFF",
  bg: "#F0F4FA",
  card: "#FFFFFF",
  border: "#D0DCEE",
  text: "#1A1A2E",
  muted: "#6C757D",
  success: "#198754",
  danger: "#DC3545",
  warning: "#FFC107",
};

// ── Reusable UI atoms ─────────────────────────────────────────────────────────
const Card = ({ children, style = {} }) => (
  <div style={{
    background: C.card, borderRadius: 12, padding: 24,
    boxShadow: "0 2px 12px rgba(0,0,0,0.08)", border: `1px solid ${C.border}`,
    ...style,
  }}>{children}</div>
);

const Badge = ({ type, children }) => {
  const colours = {
    SAVINGS: { bg: "#E8F4FD", text: "#0969DA" },
    RETIREMENT: { bg: "#FFF3CD", text: "#856404" },
    APPROVED: { bg: "#D1FAE5", text: "#065F46" },
    PENDING: { bg: "#FEF9C3", text: "#854D0E" },
    REJECTED: { bg: "#FEE2E2", text: "#991B1B" },
  };
  const c = colours[type] || { bg: "#E5E7EB", text: "#374151" };
  return (
    <span style={{
      background: c.bg, color: c.text, padding: "2px 10px",
      borderRadius: 20, fontSize: 12, fontWeight: 600,
    }}>{children}</span>
  );
};

const Alert = ({ type, message, onClose }) => {
  if (!message) return null;
  const colours = {
    success: { bg: "#D1FAE5", border: "#6EE7B7", text: "#065F46" },
    error: { bg: "#FEE2E2", border: "#FCA5A5", text: "#991B1B" },
  };
  const c = colours[type];
  return (
    <div style={{
      background: c.bg, border: `1px solid ${c.border}`, color: c.text,
      borderRadius: 8, padding: "12px 16px", marginBottom: 16,
      display: "flex", justifyContent: "space-between", alignItems: "center",
    }}>
      <span>{message}</span>
      {onClose && (
        <button onClick={onClose} style={{
          background: "none", border: "none", cursor: "pointer",
          color: c.text, fontSize: 18, lineHeight: 1,
        }}>×</button>
      )}
    </div>
  );
};

const Btn = ({ onClick, children, variant = "primary", disabled, style = {} }) => {
  const variants = {
    primary: { background: C.blue, color: C.white, border: "none" },
    secondary: { background: C.navyMid, color: C.white, border: "none" },
    outline: { background: "transparent", color: C.blue, border: `1px solid ${C.blue}` },
    danger: { background: C.danger, color: C.white, border: "none" },
    gold: { background: C.gold, color: C.navy, border: "none" },
  };
  return (
    <button onClick={onClick} disabled={disabled} style={{
      ...variants[variant], borderRadius: 8, padding: "10px 20px",
      fontWeight: 600, fontSize: 14, cursor: disabled ? "not-allowed" : "pointer",
      opacity: disabled ? 0.6 : 1, transition: "opacity .2s",
      ...style,
    }}>{children}</button>
  );
};

// ── Portfolio Dashboard ────────────────────────────────────────────────────────
function PortfolioDashboard({ portfolio }) {
  if (!portfolio) return null;
  const fmt = (n) =>
    new Intl.NumberFormat("en-ZA", { style: "currency", currency: "ZAR" }).format(n);

  return (
    <div>
      {/* Investor header */}
      <Card style={{ marginBottom: 20, background: C.navy, color: C.white }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 12 }}>
          <div>
            <div style={{ fontSize: 22, fontWeight: 700 }}>{portfolio.name}</div>
            <div style={{ color: "#A0B0C8", fontSize: 14 }}>{portfolio.email} · Age {portfolio.age}</div>
          </div>
          <div style={{ textAlign: "right" }}>
            <div style={{ color: "#A0B0C8", fontSize: 13 }}>Total Portfolio Value</div>
            <div style={{ fontSize: 28, fontWeight: 700, color: C.gold }}>
              {fmt(portfolio.totalBalance)}
            </div>
          </div>
        </div>
      </Card>

      {/* Products grid */}
      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill,minmax(260px,1fr))", gap: 16 }}>
        {portfolio.products.map((p) => (
          <Card key={p.id}>
            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 12 }}>
              <span style={{ fontWeight: 600, color: C.text }}>{p.productName}</span>
              <Badge type={p.productType}>{p.productType}</Badge>
            </div>
            <div style={{ fontSize: 24, fontWeight: 700, color: C.navyMid }}>{fmt(p.balance)}</div>
            <div style={{ fontSize: 12, color: C.muted, marginTop: 4 }}>Available balance</div>
            {p.productType === "RETIREMENT" && portfolio.age <= 65 && (
              <div style={{ marginTop: 10, fontSize: 12, color: C.warning, background: "#FFF8E7", padding: "6px 10px", borderRadius: 6 }}>
                ⚠ Retirement withdrawals require age &gt; 65
              </div>
            )}
          </Card>
        ))}
      </div>
    </div>
  );
}

// ── Withdrawal Form ────────────────────────────────────────────────────────────
function WithdrawalForm({ portfolio, onSuccess }) {
  const [productId, setProductId] = useState("");
  const [amount, setAmount] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const selectedProduct = portfolio?.products.find((p) => p.id === Number(productId));
  const maxAllowed = selectedProduct
    ? (selectedProduct.balance * 0.9).toFixed(2)
    : 0;

  const handleSubmit = async () => {
    // ── UI Validation ────────────────────────────────────────────────────────
    if (!productId) return setError("Please select a product.");
    if (!amount || Number(amount) <= 0) return setError("Please enter a valid amount.");
    if (Number(amount) > Number(maxAllowed))
      return setError(`Amount exceeds 90% of balance (max R${maxAllowed}).`);

    setError("");
    setLoading(true);
    try {
      await api.post("/withdrawals", {
        investorId: portfolio.id,
        productId: Number(productId),
        amount: Number(amount),
      });
      setSuccess(`Withdrawal of R${amount} submitted successfully!`);
      setAmount("");
      setProductId("");
      onSuccess?.();
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  if (!portfolio) return null;

  const labelStyle = { display: "block", fontWeight: 600, marginBottom: 6, color: C.text, fontSize: 14 };
  const inputStyle = {
    width: "100%", padding: "10px 12px", borderRadius: 8, border: `1px solid ${C.border}`,
    fontSize: 14, boxSizing: "border-box", outline: "none",
  };

  return (
    <Card>
      <h3 style={{ marginTop: 0, color: C.navy }}>Submit Withdrawal Notice</h3>
      <Alert type="success" message={success} onClose={() => setSuccess("")} />
      <Alert type="error" message={error} onClose={() => setError("")} />

      <div style={{ marginBottom: 16 }}>
        <label style={labelStyle}>Investment Product</label>
        <select value={productId} onChange={(e) => setProductId(e.target.value)} style={inputStyle}>
          <option value="">-- Select product --</option>
          {portfolio.products.map((p) => (
            <option key={p.id} value={p.id}>
              {p.productName} ({p.productType}) — R{Number(p.balance).toLocaleString("en-ZA")}
            </option>
          ))}
        </select>
      </div>

      {selectedProduct && (
        <div style={{
          background: "#F0F4FA", borderRadius: 8, padding: 12, marginBottom: 16, fontSize: 13,
        }}>
          <div>Balance: <strong>R{Number(selectedProduct.balance).toLocaleString("en-ZA")}</strong></div>
          <div>Max withdrawal (90%): <strong style={{ color: C.blue }}>R{Number(maxAllowed).toLocaleString("en-ZA")}</strong></div>
          {selectedProduct.productType === "RETIREMENT" && portfolio.age <= 65 && (
            <div style={{ color: C.danger, marginTop: 4 }}>
              ✗ Not eligible — retirement withdrawals require age &gt; 65
            </div>
          )}
        </div>
      )}

      <div style={{ marginBottom: 20 }}>
        <label style={labelStyle}>Withdrawal Amount (R)</label>
        <input
          type="number"
          min="0.01"
          step="0.01"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          placeholder="Enter amount"
          style={inputStyle}
        />
      </div>

      <Btn onClick={handleSubmit} disabled={loading}>
        {loading ? "Submitting…" : "Submit Withdrawal"}
      </Btn>
    </Card>
  );
}

// ── Withdrawal History Table ───────────────────────────────────────────────────
function WithdrawalHistory({ investorId, onDownloadCSV, refresh }) {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);

  const load = useCallback(async () => {
    if (!investorId) return;
    setLoading(true);
    try {
      const data = await api.get(`/withdrawals/investor/${investorId}`);
      setHistory(data);
    } catch {
      setHistory([]);
    } finally {
      setLoading(false);
    }
  }, [investorId]);

  useEffect(() => { load(); }, [load, refresh]);

  const fmt = (n) =>
    new Intl.NumberFormat("en-ZA", { style: "currency", currency: "ZAR" }).format(n);

  return (
    <Card>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
        <h3 style={{ margin: 0, color: C.navy }}>Withdrawal History</h3>
        <Btn variant="gold" onClick={() => onDownloadCSV(investorId)} style={{ fontSize: 13 }}>
          ⬇ Download CSV
        </Btn>
      </div>

      {loading ? (
        <div style={{ textAlign: "center", color: C.muted, padding: 24 }}>Loading…</div>
      ) : history.length === 0 ? (
        <div style={{ textAlign: "center", color: C.muted, padding: 24 }}>No withdrawals yet.</div>
      ) : (
        <div style={{ overflowX: "auto" }}>
          <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
            <thead>
              <tr style={{ background: C.bg }}>
                {["#", "Product", "Type", "Amount", "Balance After", "Date", "Status"].map((h) => (
                  <th key={h} style={{
                    padding: "10px 12px", textAlign: "left", fontWeight: 600,
                    color: C.navyMid, borderBottom: `2px solid ${C.border}`, whiteSpace: "nowrap",
                  }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {history.map((w) => (
                <tr key={w.id} style={{ borderBottom: `1px solid ${C.border}` }}>
                  <td style={{ padding: "10px 12px", color: C.muted }}>{w.id}</td>
                  <td style={{ padding: "10px 12px" }}>{w.productName}</td>
                  <td style={{ padding: "10px 12px" }}><Badge type={w.productType}>{w.productType}</Badge></td>
                  <td style={{ padding: "10px 12px", fontWeight: 600, color: C.danger }}>{fmt(w.amount)}</td>
                  <td style={{ padding: "10px 12px" }}>{fmt(w.balanceAfterWithdrawal)}</td>
                  <td style={{ padding: "10px 12px", color: C.muted, whiteSpace: "nowrap" }}>
                    {new Date(w.createdAt).toLocaleString("en-ZA")}
                  </td>
                  <td style={{ padding: "10px 12px" }}><Badge type={w.status}>{w.status}</Badge></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </Card>
  );
}

// ── Main App ──────────────────────────────────────────────────────────────────
export default function App() {
  const [investors, setInvestors] = useState([]);
  const [selectedId, setSelectedId] = useState("");
  const [portfolio, setPortfolio] = useState(null);
  const [activeTab, setActiveTab] = useState("dashboard");
  const [refreshKey, setRefreshKey] = useState(0);
  const [loadingPortfolio, setLoadingPortfolio] = useState(false);
  const [globalError, setGlobalError] = useState("");

  // Load investor list on mount
  useEffect(() => {
    api.get("/investors")
      .then(setInvestors)
      .catch(() => setGlobalError("Cannot connect to backend. Ensure Spring Boot is running on :8080."));
  }, []);

  // Load portfolio when investor selected
  useEffect(() => {
    if (!selectedId) { setPortfolio(null); return; }
    setLoadingPortfolio(true);
    api.get(`/investors/${selectedId}/portfolio`)
      .then(setPortfolio)
      .catch((e) => setGlobalError(e.message))
      .finally(() => setLoadingPortfolio(false));
  }, [selectedId]);

  const tabs = ["dashboard", "withdraw", "history"];
  const tabLabels = { dashboard: " Portfolio", withdraw: " Withdraw", history: " History" };

  return (
    <div style={{ minHeight: "100vh", background: C.bg, fontFamily: "'Segoe UI', sans-serif" }}>
      {/* Top nav */}
      <nav style={{
        background: C.navy, padding: "0 24px", display: "flex",
        alignItems: "center", justifyContent: "space-between", height: 64,
        boxShadow: "0 2px 8px rgba(0,0,0,0.3)",
      }}>
        <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
          <div style={{
            background: C.gold, color: C.navy, borderRadius: 8,
            padding: "4px 10px", fontWeight: 800, fontSize: 16,
          }}>E365</div>
          <span style={{ color: C.white, fontWeight: 600, fontSize: 18 }}>
            Enviro365 Investments
          </span>
        </div>
        <div style={{ color: "#A0B0C8", fontSize: 13 }}>Withdrawal Portal</div>
      </nav>

      <div style={{ maxWidth: 960, margin: "0 auto", padding: "24px 16px" }}>
        {globalError && <Alert type="error" message={globalError} onClose={() => setGlobalError("")} />}

        {/* Investor selector */}
        <Card style={{ marginBottom: 24 }}>
          <label style={{ fontWeight: 600, color: C.navyMid, display: "block", marginBottom: 8 }}>
            Select Investor
          </label>
          <select
            value={selectedId}
            onChange={(e) => setSelectedId(e.target.value)}
            style={{
              width: "100%", padding: "10px 12px", borderRadius: 8,
              border: `1px solid ${C.border}`, fontSize: 15,
            }}
          >
            <option value="">-- Choose an investor --</option>
            {investors.map((inv) => (
              <option key={inv.id} value={inv.id}>{inv.name} ({inv.email})</option>
            ))}
          </select>
        </Card>

        {loadingPortfolio && (
          <div style={{ textAlign: "center", color: C.muted, padding: 40 }}>Loading portfolio…</div>
        )}

        {portfolio && (
          <>
            {/* Tab bar */}
            <div style={{
              display: "flex", gap: 4, marginBottom: 20,
              background: C.card, borderRadius: 10, padding: 4,
              border: `1px solid ${C.border}`, width: "fit-content",
            }}>
              {tabs.map((t) => (
                <button key={t} onClick={() => setActiveTab(t)} style={{
                  padding: "8px 20px", borderRadius: 8, border: "none",
                  background: activeTab === t ? C.navy : "transparent",
                  color: activeTab === t ? C.white : C.muted,
                  fontWeight: 600, cursor: "pointer", fontSize: 14,
                  transition: "all .2s",
                }}>{tabLabels[t]}</button>
              ))}
            </div>

            {/* Tab content */}
            {activeTab === "dashboard" && <PortfolioDashboard portfolio={portfolio} />}
            {activeTab === "withdraw" && (
              <WithdrawalForm
                portfolio={portfolio}
                onSuccess={() => {
                  setRefreshKey((k) => k + 1);
                  // Reload portfolio to get updated balances
                  api.get(`/investors/${selectedId}/portfolio`).then(setPortfolio).catch(() => {});
                }}
              />
            )}
            {activeTab === "history" && (
              <WithdrawalHistory
                investorId={portfolio.id}
                onDownloadCSV={api.downloadCsv}
                refresh={refreshKey}
              />
            )}
          </>
        )}
      </div>
    </div>
  );
}
