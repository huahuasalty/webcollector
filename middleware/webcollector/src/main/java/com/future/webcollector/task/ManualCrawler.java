package com.future.webcollector.task;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.Proxys;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;

import javax.swing.*;
import java.util.List;

public class ManualCrawler extends BreadthCrawler {
    public ManualCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        getConf().setTopN(100);
        setThreads(5);
    }

    /**
     * 如果需要数据持久化，则在visit()方法中处理
     * @param page
     * @param crawlDatums
     */
    @Override
    public void visit(Page page, CrawlDatums crawlDatums) {
//        WebDriver driver = PageUtils.getDriver(page);
       /* System.setProperty("webdriver.chrome.driver", "E://googleDownloads/chromedriver.exe");  // chromedriver服务地址
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");                     // 谷歌使用无痕模式
        WebDriver webDriver = new ChromeDriver(options);*/
       WebDriver webDriver = new HtmlUnitDriver(true);
        webDriver.get(page.url());
//        System.out.println("PageType"+page.links().toString());
        System.out.println("浏览器网页1："+webDriver.findElement(By.cssSelector("ul.cm-pagination li.cm-pagination-number-current")).getText());
        /*List<WebElement> elements = webDriver.findElements(By.cssSelector("div.product-list-items-wrap>div"));
//            List<WebElement> elements = driver1.findElementsByCssSelector("div.product-list-items-wrap>div");
        for (WebElement element:elements) {
            String title = element.findElement(By.cssSelector("div.item-image-wrap")).getAttribute("title");
            System.out.println("商品名："+title);
        }*/
        System.out.println(webDriver.findElement(By.cssSelector("li.cm-pagination-next")).getText());
        Actions action = new Actions(webDriver);
        action.click(webDriver.findElement(By.cssSelector("li.cm-pagination-next")));
        System.out.println("浏览器网页2："+webDriver.findElement(By.cssSelector("ul.cm-pagination li.cm-pagination-number-current")).getText());

        webDriver.close();
//        String keyword = page.meta("keyword");
    }

    public static void main(String[] args) throws Exception {
        ManualCrawler manualCrawler = new ManualCrawler("crawler",true);
        manualCrawler.addSeed("http://www.cdfgsanya.com/product-list.html?sw=%E6%89%8B%E8%A1%A8","list");
//        manualCrawler.addSeed("http://www.cdfgsanya.com/api/overseas/products/search?c=1&sw=%E6%89%8B%E8%A1%A8&hs=false&pn=1&ps=20&s=0");
        manualCrawler.start(2);
    }

   /* Proxys proxys = new Proxys();

    @Override
    public Page getResponse(CrawlDatum crawlDatum) throws Exception{
        HttpRequest request = new HttpRequest(crawlDatum);
        request.setProxy(proxys.nextRandom());   //通过Proxys.nextRandom()方法可以随机获取加入的代理。
        proxys.add("127.0.0.1",9091);   //添加代理
//        request.setProxy();
        return request.responsePage();

    }*/
}
