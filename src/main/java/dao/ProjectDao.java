package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
/**
 * @Author：Eric
 * @Date：2020/4/5 0:44
 */
public class ProjectDao {
    public void save(Project project) {
        // 通过 save 方法就能把一个 project 对象保存到 数据库 中
        // 1. 获取到数据库连接
        Connection connection = DBUtil.getConnection();
        // 2. 构造 PrepareStatement 对象拼装 SQL 语句
        PreparedStatement statement = null;
        String sql = "insert into project_table values(?, ?, ?, ?, ?, ?, ?)";
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, project.getName());
            statement.setString(2, project.getUrl());
            statement.setString(3, project.getDescription());
            statement.setInt(4, project.getStarCount());
            statement.setInt(5, project.getForkCount());
            statement.setInt(6, project.getOpenedIssueCount());
            // 预期想往数据库中插入的日期形如: 20200321
            // 可以根据当前系统时间 + SimpleDateFormat 类来完成
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            statement.setString(7, simpleDateFormat.format(System.currentTimeMillis()));
            // 3. 执行 SQL 语句, 完成数据库插入操作
            int ret = statement.executeUpdate();
            if (ret != 1) {
                System.out.println("当前数据库执行插入数据出错");
                return;
            }
            System.out.println("数据插入成功");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, statement, null);
        }
    }

    public List<Project> selectProjectByDate(String date) {
        List<Project> projects = new ArrayList<>();
        // 1. 获取到数据库连接
        Connection connection = DBUtil.getConnection();
        // 2. 拼装 SQL 语句
        String sql = "select name,url,starCount,forkCount," +
                "openedIssueCount from project_table where date = ? order by starCount desc";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, date);
            // 3. 执行 SQL 语句
            resultSet = statement.executeQuery();
            // 4. 遍历结果集合
            while (resultSet.next()) {
                Project project = new Project();
                project.setName(resultSet.getString("name"));
                project.setUrl(resultSet.getString("url"));
                project.setStarCount(resultSet.getInt("starCount"));
                project.setForkCount(resultSet.getInt("forkCount"));
                project.setOpenedIssueCount(resultSet.getInt("openedIssueCount"));
                projects.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, statement, resultSet);
        }
        return projects;
    }

    public static void main(String[] args) {
        ProjectDao projectDao = new ProjectDao();
        List<Project> projects = projectDao.selectProjectByDate("20200407");
        System.out.println(projects);
    }
}
