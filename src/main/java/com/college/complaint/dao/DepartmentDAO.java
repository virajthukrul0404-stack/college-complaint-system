package com.college.complaint.dao;

import com.college.complaint.model.Department;
import java.util.List;

public interface DepartmentDAO {
    List<Department> findAll();
    Department findById(int id);
    Department findByCode(String code);
}
