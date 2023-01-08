package com.mashang.servlet.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashang.model.ResultView;
import com.mashang.model.Student;
import com.mashang.util.IdGenUtil;
import com.mashang.util.JdbcUtilDruid;
//import sun.jvm.hotspot.types.CIntegerField;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/student1")
public class StudentAddServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");

        //获取前端传过来的数据
        String ageStr = req.getParameter("age");
        Integer age = null;
        if(ageStr != null && !"".equals(ageStr))
        {
            age = Integer.valueOf(ageStr);
        }//年龄非空判断，并把类型统一成integer

        String classIdStr = req.getParameter("classId");
        String name = req.getParameter("name");
        String phone = req.getParameter("phone");
        String remark = req.getParameter("remark");
        String sex = req.getParameter("sex");
        String studentNo = req.getParameter("studentNo");


        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDateStr = simpleDateFormat.format(new Date());//时间格式化


        try {
            String className = "";
            connection = JdbcUtilDruid.getConnection();
            //根据班级id查询班级名称
            String queryClazz = "select name from t_clazz where class_id=?";
            preparedStatement = connection.prepareStatement(queryClazz);
            preparedStatement.setLong(1, Long.valueOf(classIdStr));
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                className = rs.getString("name");
            }


            String sql = "insert into t_student values(?,?,?,?,?,?,?,?,?,?)";//占位符？
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, IdGenUtil.gen());
            preparedStatement.setString(2, ageStr);
            preparedStatement.setString(3, classIdStr);
            preparedStatement.setString(4, className);
            preparedStatement.setString(5, nowDateStr);
            preparedStatement.setString(6, name);
            preparedStatement.setString(7, phone);
            preparedStatement.setString(8, remark);
            preparedStatement.setString(9, sex);
            preparedStatement.setString(10, studentNo);
            //执行添加班级的方法
            int result = preparedStatement.executeUpdate();

            ResultView view = new ResultView();
            view.setCode(0);
            view.setMsg("添加成功");

            resp.setContentType("text/html;charset=utf-8");
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(view);
            resp.getWriter().write(jsonStr);//转换成json格式


        } catch (SQLException throwables) {

            throwables.printStackTrace();
        } finally {
            JdbcUtilDruid.close(connection, preparedStatement, rs);
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");

        String studentIdStr = req.getParameter("studentId");
        Long studentId= null;
        if(studentIdStr != null && !"".equals(studentIdStr))
        {
            studentId = Long.valueOf(studentIdStr);
        }//判断非空，格式统一

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = JdbcUtilDruid.getConnection();
            String delSql = "delete from t_student where student_id = ?" ;//删除操作
            preparedStatement = connection.prepareStatement(delSql);
            preparedStatement.setLong(1,studentId);
            int result = preparedStatement.executeUpdate();


            ResultView view = new ResultView();
            view.setCode(0);
            view.setMsg("删除成功");

            resp.setContentType("text/html;charset=utf-8");
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(view);
            resp.getWriter().write(jsonStr);//转换成json格式

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtilDruid.close(connection, preparedStatement, null);
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String studentIdStr = req.getParameter("studentId");
        if (studentIdStr == null || "".equals(studentIdStr)) {
            resp.getWriter().write("studentId is not null");
            return;//判空
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            connection = JdbcUtilDruid.getConnection();
            String sql = "select * from t_student where student_id=" + Long.valueOf(studentIdStr);//查找
            preparedStatement = connection.prepareStatement(sql);
            rs = preparedStatement.executeQuery();
            Student student = new Student();//封装成Student对象

            while (rs.next()) {
                student.setStudentId(rs.getLong("student_id"));
                student.setStudentNo(rs.getString("student_no"));
                student.setSex(rs.getString("sex"));
                student.setRemark(rs.getString("remark"));
                student.setPhone(rs.getString("phone"));
                student.setClassName(rs.getString("class_name"));
                student.setAge(rs.getInt("age"));
                student.setName(rs.getString("name"));
                student.setClassId(rs.getInt("class_id"));
                student.setCreateTime(rs.getString("create_time"));
            }

            ResultView resultView = new ResultView();
            resultView.setData(student);//将封装好的Student对象，添加到集合中

            //将这个公共的代码放到过滤器执行
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().write(new ObjectMapper().writeValueAsString(resultView));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtilDruid.close(connection, preparedStatement, rs);
        }

    }


    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String name = req.getParameter("name");
//        System.out.println(name);     //req.getParameter方法无法实现

        BufferedReader reader = req.getReader();
        String paramterStr = reader.readLine();
        String decodeParamterStr = URLDecoder.decode(paramterStr, "utf-8");//解码成utf-8编码
        System.out.println(decodeParamterStr);

        String[] paraArr = decodeParamterStr.split("&");//分割
        //将前端传过来的数据封装到Student对象中
        Student updateStudent = new Student();
        for (String para : paraArr) {
            String[] param = para.split("=");
            switch (param[0]) {
                case "age":
                    updateStudent.setAge(Integer.valueOf(param[1]));
                    break;
                case "classId":
                    updateStudent.setClassId(Integer.valueOf(param[1]));
                    break;
                case "studentId":
                    updateStudent.setStudentId(Long.valueOf(param[1]));
                    break;
                case "createTime":
                    updateStudent.setCreateTime((param[1]));
                    break;
                case "studentNo":
                    updateStudent.setStudentNo((param[1]));
                    break;
                case "name":
                    updateStudent.setName((param[1]));
                    break;
                case "className":
                    updateStudent.setClassName(param[1]);
                    break;
                case "phone":
                    updateStudent.setPhone((param[1]));
                    break;
                case "remark":
                    updateStudent.setRemark((param[1]));
                    break;
                case "sex":
                    updateStudent.setSex((param[1]));
                    break;
                default:
                    break;
            }
        }

        //修改数据
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = JdbcUtilDruid.getConnection();
            // 根据class_id查询class_name


            String uptSql = "update  t_student set age=?,class_id=?,class_name=? where student_id= ?";
            preparedStatement = conn.prepareStatement(uptSql);
            preparedStatement.setInt(1, updateStudent.getAge());
            preparedStatement.setInt(2, updateStudent.getClassId());
            preparedStatement.setString(3, updateStudent.getClassName());
            preparedStatement.setLong(4, updateStudent.getStudentId());
            int num = preparedStatement.executeUpdate();

            //返回数据给前端
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().write(new ObjectMapper().writeValueAsString(new ResultView()));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtilDruid.close(conn, preparedStatement, null);
        }


    }
}
