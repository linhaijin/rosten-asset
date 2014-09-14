<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>图书登记</title>
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
		 		"dijit/form/ComboBox",
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
				bookRegister_save = function(){
					rosten.readSync(rosten.webPath + "/book/bookRegisterSave",{},function(data){
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
		data-dojo-props='actionBarSrc:"${createLink(controller:'book',action:'bookRegisterForm',id:bookRegister?.id,params:[userid:user?.id])}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props=''>
		<form id="rosten_form" name="rosten_form" onsubmit="return false;" class="rosten_form" style="height:455px;padding:0px">
			<g:hiddenField name="registerNum_form" value="${bookRegister?.registerNum}" />
			<div style="display:none">
				<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${bookRegister?.id }"' />
	        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
			</div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"资产信息",toggleable:false,moreText:"",height:"385px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>资产编号：</div></td>
					    <td width="250">
                           	<input id="registerNum" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"registerNum",${fieldAcl.isReadOnly("registerNum")},
                               		trim:true,
                               		required:true,
                               		disabled:"disabled",
             						value:"${bookRegister?.registerNum}"
                           	'/>
			            </td>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>资产分类：</div></td>
					    <td width="250">
					    	<input id="allowCategoryName" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"allowCategoryName",${fieldAcl.isReadOnly("allowCategoryName")},
                               		trim:true,
                               		required:true,
             						value:"${bookRegister?.getCategoryName()}"
                           	'/>
                           	<g:hiddenField name="allowCategoryId" value="${bookRegister?.userCategory?.id }" />
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
             						value:"${bookRegister?.assetName}"
                           	'/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>管理部门：</div></td>
					   	<td width="250">
					    	<input id="allowdepartsName" data-dojo-type="dijit/form/ValidationTextBox" 
				               	data-dojo-props='name:"allowdepartsName",${fieldAcl.isReadOnly("allowdepartsName")},
				               		trim:true,
				               		required:true,
									value:"${bookRegister?.getDepartName()}"
				          	'/>
				         	<g:hiddenField name="allowdepartsId" value="${bookRegister?.userDepart?.id }" />
							<button data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}")}'>选择</button>
			           </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>使用状况：</div></td>
					    <td>
					    	<select id="userStatus" data-dojo-type="dijit/form/FilteringSelect"
                           		data-dojo-props='name:"userStatus",
                           			autoComplete:false,${fieldAcl.isReadOnly("userStatus")},
                           			trim:true,
                           			required:true,
            						value:"${bookRegister?.userStatus}"
                            '>
	                            <option value="在用">在用</option>
								<option value="多余">多余</option>
								<option value="待修">待修</option>
								<option value="其他">其他</option>
                           	</select>
			            </td>
			            <td><div align="right"><span style="color:red">*&nbsp;</span>资产来源：</div></td>
					    <td>
                           	<select id="assetSource" data-dojo-type="dijit/form/FilteringSelect"
                           		data-dojo-props='name:"assetSource",
                           			autoComplete:false,${fieldAcl.isReadOnly("assetSource")},
                           			trim:true,
                           			required:true,
            						value:"${bookRegister?.assetSource}"
                            '>
	                            <option value="购置">购置</option>
								<option value="捐赠">捐赠</option>
								<option value="其他">其他</option>
                           	</select>
			            </td>
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>价值类型：</div></td>
					    <td>
					    	<input id="costCategory" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"costCategory",${fieldAcl.isReadOnly("costCategory")},
                               		trim:true,
                               		required:true,
             						value:"${bookRegister?.costCategory}"
                           	'/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>购置日期：</div></td>
					    <td>
					    	<input id="buyDate" data-dojo-type="dijit/form/DateTextBox" 
		                 		data-dojo-props='name:"buyDate",${fieldAcl.isReadOnly("buyDate")},
									trim:true,
                               		required:true,
								value:"${bookRegister?.getFormattedShowBuyDate()}"
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
                               		value:"${bookRegister?.amount}"
                           	'/>
			            </td>
						<td><div align="right"><span style="color:red">*&nbsp;</span>总金额（元）：</div></td>
					    <td>
					    	<input id="totalPrice" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"totalPrice",${fieldAcl.isReadOnly("totalPrice")},
                               		trim:true,
                               		required:true,
             						value:"${bookRegister?.totalPrice}"
                           	'/>
			            </td>
					</tr>
					<tr>
					    <td><div align="right">事业收入（元）：</div></td>
					    <td>
					    	<input id="undertakingRevenue" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"undertakingRevenue",${fieldAcl.isReadOnly("undertakingRevenue")},
                               		trim:true,
                               		value:"${bookRegister?.undertakingRevenue}"
                           	'/>
			            </td>
			            <td><div align="right">财政拨款（元）：</div></td>
					    <td>
					    	<input id="fiscalAppropriation" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"fiscalAppropriation",${fieldAcl.isReadOnly("fiscalAppropriation")},
                               		trim:true,
                               		value:"${bookRegister?.fiscalAppropriation}"
                           	'/>
			            </td>
					</tr>
					<tr>
					    <td><div align="right">其他资金元（元）：</div></td>
					    <td colspan="3">
					    	<input id="otherFund" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"otherFund",${fieldAcl.isReadOnly("otherFund")},
                               		trim:true,
                               		value:"${bookRegister?.otherFund}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td ><div align="right">备注：</div></td>
						<td colspan="3">
					    	<textarea id="remark" data-dojo-type="dijit/form/SimpleTextarea" 
    							data-dojo-props='name:"remark",
                               		style:{width:"550px",height:"150px"},
                               		trim:true,
                               		value:"${bookRegister?.remark}"
	                           '>
    						</textarea>
					    </td>
					</tr>
				</table>
			</div>
			<div style="height:5px;"></div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"设备信息",toggleable:false,moreText:"",height:"60px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>文物等级：</div></td>
					    <td>
					    	<select id="antiqueRegistration" data-dojo-type="dijit/form/FilteringSelect" 
                               	data-dojo-props='name:"antiqueRegistration",autoComplete:false,${fieldAcl.isReadOnly("antiqueRegistration")},
                               		trim:true,
                           			required:true,
                               		value:"${bookRegister?.antiqueRegistration}"
                           	'/>
                           		<option value="一级文物">一级文物</option>
								<option value="二级文物">二级文物</option>
								<option value="三级文物">三级文物</option>
                           	</select>
			           </td>
					   <td width="120"><div align="right">管理单位：</div></td>
					   <td>
					    	<input id="manageCompany" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"manageCompany",${fieldAcl.isReadOnly("manageCompany")},
                               		trim:true,
                               		value:"${bookRegister?.manageCompany}"
                           	'/>
			            </td>
					</tr>
					<tr>
					    <td><div align="right">存放地点：</div></td>
					    <td>
                           	<select id="storagePosition" data-dojo-type="dijit/form/ComboBox"
                           		data-dojo-props='name:"storagePosition",
                           			autoComplete:false,${fieldAcl.isReadOnly("storagePosition")},
                           			trim:true,
            						value:"${bookRegister?.storagePosition}"
                            '>
	                            <option value="本单位">本单位</option>
								<option value="外单位">外单位</option>
								<option value="其他">其他</option>
                           	</select>
			            </td>
					    <td><div align="right">坐落位置：</div></td>
					    <td>
					    	<input id="locatePosition" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"locatePosition",${fieldAcl.isReadOnly("locatePosition")},
                               		trim:true,
                               		value:"${bookRegister?.locatePosition}"
                           	'/>
			            </td>
					</tr>
				</table>
			</div>
		</form>
	</div>
</div>
</body>