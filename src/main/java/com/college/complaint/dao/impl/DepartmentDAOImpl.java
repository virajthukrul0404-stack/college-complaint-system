package com.college.complaint.dao.impl;

import com.college.complaint.dao.DepartmentDAO;
import com.college.complaint.model.Department;
import com.college.complaint.util.DbPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAOImpl implements DepartmentDAO {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentDAOImpl.class);

    @Override
    public List<Department> findAll() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT id, code, name, description, created_at FROM departments ORDER BY name ASC";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all departments", e);
            throw new RuntimeException("Database error fetching departments", e);
        }
        return departments;
    }

    @Override
    public Department findById(int id) {
        String sql = "SELECT id, code, name, description, created_at FROM departments WHERE id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDepartment(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding department by id: " + id, e);
            throw new RuntimeException("Database error fetching department", e);
        }
        return null;
    }

    @Override
    public Department findByCode(String code) {
        String sql = "SELECT id, code, name, description, created_at FROM departments WHERE code = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDepartment(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding department by code: " + code, e);
            throw new RuntimeException("Database error fetching department", e);
        }
        return null;
    }

    private Department mapResultSetToDepartment(ResultSet rs) throws SQLException {
        Department dept = new Department();
        dept.setId(rs.getInt("id"));
        dept.setCode(rs.getString("code"));
        dept.setName(rs.getString("name"));
        dept.setDescription(rs.getString("description"));
        dept.setCreatedAt(rs.getTimestamp("created_at"));
        return dept;
    }
}
