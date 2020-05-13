package org.run.sql.query;


import java.util.Map;

public class PageRequest {
    private int pageNo;
    private int pageSize;
    public PageRequest() {
        this.pageNo = 1;
        this.pageSize = 20;
    }
    public PageRequest(Map<String,Object> params) {

        if(params.get("page") instanceof Integer && params.get("limit") instanceof Integer) {
            this.pageNo = (Integer)params.get("page");
            this.pageSize = (Integer)params.get("limit");
        }else {
            String pageNo = (String)params.remove("page");
            String  pageSize = (String)params.remove("limit");

            if(pageNo != null && !"".equals(pageNo)) {
                this.pageNo = Integer.valueOf(pageNo);
            }else {
                this.pageNo = 1;
            }
            if(pageSize != null && !"".equals(pageSize)) {
                this.pageSize = Integer.valueOf(pageSize);
            }else {
                this.pageSize = 20;
            }
        }

    }
    public int getPageNo() {
        return pageNo;
    }
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}