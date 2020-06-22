/**
 * Copyright (C), 2019-2019, 成都房联云码科技有限公司
 * FileName: ClearBufferThread
 * Author:   Arron-wql
 * Date:     2019/12/4 12:19
 * Description: 清理输入流缓存的线程
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.pig4cloud.pigx.common.core.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 清理输入流缓存的线程
 *
 * @author qinglong.wu@funi365.com
 * @create 2019/12/4
 * @Version 1.0.0
 */


public class ClearBufferThread implements Runnable {
    private InputStream inputStream;
    private String type;

    public ClearBufferThread(String type, InputStream inputStream){
        this.inputStream = inputStream;
        this.type = type;
    }

    public void run() {
        BufferedReader br = null;
        String line = null;
        try{
            br = new BufferedReader(new InputStreamReader(inputStream));
            while((line = br.readLine()) != null) {
                System.out.println(type+"===>"+line);
            };
        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
