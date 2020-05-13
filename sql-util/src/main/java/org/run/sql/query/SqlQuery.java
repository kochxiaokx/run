package org.run.sql.query;


import org.run.base.asserts.StringUtil;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

public class SqlQuery extends Query {

    public SqlQuery(PageRequest pageRequest, String sql, String countSql, Map<String, Object> map) {
        this.params = new Object[0];
        this.page = pageRequest.getPageNo();
        this.limit = pageRequest.getPageSize();
        this.map = map;
        this.countHql = countSql;
        this.hql = sql;
        if(sql.indexOf("where") != -1
                ||sql.indexOf("WHERE") != -1){

            condition ++;
        }
    }
    public SqlQuery sqlAndConditionEqual(String fieldName,String mapKey,Class<?>...valueType){
        baseAndCondition(fieldName,mapKey,EQUAL,valueType);
        return this;
    }
    public SqlQuery sqlAndConditionLike(String fieldName,String mapKey,Class<?>...valueType){
        baseAndCondition(fieldName,mapKey,LIKE,valueType);
        return this;
    }
    public SqlQuery sqlAndConditionGreat(String fieldName,String mapKey,Class<?>...valueType){
        baseAndCondition(fieldName,mapKey,GREAT_THAN,valueType);
        return this;
    }
    public SqlQuery sqlAndConditionGreatEqual(String fieldName,String mapKey,Class<?>...valueType){
        baseAndCondition(fieldName,mapKey,GREAT_THAN_EQUAL,valueType);
        return this;
    }
    public SqlQuery sqlAndConditionLess(String fieldName,String mapKey,Class<?>...valueType){
        baseAndCondition(fieldName,mapKey,LESS_THAN,valueType);
        return this;
    }
    public SqlQuery sqlAndConditionLessEqual(String fieldName,String mapKey,Class<?>...valueType){
        baseAndCondition(fieldName,mapKey,LESS_THAN_EQUAL,valueType);
        return this;
    }
    public SqlQuery sqlAnd(String condition){
        if(this.condition > 0){
            condition = AND + condition;
        }
        this.hql = this.hql + condition;
        this.countHql = this.countHql + condition;
        this.condition++;
        return this;
    }
    public Query sqlLike(String condition){
        return this.sqlAnd(condition);
    }
    private void baseAndCondition(String fieldName,String mapKey,String condition,Class<?>...valueType){
        if(this.map.get(mapKey) == null) return;
        String value =  this.map.get(mapKey).toString();
        if(valueType != null && valueType.length > 0){
            Class<?> type = valueType[0];
            if((type == String.class || type == Date.class || type == Timestamp.class)
                    && (condition.equals(EQUAL) || condition.equals(LESS_THAN_EQUAL) || condition.equals(GREAT_THAN_EQUAL))){
                value = charEqual(value);
            }else if(type == String.class && condition.equals(LIKE)){
                value = charLike(value);
            }
        }
        condition = fieldName + condition;
        if(this.condition > 0){
            condition = AND + condition;
        }
        this.hql = this.hql + condition + value;
        this.countHql = this.countHql+condition+value;
        this.condition ++;
    }
    public SqlQuery in(String fieldName,String mapKey,Class<?>...type){
        if(this.map.get(mapKey) == null || this.map.get(mapKey).toString().trim().equals("")) return this;
        return sqlAnd(SPACE+fieldName + preconditionIn(this.map.get(mapKey).toString(),type));
    }
    public SqlQuery notIn(String fieldName,String mapKey,Class<?>...type){
        if(this.map.get(mapKey) == null || this.map.get(mapKey).toString().trim().equals("")) return this;
        return sqlAnd(SPACE+fieldName +SPACE+"not"+SPACE+preconditionIn(this.map.get(mapKey).toString(),type));
    }

    public SqlQuery in(String fieldName,Object[] params,Class<?>...type){
        String condition = getInArrayCondition(params);
        if(StringUtil.isEmpty(condition))return this;
        if(type != null && type.length > 0 && type[0] == String.class) {
            String newCondition = "";
            String[] coditions =  condition.split(",");
            for(String index : coditions) {
                newCondition += "\'" + index+"\',";
            }
            newCondition = newCondition.substring(0,newCondition.length() - 1);
            return sqlAnd(SPACE+fieldName+SPACE+"in"+"(\'"+newCondition+"\')");
        }
        return sqlAnd(SPACE+fieldName+SPACE+"in"+"("+condition+")");

    }



    public SqlQuery in(String fieldName,String mapKey) {
        Object condition = this.map.get(mapKey);
        if(StringUtil.isEmpty(condition))return this;
        if(condition instanceof String) {
            String newCondition = "";
            String[] coditions =  condition.toString().split(",");
            for(String index : coditions) {
                newCondition += "\'" + index+"\',";
            }
            newCondition = newCondition.substring(0,newCondition.length() - 1);

            return sqlAnd(SPACE+fieldName+SPACE+"in"+"("+newCondition+")");
        }else if(condition instanceof Object[]) {
            Object[] conditionArraay = (Object[])condition;
            String conditions = "";
            for(int i = 0 ; i < conditionArraay.length ; i++) {
                conditions = "\'"+conditionArraay[i]+"\',";
            }
            if(!StringUtil.isEmpty(conditions)) conditions = conditions.substring(0, conditions.length()-1);
            return sqlAnd(SPACE+fieldName+SPACE+"in"+"("+conditions+")");
        }
        return this;
    }

    public SqlQuery notIn(String fieldName,Object[] params){
        String condition = getInArrayCondition(params);
        if(StringUtil.isEmpty(condition))return this;
        return sqlAnd(SPACE+fieldName+SPACE+"not in"+"(\'"+condition+"\')");
    }
    @Override
    public SqlQuery sidxFromMap(String mapKey) {
        super.sidxFromMap(mapKey);
        return this;
    }
    @Override
    public SqlQuery orderByFromMap(String mapKey) {
        super.orderByFromMap(mapKey);
        return this;
    }
    private String getInArrayCondition(Object[] params){
        if(params==null || params.length <= 0)return null;
        String condition = "";
        for(int i = 0 ; i < params.length ; i++){
            condition +=params[i]+",";
        }
        condition = condition.substring(0,condition.length()-1); //去掉最后一个逗号
        return condition;
    }
    private String preconditionIn(String condition,Class<?>...type){
        if(type != null && type.length > 0 && type[0] == String.class){
            return SPACE + "in(\'"+condition+"\')"+SPACE;
        }else {
            return SPACE + "in("+condition+")"+SPACE;
        }
    }

    private static String charEqual(String value){
        return "\'"+value+"\'";
    }
    private static String charLike(String value){
        return "\'%"+value+"%\'";
    }
    public String getSql() {
        return hql;
    }

    @Override
    public SqlQuery ok(){
        super.ok();
        return this;
    }
    @Override
    public SqlQuery where(){
        super.where();
        return this;
    }
    @Override
    public SqlQuery orderBy(String order){
        super.orderBy(order);
        return this;
    }
    @Override
    public SqlQuery orderByIdDESC(){
        super.orderByIdDESC();
        return this;
    }
    @Override
    public Integer getPage() {
        return page;
    }

    @Override
    public Integer getLimit() {
        return limit;
    }

    @Override
    public Object[] getParams() {
        return this.params = new Object[0];
    }
    @Override
    public String getHQL() {
        return null;
    }

    @Override
    public String getCountHQL() {
        return null;
    }

    @Override
    protected String initSQL() {
        return this.hql;
    }

    @Override
    protected String initCountSQL() {
        return this.countHql;
    }
}
