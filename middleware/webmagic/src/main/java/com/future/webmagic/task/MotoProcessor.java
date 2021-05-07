package com.future.webmagic.task;

import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Html;

import java.util.List;

@Component
public class MotoProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        WebDriver driver = new HtmlUnitDriver(true);
        driver.get("");
        //获取需要解析的页面
        Html html = page.getHtml();
        //判断该页面是列表页还是详情页
        List<String> list = html.css("td#MoreInfoList1_tdcontent>table>tbody>tr").all();
        if(list.size()==0){
            //如果集合为空则为详情页
            System.out.println("详情页");
        }else{
            //如果集合不为空则为列表页
            System.out.println("列表页");
        }


        //将获取到的数据保存到自定义pipeline中
        //添加其他的url到队列中待爬取
//        page.addTargetRequest(page.getHtml().css("").links().toString());
//        List<String> all = html.css("div.product-list-items-wrap").all();
        Document document = html.getDocument();
        page.putField("document",document);
    }

    private Site site = Site.me()
            .setCharset("utf8")                 //设置编码格式
            .setTimeOut(5*1000)                 //设置超时时间
            .setRetryTimes(3)                   //设置重试次数
            .setRetrySleepTime(1000)            //设置每次重试时间
            .addHeader("Accept-Encoding", "/");  //设置请求头 最基本的反 反爬虫手段

    @Override
    public Site getSite() {
        return site;
    }

    @Autowired
    private MotoPipeline motoPipeline;

   /* @Scheduled(cron = "0/5 * * * * *")
    public void doTask(){
        Spider.create(new MotoProcessor())      //
//                .addUrl("http://www.cdfgsanya.com/product-list.html?sw=%E6%89%8B%E8%A1%A8")
                .addUrl("http://www.jszb.com.cn/jszb/")
                .thread(5)                      //开启几个线程进行数据爬取
//                .addPipeline(motoPipeline)      //将页面解析出来的数据   默认输出到控制台
                //将webmagic中QueueScheduler的默认Hashset过滤器替换为布隆过滤器
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                .run();
    }*/

    public static void main(String[] args) {
        Spider.create(new MotoProcessor())      //
//                .addUrl("http://www.cdfgsanya.com/product-list.html?sw=%E6%89%8B%E8%A1%A8")
                .addUrl("http://www.jszb.com.cn/jszb/YW_info/ZhaoBiaoGG/MoreInfo_ZBGG.aspx?categoryNum=012")
                .thread(100)                      //开启几个线程进行数据爬取
//                .addPipeline(motoPipeline)      //将页面解析出来的数据   默认输出到控制台
                //将webmagic中QueueScheduler的默认Hashset过滤器替换为布隆过滤器
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                .run();
    }
}
