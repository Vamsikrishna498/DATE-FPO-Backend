import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './SuperAdminDashboard.css';

const SuperAdminDashboard = () => {
  const [dashboardStats, setDashboardStats] = useState({
    totalFarmers: 0,
    totalEmployees: 0,
    pendingUsers: 0,
    approvedUsers: 0
  });
  const [pendingRegistrations, setPendingRegistrations] = useState([]);
  const [approvedUsers, setApprovedUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedRole, setSelectedRole] = useState('FARMER');

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const token = localStorage.getItem('token');
      const config = {
        headers: { Authorization: `Bearer ${token}` }
      };

      // Fetch dashboard stats
      const statsResponse = await axios.get('/api/super-admin/dashboard/stats', config);
      setDashboardStats(statsResponse.data);

      // Fetch pending registrations
      const pendingResponse = await axios.get('/api/super-admin/pending-registrations', config);
      setPendingRegistrations(pendingResponse.data);

      // Fetch approved users
      const approvedResponse = await axios.get('/api/super-admin/approved-users', config);
      setApprovedUsers(approvedResponse.data);

      setLoading(false);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
      setLoading(false);
    }
  };

  const approveUser = async (userId, role) => {
    try {
      const token = localStorage.getItem('token');
      const config = {
        headers: { Authorization: `Bearer ${token}` }
      };

      await axios.put(`/api/auth/users/${userId}/approve`, 
        { role: role }, 
        config
      );

      // Refresh data after approval
      fetchDashboardData();
      alert('User approved successfully! Email sent with credentials.');
    } catch (error) {
      console.error('Error approving user:', error);
      alert('Error approving user. Please try again.');
    }
  };

  const rejectUser = async (userId) => {
    try {
      const token = localStorage.getItem('token');
      const config = {
        headers: { Authorization: `Bearer ${token}` }
      };

      await axios.put(`/api/auth/users/${userId}/status`, 
        { status: 'REJECTED' }, 
        config
      );

      fetchDashboardData();
      alert('User rejected successfully!');
    } catch (error) {
      console.error('Error rejecting user:', error);
      alert('Error rejecting user. Please try again.');
    }
  };

  if (loading) {
    return <div className="loading">Loading Dashboard...</div>;
  }

  return (
    <div className="dashboard-container">
      {/* Header with Banner */}
      <div className="dashboard-header">
        <div className="header-banner">
          <h1>Dashboard Overview</h1>
          <p>Welcome back! Here's what's happening with your agricultural data.</p>
        </div>
        <div className="header-filters">
          <button className="refresh-btn">🔄 Refresh</button>
          <button className="filter-btn active">Today</button>
          <button className="filter-btn">This Month</button>
          <button className="filter-btn">This Year</button>
        </div>
      </div>

      {/* Key Metrics Cards */}
      <div className="metrics-section">
        <div className="metric-card farmers">
          <div className="metric-icon">👥</div>
          <div className="metric-content">
            <h3>Farmers</h3>
            <div className="metric-number">{dashboardStats.totalFarmers}</div>
            <div className="metric-change positive">+12.4%</div>
          </div>
        </div>

        <div className="metric-card employees">
          <div className="metric-icon">👤</div>
          <div className="metric-content">
            <h3>Employees</h3>
            <div className="metric-number">{dashboardStats.totalEmployees}</div>
            <div className="metric-change negative">-3.0%</div>
          </div>
        </div>

        <div className="metric-card pending">
          <div className="metric-icon">⏳</div>
          <div className="metric-content">
            <h3>Pending Approvals</h3>
            <div className="metric-number">{dashboardStats.pendingUsers}</div>
            <div className="metric-change neutral">+0.0%</div>
          </div>
        </div>
      </div>

      {/* Main Content Area */}
      <div className="main-content">
        {/* Pending Registrations */}
        <div className="content-section">
          <div className="section-header">
            <h2>Pending Registrations</h2>
            <span className="count-badge">{pendingRegistrations.length}</span>
          </div>
          
          <div className="registration-list">
            {pendingRegistrations.length === 0 ? (
              <p className="no-data">No pending registrations</p>
            ) : (
              pendingRegistrations.map((user) => (
                <div key={user.id} className="registration-item">
                  <div className="user-info">
                    <h4>{user.firstName} {user.lastName}</h4>
                    <p>{user.email}</p>
                    <p>Phone: {user.phoneNumber}</p>
                    <span className="status pending">Pending Approval</span>
                  </div>
                  
                  <div className="approval-actions">
                    <select 
                      value={selectedRole} 
                      onChange={(e) => setSelectedRole(e.target.value)}
                      className="role-select"
                    >
                      <option value="FPO">FPO</option>
                      <option value="FARMER">Farmer</option>
                      <option value="EMPLOYEE">Employee</option>
                    </select>
                    
                    <button 
                      onClick={() => approveUser(user.id, selectedRole)}
                      className="approve-btn"
                    >
                      Approve
                    </button>
                    
                    <button 
                      onClick={() => rejectUser(user.id)}
                      className="reject-btn"
                    >
                      Reject
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Recent Activities */}
        <div className="content-section">
          <div className="section-header">
            <h2>Recent Activities</h2>
            <a href="#" className="view-all">View All</a>
          </div>
          
          <div className="activities-list">
            <div className="activity-item">
              <span className="activity-text">Farmer profile updated</span>
              <span className="activity-time">20m ago</span>
              <span className="activity-status success">success</span>
            </div>
            <div className="activity-item">
              <span className="activity-text">Employee profile updated</span>
              <span className="activity-time">10m ago</span>
              <span className="activity-status success">success</span>
            </div>
            <div className="activity-item">
              <span className="activity-text">New FPO application submitted</span>
              <span className="activity-time">Just now</span>
              <span className="activity-status pending">pending</span>
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="content-section">
          <div className="section-header">
            <h2>Quick Actions</h2>
          </div>
          
          <div className="quick-actions">
            <button className="action-btn add-farmer">
              👤 Add New Farmer
            </button>
            <button className="action-btn add-employee">
              👤 Add Employee
            </button>
            <button className="action-btn generate-report">
              📊 Generate Report
            </button>

          </div>
        </div>
      </div>
    </div>
  );
};

export default SuperAdminDashboard; 