package org.run.sql.query;

import org.run.base.asserts.StringUtil;
import org.run.date.util.DateUtil;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static org.run.date.constant.DateConstant.DATE_TIME_PATTERN;

public class Query extends BaseQuery {
    protected String hql;
    protected String countHql;
    protected Object[] params;
    protected Integer page;
    protected Integer limit;
    protected Class<?> clazz;
    protected Field[] fields;
    protected Map<String,Object> map;
    protected int condition;
    protected String errorMethodName = "";
    protected Query(){}
    private Query(Map<String,Object> map,Class<?> clazz,PageRequest pageRequest,String hql,String countHql,Object[] params){
        this.hql = hql;
        this.countHql = countHql;
        this.page = pageRequest.getPageNo();
        this.limit = pageRequest.getPageSize();
        this.clazz = clazz;
        this.params = params;
        this.map = map;
        this.fields = clazz.getDeclaredFields();
        if(hql.indexOf("where") != -1
                ||hql.indexOf("WHERE") != -1){

            condition ++;
        }
    }
    /**
     * 获得查询需要的数据
     * @param map
     * @param clazz
     * @return
     */
    public static Query getQuery(Map<String,Object> map,Class<?> clazz){
        PageRequest pageRequest = new PageRequest(map); //分页条件
        Map<String,Object> initMap =   preconditionMap(map,clazz);//处理map集合 去除value为""的key-value
        String hql = preconditionHql(initMap,clazz);
        String countHql = preconditionCount(initMap,clazz);
        Object[] params = preconditionParams(initMap,clazz);
        return new Query(map,clazz,pageRequest,hql,countHql,params);
    }

