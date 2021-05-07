package com.future.webmagic.zb;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过模拟发送post请求获取下页
 */
public class ZBProcessorBak implements PageProcessor {
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

            System.out.println("标段编号:"+Jsoup.parse(proNodes.get(2).css("td").nodes().get(1).get()).text());
            page.putField("标段编号",Jsoup.parse(proNodes.get(2).css("td").nodes().get(1).get()).text());
            System.out.println("标段名称:"+Jsoup.parse(proNodes.get(2).css("td").nodes().get(3).get()).text());
            page.putField("标段名称:",Jsoup.parse(proNodes.get(2).css("td").nodes().get(3).get()).text());
            String start = Jsoup.parse(page.getHtml().css("span#RptStartDate_23").get()).text();
            String end = Jsoup.parse(page.getHtml().css("span#RptEndDate_23").get()).text();
            System.out.println("公告起始时间："+start+"-"+end);

        }else{
           /* //获取当前页数以及总页数
            Selectable selectable = page.getHtml().css("div#MoreInfoList1_Pager>table tr>td>font>b").nodes().get(1);
            String pages = Jsoup.parse(selectable.get()).text();
            int nowPage = Integer.parseInt(pages.split("/")[0]);
            int totalPage = Integer.parseInt(pages.split("/")[1]);*/
            //节点不为空，为列表页
            System.out.println("列表页");
            for (Selectable node : nodes) {
                //解析对应元素，获取详情页url
                Selectable td = node.css("td").nodes().get(1);
                String href = td.css("a", "onclick").toString();
                String url = href.split("\"")[1];
                String suffixUrl = url.substring(2);
                String wholeUrl = baseUrl + suffixUrl;
                //将详情页url添加到待抓取队列中
                page.addTargetRequest(wholeUrl);
            }
        }
    }

    private Site site = Site.me()
            .setCharset("gbk")
            .setTimeOut(100*1000)
            .setRetryTimes(3)
            .setRetrySleepTime(1000)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36");

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        //查询页数
        final int queryNum = 6;

        //模拟表单提交 form-data提交
        String baseUrl = "http://www.jszb.com.cn/jszb/YW_info/ZhaoBiaoGG/MoreInfo_ZBGG.aspx?categoryNum=012";
        //通过jsoup方式获取dom文本，以获取我们所需的参数值
        Document document = Jsoup.connect(baseUrl).get();
        Map<String,Object> params = new HashMap<>();
        params.put("__VIEWSTATE",document.select("input#__VIEWSTATE").attr("value"));
        params.put("__EVENTTARGET","MoreInfoList1$Pager");
        params.put("__LASTFOCUS","");
        params.put("__VIEWSTATEENCRYPTED","");
        params.put("__EVENTVALIDATION",document.select("input#__EVENTVALIDATION").attr("value"));
        params.put("MoreInfoList1$jpdDi",-1);
        params.put("MoreInfoList1$jpdXian", -1);
        Request[] requests = new Request[queryNum];
        for (int i =1; i <= queryNum; i++) {
            params.put("__EVENTARGUMENT", String.valueOf(i));
            //模拟post方式表单提交
            Request request = new Request(baseUrl);
            request.setMethod(HttpConstant.Method.POST);
            //表单提交信息
            request.setRequestBody(HttpRequestBody.form(params, "gbk"));
//            request.addCookie("ASP.NET_SessionId", "1fswkozxo4t1qrbdz3sihgek");
            requests[i-1]=request;
            System.out.println(requests);
        }

        Spider.create(new ZBProcessorBak())
                .addRequest(requests)
                .addUrl(baseUrl)
                .addPipeline(new FilePipeline("C:\\Users\\Administrator\\Desktop\\crawler"))    //默认输出到控制台，filePipeline 输出到指定文件夹， 自定义pipeline 则可以自定义持久化
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                .thread(50)
                .run();

        long end = System.currentTimeMillis();
        System.out.println("程序共执行："+(end-start)/1000+"秒");
    }
}
