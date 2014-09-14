<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>车辆登记</title>
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
				carRegister_save = function(){
					rosten.readSync(rosten.webPath + "/car/carRegisterSave",{},function(data){
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
		data-dojo-props='actionBarSrc:"${createLink(controller:'car',action:'carRegisterForm',id:carRegister?.id,params:[userid:user?.id])}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props=''>
		<form id="rosten_form" name="rosten_form" onsubmit="return false;" class="rosten_form" style="height:570px;padding:0px">
			<g:hiddenField name="registerNum_form" value="${carRegister?.registerNum}" />
			<div style="display:none">
				<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${carRegister?.id }"' />
	        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
			</div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"资产信息",toggleable:false,moreText:"",height:"410px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>资产编号：</div></td>
					    <td width="250">
                           	<input id="registerNum" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"registerNum",${fieldAcl.isReadOnly("registerNum")},
                               		trim:true,
                               		required:true,
                               		disabled:"disabled",
             						value:"${carRegister?.registerNum}"
                           	'/>
			            </td>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>资产分类：</div></td>
					    <td width="250">
					    	<input id="allowCategoryName" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"allowCategoryName",${fieldAcl.isReadOnly("allowCategoryName")},
                               		trim:true,
                               		required:true,
             						value:"${carRegister?.getCategoryName()}"
                           	'/>
                           	<g:hiddenField name="allowCategoryId" value="${carRegister?.userCategory?.id }" />
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
             						value:"${carRegister?.assetName}"
                           	'/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>管理部门：</div></td>
					   <td width="250">
					    	<input id="allowdepartsName" data-dojo-type="dijit/form/ValidationTextBox" 
				               	data-dojo-props='name:"allowdepartsName",${fieldAcl.isReadOnly("allowdepartsName")},
				               		trim:true,
				               		required:true,
									value:"${carRegister?.getDepartName()}"
				          	'/>
				         	<g:hiddenField name="allowdepartsId" value="${carRegister?.userDepart?.id }" />
							<button data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}")}'>选择</button>
			           </td>
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>使用状况：</div></td>
					    <td>
					    	<select id="userStatus" data-dojo-type="dijit/form/FilteringSelect"
                           		data-dojo-props='name:"userStatus",trim:true,required:true,
                           			autoComplete:false,${fieldAcl.isReadOnly("userStatus")},
            						value:"${carRegister?.userStatus}"
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
                           		data-dojo-props='name:"assetSource",trim:true,required:true,
                           			autoComplete:false,${fieldAcl.isReadOnly("assetSource")},
            						value:"${carRegister?.assetSource}"
                            '>
	                            <option value="购置">购置</option>
								<option value="捐赠">捐赠</option>
								<option value="自制">自制</option>
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
             						value:"${carRegister?.costCategory}"
                           	'/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>购买日期：</div></td>
					    <td>
					    	<input id="buyDate" data-dojo-type="dijit/form/DateTextBox" 
		                 	data-dojo-props='name:"buyDate",trim:true,${fieldAcl.isReadOnly("buyDate")},
								value:"${carRegister?.getFormattedShowBuyDate()}"
		                '/>
			            </td>
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>数量（辆）：</div></td>
					    <td>
					    	<input id="amount" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"amount",${fieldAcl.isReadOnly("amount")},
                               		trim:true,
                               		required:true,
             						value:"${carRegister?.amount}"
                           	'/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>总金额（元）：</div></td>
					    <td>
					    	<input id="totalPrice" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"totalPrice",${fieldAcl.isReadOnly("totalPrice")},
                               		trim:true,
                               		required:true,
             						value:"${carRegister?.totalPrice}"
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
             						value:"${carRegister?.undertakingRevenue}"
                           	'/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>财政拨款（元）：</div></td>
					    <td>
					    	<input id="fiscalAppropriation" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"fiscalAppropriation",${fieldAcl.isReadOnly("fiscalAppropriation")},
                               		trim:true,
                               		required:true,
             						value:"${carRegister?.fiscalAppropriation}"
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
             						value:"${carRegister?.otherFund}"
                           	'/>
			            </td>
					    <td><div align="right">采购组织形式：</div></td>
					    <td>
					    	<select id="organizationalType" data-dojo-type="dijit/form/FilteringSelect"
                           		data-dojo-props='name:"organizationalType",trim:true,required:true,
                           			autoComplete:false,${fieldAcl.isReadOnly("organizationalType")},
            						value:"${carRegister?.organizationalType}"
                            '>
	                            <option value="政府集中采购">政府集中采购</option>
								<option value="部门集中采购">部门集中采购</option>
								<option value="分散采购">分散采购</option>
								<option value="其他">其他</option>
                           	</select>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>国别：</div></td>
					    <td>
					    	<input id="country" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"country",${fieldAcl.isReadOnly("country")},
                               		trim:true,
                               		required:true,
             						value:"${carRegister?.country}"
                           	'/>
			            </td>
					    <td><div align="right">存放地点：</div></td>
					    <td>
					    	<input id="storagePosition" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"storagePosition",${fieldAcl.isReadOnly("storagePosition")},
                               		trim:true,
             						value:"${carRegister?.storagePosition}"
                           	'/>
			            </td>
					</tr>
					<tr>
						 <td ><div align="right">备注：</div></td>
						  <td colspan="3">
					    	<textarea id="remark" data-dojo-type="dijit/form/SimpleTextarea" 
    							data-dojo-props='name:"remark",
                               		style:{width:"550px",height:"150px"},
                               		trim:true,value:"${carRegister?.remark}"
                           '>
    						</textarea>
					    </td>
					</tr>
				</table>
			</div>
			<div style="height:5px;"></div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"车辆信息",toggleable:false,moreText:"",height:"150px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					     <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>车牌号：</div></td>
					      <td width="250">
					    	<input id="carplateNo" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"carplateNo",${fieldAcl.isReadOnly("carplateNo")},
                               		trim:true,
                               		required:true,
             						value:"${carRegister?.carplateNo}"
                           	'/>
			            </td>
			            <td><div align="right">车架号：</div></td>
					    <td>
					    	<input id="carframeNo" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"carframeNo",${fieldAcl.isReadOnly("carframeNo")},
                               		trim:true,
             						value:"${carRegister?.carframeNo}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td><div align="right">发动机号：</div></td>
					    <td>
					    	<input id="carengineNo" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"carengineNo",${fieldAcl.isReadOnly("carengineNo")},
                               		trim:true,
             						value:"${carRegister?.carengineNo}"
                           	'/>
			            </td>
			            <td><div align="right">排气量：</div></td>
					    <td>
					    	<input id="gasDisplacement" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"gasDisplacement",${fieldAcl.isReadOnly("gasDisplacement")},
                               		trim:true,
             						value:"${carRegister?.gasDisplacement}"
                           	'/>
			            </td>
					</tr>
					<tr>
					    <td width="120"><div align="right">规格：</div></td>
					   <td width="250">
					    	<input id="specifications" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"specifications",${fieldAcl.isReadOnly("specifications")},
                               		trim:true,
             						value:"${carRegister?.specifications}"
                           	'/>
			            </td>
					    <td><div align="right">型号：</div></td>
					    <td>
					    	<input id="modelNo" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"modelNo",${fieldAcl.isReadOnly("modelNo")},
                               		trim:true,
             						value:"${carRegister?.modelNo}"
                           	'/>
			            </td>
					</tr>
					<tr>
					    <td><div align="right">厂家：</div></td>
					    <td>
					    	<input id="produceFactory" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"produceFactory",${fieldAcl.isReadOnly("produceFactory")},
                               		trim:true,
             						value:"${carRegister?.produceFactory}"
                           	'/>
			            </td>
					    <td><div align="right">供应商：</div></td>
					    <td>
					    	<input id="supplier" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"supplier",${fieldAcl.isReadOnly("supplier")},
                               		trim:true,
             						value:"${carRegister?.supplier}"
                           	'/>
			            </td>
					</tr>
					<tr>
					    <td><div align="right">编制情况：</div></td>
					    <td>
					    	<input id="staffingStatus" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"staffingStatus",${fieldAcl.isReadOnly("staffingStatus")},
                               		trim:true,
             						value:"${carRegister?.staffingStatus}"
                           	'/>
			            </td>
					    <td><div align="right">车辆产地：</div></td>
					    <td>
					    	<input id="productionPlace" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"productionPlace",${fieldAcl.isReadOnly("productionPlace")},
                               		trim:true,
             						value:"${carRegister?.productionPlace}"
                           	'/>
			            </td>
					</tr>
				</table>
			</div>
		</form>
	</div>
</div>
</body>