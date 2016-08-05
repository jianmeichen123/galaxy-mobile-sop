<%@ page language="java" pageEncoding="UTF-8"%>
<% 
	String path = request.getContextPath(); 
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>繁星</title>
	<link href="<%=path %>/css/axure.css" type="text/css" rel="stylesheet"/>
	<!--[if lt IE 9]><link href="css/lfie8.css" type="text/css" rel="stylesheet"/><![endif]-->
	<!-- jsp文件头和头部 -->
	<%@ include file="/WEB-INF/view/common/taglib.jsp"%>
	<link rel="stylesheet" href="<%=path %>/bootstrap/bootstrap-table/bootstrap-table.css"  type="text/css">

	
	<script src="<%=path%>/js/bootstrap-v3.3.6.js"></script>
	<script src="<%=path%>/bootstrap/bootstrap-table/bootstrap-table-xhhl.js"></script>
	<script src="<%=path%>/bootstrap/bootstrap-table/locale/bootstrap-table-zh-CN.js"></script>
    <script src="<%=path %>/js/init.js"></script>

</head>

<body>

<jsp:include page="../common/header.jsp" flush="true"></jsp:include>
<div class="pagebox clearfix">
	<jsp:include page="../common/menu.jsp" flush="true"></jsp:include>
    <!--右中部内容-->
 	<div class="ritmin">
    	<h2>意见反馈</h2>
        <!--表格内容-->
		<table width="100%" cellspacing="0" cellpadding="0" 
						 id="data-table" data-url="<%=request.getContextPath() %>/galaxy/feedback/queryFeedbackList"  data-page-list="[10, 20, 30]" data-show-refresh="true" 
				         data-toolbar="#custom-toolbar" >
			<thead>
		    <tr>
		        <th data-field="username" data-align="center" class="data-input">反馈人</th>
		        <th data-field="content" data-align="center" data-formatter="content" class="data-input">反馈内容</th>
		        <th data-field="createdDate" data-align="center" data-formatter="dateformatter" class="col-md-1 status ">提交日期</th>
			 </tr>
			</thead>
		</table>
	

    </div>
</div>
<style>
.content_length{ width:500px; overflow:hidden; height:28px; line-height:28px;}
</style>
<jsp:include page="../common/footer.jsp" flush="true"></jsp:include>
</body>
<script>
$(function(){
	createMenus(8);
});
function dateformatter(value,row){
	var result = formatDate(value) 
	return result;
}
function content(value){
	var result ="<div class='content_length'><a style='text-decoration:none' href='javascript:void(0)' title='"+value+"'>"+value+"</a></div>";
	return result;
}

function formatDate(date, format) {   
    if (!date) date = new Date();   
    if (!format) format = "yyyy-MM-dd";   
    switch(typeof date) {   
        case "string":   
            date = new Date(date.replace(/-/, "/"));   
            break;   
        case "number":   
            date = new Date(date);   
            break;   
    }    
    if (!date instanceof Date) return;   
    var dict = {   
        "yyyy": date.getFullYear(),   
        "M": date.getMonth() + 1,   
        "d": date.getDate(),   
        "H": date.getHours(),   
        "m": date.getMinutes(),   
        "s": date.getSeconds(),   
        "MM": ("" + (date.getMonth() + 101)).substr(1),   
        "dd": ("" + (date.getDate() + 100)).substr(1),   
        "HH": ("" + (date.getHours() + 100)).substr(1),   
        "mm": ("" + (date.getMinutes() + 100)).substr(1),   
        "ss": ("" + (date.getSeconds() + 100)).substr(1)   
    };       
    return format.replace(/(yyyy|MM?|dd?|HH?|ss?|mm?)/g, function() {   
        return dict[arguments[0]];   
    });                   
}
</script>
</html>

