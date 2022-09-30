package com.pofa.ebcadmin.mybatisplus;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.io.Serial;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        methodList.add(new InsertBatchSomeColumn());
        methodList.add(new ReplaceBatchSomeColumn());
        return methodList;
    }

    //insert into table (a,b,c) values (1,2,3),
    //replace into

    static class ReplaceBatchSomeColumn extends AbstractMethod {
        @Serial
        private static final long serialVersionUID = -2520803692505184308L;
        private static final String METHOD_NAME = "replaceBatchSomeColumn";

        public ReplaceBatchSomeColumn() {
            super(METHOD_NAME);
        }

        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            var fieldList = tableInfo.getFieldList();
            var columnScript = tableInfo.getKeyInsertSqlColumn(false, false) + fieldList.stream().map(TableFieldInfo::getInsertSqlColumn).collect(Collectors.joining(EMPTY));
            columnScript = LEFT_BRACKET + columnScript.substring(0, columnScript.length() - 1) + RIGHT_BRACKET;
            var replaceIntoValue = tableInfo.getKeyInsertSqlProperty(true, ENTITY_DOT, false) + fieldList.stream().map(i -> i.getInsertSqlProperty(ENTITY_DOT)).collect(Collectors.joining(EMPTY));
            replaceIntoValue = LEFT_BRACKET + replaceIntoValue.substring(0, replaceIntoValue.length() - 1) + RIGHT_BRACKET;
            var valuesScript = SqlScriptUtils.convertForeach(replaceIntoValue, "list", null, ENTITY, COMMA);

            var sql = String.format("<script>\nREPLACE INTO %s %s VALUES %s\n</script>", tableInfo.getTableName(), columnScript, valuesScript);
            var sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
            // 表包含主键处理逻辑，如果不包含主键当普通字段处理
            var keyGenerator = (KeyGenerator) NoKeyGenerator.INSTANCE;
            var keyProperty = tableInfo.getKeyProperty();
            var keyColumn = tableInfo.getKeyColumn();
            if (StringUtils.isNotBlank(tableInfo.getKeyProperty())) {
                if (tableInfo.getIdType() == IdType.AUTO) {
                    keyGenerator = Jdbc3KeyGenerator.INSTANCE;
                } else {
                    if (Objects.nonNull(tableInfo.getKeySequence())) {
                        keyGenerator = TableInfoHelper.genKeyGenerator(METHOD_NAME, tableInfo, builderAssistant);
                    }
                }
            }
            return addInsertMappedStatement(mapperClass, modelClass, METHOD_NAME, sqlSource, keyGenerator, keyProperty, keyColumn);
        }
    }

}
