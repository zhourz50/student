package com.mashang.model;

import java.util.List;

public class StudentListView {

    //状态码
    private Integer code = 0;
    //返回的消息
    private String msg;
    //返回的数据
    private List<Student> rows;
    //返回的总条数
    private Integer total;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Student> getRows() {
        return rows;
    }

    public void setRows(List<Student> rows) {
        this.rows = rows;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
