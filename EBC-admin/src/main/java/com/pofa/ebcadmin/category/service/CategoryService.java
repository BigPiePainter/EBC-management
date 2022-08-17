package com.pofa.ebcadmin.category.service;

import com.pofa.ebcadmin.category.dto.Category;
import com.pofa.ebcadmin.category.entity.CategoryHistoryInfo;
import com.pofa.ebcadmin.category.entity.CategoryInfo;

import java.util.List;

public interface CategoryService {
    List<CategoryInfo> getCategorys(Category.GetDTO dto);

    int addCategory(Category.AddDTO dto);

    int deleteCategoryByUid(Long uid);

    int editCategory(Category.EditDTO dto);




    List<CategoryHistoryInfo> getCategoryHistorys(Category.GetHistoryDTO dto);

    int addCategoryHistory(Category.AddHistoryDTO dto);

    int editCategoryHistory(Category.EditHistoryDTO dto);

    int deleteCategoryHistoryByUid(Long uid);


}
