<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>资产变动--报废报损</title>
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
		 		"dojox/grid/DataGrid",
		     	"rosten/widget/ActionBar",
		     	"rosten/widget/TitlePane",
		     	"rosten/app/Application",
		     	"rosten/app/SystemApplication",
		     	"rosten/app/BookKeeping",
		     	"rosten/kernel/behavior"],
			function(parser,dom,kernel,registry){
				kernel.addOnLoad(function(){
					rosten.init({webpath:"${request.getContextPath()}",dojogridcss : true});
					rosten.cssinit();
				});
				assetScrap_save = function(){
					rosten.readSync(rosten.webPath + "/assetScrap/assetScrapSave",{},function(data){
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
		data-dojo-props='actionBarSrc:"${createLink(controller:'assetScrap',action:'assetScrapForm',id:assetScrap?.id,params:[userid:user?.id])}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="申请信息" data-dojo-props='height:"460px",marginBottom:"2px",region:"top"'>
		<form id="rosten_form" name="rosten_form" onsubmit="return false;" class="rosten_form" style="padding:0px">
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
                               	data-dojo-props='name:"assetTotal",${fieldAcl.isReadOnly("assetTotal")},
                               		trim:true,
                               		required:true,
             						value:"${assetScrap?.assetTotal}"
                           	'/>
			            </td>
			            <td ><div align="right">申请描述：</div></td>
						<td>
						    <input id="applyDesc" data-dojo-type="dijit/form/ValidationTextBox" 
	    						data-dojo-props='name:"applyDesc",${fieldAcl.isReadOnly("applyDesc")},
	                               	trim:true,
	                               	required:true,
	                               	value:"${assetScrap?.applyDesc}"
	                         '/>
						</td>
					</tr>
				</table>
			</div>
			
			<div data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){addAsset()}'>添加</div>
			<div data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){deleteAsset()}'>删除</div>
	
			<div style="height:5px;"></div>
	
			<div id="assetScrapList" data-dojo-type="dijit.layout.ContentPane" data-dojo-props='style:"width:780px;height:300px;padding:2px;"'>
				<div data-dojo-type="rosten/widget/RostenGrid" id="assetScrapListGrid" data-dojo-id="assetScrapListGrid"
					data-dojo-props='url:"${createLink(controller:'assetScrap',action:'assetScrapListDataStore',params:[companyId:company?.id])}"'></div>
			</div>
		</form>
		
			
	</div>
	
	
	
</div> 
</body>