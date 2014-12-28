<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>资产筛选</title>
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
		"dijit/layout/ContentPane",
		"dijit/form/ValidationTextBox",
		"dijit/form/Button",
		"dijit/form/Select",
		"dijit/Dialog",
		"dojox/grid/DataGrid",
    	"rosten/widget/ActionBar",
    	"rosten/app/Application",
    	"rosten/app/SystemApplication",
    	"rosten/kernel/behavior"]);
	</script>
</head>
<body>
<div class="rosten_action">
	<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="rosten_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'assetCategoryChoose',action:'assetCategoryChooseForm',params:[])}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/ContentPane" data-dojo-props='height:"630px",marginBottom:"2px",region:"top"'>
	<form id="assetCategoryChoose_form" name="assetCategoryChoose_form" onsubmit="return false;" class="rosten_form" style="padding:0px">
		<div class="searchtab">
			<table width="100%" height="50" border="0">
	        	<tbody>
	          		<tr>
	            		<th width="8%">资产类别</th>
			            <td width="14%">
			            	<select id="assetCardsType" data-dojo-type="dijit/form/FilteringSelect" data-dojo-props='trim:true,autoComplete:false,value:"",style:"width:120px;"'>
								<option value="house">房屋及建筑物</option>
								<option value="car">运输工具</option>
								<option value="device">电子设备</option>
								<option value="furniture">办公家具</option>
	                        </select>
			            </td>
			            <th width="8%">部  门</th>
			            <td width="14%">
			            	<div id="assetDepart" data-dojo-type="dijit/form/ComboBox"
				                data-dojo-props='trim:true,value:"",style:"width:120px;"'>
				            	 <g:each in="${DepartList}" var="item">
				                	<option value="${item.id}">${item.departName }</option>
				                </g:each>
				            </div>
			            </td>
			            <th width="8%">使用人</th>
			            <td width="14%">
			            	<input id="assetUser" data-dojo-type="dijit/form/ValidationTextBox" 
			                	data-dojo-props='trim:true,value:"",style:"width:120px;"'/>
			            </td>
			            <td width="12%">
			            	<div class="btn">
			                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){assetCategoryChoose_search()}'>查询</button>
			                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){assetCategoryChoose_reset()}'>重置</button>
			              	</div>
			            </td>
	          		</tr>
	        	</tbody>
	      	</table>
      	</div>
      	<div id="assetCategoryChooseList" data-dojo-type="dijit.layout.ContentPane" data-dojo-props='style:"height:500px;padding:2px;overflow:auto;"'>
			<div data-dojo-type="rosten/widget/RostenGrid" id="assetCategoryChooseListGrid" data-dojo-id="assetCategoryChooseListGrid"
			data-dojo-props='imgSrc:"${resource(dir:'images/rosten/share',file:'wait.gif')}",url:"${createLink(controller:'assetCategoryChoose',action:'assetCategoryChooseListDataStore',params:[companyId:company?.id,controlName:controlName,seriesNo:seriesNo])}"'></div>
		</div>
	</form>
</div>
</body>
</html>