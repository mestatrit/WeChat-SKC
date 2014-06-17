package tool;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with Intellij IDEA.
 * User: WuHaoLin
 * Date: 2/24/14
 * Time: 7:58 PM
 */
public class Tool {
	private static Calendar calendar = Calendar.getInstance();
	private static int Week_NOW=calendar.get(Calendar.DAY_OF_WEEK);
	public static final SimpleDateFormat DateFormat_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
	private static String nowDate = DateFormat_YYYY_MM_DD.format(new Date(System.currentTimeMillis()));
	public static final SimpleDateFormat DateFormat_YYYY_MM_DD_HHMM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static String nowDate_HHMM = DateFormat_YYYY_MM_DD_HHMM.format(new Date(System.currentTimeMillis()));

	static {
		calendar.setTime(new Date(System.currentTimeMillis()));
		Week_NOW=calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 更新当前系统时间
	 * TODO
	 */
	public static void update() {
		System.out.println("更新系统时间");
		nowDate = DateFormat_YYYY_MM_DD.format(new Date(System.currentTimeMillis()));
		nowDate_HHMM = DateFormat_YYYY_MM_DD_HHMM.format(new Date(System.currentTimeMillis()));

		//今天是星期几
		calendar.setTime(new Date(System.currentTimeMillis()));
		Week_NOW=calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获得今天的时间格式为 yyyy-mm-dd
	 *
	 * @return
	 */
	public static String time_YYYY_MM_DD() {
		return nowDate;
	}

	/**
	 * 获得今天的时间格式为 yyyy-mm-dd HH:mm 每小时更新一次
	 *
	 * @return
	 */
	public static String time_YYYY_MM_DD_HH_MM() {
		return nowDate_HHMM;
	}

	/**
	 * 获得系统现在时间 获取准确的现在系统的时间 yyyy-MM-dd HH:mm
	 *
	 * @return
	 */
	public static String time_YYYY_MM_DD_HH_MM_NOW() {
		return DateFormat_YYYY_MM_DD_HHMM.format(new Date(System.currentTimeMillis()));
	}


	/**
	 * 获得今天是星期几
	 * @return
	 */
	public static int week_NOW(){
		return Week_NOW;
	}

	/**
	 * 用于AJAX加载js函数然后依次执行一条条函数
	 * 向jsp页面输出js函数,只需要传入函数体名称和(参数,参数)不要加;号
	 *
	 */
	public static void jspWriteJSForAJAX(HttpServletResponse response, String... args) {
		try {
			Writer writer = response.getWriter();
//			writer.write("<script>\n");
			for (String oneF : args) {
				writer.write("" + oneF + ";\n");
			}
//			writer.write("</script>");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用于直接向html加载js函数然后依次执行一条条函数
	 * 向jsp页面输出js函数,只需要传入函数体名称和(参数,参数)不要加;号
	 *
	 */
	public static void jspWriteJSForHTML(HttpServletResponse response, String... args) {
		StringBuilder re = new StringBuilder(
				"<!DOCTYPE html>\n" +
						"<html>\n" +
						"<head>\n" +
						"\t<meta charset=\"utf-8\"/>\n" +
						"\t<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\"/>\n" +
						"\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\">\n" +
						"\t<link rel=\"stylesheet\" type=\"text/css\" href=\"/lib/css/semantic.min.css\">\n" +
						"\t<link rel=\"stylesheet\" type=\"text/css\" href=\"/lib/css/main.css\">\n" +
						"\t<script src=\"/lib/js/jquery-1.10.2.min.js\"></script>\n" +
						"\t<script src=\"/lib/js/semantic.min.js\"></script>\n" +
						"\t<script src=\"/lib/js/main.js\"></script>\n" +
						"</head>\n" +
						"<body>\n" +
						"<script>\n"
		);
		for (String oneF : args) {
			re.append(oneF + ";\n");
		}
		re.append("</script>\n" +
				"</body>\n" +
				"</html>");
		try {
			Writer writer = response.getWriter();
			writer.write(re.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * **生成AJAX加载更多JS 不包含<scritp></scritp>标签
	 *
	 * @param ChangeCount **分页查询时每次取出多少个
	 * @param targetURL   **目标URL
	 * @param datas       **传入的参数   如果没有就传入 "" **如果有先加一个,然后dataName:dateValue,dataName:dateValue
	 * @return
	 */
	public static String makeAJAXLoadMoreJS(int ChangeCount, String targetURL, String datas) {
		StringBuilder re = new StringBuilder(
				"function ajaxMore(btn) {\n" +
						"$(btn).addClass('loading');\n" +
						"var begin = Number($(btn).attr('begin'));\n" +
						"begin += " + ChangeCount + ";\n" +
						"$.ajax({\n" +
						"type: 'POST',\n"+
						"url: '" + targetURL + "',\n" +
						"data: { begin: begin" + datas + "},\n" +
						"contentType: \"application/x-www-form-urlencoded; charset=utf-8\"\n" +
						"}).done(function (data) {\n" +
						"$(btn).removeClass('loading');\n" +
						"if (data.length < 20) {\n" +
						"$(btn).text(\"没有更多了!\");\n" +
						"$(btn).addClass('disabled');\n" +
						"} else {\n" +
						"$(btn).before(data);\n" +
						"$(btn).text(\"更多\");\n" +
						"$(btn).attr('begin', begin);\n" +
						"}\n" +
						"});\n" +
						"}"
		);
		return re.toString();
	}

	/**
	 * **生成AJAX加载更多JS 不包含<scritp></scritp>标签
	 *
	 * @param ChangeCount **分页查询时每次取出多少个
	 * @param targetURL   **目标URL
	 * @param datas       **传入的参数   如果没有就传入 "" **如果有先加一个,然后dataName:dateValue,dataName:dateValue
	 * @param javastript 如果这次执行加载更多信息成功后执行会  javastript语句
	 * @return
	 */
	public static String makeAJAXLoadMoreJS_appendJS(int ChangeCount, String targetURL, String datas,String javastript) {
		StringBuilder re = new StringBuilder(
				"function ajaxMore(btn) {\n" +
						"$(btn).addClass('loading');\n" +
						"var begin = Number($(btn).attr('begin'));\n" +
						"begin += " + ChangeCount + ";\n" +
						"$.ajax({\n" +
						"type: 'POST',\n"+
						"url: '" + targetURL + "',\n" +
						"data: { begin: begin" + datas + "},\n" +
						"contentType: \"application/x-www-form-urlencoded; charset=utf-8\"\n" +
						"}).done(function (data) {\n" +
						"$(btn).removeClass('loading');\n" +
						"if (data.length < 20) {\n" +
						"$(btn).text(\"没有更多了!\");\n" +
						"$(btn).addClass('disabled');\n" +
						"} else {\n" +
						"$(btn).before(data);\n" +
						"$(btn).text(\"更多\");\n" +
						"$(btn).attr('begin', begin);\n" +
						javastript+"\n"+
						"}\n" +
						"});\n" +
						"}"
		);
		return re.toString();
	}

	public static void main(String[] args) {
//		setXHMMtoSQL("2012210817", "930820");
		System.out.println(time_YYYY_MM_DD_HH_MM());
	}

}
