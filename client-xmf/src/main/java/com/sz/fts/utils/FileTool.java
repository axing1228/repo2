package com.sz.fts.utils;

import com.jcraft.jsch.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Properties;
import java.util.Vector;

/**
 * SFTP上传文件工具类 请在资源文件global.properties中配置sftp服务器信息（目前只支持一台资源服务器上传）
 * 
 * @see:
 * @Company:江苏鸿信系统集成有限公司微信开发组
 * @author 杨坚
 * @Time 2016年10月3日
 * @version 1.0v
 */
public class FileTool {
	/**
	 * 
	 * 连接sftp服务器
	 * 
	 * @param host
	 *            主机
	 * @param port
	 *            端口
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 * @author 杨坚
	 * @Time 2017年1月17日
	 * @version 1.0v
	 */
	public static ChannelSftp createConnect(String host, int port, String username, String password) {
		ChannelSftp sftp = null;
		try {
			JSch jsch = new JSch();
			jsch.getSession(username, host, port);
			Session sshSession = jsch.getSession(username, host, port);
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sftp;
	}

	public static void main(String[] args) {
		System.out.println(createConnect("180.97.30.67", 22, "qdwechat", "jshx@!20161015#qd$wechat67"));
	}

	/**
	 * 
	 * 上传文件
	 * 
	 * @param directory
	 *            上传的目录
	 * @param uploadFile
	 *            要上传的文件 
	 * @author 杨坚
	 * @Time 2017年1月17日
	 * @version 1.0v
	 */
	public static void upload(String directory, String uploadFile) {
		directory = directory.replaceAll("\\\\", "/");
		ChannelSftp sftp = new ChannelSftp();
		try {
			// 初始连接
			sftp = createConnect("58.211.5.56",3922,
					"hxload", "hxload");
			if (!isDirExist(sftp, directory)) {
				createDir(directory, sftp);
			}
			sftp.cd(directory);
			File file = new File(uploadFile);
			sftp.put(new FileInputStream(file), file.getName());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				disconnect(sftp);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建一个文件目录
	 */
	public static void createDir(String createpath, ChannelSftp sftp) {
		try {
			if (isDirExist(sftp, createpath)) {
				sftp.cd(createpath);
			}
			String pathArry[] = createpath.split("/");
			StringBuffer filePath = new StringBuffer("/");
			for (String path : pathArry) {
				if (path.equals("")) {
					continue;
				}
				filePath.append(path + "/");
				if (isDirExist(sftp, filePath.toString())) {
					sftp.cd(filePath.toString());
				} else {
					// 建立目录
					sftp.mkdir(filePath.toString());
					// 进入并设置为当前目录
					sftp.cd(filePath.toString());
				}
			}
			sftp.cd(createpath);
		} catch (SftpException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断目录是否存在
	 */
	public static boolean isDirExist(ChannelSftp sftp, String directory) {
		boolean isDirExistFlag = false;
		try {
			SftpATTRS sftpATTRS = sftp.lstat(directory);
			isDirExistFlag = true;
			return sftpATTRS.isDir();
		} catch (Exception e) {
			if (e.getMessage().toLowerCase().equals("no such file")) {
				isDirExistFlag = false;
			}
		}
		return isDirExistFlag;
	}

	/**
	 * 
	 * 下载文件
	 * 
	 * @param directory
	 *            下载目录
	 * @param downloadFile
	 *            下载的文件
	 * @param saveFile
	 *            存在本地的路径
	 * @param sftp
	 *            渠道对象
	 * @author 杨坚
	 * @Time 2017年1月17日
	 * @version 1.0v
	 */
	public static void download(String directory, String downloadFile, String saveFile, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			File file = new File(saveFile);
			sftp.get(downloadFile, new FileOutputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				disconnect(sftp);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * 删除文件
	 * 
	 * @param directory
	 *            要删除文件所在目录
	 * @param deleteFile
	 *            要删除的文件
	 * @param sftp
	 *            渠道对象
	 * @author 杨坚
	 * @Time 2017年1月17日
	 * @version 1.0v
	 */
	public static void delete(String directory, String deleteFile, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			sftp.rm(deleteFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 列出目录下的文件
	 * 
	 * @param directory
	 *            要列出的目录
	 * @param sftp
	 *            渠道对象
	 * @return
	 * @throws SftpException
	 * @author 杨坚
	 * @Time 2017年1月17日
	 * @version 1.0v
	 */
	@SuppressWarnings("rawtypes")
	public static Vector listFiles(String directory, ChannelSftp sftp) throws SftpException {
		return sftp.ls(directory);
	}

	/**
	 * 关闭连接
	 * 
	 * @param sftp
	 * @author 杨坚
	 * @Time 2017年1月17日
	 * @version 1.0v
	 */
	public static void disconnect(ChannelSftp sftp) {
		try {
			sftp.disconnect();
			sftp.getSession().disconnect();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 文件写入操作
	 * 
	 * @param fileName
	 *            文件名称
	 * @param filePath
	 *            文件路径
	 * @param data
	 *            文件数据
	 * @author 杨坚
	 * @Time 2017年1月18日
	 * @version 1.0v
	 */
	public static boolean writeFile(String fileName, String filePath, HttpServletRequest request, String data) {
		// 获取tomcat路径
		String tomcatPath = request.getSession().getServletContext().getRealPath("")
				.replaceAll(request.getSession().getServletContext().getContextPath().substring(1), "");
		boolean flag = false;
		// 文件名称
		fileName = StringUtils.isNotEmpty(fileName) ? fileName : DateUtils.getCurrentTimeTrade_no() + "_menu.txt";
		// 文件路径
		filePath = StringUtils.isNotEmpty(filePath) ? filePath : tomcatPath.concat("/menu");
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			fw = new FileWriter(filePath + File.separator + fileName, true);
			bw = new BufferedWriter(fw);
			bw.write(data);
			flag = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					bw.close();
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

}