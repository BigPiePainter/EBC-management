package com.pofa.ebcadmin.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.segments.NormalSegmentList;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

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

    @Override
    public String getCustomSqlSegment() {
        MergeSegments expression = this.getExpression();
        if (Objects.nonNull(expression)) {
            NormalSegmentList normal = expression.getNormal();
            String sqlSegment = this.expression.getSqlSegment() + this.lastSql.getStringValue();
            if (StringUtils.isNotBlank(sqlSegment)) {
                if (normal.isEmpty()) {
                    return sqlSegment;
                }

                return "WHERE " + sqlSegment;
            }
        }

        return "";
    }
}
