<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>资产运维--资产报修</title>
    <link rel="stylesheet" href="${createLinkTo(dir:'js/dojox/widget/Wizard',file:'Wizard.css') }"></link>
    <style type="text/css">
    	.rosten .dsj_form table tr{
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
		"dojo/_base/lang",
		"dijit/registry",
		"dijit/layout/TabContainer",
		"dijit/layout/BorderContainer",
		"dijit/layout/ContentPane",
		"dijit/form/ValidationTextBox",
		"dijit/form/DateTextBox",
		"dijit/form/SimpleTextarea",
		"dijit/form/Button",
		"dijit/form/Select",
		"dijit/Dialog",
		"dojox/grid/DataGrid",
    	"rosten/widget/ActionBar",
    	"rosten/widget/TitlePane",
    	"rosten/app/Application",
    	"rosten/app/SystemApplication",
    	"rosten/kernel/behavior"],
		function(parser,dom,kernel,lang,registry){
			kernel.addOnLoad(function(){
				rosten.init({webpath:"${request.getContextPath()}",dojogridcss : true});
				rosten.cssinit();
			});
			
			assetRepair_save = function(object){
				
				var usedDepartName = dojo.byId("usedDepartName").value;
				if(usedDepartName == "" || usedDepartName == null){
					rosten.alert("注意：使用部门不能为空！");
					document.getElementById("usedDepartName").focus();
					return;
				}
				var usedMan = dojo.byId("usedMan").value;
				if(usedMan == "" || usedMan == null){
					rosten.alert("注意：使用人不能为空！");
					document.getElementById("usedMan").focus();
					return;
				}
				var repairReason = dojo.byId("repairReason").value;
				if(repairReason == "" || repairReason == null){
					rosten.alert("注意：报修原因不能为空！");
					document.getElementById("repairReason").focus();
					return;
				}
				var contacts = dojo.byId("contacts").value;
				if(contacts == "" || contacts == null){
					rosten.alert("注意：联系人不能为空！");
					document.getElementById("contacts").focus();
					return;
				}
				var contactPhone = dojo.byId("contactPhone").value;
				if(contactPhone == "" || contactPhone == null){
					rosten.alert("注意：联系电话不能为空！");
					document.getElementById("contactPhone").focus();
					return;
				}
				var assetTotal = dojo.byId("assetTotal").value;
				if(assetTotal == 0 || assetTotal == "" || assetTotal == null){
					rosten.alert("注意：请添加资产信息！");
					return;
				}

				//新增是否同类型资产变更start--2014-11-15
				var searchQuery = {id:"*"};
				var categoryId = "";
				var grid = dijit.byId("assetRepairListGrid");
				var store = grid.store;
				store.fetch({
					query:searchQuery,onComplete:function(items){
						for(var i=0;i < items.length;i++){
							var _item = items[i];
							categoryId += store.getValue(_item, "id") + ",";
						}
					},queryOptions:{deep:true}
				});
				categoryId = categoryId.substring(0,categoryId.length-1) 
				
				var qSeriesNo = "";
				var seriesNo = "${assetRepair?.seriesNo}";
				qSeriesNo = "?seriesNo="+encodeURI(seriesNo);

				var url = "${createLink(controller:'assetRepair',action:'assetRepairSaveCheck')}";
				//url += "?categoryId="+encodeURI(categoryId);
				url += qSeriesNo;

				var ioArgs = {
					url : url,
					handleAs : "json",
					load : function(response,args) {
						if(response.result=="false"){//rensult为false，资产列表为不同类型资产
							rosten.alert("注意：只能为同类型资产！");
							return;
						}else{//rensult为true，资产列表为同类型资产，继续
							//增加对多次单击的次数----2014-9-4
							var buttonWidget = object.target;
							rosten.toggleAction(buttonWidget,true);

							//流程相关信息
							var content = {};
							content.assetTotal = assetTotal;
							<g:if test='${flowCode}'>
								content.flowCode = "${flowCode}";
								content.relationFlow = "${relationFlow}";
							</g:if>
							rosten.readSync(rosten.webPath + "/assetRepair/assetRepairSave",content,function(data){
								if(data.result=="true" || data.result == true){
									rosten.alert("保存成功！").queryDlgClose= function(){
										<g:if test='${flowCode}'>
											if(window.location.href.indexOf(data.id)==-1){
												window.location.replace(window.location.href + "&id=" + data.id);
											}else{
												window.location.reload();
											}
										</g:if>
										<g:else>
											page_quit();
										</g:else>
									};
								}else{
									rosten.alert("保存失败!");
								}
							},function(error){
								rosten.alert("系统错误，请通知管理员！");
								rosten.toggleAction(buttonWidget,false);
							},"rosten_form");
						}
					},
					error : function(response,args) {
						rosten.alert(response.message);
						return;
					}
				};
				dojo.xhrPost(ioArgs);
			}
			
			page_quit = function(){
				rosten.pagequit();
			}	

			assetRepair_addComment = function(){
				//flowCode为是否需要走流程，如需要，则flowCode为业务流程代码
				var commentDialog = rosten.addCommentDialog({type:"assetRepair"});
				commentDialog.callback = function(_data){
					var content = {dataStr:_data.content,userId:"${user?.id}",status:"${assetRepair?.status}",flowCode:"${flowCode}"};
					rosten.readSync(rosten.webPath + "/share/addComment/${assetRepair?.id}",content,function(data){
						if(data.result=="true" || data.result == true){
							rosten.alert("意见填写成功！").queryDlgClose= function(){
								var selectWidget = rosten_tabContainer.selectedChildWidget;
								if(selectWidget.get("id")=="flowComment"){
									rosten_tabContainer.selectedChildWidget.refresh();
								}
							};
						}else{
							rosten.alert("意见填写失败!");
						}	
					});
				};
			}

			assetRepair_submit = function(object,conditionObj){
				/*
				 * 从后台获取下一处理人;conditionObj为流程中排他分支使用
				 */
				//增加对多次单击的控制
				var buttonWidget = object.target;
				rosten.toggleAction(buttonWidget,true);
				
				var content = {};

				//增加对排他分支的控制
				if(conditionObj){
					lang.mixin(content,conditionObj);
				}
				rosten.readSync("${createLink(controller:'share',action:'getSelectFlowUser',params:[userId:user?.id,taskId:assetRepair?.taskId,drafterUsername:assetRepair?.drafter?.username])}",content,function(data){
					if(data.dealFlow==false){
						//流程无下一节点
						assetRepair_deal("submit",null,buttonWidget,conditionObj);
						return;
					}
					var url = "${createLink(controller:'system',action:'userTreeDataStore',params:[companyId:company?.id])}";
					if(data.dealType=="user"){
						//人员处理
						if(data.showDialog==false){
							//单一处理人
							var _data = [];
							_data.push(data.userId + ":" + data.userDepart);
							assetRepair_deal("submit",_data,buttonWidget,conditionObj);
						}else{
							//多人，多部门处理
							url += "&type=user&user=" + data.user;
							assetRepair_select(url,buttonWidget,conditionObj);
						}
					}else{
						//群组处理
						url += "&type=group&groupIds=" + data.groupIds;
						if(data.limitDepart){
							url += "&limitDepart="+data.limitDepart;
						}
						assetRepair_select(encodeURI(url),buttonWidget,conditionObj);
					}

				},function(error){
					rosten.alert("系统错误，请通知管理员！");
					rosten.toggleAction(buttonWidget,false);
				});
			}

			assetRepair_select = function(url,buttonWidget,conditionObj){
				var rostenShowDialog = rosten.selectFlowUser(url,"single");
	            rostenShowDialog.callback = function(data) {
	            	if(data.length==0){
		            	rosten.alert("请正确选择人员！");
	            		rosten.toggleAction(buttonWidget,false);
		            }else{
		            	var _data = [];
		            	for (var k = 0; k < data.length; k++) {
		            		var item = data[k];
		            		_data.push(item.value + ":" + item.departId);
		            	};
		            	assetRepair_deal("submit",_data,buttonWidget,conditionObj);
		            }
	            };
				rostenShowDialog.afterLoad = function(){
					var _data = rostenShowDialog.getData();
		            if(_data && _data.length==1){
			            //直接调用
		            	rostenShowDialog.doAction();
			        }else{
						//显示对话框
						rostenShowDialog.open();
				    }
				};
				rostenShowDialog.queryDlgClose = function(){
					rosten.toggleAction(buttonWidget,false);
				};	
			}
		
			assetRepair_deal = function(type,readArray,buttonWidget,conditionObj){
				var content = {};
				content.id = "${assetRepair?.id}";
				content.status = "${assetRepair?.status}";
				content.deal = type;
				if(readArray){
					content.dealUser = readArray.join(",");
				}
				if(conditionObj){
					lang.mixin(content,conditionObj);
				}
				rosten.readSync(rosten.webPath + "/assetRepair/assetRepairFlowDeal",content,function(data){
					if(data.result=="true" || data.result == true){
						var _nextUserName = "";
						if(data.nextUserName && data.nextUserName!=""){
							_nextUserName = data.nextUserName;
						}
						if(_nextUserName == "" || _nextUserName == null){
							rosten.alert("成功，流程已结束！").queryDlgClose= function(){
								//刷新待办事项内容
								window.opener.showStartGtask("${user?.id}","${company?.id }");
								rosten.pagequit();
							}
						}else{
							rosten.alert("成功，已发送至< " + _nextUserName +" >！").queryDlgClose= function(){
								//刷新待办事项内容
								window.opener.showStartGtask("${user?.id}","${company?.id }");
								
								if(data.refresh=="true" || data.refresh==true){
									window.location.reload();
								}else{
									rosten.pagequit();
								}
							}
						}
					}else{
						rosten.alert("失败!");
						rosten.toggleAction(buttonWidget,false);
					}	
				},function(error){
					rosten.alert("系统错误，请通知管理员！");
					rosten.toggleAction(buttonWidget,false);
				});
			}
		
			assetRepair_back = function(object,conditionObj){
				//增加对多次单击的控制
				var buttonWidget = object.target;
				rosten.toggleAction(buttonWidget,true);
				
				var content = {};
				rosten.readSync("${createLink(controller:'assetRepair',action:'assetRepairFlowBack',params:[id:assetRepair?.id])}",content,function(data){
					if(data.result=="true" || data.result == true){
						var _nextUserName = "";
						if(data.nextUserName && data.nextUserName!=""){
							_nextUserName = data.nextUserName;
						}
						rosten.alert("成功，已发送至< " + _nextUserName +" >！").queryDlgClose= function(){
							//刷新待办事项内容
							window.opener.showStartGtask("${user?.id}","${company?.id }");
							
							if(data.refresh == "true" || data.refresh == true){
								window.location.reload();
							}else{
								rosten.pagequit();
							}
						}
					}else{
						rosten.alert("失败!");
						rosten.toggleAction(buttonWidget,false);
					}
					
				},function(error){
					rosten.alert("系统错误，请通知管理员！");
					rosten.toggleAction(buttonWidget,false);
				});
			}

			addAsset = function(){
				var tabContainer = dijit.byId("rosten_tabContainer");
				var title = "资产筛选";
				var assetpaneid = "assetCategoryChoose_pane";
				var url = "${createLink(controller:'assetCategoryChoose',action:'assetChoose',params:[companyId:company?.id])}";
	
				var qcontrolName = "&controlName=assetRepair";
				
				var qseriesNo = "";
				var seriesNo = dijit.byId("seriesNo").attr("value");
				if(seriesNo != null || seriesNo != ""){
					qseriesNo = "&seriesNo="+seriesNo;
				}
				
				url += qcontrolName + qseriesNo;
				
				if(dijit.byId("assetCategoryChoose_pane")){
					var node_pane = dijit.byId("assetCategoryChoose_pane");
					tabContainer.removeChild(node_pane);
					node_pane.destroyRecursive();
				}

				var node = document.createElement("div");
				var w = new dijit.layout.ContentPane({id:assetpaneid,renderStyles:true,title:title,selected:true,closable:true,href:url},node);
				dojo.connect(dijit.byId(assetpaneid), "onClose", null , function(){});
				dojo.style(w.domNode,{"padding":"4px"})
				tabContainer.addChild(w);
				tabContainer.selectChild(w);
			}
		
			deleteAsset = function(){
				var grid = dijit.byId("assetRepairListGrid");
				var selected = grid.getSelected();
				if (selected.length == 0) {
					rosten.alert("注意：请在列表中选择资产！");
					return;
				}
				
				var assetId = "";
				var store = grid.store;
				dojo.forEach(selected,function(item){
					if (assetId==""){
						assetId = store.getValue(item, "id");
					}else{
						assetId = assetId+","+store.getValue(item, "id");
					}
				});
	
				var repairId = "${assetRepair?.id}";
				var seriesNo = "${assetRepair?.seriesNo}";
				var assetTotal = dojo.byId("assetTotal").value;
				var url = "${createLink(controller:'assetRepair',action:'assetChooseDelete')}";
				url += "?assetId="+encodeURI(assetId)+"&repairId="+repairId+"&seriesNo="+seriesNo+"&assetTotal="+assetTotal;
				var ioArgs = {
					url : url,
					handleAs : "json",
					load : function(response,args) {
						if(response.result=="true"){//rensult为true，后台数据操作成功并返回总金额，同时刷新父页面Grid
							var assetTotal = response.assetTotal;
							dojo.byId("assetTotal").value = assetTotal.toFixed(2);
		
							var url_twice = "${createLink(controller:'assetRepair',action:'assetRepairListDataStore')}";
							var qs;
							var qCompany = "";
							var compamyId;
							<g:if test="${company?.id}">
								compamyId = "${company?.id}";
								qCompany = "?companyId="+encodeURI(compamyId);
							</g:if>
		
							var qFreshType = "&freshType=twice";
		
							var qSeriesNo = "&seriesNo="+seriesNo;
							
							url_twice += qCompany+qSeriesNo;
							var grid_twice = dijit.byId("assetRepairListGrid");
							
							grid_twice.url = url_twice;
							grid_twice.refresh();
						}else{//rensult为false，处理失败
							rosten.alert("操作失败，请联系管理员!");
							return;
						}
					},
					error : function(response,args) {
						rosten.alert(response.message);
						return;
					}
				};
				dojo.xhrPost(ioArgs);
			}
			
			assetCategoryChoose_close = function(){
				var tabContainer = dijit.byId("rosten_tabContainer");
				var node = dijit.byId("assetCategoryChoose_pane");
				tabContainer.removeChild(node);
				node.destroyRecursive();
			}
	
			assetCategoryChoose_search = function(){
				var url = "${createLink(controller:'assetCategoryChoose',action:'assetCategoryChooseListDataStore')}";
				
				var qCompany = "";
				var compamyId;
				<g:if test="${company?.id}">
					compamyId = "${company?.id}";
					qCompany = "?companyId="+encodeURI(compamyId);
				</g:if>
				
				var qSeriesNo = "";
				var seriesNo = "${assetRepair?.seriesNo}";
				qSeriesNo = "&seriesNo="+encodeURI(seriesNo);
				
				var qControlName = "&controlName=assetRepair";
		
				var qAssetCardsType = "";
				var assetCardsType;
				var assetCardsTypeSel = dijit.byId("assetCardsType");
				if(assetCardsTypeSel){
					if(assetCardsTypeSel.attr("value")!=""){
						assetCardsType = assetCardsTypeSel.attr("value");
						qAssetCardsType = "&assetCardsType="+encodeURI(assetCardsType);
					}else{
						rosten.alert("注意：请选择资产类别！");
						document.getElementById("assetCardsType").focus();
						return;
					}
				}

				var qAssetDepart = "";
				var assetDepart;
				var assetDepartSel = dijit.byId("assetDepart");
				if(assetDepartSel){
					if(assetDepartSel.attr("value") != ""){
						assetDepart = assetDepartSel.attr("value");
						qAssetDepart = "&assetDepart=" + encodeURI(assetDepart);
					}
				}

				var qAssetUser = "";
				var assetUser;
				var assetUserSel = dijit.byId("assetUser");
				if(assetUserSel){
					if(assetUserSel.attr("value") != ""){
						assetUser = assetUserSel.attr("value");
						qAssetUser = "&assetUser=" + encodeURI(assetUser);
					}
				}
				
				url += qCompany+qSeriesNo+qAssetCardsType+qAssetDepart+qAssetUser+qControlName;
				
				var grid = dijit.byId("assetCategoryChooseListGrid");
				grid.url = url;
				grid.refresh();
			}
			
			assetCategoryChoose_reset = function(){
				registry.byId("assetCardsType").set("value","");
				registry.byId("assetDepart").set("value","");
				registry.byId("assetUser").set("value","");
				rosten.kernel.refreshGrid();
			}
			
			assetCategoryChoose_add = function(){
				var grid = dijit.byId("assetCategoryChooseListGrid");
				var selected = grid.getSelected();
				if (selected.length == 0) {
					rosten.alert("注意：请在列表中选择资产！");
					return;
				}else{
					var url = "${createLink(controller:'assetCategoryChoose',action:'assetCategoryChooseOperate')}";

					var qCompany = "";
					var compamyId;
					<g:if test="${company?.id}">
						compamyId = "${company?.id}";
						qCompany = "?companyId="+encodeURI(compamyId);
					</g:if>
					
					var qControlName = "&controlName=assetRepair";

					var qSeriesNo = "";
					var seriesNo = "${assetRepair?.seriesNo}";
					qSeriesNo = "&seriesNo="+encodeURI(seriesNo);
			
					var qAssetCardsType = "";
					var assetCardsType;
					var assetCardsTypeSel = dijit.byId("assetCardsType");
					if(assetCardsTypeSel){
						if(assetCardsTypeSel.attr("value")!=""){
							assetCardsType = assetCardsTypeSel.attr("value");
							qAssetCardsType = "&assetCardsType="+encodeURI(assetCardsType);
						}
					}

					var qAssetId = "";
					var assetId = "";
					var store = grid.store;
					dojo.forEach(selected,function(item){
						if (assetId==""){
							assetId = store.getValue(item, "id");
						}else{
							assetId = assetId+","+store.getValue(item, "id");
						}
					});
					if(assetId != ""){
						qAssetId = "&assetId="+encodeURI(assetId);
					}
					
					
					var assetTotal;
					var assetTotalSel = dijit.byId("assetTotal");
					if(assetTotalSel){
						if(assetTotalSel.attr("value") == "" || assetTotalSel.attr("value") == null){
							assetTotal = "0-0";
						}else{
							assetTotal = assetTotalSel.attr("value").replace(".","-");
						}
					}
					var qAssetTotal = "&assetTotal="+encodeURI(assetTotal);
					
					url += qCompany+qControlName+qSeriesNo+qAssetCardsType+qAssetId+qAssetTotal;
					//异步处理所选资产数据
					var ioArgs = {
						url : url,
						handleAs : "json",
						load : function(response,args) {
							if(response.result=="true"){//rensult为true，后台数据操作成功并返回总金额，同时刷新父页面Grid
								var url_twice = "${createLink(controller:'assetRepair',action:'assetRepairListDataStore')}";
								var qCompany = "";
								var compamyId;
								<g:if test="${company?.id}">
									compamyId = "${company?.id}";
									qCompany = "?companyId="+encodeURI(compamyId);
								</g:if>

								var qSeriesNo = "";
								var seriesNo = "${assetRepair?.seriesNo}";
								qSeriesNo = "&seriesNo="+encodeURI(seriesNo);
			
								url_twice += qCompany+qSeriesNo;
								
								var assetTotal = response.assetTotal;
								dojo.byId("assetTotal").value = assetTotal.toFixed(2);
								
								//刷新资产筛选页面
								assetCategoryChoose_search();
								
								var grid_twice = dijit.byId("assetRepairListGrid");
								grid_twice.url = url_twice;
								grid_twice.refresh();
								
								//var pane = dijit.byId("assetCategoryChoose_pane");
								//pane.set("disabled", true);
							}else{//rensult为false，处理失败
								rosten.alert("操作失败，请联系管理员!");
								return;
							}
						},
						error : function(response,args) {
							rosten.alert(response.message);
							return;
						}
					};
					dojo.xhrPost(ioArgs);
				}
			}
		});
    </script>
