<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>资产变动--资产调拨</title>
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
    	"rosten/app/AssetShareManage",
    	"rosten/kernel/behavior"],
		function(parser,dom,kernel,lang,registry){
			kernel.addOnLoad(function(){
				rosten.init({webpath:"${request.getContextPath()}",dojogridcss : true});
				rosten.cssinit();
			});
		
			assetAllocate_save = function(object){
				var originalDepartName = dojo.byId("originalDepartName").value;
				if(originalDepartName == "" || originalDepartName == null){
					rosten.alert("注意：原部门不能为空！");
					document.getElementById("originalDepartName").focus();
					return;
				}
				var originalUser = dojo.byId("originalUser").value;
				if(originalUser == "" || originalUser == null){
					rosten.alert("注意：原负责人不能为空！");
					document.getElementById("originalUser").focus();
					return;
				}
				var newDepartName = dojo.byId("newDepartName").value;
				if(newDepartName == "" || newDepartName == null){
					rosten.alert("注意：新部门不能为空！");
					document.getElementById("newDepartName").focus();
					return;
				}
				var newUser = dojo.byId("newUser").value;
				if(newUser == "" || newUser == null){
					rosten.alert("注意：新负责人不能为空！");
					document.getElementById("newUser").focus();
					return;
				}
				var applyDesc = dojo.byId("applyDesc").value;
				if(applyDesc == "" || applyDesc == null){
					rosten.alert("注意：申请理由不能为空！");
					document.getElementById("applyDesc").focus();
					return;
				}

				var assetTotal = dojo.byId("assetTotal").value;
				if(assetTotal == 0 || assetTotal == "" || assetTotal == null){
					rosten.alert("注意：请添加资产信息！");
					document.getElementById("assetTotal").focus();
					return;
				}

				//if(dijit.byId("assetCategoryChoose_pane")){
				//	var node_pane = dijit.byId("assetCategoryChoose_pane");
				//	tabContainer.removeChild(node_pane);
				//	node_pane.destroyRecursive();
				//}
				
				//新增是否同类型资产变更start--2014-11-15
				var searchQuery = {id:"*"};
				var categoryId = "";
				var grid = dijit.byId("assetAllocateListGrid");
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
				var seriesNo = "${assetAllocate?.seriesNo}";
				qSeriesNo = "?seriesNo="+encodeURI(seriesNo);

				var url = "${createLink(controller:'assetAllocate',action:'assetAllocateSaveCheck')}";
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
							content.categoryId = encodeURI(categoryId);
							content.assetTotal = assetTotal;
							<g:if test='${flowCode}'>
								content.flowCode = "${flowCode}";
								content.relationFlow = "${relationFlow}";
							</g:if>
							
							rosten.readSync(rosten.webPath + "/assetAllocate/assetAllocateSave",content,function(data){
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
				//新增是否同类型资产变更end
			};
			
			page_quit = function(){
				rosten.pagequit();
			};	

			assetAllocate_addComment = function(){
				//flowCode为是否需要走流程，如需要，则flowCode为业务流程代码
				var commentDialog = rosten.addCommentDialog({type:"assetAllocate"});
				commentDialog.callback = function(_data){
					if(_data.content == "" || _data.content == null){
						rosten.alert("意见不能为空！");
					}else{
						var content = {dataStr:_data.content,userId:"${user?.id}",status:"${assetAllocate?.status}",flowCode:"${flowCode}"};
						rosten.readSync(rosten.webPath + "/share/addComment/${assetAllocate?.id}",content,function(data){
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
					}
				};
			};

			assetAllocate_submit = function(object,conditionObj){
				/*
				 * 从后台获取下一处理人;conditionObj为流程中排他分支使用
				 */
				//增加对多次单击的控制
				var buttonWidget = object.target;
				rosten.toggleAction(buttonWidget,true);
				
				var content = {};

				//增加对应节点上的金额控制
				if("${assetAllocate?.status}" == "新建"){
					if("${isSubDepart}"==true || "${isSubDepart}"=="true"){
						content.selectDepart = "${assetAllocate?.getOriginalDepartName()}";
					}
					if(!conditionObj){
						conditionObj = {};
					}
					conditionObj.conditionName = "IsSubDepart";
					conditionObj.conditionValue = "${isSubDepart}";
				}else if("${assetAllocate?.status}" == "调出部门审核" || "${assetAllocate?.status}" == "调入部门资产管理员审核"){
					//此种情况只在二级单位无子部门情况
					content.selectDepart = "${assetAllocate?.getNewDepartName()}";
				}

				//增加对排他分支的控制
				if(conditionObj){
					lang.mixin(content,conditionObj);
				}
				rosten.readSync("${createLink(controller:'share',action:'getSelectFlowUser',params:[userId:user?.id,taskId:assetAllocate?.taskId,drafterUsername:assetAllocate?.drafter?.username])}",content,function(data){
					if(data.dealFlow==false){
						//流程无下一节点
						assetAllocate_deal("submit",null,buttonWidget,conditionObj);
						return;
					}
					var url = "${createLink(controller:'system',action:'userTreeDataStore',params:[companyId:company?.id])}";
					if(data.dealType=="user"){
						//人员处理
						if(data.showDialog==false){
							//单一处理人
							var _data = [];
							_data.push(data.userId + ":" + data.userDepart);
							assetAllocate_deal("submit",_data,buttonWidget,conditionObj);
						}else{
							//多人，多部门处理
							url += "&type=user&user=" + data.user;
							assetAllocate_select(url,buttonWidget,conditionObj);
						}
					}else{
						//群组处理
						url += "&type=group&groupIds=" + data.groupIds;
						if(data.limitDepart){
							url += "&limitDepart="+data.limitDepart;
						}
						assetAllocate_select(encodeURI(url),buttonWidget,conditionObj);
					}

				},function(error){
					rosten.alert("系统错误，请通知管理员！");
					rosten.toggleAction(buttonWidget,false);
				});
			};

			assetAllocate_select = function(url,buttonWidget,conditionObj){
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
		            	assetAllocate_deal("submit",_data,buttonWidget,conditionObj);
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
		
			assetAllocate_deal = function(type,readArray,buttonWidget,conditionObj){
				var content = {};
				content.id = "${assetAllocate?.id}";
				content.status = "${assetAllocate?.status}";
				content.deal = type;
				if(readArray){
					content.dealUser = readArray.join(",");
				}
				if(conditionObj){
					lang.mixin(content,conditionObj);
				}
				rosten.readSync(rosten.webPath + "/assetAllocate/assetAllocateFlowDeal",content,function(data){
					if(data.result == "true" || data.result == true){
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
								
								if(data.refresh == "true" || data.refresh == true){
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
		
			assetAllocate_back = function(object,conditionObj){
				//增加对多次单击的控制
				var buttonWidget = object.target;
				rosten.toggleAction(buttonWidget,true);
				
				var content = {};
				rosten.readSync("${createLink(controller:'assetAllocate',action:'assetAllocateFlowBack',params:[id:assetAllocate?.id])}",content,function(data){
					if(data.result == "true" || data.result == true){
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

				var qcontrolName = "&controlName=assetAllocate";
				
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
				var grid = dijit.byId("assetAllocateListGrid");
				
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

				var allocateId = "${assetAllocate?.id}";
				var assetTotal = dojo.byId("assetTotal").value;
				var originalUser = dojo.byId("originalUser").value;
				var qSeriesNo = "";
				var seriesNo = "${assetAllocate?.seriesNo}";
				qSeriesNo = "&seriesNo="+encodeURI(seriesNo);
				
				var url = "${createLink(controller:'assetAllocate',action:'assetChooseDelete')}";
				url += "?assetId="+encodeURI(assetId)+"&allocateId="+allocateId+"&assetTotal="+assetTotal+"&originalUser="+originalUser+qSeriesNo;
				var ioArgs = {
					url : url,
					handleAs : "json",
					load : function(response,args) {
						if(response.result=="true"){//rensult为true，后台数据操作成功并返回总金额，同时刷新父页面Grid
							var assetTotal = response.assetTotal;
							dojo.byId("assetTotal").value = assetTotal.toFixed(2);
		
							var url_twice = "${createLink(controller:'assetAllocate',action:'assetAllocateListDataStore')}";
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
							var grid_twice = dijit.byId("assetAllocateListGrid");
							
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
			refreshAsset = function(){
				assetAllocateListGrid.refresh(assetAllocateListGrid.defaultUrl);
			};
			assetCategoryChoose_search = function(){
				var controlName = "assetAllocate";
				var url = "${createLink(controller:'assetCategoryChoose',action:'assetCategoryChooseListDataStore')}";
				assetCategoryChoose_search_common(url,controlName,"${company?.id}","${assetAllocate?.seriesNo}");
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
					
					var qControlName = "&controlName=assetAllocate";

					var qSeriesNo = "";
					var seriesNo = "${assetAllocate?.seriesNo}";
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
								var url_twice = "${createLink(controller:'assetAllocate',action:'assetAllocateListDataStore')}";
								var qCompany = "";
								var compamyId;
								<g:if test="${company?.id}">
									compamyId = "${company?.id}";
									qCompany = "?companyId="+encodeURI(compamyId);
								</g:if>

								var qSeriesNo = "";
								var seriesNo = "${assetAllocate?.seriesNo}";
								qSeriesNo = "&seriesNo="+encodeURI(seriesNo);
			
								url_twice += qCompany+qSeriesNo;
								
								//处理相关表单的显示数据-----------22015-1-11---------------------------------------
								var assetTotal = response.assetTotal;
								dojo.byId("assetTotal").value = assetTotal.toFixed(2);
								
								var originalDepartName = registry.byId("originalDepartName");
								if(originalDepartName.get("value")==""){
									originalDepartName.set("value",store.getValue(selected[0], "userDepart"));
									registry.byId("originalDepartId").set("value",store.getValue(selected[0], "userDepartId"));
								}
								
								var originalUser = registry.byId("originalUser");
								if(originalUser.get("value")==""){
									originalUser.set("value",store.getValue(selected[0], "assetUser"));
								}
								
								//----------------------------------------------------------------
								
								//刷新资产筛选页面-------2015-1-14---去除此功能，直接刷新主页面的列表数据
								//assetCategoryChoose_search();
								rosten.alert("资产已成功添加！").queryDlgClose= function(){
									assetCategoryChoose_close();
									var tabContainer_w = dijit.byId("rosten_tabContainer");
									var node_w = registry.byId("assetAllocatePane");
									tabContainer_w.selectChild(node_w);
									refreshAsset();
								};
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
			};
		});
		
	</script>
</head>
<body>
<div class="rosten_action">
	<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="rosten_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'assetAllocate',action:'assetAllocateForm',id:assetAllocate?.id,params:[userid:user?.id])}"'>
	</div>
</div>

<div id="rosten_tabContainer" data-dojo-id="rosten_tabContainer" data-dojo-type="dijit/layout/TabContainer" data-dojo-props='doLayout:false,persist:false, tabStrip:true,style:{width:"800px",height:"680px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" id="assetAllocatePane" title="申请信息" data-dojo-props='doLayout:false,height:"550px",marginBottom:"2px",region:"top",style:{padding:"4px"}'>
		<form id="rosten_form" name="rosten_form" onsubmit="return false;" class="rosten_form" style="padding:0px">
			<g:hiddenField id="seriesNo_form" name="seriesNo_form" value="${assetAllocate?.seriesNo}" />
			<g:hiddenField id="applyMan" name="applyMan" value="${assetAllocate?.applyMan == null?user.chinaName:assetAllocate?.applyMan}" />
			<div style="display:none">
				<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${assetAllocate?.id }"' />
	        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
			</div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"登记信息",toggleable:false,moreText:"",height:"210px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>申请单号：</div></td>
					    <td width="250">
                           	<input id="seriesNo" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"seriesNo",${fieldAcl.isReadOnly("seriesNo")},
                               		trim:true,
                               		required:true,
                               		readOnly:true,disabled:true,
             						value:"${assetAllocate?.seriesNo}"
                           	'/>
			            </td>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>申请日期：</div></td>
					    <td width="250">
					    	<input id="applyDate" data-dojo-type="dijit/form/DateTextBox" 
                               	data-dojo-props='name:"applyDate",${fieldAcl.isReadOnly("applyDate")},
                               	trim:true,
                               	readOnly:true,
                               	value:"${assetAllocate?.getFormattedShowApplyDate()}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>原部门：</div></td>
					   	<td width="250">
					    	<input id="originalDepartName" data-dojo-type="dijit/form/ValidationTextBox" 
				               	data-dojo-props='name:"originalDepartName",${fieldAcl.isReadOnly("originalDepartName")},
				               		trim:true,
				               		required:true,
				               		readOnly:true,
									value:"${assetAllocate?.getOriginalDepartName()}"
				          	'/>
				         	<g:hiddenField id="originalDepartId" data-dojo-type="dijit/form/ValidationTextBox"  name="originalDepartId" value="${assetAllocate?.originalDepart?.id }" />
				         	<g:if test="${isAllowedEdit in ['new','yes']}">
								<button data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){rosten.selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}",false,"originalDepartName","originalDepartId")}'>选择</button>
							</g:if>
						</td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>原负责人：</div></td>
					    <td>
					    	<input id="originalUser" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"originalUser",${fieldAcl.isReadOnly("originalUser")},
                               		trim:true,
                               		required:true,
                               		${isAllowedEdit in ['new','yes']?'':'readOnly:true,'}
             						value:"${assetAllocate?.originalUser}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>新部门：</div></td>
					    <td>
					    	<input id="newDepartName" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"newDepartName",${fieldAcl.isReadOnly("newDepartName")},
                               		trim:true,
                               		required:true,
                               		readOnly:true,
             						value:"${assetAllocate?.getNewDepartName()}"
                           	'/>
                           	<g:hiddenField name="newDepartId" value="${assetAllocate?.newDepart?.id }" />
                           	<g:if test="${isAllowedEdit in ['new','yes']}">
								<button data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){rosten.selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}",false,"newDepartName","newDepartId")}'>选择</button>
			            	</g:if>
			            </td>
						<td><div align="right"><span style="color:red">*&nbsp;</span>新负责人：</div></td>
					    <td>
					    	<input id="newUser" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"newUser",${fieldAcl.isReadOnly("newUser")},
                               		trim:true,
                               		required:true,
                               		${isAllowedEdit in ['new','yes']?'':'readOnly:true,'} 
             						value:"${assetAllocate?.newUser}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>资产总和：</div></td>
					    <td colspan="3">
					    	<input id="assetTotal" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='id:"assetTotal",name:"assetTotal",${fieldAcl.isReadOnly("assetTotal")},
                               		trim:true,
                               		required:true,
                               		readOnly:true,
             						value:"${String.format("%.2f", assetAllocate?.assetTotal)}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td ><div align="right"><span style="color:red">*&nbsp;</span>申请理由：</div></td>
						<td colspan="3">
					    	<input id="applyDesc" data-dojo-type="dijit/form/ValidationTextBox" 
    							data-dojo-props='id:"applyDesc",name:"applyDesc",${fieldAcl.isReadOnly("applyDesc")},
                               		trim:true,
                               		required:true,
                               		${isAllowedEdit in ['new','yes']?'':'readOnly:true,'}
                               		style:{width:"550px",height:"80px"},
                               		value:"${assetAllocate?.applyDesc}"
                           '/>
					    </td>
					</tr>
				</table>
			</div>
			<g:if test="${isAllowedEdit in ['new','yes']}">
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
			<button data-dojo-type='dijit.form.Button' 
				data-dojo-props="label:'刷新',iconClass:'docRefreshIcon'">
				<script type="dojo/method" data-dojo-event="onClick">
					refreshAsset();
				</script>
			</button>
			<div style="height:5px;"></div>
			</g:if>
			
		</form>
		<div id="assetAllocateList" data-dojo-type="dijit.layout.ContentPane" data-dojo-props='style:"width:780px;padding:2px"'>
			<div data-dojo-type="rosten/widget/RostenGrid" id="assetAllocateListGrid" data-dojo-id="assetAllocateListGrid"
				data-dojo-props='showRowSelector:"new",imgSrc:"${resource(dir:'images/rosten/share',file:'wait.gif')}",url:"${createLink(controller:'assetAllocate',action:'assetAllocateListDataStore',params:[companyId:company?.id,seriesNo:assetAllocate?.seriesNo])}"'></div>
		</div>
	</div>
	<g:if test="${assetAllocate?.id}">
		<div data-dojo-type="dijit/layout/ContentPane" id="flowComment" title="流转意见" data-dojo-props='doLayout:false,refreshOnShow:true,
			href:"${createLink(controller:'share',action:'getCommentLog',id:assetAllocate?.id)}"
		'>	
		</div>
		<div data-dojo-type="dijit/layout/ContentPane" id="flowLog" title="流程跟踪" data-dojo-props='doLayout:false,refreshOnShow:true,
			href:"${createLink(controller:'share',action:'getFlowLog',id:assetAllocate?.id,params:[processDefinitionId:assetAllocate?.processDefinitionId,taskId:assetAllocate?.taskId])}"
		'>	
		</div>
	</g:if>
</div> 
</body>
</html>