<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>资产盘点任务</title>
    <style type="text/css">
    	.rosten table tr{
    		height:30px;
    	}
    	body{
			overflow:auto;
		}
		.rosten .rostenGridView .pagecontrol{
			/*text-align:left;*/
		}
		.rosten .rostenTaskTitleGrid .dijitTitlePaneContentInner{
			padding:2px 1px 1px 1px;
		}
    </style>
	<script type="text/javascript">
	require(["dojo/parser",
	         "dojo/dom",
		 		"dojo/_base/kernel",
		 		"dijit/registry",
		 		"dojo/_base/connect",
		 		"dojo/dom-style",
		 		"dojo/dom",
		 		"rosten/widget/ShowDialog",
		 		"dijit/layout/TabContainer",
		 		"dijit/layout/ContentPane",
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/DateTextBox",
		 		"dijit/form/SimpleTextarea",
		 		"dijit/form/Button",
		     	"rosten/widget/ActionBar",
		     	"rosten/widget/TitlePane",
		     	"rosten/app/Application",
		     	"rosten/app/SystemApplication",
		     	"rosten/kernel/behavior"],
			function(parser,dom,kernel,registry,connect,domStyle,dom,ShowDialog){
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}",dojogridcss : true});
					rosten.cssinit();
					
				});
				//盘点导入
				pddr = function(){
					rosten.variable.dialog = new ShowDialog({src:rosten.webPath + "/inventoryTask/zddr"});
				};
				//扫描枪盘点
				zdpd = function(){
					rosten.variable.dialog = new ShowDialog({src:rosten.webPath + "/inventoryTask/zdpd"});
				};
				//结束盘点
				zdpd_ok = function(){
					rosten.readSyncNoTime(rosten.webPath + "/inventoryTask/zdpd_ok/${myTask?.id }",{},function(data){
						if(data.result=="true" || data.result == true){
							rosten.alert("成功！").queryDlgClose= function(){
								page_quit();
							};
						}else{
							rosten.alert("失败!");
						}
					});
				};
				//资产查询
				zdpd_search = function(){
					var registerNum = registry.byId("registerNum").attr("value");
					if(registerNum == null || registerNum == "" ){
						rosten.alert("注意：资产编号不能为空！");
						registry.byId("registerNum").focus();
						return;
					}
					
					var content ={};
					content.id = registerNum;
					rosten.readSyncNoTime(rosten.webPath + "/inventoryTask/zdpd_search",content,function(data){
						if(data.result=="true" || data.result == true){
							registry.byId("registerNum").set("value",data.registerNum);
							registry.byId("assetName").set("value",data.assetName);
							registry.byId("specifications").set("value",data.specifications);
							registry.byId("buyDate").set("value",data.buyDate);
							registry.byId("onePrice").set("value",data.onePrice);
							registry.byId("storagePosition").set("value",data.storagePosition);
							registry.byId("purchaser").set("value",data.purchaser);
							registry.byId("depart").set("value",data.depart);
						}else{
							rosten.alert("未找到对应资产或未在此次核查范围，请核查!");
						}
					});
				};
				//资产盘点，入库
				zdpd_pdrk = function(){
					var registerNum = registry.byId("registerNum").attr("value");
					if(registerNum == null || registerNum == "" ){
						rosten.alert("注意：资产编号不能为空！");
						registry.byId("registerNum").focus();
						return;
					}
					
					var content ={};
					content.id = registerNum;
					rosten.readSyncNoTime(rosten.webPath + "/inventoryTask/zdpd_pdrk",content,function(data){
						if(data.result=="true" || data.result == true){
							rosten.alert("成功！").queryDlgClose= function(){
								rosten.variable.dialog.hide();
								rosten.variable.dialog.destroy()
								taskItemGrid.refresh();
							};
						}else{
							rosten.alert("失败!");
						}
					});
					
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
		data-dojo-props='actionBarSrc:"${createLink(controller:'inventoryTask',action:'myTaskForm',id:myTask?.id,params:[userid:user?.id])}"'>
	</div>
</div >
<div style="display:none">
	<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${myTask?.id }"' />
   	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
</div>
<div style="width:800px;margin:0px auto;margin-top:10px;">
	<div data-dojo-type="rosten/widget/TitlePane"  
		data-dojo-props='title:"任务信息",toggleable:false,moreText:"",marginBottom:"2px"'>
		<table border="0" width="740" align="left">
			<tr>
			    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>任务单号：</div></td>
			    <td width="250">
                         	<input id="taskNum" data-dojo-type="dijit/form/ValidationTextBox" 
                             	data-dojo-props='name:"taskNum",${fieldAcl.isReadOnly("taskNum")},
                             	trim:true,
                             	readOnly:true,
           					value:"${myTask?.getTaskNumber()}"
                         	'/>
	            </td>
	            <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>盘点类型：</div></td>
			   	<td width="250">
			    	<input id="makeDate" data-dojo-type="dijit/form/ValidationTextBox" 
                 		data-dojo-props='name:"makeDate",${fieldAcl.isReadOnly("makeDate")},
                 		trim:true,
                 		readOnly:true,
						value:"${myTask?.getTaskName()}"
                	'/>
                </td>
			</tr>
			
			<tr>
			    <td><div align="right"><span style="color:red">*&nbsp;</span>开始日期：</div></td>
			    <td>
			    	<input id="startDate" data-dojo-type="dijit/form/DateTextBox" 
                 		data-dojo-props='name:"startDate",${fieldAcl.isReadOnly("startDate")},
                 		trim:true,
                 		readOnly:true,
						value:"${myTask?.getStartDate()}"
                	'/>
                </td>
	            <td><div align="right"><span style="color:red">*&nbsp;</span>截止日期：</div></td>
			   	<td>
			    	<input id="endDate" data-dojo-type="dijit/form/DateTextBox" 
                 		data-dojo-props='name:"endDate",${fieldAcl.isReadOnly("endDate")},
                 		trim:true,
                 		readOnly:true,
						value:"${myTask?.getEndDate()}"
                	'/>
                </td>
			</tr>
			<tr>
				<td><div align="right"><span style="color:red">*&nbsp;</span>任务描述：</div></td>
			    <td colspan="3">
			    	<textarea id="taskDesc" data-dojo-type="dijit/form/SimpleTextarea" 
  							data-dojo-props='name:"taskDesc",
                             		style:{width:"550px",height:"60px"},
                             		trim:true,readOnly:true,
                             		value:"${myTask?.getTaskDesc()}"
                         '>
  						</textarea>
	            </td>
			</tr>
		</table>
		<div style="clear:both;"></div>
	</div>
	<div data-dojo-type="rosten/widget/TitlePane" style="margin-top:2px" data-dojo-props='"class":"rostenTaskTitleGrid",title:"盘点明细",toggleable:false,moreText:"",marginBottom:"2px"'>
		<div data-dojo-type="rosten/widget/RostenGrid" id="taskItemGrid" data-dojo-id="taskItemGrid"
			data-dojo-props='imgSrc:"${resource(dir:'images/rosten/share',file:'wait.gif')}",showPageControl:true,url:"${createLink(controller:'inventoryTask',action:'taskItemGrid',id:myTask?.id,params:[companyId:company?.id])}"'></div>
	</div>
	
</div>
</body>