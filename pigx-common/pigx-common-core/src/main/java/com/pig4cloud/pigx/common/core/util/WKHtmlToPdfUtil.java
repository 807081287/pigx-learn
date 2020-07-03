/**
 * Copyright (C), 2019-2019, 成都房联云码科技有限公司
 * FileName: WKHtmlToPdfUtil
 * Author:   Arron-wql
 * Date:     2019/12/4 12:20
 * Description: wkhtmltopdf工具类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.pig4cloud.pigx.common.core.util;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * wkhtmltopdf工具类
 *
 * @author qinglong.wu@funi365.com
 * @create 2019/12/4
 * @Version 1.0.0
 *
 * 约定：
 *      1. 插件安装位置，windows自己选，要在调用中可以自行修改路径， 在Linux系统中将插件安装在opt目录下（/opt）
 *
 * 注意：
 *      1. wkhtmltopdf的Linux版本中，解压后，默认的文件名为"wkhtmltox"，为了统一起见，一律将解压后的文件名，重命名为"wkhtmltopdf"（命令：mv wkhtmltox wkhtmltopdf）
 *
 */
public class WKHtmlToPdfUtil {
    /**
     * 将HTML文件内容输出为PDF文件
     *
     * @param htmlFilePath HTML文件路径
     * @param pdfFilePath  PDF文件路径
     */
    public static void htmlToPdf(String htmlFilePath, String pdfFilePath) {
        Process process = null;
        try {//注意命令调用路径与安装路径保持一致
            process = Runtime.getRuntime().exec(getCommand(htmlFilePath, pdfFilePath));
            //为了防止waitFor因为流缓存而阻塞，启用两个线程进行流的读取
            new Thread(new ClearBufferThread("Input",process.getInputStream())).start();
            new Thread(new ClearBufferThread("Error",process.getErrorStream())).start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            process.destroy();
        }
    }


    /**
     * 将HTML字符串转换为HTML文件
     *
     * @param htmlStr HTML字符串
     * @return HTML文件的绝对路径
     */
    public static String strToHtmlFile(String htmlStr,String path) {
        OutputStream outputStream = null;
        try {
            String fileName = UUID.randomUUID().toString();
            String htmlFilePath = path+ fileName + ".html";// UUID.randomUUID().toString() +
            outputStream = new FileOutputStream(htmlFilePath);
            outputStream.write(htmlStr.getBytes("UTF-8"));
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                    outputStream = null;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 获得HTML转PDF的命令语句,注意命令调用路径与安装路径保持一致
     *（一些命令参数可以自行去做修改，或者使用）
     * @param htmlFilePath HTML文件路径
     * @param pdfFilePath  PDF文件路径
     * @return HTML转PDF的命令语句
     */
    private static String getCommand(String htmlFilePath, String pdfFilePath) {
        String osName = System.getProperty("os.name");
        StringBuilder cmd = new StringBuilder();
        cmd.append("D:/wkhtmltopdf/bin/wkhtmltopdf.exe ");
        cmd.append(" ");
//        cmd.append(" --header-line");//页眉下面的线
        //cmd.append(" --header-center 这里是页眉这里是页眉这里是页眉这里是页眉 ");//页眉中间内容
        cmd.append(" --margin-top 1.8cm ");//设置页面上边距 (default 10mm)
        cmd.append(" --margin-right 1.5cm ");//设置页面下边距 (default 10mm)
        cmd.append(" --margin-bottom 1.8cm ");//设置页面下边距 (default 10mm)
        cmd.append(" --margin-left 1.5cm ");//设置页面下边距 (default 10mm)
        cmd.append(" --page-size Letter ");//纸张大小A4, Letter, etc.
//        cmd.append(" --header-html file:///"+WebUtil.getServletContext().getRealPath("")+FileUtil.convertSystemFilePath("\\style\\pdf\\head.html"));// (添加一个HTML页眉,后面是网址)
//        cmd.append(" --header-spacing 6 ");// (设置页眉和内容的距离,默认0)
        //cmd.append(" --footer-center (设置在中心位置的页脚内容)");//设置在中心位置的页脚内容
//        cmd.append(" --footer-html file:///"+WebUtil.getServletContext().getRealPath("")+FileUtil.convertSystemFilePath("\\style\\pdf\\foter.html"));// (添加一个HTML页脚,后面是网址)
//        cmd.append(" --footer-line");//* 显示一条线在页脚内容上)
//        cmd.append(" --footer-spacing 6 ");// (设置页脚和内容的距离)
        cmd.append(" %s %s");
        // Windows
        if (osName.startsWith("Windows")) {//C:/Program Files/
//            return String.format("C:/Program Files/wkhtmltopdf/bin/wkhtmltopdf.exe %s %s", htmlFilePath, pdfFilePath);
//            return String.format("wkhtmltopdf/bin/wkhtmltopdf.exe %s %s", htmlFilePath, pdfFilePath);
            return String.format(cmd.toString(), htmlFilePath, pdfFilePath);
        }
        // Linux
        else {
            return String.format("/opt/wkhtmltopdf/bin/wkhtmltopdf %s %s", htmlFilePath, pdfFilePath);
        }
    }

}
