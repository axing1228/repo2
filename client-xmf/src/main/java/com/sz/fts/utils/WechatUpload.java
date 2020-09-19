package com.sz.fts.utils;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 上传多媒体文件
 * 
 * @author 杨坚
 * @version [版本号, 2016年5月17日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class WechatUpload extends BaseRquestAndResponse {

	public static InputStream getInputStream(String URLPATH) {
		InputStream inputStream = null;
		HttpURLConnection httpURLConnection = null;
		try {
			URL url = new URL(URLPATH);
			if (url != null) {
				httpURLConnection = (HttpURLConnection) url.openConnection();// 打开链接
				// 设置连接网络的超时时间
				httpURLConnection.setConnectTimeout(3000);
				httpURLConnection.setDoInput(true);
				//
				httpURLConnection.setRequestMethod("GET");
				int resposeCode = httpURLConnection.getResponseCode();
				if (resposeCode == 200)// 如果请求成功
				{
					// 从服务器中获得一个输入流
					inputStream = httpURLConnection.getInputStream();
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return inputStream;
	}

	/**
	 * 
	 * 下载网络图片
	 * 
	 * @param srcPath
	 *            源网址
	 * @param format
	 *            图片格式
	 * @param accounts
	 *            帐号
	 * @return 图片路径
	 * @author 杨坚
	 * @Time 2016年11月29日
	 * @version 1.0v
	 */
	public static String saveImageToDiskImage(String srcPath, String accounts, String format) {
		// tomcat路径
		String tomcatPath = getSession().getServletContext().getRealPath("")
				.replaceAll(getSession().getServletContext().getContextPath().substring(1), "");
		// 图片绝对路径
		String uploadPath = "upload" + File.separator + DateUtils.getCurrentDate() + File.separator + "image"
				+ File.separator + accounts + File.separator;
		// 文件名称
		String fileName = DateUtils.getCurrentTime17().concat(".").concat(format == null ? "jpg" : format);
		InputStream inputStream = getInputStream(srcPath);
		byte[] data = new byte[1024];
		int len = 0;
		FileOutputStream fileOutputStream = null;
		try {
			File file = new File(tomcatPath + uploadPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			fileOutputStream = new FileOutputStream(
					tomcatPath.substring(0, tomcatPath.length() - 1) + uploadPath + fileName);
			while ((len = inputStream.read(data)) != -1) {
				fileOutputStream.write(data, 0, len);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return uploadPath.concat(fileName).replaceAll("\\\\", "/");
	}

	/**
	 * 
	 * 字节流转文件
	 * 
	 * @param inputStream
	 *            源
	 * @param decPath
	 *            图片格式
	 * @author 杨坚
	 * @Time 2016年11月29日
	 * @version 1.0v
	 */
	public static String convertFile(InputStream inputStream, String decPath, String fileName) {
		byte[] data = new byte[1024];
		int len = 0;
		FileOutputStream fileOutputStream = null;
		try {
			File file = new File(decPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			fileOutputStream = new FileOutputStream(decPath +File.separator+ fileName);
			while ((len = inputStream.read(data)) != -1) {
				fileOutputStream.write(data, 0, len);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return decPath.concat(fileName).replaceAll("\\\\", "/");
	}

	/**
	 * 下载网络资源
	 * 
	 * @param srcPath
	 *            源网址
	 * @param decPath
	 *            下载本地
	 */
	public static boolean saveImageToDisk(String srcPath, String decPath) {
		InputStream inputStream = getInputStream(srcPath);
		byte[] data = new byte[1024];
		int len = 0;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(decPath);
			while ((len = inputStream.read(data)) != -1) {
				fileOutputStream.write(data, 0, len);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public static boolean saveImageToDisk(String path, byte[] data, String imageName) throws IOException {
		File photoPathFile = new File(path);
		// 写入到文件
		FileOutputStream outputStream = new FileOutputStream(new File(photoPathFile, imageName));
		outputStream.write(data);
		outputStream.flush();
		outputStream.close();
		//
		return true;
	}

	/**
	 * 合成图片
	 * 
	 * @param first
	 *            第一张图片
	 * @param second
	 *            第二张图片
	 * @param third
	 *            生成图片
	 * @param x
	 *            平行位置
	 * @param y
	 *            垂直位置
	 * @param width
	 *            第二张图片宽度
	 * @param height
	 *            高度
	 * @return
	 */
	@SuppressWarnings("restriction")
	public static boolean GenerateTwoImg(String first, String second, String third, int x, int y, int width,
			int height) {
		try {
			InputStream imagein = new FileInputStream(first);
			InputStream imagein2 = new FileInputStream(second);

			BufferedImage image = ImageIO.read(imagein);
			BufferedImage image2 = ImageIO.read(imagein2);
			System.out.println(image2.getWidth());
			Graphics g = image.getGraphics();
			g.drawImage(image2, x, y, width, height, null);
			OutputStream outImage = new FileOutputStream(third);
			JPEGImageEncoder enc = JPEGCodec.createJPEGEncoder(outImage);
			enc.encode(image);
			imagein.close();
			imagein2.close();
			outImage.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static byte[] decode(String imageData) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] data = decoder.decodeBuffer(imageData);
		for (int i = 0; i < data.length; ++i) {
			if (data[i] < 0) {
				// 调整异常数据
				data[i] += 256;
			}
		}
		//
		return data;
	}

	/**
	 * 本地上传图片工具
	 * 
	 * @param imageData
	 *            base64字符
	 * @param serverRealPath
	 *            tomcat路径
	 * @param uploadPath
	 *            资源文件夹及路径
	 * @param host
	 *            域名
	 * @return
	 * @author 杨坚
	 * @Time 2016年11月18日
	 * @version 1.0v
	 * @throws IOException
	 */
	public static JSONObject uploadLocalPicture(String imageData, String serverRealPath, String uploadPath, String host)
			throws IOException {
		String str[] = { imageData };
		JSONObject obj = new JSONObject();
		obj.put("result", "1");
		obj.put("msg", "失败");
		if (StringUtils.isNull(str)) {
			// 去除空格
			byte[] imgData = decode(str[0].substring(str[0].indexOf("base64,") + 7, str[0].length()));
			// 时间戳
			SimpleDateFormat time = new SimpleDateFormat("yyyyMMddhhmmssSSS");
			String timestamp = time.format(new Date()).concat("." + str[0].substring(11, str[0].indexOf(";base64")));
			SimpleDateFormat simple = new SimpleDateFormat("yyyyMMdd");
			// 判断文件路径
			File filePath = new File(serverRealPath + uploadPath);
			if (!filePath.exists() && !filePath.isDirectory()) {
				filePath.mkdirs();
			}
			// true为成功
			boolean flag = saveImageToDisk(filePath + File.separator, imgData, timestamp);
			if (flag) {
				// 获取本地链接
				String uploadUrl = host + uploadPath + File.separator + timestamp;
				obj.put("path", uploadUrl.replaceAll("\\\\", "/"));
				obj.put("result", "0");
				obj.put("msg", "成功");
				return obj;
			}
		}
		return null;
	}

	public boolean fileUpload() {

		return false;
	}

	public static void main(String[] args) {
		File file = new File("f:/logo.jpg");
		System.out.println(file.length());
	}

}
