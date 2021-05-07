package com.future.webcollector.task;

import cn.edu.hfut.dmic.webcollector.model.Page;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class PageUtils {
    /**
     * 获取webcollector 自带 htmlUnitDriver实例(模拟默认浏览器)
     *
     * @param page
     * @return
     */
    public static HtmlUnitDriver getDriver(Page page) {
        HtmlUnitDriver driver = new HtmlUnitDriver();
        driver.setJavascriptEnabled(true);
        driver.get(page.url());
        return driver;
    }

    /**
     * 获取webcollector 自带htmlUnitDriver实例
     *
     * @param page
     * @param browserVersion 模拟浏览器
     * @return
     */
    public static HtmlUnitDriver getDriver(Page page, BrowserVersion browserVersion) {
        HtmlUnitDriver driver = new HtmlUnitDriver(browserVersion);
        driver.setJavascriptEnabled(true);
        driver.get(page.url());
        return driver;
    }

    /**
     * 获取PhantomJsDriver(可以爬取js动态生成的html)
     *
     * @param page
     * @return
     */
    public static WebDriver getWebDriver(Page page) {
//    	WebDriver driver = new HtmlUnitDriver(true);

//    	System.setProperty("webdriver.chrome.driver", "D:\\Installs\\Develop\\crawling\\chromedriver.exe");
//    	WebDriver driver = new ChromeDriver();

        System.setProperty("phantomjs.binary.path", "D:/Program Files/phantomjs-2.0.0-windows/bin/phantomjs.exe");
        WebDriver driver = new PhantomJSDriver();
        driver.get(page.url());

//    	JavascriptExecutor js = (JavascriptExecutor) driver;
//    	js.executeScript("function(){}");
        return driver;
    }

    /**
     * 直接调用原生phantomJS(即不通过selenium)
     *
     * @param page
     * @return
     */
    public static String getPhantomJSDriver(Page page) {
        Runtime rt = Runtime.getRuntime();
        Process process = null;
        try {
            process = rt.exec("D:/Program Files/phantomjs-2.0.0-windows/bin/phantomjs.exe" +
                    "D:/MyEclipseWorkSpace/WebCollectorDemo/src/main/resources/parser.js " +
                    page.url().trim());
            InputStream in = process.getInputStream();
            InputStreamReader reader = new InputStreamReader(
                    in, "UTF-8");
            BufferedReader br = new BufferedReader(reader);
            StringBuffer sbf = new StringBuffer();
            String tmp = "";
            while((tmp = br.readLine())!=null){
                sbf.append(tmp);
            }
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
