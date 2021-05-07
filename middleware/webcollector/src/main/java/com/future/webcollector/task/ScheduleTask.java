package com.future.webcollector.task;

import com.future.webcollector.zb.ZBCrawler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

public class ScheduleTask {
    @Scheduled(cron = "* */1 * * * ?")
    public void doTask() throws Exception {
        System.out.println("执行定时任务...");
        long start = System.currentTimeMillis();
        //第一个参数表示设置保存爬取记录的文件夹，第二个参数为是否自动爬取
        ZBCrawler zbCrawler = new ZBCrawler("C:\\Users\\Administrator\\Desktop\\crawler",false);
        //设置请求插件，这里用于解决部分编码乱码问题
        zbCrawler.setRequester(new ZBCrawler.MyRequester());
        //添加种子路径
        zbCrawler.addSeed("http://www.jszb.com.cn/jszb/YW_info/ZhaoBiaoGG/MoreInfo_ZBGG.aspx?categoryNum=012");
        //爬取深度
        zbCrawler.start(2);
        long end = System.currentTimeMillis();
        System.out.println("程序执行时间："+(end-start)/1000+"秒");
    }
}
