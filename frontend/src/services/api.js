// src/services/api.js
// Central API service — all backend calls go through here

const BASE_URL = 'http://localhost:8080/api';

const handleResponse = async (res) => {
  const data = await res.json();
  if (!data.success) throw new Error(data.message || 'Request failed');
  return data.data;
};

// ── Investors ────────────────────────────────────────────────────────────────

export const getAllInvestors = () =>
  fetch(`${BASE_URL}/investors`).then(handleResponse);

export const getPortfolio = (investorId) =>
  fetch(`${BASE_URL}/investors/${investorId}/portfolio`).then(handleResponse);

// ── Withdrawals ──────────────────────────────────────────────────────────────

export const submitWithdrawal = (payload) =>
  fetch(`${BASE_URL}/withdrawals`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  }).then(async (res) => {
    const data = await res.json();
    if (!data.success) throw new Error(data.message);
    return data.data;
  });

export const getWithdrawalHistory = (investorId) =>
  fetch(`${BASE_URL}/withdrawals/investor/${investorId}`).then(handleResponse);

// ── CSV Export ───────────────────────────────────────────────────────────────

export const downloadCSV = (investorId = null) => {
  const url = investorId
    ? `${BASE_URL}/export/csv?investorId=${investorId}`
    : `${BASE_URL}/export/csv`;

  // Trigger browser download
  const a = document.createElement('a');
  a.href = url;
  a.download = investorId ? `withdrawals_investor_${investorId}.csv` : 'withdrawals_all.csv';
  a.click();
};
