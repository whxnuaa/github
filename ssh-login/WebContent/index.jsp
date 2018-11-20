<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">

		<title>My JSP 'index.jsp' starting page</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	</head>

	<script language="JavaScript">
function checkit(){
    var theForm;
    theForm=document.userform;


 if(theForm.username.value==""){
    alert("用户名不能为空!");
    theForm.username.focus();
    return false;
    }

    if(theForm.pwd.value==""){
    alert("密码不能为空!");
    theForm.pwd.focus();
    return false;
    }
 
    theForm.submit();
}
</script>

	<body style="text-align: center;">

		<form name="userform" method="post"
			action="<%=request.getContextPath()%>/actions/login">
			POST方式
			<div>
				用户名&nbsp;&nbsp;
				<input type="text" name="user.name" />
			</div>
			<div style="margin-top: 10px;">
				&nbsp;&nbsp;密码&nbsp;&nbsp;
				<input type="password" name="user.password" />
			</div>
			<div style="margin-top: 10px;">
				<input type="submit" value="登陆" onclick="checkit()" />
			</div>

		</form>



		<form name="userform" method="get"
			action="<%=request.getContextPath()%>/actions/login">
			GET方式
			<div>
				用户名&nbsp;&nbsp;
				<input type="text" name="user.name" />
			</div>
			<div style="margin-top: 10px;">
				&nbsp;&nbsp;密码&nbsp;&nbsp;
				<input type="password" name="user.password" />
			</div>
			<div style="margin-top: 10px;">
				<input type="submit" value="登陆" onclick="checkit()" />
			</div>

		</form>
	</body>
</html>
