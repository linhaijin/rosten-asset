/**
 * @author ercjlo
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior"], function(
		connect, lang,registry,kernel) {
	
	assetCheck_search = function(){
		var content = {};
		
		var registerNum = registry.byId("check_registerNum");
		if(registerNum.get("value")!=""){
			content.registerNum = registerNum.get("value");
		}
		
		var category = registry.byId("check_category");
		if(category.get("value")!=""){
			content.category = category.get("value");
		}
		
		var assetName = registry.byId("check_assetName");
		if(assetName.get("value")!=""){
			content.assetName = assetName.get("value");
		}
		
		var userDepart = registry.byId("check_userDepartIds");
		if(userDepart.get("value")!=""){
			content.userDepart = userDepart.get("value");
		}
		
		var assetStatus = registry.byId("check_assetStatus");
		if(assetStatus.get("value")!=""){
			content.assetStatus = assetStatus.get("value");
		}
		
		switch(rosten.kernel.navigationEntity) {
		default:
			rosten.kernel.refreshGrid(rosten.kernel.getGrid().defaultUrl, content);
			break;
		}
	};
	
	assetCheck_resetSearch = function(){
		switch(rosten.kernel.navigationEntity) {
		default:
			registry.byId("check_registerNum").set("value","");
			registry.byId("check_category").set("value","");
			registry.byId("check_assetName").set("value","");
			registry.byId("check_userDepart").set("value","");
			registry.byId("check_userDepartIds").set("value","");
			registry.byId("check_assetStatus").set("value","");
			
			rosten.kernel.refreshGrid();
			break;
		}	
	};
	
	assetCheck_export = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var registerNum;
		var qregisterNum = "";
		var category;
		var qcategory = "";
		var assetName;
		var qassetName = "";
		var userDepart;
		var quserDepart = "";
		var assetStatus;
		var qassetStatus = "";
		
		var registerNum_ = registry.byId("check_registerNum");
		if(registerNum_.get("value")!=""){
			registerNum = registerNum_.get("value");
			qregisterNum = "&registerNum="+registerNum;
		}
		
		var category_ = registry.byId("check_category");
		if(category_.get("value")!=""){
			category = category_.get("value");
			qcategory = "&category="+category;
		}
		
		var assetName_ = registry.byId("check_assetName");
		if(assetName_.get("value")!=""){
			assetName = assetName_.get("value");
			qassetName = "&assetName="+assetName;
		}
		
		var userDepart_ = registry.byId("check_userDepartIds");
		if(userDepart_.get("value")!=""){
			userDepart = userDepart_.get("value");
			quserDepart = "&userDepart="+userDepart;
		}
		
		var assetStatus = registry.byId("check_assetStatus");
		if(assetStatus.get("value")!=""){
			assetStatus = assetStatus.get("value");
			qassetStatus = "&assetStatus="+assetStatus;
		}
		
		rosten.openNewWindow("assetCheck", rosten.webPath + "/inventoryTask/assetCheckExport?companyId="+companyId+qregisterNum+qcategory+qassetName+quserDepart+qassetStatus);
		
//		var url = rosten.webPath + "/inventoryTask/assetCheckExport?companyId="+companyId+qregisterNum+qcategory+qassetName+quserDepart;
//		var copyOfWindowOpen = window.open;
//		var width = 200;
//	    var height = 200;
//	    var params = "height=" + height + ",width=" + width + ",scrollbars=yes,toolbar=no,menubar=yes,status=yes,resizable=yes,border=0,top=0,left=0";
//		if (typeof(url) != "undefined") {
//			 var docwin = copyOfWindowOpen(url, "assetCheck", params);
//		}	
	};
	
	myTask_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:myTask_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	}
	myTask_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("myTask", rosten.webPath + "/inventoryTask/myTaskShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	assetCard_formatTopic =function(value,rowIndex){
		return "<a href=\"javascript:assetCard_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	}
	assetCard_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var categoryId = rosten.kernel.getGridItemValue(rowIndex,"userCategoryId");
		rosten.openNewWindow("assetCheck", rosten.webPath + "/inventoryTask/assetCardShow/" + unid + "?userid=" + userid + "&companyId=" + companyId + "&categoryId=" + categoryId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	//盘点任务发布
	assetCheck_add = function(){//新建盘点任务
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("assetCheck", rosten.webPath + "/inventoryTask/assetCheckAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	assetCheck_run = function(){//启动盘点任务
		var _1 = rosten.confirm("确认启动盘点任务，是否继续?");
		_1.callback = function() {
			var runStatus = rosten.getGridSelectedValue("getRunStatusLabel");
			if(runStatus == "已启动"){
				rosten.alert("注意：该任务已经启动！");
				return;
			}
			var unids = rosten.getGridUnid("single");
			var content = {};
			if (unids == ""){
				rosten.alert("注意：请在列表中选择数据！");
				return;
			}else{
				content.runStatus = "1";
				content.taskStatus = "1";
				content.id = unids;
			}
			rosten.readSyncNoTime(rosten.webPath + "/inventoryTask/assetCheckRun", content,rosten.submitCallback);
		};
	};
	
	assetCheck_delete = function(){//删除盘点任务
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			var content = {};
			if (unids == ""){
				rosten.alert("注意：请在列表中选择数据！");
				return;
			}else{
				content.id = unids;
			}
			rosten.read(rosten.webPath + "/inventoryTask/assetCheckDelete", content,rosten.deleteCallback);
		};
	};
	
	assetCheck_start = function(){//开始盘点任务
		var unids = rosten.getGridUnid("single");
		if (unids == ""){
			rosten.alert("注意：请在列表中选择数据！");
			return;
		}
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        var taskStatus = "2";
        rosten.openNewWindow("assetCheck", rosten.webPath + "/inventoryTask/myTaskShow/" + unids + "?companyId=" + companyId + "&userid=" + userid + "&taskStatus=" + taskStatus);
	};
	
	assetCheck_complete = function(){//完成盘点任务
		var _1 = rosten.confirm("确认完成盘点任务，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("single");
			
			var content = {};
			if (unids == ""){
				rosten.alert("注意：请在列表中选择数据！");
				return;
			}else{
				content.completeStatus = "1";
				content.taskStatus = "3";
				content.id = unids;
			}
			rosten.read(rosten.webPath + "/inventoryTask/assetCheckComplete", content,rosten.submitCallback);
		};
	};
	
	assetCheck_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:assetCheck_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	assetCheck_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("assetCheck", rosten.webPath + "/inventoryTask/assetCheckShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
		
	/*
	 * 此功能默认必须存在
	 */
	show_assetCheckNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		switch (oString) {
			case "assetRwfb":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/inventoryTask/inventoryTaskView?userId=" + userid,
	                gridSrc : rosten.webPath + "/inventoryTask/inventoryTaskGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	            break;
			case "myPdrw":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/inventoryTask/myTaskView?userId=" + userid,
	                gridSrc : rosten.webPath + "/inventoryTask/myTaskGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	            break;
			case "assetHc"://资产核查
				var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/inventoryTask/assetHcView?userId=" + userid,
	                searchSrc:rosten.webPath + "/inventoryTask/assetHcSearchView?companyId=" + companyId,
	                gridSrc : rosten.webPath + "/inventoryTask/assetHcGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            break;
			
		}
		
	}
	connect.connect("show_naviEntity", show_assetCheckNaviEntity);
});