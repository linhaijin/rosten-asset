/**
 * @author rosten
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	carRegister_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("carRegister", rosten.webPath + "/car/carRegisterAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	carRegister_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/car/carRegisterDelete", content,rosten.deleteCallback);
		};
	};
	
	carRegister_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:carRegister_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	carRegister_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("carRegister", rosten.webPath + "/car/carRegisterShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	/*
	 * 此功能默认必须存在
	 */
	show_carNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		switch (oString) {
			case "carRegister":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/carAction/carRegisterView?userId=" + userid,
	                gridSrc : rosten.webPath + "/car/carRegisterGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
		}
		
	}
	connect.connect("show_naviEntity", show_carNaviEntity);
});
