<%--搜索功能的结果展示页--%>
<%@ page import="notice.ManageNotice" %>
<%@ page import="tool.Tool" %>
<%--通知列表--%>
<%--
  Created by Intellij IDEA.
  User: WuHaoLin
  Date: 2/21/14
  Time: 3:01 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8"/>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
	<link rel="stylesheet" type="text/css" href="../lib/css/semantic.min.css">
	<link rel="stylesheet" type="text/css" href="../lib/css/main.css">
	<script src="../lib/js/jquery-1.10.2.min.js"></script>
	<script src="../lib/js/semantic.min.js"></script>
	<script src="../lib/js/main.js"></script>
	<title>华大社科</title>
</head>
<body>
<div class="ui stackable three column page grid">
	<%
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String want = request.getParameter("want");
	%>
	<%--默认拿出前changeCount通知--%>
	<jsp:include page="GetAJAXServlet.jsp">
		<jsp:param name="begin" value="0"/>
		<jsp:param name="want" value="<%=want%>"/>
	</jsp:include>
	<%--ajax 加载更多--%>
	<button class="ui button fluid circular" onclick="ajaxMore(this);" begin="0" style="margin-top: 10px">更多
	</button>
	<div class="ui divider horizontal icon inverted"><i class="icon smile"></i></div>
</div>

<jsp:include page="searchBox.jsp"/>

<script>
	showHighLight('<%=want%>');
	closeWeiXinBtn();
	<%=Tool.makeAJAXLoadMoreJS_appendJS(ManageNotice.ChangeCount,"GetAJAXServlet.jsp",",want:'"+want+"'","showHighLight('"+want+"');")%>

	function openOneAJAX(id) {
		$.get('GetOneAJAX.jsp?id=' + id, function (data) {
			var newOne = $(data);
			$('body').append(newOne);
			$(newOne).modal('toggle');
			showHighLight('<%=want%>');
		});
	}

</script>

</body>
</html>