<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>设备申请</title>
    <style type="text/css">
    	.rosten .rosten_form table tr{
    		height:40px;
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
			function(parser,dom,kernel,registry){
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}"});
					rosten.cssinit();
				});
				apply_save = function(){
					rosten.readSync(rosten.webPath + "/demo/applySave",{},function(data){
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
		data-dojo-props='actionBarSrc:"${createLink(controller:'demo',action:'applyForm',id:apply?.id,params:[userid:user?.id])}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props=''>
		<form id="rosten_form" name="rosten_form" onsubmit="return false;" class="rosten_form" style="height:260px;padding:0px">
			<div style="display:none">
				<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${apply?.id }"' />
	        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
			</div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"资产信息",toggleable:false,moreText:"",height:"180px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>申请人：</div></td>
					    <td width="250">
                           	<input data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='
                               		trim:true,
                               		required:true,
                               		disabled:"disabled",
             						value:"${apply?.getFormattedUser()}"
                           	'/>
			            </td>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>资产分类：</div></td>
					    <td width="250">
					    	<input id="assetCategory" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"assetCategory",${fieldAcl.isReadOnly("assetCategory")},
                               		trim:true,
                               		required:true,
             						value:"${apply?.assetCategory}"
                           	'/>
			            </td>
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>资产名称：</div></td>
					    <td>
					    	<input id="assetName" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"assetName",${fieldAcl.isReadOnly("assetName")},
                               		trim:true,
                               		required:true,
             						value:"${apply?.assetName}"
                           	'/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>用途：</div></td>
					   <td width="250">
					    	<input id="usedAddress" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"usedAddress",${fieldAcl.isReadOnly("usedAddress")},
                               		trim:true,
                               		required:true,
             						value:"${apply?.usedAddress}"
                           	'/>
			           </td>
					</tr>
					
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>数量：</div></td>
					    <td>
					    	<input id="amount" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"amount",${fieldAcl.isReadOnly("amount")},
                               		trim:true,
                               		required:true,
                               		value:"${apply?.amount}"
                           	'/>
			            </td>
						<td><div align="right"><span style="color:red">*&nbsp;</span>总金额：</div></td>
					    <td>
					    	<input id="totalPrice" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"totalPrice",${fieldAcl.isReadOnly("totalPrice")},
                               		trim:true,
                               		required:true,
             						value:"${apply?.totalPrice}"
                           	'/><span style="margin-left:10px">元</span>
			            </td>
					</tr>
					
				</table>
			
			</div>
		</form>
	</div>
</div>
</body>