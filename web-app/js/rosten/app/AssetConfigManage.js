/**
 * @author rosten
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	zcdl_formatTitle = function(value,rowIndex){
		return "<a href=\"javascript:zcdl_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	zcdl_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("zcdl", rosten.webPath + "/assetConfig/assetCategoryShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	zcdl_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("zcdl", rosten.webPath + "/assetConfig/assetCategoryAdd?companyId=" + companyId + "&userid=" + userid);
	};
	zcdl_read = function(){
		zcdl_change();
	};
	zcdl_change = function(){
		var unid = rosten.getGridUnid("single");
		if (unid == "")
			return;
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("zcdl", rosten.webPath + "/assetConfig/assetCategoryShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
	};
	zcdl_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/assetConfig/assetCategoryDelete", content,rosten.deleteCallback);
		};
	};
	
	/*
	 * 此功能默认必须存在
	 */
	show_assetConfigNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		
		switch (oString) {
		case "assetCategory":
            var naviJson = {
                identifier : oString,
                actionBarSrc : rosten.webPath + "/assetConfigAction/assetCategoryView?userId=" + userid,
                gridSrc : rosten.webPath + "/assetConfig/assetCategoryGrid?companyId=" + companyId
            };
            rosten.kernel.addRightContent(naviJson);

            var rostenGrid = rosten.kernel.getGrid();
            break;
		}
		
	}
	connect.connect("show_naviEntity", show_assetConfigNaviEntity);
});
