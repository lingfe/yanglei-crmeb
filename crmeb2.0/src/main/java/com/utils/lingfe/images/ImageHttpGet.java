package com.utils.lingfe.images;

import com.exception.CrmebException;
import com.utils.UploadUtil;
import com.zbkj.crmeb.upload.vo.FileResultVo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @program: crmeb
 * @description: 网络获取图片工具类
 * @author: 零风
 * @create: 2021-06-22 11:18
 **/
public class ImageHttpGet {

    /**
     * 通过url读取图片信息，返回文件流
     * @return 文件流
     */
    public static InputStream getInputStreamByUrl(String url) {
        InputStream ins = null;
        try {
            URL url_ = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url_.openConnection();
            conn.setConnectTimeout(3 * 1000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            ins = conn.getInputStream();
        } catch (IOException e) {
            System.out.println("图片读取失败!-->" + e.getMessage());
        }
        return ins;
    }

    /**
     * 获取网络图片,返回文件信息
     * @param imgUrl        图片url
     * @param folder        自定义文件夹
     * @param storagePath   存储路径
     * @param imgPrefix     图片前缀
     * @return  文件信息
     */
    public static FileResultVo getImages(String imgUrl,String folder,String storagePath,String imgPrefix)  {
        // 拼装返回的数据
        FileResultVo result = new FileResultVo();

        //验证是否为网络链接
        if(!imgUrl.contains("http")){
            System.out.println(imgUrl);
            throw new CrmebException(imgUrl+"==>不是网络链接");
        }

        try{
            URL url = new URL(imgUrl);
            // 打开连接
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36)");
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            //验证验证是否为图片文件，https://my.oschina.net/u/3994156/blog/2994129
            String contentType = conn.getContentType();
            boolean image = contentType.startsWith("image");
            if (!image) {
                System.out.println(contentType);
                throw new CrmebException(imgUrl+"==>不是是图片文件");
            }

            // 输入流
            InputStream is = conn.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;

            String ext = "jpg";

            //验证文件夹
            if(folder==null||"".equals(folder))folder="default";

            //得到文件名称
            String fileName = UploadUtil.fileName(ext);
            System.out.println(fileName);

            //文件路径
            StringBuffer path=new StringBuffer(storagePath).append("/").append(folder).append("/");
            // 如果该目录不存在，就创建此抽象路径名指定的目录。
            File file1 = new File(path.toString());
            if (!file1.exists()) {
                // file1.mkdir();
                file1.mkdirs();
            }

            //拼接访问url
            if(imgPrefix==null ||"".equals(imgPrefix))imgPrefix=storagePath;
            String httpUrl=new StringBuffer(imgPrefix).append("/").append(folder).append("/").append(fileName).toString();

            // 输出的文件流
            OutputStream os = new FileOutputStream(path.append(fileName).toString());

            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }

            // 完毕，关闭所有链接
            os.close();
            is.close();

            //赋值图片参数
            result.setFileSize(conn.getDate());
            result.setFileName(fileName);
            result.setExtName(ext);
            result.setServerPath(storagePath);
            result.setUrl(httpUrl);
            result.setPath(path.toString());
            result.setType(conn.getContentType());
            result.setPrefix(imgPrefix);

            //返回
            return result;
        }catch (Exception e){
            throw new CrmebException(imgUrl+"==>"+e.getMessage());
        }
    }

    public static void main(String[] args)  {
        String imgurl="https://bing.ioliu.cn/v1/rand";
        FileResultVo vo=ImageHttpGet.getImages(imgurl,null,"D:\\lingfe\\other\\imgs",null);
        System.out.println(vo);
    }

}
