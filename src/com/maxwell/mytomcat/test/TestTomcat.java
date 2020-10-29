package com.maxwell.mytomcat.test;

import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.maxwell.mytomcat.util.MiniBrowser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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
}
