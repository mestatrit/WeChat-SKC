package skc.notice;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import skc.tool.AutoUpdate;
import skc.tool.HibernateUtil;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with Intellij IDEA.
 * User: WuHaoLin
 * Date: 2/22/14
 * Time: 5:32 PM
 */
public class ManageNotice {
	/**
	 * 后台管理系统命令
	 */
	public static final String CMD_Add = "add";
	public static final String CMD_Delete = "delete";
	public static final String CMD_Change = "change";

	/**
	 * 登入后台管理系统的密码
	 */
	public static final String ManagePassword = "NOTICE";

	/**
	 * 分页查询每次出现的个数
	 */
	public static final int ChangeCount = 5;

	public static final int IsOK_YES = 0;//用于标准人工筛选后的信息删除,该信息ok
	public static final int IsOK_NO = 1;//用于标准人工筛选后的信息删除,该信息不符合要求不能被显示

	/**
	 * 信息来源 该类文章的名称,该类文章的中的文章列表,该类位置在数据库中的标识符
	 */
	public static final String[][] FromSiteName={

			{"社科讯息-重要通知","http://kyb.ccnu.edu.cn/skc/channels/153.html","SK-ZYTZ"},
			{"社科讯息-社科要闻","http://kyb.ccnu.edu.cn/skc/channels/154.html","SK-SKYW"},
			{"社科讯息-学术动态","http://kyb.ccnu.edu.cn/skc/channels/155.html","SK-XSDT"},
			{"社科讯息-学术活动预告","http://kyb.ccnu.edu.cn/skc/channels/156.html","SK-XSHDYG"},
			{"社科讯息-华大人文学术沙龙","http://kyb.ccnu.edu.cn/skc/channels/157.html","SK-HDRWXSSN"},

			{"科研项目-主要纵向项目概览","http://kyb.ccnu.edu.cn/skc/channels/165.html","KYXM-ZYZX"},
			{"科研项目-项目指南","http://kyb.ccnu.edu.cn/skc/channels/166.html","KYXM-XMZN"},

			{"科研成果-报告类","http://kyb.ccnu.edu.cn/skc/channels/168.html","KYCG-BGL"},
			{"科研成果-著作类","http://kyb.ccnu.edu.cn/skc/channels/169.html","KYCG-ZZL"},
			{"科研成果-论文类","http://kyb.ccnu.edu.cn/skc/channels/170.html","KYCG-LWL"},
			{"科研成果-获奖成果","http://kyb.ccnu.edu.cn/skc/channels/171.html","KYCG-HJCG"},
			{"科研成果-学者名家","http://kyb.ccnu.edu.cn/skc/channels/172.html","KYCG-XZMJ"},

			{"规章制度-项目管理文件","http://kyb.ccnu.edu.cn/skc/channels/174.html","GZZD-XM"},
			{"规章制度-成果管理文件","http://kyb.ccnu.edu.cn/skc/channels/175.html","GZZD-CG"},
			{"规章制度-基地管理文件","http://kyb.ccnu.edu.cn/skc/channels/176.html","GZZD-JD"},
			{"规章制度-期刊管理文件","http://kyb.ccnu.edu.cn/skc/channels/177.html","GZZD-QK"},
			{"规章制度-经费管理文件","http://kyb.ccnu.edu.cn/skc/channels/178.html","GZZD-JF"},
			{"规章制度-学会管理文件","http://kyb.ccnu.edu.cn/skc/channels/179.html","GZZD-XH"},
			{"规章制度-其他管理文件","http://kyb.ccnu.edu.cn/skc/channels/180.html","GZZD-QT"},

			{"常用速查","http://kyb.ccnu.edu.cn/skc/channels/181.html","CYSC"},

	};


	/**
	 * 用来判断该标题的文章是一个通知的筛选器,如果包含了该关键字就认为是文明想要的,这个系统没有用到
	 */
	public static final String FilterString[] = {
			"通知",
			"安排",
			"说明",
			"启事",
	};

	/**
	 * 加载类时自动更新最新通知
	 * 每隔2小时自动扫描
	 */
	static {
		update();
		AutoUpdate.start();
	}

	/**
	 * 判断管理员密码是否正确
	 *
	 * @param password 登入者输入的密码
	 * @return 密码是否正确
	 */
	public static boolean managePasswordIsOK(String password) {
		return password.equals(ManagePassword);
	}