</head>
<body>
<div class="rosten_action">
	<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="rosten_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'assetRepair',action:'assetRepairForm',id:assetRepair?.id,params:[userid:user?.id])}"'>
	</div>
</div>

<div id="rosten_tabContainer" data-dojo-id="rosten_tabContainer" data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",height:"730px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="申请信息" data-dojo-props='height:"570px",marginBottom:"2px",region:"top"'>
		<form id="rosten_form" name="rosten_form" onsubmit="return false;" class="rosten_form" style="padding:0px">
			<g:hiddenField name="seriesNo_form" value="${assetRepair?.seriesNo}" />
			<g:hiddenField name="applyMan" id="applyMan" value="${assetRepair?.applyMan == null?user.chinaName:assetRepair?.applyMan}" />
			<g:hiddenField id="assetTotal" data-dojo-type="dijit/form/ValidationTextBox" name="assetTotal" value="${String.format("%.2f", assetRepair?.assetTotal)}" />
			<div style="display:none">
				<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${assetRepair?.id }"' />
	        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
			</div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"登记信息",toggleable:false,moreText:"",height:"150px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>申请单号：</div></td>
					    <td width="250">
                           	<input id="seriesNo" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"seriesNo",${fieldAcl.isReadOnly("seriesNo")},
                               	trim:true,
                               	required:true,
                               	readOnly:true,
             					value:"${assetRepair?.seriesNo}"
                           	'/>
			            </td>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>申请日期：</div></td>
					    <td width="250">
					    	<input id="applyDate" data-dojo-type="dijit/form/DateTextBox" 
                               	data-dojo-props='name:"applyDate",${fieldAcl.isReadOnly("applyDate")},
                               	trim:true,
                               	readOnly:true,
                               	value:"${assetRepair?.getFormattedShowApplyDate()}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>使用部门：</div></td>
					   	<td width="250">
					    	<input id="usedDepartName" data-dojo-type="dijit/form/ValidationTextBox" 
				               	data-dojo-props='name:"usedDepartName",${fieldAcl.isReadOnly("usedDepartName")},
				               	trim:true,
				               	required:true,
				               	readOnly:true,
								value:"${assetRepair?.getUsedDepartName()}"
				          	'/>
				         	<g:hiddenField name="usedDepartId" value="${assetRepair?.usedDepart?.id }" />
				         	<g:if test="${assetRepair?.dataStatus=='未审批'}">
								<button data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){rosten.selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}",false,"usedDepartName","usedDepartId")}'>选择</button>
			           		</g:if>
			           </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>使用人：</div></td>
					    <td>
					    	<input id="usedMan" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"usedMan",${fieldAcl.isReadOnly("usedMan")},
                               	trim:true,
                               	required:true,
                               	${assetRepair?.dataStatus!='未审批'?'readOnly:true,':'' }
             					value:"${assetRepair?.usedMan}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>报修原因：</div></td>
					    <td>
					    	<input id="repairReason" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='id:"repairReason",name:"repairReason",${fieldAcl.isReadOnly("repairReason")},
                               	trim:true,
                               	required:true,
                               	${assetRepair?.dataStatus!='未审批'?'readOnly:true,':'' }
             					value:"${assetRepair?.repairReason}"
                           	'/>
			            </td>
			            <td ><div align="right"><span style="color:red">*&nbsp;</span>预约维修时间：</div></td>
						<td>
						    <input id="expectDate" data-dojo-type="dijit/form/DateTextBox" 
	    						data-dojo-props='name:"expectDate",${fieldAcl.isReadOnly("expectDate")},
	                            trim:true,
	                            required:true,
	                            value:"${assetRepair?.getFormattedShowExpectDate()}"
	                         '/>
						</td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>联系人：</div></td>
					    <td>
					    	<input id="contacts" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"contacts",${fieldAcl.isReadOnly("contacts")},
                               	trim:true,
                               	required:true,
                               	${assetRepair?.dataStatus!='未审批'?'readOnly:true,':'' }
             					value:"${assetRepair.contacts==null?user.chinaName:assetRepair.contacts}"
                           	'/>
			            </td>
			            <td ><div align="right"><span style="color:red">*&nbsp;</span>联系电话：</div></td>
						<td>
						    <input id="contactPhone" data-dojo-type="dijit/form/ValidationTextBox" 
	    						data-dojo-props='name:"contactPhone",${fieldAcl.isReadOnly("contactPhone")},
	                            trim:true,
	                            required:true,
	                            ${assetRepair?.dataStatus!='未审批'?'readOnly:true,':'' }
	                            value:"${assetRepair?.contactPhone}"
	                         '/>
						</td>
					</tr>
					<tr>
						<td><div align="right">存放地点：</div></td>
					    <td colspan="3">
					    	<input id="storagePosition" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"storagePosition",${fieldAcl.isReadOnly("storagePosition")},
                               	trim:true,
             					value:"${assetRepair?.storagePosition}"
                           	'/>
			            </td>
					</tr>
				</table>
			</div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"维护信息",toggleable:false,moreText:"",height:"90px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right">维护人：</div></td>
					    <td width="250">
                           	<input id="maintenanceMan" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"maintenanceMan",${fieldAcl.isReadOnly("maintenanceMan")},
                               	trim:true,
             					value:"${assetRepair?.maintenanceMan}"
                           	'/>
			            </td>
					    <td width="120"><div align="right">维护联系电话：</div></td>
					    <td width="250">
                           	<input id="maintenancePhone" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"maintenancePhone",${fieldAcl.isReadOnly("maintenancePhone")},
                               	trim:true,
             					value:"${assetRepair?.maintenancePhone}"
                           	'/>
			            </td>
					</tr>
					<tr>
					    <td><div align="right">维护时间：</div></td>
					    <td>
					    	<input id="maintenanceDate" data-dojo-type="dijit/form/DateTextBox" 
                               	data-dojo-props='name:"maintenanceDate",${fieldAcl.isReadOnly("maintenanceDate")},
                               	trim:true,
             					value:"${assetRepair?.getFormattedShowMaintenanceDate()}"
                           	'/>
			            </td>
					    <td><div align="right">维护费用：</div></td>
					   <td width="250">
					    	<input id="maintenanceCost" data-dojo-type="dijit/form/ValidationTextBox" 
				               	data-dojo-props='name:"maintenanceCost",${fieldAcl.isReadOnly("maintenanceCost")},
				               	trim:true,
								value:"${assetRepair?.maintenanceCost}"
				          	'/>
			           </td>
					</tr>
					<tr>
						<td><div align="right">是否修复：</div></td>
					    <td>
					    	<select id="repairComplete" data-dojo-type="dijit/form/FilteringSelect"
                           		data-dojo-props='name:"repairComplete",${fieldAcl.isReadOnly("repairComplete")},
                           		autoComplete:false,
                           		trim:true,
            					value:"${assetRepair?.repairComplete}"
                            '>
	                            <option value="未修复">未修复</option>
								<option value="已修复">已修复</option>
								<option value="无法修复">无法修复</option>
                           	</select>
			            </td>
			            <td ><div align="right">维护反馈：</div></td>
						<td>
						    <input id="feedback" data-dojo-type="dijit/form/ValidationTextBox" 
	    						data-dojo-props='name:"feedback",${fieldAcl.isReadOnly("feedback")},
	                            trim:true,
	                            value:"${assetRepair?.feedback}"
	                         '/>
						</td>
					</tr>
				</table>
			</div>
			<g:if test="${assetRepair?.dataStatus=='未审批'}">
			<button data-dojo-type='dijit.form.Button' 
				data-dojo-props="label:'添加',iconClass:'docCloseIcon'">
				<script type="dojo/method" data-dojo-event="onClick">
					addAsset();
				</script>
			</button>
			<button data-dojo-type='dijit.form.Button' 
				data-dojo-props="label:'删除',iconClass:'docCloseIcon'">
				<script type="dojo/method" data-dojo-event="onClick">
					deleteAsset();
				</script>
			</button>
			<div style="height:5px;"></div>
			</g:if>
			<div id="assetRepairList" data-dojo-type="dijit.layout.ContentPane" data-dojo-props='style:"width:780px;height:310px;padding:2px;overflow:auto;"'>
				<div data-dojo-type="rosten/widget/RostenGrid" id="assetRepairListGrid" data-dojo-id="assetRepairListGrid"
					data-dojo-props='imgSrc:"${resource(dir:'images/rosten/share',file:'wait.gif')}",url:"${createLink(controller:'assetRepair',action:'assetRepairListDataStore',params:[companyId:company?.id,seriesNo:assetRepair?.seriesNo])}"'></div>
			</div>
		</form>
	</div>
	<g:if test="${assetRepair?.id}">
		<div data-dojo-type="dijit/layout/ContentPane" id="flowComment" title="流转意见" data-dojo-props='refreshOnShow:true,
			href:"${createLink(controller:'share',action:'getCommentLog',id:assetRepair?.id)}"
		'>	
		</div>
		<div data-dojo-type="dijit/layout/ContentPane" id="flowLog" title="流程跟踪" data-dojo-props='refreshOnShow:true,
			href:"${createLink(controller:'share',action:'getFlowLog',id:assetRepair?.id,params:[processDefinitionId:assetRepair?.processDefinitionId,taskId:assetRepair?.taskId])}"
		'>	
		</div>
	</g:if>
</div> 
</body>
</html>