package org.run.sql.query;


import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

public class MapQuery extends Query {
    MapQuery(Query query){
        this.hql = query.hql;
        this.countHql = query.countHql;
        this.page = query.page;
        this.limit = query.limit;
        this.clazz = query.clazz;
        this.fields = query.fields;
        this.map = query.map;
        this.params = new Object[0];
        if(hql.indexOf("where") != -1
                ||hql.indexOf("WHERE") != -1){

            condition ++;
        }
    }

    MapQuery(PageRequest pageRequest, String sql, String countSql, Map<String, Object> map, Class<?> clazz) {
        this.page = pageRequest.getPageNo();
        this.limit = pageRequest.getPageSize();
        this.clazz = clazz;
        this.map = map;
        this.hql = sql;
        this.countHql = countSql;
        if(sql.indexOf("where") != -1){
            condition ++;
        }
    }

    public MapQuery mapConditionEqual(String fieldName,String mapKey){
        try {
            setErrorMethodName("mapConditionAnd()");
            Field field =     clazz.getDeclaredField(fieldName);
            this.andMapCondition(field,mapKey,EQUAL);
        } catch (NoSuchFieldException e) {
            System.err.println("字段名错误,方法名为:"+this.errorMethodName);
            e.printStackTrace();
        }
        return this;
    }
    public MapQuery mapConditionLike(String fieldName,String mapKey){
        try {
            setErrorMethodName("mapConditionLike()");
            Field field =     clazz.getDeclaredField(fieldName);
            this.andMapCondition(field,mapKey,LIKE);
        } catch (NoSuchFieldException e) {
            System.err.println("字段名错误,方法名为:"+this.errorMethodName);
            e.printStackTrace();
        }
        return this;
    }
    public MapQuery mapConditionGreatThan(String fieldName,String mapKey){
        try {
            setErrorMethodName("mapConditionGreatThan()");
            Field field =     clazz.getDeclaredField(fieldName);
            this.andMapCondition(field,mapKey,GREAT_THAN);
        } catch (NoSuchFieldException e) {
            System.err.println("字段名错误,方法名为:"+this.errorMethodName);
            e.printStackTrace();
        }
        return this;
    }
    public MapQuery mapConditionGreatThanEqual(String fieldName,String mapKey){
        try {
            setErrorMethodName("mapConditionGreatThanEqual()");
            Field field =     clazz.getDeclaredField(fieldName);
            this.andMapCondition(field,mapKey,GREAT_THAN_EQUAL);
        } catch (NoSuchFieldException e) {
            System.err.println("字段名错误,方法名为:"+this.errorMethodName);
            e.printStackTrace();
        }
        return this;
    }
    public MapQuery mapConditionLessThan(String fieldName,String mapKey){
        try {
            setErrorMethodName("mapConditionLessThan()");
            Field field =     clazz.getDeclaredField(fieldName);
            this.andMapCondition(field,mapKey,LESS_THAN);
        } catch (NoSuchFieldException e) {
            System.err.println("字段名错误,方法名为:"+this.errorMethodName);
            e.printStackTrace();
        }
        return this;
    }
    public MapQuery mapConditionLessThanEqual(String fieldName,String mapKey){
        try {
            setErrorMethodName("mapConditionLessThanEqual()");
            Field field =     clazz.getDeclaredField(fieldName);
            this.andMapCondition(field,mapKey,LESS_THAN_EQUAL);
        } catch (NoSuchFieldException e) {
            System.err.println("字段名错误,方法名为:"+this.errorMethodName);
            e.printStackTrace();
        }
        return this;
    }
    private void andMapCondition(Field field,String mapKey,String condition){
        if(this.map.get(mapKey) == null) return;
        String hqlCondition = SPACE+field.getName()+ SPACE + condition;
        String value = map.get(mapKey).toString();
        if(this.condition > 0){
            hqlCondition = AND + hqlCondition;
        }
        Class<?> type = field.getType();
        if(!condition.equals(LIKE) && (type == String.class || type == Date.class || type == Timestamp.class)) {
            value = charEqual(value);
        }else if(condition.equals(LIKE) && type == String.class){
            value = charLike(value);
        }
        this.hql = this.hql + hqlCondition + value;
        this.countHql = this.countHql + hqlCondition + value;
        this.condition++;
    }
    public MapQuery between(String fieldName,String le,String ge){
        try {
            Class<?> type = this.clazz.getDeclaredField(fieldName).getType();
            if(type == Date.class || type == Timestamp.class){
                if(this.map.get(le) != null){
                    if(this.condition > 0){
                        this.hql = this.hql+AND;
                        this.countHql = this.countHql+ AND;
                    }
                    this.hql = this.hql + fieldName + LESS_THAN_EQUAL + charEqual(this.map.get(le).toString());
                    this.countHql = this.countHql+ fieldName + LESS_THAN_EQUAL + charEqual(this.map.get(le).toString());
                    this.condition++;
                }
                if(this.map.get(ge) != null){
                    if(this.condition > 0){
                        this.hql = this.hql+AND;
                        this.countHql = this.countHql+ AND;
                    }
                    String value = this.map.get(ge).toString()+SPACE+"23:59:59";
                    this.hql = this.hql + AND + fieldName + LESS_THAN_EQUAL + charEqual(value);
                    this.countHql = this.countHql+ AND + fieldName + LESS_THAN_EQUAL + charEqual(value);
                    this.condition++;
                }
            }else {
                this.mapConditionLessThanEqual(fieldName,le);
                this.mapConditionGreatThanEqual(fieldName,ge);
            }
        } catch (NoSuchFieldException e) {
            System.err.println("字段名错误,方法名为:"+this.errorMethodName);
        }
        return this;
    }
    private static String charEqual(String value){
        return "\'"+value+"\'";
    }
    private static String charLike(String value){
        return "\'%"+value+"%\'";
    }
    private void setErrorMethodName(String methodName){
        this.errorMethodName = methodName;
    }
    @Override
    public MapQuery ok(){
        super.ok();
        return this;
    }
    @Override
    public MapQuery where(){
        super.where();
        return this;
    }
    @Override
    public MapQuery orderBy(String order){
        super.orderBy(order);
        return this;
    }
    @Override
    public MapQuery orderByIdDESC(){
        super.orderByIdDESC();
        return this;
    }
    @Override
    public Object[] getParams() {
        return new Object[0];
    }

    @Override
    public String getHQL() {
        return this.hql;
    }

    @Override
    public String getCountHQL() {
        return this.countHql;
    }

    @Override
    public Integer getPage() {
        return this.page;
    }

    @Override
    public Integer getLimit() {
        return this.limit;
    }
}