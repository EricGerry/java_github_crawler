package crawler;
import dao.Project;
import dao.ProjectDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Author：Eric
 * @Date：2020/4/5 9:12
 */
public class ThreadCrawler extends Crawler {
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        // 使用多线程的方式重新组织核心逻辑. 访问 Github API 变成并行式访问
        ThreadCrawler crawler = new ThreadCrawler();
        // 1. 获取到首页内容
        String html = crawler.getPage("https://github.com/akullpp/awesome-java/blob/master/README.md");
        // 2. 分析项目列表
        List<Project> projects = crawler.parseProjectList(html);

        long startCallAPI = System.currentTimeMillis();

        // 3. 遍历项目列表, 就要使用多线程的方式了. 线程池
        //    ExecutorService 有两种提交任务的操作.
        //    a) execute: 不关注任务的结果
        //    b) submit: 关注任务的结果
        //    此处使用 submit 最主要的目的是为了能够知道当前线程池中所有任务啥时候能全完成
        //    等到全都完成之后再保存数据
        List<Future<?>> taskResults = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (Project project : projects) {
            Future<?> taskResult = executorService.submit(new CrawlerTask(project, crawler));
            taskResults.add(taskResult);
        }
        // 等待所有线程池中的任务执行结束, 再进行下一步操作
        for (Future<?> taskResult : taskResults) {
            // 调用 get 方法就会阻塞, 阻塞到该任务执行完毕, get 才会返回
            try {
                taskResult.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        // 代码执行到这一步, 说明所有任务都执行完毕了, 结束线程池
        // 这个操作就是卸磨杀驴, 避免出现线程在这干吃饭不干活. 一定要及时裁员
        executorService.shutdown();

        long finishCallAPI = System.currentTimeMillis();
        // 这个时间是 22s
        System.out.println("调用 API 的时间: " + (finishCallAPI - startCallAPI) + " ms");

        // 4. 保存到数据库
        ProjectDao projectDao = new ProjectDao();
        for (Project project : projects) {
            projectDao.save(project);
        }
        // 这个时间是 28s
        System.out.println("整体程序执行时间: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    static class CrawlerTask implements Runnable {
        private Project project;
        private ThreadCrawler threadCrawler;

        public CrawlerTask(Project project, ThreadCrawler threadCrawler) {
            this.project = project;
            this.threadCrawler = threadCrawler;
        }

        @Override
        public void run() {
            // 依赖两种对象:
            // project 对象, crawler 对象(调用对应的方法完成抓取)

            // 基本步骤
            try {
                System.out.println("crawing " + project.getName() + "...");
                // 1. 调用 API 获取项目数据
                String repoName = threadCrawler.getRepoName(project.getUrl());
                String jsonString = threadCrawler.getRepoInfo(repoName);
                // 2. 解析项目数据
                threadCrawler.parseRepoInfo(jsonString, project);
                System.out.println("crawing " + project.getName() + " done!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
