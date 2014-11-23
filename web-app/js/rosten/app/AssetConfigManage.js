/**
 * @author rosten
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	assetCategory_formatTitle = function(value,rowIndex){
		return "<a href=\"javascript:assetCategory_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	assetCategory_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("assetCategory", rosten.webPath + "/assetConfig/assetCategoryShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	assetCategory_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("assetCategory", rosten.webPath + "/assetConfig/assetCategoryAdd?companyId=" + companyId + "&userid=" + userid);
	};
	assetCategory_read = function(){
		assetCategory_change();
	};
	assetCategory_change = function(){
		var unid = rosten.getGridUnid("single");
		if (unid == "")
			return;
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("assetCategory", rosten.webPath + "/assetConfig/assetCategoryShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
	};
	assetCategory_delete = function(){
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
            /*资产分类*/
            var companyId = rosten.kernel.getUserInforByKey("companyid");
            rosten.kernel.setHref(rosten.webPath + "/assetConfig/assetCategory?companyId=" + companyId, oString);
            break;
		case "assetCodeConfig":
            /*资产编号配置*/
            var companyId = rosten.kernel.getUserInforByKey("companyid");
            rosten.kernel.setHref(rosten.webPath + "/assetConfig/assetCodeConfig?companyId=" + companyId, oString);
            break;
		}
		
	}
	connect.connect("show_naviEntity", show_assetConfigNaviEntity);
});
