package com.utils.lingfe.files;

import com.alibaba.druid.util.StringUtils;
import com.exception.CrmebException;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @program: crmeb
 * @description: 文件相关-工具类
 * @author: 零风
 * @create: 2021-08-03 17:04
 **/
public class FilesUtils {

    public static void main(String[] args) {
        String test="ABCD.png.jpg";
        System.out.println(test.substring(0,test.indexOf(".")));
    }

    @Test
    public void test() throws IOException {
        String filePath = "C:\\Users\\Administrator\\Desktop\\Aqykj\\产品\\date\\2021-08-20\\-3\\2021-08-20.zip";
        String saveRootDirectory="D://test/product/20210820-3/";
        List<String> listFiles=FilesUtils.zipFileRead(filePath,saveRootDirectory);

        String filePath2 = "D://www";
        String saveRootDirectory2="D://www.zip";
        FilesUtils.zip(filePath2,saveRootDirectory2);
    }

    /**
     * 文件验证(MultipartFile)
     * @param multipartFile 文件
     * @param formatFile    指定文件格式(后缀)
     * @Author 零风
     * @Date  2022/3/4
     * @return 文件名称
     */
    public static String verificationFileMultipartFile(MultipartFile multipartFile, String formatFile) {
        //验证非空
        if(multipartFile == null){
            throw new CrmebException("文件为空！");
        }

        //得到文件名称
        String fileName = multipartFile.getOriginalFilename();

        // 上传文件为空
        if (com.alibaba.druid.util.StringUtils.isEmpty(fileName)) {
            throw new CrmebException("文件名称为空！");
        }

        //上传文件大小为1000条数据
        if (multipartFile.getSize() > 1024 * 1024 * 10) {
            //logger.error("upload | 上传失败: 文件大小超过10M，文件大小为：{}", file.getSize());
            throw new CrmebException("上传失败: 文件大小不能超过10M!");
        }

        //验证-是否为文件
        if(fileName.lastIndexOf(".") == -1){
            throw new CrmebException("没有后缀,不是文件格式！");
        }

        // 上传文件名格式不正确
        if (!formatFile.equals(fileName.substring(fileName.lastIndexOf(".")))) {
            throw new CrmebException("文件格式不正确, 请使用后缀名为"+formatFile+"的文件!");
        }
        return fileName;
    }

    /**
     * 根据路径删除文件
     * @param path 路径
     * @Author 零风
     * @Date  2022/3/4
     * @return 是否成功
     */
    public static Boolean delFilePath(String path){
        File file=new File(path);
        if(!file.isFile()){
            return  false;
        }
        return file.delete();
    }

    /**
     * 删除文件(包括子文件)
     * -递归算法删除
     * @param file 文件
     * @Author 零风
     * @Date  2022/3/4
     * @return 删除结果
     */
    public static Boolean delFile(File file){
        //验证非空
        if(!file.isFile()){
            return false;
        }

        //验证是否存在目录
        if(file.isDirectory()){
            File[] arr= file.listFiles();
            for (File f: arr) {
                FilesUtils.delFile(f);
            }
        }

        //执行删除
        return file.delete();
    }

    /**
     * 将文件另存为指定路径
     * @param fileName  文件名称
     * @param read      文件流
     * @param saveRootDirectory  另存为路径
     * @Author 零风
     * @Date  2022/3/4
     */
    public static void unZipFile(String fileName, InputStream read, String saveRootDirectory) throws IOException {
        // 如果只读取图片，自行判断就OK.
        saveRootDirectory=new StringBuffer(saveRootDirectory).append("/").append(fileName).toString();

        // 指定要解压出来的文件格式（这些格式可抽取放置在集合或String数组通过参数传递进来，方法更通用）
        File file = new File(saveRootDirectory);
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

            try {
                // 创建文件
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 写入文件
        BufferedOutputStream write = new BufferedOutputStream(new FileOutputStream(file));
        int cha = 0;
        while ((cha = read.read()) != -1) {
            write.write(cha);
        }

        // 要注意IO流关闭的先后顺序
        write.flush();
        write.close();
        read.close();
    }

    /**
     * 读取Zip信息
     * -获得zip中所有的目录文件信息
     * -可以另存为
     * @param pathZip             压缩包文件路径
     * @param saveRootDirectory   另存为路径
     * @Author 零风
     * @Date  2022/3/4
     * @return 文件名称list
     */
    public static List<String> zipFileRead(String pathZip, String saveRootDirectory) {
        List<String> listFiles=new ArrayList<>();
        try {
            // 获得zip信息
            ZipFile zipFile = new ZipFile(pathZip, Charset.forName("GBK"));
            System.out.println(zipFile.getName());

            //转换
            Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile.entries();
            while (enu.hasMoreElements()) {
                //转换-文件流
                ZipEntry zipElement = (ZipEntry) enu.nextElement();
                InputStream read = zipFile.getInputStream(zipElement);

                //文件名称-并验证是否为文件
                String fileName = zipElement.getName();
                if (fileName != null && fileName.indexOf(".") != -1) {
                    //是，执行另存为
                    FilesUtils.unZipFile(fileName, read, saveRootDirectory);
                    listFiles.add(fileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listFiles;
    }

    /**
     * 压缩文件目录
     * @param source 源文件目录（单个文件和多层目录路径）
     * @param destit 目标目录(压缩后存放的路径)
     * @Author 零风
     * @Date  2022/3/4
     */
    public static void zip(String source,String destit) {
        File file = new File(source);
        ZipOutputStream zipOutputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(destit);
            zipOutputStream = new ZipOutputStream(fileOutputStream);
            if (file.isDirectory()) {
                FilesUtils.directory(zipOutputStream, file, "" );
            } else {
                FilesUtils.zipFile(zipOutputStream, file, "" );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                zipOutputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 压缩目录文件另存为
     * @param zipOutputStream   压缩目录文件流
     * @param file              文件
     * @param parentFileName    父文件名
     * @Author 零风
     * @Date  2022/3/4
     */
    public static void zipFile(ZipOutputStream zipOutputStream, File file, String parentFileName){
        FileInputStream in = null;
        try {
            ZipEntry zipEntry = new ZipEntry(parentFileName);
            zipOutputStream.putNextEntry( zipEntry );
            in = new FileInputStream( file);
            int len;
            byte [] buf = new byte[8*1024];
            while ((len = in.read(buf)) != -1){
                zipOutputStream.write(buf, 0, len);
            }
            zipOutputStream.closeEntry(  );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 递归压缩目录结构
     * @param zipOutputStream 文件流
     * @param file            文件
     * @param parentFileName  父文件名
     * @Author 零风
     * @Date  2022/3/4
     */
    public static void directory(ZipOutputStream zipOutputStream, File file, String parentFileName){
        File[] files = file.listFiles();
        String parentFileNameTemp = null;
        for (File fileTemp: files) {
            parentFileNameTemp =  StringUtils.isEmpty(parentFileName)?fileTemp.getName():parentFileName+"/"+fileTemp.getName();
            if(fileTemp.isDirectory()){
                FilesUtils.directory(zipOutputStream,fileTemp, parentFileNameTemp);
            }else{
                FilesUtils.zipFile(zipOutputStream,fileTemp,parentFileNameTemp);
            }
        }
    }

}
