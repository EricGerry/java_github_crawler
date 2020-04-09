# java_github_crawler

爬取Github上的一些java中的知名项目，做一个简单的类似GitHub趋势功能的程序。在awesome-java项目中可以看到很多的java开源的第三方库和框架，点进去可以看到很具体的页面后，可以看到该项目中的一些具体信息(star,fork,open_issue)，于是收集这些项目的这些属性，衡量出这一大堆项目中，哪些项目是比较活跃和流行的，最终需求，是给awesome-java这个项目中提到的所有的github上的项目按照活跃程度进行排序。形成一个类似于Github趋势的小程序。

---

## 基本步骤

### 获取所有待收集信息的项目列表

使用爬虫程序，获取到[awesome-java](https://github.com/akullpp/awesome-java/blob/master/README.md)这个页面的内容，进而知道所有项目的链接信息。
分析页面的结构，可以看到，入口页面中包含很多url，每个url中又有很多a标签，a标签中href属性里的url就是我们要获到的内容。

#### 如何获取页面内容：构造一个http请求发送给服务器

通过[OkHttp](https://square.github.io/okhttp/)这个库，就能根据url获取到对应的页面的内容，内容一般是一个html页面,具体使用方法参考[官方文档](https://square.github.io/okhttp/)。

> OkHttp这个库是使用Kotlin(一种编程语言，和java类似，都是把代码编译生成JVM的字节码)

#### 如何分析页面结构

采用[JSoup](https://jsoup.org/)分析网页结构，JSoup是一个用于处理实际HTML的Java库。它使用HTML5最佳DOM方法和CSS选择器，为获取URL以及提取和处理数据提供了非常方便的API。，具体使用参考官方文档。

HTML是一个树形结构，JSoup中的Document对象对应着一个树形结构。树中的每个标签，就是一个Element对象，把很多标签放在一起构成的集合，就是Elements。

- 先获取li标签得到Element对象针对这个对象再次findElementByTag("a")获取到a标签中的内容
- JSoup支持CSS风格的选择器语法。

---

### 遍历项目列表，依次获取到每个项目的主页信息，进一步就可以知道该项目的star fork open_issue

#### 通过API来获取某个项目/仓库的相关信息

```curl
curl https://api.github.com/repos/[用户名]/[仓库名]
```

#### 通过JSON数据格式保存相关信息

Github API获取到的数据格式是JSON格式是一种非常广泛使用的数据组织格式,特点是以键值对的的方式来组织数据。基本规则: {}包裹里面包含若干个键值对.键值对和键值对之间使用,分割。键和值之间使用:分割，进而获取要爬取的关键信息(每个项目的相关指标)，以JSON格式保存。

Java中有很多第三方库帮我们解析，JSON源自于JavaScript这个语言。通过JSON来表示对象。JSON比XML好用很多。GSON google出品的一个JSON解析库。

---

### 把这些数据保存到数据库中

#### 创建表结构

```mysql
create database java_github_crawler;
use java_github_crawler;
create table project_table(
    name varchar(50),url varchar(1024),
    description varchar(1024),starCount int,
    forkCount int, openedIssueCount int, date varchar(128)
);
```

#### 实现ProjectDao

先创建Connection,再根据Connection创建PrepareStatement，通过PrepareStatement获取到ResultSet。关闭的时候,也要先关闭ResultSet,然后关闭PrepareStatement,最后关闭Connection。

#### 抓取时可能出现的问题

##### 1.出现异常.

当前这个url对应的不是一个github的项目.前面把这些不是项目的url放到一个黑名单里面了(没放干净)。我们真正应该保证的是,抓取过程中任何一个项目出现异常,不要影响到后面的抓取.用try catch把抓取的循环包裹起来。

##### 2.程序执行速度太慢了.如何进行性能优化

打印出每个环节的时间，可以看到性能最差的就是循环调用Github API这个操作是访问网络;访问的还是一个外国的服务器。
大量的时间都消耗在等待服务器响应上了。一次服务器的响应时间， 这基本是固定. (取决于网络环境)
实现批量发送的方式，有很多种方式使用多线程方式来发送数据。例如我想批量发送10个数据就创建10个线程.每个线程负责等待自己的结构

---

### 写一个简单网页服务器，来展示数据库中的数据(通过图表的的形式，看到一个更直观的排名效果)

- 1.扩充ProjectDao类,新增一个方法,能够按照指定日期来获取数据库中的信息
- 2.写一个简单的Servlet,提供-个HTTP的接口，让前端可以通过这个接口来获取到数据
- 3.写一个网页,在网页上获取到数据,并展示成图表EChart 百度提供的一个开源的图表组件，使用方法具体参考
[官方文档](https://www.echartsjs.com/zh/tutorial.html#5%20%E5%88%86%E9%92%9F%E4%B8%8A%E6%89%8B%20ECharts)
[下载安装](https://www.echartsjs.com/zh/builder.html)

---

[项目演示](http://47.93.16.83:8080/java_github_crawler/index.html)