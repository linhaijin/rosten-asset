<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>资产盘点任务</title>
    <style type="text/css">
    	.rosten .rosten_form table tr{
    		height:30px;
    	}
    	body{
			overflow:auto;
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
			function(parser,dom,kernel,registry,connect,domStyle,dom){
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}"});
					rosten.cssinit();

					connect.connect(registry.byId("isAllDepart1"),"onClick",function(){
						domStyle.set(dom.byId("departShowTr"),"display","none");
						domStyle.set(dom.byId("receiveManShowTr"),"display","none");
					});

					connect.connect(registry.byId("isAllDepart2"),"onClick",function(){
						domStyle.set(dom.byId("departShowTr"),"display","");
						domStyle.set(dom.byId("receiveManShowTr"),"display","");
					});

					connect.connect(registry.byId("isAllCategory1"),"onClick",function(){
						domStyle.set(dom.byId("categoryShowTr"),"display","none");
					});

					connect.connect(registry.byId("isAllCategory2"),"onClick",function(){
						domStyle.set(dom.byId("categoryShowTr"),"display","");
					});
					
				});
				assetCheck_save = function(){
					var formWidget = registry.byId("rosten_form");
					if(!formWidget.validate()){
						rosten.alert("请正确填写相关信息！");
						return;
					}
					
					rosten.readSync(rosten.webPath + "/inventoryTask/assetCheckSave",{},function(data){
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
		data-dojo-props='actionBarSrc:"${createLink(controller:'inventoryTask',action:'assetCheckForm',id:inventoryTask?.id,params:[userid:user?.id])}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="盘点任务单" data-dojo-props=''>
		<form id="rosten_form" data-dojo-type="dijit/form/Form" name="rosten_form" onsubmit="return false;" class="rosten_form" style="padding:0px">
			<g:hiddenField name="taskNum_form" value="${inventoryTask?.taskNum}" />
			<div style="display:none">
				<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${inventoryTask?.id }"' />
	        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
			</div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"任务信息",toggleable:false,moreText:"",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>任务单号：</div></td>
					    <td width="250">
                           	<input id="taskNum" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"taskNum",${fieldAcl.isReadOnly("taskNum")},
                               	trim:true,
                               	required:true,
                               	disabled:"disabled",
             					value:"${inventoryTask?.taskNum}"
                           	'/>
			            </td>
			            <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>制单日期：</div></td>
					   	<td width="250">
					    	<input id="makeDate" data-dojo-type="dijit/form/DateTextBox" 
		                 		data-dojo-props='name:"makeDate",${fieldAcl.isReadOnly("makeDate")},
		                 		trim:true,
		                 		required:true,
								value:"${inventoryTask?.getFormattedMakeDate()}"
		                	'/>
		                </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>盘点部门：</div></td>
					   	<td>
					   		<input id="isAllDepart1" data-dojo-type="dijit/form/RadioButton"
				           		data-dojo-props='name:"isAllDepart",type:"radio",${fieldAcl.isReadOnly("isAllDepart")},
				           			<g:if test="${inventoryTask?.isAllDepart==true }">checked:true,</g:if>
									value:"true"
			              	'/>
							<label for="isAllDepart1">所有部门</label>
						
			              	<input id="isAllDepart2" data-dojo-type="dijit/form/RadioButton"
			           			data-dojo-props='name:"isAllDepart",type:"radio",${fieldAcl.isReadOnly("isAllDepart")},
			           			<g:if test="${inventoryTask?.isAllDepart==false }">checked:true,</g:if>
								value:"false"
			              	'/>
							<label for="isAllDepart2">部分部门</label>
			           	</td>
			           	<td><div align="right"><span style="color:red">*&nbsp;</span>发布人：</div></td>
					   	<td>
					    	<input id="sendMan" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"sendMan",${fieldAcl.isReadOnly("sendMan")},
                            	trim:true,readOnly:true,
                            	required:true,
          						value:"${user?.getFormattedName()}"
                           	'/>
			           	</td>
					</tr>
					<tr id="departShowTr" <g:if test="${inventoryTask?.isAllDepart }">style="display:none" </g:if>>
						<td></td>
						<td colspan="3">
							<input id="allowdepartsName" data-dojo-type="dijit/form/ValidationTextBox" 
				               	data-dojo-props='name:"allowdepartsName",${fieldAcl.isReadOnly("allowdepartsName")},
				               	trim:true,readOnly:true,style:{width:"550px"},
								value:"${inventoryTask?.getDepartName()}"
				          	'/>
				         	<g:hiddenField id ="allowdepartsId" name="allowdepartsId" value="${inventoryTask?.getDepartId()}" />
							<button data-dojo-type="dijit.form.Button" 
								data-dojo-props='onClick:function(){
									var _object = rosten.selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}",true,"allowdepartsName","allowdepartsId");
									_object.afterLoad = function(){
							 			var allowdepartsId = document.getElementById("allowdepartsId").value.split(",");
										_object.selectedData(allowdepartsId);
							 		}
									
								}'>选择</button>
						</td>
					
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>资产类型：</div></td>
					   	<td>
					   		<input id="isAllCategory1" data-dojo-type="dijit/form/RadioButton"
				           		data-dojo-props='name:"isAllCategory",type:"radio",${fieldAcl.isReadOnly("isAllCategory")},
				           			<g:if test="${inventoryTask?.isAllCategory==true }">checked:true,</g:if>
									value:"true"
			              	'/>
							<label for="isAllCategory1">所有类型</label>
						
			              	<input id="isAllCategory2" data-dojo-type="dijit/form/RadioButton"
			           			data-dojo-props='name:"isAllCategory",type:"radio",${fieldAcl.isReadOnly("isAllCategory")},
			           			<g:if test="${inventoryTask?.isAllCategory==false }">checked:true,</g:if>
								value:"false"
			              	'/>
							<label for="isAllCategory2">部分类型</label>
			           	</td>
			           	<td></td>
					   	<td></td>
					</tr>
					<tr id="categoryShowTr" <g:if test="${inventoryTask?.isAllCategory }">style="display:none" </g:if>>
						<td></td>
					    <td colspan="3">
					    	<input id="allowCategoryName" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"allowCategoryName",${fieldAcl.isReadOnly("allowCategoryName")},
                               	trim:true,
                               	readOnly:true,style:{width:"550px"},
             					value:"${inventoryTask?.getCategoryName()}"
                           	'/>
                           	<g:hiddenField id="allowCategoryId" name="allowCategoryId" value="${inventoryTask?.getCategoryId() }" />
							<button data-dojo-type="dijit.form.Button" 
								data-dojo-props='onClick:function(){
									var _object = rosten.selectAssetCategory("${createLink(controller:'assetConfig',action:'assetCategoryTreeDataStore',params:[companyId:company?.id])}",true,"allowCategoryName","allowCategoryId");
									_object.afterLoad = function(){
							 			var allowCategoryId = document.getElementById("allowCategoryId").value.split(",");
										_object.selectedData(allowCategoryId);
							 		}
								}'>选择</button>
			           	</td>
					</tr>
					<tr id="receiveManShowTr" <g:if test="${inventoryTask?.isAllDepart }">style="display:none" </g:if>>
						<td><div align="right"><span style="color:red">*&nbsp;</span>接收人：</div></td>
					    <td colspan="3">
					    	<input id="receiveMan" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"receiveMan",${fieldAcl.isReadOnly("receiveMan")},
                           		trim:true,
                           		readOnly:true,style:{width:"550px"},
                           		value:"${inventoryTask?.receiveMan}"
                           	'/>
                            <g:hiddenField id="allowusersId" name="allowusersId" value="${inventoryTask?.getReceiveManId()}" />
						 	<button data-dojo-type="dijit.form.Button"
						 			data-dojo-props='onClick:function(){
						 				var _object = rosten.selectUser("${createLink(controller:'system',action:'userTreeDataStore',params:[companyId:company?.id])}","multile","receiveMan","allowusersId");
						 				
						 			}'
						 	>选择</button>
			            </td>
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>开始日期：</div></td>
					    <td>
					    	<input id="startDate" data-dojo-type="dijit/form/DateTextBox" 
		                 		data-dojo-props='name:"startDate",${fieldAcl.isReadOnly("startDate")},
		                 		trim:true,
		                 		required:true,
								value:"${inventoryTask?.getFormattedStartDate()}"
		                	'/>
		                </td>
			            <td><div align="right"><span style="color:red">*&nbsp;</span>截止日期：</div></td>
					   	<td>
					    	<input id="endDate" data-dojo-type="dijit/form/DateTextBox" 
		                 		data-dojo-props='name:"endDate",${fieldAcl.isReadOnly("endDate")},
		                 		trim:true,
		                 		required:true,
								value:"${inventoryTask?.getFormattedEndDate()}"
		                	'/>
		                </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>任务描述：</div></td>
					    <td colspan="3">
					    	<textarea id="taskDesc" data-dojo-type="dijit/form/SimpleTextarea" 
    							data-dojo-props='name:"taskDesc",
                               		style:{width:"550px",height:"150px"},
                               		trim:true,require:true,
                               		value:"${inventoryTask?.taskDesc}"
                           '>
    						</textarea>
			            </td>
					</tr>
				</table>
				<div style="clear:both;"></div>
			</div>
		</form>
	</div>
</div>
</body>