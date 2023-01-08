package com.mashang.servlet.clazz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashang.model.ResultView;
import com.mashang.model.clazz.ClazzViewVo;
import com.mashang.util.JdbcUtilDruid;
import com.mysql.cj.jdbc.JdbcConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/class/name")
public class ClazzNameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;


        conn = JdbcUtilDruid.getConnection();
        String sql = "select class_id ,name from t_clazz";
        try {
            preparedStatement = conn.prepareStatement(sql);
            rs = preparedStatement.executeQuery();

            List<ClazzViewVo> clazzViewVoList = new ArrayList<>();
            while (rs.next()) {
                //封装ClazzViewVo对象
                ClazzViewVo clazzViewVo = new ClazzViewVo();
                clazzViewVo.setName(rs.getString("name"));
                clazzViewVo.setClassId(rs.getInt("class_id"));
                clazzViewVoList.add(clazzViewVo);
            }

            ResultView<List<ClazzViewVo>> resultView = new ResultView();
            resultView.setCode(0);
            resultView.setMsg("查询成功");
            resultView.setData(clazzViewVoList);

            //将要返回的对象，整理成json格式的字符串
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(resultView);

            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().write(json);

        } catch (Exception throwables) {
            throwables.printStackTrace();
        } finally {
            JdbcUtilDruid.close(conn, preparedStatement, rs);
        }


    }
}
