package com.sz.fts.utils;


import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 耿怀志
 * @version [1.0, 2017/3/21]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class FileUtil {


    private static Logger log = LogManager.getLogger(FileUtil.class);

    /**
     * 读取json文件的内容
     *
     * @param filePath
     *            文件路径(带文件名称)
     * @return
     *              文件内容
     */
    public static JSONObject getJson(String filePath){
        File file = new File(filePath);
        StringBuffer buffer = new StringBuffer();
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String read = null;
            while ((read = bufferedReader.readLine()) != null) {
                buffer.append(read);
            }
        } catch (Exception e) {

        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        JSONObject indexJson = JSONObject.fromObject(buffer.toString());
        return indexJson;
    }

    /**
     * 读取excel，只支持读取一个sheet
     *
     * @param file
     *            上传文件流
     * @param ignoreRows
     *            设置读取初始行，0表示从第一行开始读取，1表示从第二行开始读取
     * @param endRow
     *            设置读取末行，0表示读取到最后一行，1表示读取到倒数第二行
     * @return
     */
    public static List<String[]> getData(File file, int ignoreRows, int endRow) throws IOException {
        List<String[]> result = new ArrayList<String[]>();
        int rowSize = 0;
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(file);
            Cell cell = null;
            // 获取sheet1表格
            Sheet st = wb.getSheetAt(0);
            for (int rowIndex = ignoreRows; rowIndex <= st.getLastRowNum() - endRow; rowIndex++) {
                Row row = st.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                int tempRowSize = row.getLastCellNum() + 1;

                if (tempRowSize > rowSize) {
                    rowSize = tempRowSize;
                }
                String[] values = new String[rowSize];
                Arrays.fill(values, "");
                // 读取每一列的值
                for (short columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
                    String value = "";
                    cell = row.getCell(columnIndex);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:
                                value = cell.getStringCellValue();
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                value = new DecimalFormat("0.########").format(cell.getNumericCellValue());
                                break;
                            default:
                                value = "";
                        }
                    }
                    values[columnIndex] = rightTrim(value);
                }
                result.add(values);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 去掉字符串右边的空格
     *
     * @param str
     *            要处理的字符串
     * @return 处理后的字符串
     */
    private static String rightTrim(String str) {
        if (str == null) {
            return "";
        }
        int length = str.length();
        for (int i = length - 1; i >= 0; i--) {
            if (str.charAt(i) != 0x20) {
                break;
            }
            length--;
        }
        return str.substring(0, length);
    }

    /**
     * 读取文件为一个内存字符串,保持文件原有的换行格式
     *
     * @param file        文件对象
     * @param charset 文件字符集编码
     * @return 文件内容的字符串
     */
    public static String fileToString(File file, String charset) {
        StringBuffer sb = new StringBuffer();
        try {
            LineNumberReader reader = new LineNumberReader(new BufferedReader(new InputStreamReader(new FileInputStream(file), charset)));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.getProperty("line.separator"));
            }
        } catch (UnsupportedEncodingException e) {
            log.error("读取文件为一个内存字符串失败，失败原因是使用了不支持的字符编码" + charset, e);
        } catch (FileNotFoundException e) {
            log.error("读取文件为一个内存字符串失败，失败原因所给的文件" + file + "不存在！", e);
        } catch (IOException e) {
            log.error("读取文件为一个内存字符串失败，失败原因是读取文件异常！", e);
        }
        return sb.toString();
    }

    /**
     * 将字符串存储为一个文件，当文件不存在时候，自动创建该文件，当文件已存在时候，重写文件的内容，特定情况下，还与操作系统的权限有关。
     *
     * @param text         字符串
     * @param distFile 存储的目标文件
     * @return 当存储正确无误时返回true，否则返回false
     */
    public static boolean stringToFile(String text, File distFile) {
        if (!distFile.getParentFile().exists()) distFile.getParentFile().mkdirs();
        BufferedReader br = null;
        BufferedWriter bw = null;
        boolean flag = true;
        try {
            br = new BufferedReader(new StringReader(text));
            bw = new BufferedWriter(new FileWriter(distFile));
            char buf[] = new char[1024 * 64];         //字符缓冲区
            int len;
            while ((len = br.read(buf)) != -1) {
                bw.write(buf, 0, len);
            }
            bw.flush();
            br.close();
            bw.close();
        } catch (IOException e) {
            flag = false;
            log.error("将字符串写入文件发生异常！", e);
        }
        return flag;
    }

    /**
     * 生成excel并下载
     */
    public static void exportExcel(HttpServletResponse response,File file,String fileName) throws IOException {
        //下载
        InputStream fis = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        response.reset();
        response.setContentType("text/html;charset=UTF-8");
        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/x-msdownload");
        String newName = URLEncoder.encode(fileName+DateUtils.getCurrentTime14()+".xls", "UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename=\""+ newName + "\"");
        response.addHeader("Content-Length", "" + file.length());
        toClient.write(buffer);
        toClient.flush();
        //删除创建的新文件
        if (file.exists()) {
            file.delete();
        }
    }

    /*BASE64Encoder和BASE64Decoder这两个方法是sun公司的内部方法，并没有在java api中公开过，所以使用这些方法是不安全的，
        * 将来随时可能会从中去除，所以相应的应该使用替代的对象及方法，建议使用apache公司的API*/
    static BASE64Encoder encoder = new sun.misc.BASE64Encoder();
    static BASE64Decoder decoder = new sun.misc.BASE64Decoder();

    /**
     *  将PDF转换成base64编码
     *  1.使用BufferedInputStream和FileInputStream从File指定的文件中读取内容；
     *  2.然后建立写入到ByteArrayOutputStream底层输出流对象的缓冲输出流BufferedOutputStream
     *  3.底层输出流转换成字节数组，然后由BASE64Encoder的对象对流进行编码
     * */
    static String getPDFBinary(File file) {
        FileInputStream fin =null;
        BufferedInputStream bin =null;
        ByteArrayOutputStream baos = null;
        BufferedOutputStream bout =null;
        try {
            //建立读取文件的文件输出流
            fin = new FileInputStream(file);
            //在文件输出流上安装节点流（更大效率读取）
            bin = new BufferedInputStream(fin);
            // 创建一个新的 byte 数组输出流，它具有指定大小的缓冲区容量
            baos = new ByteArrayOutputStream();
            //创建一个新的缓冲输出流，以将数据写入指定的底层输出流
            bout = new BufferedOutputStream(baos);
            byte[] buffer = new byte[1024];
            int len = bin.read(buffer);
            while(len != -1){
                bout.write(buffer, 0, len);
                len = bin.read(buffer);
            }
            //刷新此输出流并强制写出所有缓冲的输出字节，必须这行代码，否则有可能有问题
            bout.flush();
            byte[] bytes = baos.toByteArray();
            //sun公司的API
            return encoder.encodeBuffer(bytes).trim();
            //apache公司的API
            //return Base64.encodeBase64String(bytes);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fin.close();
                bin.close();
                //关闭 ByteArrayOutputStream 无效。此类中的方法在关闭此流后仍可被调用，而不会产生任何 IOException
                //baos.close();
                bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将base64编码转换成PDF
     * @param base64sString
     * 1.使用BASE64Decoder对编码的字符串解码成字节数组
     *  2.使用底层输入流ByteArrayInputStream对象从字节数组中获取数据；
     *  3.建立从底层输入流中读取数据的BufferedInputStream缓冲输出流对象；
     *  4.使用BufferedOutputStream和FileOutputSteam输出数据到指定的文件中
     */
    public static void base64StringToPDF(String base64sString,String name){
        BufferedInputStream bin = null;
        FileOutputStream fout = null;
        BufferedOutputStream bout = null;
        try {
            //将base64编码的字符串解码成字节数组
            byte[] bytes = decoder.decodeBuffer(base64sString);
            //apache公司的API
            //byte[] bytes = Base64.decodeBase64(base64sString);
            //创建一个将bytes作为其缓冲区的ByteArrayInputStream对象
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            //创建从底层输入流中读取数据的缓冲输入流对象
            bin = new BufferedInputStream(bais);
            //指定输出的文件
            File file = new File("/home/szfts/tomcat/apache-tomcat-8.0.50/webapps/szfts/resources/file/wxmbImg/"+ name+".pdf");
            if(!file.exists()){
                try {
                    //创建文件
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //创建到指定文件的输出流
            fout  = new FileOutputStream(file);
            //为文件输出流对接缓冲输出流对象
            bout = new BufferedOutputStream(fout);

            byte[] buffers = new byte[1024];
            int len = bin.read(buffers);
            while(len != -1){
                bout.write(buffers, 0, len);
                len = bin.read(buffers);
            }
            //刷新此输出流并强制写出所有缓冲的输出字节，必须这行代码，否则有可能有问题
            bout.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                bin.close();
                fout.close();
                bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
