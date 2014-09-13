<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>资产分类管理</title>
	<script type="text/javascript">
		require([
				"dojo/_base/kernel",
				"dijit/registry",
				"dojo/_base/connect",
				"dijit/Menu",
				"dijit/MenuItem",
				"dojo/data/ItemFileReadStore",
				"dijit/Tree",
				"dijit/tree/ForestStoreModel",
				"dijit/layout/BorderContainer",
				"dojox/layout/ContentPane",
				"dijit/form/SimpleTextarea",
				"dijit/form/Button"
			], function(kernel,registry,connect,Menu, MenuItem,ItemFileReadStore,Tree,ForestStoreModel){
			
			var assetCategory_treenode;
			treeOnLoad = function(){
				var menu = registry.byId("assetCategory_tree_menu");
				var tree = registry.byId("assetCategory_tree");
				menu.bindDomNode(tree.domNode);
				connect.connect(menu,"_openMyself",tree,function(e){
					assetCategory_treenode = registry.getEnclosingWidget(e.target);
				});
			}
			
			kernel.addOnLoad(function(){
				if(registry.byId("assetCategory_tree_menu")) return;
				var menu = new Menu({
					id: 'assetCategory_tree_menu',
					selector: ".dijitTreeNode"
				});
				menu.addChild(new MenuItem({
					label: "添加分类",
					disabled:false,
					iconClass:'docCreateIcon',
					onClick:function() {createSubassetCategory(assetCategory_treenode)}
				}));
				menu.addChild(new MenuItem({
					label: "编辑分类",
					iconClass:"docOpenIcon",
					disabled:false,
					onClick:function(){editSubassetCategory(assetCategory_treenode)}
				}));
				menu.addChild(new MenuItem({
					label: "删除分类",
					iconClass:"docDeleteIcon",
					disabled:false,
					onClick:function(){deleteSubassetCategory(assetCategory_treenode)}
				}));
				
			});
			
			createSubassetCategory = function(selectedTreeNode){
				var w = registry.byId("assetCategoryEditPane");
				var href = "${createLink(controller:'assetConfig',action:'assetCategoryCreate')}";
				href = href + "?companyId=${company?.id}";
				if(!assetCategory_treenode.item.root){
					href = href + "&parentId="+assetCategory_treenode.item.id;
				}
				w.attr("href",href);
			}
			
			editSubassetCategory = function(selectedTreeNode){
				if(!selectedTreeNode.item.root){
					var w = registry.byId("assetCategoryEditPane");
					var tree = registry.byId("assetCategory_tree");
					
					if(tree.model==null) var store = tree.store;
					else store = tree.model.store;
					
					var href = "${createLink(controller:'assetConfig',action:'assetCategoryShow')}";
					var href = href+"/"+selectedTreeNode.item.id+"?categoryName="+encodeURI(selectedTreeNode.item.name);
					alert(href);
					w.attr("href",href);
				}
			}

			deleteSubassetCategory = function(selectedTreeNode){
				var w = registry.byId("assetCategoryEditPane");
				var tree = registry.byId("assetCategory_tree");
				if(tree.model==null) var store = tree.store;
				else store = tree.model.store;
							
				rosten.confirm("您是否将删除所选中的分类？").callback = function(){
					var href = "${createLink(controller:'assetConfig',action:'assetCategoryDelete')}";
					if(!selectedTreeNode.item.root){
						href = href + "/"+selectedTreeNode.item.id;
						w.attr("href",href);
					}
				}
			}
			
			refreshAssetCategoryTree = function(){
				var tree = registry.byId("assetCategory_tree");
				if(tree){
					var store = new ItemFileReadStore({url:"${createLink(controller:'assetConfig',action:'assetCategoryTreeDataStore',params:[companyId:company?.id])}"});
					tree.destroy();
					var div = document.createElement("div");
					var treeModel = new ForestStoreModel({ 
				    	store: store, // the data store that this model connects to 
				    	query: {parentId:null}, // filter multiple top level items 
				    	rootLabel: "资产分类", 
				    	childrenAttrs: ["children"] // children attributes used in data store. 
					}); 
					var tree = new Tree({
						id:"assetCategory_tree",
						model: treeModel,
						onClick:function(item){
							if(item && !item.root){
								var w = registry.byId("assetCategoryEditPane");
								var href = "${createLink(controller:'assetConfig',action:'assetCategoryShow')}";
								var href = href+"/"+item.id;
								w.attr("href",href);
							}
						},
						onLoad:treeOnLoad,
						autoExpand:true,
						showRoot:false,
						openOnClick:false,openOnDblClick:true},div);
					var p = registry.byId("assetCategoryTreePane");
					p.domNode.appendChild(tree.domNode);
				}
			}
			
			getItem = function(){
				var tree = registry.byId('assetCategory_tree');
				if(tree.selectedItem){
					if(tree.selectedItem != tree.model.root){
						console.log(tree.selectedItem.name);
					}else{
						console.log("root 节点....");
					}
				}else{
					alert("请选择节点");
				}
				
			};
		});
	</script>
</head>
<body>
	<g:set var="dataurl" scope="page"> ${createLink(controller:'assetConfig',action:'assetCategoryTreeDataStore',params:[companyId:company?.id])}</g:set>
	<div data-dojo-id="treeDataStore" data-dojo-type="dojo/data/ItemFileReadStore" data-dojo-props='url:"${dataurl}"'></div>

	<div data-dojo-type="dijit/layout/BorderContainer" data-dojo-props='style:"height:100%;padding:0"'>
		
		<div id="assetCategoryTreePane" data-dojo-type="dojox/layout/ContentPane" data-dojo-props="region:'leading',splitter:true,style:'width:280px'">
			<div id="assetCategory_tree" data-dojo-type="dijit.Tree" data-dojo-props='store:treeDataStore, query:{parentId:null},
				//label:"资产分类",
				autoExpand:true, 
				showRoot:false,
				onLoad:function(){treeOnLoad()}'>
				<script type="dojo/method" data-dojo-event="onClick" data-dojo-args="item">
					if(item && !item.root){
						var w = dijit.byId("assetCategoryEditPane");
						var href = "${createLink(controller:'assetConfig',action:'assetCategoryShow')}";
						var href = href+"/"+item.id+"?categoryName="+encodeURI(item.name);
						w.attr("href",href);
					}
				</script>
			</div>
		</div>
		<div id="assetCategoryEditPane" data-dojo-type="dojox/layout/ContentPane" 
			data-dojo-props="style:'padding:0px',renderStyles:true,region:'center'">
		</div>
	</div>

</body>
</html>