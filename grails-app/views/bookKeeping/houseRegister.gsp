<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>房屋登记</title>
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
				houseRegister_save = function(){
					rosten.readSync(rosten.webPath + "/house/houseRegisterSave",{},function(data){
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
		data-dojo-props='actionBarSrc:"${createLink(controller:'house',action:'houseRegisterForm',id:houseRegister?.id,params:[userid:user?.id])}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="基本信息" data-dojo-props=''>
		<form id="rosten_form" name="rosten_form" onsubmit="return false;" class="rosten_form" style="height:550px;padding:0px">
			<g:hiddenField name="registerNum_form" value="${houseRegister?.registerNum}" />
			<div style="display:none">
				<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${houseRegister?.id }"' />
	        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
			</div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"资产信息",toggleable:false,moreText:"",height:"380px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>资产编号：</div></td>
					    <td width="250">
                           	<input id="registerNum" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"registerNum",${fieldAcl.isReadOnly("registerNum")},
                               		trim:true,
                               		required:true,
                               		disabled:"disabled",
             						value:"${houseRegister?.registerNum}"
                           	'/>
			            </td>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>资产分类：</div></td>
					    <td width="250">
					    	<input id="assetCategory" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"assetCategory",${fieldAcl.isReadOnly("assetCategory")},
                               		trim:true,
                               		required:true,
             						value:"${houseRegister?.assetCategory}"
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
             						value:"${houseRegister?.assetName}"
                           	'/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>管理部门：</div></td>
					   <td width="250">
					    	<input id="allowdepartsName" data-dojo-type="dijit/form/ValidationTextBox" 
				               	data-dojo-props='name:"allowdepartsName",${fieldAcl.isReadOnly("allowdepartsName")},
				               		trim:true,
				               		required:true,
									value:"${houseRegister?.getDepartName()}"
				          	'/>
				         	<g:hiddenField name="allowdepartsId" value="${houseRegister?.userDepart?.id }" />
							<button data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}")}'>选择</button>
			           </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>使用状况：</div></td>
					    <td>
					    	<select id="userStatus" data-dojo-type="dijit/form/FilteringSelect"
                           		data-dojo-props='name:"userStatus",trim:true,required:true,
                           			autoComplete:false,${fieldAcl.isReadOnly("userStatus")},
            						value:"${houseRegister?.userStatus}"
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
            						value:"${houseRegister?.assetSource}"
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
             						value:"${houseRegister?.costCategory}"
                           	'/>
			            </td>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>购置日期：</div></td>
					    <td>
					    	<input id="buyDate" data-dojo-type="dijit/form/DateTextBox" 
		                 	data-dojo-props='name:"buyDate",trim:true,${fieldAcl.isReadOnly("buyDate")},
								value:"${houseRegister?.getFormattedShowBuyDate()}"
		                '/>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>总金额：</div></td>
					    <td>
					    	<input id="totalPrice" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"totalPrice",${fieldAcl.isReadOnly("totalPrice")},
                               		trim:true,
                               		required:true,
             						value:"${houseRegister?.totalPrice}"
                           	'/><span style="margin-left:10px">元</span>
			            </td>
			            <td><div align="right"><span style="color:red">*&nbsp;</span>事业收入：</div></td>
					    <td>
					    	<input id="undertakingRevenue" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"undertakingRevenue",${fieldAcl.isReadOnly("undertakingRevenue")},
                               		value:"${houseRegister?.undertakingRevenue}"
                           	'/><span style="margin-left:10px">元</span>
			            </td>
					</tr>
					<tr>
					    <td><div align="right"><span style="color:red">*&nbsp;</span>财政拨款：</div></td>
					    <td>
					    	<input id="fiscalAppropriation" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"fiscalAppropriation",${fieldAcl.isReadOnly("fiscalAppropriation")},
                               		value:"${houseRegister?.fiscalAppropriation}"
                           	'/><span style="margin-left:10px">元</span>
			            </td>
			            <td><div align="right"><span style="color:red">*&nbsp;</span>其他资金：</div></td>
					    <td>
					    	<input id="otherFund" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"otherFund",${fieldAcl.isReadOnly("otherFund")},
                               		value:"${houseRegister?.otherFund}"
                           	'/><span style="margin-left:10px">元</span>
			            </td>
					</tr>
					<tr>
					    <td><div align="right">建筑面积：</div></td>
					    <td>
					    	<input id="houseArea" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"houseArea",${fieldAcl.isReadOnly("houseArea")},
                               		value:"${houseRegister?.houseArea}"
                           	'/> <span style="margin-left:10px">m<SUP>2</SUP></span>
			            </td>
					    <td><div align="right">采购组织形式：</div></td>
					    <td>
					    	<select id="organizationalType" data-dojo-type="dijit/form/FilteringSelect"
                           		data-dojo-props='name:"organizationalType",
                           			autoComplete:false,${fieldAcl.isReadOnly("organizationalType")},
            						value:"${houseRegister?.organizationalType}"
                            '>
	                            <option value="政府集中采购">政府集中采购</option>
								<option value="部门集中采购">部门集中采购</option>
								<option value="分散采购">分散采购</option>
								<option value="其他">其他</option>
                           	</select>
			            </td>
					</tr>
					<tr>
						 <td ><div align="right">备注：</div></td>
						  <td  colspan="3">
						    	<textarea id="remark" data-dojo-type="dijit/form/SimpleTextarea" 
	    							data-dojo-props='name:"remark",
	                               		style:{width:"550px",height:"150px"},
	                               		trim:true,value:"${houseRegister?.remark}"
	                           '>
	    						</textarea>
						    </td>
						</tr>
				</table>
			</div>
			<div style="height:5px;"></div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"房屋信息",toggleable:false,moreText:"",height:"160px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>产权形式：</div></td>
					    <td>
					    	<select id="rightModality" data-dojo-type="dijit/form/FilteringSelect"
                           		data-dojo-props='name:"rightModality",trim:true,required:true,
                           			autoComplete:false,${fieldAcl.isReadOnly("rightModality")},
            						value:"${houseRegister?.rightModality}"
                            '>
	                            <option value="有产权">有产权</option>
								<option value="无产权">无产权</option>
                           </select>
			           </td>
					   <td width="120"><div align="right">建筑结构：</div></td>
					   <td>
					    	<select id="houseStructure" data-dojo-type="dijit/form/FilteringSelect"
                           		data-dojo-props='name:"houseStructure",trim:true,required:true,
                           			autoComplete:false,${fieldAcl.isReadOnly("houseStructure")},
            						value:"${houseRegister?.houseStructure}"
                            '>
	                            <option value="钢结构">钢结构</option>
								<option value="钢混结构">钢混结构</option>
								<option value="砖混结构">砖混结构</option>
								<option value="砖木结构">砖木结构</option>
								<option value="其他">其他</option>
                           	</select>
			            </td>
					</tr>
					<tr>
					    <td><div align="right">权属证明：</div></td>
					    <td>
					    	<input id="ownershipProve" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"ownershipProve",${fieldAcl.isReadOnly("ownershipProve")},
                               		value:"${houseRegister?.ownershipProve}"
                           	'/>
			            </td>
					    <td><div align="right">权属证号：</div></td>
					    <td>
					    	<input id="ownershipNo" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"ownershipNo",${fieldAcl.isReadOnly("ownershipNo")},
                               		value:"${houseRegister?.ownershipNo}"
                           	'/>
			            </td>
					</tr>
					<tr>
			            <td><div align="right"><span style="color:red">*&nbsp;</span>发证时间：</div></td>
						    <td>
						    	<input id="certificationDate" data-dojo-type="dijit/form/DateTextBox" 
			                 	data-dojo-props='name:"certificationDate",${fieldAcl.isReadOnly("certificationDate")},
									value:"${houseRegister?.getFormattedShowCertificationDate()}"
			                '/>
				        </td>
					    <td><div align="right">坐落位置：</div></td>
					    <td>
					    	<input id="houseLocated" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"houseLocated",${fieldAcl.isReadOnly("houseLocated")},
                               		trim:true,
                               		required:true,
             						value:"${houseRegister?.houseLocated}"
                           	'/>
			            </td>
					</tr>
				</table>
			</div>
		</form>
	</div>
</div>
</body>