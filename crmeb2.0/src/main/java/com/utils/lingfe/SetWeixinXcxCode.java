package com.utils.lingfe;

import com.alibaba.fastjson.JSONObject;
import com.exception.CrmebException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 生成微信小程序二维码类
 * @author: 零风
 * @CreateDate: 2022/1/7 10:46
 */
public class SetWeixinXcxCode {

    public static void main(String[] args) throws Exception {
        //获取接口调用凭证access_token
        String appId = "wx0e3a54572ef72ba1";//小程序id
        String appKey = "bba1784c781bad6ae261fe42940896b6";//小程序密钥
        String token = postToken(appId, appKey);

        //生成二维码
        generateQrCode("D:\\lingfe\\Code\\60.png", "pages/index/index", "pid=60", token);

    }

    /**
     * 获取-生成微信小程序二维码接口凭证
     * @param appId     小程序appid
     * @param appKey    小程序key
     * @return  结果
     * @throws Exception
     */
    public static String postToken(String appId, String appKey) {
        try {

            String requestUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appKey;

            // 打开和URL之间的连接
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // 设置通用的请求属性
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // 得到请求的输出流对象
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes("");
            out.flush();
            out.close();

            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in;
            if (requestUrl.contains("nlp")) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            }

            // 接收结果处理
            StringBuilder result = new StringBuilder();
            String getLine;
            while ((getLine = in.readLine()) != null) {
                result.append(getLine);
            }
            in.close();

            //处理结果
            JSONObject jsonObject = JSONObject.parseObject(result.toString());
            return jsonObject.getString("access_token");
        }catch (Exception e){
            throw new CrmebException(e.getMessage());
        }
    }

    /**
     * 生成微信小程序二维码
     * @param filePath 本地生成二维码路径
     * @param page 当前小程序相对页面 必须是已经发布的小程序存在的页面（否则报错），
     *             例如 pages/index/index,
     *             根路径前不要填加 /,不能携带参数（参数请放在scene字段里），
     *             如果不填写这个字段，默认跳主页面
     * @param scene 最大32个可见字符，只支持数字，大小写英文以及部分特殊字符：!#$&'()*+,/:;=?@-._~，
     *              其它字符请自行编码为合法字符（因不支持%，中文无法使用 urlencode 处理，请使用其他编码方式）
     * @param accessToken 接口调用凭证
     */
    public static void generateQrCode(String filePath, String page, String scene, String accessToken) {
        try {
            // 指定要解压出来的文件格式（这些格式可抽取放置在集合或String数组通过参数传递进来，方法更通用）
            File file = new File(filePath);
            if (!file.exists()) {
                // 文件目录不存在，就创建目录
                File rootDirectoryFile = new File(file.getParent());
                if (!rootDirectoryFile.exists()) {
                    boolean ifSuccess = rootDirectoryFile.mkdirs();
                    if (ifSuccess) {
                        System.out.println("文件夹创建成功!");
                    } else {
                        System.out.println("文件创建失败!");
                    }
                }
            }

            //调用微信接口生成二维码 https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/qr-code/wxacode.getUnlimited.html#HTTPS%20%E8%B0%83%E7%94%A8
            URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            // conn.setConnectTimeout(10000);//连接超时 单位毫秒
            // conn.setReadTimeout(2000);//读取超时 单位毫秒

            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            JSONObject paramJson = new JSONObject();
            //这就是你二维码里携带的参数 String型  名称不可变
            paramJson.put("scene", scene);
            //注意该接口传入的是page而不是path
            paramJson.put("page", page);
            paramJson.put("check_path",Boolean.TRUE);
            //这是设置扫描二维码后跳转的页面
            paramJson.put("width", 430);
            paramJson.put("is_hyaline", true);
            paramJson.put("auto_color", true);
            printWriter.write(paramJson.toString());
            // flush输出流的缓冲
            printWriter.flush();

            //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            OutputStream os = new FileOutputStream(new File(filePath));
            int len;
            byte[] arr = new byte[1024];
            while ((len = bis.read(arr)) != -1) {
                os.write(arr, 0, len);
                os.flush();
            }
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("打开地址查看生成的二维码：" + filePath);
    }

}
