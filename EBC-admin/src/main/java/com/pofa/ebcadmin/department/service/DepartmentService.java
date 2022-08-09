package com.pofa.ebcadmin.department.service;

import com.pofa.ebcadmin.department.dto.Department;
import com.pofa.ebcadmin.department.entity.DepartmentInfo;
import com.pofa.ebcadmin.manufacturer.entity.ManufacturerInfo;

import java.util.List;

public interface DepartmentService {

    int addDepartment(Department.AddDTO dto);

    int editDepartment(Department.EditDTO dto);

    List<DepartmentInfo> getDepartments();

}
