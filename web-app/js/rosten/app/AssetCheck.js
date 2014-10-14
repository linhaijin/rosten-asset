/**
 * @author ercjlo
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	
//	startPd = function(){
//		rosten.openNewWindow("startPd", rosten.webPath + "/demo/startPd");	
//	};
//	endPd = function(){
//		rosten.kernel.setHref(rosten.webPath + "/demo/endPd", "endPd");
//	};
//	addRw = function(){
//		rosten.kernel.createRostenShowDialog(rosten.webPath + "/demo/addRw", {
//            onLoadFunction : function() {
//
//            }
//        });
//	};
	
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
				alert("注意：该任务已经启动！");
				return;
			}
			var unids = rosten.getGridUnid("single");
			if (unids == "")
				return;
			var content = {runStatus:"1"};
			content.taskStatus = "1";
			content.id = unids;
			rosten.read(rosten.webPath + "/inventoryTask/assetCheckRun", content,rosten.submitCallback);
		};
	};
	
	assetCheck_delete = function(){//删除盘点任务
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/inventoryTask/assetCheckDelete", content,rosten.deleteCallback);
		};
	};
	
	assetCheck_start = function(){//开始盘点任务
		var unids = rosten.getGridUnid("single");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        var taskStatus = "2";
        rosten.openNewWindow("assetCheck", rosten.webPath + "/inventoryTask/assetCheckStart?companyId=" + companyId + "&userid=" + userid + "&taskId=" +unids + "&taskStatus=" + taskStatus);
	};
	
	assetCheck_complete = function(){//完成盘点任务
		var _1 = rosten.confirm("确认完成盘点任务，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("single");
			if (unids == "")
				return;
			var content = {completeStatus:"1"};
			content.taskStatus = "3";
			content.id = unids;
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
//			case "assetRwfb":
//				rosten.kernel.setHref(rosten.webPath + "/demo/fbrw", oString);
//	            break;
//			case "myPdrw":
//				rosten.kernel.setHref(rosten.webPath + "/demo/myPdrw", oString);
//	            break;
			
		}
		
	}
	connect.connect("show_naviEntity", show_assetCheckNaviEntity);
});