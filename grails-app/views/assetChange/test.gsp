<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>资产变动--报废报损</title>
    
	<script type="text/javascript">
	require(["dojo/parser",
	         "dojo/dom",
		 		"dojo/_base/kernel",
		 		"dijit/registry",
		 		"rosten/app/BookKeeping"
		 		],
			function(parser,dom,kernel,registry){
				
				assetScrap_save = function(){
					rosten.readSync(rosten.webPath + "/assetScrap/assetScrapSave",{},function(data){
						if(data.result=="true" || data.result == true){
							rosten.alert("保存成功！").queryDlgClose= function(){
								page_quit();
								//window.location.reload();
							};
						}else{
							rosten.alert("保存失败!");
						}
					},null,"rosten_form");
				};
				page_quit = function(){
					rosten.pagequit();
				};
				
		});
    </script>
</head>
<body>
<div class="rosten_action">
	<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="rosten_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'assetScrap',action:'assetScrapForm',id:assetScrap?.id,params:[userid:user?.id])}"'>
	</div>
</div>

	
	<div id="assetScrapList" data-dojo-type="dijit.layout.ContentPane" data-dojo-props='style:"width:780px;height:300px;padding:2px;"'>
				<div data-dojo-type="rosten/widget/RostenGrid" id="assetScrapListGrid" data-dojo-id="assetScrapListGrid"
					data-dojo-props='url:"${createLink(controller:'assetScrap',action:'assetScrapListDataStore',params:[companyId:company?.id])}",showRowSelector:"normal"'></div>
				
			</div>
	
	 
</body>