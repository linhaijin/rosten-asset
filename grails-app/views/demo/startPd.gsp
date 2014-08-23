<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>盘点</title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta name="layout" content="rosten" />


<script type="text/javascript">
require(["dojo/parser",
	 		"dojo/_base/kernel",
	 		"dijit/registry",
	 		"dojo/_base/xhr",
	 		"rosten/widget/ShowDialog",
	 		
	     	"rosten/widget/ActionBar",
	     	"rosten/app/Application",
	     	"rosten/kernel/behavior"],
		function(parser,kernel,registry,xhr,ShowDialog){
			kernel.addOnLoad(function(){
				rosten.init({webpath:"${request.getContextPath()}"});
				rosten.cssinit();
			});


			pddr = function(){
				rosten.variable.dialog = new ShowDialog({src:rosten.webPath + "/demo/importPd"});
			};
			zdpd = function(){
				rosten.variable.dialog = new ShowDialog({src:rosten.webPath + "/demo/zdpd"});
			};
			zdpd_ok = function(){
				window.opener.endPd();
				window.close();
			};
		
		page_quit = function(){
			window.close();
		};
	
	});
	
</script>
</head>
<body>
	<div class="rosten_action">
		<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" 
			data-dojo-props='actionBarSrc:"${createLink(controller:'demo',action:'startPdAction')}"'></div>
	</div>
	
	<div style="margin:0 auto;text-align:center;margin-top:10px">
		<img src="${resource(dir:'images/rosten/demo',file:'startPd.jpg')}">
	</div>
	
</body>
</html>
