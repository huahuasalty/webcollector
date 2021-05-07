package com.future.webcollector.zb;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.plugin.net.OkHttpRequester;
import com.future.webcollector.dao.ZBInfosRepository;
import com.future.webcollector.model.ZbInfos;
import com.sun.istack.internal.logging.Logger;
import okhttp3.Request;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ZBCrawler extends BreadthCrawler {

    private static Logger logger= Logger.getLogger(ZBCrawler.class);
    @Autowired
    private ZBInfosRepository repository;

    private  final String baseUrl = "http://www.jszb.com.cn/jszb/YW_info";

    private final String seedUrl = "http://www.jszb.com.cn/jszb/YW_info/ZhaoBiaoGG/MoreInfo_ZBGG.aspx?categoryNum=012";

    public ZBCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        //设置线程数量
        setThreads(50);
        //设置每页爬取的数量
        getConf().setTopN(100);
        //断点爬取  默认为false
//        setResumable(true);
        //添加过滤url的正则表达式
//        addRegex("");
    }

    @Override
    public void visit(Page page, CrawlDatums crawlDatums) {
        System.out.println(page.url());
        Elements eles = page.select("td#MoreInfoList1_tdcontent>table>tbody>tr");
        if(eles.size()>0){
            //列表页
            System.out.println("列表页");
            //获取总页数和当前页数
            String pages = page.select("div#MoreInfoList1_Pager>table>tbody>tr>td").get(0).select("font").get(1).select("b").text();
            int nowPage = Integer.parseInt(pages.split("/")[0]);
            int totalPage = Integer.parseInt(pages.split("/")[1]);

            //获取列表页中每列数据
            for (Element ele:eles) {
                String href = ele.select("td").get(1).select("a").attr("onclick");
                String nextUrl = getNextUrl(href);
                //将拼接完成的url添加到待下载队列
                crawlDatums.add(nextUrl);
            }

            if(nowPage<totalPage){
                //获取页面驱动
                HtmlUnitDriver driver = new HtmlUnitDriver(true);
                //打开指定的网站
                driver.get(page.url());
                //如果当前页小于总页数，向后翻页获取下一页列表的链接
                for (int i = nowPage+1;i<=6;i++) {
                    //获取下一页的标签元素
                    WebElement element1 = driver.findElement(By.xpath("//div[@id='MoreInfoList1_Pager']/table/tbody/tr/td/a/img[@src='/JSZB/images/page/nextn.gif']"));
                    //点击下一页的标签
                    element1.click();
                    //获取下一页的列表信息
                    List<WebElement> elements = driver.findElements(By.cssSelector("td#MoreInfoList1_tdcontent>table>tbody>tr"));
                    for (WebElement ele : elements) {
                        //获取下一页列表中链接
                        String href = ele.findElements(By.cssSelector("td")).get(1).findElement(By.cssSelector("a")).getAttribute("onclick");
                        String nextUrl = getNextUrl(href);
                        crawlDatums.add(nextUrl);
                    }
                }
            }
        }else {
            //详情页
//            System.out.println("详情页");
            Elements select = page.select("table#Table1>tbody>tr");
            Element trele = select.get(1);
            Element bdele = select.get(2);
            String proNo = null;
            String proName = null;
            String bdNo = null;
            String bdName = null;
            String start = page.selectText("span#RptStartDate_23");
            String end = page.selectText("span#RptEndDate_23");;

            proNo = trele.select("td").get(1).text();
            proName = trele.select("td").get(3).text();
            bdNo = bdele.select("td").get(1).text();
            bdName = bdele.select("td").get(3).text();

            System.out.println("项目名称："+proName+" 项目编号："+proNo);
            System.out.println("标段名称："+bdName+" 标段编号："+bdNo);
            System.out.println("公告起始时间："+start+"-"+end);

           /* ZbInfos info = new ZbInfos();
            info.setZbBdName(bdName);
            info.setZbBdNo(bdNo);
            info.setZbProName(proName);
            info.setZbProNo(proNo);
            repository.save(info);*/
        }
    }

    // 自定义的请求插件
    // 可以自定义User-Agent和Cookie,这里主要是解决爬取内容乱码问题
   public static class MyRequester extends OkHttpRequester {
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36";
        String cookie = "";
        // 每次发送请求前都会执行这个方法来构建请求
        @Override
        public Request.Builder createRequestBuilder(CrawlDatum crawlDatum) {
            // 这里使用的是OkHttp中的Request.Builder
            // 可以参考OkHttp的文档来修改请求头
//            System.out.println("自定义请求");
            return super.createRequestBuilder(crawlDatum)
                    .removeHeader("User-Agent")  //移除默认的UserAgent
//                    .addHeader("Referer", "http://www.xxx.com")
                    .addHeader("User-Agent", userAgent)
//                    .addHeader("Cookie",cookie);
//                    .addHeader("Character-Encoding","GBK")
                    .addHeader("Content-Type","text/html; charset=gb2312");
        }
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




public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        //第一个参数表示设置保存爬取记录的文件夹，第二个参数为是否自动爬取
        ZBCrawler zbCrawler = new ZBCrawler("C:\\Users\\Administrator\\Desktop\\crawler",false);
        //设置请求插件，这里用于解决部分编码乱码问题
        zbCrawler.setRequester(new MyRequester());
        //添加种子路径
        zbCrawler.addSeed("http://www.jszb.com.cn/jszb/YW_info/ZhaoBiaoGG/MoreInfo_ZBGG.aspx?categoryNum=012");
        //爬取深度
        zbCrawler.start(2);
        long end = System.currentTimeMillis();
        System.out.println("程序执行时间："+(end-start)/1000+"秒");
    }

}
