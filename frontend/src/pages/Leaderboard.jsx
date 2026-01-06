import React, { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import { leaderboardAPI } from "../services/api";
import { useAuth } from "../auth/AuthContext";
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell
} from "recharts";
import "./Leaderboard.css";

const COLORS = ["#6366f1", "#22c55e", "#f59e0b", "#ef4444", "#0ea5e9"];

const Leaderboard = () => {
  const { user } = useAuth(); // current user
  const [users, setUsers] = useState([]);
  const [view, setView] = useState([]);
  const [sortKey, setSortKey] = useState("rank");
  const [filter, setFilter] = useState("all");

  useEffect(() => {
    loadLeaderboard();
  }, []);

  useEffect(() => {
    applyFilters();
  }, [users, sortKey, filter]);

  const loadLeaderboard = async () => {
    const data = await leaderboardAPI.getLeaderboard();
    const ranked = data
      .sort((a, b) => b.points - a.points)
      .map((u, i) => ({ ...u, rank: i + 1 }));
    setUsers(ranked);
  };

  const applyFilters = () => {
    let list = [...users];
    if (filter === "top10") list = list.slice(0, 10);
    if (filter === "top25") list = list.slice(0, 25);

    list.sort((a, b) => {
      if (sortKey === "name") return a.name.localeCompare(b.name);
      if (sortKey === "points") return b.points - a.points;
      return a.rank - b.rank;
    });

    setView(list);
  };

  /* ================= EXPORT ================= */
  const exportCSV = () => {
    const rows = [["Rank", "Name", "Points"]];
    view.forEach(u => rows.push([u.rank, u.name, u.points]));
    const csv = rows.map(r => r.join(",")).join("\n");
    const blob = new Blob([csv], { type: "text/csv" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = "leaderboard.csv";
    link.click();
  };


  const chartData = users.slice(0, 6);

  return (
    <div className="leaderboard-page">
      <Navbar />

      <div className="leaderboard-container">

        {/* HEADER */}
        <div className="leaderboard-header">
          <h1>Volunteer Leaderboard 🏆</h1>
          <p>Recognizing top contributors</p>
        </div>

        {/* CONTROLS */}
        <div className="leaderboard-controls">
          <select onChange={e => setSortKey(e.target.value)}>
            <option value="rank">Sort by Rank</option>
            <option value="name">Sort by Name</option>
            <option value="points">Sort by Points</option>
          </select>

          <select onChange={e => setFilter(e.target.value)}>
            <option value="all">All</option>
            <option value="top10">Top 10</option>
            <option value="top25">Top 25</option>
          </select>

          <button onClick={exportCSV} className="export-btn">Export CSV</button>
        </div>

        {/* TABLE */}
        <div className="leaderboard-card mobile-scroll">
          <div className="table-header">
            <span>Rank</span>
            <span>Name</span>
            <span>Points</span>
          </div>

          {view.map(u => {
            const isCurrentUser = user && u.id === user.id;

            return (
              <div
                key={u.id}
                className={`table-row ${isCurrentUser ? "current-user" : ""}`}
              >
                <span className="rank-cell">
                  {u.rank === 1 && "🥇"}
                  {u.rank === 2 && "🥈"}
                  {u.rank === 3 && "🥉"}
                  {u.rank > 3 && `${u.rank}`}
                </span>

                <span className="name-cell">
                  {u.name}
                  {isCurrentUser && <span className="you-badge">You</span>}
                </span>

                <span className="points-pill">{u.points} pts</span>
              </div>
            );
          })}
        </div>

        {/* ANALYSIS (BOTTOM) */}
        <div className="leaderboard-analysis">
          <h2>Performance Analysis 📊</h2>

          <div className="analysis-grid">
            <div className="analysis-card">
              <h3>Top Volunteers</h3>
              <ResponsiveContainer width="100%" height={260}>
                <BarChart data={chartData}>
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="points" fill="#6366f1" radius={[8, 8, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>

            <div className="analysis-card">
              <h3>Contribution Share</h3>
              <ResponsiveContainer width="100%" height={260}>
                <PieChart>
                  <Pie data={chartData} dataKey="points" outerRadius={90}>
                    {chartData.map((_, i) => (
                      <Cell key={i} fill={COLORS[i % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
};

export default Leaderboard;
