<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>图表</title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
</head>

<script>
	require([
     	"dojo/_base/kernel"
     					
     	],
	function( kernel
		) {
		demoReturn = function(){
			demo_static("staticDesign");
		};
		addDemoTitle = function(){
			rosten.kernel.createRostenShowDialog(rosten.webPath + "/demo/addTitle", {
	            onLoadFunction : function() {

	            }
	        });
		};
		deleteDemoTitle = function(){

		};
		demoView = function(){
			rosten.openNewWindow("demoView", rosten.webPath + "/demo/staticView");
		};
	
	});
	
</script>
<body>
	<div data-dojo-type="rosten/widget/ActionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'demo',action:'desgine')}"'>
	</div>
	<div style="margin:0 auto;text-align:center;margin-top:10px">
		<img src="${resource(dir:'images/rosten/demo',file:'6.jpg')}">
	</div>
	
</body>
</html>
