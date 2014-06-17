<%--Servlet:用于AJAX分页获得通知列表里获得通知--%>
<%@ page import="java.util.List" %>
<%@ page import="notice.MyNoticeEntity" %>
<%@ page import="notice.ManageNotice" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
	// 将请求、响应的编码均设置为UTF-8（防止中文乱码）
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
	try {
		int begin = Integer.parseInt(request.getParameter("begin"));
		String want = request.getParameter("want");
		List<MyNoticeEntity> notices;
		if (want == null) {//
			notices = ManageNotice.get_fromSite_page(begin, request.getParameter("from"));
		} else {//如果有want说明是要搜索
			notices = ManageNotice.search_page(begin, want);
		}
		for (MyNoticeEntity one : notices) {
%>

<div class="column">
	<div class="ui stacked segment" onclick="openOneAJAX(<%=one.getId()%>)">
		<%--是否是今天的--%>
		<%
			if (one.isTaday()) {
		%>
		<div class="ui label corner inverted red left icon"><i class="icon">N</i></div>
		<%
			}
		%>

		<!--标题-->
		<div class="ui header">
			<div class="content"><%=one.toString()%>
			</div>
		</div>

		<div class="ui relaxed horizontal divided mini list">

			<div class="item">
				<i class="time icon blank"></i>

				<div class="content">
					<a class="header"><%=one.getDate()%>
					</a>
					<!--时间-->
					<div class="description">时间
					</div>
				</div>
			</div>
		</div>

	</div>
</div>

<%
		}
	} catch (Exception e) {
		return;
	}
%>