package com.maxwell.mytomcat;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;
import com.maxwell.mytomcat.http.Request;
import com.maxwell.mytomcat.http.Response;
import com.maxwell.mytomcat.util.Constant;
import com.maxwell.mytomcat.util.ThreadPoolUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Bootstrap {
    public static void main(String[] args) {

        try {
            logJVM();

            int port = 18080;

            ServerSocket ss = new ServerSocket(port);

            while(true) {
                Socket s = ss.accept();

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Request request = new Request(s);
                            String requestString = request.getRequestString();
                            System.out.println("浏览器的输入信息： \r\n" + requestString);

                            Response response = new Response();
                            // 获取URI
                            String uri = request.getUri();
                            if(uri == null) {
                                return;
                            }
                            if(Objects.equals(uri, "/")) {
                                String html = "Hello DIY Tomcat from Maxwell-L";
                                response.getPrintWriter().println(html);
                            } else {
                                String fileName = StrUtil.removePrefix(uri, "/");
                                File file = FileUtil.file(Constant.rootFolder, fileName);
                                if(file.exists()) {
                                    String fileContent = FileUtil.readUtf8String(file);
                                    response.getPrintWriter().println(fileContent);

                                    if(fileName.equals("timeConsume.html")) {
                                        ThreadUtil.sleep(1000);
                                    }
                                } else {
                                    response.getPrintWriter().println("File Not Found");
                                }
                            }
                            handle200(s, response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ThreadPoolUtil.run(r);
            }
        } catch (IOException e) {

        }

    }

    private static void logJVM() {
        Map<String, String> infos = new LinkedHashMap<>();
        infos.put("Server version", "Maxwell-L Tomcat/1.0");
        infos.put("Server built", "2020-10-28 12:00:00");
        infos.put("Server number", "1.1");
        infos.put("OS Name\t", SystemUtil.get("os.name"));
        infos.put("OS Version", SystemUtil.get("os.version"));
        infos.put("Architecture", SystemUtil.get("os.arch"));
        infos.put("Java Home", SystemUtil.get("java.home"));
        infos.put("JVM Version", SystemUtil.get("java.runtime.version"));
        infos.put("JVM Vendor", SystemUtil.get("java.vm.specification.vendor"));

        Set<String> keys = infos.keySet();
        for(String key : keys) {
            LogFactory.get().info(key + ":\t\t" + infos.get(key));
        }
    }

    private static void handle200(Socket s, Response response) throws IOException {
        String contentType = response.getContentType();
        String headText = Constant.response_head_202;
        headText = StrUtil.format(headText, contentType);
        byte[] head = headText.getBytes();

        byte[] body = response.getBody();

        byte[] responseBytes = new byte[head.length + body.length];
        ArrayUtil.copy(head, 0, responseBytes, 0, head.length);
        ArrayUtil.copy(body, 0, responseBytes, head.length, body.length);

        OutputStream outputStream = s.getOutputStream();
        outputStream.write(responseBytes);
        s.close();
    }
}
