package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.Project;
import dao.ProjectDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// 此处写这个代码的时候大家要意识到这是在基于多态的语法来实现的.
// 带来的好处就是基于多态, Servlet 框架就能提供一些时机, 让用户插入自己的逻辑来完成一些工作.
// 此时对于用户来说, 并不需要了解 Servlet 内部是怎么工作, 只需要知道针对一个 GET / POST 请求
// 如何处理即可

/**
 * @Author：Eric
 * @Date：2020/4/5 9:49
 */
public class AllRankServlet extends HttpServlet {
    private Gson gson = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 准备工作
        resp.setContentType("application/json; charset=utf-8");
        // 2. 解析请求, 获取其中日期参数. "date" 来源于 请求中的 query string 部分
        String date = req.getParameter("date");
        if (date == null || date.equals("")) {
            resp.setStatus(404);
            resp.getWriter().write("date 参数错误");
            return;
        }
        // 3. 从数据库中查找数据
        ProjectDao projectDao = new ProjectDao();
        List<Project> projects = projectDao.selectProjectByDate(date);
        // 4. 把数据整理成 json 格式并返回给客户端
        String respString = gson.toJson(projects);
        resp.getWriter().write(respString);
    }
}
