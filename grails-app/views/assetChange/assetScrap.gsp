<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>资产变动--报废报损</title>
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
			"dijit/layout/ContentPane",
			"dijit/form/ValidationTextBox",
			"dijit/form/DateTextBox",
			"dijit/form/SimpleTextarea",
			"dijit/form/Button",
			"dijit/form/Select",
			"dijit/Dialog",
			"dojox/grid/DataGrid",
			"dojox/widget/Wizard",
			"dojox/widget/WizardPane",
	    	"rosten/widget/ActionBar",
	    	"rosten/widget/TitlePane",
	    	"rosten/app/Application",
	    	"rosten/app/SystemApplication",
	    	"rosten/app/BookKeeping",
	    	"rosten/kernel/behavior"],
			function(parser,dom,kernel,lang,registry){
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}",dojogridcss : true});
					rosten.cssinit();
				});
			
				assetScrap_save = function(object){
					var assetTotal = dojo.byId("assetTotal").value;
					if(assetTotal==0){
						alert("注意：请添加资产信息！");
						document.getElementById("assetTotal").focus();
						return;
					}
					var applyDesc = dojo.byId("applyDesc").value;
					if(applyDesc=="" || applyDesc==null){
						alert("注意：请填写申请理由！");
						document.getElementById("applyDesc").focus();
						return;
					}
					
					//新增是否同类型资产变更start--2014-10-31
					var searchQuery = {id:"*"};
					var categoryId = "";
					var grid = dijit.byId("assetScrapListGrid");
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

					var url = "${createLink(controller:'assetScrap',action:'assetScrapSaveCheck')}";
					url += "?categoryId="+encodeURI(categoryId);
					var ioArgs = {
						url : url,
						handleAs : "json",
						load : function(response,args) {
							if(response.result=="false"){//rensult为false，资产列表为不同类型资产
								alert("注意：资产列表只能为同类型资产！");
								return;
							}else{//rensult为true，资产列表为同类型资产，继续
								//增加对多次单击的次数----2014-9-4
								var buttonWidget = object.target;
								rosten.toggleAction(buttonWidget,true);

								//流程相关信息
								var content = {};
								<g:if test='${flowCode}'>
									content.flowCode = "${flowCode}";
									content.relationFlow = "${relationFlow}";
								</g:if>
								
								rosten.readSync(rosten.webPath + "/assetScrap/assetScrapSave",content,function(data){
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
							alert(response.message);
							return;
						}
					};
					dojo.xhrPost(ioArgs);
					//新增是否同类型资产变更end
				};
				
				page_quit = function(){
					rosten.pagequit();
				};	

				assetScrap_addComment = function(){
					//flowCode为是否需要走流程，如需要，则flowCode为业务流程代码
					var commentDialog = rosten.addCommentDialog({type:"assetScrap"});
					commentDialog.callback = function(_data){
						var content = {dataStr:_data.content,userId:"${user?.id}",status:"${assetScrap?.status}",flowCode:"${flowCode}"};
						rosten.readSync(rosten.webPath + "/share/addComment/${assetScrap?.id}",content,function(data){
							if(data.result=="true" || data.result == true){
								rosten.alert("意见已填写！");
							}else{
								rosten.alert("意见填写失败!");
							}		
						});
					};
				};

				assetScrap_submit = function(object,conditionObj){
					/*
					 * 从后台获取下一处理人;conditionObj为流程中排他分支使用
					 */
					//增加对多次单击的控制
					var buttonWidget = object.target;
					rosten.toggleAction(buttonWidget,true);
					
					var content = {};

					//增加对应节点上的设备类型控制
					if("${assetScrap?.status}" == "部门领导审核"){
						if(!conditionObj){
							conditionObj = {};
						}
						conditionObj.conditionName = "assetType";
						conditionObj.conditionValue = "${assetType}";
					}
					
					//增加对应节点上的金额控制
					if("${assetScrap?.status}" == "后勤分管领导审核"){
						if(!conditionObj){
							conditionObj = {};
						}
						conditionObj.conditionName = "money";
						conditionObj.conditionValue = ${assetScrap?.assetTotal};
					}
					
					//增加对排他分支的控制
					if(conditionObj){
						lang.mixin(content,conditionObj);
					}
					rosten.readSync("${createLink(controller:'share',action:'getSelectFlowUser',params:[userId:user?.id,taskId:assetScrap?.taskId,drafterUsername:assetScrap?.drafter?.username])}",content,function(data){
						if(data.dealFlow==false){
							//流程无下一节点
							assetScrap_deal("submit",null,buttonWidget,conditionObj);
							return;
						}
						var url = "${createLink(controller:'system',action:'userTreeDataStore',params:[companyId:company?.id])}";
						if(data.dealType=="user"){
							//人员处理
							if(data.showDialog==false){
								//单一处理人
								var _data = [];
								_data.push(data.userId + ":" + data.userDepart);
								assetScrap_deal("submit",_data,buttonWidget,conditionObj);
							}else{
								//多人，多部门处理
								url += "&type=user&user=" + data.user;
								assetScrap_select(url,buttonWidget,conditionObj);
							}
						}else{
							//群组处理
							url += "&type=group&groupIds=" + data.groupIds;
							if(data.limitDepart){
								url += "&limitDepart="+data.limitDepart;
							}
							assetScrap_select(encodeURI(url),buttonWidget,conditionObj);
						}
	
					},function(error){
						rosten.alert("系统错误，请通知管理员！");
						rosten.toggleAction(buttonWidget,false);
					});
				};

				assetScrap_select = function(url,buttonWidget,conditionObj){
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
			            	assetScrap_deal("submit",_data,buttonWidget,conditionObj);
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
				};
			
				assetScrap_deal = function(type,readArray,buttonWidget,conditionObj){
					var content = {};
					content.id = "${assetScrap?.id}";
					content.status = "${assetScrap?.status}";
					content.deal = type;
					if(readArray){
						content.dealUser = readArray.join(",");
					}
					if(conditionObj){
						lang.mixin(content,conditionObj);
					}
					rosten.readSync(rosten.webPath + "/assetScrap/assetScrapFlowDeal",content,function(data){
						if(data.result=="true" || data.result == true){
							rosten.alert("成功！").queryDlgClose= function(){
								//刷新待办事项内容
								window.opener.showStartGtask("${user?.id}","${company?.id }");
								
								if(data.refresh=="true" || data.refresh==true){
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
				};
			
				assetScrap_back = function(object,conditionObj){
					//增加对多次单击的控制
					var buttonWidget = object.target;
					rosten.toggleAction(buttonWidget,true);
					
					var content = {};
					rosten.readSync("${createLink(controller:'assetScrap',action:'assetScrapFlowBack',params:[id:assetScrap?.id])}",content,function(data){
						if(data.result=="true" || data.result == true){
							rosten.alert("成功！").queryDlgClose= function(){
								//刷新待办事项内容
								window.opener.showStartGtask("${user}","${company?.id }");
								
								if(data.refresh=="true" || data.refresh==true){
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
				};
			});

			assetTypeSelect = function(){
				var url = "${createLink(controller:'assetScrap',action:'assetChooseListDataStore')}";
				var qs;
				var qCompany = "";
				var compamyId;
				<g:if test="${company?.id}">
					compamyId = "${company?.id}";
					qCompany = "?companyId="+encodeURI(compamyId);
				</g:if>
		
				var qAssetType = "";
				var assetType;
				var assetSel = dijit.byId("assetTypeRange");
				if(assetSel){
					if(assetSel.attr("value")!=""){
						assetType = assetSel.attr("value");
						qAssetType = "&assetType="+assetType;
					}
				}
				url += qCompany+qAssetType;
				var grid = dijit.byId("assetChooseListGrid");
				grid.url = url;
				grid.refresh();
			}
		
			addAsset = function(){
				dijit.byId("assetChooseDialog").show();
			}
	
			assetChooseDone = function(){
				var seriesNo = dijit.byId("seriesNo").attr("value");
				
				var grid = dijit.byId("assetChooseListGrid");
				var selected = grid.getSelected();
				if (selected.length == 0) {
					alert("请在资产列表中选择数据！");
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
		
				var assetType
				var assetSel = dijit.byId("assetTypeRange");
				if(assetSel){
					if(assetSel.attr("value")!=""){
						assetType = assetSel.attr("value");
					}
				}
	
				var assetTotal;
				var assettotal;
				var assetTotalSel = dijit.byId("assetTotal");
				if(assetTotalSel){
					if(assetTotalSel.attr("value") == "" || assetTotalSel.attr("value") == null){
						assetTotal = "0-0";
					}else{
						assetTotal = assetTotalSel.attr("value").replace(".","-");
					}
				}
				//异步处理所选资产数据
				var url = "${createLink(controller:'assetScrap',action:'assetChooseOperate')}";
				url += "?assetId="+encodeURI(assetId)+"&assetType="+assetType+"&seriesNo="+seriesNo+"&assetTotal="+assetTotal;
				var ioArgs = {
					url : url,
					handleAs : "json",
					load : function(response,args) {
						if(response.result=="true"){//rensult为true，后台数据操作成功并返回总金额，同时刷新父页面Grid
							var assetTotal = response.assetTotal;
							dojo.byId("assetTotal").value = assetTotal;
		
							var url_twice = "${createLink(controller:'assetScrap',action:'assetScrapListDataStore')}";
							var qs;
							var qCompany = "";
							var compamyId;
							<g:if test="${company?.id}">
								compamyId = "${company?.id}";
								qCompany = "?companyId="+encodeURI(compamyId);
							</g:if>
		
							var qAssetType = "";
							var assetType;
							var assetSel = dijit.byId("assetTypeRange");
							if(assetSel){
								if(assetSel.attr("value")!=""){
									assetType = assetSel.attr("value");
									qAssetType = "&assetType="+assetType;
								}
							}
		
							var qFreshType = "&freshType=twice";
		
							var qSeriesNo = "&seriesNo="+seriesNo;
							
							url_twice += qCompany+qSeriesNo;
							var grid_twice = dijit.byId("assetScrapListGrid");
							
							grid_twice.url = url_twice;
							grid_twice.refresh();
						}else{//rensult为false，处理失败
							alert("操作失败，请联系管理员!");
							return;
						}
					},
					error : function(response,args) {
						alert(response.message);
						return;
					}
				};
				dojo.xhrPost(ioArgs);
				dijit.byId("assetChooseDialog").hide();
			}
	
			deleteAsset = function(){
				var grid = dijit.byId("assetScrapListGrid");
				var selected = grid.getSelected();
				if (selected.length == 0) {
					alert("请在资产列表中选择数据！");
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
	
				var scrapId = "${assetScrap?.id}";
				var seriesNo = "${assetScrap?.seriesNo}";
				var assetTotal = dojo.byId("assetTotal").value;
				var url = "${createLink(controller:'assetScrap',action:'assetChooseDelete')}";
				url += "?assetId="+encodeURI(assetId)+"&scrapId="+scrapId+"&assetTotal="+assetTotal;
				var ioArgs = {
					url : url,
					handleAs : "json",
					load : function(response,args) {
						if(response.result=="true"){//rensult为true，后台数据操作成功并返回总金额，同时刷新父页面Grid
							var assetTotal = response.assetTotal;
							dojo.byId("assetTotal").value = assetTotal;
		
							var url_twice = "${createLink(controller:'assetScrap',action:'assetScrapListDataStore')}";
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
							var grid_twice = dijit.byId("assetScrapListGrid");
							
							grid_twice.url = url_twice;
							grid_twice.refresh();
						}else{//rensult为false，处理失败
							alert("操作失败，请联系管理员!");
							return;
						}
					},
					error : function(response,args) {
						alert(response.message);
						return;
					}
				};
				dojo.xhrPost(ioArgs);
			}
		
    </script>
</head>
<body>
<div class="rosten_action">
	<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="rosten_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'assetScrap',action:'assetScrapForm',id:assetScrap?.id,params:[userid:user?.id])}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="申请信息" data-dojo-props='height:"460px",marginBottom:"2px",region:"top"'>
		<form id="rosten_form" name="rosten_form" onsubmit="return false;" class="rosten_form" style="padding:0px">
			<g:hiddenField name="seriesNo_form" value="${assetScrap?.seriesNo}" />
			<div style="display:none">
				<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${assetScrap?.id }"' />
	        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
			</div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"登记信息",toggleable:false,moreText:"",height:"90px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>申请单号：</div></td>
					    <td width="250">
                           	<input id="seriesNo" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"seriesNo",${fieldAcl.isReadOnly("seriesNo")},
                               		trim:true,
                               		required:true,disabled:"disabled",
             						value:"${assetScrap?.seriesNo}"
                           	'/>
			            </td>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>申请日期：</div></td>
					    <td width="250">
					    	<input id="applyDate" data-dojo-type="dijit/form/DateTextBox" 
                               	data-dojo-props='name:"applyDate",trim:true,${fieldAcl.isReadOnly("applyDate")},
                               		value:"${assetScrap?.getFormattedShowApplyDate()}"
                           	'/>
			            </td>
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>申请人：</div></td>
					    <td>
					    	<input id="applyMan" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"applyMan",${fieldAcl.isReadOnly("applyMan")},
                               		trim:true,
                               		required:true,
             						value:"${user?.chinaName}"
                           	'/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>申请部门：</div></td>
					   	<td width="250">
					    	<input id="allowdepartsName" data-dojo-type="dijit/form/ValidationTextBox" 
				               	data-dojo-props='name:"allowdepartsName",${fieldAcl.isReadOnly("allowdepartsName")},
				               		trim:true,
				               		required:true,
									value:"${assetScrap?.getDepartName()}"
				          	'/>
				         	<g:hiddenField name="allowdepartsId" value="${assetScrap?.applyDept?.id }" />
							<button data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}")}'>选择</button>
			           </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>资产总和：</div></td>
					    <td>
					    	<input id="assetTotal" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='id:"assetTotal",name:"assetTotal",${fieldAcl.isReadOnly("assetTotal")},
                               		trim:true,
                               		required:true,
                               		
             						value:"${assetScrap?.assetTotal}"
                           	'/>
			            </td>
			            <td ><div align="right"><span style="color:red">*&nbsp;</span>申请理由：</div></td>
						<td>
						    <input id="applyDesc" data-dojo-type="dijit/form/ValidationTextBox" 
	    						data-dojo-props='id:"applyDesc",name:"applyDesc",${fieldAcl.isReadOnly("applyDesc")},
	                               	trim:true,
	                               	required:true,
	                               	value:"${assetScrap?.applyDesc}"
	                         '/>
						</td>
					</tr>
				</table>
			</div>
			<g:if test="${assetScrap?.dataStatus=='未审批'}">
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
			<div id="assetScrapList" data-dojo-type="dijit.layout.ContentPane" data-dojo-props='style:"width:780px;height:300px;padding:2px;"'>
				<div data-dojo-type="rosten/widget/RostenGrid" id="assetScrapListGrid" data-dojo-id="assetScrapListGrid"
					data-dojo-props='url:"${createLink(controller:'assetScrap',action:'assetScrapListDataStore',params:[companyId:company?.id,seriesNo:assetScrap?.seriesNo])}"'></div>
			</div>
		</form>
	</div>
	<g:if test="${assetScrap?.id}">
		<div data-dojo-type="dijit/layout/ContentPane" id="flowComment" title="流转意见" data-dojo-props='refreshOnShow:true,
			href:"${createLink(controller:'share',action:'getCommentLog',id:assetScrap?.id)}"
		'>	
		</div>
		<div data-dojo-type="dijit/layout/ContentPane" id="flowLog" title="流程跟踪" data-dojo-props='refreshOnShow:true,
			href:"${createLink(controller:'share',action:'getFlowLog',id:assetScrap?.id,params:[processDefinitionId:assetScrap?.processDefinitionId,taskId:assetScrap?.taskId])}"
		'>	
		</div>
	</g:if>
</div> 
<div id="assetChooseDialog" data-dojo-type="dijit.Dialog" class="displayLater" data-dojo-props="title:'资产筛选',style:'width:855px;height:455px'">
	<div id="assetChooseWizard" data-dojo-type="dojox.widget.Wizard" style="width:850px; height:415px; margin:5 auto;">
		<div data-dojo-type="dojox.widget.WizardPane" id="assetSelectWizardPane" style="height:360px;border:1px;" >
			<div>
				<label>请选择资产类别：</label>
				<select data-dojo-type="dijit.form.Select" data-dojo-props="id:'assetTypeRange',onChange:assetTypeSelect,style:'width:180px'">
					<option value="car">车辆</option>
					<option value="land">土地</option>
					<option value="house">房屋</option>
					<option value="device">设备</option>
					<option value="book">图书</option>
					<option value="furniture">家具</option>
				</select>
			</div>
		</div>
		<div data-dojo-type="dojox.widget.WizardPane" data-dojo-props='canGoBack:"true",doneFunction:assetChooseDone' >
			<div id="chooseAsset">
				<div id="assetChooseList" data-dojo-type="dijit.layout.ContentPane" data-dojo-props='style:"width:820px;height:360px;padding:2px;"'>
					<div data-dojo-type="rosten/widget/RostenGrid" id="assetChooseListGrid" data-dojo-id="assetChooseListGrid"
						data-dojo-props='url:"${createLink(controller:'assetScrap',action:'assetChooseListDataStore',params:[companyId:company?.id])}"'></div>
				</div>
			</div>
		</div>
		<script type="dojo/method" event="cancelFunction">
				dijit.byId("assetChooseDialog").hide();
			</script>
	</div>
</div>
</body>