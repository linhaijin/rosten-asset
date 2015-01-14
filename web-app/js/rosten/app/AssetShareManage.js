/**
 * @author rosten
 */
define(["dojo/dom",
        "dijit/registry",
        "rosten/kernel/behavior"], function(dom,registry) {
	
	//卡片搜索
	assetCategoryChoose_search_common = function(url,controlName,companyId,seriesNo){
        var hasArgs = false;
        
        var qCompany = "?companyId="+encodeURI(companyId);
        
        var qSeriesNo = "&seriesNo="+encodeURI(seriesNo);
        var qControlName = "&controlName="+encodeURI(controlName);

        var qAssetCardsType = "";
        var assetCardsType;
        var assetCardsTypeSel = registry.byId("assetCardsType");
        if(assetCardsTypeSel){
            if(assetCardsTypeSel.attr("value")!=""){
                assetCardsType = assetCardsTypeSel.attr("value");
                qAssetCardsType = "&assetCardsType="+encodeURI(assetCardsType);
                
                if(!hasArgs) hasArgs = true;
            }
//            else{
//                rosten.alert("注意：请选择资产类别！");
//                dom.byId("assetCardsType").focus();
//                return;
//            }
        }

        var qAssetDepart = "";
        var assetDepart;
        var assetDepartSel = registry.byId("assetDepart");
        if(assetDepartSel){
            if(assetDepartSel.attr("value") != ""){
                assetDepart = assetDepartSel.attr("value");
                qAssetDepart = "&assetDepart=" + encodeURI(assetDepart);
                
                if(!hasArgs) hasArgs = true;
            }
        }

        var qAssetUser = "";
        var assetUser;
        var assetUserSel = registry.byId("assetUser");
        if(assetUserSel){
            if(assetUserSel.attr("value") != ""){
                assetUser = assetUserSel.attr("value");
                qAssetUser = "&assetUser=" + encodeURI(assetUser);
                
                if(!hasArgs) hasArgs = true;
            }
        }
        
        //2015-1-10----增加资产编号、资产名称、购买日期的查询-------------------------------
        var qassetRegisterNum = "";
        var assetRegisterNumSel = registry.byId("assetRegisterNum");
        if(assetRegisterNumSel){
            if(assetRegisterNumSel.attr("value") != ""){
                qassetRegisterNum = "&assetRegisterNum=" + encodeURI(assetRegisterNumSel.attr("value"));
                
                if(!hasArgs) hasArgs = true;
            }
        }
        
        var qassetName = "";
        var assetNameSel = registry.byId("assetName");
        if(assetNameSel){
            if(assetNameSel.attr("value") != ""){
                qassetName = "&assetName=" + encodeURI(assetNameSel.attr("value"));
                
                if(!hasArgs) hasArgs = true;
            }
        }
        
        var qbuyDate = "";
        var buyDateSel = registry.byId("buyDate");
        if(buyDateSel){
            if(buyDateSel.attr("value") != "" && buyDateSel.attr("value") !=null ){
                qbuyDate = "&buyDate=" + encodeURI(buyDateSel.attr("value"));
                
                if(!hasArgs) hasArgs = true;
            }
        }
        //---------------------------------------------------------------------
        
        if(!hasArgs){
            rosten.alert("请正确输入搜索条件！");
            return;
        }
        
        url += qCompany+qSeriesNo+qAssetCardsType+qAssetDepart+qAssetUser+qControlName+qassetRegisterNum+qassetName+qbuyDate;
        
        var grid = registry.byId("assetCategoryChooseListGrid");
        grid.url = url;
        grid.refresh();
	};
	
	//重置卡片搜索
	assetCategoryChoose_reset = function(){
        registry.byId("assetCardsType").set("value","");
        registry.byId("assetDepart").set("value","");
        registry.byId("assetUser").set("value","");
        registry.byId("assetName").set("value","");
        registry.byId("assetRegisterNum").set("value","");
        registry.byId("buyDate").set("value"," ");
        var grid = registry.byId("assetCategoryChooseListGrid");
        grid.refresh(grid.defaultUrl);
    };
	
	//关闭卡片搜索
	assetCategoryChoose_close = function(){
		var tabContainer = registry.byId("rosten_tabContainer");
		var node = registry.byId("assetCategoryChoose_pane");
		tabContainer.removeChild(node);
		node.destroyRecursive();
		
		refreshAsset();
	};

});
