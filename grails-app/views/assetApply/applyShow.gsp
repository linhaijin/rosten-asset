<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>资产申请</title>
    <link rel="stylesheet" href="${createLinkTo(dir:'js/dojox/widget/Wizard',file:'Wizard.css') }"></link>
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
	        "dojo/_base/lang",
	 		"dojo/_base/kernel",
	 		"dijit/registry",
	 		"dijit/layout/TabContainer",
	 		"dijit/layout/ContentPane",
	 		"dijit/form/ValidationTextBox",
	 		"dijit/form/DateTextBox",
	 		"dijit/form/SimpleTextarea",
	 		"dijit/form/Button",
	 		"dijit/form/Select",
	     	"rosten/widget/ActionBar",
	     	"rosten/widget/TitlePane",
	     	"rosten/app/Application",
	     	"rosten/app/SystemApplication",
	     	"rosten/kernel/behavior"],
		function(parser,dom,lang,kernel,registry){
			kernel.addOnLoad(function(){
				rosten.init({webpath:"${request.getContextPath()}"});
				rosten.cssinit();
			});
			
			assetApply_save = function(object){
				var allowdepartsName = dijit.byId("allowdepartsName").attr("value");
				if(allowdepartsName == "" || allowdepartsName == null){
					rosten.alert("注意：申请部门不能为空！");
					dijit.byId("allowdepartsName").focus();
					return;
				}
				var allowCategoryName = dijit.byId("allowCategoryName").attr("value");
				if(allowCategoryName == "" || allowCategoryName == null){
					rosten.alert("注意：资产分类不能为空！");
					dijit.byId("allowCategoryName").focus();
					return;
				}
				var assetName = dijit.byId("assetName").attr("value");
				if(assetName == "" || assetName == null){
					rosten.alert("注意：资产名称不能为空！");
					dijit.byId("assetName").focus();
					return;
				}
				var userName = dijit.byId("userName").attr("value");
				if(userName == "" || userName == null){
					rosten.alert("注意：使用人不能为空！");
					dijit.byId("userName").focus();
					return;
				}
				var onePrice = dijit.byId("onePrice").attr("value");
				if(onePrice == 0 || onePrice == "" || onePrice == null){
					rosten.alert("注意：单价不能为0！");
					dijit.byId("onePrice").focus();
					return;
				}
				//增加对多次单击的次数----2014-9-4
				var buttonWidget = object.target;
				rosten.toggleAction(buttonWidget,true);

				//流程相关信息
				var content = {};
				<g:if test='${flowCode}'>
					content.flowCode = "${flowCode}";
					content.relationFlow = "${relationFlow}";
				</g:if>
				
				rosten.readSync(rosten.webPath + "/applyManage/assetApplySave",content,function(data){
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
			};

			page_quit = function(){
				rosten.pagequit();
			};

			assetApply_addComment = function(){
				//flowCode为是否需要走流程，如需要，则flowCode为业务流程代码
				var commentDialog = rosten.addCommentDialog({type:"assetApply"});
				commentDialog.callback = function(_data){
					var content = {dataStr:_data.content,userId:"${user?.id}",status:"${applyNotes?.status}",flowCode:"${flowCode}"};
					rosten.readSync(rosten.webPath + "/share/addComment/${applyNotes?.id}",content,function(data){
						if(data.result=="true" || data.result == true){
							rosten.alert("意见填写成功！");
						}else{
							rosten.alert("意见填写失败!");
						}	
					});
				};
			};
			
			assetApply_submit = function(object,conditionObj){
				/*
				 * 从后台获取下一处理人;conditionObj为流程中排他分支使用
				 */
				//增加对多次单击的控制
				var buttonWidget = object.target;
				rosten.toggleAction(buttonWidget,true);
				
				var content = {};

				//增加对应节点上的金额控制
				if("${applyNotes?.status}" == "分管领导审核" || "${applyNotes?.status}" == "秘书长审批"){
					if(!conditionObj){
						conditionObj = {};
					}
					conditionObj.conditionName = "money";
					conditionObj.conditionValue = ${applyNotes?.onePrice};
				}
				
				//增加对排他分支的控制
				if(conditionObj){
					lang.mixin(content,conditionObj);
				}
				rosten.readSync("${createLink(controller:'share',action:'getSelectFlowUser',params:[userId:user?.id,taskId:applyNotes?.taskId,drafterUsername:applyNotes?.drafter?.username])}",content,function(data){
					if(data.dealFlow==false){
						//流程无下一节点
						assetApply_deal("submit",null,buttonWidget,conditionObj);
						return;
					}
					var url = "${createLink(controller:'system',action:'userTreeDataStore',params:[companyId:company?.id])}";
					if(data.dealType=="user"){
						//人员处理
						if(data.showDialog==false){
							//单一处理人
							var _data = [];
							_data.push(data.userId + ":" + data.userDepart);
							assetApply_deal("submit",_data,buttonWidget,conditionObj);
						}else{
							//多人，多部门处理
							url += "&type=user&user=" + data.user;
							assetApply_select(url,buttonWidget,conditionObj);
						}
					}else{
						//群组处理
						url += "&type=group&groupIds=" + data.groupIds;
						if(data.limitDepart){
							url += "&limitDepart="+data.limitDepart;
						}
						assetApply_select(encodeURI(url),buttonWidget,conditionObj);
					}

				},function(error){
					rosten.alert("系统错误，请通知管理员！");
					rosten.toggleAction(buttonWidget,false);
				});
			};
			
			assetApply_select = function(url,buttonWidget,conditionObj){
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
		            	assetApply_deal("submit",_data,buttonWidget,conditionObj);
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
			
			assetApply_deal = function(type,readArray,buttonWidget,conditionObj){
				var content = {};
				content.id = "${applyNotes?.id}";
				content.status = "${applyNotes?.status}";
				content.deal = type;
				if(readArray){
					content.dealUser = readArray.join(",");
				}
				if(conditionObj){
					lang.mixin(content,conditionObj);
				}
				rosten.readSync(rosten.webPath + "/applyManage/assetApplyFlowDeal",content,function(data){
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
			};
			
			assetApply_back = function(object,conditionObj){
				//增加对多次单击的控制
				var buttonWidget = object.target;
				rosten.toggleAction(buttonWidget,true);
				
				var content = {};
				rosten.readSync("${createLink(controller:'applyManage',action:'assetApplyFlowBack',params:[id:applyNotes?.id])}",content,function(data){
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
			};
		});
    </script>
</head>
<body>
<div class="rosten_action">
	<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="rosten_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'applyManage',action:'assetApplyForm',id:applyNotes?.id,params:[userid:user?.id])}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"850px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props=''>
		<form id="rosten_form" name="rosten_form" onsubmit="return false;" class="rosten_form" style="height:450px;padding:0px">
			<g:hiddenField name="registerNum_form" value="${applyNotes?.registerNum}" />
			<div style="display:none">
				<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${applyNotes?.id }"' />
	        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
			</div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"申请信息",toggleable:false,moreText:"",height:"220px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>申请人：</div></td>
					    <td width="250">
                           	<input data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='
                             	trim:true,
                             	required:true,
                             	readOnly:true,
           						value:"${applyNotes?.getFormattedUser()}"
                           	'/>
			            </td>
			            <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>申请部门：</div></td>
					   	<td width="250">
					    	<input id="allowdepartsName" data-dojo-type="dijit/form/ValidationTextBox" 
				               	data-dojo-props='name:"allowdepartsName",${fieldAcl.isReadOnly("allowdepartsName")},
				               	trim:true,
				               	required:true,
				               	readOnly:true,
								value:"${applyNotes?.getDepartName()}"
				          	'/>
				         	<g:hiddenField name="allowdepartsId" value="${applyNotes?.userDepart?.id }" />
				         	<g:if test="${applyNotes?.applyStatus=='未审核'}">
				         		<button data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}")}'>选择</button>
				         	</g:if>
			           	</td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>资产分类：</div></td>
					    <td>
					    	<input id="allowCategoryName" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"allowCategoryName",${fieldAcl.isReadOnly("allowCategoryName")},
                               	trim:true,
                               	required:true,
                               	readOnly:true,
             					value:"${applyNotes?.getCategoryName()}"
                           	'/>
                           	<g:hiddenField name="allowCategoryId" value="${applyNotes?.userCategory?.id }" />
							<g:if test="${applyNotes?.applyStatus=='未审核'}">
				         		<button data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){selectAssetCategory("${createLink(controller:'assetConfig',action:'assetCategoryTreeDataStore',params:[companyId:company?.id])}")}'>选择</button>
				         	</g:if>
							
			           	</td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>资产名称：</div></td>
					    <td>
					    	<input id="assetName" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"assetName",${fieldAcl.isReadOnly("assetName")},
                               	trim:true,
                               	required:true,
                               	${applyNotes?.applyStatus!='未审核'?'readOnly:true,':'' }
             					value:"${applyNotes?.assetName}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>使用人：</div></td>
					   	<td>
					    	<input id="userName" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"userName",${fieldAcl.isReadOnly("userName")},
                               	trim:true,
                               	required:true,
                               	${applyNotes?.applyStatus!='未审核'?'readOnly:true,':'' }
             					value:"${applyNotes?.getFormattedUser()}"
                           	'/>
			           	</td>
						<td><div align="right"><span style="color:red">*&nbsp;</span>数量：</div></td>
					    <td>
					    	<input id="amount" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"amount",${fieldAcl.isReadOnly("amount")},
                               	trim:true,
                               	required:true,
                               	${applyNotes?.applyStatus!='未审核'?'readOnly:true,':'' }
                               	value:"${applyNotes?.amount}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>单价（元）：</div></td>
					    <td>
					    	<input id="onePrice" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='id:"onePrice",name:"onePrice",${fieldAcl.isReadOnly("onePrice")},
                               	trim:true,
                               	required:true,
                               	${applyNotes?.applyStatus!='未审核'?'readOnly:true,':'' }
             					value:"${String.format("%.2f", applyNotes?.onePrice)}"
                           	'/>
			            </td>
			            <td></td>
			            <td></td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>用途：</div></td>
					   	<td colspan=3>
					    	<input id="usedBy" data-dojo-type="dijit/form/SimpleTextarea" 
                               	data-dojo-props='name:"usedBy",${fieldAcl.isReadOnly("usedBy")},
                               	trim:true,
                               	required:true,
                               	${applyNotes?.applyStatus!='未审核'?'readOnly:true,':'' }
                               	style:{width:"550px",height:"80px"},
             					value:"${applyNotes?.usedBy}"
                           	'/>
			           	</td>
					
					</tr>
				</table>
			</div>
		</form>
	</div>
	<g:if test="${applyNotes?.id}">
		<div data-dojo-type="dijit/layout/ContentPane" id="flowComment" title="流转意见" data-dojo-props='refreshOnShow:true,
			href:"${createLink(controller:'share',action:'getCommentLog',id:applyNotes?.id)}"
		'>	
		</div>
		<div data-dojo-type="dijit/layout/ContentPane" id="flowLog" title="流程跟踪" data-dojo-props='refreshOnShow:true,
			href:"${createLink(controller:'share',action:'getFlowLog',id:applyNotes?.id,params:[processDefinitionId:applyNotes?.processDefinitionId,taskId:applyNotes?.taskId])}"
		'>	
		</div>
	</g:if>
</div>
</body>