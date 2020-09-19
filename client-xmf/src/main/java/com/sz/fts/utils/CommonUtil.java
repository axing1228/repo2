package com.sz.fts.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

/**
 * @author 杨坚
 * @version [版本号, 2016年2月19日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class CommonUtil {
	private static Des des = new Des();
	private static PropertiesUtil proper = null;
	private static Logger logger = LogManager.getLogger(CommonUtil.class);

	static {
		try {
			proper = new PropertiesUtil();
		} catch (IOException var1) {
			var1.printStackTrace();
		}

	}

	public CommonUtil() {
	}

	public static <T> void commonRequest(HttpServletRequest request, HttpServletResponse response, String reqfield, List<T> lists, int count, String pageNum) {
		request.setAttribute("totalCount", count);
		request.setAttribute(reqfield, lists);
		request.setAttribute("targetType", "navTab");
		request.setAttribute("numPerPage", "20");
		request.setAttribute("pageNum", pageNum);
	}

	public static void printPlatformMsg(boolean flag, HttpServletResponse response, JSONObject outcome) {
		response.setContentType("text/html;charset=UTF-8");

		try {
			if (flag) {
				logger.info("<<返回平台参数>>===========>" + outcome);
			}

			PrintWriter pw = response.getWriter();
			pw.write(outcome.toString());
			pw.flush();
			pw.close();
		} catch (IOException var5) {
			logger.error("response wirter fail!", var5);
		}

	}

	public static void printPlatformMsg(HttpServletResponse response, String msg) {
		response.setContentType("text/html;charset=UTF-8");

		try {
			PrintWriter pw = response.getWriter();
			pw.write(msg);
			pw.flush();
			pw.close();
		} catch (IOException var4) {
			logger.error("response wirter fail!", var4);
		}

	}

	public static void printMsgArray(HttpServletResponse response, JSONArray msg) {
		response.setContentType("text/html;charset=UTF-8");

		try {
			PrintWriter pw = response.getWriter();
			pw.write(msg.toString());
			pw.flush();
			pw.close();
		} catch (IOException var4) {
			logger.error("response wirter fail!", var4);
		}

	}

	public static int reckonPage(int count) {
		int page = 10;
		// 总页数
		int totalPage = 0;
		if (count % page > 0) {
			totalPage = count / page + 1;
		} else {
			totalPage = count / page;
		}
		return totalPage;
	}

	public static JSONObject printPlatformPropertiesList(List<String> propertiesKey) throws Exception {
		JSONObject params = new JSONObject();
		Iterator var3 = propertiesKey.iterator();

		while (var3.hasNext()) {
			String key = (String) var3.next();
			params.put(key, proper.readValue(key));
		}

		return params;
	}

	public static JSONObject printPlatformPropertiesList(String[] arrays) throws Exception {
		JSONObject params = new JSONObject();
		String[] var5 = arrays;
		int var4 = arrays.length;

		for (int var3 = 0; var3 < var4; ++var3) {
			String key = var5[var3];
			params.put(key, proper.readValue(key));
		}

		return params;
	}

	public static String printPlatformProperties(String propertiesKey) {
		try {
			return proper.readValue(propertiesKey);
		} catch (IOException var2) {
			var2.printStackTrace();
			return null;
		}
	}

	public static JSONObject printPlatform(Integer errCode) throws Exception {
		JSONObject result = new JSONObject();
		result.put("result", errCode);
		result.put("msg", proper.readValue(String.valueOf(errCode)));
		return result;
	}

	public static String encrypt(String value) {
		return des.strEnc(value, "_p$_", "$TR%_", "acceptp");
	}

	public static String decrypt(String value) {
		return des.strDec(value, "_p$_", "$TR%_", "acceptp");
	}

	public static JSONObject analysisRequestMsg(HttpServletRequest request) {
		if (StringUtils.isNotEmpty(request.getParameter("params"))) {
			JSONObject params = JSONObject.fromObject(request.getParameter("params"));
			int currentPage = 1;
			int pageSize = 10;
			if (params.containsKey("currentPage")) {
				currentPage = params.getInt("currentPage");
				if (params.containsKey("pageSize")) {
					pageSize = params.getInt("pageSize");
					params.put("pageSize", pageSize);
				}

				params.put("currentPage", currentPage);
				params.put("bigenPage", (currentPage - 1) * pageSize);
				params.put("endPage", 10);
			}

			String method = "";
			if (StringUtils.isNotEmpty(request.getParameter("method"))) {
				method = request.getParameter("method");
			}

			logger.info("<<请求平台路径>>===========>" + request.getRequestURI() + "?" + method);
			logger.info("<<请求平台参数>>===========>" + params);
			return params;
		} else {
			return null;
		}
	}

	public static int checkPages(HttpServletRequest request) {
		int pageNum = 1;
		int numPerPage = 10;
		if (request.getParameter("numPerPage") != null && !"".equals(request.getParameter("numPerPage"))) {
			numPerPage = Integer.parseInt(request.getParameter("numPerPage"));
		}

		if (request.getParameter("pageNum") != null && !"".equals(request.getParameter("pageNum"))) {
			pageNum = Integer.parseInt(request.getParameter("pageNum"));
		}

		return (pageNum - 1) * numPerPage;
	}

	public static Integer checkPage(String curr) {
		return StringUtils.isNotEmpty(curr) ? (Integer.parseInt(curr) - 1) * 10 : null;
	}

	public static void printEncryptMsg(HttpServletRequest request, HttpServletResponse response, JSONObject msg) {
		try {
			response.setHeader("Access-Control-Allow-Origin", "*");
			String en_jpCallbackId = request.getParameter("_jpCallbackId");
			PrintWriter pw = response.getWriter();
			msg.put("TSR_RESULT", "0");
			logger.info("-返回-"+msg);
			pw.write("_jsonpCallback('" + msg.toString() + "','" + en_jpCallbackId + "')");
			pw.flush();
			pw.close();
		} catch (IOException e) {
			logger.error("response wirter fail!", e);
		}
	}

	public static void printEncryptMsg(boolean flag, HttpServletRequest request, HttpServletResponse response, JSONObject outcome) {
		try {
			if (flag) {
				logger.info("<<返回平台参数>>===========>" + outcome);
			}

			String en_jpCallbackId = request.getParameter("_jpCallbackId");
			PrintWriter pw = response.getWriter();
			outcome.put("TSR_RESULT", "0");
			pw.write("_jsonpCallback('" + outcome.toString() + "','" + en_jpCallbackId + "')");
			pw.flush();
			pw.close();
		} catch (IOException var6) {
			logger.error("response wirter fail!", var6);
		}

	}

	public static void printOutMsg(HttpServletRequest request, HttpServletResponse response, JSONObject msg) {
		try {
			response.setHeader("Access-Control-Allow-Origin", "*");
			PrintWriter pw = response.getWriter();
			pw.write(msg.toString());
			logger.info("-返回-"+msg);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			logger.error("response wirter fail!", e);
		}
	}

	public static JSONObject outDecryptMsg(HttpServletRequest request) {
		if ((request.getParameter("para") != null) && (!"".equals(request.getParameter("para")))) {
			Des des = new Des();
			String deparam = des.strDec(request.getParameter("para").toString(), "_p$_", "$e%_", "hx");
			logger.info(deparam);
			return JSONObject.fromObject(deparam);
		}
		return null;
	}

	public static String outDecryptMenu(String menuStr) {
		Des des = new Des();
		String deparam = des.strDec(menuStr, "_M$_", "0^5_", "hx");
		return deparam;
	}

	public static void main(String[] args) {
		Des des = new Des();
		String base = "E29212814796F3496D208F9032E5C6028F8B739EB591B643A1D117BF127A2789C3FC4035E719E89FA8B0B04246B7722902890EA3ADC4652444C77EC2E5390F4961734621EF10B243BE538AA89E6098C5&_jpCallbackId=_jsonpCallback_9540&_t=1578031566380";
		String deparam = des.strDec(base, "_p$_", "$e%_", "hx");

		System.out.println(deparam);
	}
	public static String createRandom(boolean numberFlag, int length) {
		String retStr = "";
		String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
		int len = strTable.length();
		boolean bDone = true;
		do {
			retStr = "";
			int count = 0;
			for (int i = 0; i < length; i++) {
				double dblR = Math.random() * len;
				int intR = (int) Math.floor(dblR);
				char c = strTable.charAt(intR);
				if (('0' <= c) && (c <= '9')) {
					count++;
				}
				retStr += strTable.charAt(intR);
			}
			if (count >= 2) {
				bDone = false;
			}
		} while (bDone);

		return retStr;
	}
}
