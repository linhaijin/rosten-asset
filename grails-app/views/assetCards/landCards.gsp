<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>土地资产</title>
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
				landCards_save = function(){
					rosten.readSync(rosten.webPath + "/landCards/landCardsSave",{},function(data){
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
		data-dojo-props='actionBarSrc:"${createLink(controller:'landCards',action:'landCardsForm',id:landCards?.id,params:[userid:user?.id])}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",height:"640px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props='height:"550px",marginBottom:"2px",region:"top"'>
		<form id="rosten_form" name="rosten_form" onsubmit="return false;" class="rosten_form" style="padding:0px">
			<g:hiddenField name="CardsNum_form" value="${landCards?.registerNum}" />
			<div style="display:none">
				<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${landCards?.id }"' />
	        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
			</div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"资产信息",toggleable:false,moreText:"",height:"540px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>资产编号：</div></td>
					    <td width="250">
                           	<input id="registerNum" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"registerNum",${fieldAcl.isReadOnly("registerNum")},
                               		trim:true,
                               		required:true,
                               		disabled:"disabled",
             						value:"${landCards?.registerNum}"
                           	'/>
			            </td>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>资产分类：</div></td>
					    <td width="250">
					    	<input id="allowCategoryName" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"allowCategoryName",${fieldAcl.isReadOnly("allowCategoryName")},
                               		trim:true,
                               		required:true,
             						value:"${landCards?.getCategoryName()}"
                           	'/>
                           	<g:hiddenField name="allowCategoryId" value="${landCards?.userCategory?.id }" />
							<button data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){selectAssetCategory("${createLink(controller:'assetConfig',action:'assetCategoryTreeDataStore',params:[companyId:company?.id])}")}'>选择</button>
			           	</td>
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>资产名称：</div></td>
					    <td>
					    	<input id="assetName" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"assetName",${fieldAcl.isReadOnly("assetName")},
                               		trim:true,
                               		required:true,
             						value:"${landCards?.assetName}"
                           	'/>
			            </td>
			            <td><div align="right"><span style="color:red">*&nbsp;</span>规格型号：</div></td>
					    <td>
					    	<input id="specifications" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='id:"specifications",name:"specifications",${fieldAcl.isReadOnly("specifications")},
                               		trim:true,
                               		required:true,
             						value:"${landCards?.specifications}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>管理部门：</div></td>
					   	<td width="250">
					    	<input id="allowdepartsName" data-dojo-type="dijit/form/ValidationTextBox" 
				               	data-dojo-props='name:"allowdepartsName",${fieldAcl.isReadOnly("allowdepartsName")},
				               		trim:true,
				               		required:true,
									value:"${landCards?.getDepartName()}"
				          	'/>
				         	<g:hiddenField name="allowdepartsId" value="${landCards?.userDepart?.id }" />
							<button data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}")}'>选择</button>
			           	</td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>使用状况：</div></td>
					    <td>
					    	<select id="userStatus" data-dojo-type="dijit/form/FilteringSelect"
                           		data-dojo-props='name:"userStatus",trim:true,required:true,
                           			autoComplete:false,${fieldAcl.isReadOnly("userStatus")},
            						value:"${landCards?.userStatus}"
                            '>
	                            <option value="在用">在用</option>
								<option value="多余">多余</option>
								<option value="待修">待修</option>
								<option value="其他">其他</option>
                           	</select>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>资产来源：</div></td>
					    <td>
                           	<select id="assetSource" data-dojo-type="dijit/form/FilteringSelect"
                           		data-dojo-props='name:"assetSource",trim:true,required:true,
                           			autoComplete:false,${fieldAcl.isReadOnly("assetSource")},
            						value:"${landCards?.assetSource}"
                            '>
	                            <option value="购置">购置</option>
								<option value="捐赠">捐赠</option>
								<option value="自制">自制</option>
								<option value="其他">其他</option>
                           	</select>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>价值类型：</div></td>
					    <td>
					    	<input id="costCategory" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"costCategory",${fieldAcl.isReadOnly("costCategory")},
                               		trim:true,
                               		required:true,
             						value:"${landCards?.costCategory}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>购买日期：</div></td>
					    <td>
					    	<input id="buyDate" data-dojo-type="dijit/form/DateTextBox" 
		                 	data-dojo-props='name:"buyDate",trim:true,${fieldAcl.isReadOnly("buyDate")},
								value:"${landCards?.getFormattedShowBuyDate()}"
		                '/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>价格（元）：</div></td>
					    <td>
					    	<input id="onePrice" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"onePrice",${fieldAcl.isReadOnly("onePrice")},
                               		trim:true,
                               		required:true,
             						value:"${String.format("%.2f", landCards?.onePrice)}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>事业收入（元）：</div></td>
					    <td>
					    	<input id="undertakingRevenue" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"undertakingRevenue",${fieldAcl.isReadOnly("undertakingRevenue")},
                               		trim:true,
                               		required:true,
             						value:"${landCards?.undertakingRevenue}"
                           	'/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>财政拨款（元）：</div></td>
					    <td>
					    	<input id="fiscalAppropriation" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"fiscalAppropriation",${fieldAcl.isReadOnly("fiscalAppropriation")},
                               		trim:true,
                               		required:true,
             						value:"${landCards?.fiscalAppropriation}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>其他资金（元）：</div></td>
					    <td>
					    	<input id="otherFund" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"otherFund",${fieldAcl.isReadOnly("otherFund")},
                               		trim:true,
                               		required:true,
             						value:"${landCards?.otherFund}"
                           	'/>
			            </td>
					    <td><div align="right">采购组织形式：</div></td>
					    <td>
					    	<select id="organizationalType" data-dojo-type="dijit/form/FilteringSelect"
                           		data-dojo-props='name:"organizationalType",trim:true,required:true,
                           			autoComplete:false,${fieldAcl.isReadOnly("organizationalType")},
            						value:"${landCards?.organizationalType}"
                            '>
	                            <option value="政府集中采购">政府集中采购</option>
								<option value="部门集中采购">部门集中采购</option>
								<option value="分散采购">分散采购</option>
								<option value="其他">其他</option>
                           	</select>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>负责人：</div></td>
					    <td>
					    	<input id="purchaser" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"purchaser",${fieldAcl.isReadOnly("purchaser")},
                               		trim:true,
                               		required:true,
             						value:"${landCards?.purchaser}"
                           	'/>
			            </td>
						<td><div align="right">坐落位置：</div></td>
					    <td>
					    	<input id="landLocated" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"landLocated",${fieldAcl.isReadOnly("landLocated")},
                               		trim:true,
             						value:"${landCards?.landLocated}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td><div align="right">建筑面积（m<SUP>2</SUP>）：</div></td>
					    <td colspan="3">
					    	<input id="landArea" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"landArea",${fieldAcl.isReadOnly("landArea")},
                               		trim:true,
             						value:"${landCards?.landArea}"
                           	'/>
			            </td>
					</tr>
					<tr>
						 <td ><div align="right">条形码：</div></td>
						 <td colspan="3">
					    	<div style="text-align:left">
								<img src="${createLink(controller:'landCards',action:'getBarcode',params:[registerNum:landCards?.registerNum])}" width="280" height="80" style="left:0px; top:0px;">
							</div>
					    </td>
					</tr>
					<tr>
						 <td><div align="right">备注：</div></td>
						 <td colspan="3">
					    	<textarea id="remark" data-dojo-type="dijit/form/SimpleTextarea" 
    							data-dojo-props='name:"remark",
                               		style:{width:"550px",height:"150px"},
                               		trim:true,value:"${landCards?.remark}"
                           '>
    						</textarea>
					    </td>
					</tr>
				</table>
			</div>
		</form>
	</div>
</div>
</body>