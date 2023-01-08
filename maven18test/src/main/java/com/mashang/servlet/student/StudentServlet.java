package com.mashang.servlet.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashang.model.StudentListView;
import com.mashang.model.Student;
import com.mashang.util.JdbcUtilDruid;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/student1/list")
public class StudentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html;charset=utf-8");
        String pageNumStr = req.getParameter("pageNum");
        // 第几页
        if (pageNumStr == null || "".equals(pageNumStr)) {
            pageNumStr = "1";
        }
        //一页多少条数据
        String pageSizeStr = req.getParameter("pageSize");
        if (pageSizeStr == null || "".equals(pageSizeStr)) {
            pageSizeStr = "10";
        }

        String name = req.getParameter("name");
        String studentNo = req.getParameter("studentNo");

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            conn = JdbcUtilDruid.getConnection();

            String sql = "select * from t_student where 1=1 ";

            if (name != null && !"".equals(name)) {
                sql = sql + " and name like '%" + name + "%'";
            }
            if (studentNo != null && !"".equals(studentNo)) {
                sql = sql + " and student_no like '%" + studentNo + "%'";
            }

            //拼接分页
            Integer pageNum = Integer.parseInt(pageNumStr);
            Integer pageSize = Integer.parseInt(pageSizeStr);
            Integer index = (pageNum - 1) * pageSize;

            sql = sql + " limit " + index + "," + pageSize;

            System.out.println(sql);

            preparedStatement = conn.prepareStatement(sql);

            rs = preparedStatement.executeQuery();
            StudentListView resultListView = new StudentListView();
            resultListView.setCode(0);
            resultListView.setMsg("查询成功");
            // 在使用一个statment 去查询库里所有的记录  count(*)
            resultListView.setTotal(10);
            List<Student> studentList = new ArrayList<>();


            while (rs.next()) {
                //封装成Student对象
                Student student = new Student();
                student.setStudentId(rs.getLong("student_id"));
                student.setStudentNo(rs.getString("student_no"));
                student.setName(rs.getString("name"));
                student.setAge(rs.getInt("age"));
                student.setClassId(rs.getInt("class_id"));
                student.setCreateTime(rs.getString("create_time"));
                student.setPhone(rs.getString("phone"));
                student.setRemark(rs.getString("remark"));
                student.setSex(rs.getString("sex"));
                student.setClassName(rs.getString("class_name"));

                //将封装好的Student对象，添加到studentList集合中
                studentList.add(student);
            }
            //将封装好的学生集合，存入到resultListView(要返回给前端的对象)中
            resultListView.setRows(studentList);

            //将要返回的对象转成json格式的字符串
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(resultListView);

            //将json格式的字符串返回给前端
            resp.getWriter().write(jsonStr);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JdbcUtilDruid.close(conn, preparedStatement, rs);
        }

    }


}
