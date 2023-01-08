package com.pofa.ebcadmin.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pofa.ebcadmin.category.dao.CategoryDao;
import com.pofa.ebcadmin.category.dao.CategoryHistoryDao;
import com.pofa.ebcadmin.category.dto.Category;
import com.pofa.ebcadmin.category.entity.CategoryHistoryInfo;
import com.pofa.ebcadmin.category.entity.CategoryInfo;
import com.pofa.ebcadmin.category.service.CategoryService;
import com.pofa.ebcadmin.product.dao.ProductDao;
import com.pofa.ebcadmin.product.entity.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    public CategoryDao categoryDao;
    @Autowired
    public CategoryHistoryDao categoryHistoryDao;

    @Autowired
    public CategoryInfo categoryInfo;
    @Autowired
    public CategoryHistoryInfo categoryHistoryInfo;


    @Autowired
    public ProductDao productDao;

    @Autowired
    public ProductInfo productInfo;


    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public List<CategoryInfo> getCategorys(Category.GetDTO dto) {
        return categoryDao.selectList(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int addCategory(Category.AddDTO dto) {
        return categoryDao.insert(categoryInfo
                .setName(dto.getName())
                .setNote(dto.getNote()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int deleteCategoryByUid(Long uid) {
        var categoryList = productDao.selectList(new QueryWrapper<ProductInfo>().select("id").eq("first_category", uid).last("limit 1"));
        if (categoryList.isEmpty()) {
            categoryHistoryDao.delete(new QueryWrapper<CategoryHistoryInfo>().eq("category_id", uid));
            categoryDao.delete(new QueryWrapper<CategoryInfo>().eq("uid", uid));
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int editCategory(Category.EditDTO dto) {
        return categoryDao.update(categoryInfo
                        .setName(dto.getName())
                        .setNote(dto.getNote()),
                new UpdateWrapper<CategoryInfo>().eq("uid", dto.getUid()));
    }


    @Override
    public List<CategoryHistoryInfo> getCategoryHistorys(Category.GetHistoryDTO dto) {
        return categoryHistoryDao.selectList(null);
    }

    @Override
    public int addCategoryHistory(Category.AddHistoryDTO dto) {
        return categoryHistoryDao.insert(categoryHistoryInfo
                .setCategoryId(dto.getCategoryId())
                .setDeduction(dto.getDeduction())
                .setInsurance(dto.getInsurance())
                .setStartTime(dto.getStartTime())
                .setNote(dto.getNote())
        );
    }

    @Override
    public int editCategoryHistory(Category.EditHistoryDTO dto) {
        return categoryHistoryDao.update(categoryHistoryInfo
                        .setCategoryId(dto.getCategoryId())
                        .setDeduction(dto.getDeduction())
                        .setInsurance(dto.getInsurance())
                        .setStartTime(dto.getStartTime())
                        .setNote(dto.getNote()),
                new UpdateWrapper<CategoryHistoryInfo>().eq("uid", dto.getUid()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public int deleteCategoryHistoryByUid(Long uid) {
        return categoryHistoryDao.delete(new QueryWrapper<CategoryHistoryInfo>().eq("uid", uid));
    }

}
