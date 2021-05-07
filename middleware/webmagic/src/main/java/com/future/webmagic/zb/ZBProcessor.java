package com.future.webmagic.zb;

import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Selectable;

import java.io.IOException;
import java.util.List;

/**
 * 使用selenium方式
 * 通过动态操作页面，来实现抓取下一页数据列表中的url
 */
public class ZBProcessor implements PageProcessor {

    private final String baseUrl = "http://www.jszb.com.cn/jszb/YW_info";
    @Override
    public void process(Page page) {
        List<Selectable> nodes = page.getHtml().css("td#MoreInfoList1_tdcontent>table>tbody>tr").nodes();
        if(nodes.size()==0){
            //节点为空，为详情页
            //获取所需元素
            System.out.println("详情页");
            List<Selectable> proNodes = page.getHtml().css("table#Table1>tbody>tr").nodes();
            String proNo = Jsoup.parse(proNodes.get(1).css("td").nodes().get(1).get()).text();
            String proName = Jsoup.parse(proNodes.get(1).css("td").nodes().get(3).get()).text();

            System.out.println(Jsoup.parse(proNodes.get(1).css("td").nodes().get(0).get()).text()+":"+proNo);
            //数据持久化
            page.putField(Jsoup.parse(proNodes.get(1).css("td").nodes().get(0).get()).text(),Jsoup.parse(proNodes.get(1).css("td").nodes().get(1).get()).text());
            System.out.println(Jsoup.parse(proNodes.get(1).css("td").nodes().get(2).get()).text()+":"+proName);
            page.putField(Jsoup.parse(proNodes.get(1).css("td").nodes().get(2).get()).text(),Jsoup.parse(proNodes.get(1).css("td").nodes().get(3).get()).text());

            String no = Jsoup.parse(proNodes.get(2).css("td").nodes().get(1).get()).text();
            String name = Jsoup.parse(proNodes.get(2).css("td").nodes().get(3).get()).text();
            String start = Jsoup.parse(page.getHtml().css("span#RptStartDate_23").get()).text();
            String end = Jsoup.parse(page.getHtml().css("span#RptEndDate_23").get()).text();

            System.out.println("标段编号:"+Jsoup.parse(proNodes.get(2).css("td").nodes().get(1).get()).text());
            page.putField("标段编号",Jsoup.parse(proNodes.get(2).css("td").nodes().get(1).get()).text());
            System.out.println("标段名称:"+Jsoup.parse(proNodes.get(2).css("td").nodes().get(3).get()).text());
            page.putField("标段名称:",Jsoup.parse(proNodes.get(2).css("td").nodes().get(3).get()).text());
            System.out.println("公告起始时间："+start+"-"+end);
        }else{
            //判断当前页是否为最后一页，如果不是则进行点击下一页操作
            //获取当前页数以及总页数
            Selectable selectable = page.getHtml().css("div#MoreInfoList1_Pager>table tr>td>font>b").nodes().get(1);
            String pages = Jsoup.parse(selectable.get()).text();
            int nowPage = Integer.parseInt(pages.split("/")[0]);
            int totalPage = Integer.parseInt(pages.split("/")[1]);
            //节点不为空，为列表页
            //若为列表页则先处理当前页数据后再处理下一页数据
            System.out.println("列表页");
            for (Selectable node : nodes) {
                //解析对应元素，获取详情页url
                Selectable td = node.css("td").nodes().get(1);
                String href = td.css("a", "onclick").toString();
                /*String url = href.split("\"")[1];
                String suffixUrl = url.substring(2, url.length());*/
                String wholeUrl = getNextUrl(href);
                //将详情页url添加到待抓取队列中
                page.addTargetRequest(wholeUrl);
            }

            //如果当前页小于总页数，则获取下一页
            if(nowPage<totalPage){
                //下页数据开始处理
                //设置驱动
               /* System.setProperty("webdriver.chrome.driver", "E://googleDownloads/chromedriver.exe");
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless");                 //无需打开网页
                options.addArguments("--disable-gpu");
                //初始化driver
                WebDriver driver = new ChromeDriver();*/
//                System.setProperty("phantomjs.binary.path", "E:\\googleDownloads\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
//                WebDriver driver = new PhantomJSDriver();
                //获取页面驱动，参数为是否开启动态抓取js
                WebDriver driver = new HtmlUnitDriver(true);
                //进入页面
                driver.get("http://www.jszb.com.cn/jszb/YW_info/ZhaoBiaoGG/MoreInfo_ZBGG.aspx?categoryNum=012");
                //获取所需元素
                //获取下一页
                for (int i = nowPage+1; i<=6;i++) {
                    WebElement element1 = driver.findElement(By.xpath("//div[@id='MoreInfoList1_Pager']/table/tbody/tr/td/a/img[@src='/JSZB/images/page/nextn.gif']"));
                    element1.click();
                    List<WebElement> elements = driver.findElements(By.cssSelector("td#MoreInfoList1_tdcontent>table>tbody>tr"));
                    for (WebElement ele : elements) {
                        String href = ele.findElements(By.cssSelector("td")).get(1).findElement(By.cssSelector("a")).getAttribute("onclick");
                       /* String url = href.split("\"")[1];
                        String suffixUrl = url.substring(2, url.length());*/
                        String wholeUrl = getNextUrl(href);
                        page.addTargetRequest(wholeUrl);
                    }
                }
                //关闭网页
                driver.close();
            }

           /* List<Selectable> nodess = page.getHtml().css("td#MoreInfoList1_tdcontent>table>tbody>tr").nodes();
            System.out.println(nodes==nodess);
            for (Selectable node: nodes) {
                Selectable td = node.css("td").nodes().get(1);
                String href = td.css("a", "onclick").toString();
                String url = href.split("\"")[1];
                String suffixUrl = url.substring(2,url.length());
                String wholeUrl = baseUrl+suffixUrl;
                page.addTargetRequest(wholeUrl);
            }*/

//            page.getHtml().css("input#__EVENTTARGET")

//            page.getHtml().getDocument().select("input#__EVENTTARGET");
//            page.getHtml().getDocument().select("input#__EVENTTARGET").attr("value","MoreInfoList1$Pager");

//            System.out.println("__EVENTTARGET"+page.getHtml().getDocument().select("input#__EVENTTARGET").attr("value"));
//            System.out.println("__VIEWSTATE"+page.getHtml().getDocument().select("input#__VIEWSTATE").attr("value"));

        }
    }