    public static Query getQuery(Class<?> clazz) {
        return getQuery(new HashMap<String, Object>(), clazz);
    }
    public static Query getQuery(Object object,String...notRequired){
        try {
            if(object == null) return null;
            Map<String,Object> map = new HashMap<>();
            Class<?> clazz = object.getClass();
            Field[] fields =  clazz.getDeclaredFields();
            for(Field field : fields){ //将对象转化为Map集合
                String key = field.getName();
                Class<?> type = field.getType();
                if(key.equals("serialVersionUID") || "id".equals(key)) continue;
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(key,clazz);
                Object value = propertyDescriptor.getReadMethod().invoke(object);
                if(value == null || NULL.equals(value)) continue;
                if(type == Date.class || type == Timestamp.class){
                    value = DateUtil.format((Date) value,DATE_TIME_PATTERN);
                }else{
                    value = String.valueOf(value);
                }
                map.put(key,value);
            }
            if(notRequired != null && notRequired.length > 0){
                Iterator<String> itKeys = map.keySet().iterator();
                while (itKeys.hasNext()){
                    String key = itKeys.next();
                    for(String remove:notRequired){
                        if(key.equals(remove)){
                            itKeys.remove();
                        }
                    }
                }
            }
            return getQuery(map,clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 根据字段类型获得参数
     * @param initMap
     * @param clazz
     * @return
     */
    private static Object[] preconditionParams(Map<String, Object> initMap, Class<?> clazz) {
        if(initMap != null && !initMap.isEmpty()){
            Object[] params = new Object[initMap.size()];
            Object[] keys =  initMap.keySet().toArray();
            for(int i = 0 ; i < keys.length;i++){
                Object value = conver2Type(clazz,initMap,keys[i].toString());
                params[i] = value;
            }
            return params;
        }
        return new Object[0];
    }

    /**
     *
     * @param fieldName
     * @param params
     * @return
     */
    public Query andEqual(String fieldName,Object params){
        //   setErrorMethodName("andEqual()");
        return andCondition(fieldName,params,EQUAL_PLACEHOLDER);
    }

    public Query andNotEqual(String fieldName,Object params) {
        //setErrorMethodName("andNotEqual()");
        return andCondition(fieldName,params,NO_EQUAL_PLACEHOLDER);
    }

    public Query like(String fieldName,Object params){
        //  setErrorMethodName("like()");
        return andCondition(fieldName,params == null? null : "%"+params+"%",LIKE_PLACEHOLDER);
    }

    public Query andGreatEqual(String fieldName,Object params){
        //    setErrorMethodName("andGreatEqual()");
        return andCondition(fieldName,params,GREAT_THAN_EQUAL_PLACEHOLDER);
    }

    public Query andGreatThan(String fieldName,Object params){
        //   setErrorMethodName("andGreatThan()");
        return andCondition(fieldName,params,GREAT_THAN_PLACEHOLDER);
    }

    public Query andLessThan(String fieldName,Object params){
        //    setErrorMethodName("andLessThan()");
        return andCondition(fieldName,params,LESS_THAN_PLACEHOLDER);
    }

    public Query andLessThanEqual(String fieldName,Object params){
        //   setErrorMethodName("andLessThanEqual()");
        return andCondition(fieldName,params,LESS_THAN_EQUAL_PLACEHOLDER);
    }

    public Query andEqualFromMap(String fieldName,String mapKey,Class<?> paramsType){
        //  setErrorMethodName("andEqualFromMap()");
        Object params =  conver2Type4Map(paramsType,mapKey);
        return andEqual(fieldName,params);
    }

    public Query andLikeFromMap(String fieldName,String mapKey){
        //   setErrorMethodName("andLikeFromMap()");
        Object params =  conver2Type4Map(String.class,mapKey);
        return like(fieldName,params);
    }

    public Query andLessThanFromMap(String fieldName,String mapKey,Class<?> paramsType){
        // setErrorMethodName("andLessThanFromMap()");
        Object params =  conver2Type4Map(paramsType,mapKey);
        return andLessThan(fieldName,params);
    }

    public Query andLessThanEqualFromMap(String fieldName,String mapKey,Class<?> paramsType){
        //    setErrorMethodName("andLessThanEqualFromMap()");
        Object params =  conver2Type4Map(paramsType,mapKey);
        return andLessThanEqual(fieldName,params);
    }

    public Query andGreatThanFromMap(String fieldName,String mapKey,Class<?> paramsType){
        //   setErrorMethodName("andGreatThanFromMap()");
        //   Field field = clazz.getDeclaredField(fieldName);
        Object value = this.map.get(mapKey);
        if(value == null ) return this;
        if(paramsType == Date.class || paramsType == Timestamp.class){
            return andGreatEqual(fieldName,DateUtil.parse(value.toString()+" 23:59:59"));
        }
        Object params =  conver2Type4Map(paramsType,mapKey);
        return andGreatThan(fieldName,params);

    }

    public Query andGreatThanEqualFromMap(String fieldName,String mapKey,Class<?> paramsType){
        //      setErrorMethodName("andGreatThanEqualFromMap()");
        // Field field = clazz.getDeclaredField(fieldName);
        Object value = this.map.get(mapKey);
        if(value == null ) return this;
        if(paramsType == Date.class || paramsType == Timestamp.class){
            return andGreatEqual(fieldName,DateUtil.parse(value.toString()+" 23:59:59"));
        }
        Object params =  conver2Type4Map(paramsType,mapKey);
        return andGreatEqual(fieldName,params);
    }


    private Object conver2Type4Map(Class<?> type, String mapKey) {
        // try {
        Object value = map.get(mapKey);
        if(value != null){
            if(type == String.class){
                return value.toString();
            } else if(type == Integer.TYPE ||  Integer.class == type){
                return Integer.valueOf(value.toString());
            }else if(type == Long.TYPE || type == Long.class){
                return Long.valueOf(value.toString());
            }else if(type == Float.TYPE || type == Float.class){
                return Float.valueOf(value.toString());
            }else if(type == Double.TYPE || type == Double.class){
                return Double.valueOf(value.toString());
            }else if(type == Short.TYPE || type == Short.class){
                return Short.valueOf(value.toString());
            }else if(Character.TYPE == type || Character.class == type){
                return value.toString().charAt(0);
            }else if(Byte.class == type || Byte.TYPE == type){
                return value.toString().getBytes()[0];
            }else if(Boolean.class == type || Boolean.TYPE == type){
                if("1".equals(value.toString())){
                    return true;
                }else if("0".equals(value.toString())){
                    return false;
                }
                return Boolean.valueOf(value.toString());
            }else if(BigDecimal.class == type){
                return new BigDecimal(value.toString());
            }else if(type == Date.class || type == Timestamp.class){
                return DateUtil.parse(value.toString(),DATE_TIME_PATTERN);
            }else {
                return null;
            }
        }else {
            return null;
        }
       /* } catch (NoSuchFieldException e) {
            System.err.println("字段名错误,方法名为:"+this.errorMethodName);
            return null;
        }*/
    }


    private Query andCondition(String fieldName,Object params,String placeholder){
        //try {
        if(params == null || "".equals(params)){
            return this;
        }
        int index = this.params.length;
        if(this.condition > 0){
            this.hql = this.hql + AND;
            this.countHql = this.countHql+AND;
        }
        this.hql = this.hql + fieldName + placeholder+index+SPACE;
        this.countHql = this.countHql+fieldName+placeholder+index+SPACE;
        //  List<Object> lll = new LinkedList<Object>(Arrays.asList(this.params));
        List<Object> list =  new ArrayList<>(Arrays.asList(this.params));
        list.add(params);
        this.params = list.toArray();
        this.condition ++;
      /*  } catch (NoSuchFieldException e) {
            System.err.println("字段名错误,方法名为"+this.errorMethodName);
        }*/
        return this;
    }

    /**
     * 将某一个like的条件替换成equal
     * @param fieldName
     * @return
     */
    public Query likeReplaceEqual(String fieldName){
        //    setErrorMethodName("likeReplaceEqual()");
        try {
            Field field =  clazz.getDeclaredField(fieldName);
            String likeField =  preField4Like(field.getName());
            String equalField = preField(field.getName());
            Integer index = getIndex4Like(hql,field.getName());
            if(index == null){
                return this;
            }else {
                if(hql.indexOf(likeField) != -1 && countHql.indexOf(likeField) != -1){
                    this.hql = this.hql.replace(likeField,equalField);
                    this.countHql = this.countHql.replace(likeField,equalField);
                    this.params[index] = this.params[index].toString().replaceAll("%",NULL);
                }
                return this;
            }
        } catch (NoSuchFieldException e) {
            System.err.println("输入的字段名错误,方法名为"+this.errorMethodName);
        }
        return this;
    }

    /**
     * equal 搜索条件替换成like
     * @param fieldName
     * @return
     */
    public Query equalReplaceLike(String fieldName){
        //   setErrorMethodName("equalReplaceLike()");
        try {
            Field field =  clazz.getDeclaredField(fieldName);
            String likeField =  preField4Like(field.getName());
            String equalField = preField(field.getName());
            Integer index = getIndex4Like(hql,field.getName());
            if(index == null){
                return this;
            }else {
                if(hql.indexOf(equalField) != -1 && countHql.indexOf(equalField) != -1){
                    this.hql = this.hql.replace(equalField,likeField);
                    this.countHql = this.countHql.replace(equalField,likeField);
                    this.params[index] = "%"+this.params[index].toString()+"%";
                }
                return this;
            }
        } catch (NoSuchFieldException e) {
            System.err.println("输入的字段名错误,方法名为"+this.errorMethodName);
        }
        return this;
    }


    /**
     * 直接将要的SQL语句拼接在后面
     * @param condition
     * @return
     */
    public Query and(String condition){
        condition = SPACE+condition+SPACE;
        if(this.condition > 0){
            condition = AND + condition;
        }
        return andCondition(condition);
    }
    /**
     * 直接将要的SQL语句拼接在后面
     * @param condition
     * @return
     */
    public Query like(String condition){
        return this.and(condition);
    }
    private Query andCondition(String condition){
        this.hql = this.hql+condition;
        this.countHql = this.countHql+condition;
        this.condition++;
        return this;
    }
    public Query where(){
        int index = this.hql.indexOf(WHERE) == -1 ?
                this.hql.indexOf("WHERE") == -1?this.hql.indexOf("where"): this.hql.indexOf("WHERE")
                :this.hql.indexOf(WHERE);
        if(index == -1){
            this.hql = this.hql + WHERE;
            this.countHql = this.countHql + WHERE;
        }
        return this;
    }
    public Query orderBy(String order){
        String orderBy = ORDER_BY + order +SPACE;
        this.hql = this.hql+orderBy;
        return this;
    }

    public Query orderByFromMap(String mapKey) {
        Object sidx = this.map.get(mapKey);
        if(!StringUtil.isEmpty(sidx)) {
            this.hql = this.hql+ORDER_BY+sidx+SPACE;
        }
        return this;
    }


    public Query orderByIdDESC(){
        this.hql = this.hql + ORDER_BY_ID_DESC;
        return this;
    }

    /**
     * 将所有的String字段都用like查询
     * @return
     */
    public Query allLike(){
        for(Field field : fields){
            Class<?> type = field.getType();
            if(type == String.class){
                String fieldName = field.getName();
                String hqlField = preField(fieldName);//=的条件
                String hqlFieldLike = preField4Like(fieldName);//like的条件
                Integer index = getIndex4Equal(hql,fieldName);
                if(index == null) continue;
                if(hql.indexOf(hqlField) != -1 && countHql.indexOf(hqlField) != -1){
                    this.hql = this.hql.replace(hqlField,hqlFieldLike);
                    this.countHql = this.countHql.replace(hqlField,hqlFieldLike);
                    this.params[index] = "%"+params[index].toString()+"%";
                }
            }
        }
        return  this;
    }

    /**
     * 所有的条件都变成equal查询条件
     * @return
     */
    public Query allEqual(){
        for(Field field : fields){
            Class<?> type = field.getType();
            if(type == String.class){
                String fieldName = field.getName();
                String hqlFieldLike = preField4Like(fieldName);
                String hqlFieldEqual = preField(fieldName);
                Integer index = getIndex4Like(hql,fieldName);
                if(index == null) continue;
                if(this.hql.indexOf(hqlFieldLike) != -1 && this.countHql.indexOf(hqlFieldLike) !=-1){
                    this.hql = this.hql.replace(hqlFieldLike,hqlFieldEqual);
                    this.countHql = this.countHql.replace(hqlFieldLike,hqlFieldEqual);
                    this.params[index] = this.params[index].toString().replaceAll("%",NULL);
                }
            }
        }
        return this;
    }
    private Integer getIndex4Equal(String hql, String fieldName) {
        return getIndex(hql,preField(fieldName));
    }
    private Integer getIndex4Like(String hql, String fieldName){
        return getIndex(hql,preField4Like(fieldName));
    }
    private static Integer getIndex(String hql,String fieldName){
        if(hql.indexOf(fieldName) == -1){
            return null;
        }else {
            return Integer.valueOf(hql
                    .substring(hql.indexOf(fieldName))
                    .replace(fieldName,NULL)
                    .substring(0,1));
        }
    }

    private static String preField4Like(String fieldName){
        return fieldName + LIKE_PLACEHOLDER;
    }
    public Query DESC(){
        if(this.hql.indexOf(DESC) != -1){
            return this;
        }else {
            if(this.hql.indexOf(ASC)==-1){
                this.hql = this.hql + DESC;
                //  this.countHql = this.countHql+DESC;
            }else {
                this.hql = this.hql.replace(ASC,DESC);
                //   this.countHql = this.countHql.replace(ASC,DESC);
            }
            return this;
        }
    }
    public Query sidxFromMap(String mapKey) {
        if(this.hql.indexOf(DESC) != -1 || this.hql.indexOf("desc") != -1 || this.hql.indexOf("DESC") != -1){
            return this;
        }else {
            if(this.hql.indexOf(ASC)!=-1 || this.hql.indexOf("asc") != -1 || this.hql.indexOf("ASC") != -1){
                return this;
            }else {
                Object sidx = this.map.get(mapKey);
                if(!StringUtil.isEmpty(sidx)) {
                    this.hql = this.hql + SPACE + sidx + SPACE;
                }else {
                    this.hql = this.hql+ DESC ;
                }

            }
            return this;
        }
    }

    public Query ASC(){
        if(hql.indexOf(ASC) != -1){
            return this;
        }else {
            if(hql.indexOf(DESC)==-1){
                this.hql = this.hql + ASC;
            }else {
                this.hql = this.hql.replace(DESC,ASC);
            }
            return this;
        }
    }

    /**
     * 检查有没有遗漏或者多出where条件
     * 子查询不要调用此方法
     * @return
     */
    public Query ok(){
        int index = this.hql.indexOf(WHERE) == -1 ?
                this.hql.indexOf("WHERE") == -1?this.hql.indexOf("where")
                        : this.hql.indexOf("WHERE") :this.hql.indexOf(WHERE);
        int orderIndex = this.hql.indexOf("order") == -1 ? this.hql.indexOf("ORDER") : this.hql.indexOf("order");
        if(orderIndex != -1){
            String hql = this.hql.substring(0,this.hql.indexOf(ORDER_BY));
            if(index != -1){
                String[] hqls = hql.split(WHERE);
                if(hqls.length == 2){
                    if(hqls[1].trim().equals("")){
                        this.hql =  this.hql.replace(WHERE,NULL);
                        this.countHql = this.countHql.replace(WHERE,NULL);
                    }
                }else {
                    this.hql =  this.hql.replace(WHERE,NULL);
                    this.countHql = this.countHql.replace(WHERE,NULL);
                }
            }
        }else{
            if(index != -1){
                String[] hqls = hql.split(WHERE);
                if(hqls.length == 2){
                    if(hqls[1].trim().equals("")){
                        this.hql =  this.hql.replace(WHERE,NULL);
                        this.countHql = this.countHql.replace(WHERE,NULL);
                    }
                }else{
                    this.hql =  this.hql.replace(WHERE,NULL);
                    this.countHql = this.countHql.replace(WHERE,NULL);
                }
            }
        }
        return this;
    }
    private static Object conver2Type(Class<?> clazz,Map<String,Object> initMap,String key){

        try {
            Field field =  clazz.getDeclaredField(key);
            Class<?> type = field.getType();
            Object value = initMap.get(field.getName());
            if(value != null){
                if(type == String.class){
                    return value.toString();
                } else if(type == Integer.TYPE ||  Integer.class == type){
                    return Integer.valueOf(value.toString());
                }else if(type == Long.TYPE || type == Long.class){
                    return Long.valueOf(value.toString());
                }else if(type == Float.TYPE || type == Float.class){
                    return Float.valueOf(value.toString());
                }else if(type == Double.TYPE || type == Double.class){
                    return Double.valueOf(value.toString());
                }else if(type == Short.TYPE || type == Short.class){
                    return Short.valueOf(value.toString());
                }else if(Character.TYPE == type || Character.class == type){
                    return value.toString().charAt(0);
                }else if(Byte.class == type || Byte.TYPE == type){
                    return value.toString().getBytes()[0];
                }else if(Boolean.class == type || Boolean.TYPE == type){
                    if("1".equals(value.toString())){
                        return true;
                    }else if("0".equals(value.toString())){
                        return false;
                    }
                    return Boolean.valueOf(value.toString());
                }else if(BigDecimal.class == type){
                    return new BigDecimal(value.toString());
                }else if(type == Date.class || type == Timestamp.class){
                    return DateUtil.parse(value.toString());
                }else {
                    return null;
                }
            }else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 拼接CountHql语句
     * @param initMap
     * @param clazz
     * @return preconditionCount
     */
    private static String preconditionHql(Map<String, Object> initMap, Class<?> clazz) {
        String hql  = FROM+clazz.getSimpleName()+SPACE;
        return baseHql(hql,initMap,clazz);
    }

    /**
     * 拼接Hql语句
     * @param initMap
     * @param clazz
     * @return
     */
    private static String preconditionCount(Map<String, Object> initMap, Class<?> clazz) {
        String hql = SELECT+COUNT+FROM+clazz.getSimpleName()+SPACE;
        return baseHql(hql,initMap,clazz);
    }
    private static String baseHql(String hql,Map<String,Object> initMap,Class<?> clazz){
        StringBuilder builder =  new StringBuilder(hql);
        try {
            if(initMap != null && !initMap.isEmpty()){
                if(hql.indexOf("and") != -1 ||
                        hql.indexOf("AND") != -1 ||
                        hql.indexOf("in") != -1 || hql.indexOf("IN") != -1
                        || hql.indexOf("like") != -1 || hql.indexOf("LIKE") != -1||hql.indexOf("where") != -1
                        || hql.indexOf("WHERE") != -1){
                    builder.append(AND);
                }
                if(hql.indexOf("where") == -1 && hql.indexOf("WHERE") == -1)
                    builder.append(WHERE);

                Object[] keys = initMap.keySet().toArray();
                for(int i = 0; i < keys.length ; i++){
                    Class<?> type = clazz.getDeclaredField(keys[i].toString()).getType();
                    if(type == Date.class){
                        builder.append(keys[i].toString()+GREAT_THAN_EQUAL_PLACEHOLDER)
                                .append(i)
                                .append(AND);
                    }else {
                        builder.append(preField(keys[i].toString()))
                                .append(i)
                                .append(AND);
                    }
                }
                return   builder.substring(0,builder.lastIndexOf(AND));
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return   builder.toString();
        }
        return builder.toString();
    }
    private static String preField(String fieldName){
        return fieldName+EQUAL_PLACEHOLDER;
    }
    private static Map<String, Object> preconditionMap(Map<String, Object> map,Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Map<String,Object> params = new HashMap<>();
        if(map == null || map.isEmpty()) return params;
        Iterator<String> keys =  map.keySet().iterator();
        while(keys.hasNext()){
            String key = keys.next();
            if(StringUtil.isEmpty(map.get(key))) {
                keys.remove();
            }else {
                map.put(key,map.get(key));
            }
        }
        for(Field field : fields){
            String key = field.getName();
            Object value =     map.get(key);
            if(value != null && !NULL.equals(value))
                params.put(key,String.valueOf(value));
        }
        return params;
    }

    /**
     * 根据类名的简单hql语句为基础
     * ep: from User
     *      select count(1) from User
     * @return
     */
    public MapQuery map(){
        this.hql = FROM + clazz.getSimpleName()+SPACE;
        this.countHql = SELECT+COUNT+FROM+clazz.getSimpleName()+SPACE;
        return new MapQuery(this);
    }

    /**
     * 把自定义的Hql为基本的HQL语句为基础
     * @return
     */
    public MapQuery hqlMap(){
        return new MapQuery(this);
    }
    public static SqlQuery getSqlQuery(String sql, Map<String,Object> map, Class<?> clazz){
        PageRequest pageRequest = new PageRequest(map);
        int index = sql.indexOf("from") == -1 ? sql.indexOf("FROM") : sql.indexOf("from");
        String countSql = SELECT + COUNT  +  sql.substring(index);

        preconditionMap(map,clazz);
        return  new SqlQuery(pageRequest,sql,countSql,map);
    }

    public static SqlQuery getSqlQuery(String sql, Class<?> clazz) {
        return getSqlQuery(sql,new HashMap<>(),clazz);
    }

    public static Query getQuery(String hql,Map<String,Object> map,Class<?> clazz){
        PageRequest pageRequest = new PageRequest(map);
        String countHql = SELECT + COUNT + hql;
        Integer selectIndex = hql.indexOf("select") == -1 ? hql.indexOf("SELECT") : hql.indexOf("select");
        if(selectIndex != -1) {
            Integer fromIndex = hql.indexOf("from") == -1 ? hql.indexOf("FROM") : hql.indexOf("from");
            Integer orderIndex = hql.indexOf("order") == -1? hql.indexOf("ORDER") : hql.indexOf("order");
            String tmpHql = hql;
            if(orderIndex != -1) {
                tmpHql =tmpHql.substring(0,orderIndex);
            }
            tmpHql = tmpHql.substring(fromIndex);
            countHql = SELECT + COUNT + tmpHql;
        }
        preconditionMap(map,clazz);
        Map<String,Object> initMap =  new HashMap<>();
        Object[] params =  preconditionParams(initMap,clazz);
        return new Query(map,clazz,pageRequest,baseHql(hql,initMap,clazz),baseHql(countHql,initMap,clazz),params);
    }

    public static Query getQuery(String hql,Class<?> clazz) {
        return getQuery(hql, new HashMap<>(), clazz);
    }

    /*private void setErrorMethodName(String methodName){
        this.errorMethodName = methodName;
    }
*/
    public String getSQL(){
        return initSQL();
    }

    public String getCountSQL(){
        return initCountSQL();
    }
    protected String initCountSQL(){
        return null;
    }
    /**
     * 定义一个钩子
     * @return
     */
    protected String initSQL(){
        return null;
    }
    @Override
    public Object[] getParams() {
        return this.params;
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
