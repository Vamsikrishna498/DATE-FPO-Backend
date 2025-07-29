import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './EmployeeDashboard.css';

const EmployeeDashboard = () => {
  const [activeTab, setActiveTab] = useState('overview');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // Dashboard Data
  const [dashboardStats, setDashboardStats] = useState({
    totalAssigned: 0,
    approved: 0,
    referBack: 0,
    pending: 0,
    rejected: 0,
    completionRate: 0
  });
  
  const [assignedFarmers, setAssignedFarmers] = useState([]);
  const [todoList, setTodoList] = useState({
    pendingKyc: [],
    referBackCases: [],
    newAssignments: []
  });
  
  const [employeeProfile, setEmployeeProfile] = useState(null);
  const [kycFilter, setKycFilter] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  
  // KYC Action States
  const [selectedFarmer, setSelectedFarmer] = useState(null);
  const [kycAction, setKycAction] = useState('');
  const [kycReason, setKycReason] = useState('');
  const [showKycModal, setShowKycModal] = useState(false);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const config = {
        headers: { Authorization: `Bearer ${token}` }
      };

      // Fetch all dashboard data in parallel
      const [statsResponse, farmersResponse, todoResponse, profileResponse] = await Promise.all([
        axios.get('/api/employees/dashboard/stats', config),
        axios.get('/api/employees/dashboard/assigned-farmers', config),
        axios.get('/api/employees/dashboard/todo-list', config),
        axios.get('/api/employees/dashboard/profile', config)
      ]);

      setDashboardStats(statsResponse.data);
      setAssignedFarmers(farmersResponse.data);
      setTodoList(todoResponse.data);
      setEmployeeProfile(profileResponse.data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
      setError('Failed to load dashboard data');
      setLoading(false);
    }
  };

  const handleKycAction = async (farmerId, action, reason = '') => {
    try {
      const token = localStorage.getItem('token');
      const config = {
        headers: { Authorization: `Bearer ${token}` }
      };

      let endpoint = '';
      let requestData = {};

      switch (action) {
        case 'approve':
          endpoint = `/api/employees/kyc/approve/${farmerId}`;
          break;
        case 'refer-back':
          endpoint = `/api/employees/kyc/refer-back/${farmerId}`;
          requestData = { reason };
          break;
        case 'reject':
          endpoint = `/api/employees/kyc/reject/${farmerId}`;
          requestData = { reason };
          break;
        default:
          throw new Error('Invalid KYC action');
      }

      await axios.put(endpoint, requestData, config);
      
      // Refresh dashboard data
      await fetchDashboardData();
      
      // Close modal and reset states
      setShowKycModal(false);
      setSelectedFarmer(null);
      setKycAction('');
      setKycReason('');
      
      alert(`KYC ${action} successful!`);
    } catch (error) {
      console.error('Error performing KYC action:', error);
      alert(`Error: ${error.response?.data || error.message}`);
    }
  };

  const openKycModal = (farmer, action) => {
    setSelectedFarmer(farmer);
    setKycAction(action);
    setShowKycModal(true);
  };

  const getKycStatusBadge = (status) => {
    const statusConfig = {
      'PENDING': { class: 'status-pending', text: 'Pending' },
      'APPROVED': { class: 'status-approved', text: 'Approved' },
      'REJECTED': { class: 'status-rejected', text: 'Rejected' },
      'REFER_BACK': { class: 'status-refer-back', text: 'Refer Back' }
    };
    
    const config = statusConfig[status] || statusConfig['PENDING'];
    return <span className={`status-badge ${config.class}`}>{config.text}</span>;
  };

  const filteredFarmers = assignedFarmers.filter(farmer => {
    const matchesSearch = farmer.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         farmer.contactNumber.includes(searchTerm);
    const matchesKycFilter = !kycFilter || farmer.kycStatus === kycFilter;
    return matchesSearch && matchesKycFilter;
  });

  if (loading) {
    return (
      <div className="employee-dashboard">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Loading Employee Dashboard...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="employee-dashboard">
        <div className="error-container">
          <h2>Error</h2>
          <p>{error}</p>
          <button onClick={fetchDashboardData} className="retry-btn">Retry</button>
        </div>
      </div>
    );
  }

  return (
    <div className="employee-dashboard">
      {/* Header */}
      <div className="dashboard-header">
        <div className="header-content">
          <div className="header-left">
            <h1>Employee Dashboard</h1>
            <p>Welcome back, {employeeProfile?.firstName} {employeeProfile?.lastName}</p>
          </div>
          <div className="header-right">
            <button onClick={fetchDashboardData} className="refresh-btn">
              🔄 Refresh
            </button>
            <div className="user-info">
              <span className="user-email">{employeeProfile?.email}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Navigation Tabs */}
      <div className="dashboard-tabs">
        <button 
          className={`tab-btn ${activeTab === 'overview' ? 'active' : ''}`}
          onClick={() => setActiveTab('overview')}
        >
          📊 Overview
        </button>
        <button 
          className={`tab-btn ${activeTab === 'farmers' ? 'active' : ''}`}
          onClick={() => setActiveTab('farmers')}
        >
          👥 Assigned Farmers
        </button>
        <button 
          className={`tab-btn ${activeTab === 'todo' ? 'active' : ''}`}
          onClick={() => setActiveTab('todo')}
        >
          📋 To-Do List
        </button>
        <button 
          className={`tab-btn ${activeTab === 'profile' ? 'active' : ''}`}
          onClick={() => setActiveTab('profile')}
        >
          👤 Profile
        </button>
      </div>

      {/* Dashboard Content */}
      <div className="dashboard-content">
        {activeTab === 'overview' && (
          <div className="overview-tab">
            {/* Statistics Cards */}
            <div className="stats-grid">
              <div className="stat-card total">
                <div className="stat-icon">👥</div>
                <div className="stat-content">
                  <h3>Total Assigned</h3>
                  <div className="stat-number">{dashboardStats.totalAssigned}</div>
                </div>
              </div>
              
              <div className="stat-card approved">
                <div className="stat-icon">✅</div>
                <div className="stat-content">
                  <h3>Approved</h3>
                  <div className="stat-number">{dashboardStats.approved}</div>
                </div>
              </div>
              
              <div className="stat-card refer-back">
                <div className="stat-icon">🔄</div>
                <div className="stat-content">
                  <h3>Refer Back</h3>
                  <div className="stat-number">{dashboardStats.referBack}</div>
                </div>
              </div>
              
              <div className="stat-card pending">
                <div className="stat-icon">⏳</div>
                <div className="stat-content">
                  <h3>Pending</h3>
                  <div className="stat-number">{dashboardStats.pending}</div>
                </div>
              </div>
              
              <div className="stat-card rejected">
                <div className="stat-icon">❌</div>
                <div className="stat-content">
                  <h3>Rejected</h3>
                  <div className="stat-number">{dashboardStats.rejected}</div>
                </div>
              </div>
              
              <div className="stat-card completion">
                <div className="stat-icon">📈</div>
                <div className="stat-content">
                  <h3>Completion Rate</h3>
                  <div className="stat-number">{dashboardStats.completionRate.toFixed(1)}%</div>
                </div>
              </div>
            </div>

            {/* Quick Actions */}
            <div className="quick-actions">
              <h2>Quick Actions</h2>
              <div className="action-buttons">
                <button onClick={() => setActiveTab('farmers')} className="action-btn">
                  👥 View All Farmers
                </button>
                <button onClick={() => setActiveTab('todo')} className="action-btn">
                  📋 View To-Do List
                </button>
                <button onClick={fetchDashboardData} className="action-btn">
                  🔄 Refresh Data
                </button>
              </div>
            </div>

            {/* Recent Activity */}
            <div className="recent-activity">
              <h2>Recent Activity</h2>
              <div className="activity-list">
                {assignedFarmers.slice(0, 5).map(farmer => (
                  <div key={farmer.id} className="activity-item">
                    <div className="activity-content">
                      <span className="activity-text">
                        {farmer.name} - {getKycStatusBadge(farmer.kycStatus)}
                      </span>
                      <span className="activity-time">
                        {farmer.kycReviewedDate ? `Reviewed: ${farmer.kycReviewedDate}` : 'Not reviewed'}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {activeTab === 'farmers' && (
          <div className="farmers-tab">
            <div className="farmers-header">
              <h2>Assigned Farmers</h2>
              <div className="farmers-filters">
                <input
                  type="text"
                  placeholder="Search farmers..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="search-input"
                />
                <select
                  value={kycFilter}
                  onChange={(e) => setKycFilter(e.target.value)}
                  className="filter-select"
                >
                  <option value="">All KYC Status</option>
                  <option value="PENDING">Pending</option>
                  <option value="APPROVED">Approved</option>
                  <option value="REFER_BACK">Refer Back</option>
                  <option value="REJECTED">Rejected</option>
                </select>
              </div>
            </div>

            <div className="farmers-table">
              <table>
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Contact</th>
                    <th>Location</th>
                    <th>KYC Status</th>
                    <th>Submitted Date</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredFarmers.map(farmer => (
                    <tr key={farmer.id}>
                      <td>{farmer.name}</td>
                      <td>{farmer.contactNumber}</td>
                      <td>{farmer.district}, {farmer.state}</td>
                      <td>{getKycStatusBadge(farmer.kycStatus)}</td>
                      <td>{farmer.kycSubmittedDate || 'Not submitted'}</td>
                      <td>
                        <div className="action-buttons">
                          {farmer.kycStatus === 'PENDING' && (
                            <>
                              <button 
                                onClick={() => openKycModal(farmer, 'approve')}
                                className="btn-approve"
                              >
                                ✅ Approve
                              </button>
                              <button 
                                onClick={() => openKycModal(farmer, 'refer-back')}
                                className="btn-refer-back"
                              >
                                🔄 Refer Back
                              </button>
                              <button 
                                onClick={() => openKycModal(farmer, 'reject')}
                                className="btn-reject"
                              >
                                ❌ Reject
                              </button>
                            </>
                          )}
                          {farmer.kycStatus === 'REFER_BACK' && (
                            <>
                              <button 
                                onClick={() => openKycModal(farmer, 'approve')}
                                className="btn-approve"
                              >
                                ✅ Approve
                              </button>
                              <button 
                                onClick={() => openKycModal(farmer, 'reject')}
                                className="btn-reject"
                              >
                                ❌ Reject
                              </button>
                            </>
                          )}
                          {(farmer.kycStatus === 'APPROVED' || farmer.kycStatus === 'REJECTED') && (
                            <span className="no-actions">No actions available</span>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {activeTab === 'todo' && (
          <div className="todo-tab">
            <h2>To-Do List</h2>
            
            {/* Pending KYC */}
            <div className="todo-section">
              <h3>Pending KYC Verifications ({todoList.totalPendingKyc})</h3>
              <div className="todo-list">
                {todoList.pendingKyc.length === 0 ? (
                  <p className="no-tasks">No pending KYC verifications</p>
                ) : (
                  todoList.pendingKyc.map(farmer => (
                    <div key={farmer.id} className="todo-item">
                      <div className="todo-content">
                        <h4>{farmer.name}</h4>
                        <p>{farmer.contactNumber} • {farmer.district}, {farmer.state}</p>
                        <p>Submitted: {farmer.kycSubmittedDate || 'Not submitted'}</p>
                      </div>
                      <div className="todo-actions">
                        <button 
                          onClick={() => openKycModal(farmer, 'approve')}
                          className="btn-approve"
                        >
                          ✅ Approve
                        </button>
                        <button 
                          onClick={() => openKycModal(farmer, 'refer-back')}
                          className="btn-refer-back"
                        >
                          🔄 Refer Back
                        </button>
                        <button 
                          onClick={() => openKycModal(farmer, 'reject')}
                          className="btn-reject"
                        >
                          ❌ Reject
                        </button>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>

            {/* Refer Back Cases */}
            <div className="todo-section">
              <h3>Refer Back Cases ({todoList.totalReferBack})</h3>
              <div className="todo-list">
                {todoList.referBackCases.length === 0 ? (
                  <p className="no-tasks">No refer back cases</p>
                ) : (
                  todoList.referBackCases.map(farmer => (
                    <div key={farmer.id} className="todo-item refer-back">
                      <div className="todo-content">
                        <h4>{farmer.name}</h4>
                        <p>{farmer.contactNumber} • {farmer.district}, {farmer.state}</p>
                        <p><strong>Reason:</strong> {farmer.kycReferBackReason}</p>
                        <p>Reviewed: {farmer.kycReviewedDate}</p>
                      </div>
                      <div className="todo-actions">
                        <button 
                          onClick={() => openKycModal(farmer, 'approve')}
                          className="btn-approve"
                        >
                          ✅ Approve
                        </button>
                        <button 
                          onClick={() => openKycModal(farmer, 'reject')}
                          className="btn-reject"
                        >
                          ❌ Reject
                        </button>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>

            {/* New Assignments */}
            <div className="todo-section">
              <h3>New Assignments ({todoList.totalNewAssignments})</h3>
              <div className="todo-list">
                {todoList.newAssignments.length === 0 ? (
                  <p className="no-tasks">No new assignments</p>
                ) : (
                  todoList.newAssignments.map(farmer => (
                    <div key={farmer.id} className="todo-item new">
                      <div className="todo-content">
                        <h4>{farmer.name}</h4>
                        <p>{farmer.contactNumber} • {farmer.district}, {farmer.state}</p>
                        <p>Newly assigned - KYC not yet started</p>
                      </div>
                      <div className="todo-actions">
                        <button 
                          onClick={() => setActiveTab('farmers')}
                          className="btn-view"
                        >
                          👁️ View Details
                        </button>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>
          </div>
        )}

        {activeTab === 'profile' && (
          <div className="profile-tab">
            <h2>Employee Profile</h2>
            {employeeProfile && (
              <div className="profile-card">
                <div className="profile-header">
                  <div className="profile-avatar">
                    {employeeProfile.firstName?.charAt(0)}{employeeProfile.lastName?.charAt(0)}
                  </div>
                  <div className="profile-info">
                    <h3>{employeeProfile.firstName} {employeeProfile.lastName}</h3>
                    <p>{employeeProfile.email}</p>
                    <p>{employeeProfile.contactNumber}</p>
                  </div>
                </div>
                
                <div className="profile-details">
                  <div className="detail-row">
                    <span className="detail-label">Role:</span>
                    <span className="detail-value">{employeeProfile.role}</span>
                  </div>
                  <div className="detail-row">
                    <span className="detail-label">Location:</span>
                    <span className="detail-value">{employeeProfile.district}, {employeeProfile.state}</span>
                  </div>
                  <div className="detail-row">
                    <span className="detail-label">Education:</span>
                    <span className="detail-value">{employeeProfile.education}</span>
                  </div>
                  <div className="detail-row">
                    <span className="detail-label">Experience:</span>
                    <span className="detail-value">{employeeProfile.experience}</span>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}
      </div>

      {/* KYC Action Modal */}
      {showKycModal && selectedFarmer && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3>KYC Action - {selectedFarmer.name}</h3>
              <button onClick={() => setShowKycModal(false)} className="close-btn">×</button>
            </div>
            <div className="modal-content">
              <p>Are you sure you want to <strong>{kycAction}</strong> the KYC for {selectedFarmer.name}?</p>
              
              {(kycAction === 'refer-back' || kycAction === 'reject') && (
                <div className="reason-input">
                  <label>Reason:</label>
                  <textarea
                    value={kycReason}
                    onChange={(e) => setKycReason(e.target.value)}
                    placeholder={`Enter reason for ${kycAction}...`}
                    rows="3"
                    required
                  />
                </div>
              )}
              
              <div className="modal-actions">
                <button 
                  onClick={() => setShowKycModal(false)}
                  className="btn-cancel"
                >
                  Cancel
                </button>
                <button 
                  onClick={() => handleKycAction(selectedFarmer.id, kycAction, kycReason)}
                  className={`btn-confirm btn-${kycAction}`}
                  disabled={(kycAction === 'refer-back' || kycAction === 'reject') && !kycReason.trim()}
                >
                  Confirm {kycAction}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default EmployeeDashboard; 