package org.run.sql.query;

public abstract class BaseQuery {
    protected static final String SPACE = " ";
    protected static final String SELECT = SPACE + "SELECT" + SPACE;
    protected static final String AND = SPACE + "AND" + SPACE;
    protected static final String WHERE = SPACE + "WHERE" + SPACE;
    protected static final String NULL = "";
    protected static final String EQUAL = SPACE + "=" + SPACE;
    protected static final String EQUAL_PLACEHOLDER =  EQUAL + "?";
    protected static final String NO_EQUAL = SPACE+"!="+SPACE;
    protected static final String NO_EQUAL_PLACEHOLDER = NO_EQUAL+"?";
    protected static final String LIKE = SPACE + "LIKE" + SPACE;
    protected static final String LIKE_PLACEHOLDER  = LIKE + "?";
    protected static final String DESC = SPACE + "DESC" + SPACE;
    protected static final String ORDER_BY = SPACE + "ORDER BY" + SPACE;
    protected static final String ASC = SPACE + "ASC" + SPACE;
    protected static final String GREAT_THAN = SPACE+">"+SPACE;
    protected static final String GREAT_THAN_EQUAL = SPACE + ">=" + SPACE;
    protected static final String LESS_THAN = SPACE + "<" +SPACE;
    protected static final String LESS_THAN_EQUAL = SPACE + "<=" + SPACE;
    protected static final String GREAT_THAN_EQUAL_PLACEHOLDER = SPACE + ">=?";
    protected static final String GREAT_THAN_PLACEHOLDER = SPACE+">?";
    protected static final String LESS_THAN_PLACEHOLDER = SPACE + "<?";
    protected static final String LESS_THAN_EQUAL_PLACEHOLDER = SPACE + "<=?";
    protected static final String FROM = SPACE+"FROM"+SPACE;
    protected static final String COUNT = SPACE + "COUNT(*)" + SPACE;
    protected static final String ORDER_BY_ID_DESC = ORDER_BY+"id"+DESC;

    /**
     * 获得参数
     * @return
     */
    public abstract Object[] getParams();

    /**
     * 获得hql语句
     * @return
     */
    public abstract String getHQL();

    /**
     * 获得HQL的countHQL语句
     * @return
     */
    public abstract String getCountHQL();

    /**
     * 第几页
     * @return
     */
    public abstract Integer getPage();

    /**
     * 搜索多少条数据
     * @return
     */
    public abstract Integer getLimit();
}
