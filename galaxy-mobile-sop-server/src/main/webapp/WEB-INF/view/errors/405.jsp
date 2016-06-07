<%@ page language="java" contentType="text/html;charset=utf-8"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN""http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<base href="<%=basePath%>">
<title>HTTP Status 405</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="description"
	content="405 Method Not Allowed,请求方法对指定的资源不适用">
<jsp:include page="../common/taglib.jsp" />
</head>
<body>
	<div class="padding_top50 lineHeight200">
		<div class="box1" panelWidth="450" position="center">
			<div class="msg_icon2"></div>
			<div
				class="padding_left50 padding_right15 padding_top20 minHeight_100 font_14 font_bold">
				405 Method Not Allowed<br> 请求方法“${pageContext.request.method }”对指定的资源不适用。
			</div>
		</div>
	</div>
</body>
</html>
