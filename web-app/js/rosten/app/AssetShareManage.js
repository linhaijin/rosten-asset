/**
 * @author rosten
 */
define(["dojo/dom",
        "dijit/registry",
        "rosten/kernel/behavior"], function(dom,registry) {
	
	//卡片搜索
	assetCategoryChoose_search_common = function(url,companyId,seriesNo){
                
        var qCompany = "?companyId="+encodeURI(companyId);
        
        var qSeriesNo = "&seriesNo="+encodeURI(seriesNo);
        var qControlName = "&controlName=assetAllocate";

        var qAssetCardsType = "";
        var assetCardsType;
        var assetCardsTypeSel = registry.byId("assetCardsType");
        if(assetCardsTypeSel){
            if(assetCardsTypeSel.attr("value")!=""){
                assetCardsType = assetCardsTypeSel.attr("value");
                qAssetCardsType = "&assetCardsType="+encodeURI(assetCardsType);
            }else{
                rosten.alert("注意：请选择资产类别！");
                dom.byId("assetCardsType").focus();
                return;
            }
        }

        var qAssetDepart = "";
        var assetDepart;
        var assetDepartSel = registry.byId("assetDepart");
        if(assetDepartSel){
            if(assetDepartSel.attr("value") != ""){
                assetDepart = assetDepartSel.attr("value");
                qAssetDepart = "&assetDepart=" + encodeURI(assetDepart);
            }
        }

        var qAssetUser = "";
        var assetUser;
        var assetUserSel = registry.byId("assetUser");
        if(assetUserSel){
            if(assetUserSel.attr("value") != ""){
                assetUser = assetUserSel.attr("value");
                qAssetUser = "&assetUser=" + encodeURI(assetUser);
            }
        }
        
        //2015-1-10----增加资产编号、资产名称、购买日期的查询-------------------------------
        var qassetRegisterNum = "";
        var assetRegisterNumSel = registry.byId("assetRegisterNum");
        if(assetRegisterNumSel){
            if(assetRegisterNumSel.attr("value") != ""){
                qassetRegisterNum = "&assetRegisterNum=" + encodeURI(assetRegisterNumSel.attr("value"));
            }
        }
        
        var qassetName = "";
        var assetNameSel = registry.byId("assetName");
        if(assetNameSel){
            if(assetNameSel.attr("value") != ""){
                qassetName = "&assetName=" + encodeURI(assetNameSel.attr("value"));
            }
        }
        
        var qbuyDate = "";
        var buyDateSel = registry.byId("buyDate");
        if(buyDateSel){
            if(buyDateSel.attr("value") != ""){
                qbuyDate = "&buyDate=" + encodeURI(buyDateSel.attr("value"));
            }
        }
        //---------------------------------------------------------------------
        
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
