package com.future.webmagic.task;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class JsoupDemo {
    public static void main(String[] args) throws IOException {
        Connection conn = Jsoup.connect("http://www.jszb.com.cn/jszb/YW_info/ZhaoBiaoGG/MoreInfo_ZBGG.aspx?categoryNum=012");
//        //先获得的是整个页面的html标签页面
        Document document = conn.get();
        Elements elements = document.select("td#MoreInfoList1_tdcontent>table>tbody>tr");
        System.out.println(elements);

//        System.out.println(document);

//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        HttpGet get = new HttpGet("http://www.jszb.com.cn/jszb/YW_info/ZhaoBiaoGG/MoreInfo_ZBGG.aspx?categoryNum=012");
//        CloseableHttpResponse response = httpclient.execute(get);
//        String entity = EntityUtils.toString(response.getEntity(), "UTF8");
//        System.out.println(entity);

    }
}