    //自定义请求参数
    private Site site = Site.me()
            .setCharset("gbk")              //设置编码格式
            .setTimeOut(10*1000)          //设置超时时间
            .setRetryTimes(3)               //设置重试次数
            .setRetrySleepTime(3*1000)      //设置重试睡眠时间
//            .addHeader("")
//            .addCookie("","")
            ;
    @Override
    public Site getSite() {
        return site;
    }

    /**
     * 获取页面列表跳转路径，拼接为新的url
     * @param href
     * @return
     */
    private String getNextUrl(String href){
        String url = href.split("\"")[1];
        String suffixUrl = url.substring(2, url.length());
        String wholeUrl = baseUrl + suffixUrl;
        return wholeUrl;
    }

    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();
        Spider.create(new ZBProcessor())
                .addUrl("http://www.jszb.com.cn/jszb/YW_info/ZhaoBiaoGG/MoreInfo_ZBGG.aspx?categoryNum=012")            //设置爬取路径
                .thread(50)        //设置线程数量
                .addPipeline(new FilePipeline("C:\\Users\\Administrator\\Desktop\\crawler"))        //使用FilePipeline 将数据持久化到文件夹中 或者自定义pipeline 持久化数据  默认输出到控制台
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))    //使用布隆过滤器需要添加guava 依赖来支持
                .run();     //启动爬虫
        long endTime = System.currentTimeMillis();
        System.out.println("程序共执行："+(endTime-startTime)/1000+"秒");
    }
}
