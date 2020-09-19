<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style type="text/css">
		.divbox{
			position:absolute;  
			left:50%;  
			top:30%;   
			margin-left:-250px; 
			margin-top:-150px;  
			}
</style>
</head>
<body>
	<div class="divbox">
		<img  src="./resources/images/error.png">
	</div>
</body>
</html>