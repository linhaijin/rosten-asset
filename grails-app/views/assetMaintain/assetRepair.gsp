<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>资产运维--资产报修</title>
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
		function(parser,dom,kernel,registry){
			kernel.addOnLoad(function(){
				rosten.init({webpath:"${request.getContextPath()}",dojogridcss : true});
				rosten.cssinit();
			});
			assetRepair_save = function(){
				rosten.readSync(rosten.webPath + "/assetRepair/assetRepairSave",{},function(data){
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

		assetTypeSelect = function(){
			var url = "${createLink(controller:'assetRepair',action:'assetChooseListDataStore')}";
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
			/*
			var assetTotal;
			var assetTotalSel = dijit.byId("assetTotal");
			if(assetTotalSel){
				if(assetTotalSel.attr("value")!=""){
					assetTotal = assetTotalSel.attr("value").replace(".","-");
				}
			}
			*/
			//异步处理所选资产数据
			var url = "${createLink(controller:'assetRepair',action:'assetChooseOperate')}";
			url += "?assetId="+encodeURI(assetId)+"&assetType="+assetType+"&seriesNo="+seriesNo;
			var ioArgs = {
				url : url,
				handleAs : "json",
				load : function(response,args) {
					if(response.result=="true"){//rensult为true，后台数据操作成功并返回总金额，同时刷新父页面Grid
						//var assetTotal = response.assetTotal;
						//dojo.byId("assetTotal").value = assetTotal;
	
						var url_twice = "${createLink(controller:'assetRepair',action:'assetRepairListDataStore')}";
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
						var grid_twice = dijit.byId("assetRepairListGrid");
						
						grid_twice.url = url_twice;
						grid_twice.refresh();
					}else{//rensult为false，处理失败
						alert("操作失败!");
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
			var grid = dijit.byId("assetRepairListGrid");
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

			var repairId = "${assetRepair?.id}";
			var seriesNo = "${assetRepair?.seriesNo}";
			
			var url = "${createLink(controller:'assetRepair',action:'assetChooseDelete')}";
			url += "?assetId="+encodeURI(assetId)+"&repairId="+repairId;
			var ioArgs = {
				url : url,
				handleAs : "json",
				load : function(response,args) {
					if(response.result=="true"){//rensult为true，后台数据操作成功并返回总金额，同时刷新父页面Grid
						//var assetTotal = response.assetTotal;
						//dojo.byId("assetTotal").value = assetTotal;
	
						var url_twice = "${createLink(controller:'assetRepair',action:'assetRepairListDataStore')}";
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
						var grid_twice = dijit.byId("assetRepairListGrid");
						
						grid_twice.url = url_twice;
						grid_twice.refresh();
					}else{//rensult为false，处理失败
						alert("操作失败!");
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
		data-dojo-props='actionBarSrc:"${createLink(controller:'assetRepair',action:'assetRepairForm',id:assetRepair?.id,params:[userid:user?.id])}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"800px",margin:"0 auto"}' >
	<div data-dojo-type="dijit/layout/ContentPane" title="申请信息" data-dojo-props='height:"610px",marginBottom:"2px",region:"top"'>
		<form id="rosten_form" name="rosten_form" onsubmit="return false;" class="rosten_form" style="padding:0px">
			<g:hiddenField name="seriesNo_form" value="${assetRepair?.seriesNo}" />
			<div style="display:none">
				<input  data-dojo-type="dijit/form/ValidationTextBox" id="id"  data-dojo-props='name:"id",style:{display:"none"},value:"${assetRepair?.id }"' />
	        	<input  data-dojo-type="dijit/form/ValidationTextBox" id="companyId" data-dojo-props='name:"companyId",style:{display:"none"},value:"${company?.id }"' />
			</div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"登记信息",toggleable:false,moreText:"",height:"150px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>申请单号：</div></td>
					    <td width="250">
                           	<input id="seriesNo" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"seriesNo",${fieldAcl.isReadOnly("seriesNo")},
                               		trim:true,
                               		required:true,disabled:"disabled",
             						value:"${assetRepair?.seriesNo}"
                           	'/>
			            </td>
					    <td width="120"><div align="right"><span style="color:red">*&nbsp;</span>申请日期：</div></td>
					    <td width="250">
					    	<input id="applyDate" data-dojo-type="dijit/form/DateTextBox" 
                               	data-dojo-props='name:"applyDate",trim:true,${fieldAcl.isReadOnly("applyDate")},
                               		value:"${assetRepair?.getFormattedShowApplyDate()}"
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
									value:"${assetRepair?.getDepartName()}"
				          	'/>
				         	<g:hiddenField name="allowdepartsId" value="${assetRepair?.applyDept?.id }" />
							<button data-dojo-type="dijit.form.Button" data-dojo-props='onClick:function(){selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}")}'>选择</button>
			           </td>
					</tr>
					<tr>
						<td><div align="right">报修原因：</div></td>
					    <td>
					    	<input id="repairReason" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"repairReason",${fieldAcl.isReadOnly("repairReason")},
                               		trim:true,
             						value:"${assetRepair?.repairReason}"
                           	'/>
			            </td>
			            <td ><div align="right"><span style="color:red">*&nbsp;</span>预约维修时间：</div></td>
						<td>
						    <input id="expectDate" data-dojo-type="dijit/form/DateTextBox" 
	    						data-dojo-props='name:"expectDate",${fieldAcl.isReadOnly("expectDate")},
	                               	trim:true,
	                               	required:true,
	                               	value:"${assetRepair?.getFormattedShowExpectDate()}"
	                         '/>
						</td>
					</tr>
					<tr>
						<td><div align="right">存放地点：</div></td>
					    <td colspan="3">
					    	<input id="storagePosition" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"storagePosition",${fieldAcl.isReadOnly("storagePosition")},
                               		trim:true,
             						value:"${assetRepair?.storagePosition}"
                           	'/>
			            </td>
					</tr>
					<tr>
						<td><div align="right"><span style="color:red">*&nbsp;</span>联系人：</div></td>
					    <td>
					    	<input id="contacts" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"contacts",${fieldAcl.isReadOnly("contacts")},
                               		trim:true,
                               		required:true,
             						value:"${assetRepair?.contacts}"
                           	'/>
			            </td>
			            <td ><div align="right"><span style="color:red">*&nbsp;</span>联系电话：</div></td>
						<td>
						    <input id="contactPhone" data-dojo-type="dijit/form/ValidationTextBox" 
	    						data-dojo-props='name:"contactPhone",${fieldAcl.isReadOnly("contactPhone")},
	                               	trim:true,
	                               	required:true,
	                               	value:"${assetRepair?.contactPhone}"
	                         '/>
						</td>
					</tr>
				</table>
			</div>
			<div data-dojo-type="rosten/widget/TitlePane" data-dojo-props='title:"维护信息",toggleable:false,moreText:"",height:"90px",marginBottom:"2px"'>
				<table border="0" width="740" align="left">
					<tr>
					    <td width="120"><div align="right">维护人：</div></td>
					    <td width="250">
                           	<input id="maintenanceMan" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"maintenanceMan",${fieldAcl.isReadOnly("maintenanceMan")},
                               		trim:true,
             						value:"${assetRepair?.maintenanceMan}"
                           	'/>
			            </td>
					    <td width="120"><div align="right">维护联系电话：</div></td>
					    <td width="250">
                           	<input id="maintenancePhone" data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='name:"maintenancePhone",${fieldAcl.isReadOnly("maintenancePhone")},
                               		trim:true,
             						value:"${assetRepair?.maintenancePhone}"
                           	'/>
			            </td>
					</tr>
					<tr>
					    <td><div align="right">维护时间：</div></td>
					    <td>
					    	<input id="maintenanceDate" data-dojo-type="dijit/form/DateTextBox" 
                               	data-dojo-props='name:"maintenanceDate",${fieldAcl.isReadOnly("maintenanceDate")},
                               		trim:true,
             						value:"${assetRepair?.getFormattedShowMaintenanceDate()}"
                           	'/>
			            </td>
					    <td><div align="right">维护费用：</div></td>
					   <td width="250">
					    	<input id="maintenanceCost" data-dojo-type="dijit/form/ValidationTextBox" 
				               	data-dojo-props='name:"maintenanceCost",${fieldAcl.isReadOnly("maintenanceCost")},
				               		trim:true,
									value:"${assetRepair?.maintenanceCost}"
				          	'/>
			           </td>
					</tr>
					<tr>
						<td><div align="right">是否修复：</div></td>
					    <td>
					    	<select id="repairComplete" data-dojo-type="dijit/form/FilteringSelect"
                           		data-dojo-props='name:"repairComplete",${fieldAcl.isReadOnly("repairComplete")},
                           			autoComplete:false,
                           			trim:true,
            						value:"${assetRepair?.repairComplete}"
                            '>
	                            <option value="未修复">未修复</option>
								<option value="已修复">已修复</option>
								<option value="无法修复">无法修复</option>
                           	</select>
			            </td>
			            <td ><div align="right">维护反馈：</div></td>
						<td>
						    <input id="feedback" data-dojo-type="dijit/form/ValidationTextBox" 
	    						data-dojo-props='name:"feedback",${fieldAcl.isReadOnly("feedback")},
	                               	trim:true,
	                               	value:"${assetRepair?.feedback}"
	                         '/>
						</td>
					</tr>
				</table>
			</div>
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
	
			<div id="assetRepairList" data-dojo-type="dijit.layout.ContentPane" data-dojo-props='style:"width:780px;height:300px;padding:2px;"'>
				<div data-dojo-type="rosten/widget/RostenGrid" id="assetRepairListGrid" data-dojo-id="assetRepairListGrid"
					data-dojo-props='url:"${createLink(controller:'assetRepair',action:'assetRepairListDataStore',params:[companyId:company?.id,seriesNo:assetRepair?.seriesNo])}"'></div>
			</div>
		</form>
	</div>
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
						data-dojo-props='url:"${createLink(controller:'assetRepair',action:'assetChooseListDataStore',params:[companyId:company?.id])}"'></div>
				</div>
			</div>
		</div>
		<script type="dojo/method" event="cancelFunction">
				dijit.byId("assetChooseDialog").hide();
			</script>
	</div>
</div>
</body>