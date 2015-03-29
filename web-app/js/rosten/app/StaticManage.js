/**
 * @author rosten
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","dojo/date/stamp",
         "rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel,datestamp) {
	
	static_export =function(){
		var htmlStr = rosten.webPath + "/statistics/staticExport";
		
		var companyId = rosten.kernel.getUserInforByKey("companyid");
    	htmlStr += "?_format=HTML&companyId=" + companyId;
    	
		var s_status = registry.byId("s_status");
		if(s_status.get("value")!=""){
			htmlStr += "&s_status=" + s_status.get("value");
		}
		
		var s_startDate = registry.byId("s_startDate");
		if(s_startDate.get("value") && s_startDate.get("value")!=""){
			htmlStr += "&s_startDate=" + datestamp.toISOString(s_startDate.get("value"),{selector: "date"});
		}

		var s_endDate = registry.byId("s_endDate");
		if(s_endDate.get("value") && s_endDate.get("value")!=""){
			htmlStr += "&s_endDate=" + datestamp.toISOString(s_endDate.get("value"),{selector: "date"});
		}
		
    	window.open(htmlStr);
	};
	static_search =function(){
		var content = {};
		
		var s_status = registry.byId("s_status");
		if(s_status.get("value")!=""){
			content.s_status = s_status.get("value");
		}
		
		var s_startDate = registry.byId("s_startDate");
		if(s_startDate.get("value") && s_startDate.get("value")!=""){
			content.s_startDate = datestamp.toISOString(s_startDate.get("value"),{selector: "date"});
		}

		var s_endDate = registry.byId("s_endDate");
		if(s_endDate.get("value") && s_endDate.get("value")!=""){
			content.s_endDate = datestamp.toISOString(s_endDate.get("value"),{selector: "date"});
		}
		
		switch(rosten.kernel.navigationEntity) {
		default:
			rosten.kernel.refreshGrid(rosten.kernel.getGrid().defaultUrl, content);
			break;
		}
	};
	static_resetSearch = function(){
		switch(rosten.kernel.navigationEntity) {
		default:
			registry.byId("s_status").set("value","");
			registry.byId("s_startDate").set("value"," ");
			registry.byId("s_endDate").set("value"," ");
			
			rosten.kernel.refreshGrid();
			break;
		}	
	};
	
	/*
	 * 此功能默认必须存在
	 */
	show_staticNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		
		switch (oString) {
		case "static":
            rosten.kernel.setHref(rosten.webPath + "/statistics/chart?companyId=" + companyId, oString);
            break;
		case "staticCollect":
			rosten.kernel.setHref(rosten.webPath + "/statistics/staticSearch?companyId=" + companyId, oString);
            break;
		case "staticSearch":
			var naviJson = {
                identifier : oString,
                actionBarSrc : rosten.webPath + "/statistics/staticSearchAction?userId=" + userid,
                searchSrc:rosten.webPath + "/statistics/staticSearchView?companyId=" + companyId,
                gridSrc : rosten.webPath + "/statistics/staticSearchGrid?companyId=" + companyId
            };
            rosten.kernel.addRightContent(naviJson);
		}
		
	}
	connect.connect("show_naviEntity", show_staticNaviEntity);
});