	/**
	 * 判断数据库中是否含有该标题的文章而且时间相同
	 *
	 * @param title 一篇文章的标题
	 * @return 如果有返回true
	 */
	public static boolean DBhasThisOne(String title, String date) {
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("from MyNoticeEntity as notice where notice.title=? and date=?");
		query.setString(0, title);
		query.setString(1, date);
		int re = query.list().size();
		HibernateUtil.closeSession(session);
		if (re >= 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 如果数据库中没有该标题的文章就加入数据库中
	 *
	 * @param noticeEntity
	 */
	public static void add_NotSame(List<MyNoticeEntity> noticeEntity) {
		for (MyNoticeEntity entity : noticeEntity) {
			if (!DBhasThisOne(entity.getTitle(), entity.getDate())) {
				HibernateUtil.addEntity(entity);
			}
		}
	}

	/**
	 * 如果数据库中没有该标题的文章就加入数据库中
	 *
	 * @param noticeEntity
	 */
	public static void add_NotSame(MyNoticeEntity noticeEntity) {
		HibernateUtil.addEntity(noticeEntity);
	}

	/**
	 * 修改该通知,在数据库中
	 *
	 * @param noticeEntity
	 */
	public static void changeNotice(MyNoticeEntity noticeEntity) {
		HibernateUtil.updateEntity(noticeEntity);
	}

	/**
	 * 把这个设置为不是我们想要的通知,但还在数据库里
	 *
	 * @param id
	 */
	public static void remove(int id) {
		MyNoticeEntity removeOne = get(id);
		removeOne.setIsOk(IsOK_NO);
		changeNotice(removeOne);
	}

	/**
	 * 用文章的id去数据库中获得一篇文章
	 *
	 * @param id
	 * @return 如果数据库中不存在该文章就返回null
	 */
	public static MyNoticeEntity get(int id) {
		Session session = HibernateUtil.getSession();
		try {
			MyNoticeEntity re = (MyNoticeEntity) session.createQuery("from MyNoticeEntity as notice where notice.id=?").setInteger(0, id).uniqueResult();
			HibernateUtil.closeSession(session);
			return re;
		} catch (Exception e) {
			HibernateUtil.closeSession(session);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 用于分页查询加fromSite条件查询
	 *
	 * @param from 从这个开始
	 * @return
	 */
	public static List<MyNoticeEntity> get_fromSite_page(int from,String fromSite) {
		if (fromSite==null || fromSite.equals("null")){
			return get_page(from);
		}
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("from MyNoticeEntity as notice where isOk=0 and fromSite=? order by notice.date desc ,id desc");
		query.setString(0,fromSite);
		query.setFirstResult(from);
		query.setMaxResults(ChangeCount);
		List<MyNoticeEntity> re = query.list();
		HibernateUtil.closeSession(session);
		return re;
	}

	/**
	 * 用于分页查询加关键字搜索
	 *
	 * @param from 从这个开始
	 * @return
	 */
	public static List<MyNoticeEntity> search_page(int from,String want) {
		System.out.println("form="+from+" want="+want);
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("from MyNoticeEntity as notice where isOk=0 and content like ? or title like ? order by notice.date desc ,id desc");
		query.setString(0,"%"+want+"%");
		query.setString(1,"%"+want+"%");
		query.setFirstResult(from);
		query.setMaxResults(ChangeCount);
		List<MyNoticeEntity> re = query.list();
		HibernateUtil.closeSession(session);
		return re;
	}

	/**
	 * 用于分页查询
	 *
	 * @param from 从这个开始
	 * @return
	 */
	public static List<MyNoticeEntity> get_page(int from) {
		Session session = HibernateUtil.getSession();
		Query query = session.createQuery("from MyNoticeEntity as notice where isOk=0 order by notice.date desc ,id desc");
		query.setFirstResult(from);
		query.setMaxResults(ChangeCount);
		List<MyNoticeEntity> re = query.list();
		HibernateUtil.closeSession(session);
		return re;
	}

	/**
	 * 对从html里提起到的文章的原链接进行检查处理,如果他包含http即是完整的url就原样返回.如果不是完整的url即不包含http://就求得原url然后返回
	 *
	 * @param BasicUrl 该文章来源网站的根URL
	 */
	private static String checkURL(Element link, String BasicUrl) {
		String orgUrl = link.attr("href");//原地址
		if (!orgUrl.contains("://")) {//处理不合法的源地址使其变为合法的
			orgUrl = BasicUrl + link.attr("href");
		}
		return orgUrl;
	}

	/**
	 * 对html进行semanticUI修饰
	 * 把所有的图片全都加上semanticUI响应式图片类
	 * 把所有的表格全都加上semanticUI响应式表格
	 *
	 * @param document
	 * @return
	 */
	public static Document semanticUI(Document document) {
		//关键字去除,去除html文档中所有&nbsp,<br>,</br>
		String tempStr= document.html().replaceAll(";&nbsp|<br>|</br>","");
		document=Jsoup.parse(tempStr);

		//Semantic-UI修饰
		document.getAllElements()
                //去除原来的表格修饰
				.removeAttr("style")
				.removeAttr("width")
				.removeAttr("height")
				.removeAttr("align")
				.removeAttr("border");
		document.getElementsByTag("table")//把所有的表格全都加上Semantic-UI响应式表格
				.addClass("ui")
				.addClass("table")
				.addClass("segment");

		Elements imgs=document.getElementsByTag("img");
		for (Element one:imgs){
			one.addClass("ui").addClass("image");//把所有的图片全都加上Bootstrap响应式图片类
			//图片的URL处理
			String url=one.attr("src");
			if (!url.contains("://")){
				url="http://kyb.ccnu.edu.cn/"+url;
			}
			one.attr("src",url);
		}

        //对html中的所有链接属性分析,如果链接是用的相对链接就加上原网址,如果是觉得链接就不变
        Elements links=document.getElementsByAttribute("href");
        for(Element one:links){
            String url=one.attr("href");
            if (!url.contains("://")){
                url="http://kyb.ccnu.edu.cn/"+url;
            }
            one.attr("href",url);
        }
		return document;
	}

	/**
	 * 判断该文章是否符合是重要通知的要求
	 * 看该文章的标题是否含有筛选关键字FilterString[],如果有就认为该文章符合要求
	 *
	 * @return
	 */
	public static boolean isNotice(String title) {
		return true;
//		//判断该文章是否符合是重要通知的要求
//		for (String filter : FilterString) {//看该文章的标题是否含有筛选关键字,如果有就认为该文章符合要求
//			if (title.contains(filter)) {
//				return true;
//			}
//		}
//		return false;
	}

	/**
	 * 获得该链接的标题
	 *
	 * @param link
	 * @return
	 */
	public static String getLinkTitle(Element link) {
		//获得文章的标题
		String title = link.attr("title");
		if (title == null || title.trim().length() < 2) {//优先取该链接的title属性作为标题,如果没有该属性就取他的文本属性
			title = link.text();
		}
		return title;
	}

	/**
	 * 全部扫描一次,并把结果反应到数据库中
	 * //TODO 添加到自动更新
	 */
	public static void update() {
		System.out.println("开始扫描");
		for (int i=0;i<FromSiteName.length;i++){
			add_NotSame(scan(i));//ok
		}
		System.out.println("扫描结束");
	}

	////////////////////////////////////////////////////////////

	/**
	 * 来自 http://kyb.ccnu.edu.cn/skc/
	 * 社科处
	 *
	 * @return 如果获取失败就返回数量为0的List
	 */
	public static List<MyNoticeEntity> scan(int index) {
		String form = FromSiteName[index][2];//在数据库中的标识符
		LinkedList<MyNoticeEntity> re = new LinkedList<MyNoticeEntity>();
		try {
			Document document = Jsoup.connect(FromSiteName[index][1]).get();
			Elements links = document.getElementsByClass("uc_lanmu_content").first().getElementsByTag("ul").first().getElementsByTag("a");
			for (int i = 0; i < links.size(); i++) {
				Element link = links.get(i);
				String title = getLinkTitle(link);
				if (isNotice(title)) {
					try {
						String orgUrl = checkURL(link, "http://kyb.ccnu.edu.cn/");
						document = Jsoup.connect(orgUrl).get();
						document = semanticUI(document);

						String dateStr ="";
						try {
							dateStr= link.parent().nextElementSibling().text();
						}catch (Exception e){
						}

						String content = document.getElementById("artibody").html();

						re.add(new MyNoticeEntity(title, dateStr, orgUrl, content, form));
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println(form + "ERROR!");
						continue;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return re;
	}

	public static void main(String[] args) {
		update();
	}

}
