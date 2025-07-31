import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './EmployeeList.css';

const EmployeeList = () => {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchEmployees();
  }, []);

  const fetchEmployees = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const config = {
        headers: { Authorization: `Bearer ${token}` }
      };

      const response = await axios.get('/api/employees/list', config);
      setEmployees(response.data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching employees:', error);
      setError('Failed to load employees');
      setLoading(false);
    }
  };

  const handleDelete = async (employeeId) => {
    if (window.confirm('Are you sure you want to delete this employee?')) {
      try {
        const token = localStorage.getItem('token');
        const config = {
          headers: { Authorization: `Bearer ${token}` }
        };

        await axios.delete(`/api/employees/${employeeId}`, config);
        fetchEmployees(); // Refresh the list
        alert('Employee deleted successfully');
      } catch (error) {
        console.error('Error deleting employee:', error);
        alert('Error deleting employee');
      }
    }
  };

  const handleView = (employee) => {
    // Implement view functionality
    alert(`Viewing employee: ${employee.name}`);
  };

  const filteredEmployees = employees.filter(employee =>
    employee.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    employee.designation.toLowerCase().includes(searchTerm.toLowerCase()) ||
    employee.employeeId.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="employee-list-container">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Loading employees...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="employee-list-container">
        <div className="error-container">
          <h2>Error</h2>
          <p>{error}</p>
          <button onClick={fetchEmployees} className="retry-btn">Retry</button>
        </div>
      </div>
    );
  }

  return (
    <div className="employee-list-container">
      {/* Header */}
      <div className="list-header">
        <div className="header-left">
          <h1>📋 Employees List</h1>
        </div>
        <div className="header-right">
          <div className="search-container">
            <input
              type="text"
              placeholder="Search by name or designation"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
            <span className="search-icon">🔍</span>
          </div>
        </div>
      </div>

      {/* Employee Table */}
      <div className="table-container">
        <table className="employee-table">
          <thead>
            <tr>
              <th>Employee ID</th>
              <th>Name</th>
              <th>Designation</th>
              <th>District</th>
              <th>Contact Number</th>
              <th>Email</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {filteredEmployees.length === 0 ? (
              <tr>
                <td colSpan="8" className="no-data">
                  {searchTerm ? 'No employees found matching your search' : 'No employees found'}
                </td>
              </tr>
            ) : (
              filteredEmployees.map((employee) => (
                <tr key={employee.id}>
                  <td className="employee-id">{employee.employeeId}</td>
                  <td className="employee-name">{employee.name}</td>
                  <td className="employee-designation">{employee.designation}</td>
                  <td className="employee-district">{employee.district}</td>
                  <td className="employee-contact">{employee.contactNumber}</td>
                  <td className="employee-email">{employee.email}</td>
                  <td className="employee-status">
                    <select 
                      defaultValue={employee.status}
                      className={`status-select ${employee.status.toLowerCase()}`}
                    >
                      <option value="ACTIVE">Active</option>
                      <option value="INACTIVE">Inactive</option>
                      <option value="PENDING">Pending</option>
                    </select>
                  </td>
                  <td className="employee-actions">
                    <button 
                      onClick={() => handleView(employee)}
                      className="btn-view"
                    >
                      👁️ View
                    </button>
                    <button 
                      onClick={() => handleDelete(employee.id)}
                      className="btn-delete"
                    >
                      🗑️ Delete
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Summary */}
      <div className="list-summary">
        <p>Total Employees: {filteredEmployees.length}</p>
        <button onClick={fetchEmployees} className="refresh-btn">
          🔄 Refresh
        </button>
      </div>
    </div>
  );
};

export default EmployeeList; 