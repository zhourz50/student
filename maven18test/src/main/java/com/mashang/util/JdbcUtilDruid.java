package com.mashang.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Properties;

public class JdbcUtilDruid {

    private static DataSource dataSource;

    static {
        try {
            ClassLoader classLoader = JdbcUtilDruid.class.getClassLoader();
            URL url = classLoader.getResource("druid.properties");
            Properties pro = new Properties();
            pro.load(new FileReader(new File(url.getFile())));

            dataSource = DruidDataSourceFactory.createDataSource(pro);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static void close(Connection connection, Statement statement, ResultSet rs) {
        try {
            if (connection != null) {
                connection.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
