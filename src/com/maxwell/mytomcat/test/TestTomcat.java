package com.maxwell.mytomcat.test;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.maxwell.mytomcat.util.MiniBrowser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestTomcat {
    private static int port = 18080;
    private static String ip = "127.0.0.1";

    @BeforeClass
    public static void beforeClass() {
        if(NetUtil.isUsableLocalPort(port)) {
            System.err.println("ERROR::端口 " + port + " MyTomcat未启动");
            System.exit(1);
        } else {
            System.out.println("INFO::MyTomcat已启动, 开始单元测试");
        }
    }

    @Test
    public void testHelloTomcat() {
        String html = getContentString("/");
        Assert.assertEquals(html, "Hello DIY Tomcat from Maxwell-L");
    }

    private String getContentString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        String content = MiniBrowser.getContentString(url);
        return content;
    }

    @Test
    public void testHelloHtml() {
        String html = getContentString("/hello.html");
        Assert.assertEquals(html, "Hello DIY Tomcat from hello.html");
    }

    @Test
    public void testTimeConsumeHtml() throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                20,
                20,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(10)
        );

        TimeInterval timeInterval = DateUtil.timer();

        for(int i = 0; i < 3; i++) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    getContentString("/timeConsume.html");
                }
            });
        }

        threadPoolExecutor.shutdown();
        threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);

        long duration = timeInterval.intervalMs();

        Assert.assertTrue(duration < 3000);
    }

    @Test
    public void testApp1Index() {
        String html = getContentString("/APP1/index.html");
        Assert.assertEquals(html, "Hello DIY Tomcat from index.html@APP1");
    }
}
