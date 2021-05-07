package com.future.webcollector.task;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;
import com.future.webcollector.zb.ZBCrawler;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import okhttp3.Request;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;
import java.util.regex.Pattern;

/**
 *
 */
public class AutoCrawler extends BreadthCrawler {


    private final String baseUrl = "http://www.jszb.com.cn/jszb/YW_info";

    public AutoCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        /**
         * 设置爬取的网站地址，添加种子，种子链接会在爬虫启动前加入到抓取信息中并标记为未抓取状态，这个过程成为注入
         */
//        this.addSeed("");

        /** addRegex 参数为一个 url 正则表达式, 可以用于过滤不必抓取的链接，如 .js .jpg .css ... 等
         * 也可以指定抓取某些规则的链接，如下 addRegex 中会抓取 此类地址：
         * https://blog.github.com/2018-07-13-graphql-for-octokit/
         * */
//        this.addRegex("https://blog.github.com/[0-9]{4}-[0-9]{2}-[0-9]{2}-[^/]+/");
//        this.addRegex("http://www.cdfgsanya.com/product.html?productId=[0-9]+&goodsId=[0-9]+&warehouseId=[0-9]+&brandId=[0-9]+");
//        this.addRegex("http://www.jszb.com.cn/jszb/YW_info/ZhaoBiaoGG/[\\w]*+.aspx?.*");
        /**
         * 过滤 jpg|png|gif 等图片地址 时：
         * this.addRegex("-.*\\.(jpg|png|gif).*");
         * 过滤 链接值为 "#" 的地址时：
         * this.addRegex("-.*#.*");
         */

//        this.setNextFilter();
        /**
         * 设置线程数量
         */
        setThreads(10);
        /**
         * 设置每层爬取爬取的最大URL数量
         */
        getConf().setTopN(100);

        /**
         * 是否进行断电爬取，默认为false
         */
        setResumable(false);


    }

    /**
     * 必须重写 visit 方法，作用是:
     * 在整个抓取过程中,只要抓到符合要求的页面,webCollector 就会回调该方法,并传入一个包含了页面所有信息的 page 对象
     * @param page
     * @param crawlDatums
     */
    @Override
    public void visit(Page page, CrawlDatums crawlDatums) {
//        WebDriver driver = PageUtils.getDriver(page, BrowserVersion.CHROME);

        //引入selenium的jar 需要下载chrormdriver.exe驱动
//        System.setProperty("webdriver.chrome.driver", "E://googleDownloads/chromedriver.exe");  // chromedriver服务地址

//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");                 // 谷歌使用无痕模式
//        WebDriver driver1 = new ChromeDriver(options);      //通过 new WebDriver（）构建一个 浏览器模拟器，不仅将获取到的 html 源码来进行执行 JS 渲染，最后得到一个 JS 执行后的 html 源码
        //允许js操作
//        driver1.setJavascriptEnabled(true);
        if(page.matchUrl("http://www.jszb.com.cn/jszb/YW_info/ZhaoBiaoGG/MoreInfo_ZBGG.aspx?categoryNum=012")){
                //列表页
            System.out.println("列表页");
            HtmlUnitDriver driver1 = new HtmlUnitDriver(true);
            driver1.get(page.url());     //打开指定的网站
            List<WebElement> elements = driver1.findElements(By.cssSelector("td#MoreInfoList1_tdcontent>table>tbody>tr"));
            for (WebElement ele : elements) {
                String href = ele.findElements(By.cssSelector("td")).get(1).findElement(By.cssSelector("a")).getAttribute("onclick");
                String url = href.split("\"")[1];
                String suffixUrl = url.substring(2, url.length());
                String wholeUrl = baseUrl + suffixUrl;
                crawlDatums.add(wholeUrl);
            }

        }else {
            //详情页
            System.out.println("详情页");
        }

        /**
         * 模拟用户操作事件
         */
       /* Actions action = new Actions(driver1);
        action.click();                 //鼠标左键单击时间
        action.click(driver1.findElement(By.id("wrap")));   //鼠标单击指定元素
        action.contextClick();          //鼠标右击事件
        action.contextClick(driver1.findElement(By.id("wrap")));          //鼠标右击指定元素
        action.doubleClick();           //鼠标双击事件
        action.doubleClick(driver1.findElement(By.id("wrap")));           //鼠标双击指定元素
        action.release();               //鼠标释放事件

        action.sendKeys(Keys.TAB);      //模拟按下并释放tab键
        action.sendKeys(Keys.SPACE);    //模拟按下并释放空格键*/

    }

    // 主要解决下载图片出现403的问题
    // 自定义的请求插件
    // 可以自定义User-Agent和Cookie
    public static class MyRequester extends OkHttpRequester {
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36";
        String cookie = "";
        // 每次发送请求前都会执行这个方法来构建请求
        @Override
        public Request.Builder createRequestBuilder(CrawlDatum crawlDatum) {
            // 这里使用的是OkHttp中的Request.Builder
            // 可以参考OkHttp的文档来修改请求头
            System.out.println("自定义请求");
            return super.createRequestBuilder(crawlDatum)
                    .removeHeader("User-Agent")  //移除默认的UserAgent
//                    .addHeader("Referer", "http://www.xxx.com")
                    .addHeader("User-Agent", userAgent)
                    .addHeader("Cookie",cookie);

        }
    }

    public Page getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);
        request.setMethod(crawlDatum.meta("method"));

        return request.responsePage();
    }

    public static void main(String[] args) throws Exception {
        /**
         * AutoCrawler 构造器中会进行 数据初始化，这两个参数接着会传给父类
         * super(crawlPath, autoParse);
         * crawlPath：表示设置保存爬取记录的文件夹，本例运行之后会在应用根目录下生成一个 "crawl" 目录存放爬取信息
         * autoParse: 如果autoParse设置为true，遍历器会自动解析页面中符合正则的链接，加入后续爬取任务，否则不自动解析链接
         * */
        AutoCrawler crawler = new AutoCrawler("C:\\Users\\Administrator\\Desktop\\crawler",false);
//        crawler.addSeed("https://www.jit.edu.cn/yxbm1/jxkydw.htm");
        crawler.addSeed("http://www.jszb.com.cn/jszb/YW_info/ZhaoBiaoGG/MoreInfo_ZBGG.aspx?categoryNum=012");
        crawler.setRequester(new MyRequester());
        /**
         * 启动爬虫，爬取的深度为4层
         * 添加的第一层种子链接,为第1层
         */
        crawler.start(2);
    }
}
