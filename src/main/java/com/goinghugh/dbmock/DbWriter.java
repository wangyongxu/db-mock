package com.goinghugh.dbmock;

import com.goinghugh.dbmock.model.TableStructure;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * @author yongxu wang
 * @date 2019-05-06 07:10
 **/
public class DbWriter {

    private static final Logger logger = LoggerFactory.getLogger(DbWriter.class);

    public int writeOne(Connection conn, TableStructure structure) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement(structure.getCreateSql());
        structure.getColumns().stream()
                .filter(column -> !column.getAutoincrement())
                .forEach(column -> {
                    if (column.getJavaType() == String.class) {
                        try {
                            preparedStatement.setString(structure.getColumnIndex(column), (String) column.getGenerator().next());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else if (column.getJavaType() == Integer.class) {
                        try {
                            preparedStatement.setInt(structure.getColumnIndex(column), (Integer) column.getGenerator().next());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
        int count = preparedStatement.executeUpdate();
        return count;
    }


    public int writeMany(SqlSession sqlSession, TableStructure structure) throws SQLException {


        return 0;
    }


    public static void main(String[] args) throws SQLException {
        Connection conn = DataBaseReader.getConn();
        DataBaseReader dataBaseReader = new DataBaseReader();
        TableStructure structure = dataBaseReader.getTableStructure(conn, "blog", "student");
        logger.info("表结构为: {}", structure);
        logger.info("生成的创建sql为: {}", structure.getCreateSql());
        DbWriter dbWriter = new DbWriter();
        int i = dbWriter.writeOne(conn, structure);
        logger.info("插入数量为: {}", i);

    }
}
