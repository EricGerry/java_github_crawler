package test;

import crawler.Crawler;

import java.io.IOException;

/**
 * @Author：Eric
 * @Date：2020/4/4 22:29
 */
public class Test {
    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler();
        String html = crawler.getPage("https://github.com/doov-io/doov");
        System.out.println(html);
    }
}
