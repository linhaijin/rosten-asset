<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title></title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
</head>

<script>
	require([
     	"dojo/_base/kernel"
     					
     	],
	function( kernel
		) {
		
	
	});
	
</script>
<body>
	<div data-dojo-type="rosten/widget/ActionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'demo',action:'flowShowActionhello')}"'>
	</div>
	<div style="margin:0 auto;text-align:center;margin-top:10px">
		<img src="${resource(dir:'images/rosten/demo',file:'flow.png')}">
	</div>
	
</body>
</html>
