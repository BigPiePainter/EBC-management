package com.pofa.ebcadmin.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;

import java.util.Objects;

public class CustomQueryWrapper<T> extends QueryWrapper<T> {
    @Override
    public String getSqlSegment() {
        if (!isEmptyOfNormal()) {
            return SqlKeyword.AND + " " + this.expression.getSqlSegment() + this.lastSql.getStringValue();
        } else {
            return this.expression.getSqlSegment() + this.lastSql.getStringValue();
        }
    }
}
