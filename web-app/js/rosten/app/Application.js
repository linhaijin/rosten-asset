/**
 * @author rosten
 */
define(["dojo/_base/lang",
        "dojo/dom",
        "dojo/has",
        "dojo/json",
		"dijit/registry",
		"rosten/widget/MultiSelectDialog",
		"rosten/widget/PickTreeDialog",
		"rosten/widget/DepartUserDialog",
		"rosten/kernel/_kernel"], function(lang,dom,has,json,registry,MultiSelectDialog,PickTreeDialog,DepartUserDialog) {
			
	var application = {};
    application.cssinitcommon = function() {
        //此功能只添加css文件
        var _rosten = window.opener.rosten;
        var dojocss = _rosten.dojothemecss;
        var rostencss = _rosten.rostenthemecss;

        rosten.addDojoThemeCss(dojocss);
        rosten.addRostenCss(rostencss);
    };
    application.cssinit = function() {
        var _rosten = window.opener.rosten;
        var dojocss = _rosten.dojothemecss;
        var rostencss = _rosten.rostenthemecss;

        rosten.replaceDojoTheme(dojocss, false);
        rosten.replaceRostenTheme(rostencss);
    };
    /*
     * 关闭当前窗口，并刷新父文档视图
     */
    application.pagequit = function() {
    	if(window.opener.rosten.kernel){
    		window.opener.rosten.kernel.refreshGrid();
    	}
        window.close();
    };
    application.selectDialog = function(dialogTitle,id,url,flag,defaultValue,reload){
		/*
		 * dialogTitle:dialog中的titile
		 * id:dialog的id号需唯一
		 * url:url
		 * flag：是否多选，true为多选，默认为false
		 * reload:是否重新载入
		 * defaultValue：对话框中显示的值,为[]数组
		 */
		if (!(rosten[id] && registry.byId(id))) {
			rosten[id] = new MultiSelectDialog({
				title:dialogTitle,
		        id: id,
				single:!flag,
				datasrc:url
			});
			if(defaultValue!=undefined){
				rosten[id].defaultvalues = defaultValue;
			}
			rosten[id].open();
		}else{
			rosten[id].single = !flag;
			if(defaultValue!=undefined){
				rosten[id].defaultvalues = defaultValue;
			}
			rosten[id].open();
			if(reload!=undefined && reload==true){
				rosten[id].datasrc = url;
				rosten[id].refresh();
			}else{
				rosten[id].simpleRefresh();
			}
		}
		
	};
	application.selectFlowUser = function(url,type){
        var id = "sys_flowUserDialog";

        if (rosten[id] && registry.byId(id)) {
        	if (rosten[id].initialized == false) {
        		rosten[id].buildContent(rosten[id].contentPane);
        		rosten[id].buildControl(rosten[id].controlPane);
        		rosten[id].initialized = true;
			}
            rosten[id].refresh();
        } else {
            var args = {
                url : url,
                type:type
            };
            rosten[id] = new DepartUserDialog(args);
            if (rosten[id].initialized == false) {
        		rosten[id].buildContent(rosten[id].contentPane);
        		rosten[id].buildControl(rosten[id].controlPane);
        		rosten[id].initialized = true;
			}
        }
//        var _data = rosten[id].getData();
//        if(_data && _data.length==1){
//            //直接调用
//        	rosten[id].doAction();
//        }else{
//			//显示对话框
//        	rosten[id].open();
//	    }
        return rosten[id];
    };
    application.selectUser = function(url,type,inputName,inputId){
        var id = "sys_userDialog";

        if (rosten[id] && registry.byId(id)) {
            rosten[id].open();
            rosten[id].refresh();
        } else {
            var args = {
                url : url,
                title:"用户选择",
                type:type
            };
            rosten[id] = new DepartUserDialog(args);
            rosten[id].open();
        }
        rosten[id].callback = function(data) {
            var _data = [];
            var _data_1 = [];
            for (var k = 0; k < data.length; k++) {
                var item = data[k];
                _data.push(item.name);
                _data_1.push(item.value);
            }
            if( inputName !=undefined){
                registry.byId(inputName).attr("value", _data.join(","));
            }
            if( inputId !=undefined){
                if(registry.byId(inputId)){
            		registry.byId(inputId).attr("value", _data_1.join(","));
            	}else{
            		dom.byId(inputId).value = _data_1.join(",");
            	}
            }
        }; 
        return rosten[id];
    };
	application.selectDepart = function(url,type,inputName,inputId,showRoot) {
        var id = "sys_departDialog";
        
        if (rosten[id] && registry.byId(id)) {
            rosten[id].open();
            rosten[id].refresh();
        } else {
            var args = {
                url : url,
//                rootLabel : "部门层级",
                showCheckBox : type,
                title:"部门选择",
                folderClass : "departTree"
            };
            if(showRoot!=undefined && !showRoot){
            	args.showRoot=false;
            }else{
            	args.showRoot=true;
            	args.rootLabel = "部门层级";
            }
            rosten[id] = new PickTreeDialog(args);
            rosten[id].open();
        }
        rosten[id].callback = function(data) {
            var _data = [];
            var _data_1 = [];
            for (var k = 0; k < data.length; k++) {
                var item = data[k];
                _data.push(item.name);
                _data_1.push(item.id);

            }
            if( inputName !=undefined){
            	registry.byId(inputName).attr("value", _data.join(","));
            }
            if( inputId !=undefined){
            	if(registry.byId(inputId)){
            		registry.byId(inputId).attr("value", _data_1.join(","));
            	}else{
            		dom.byId(inputId).value = _data_1.join(",");
            	}
            	
            }
        };
        return rosten[id];
    };
    application.selectAssetCategory = function(url,type,inputName,inputId) {
        var id = "sys_departDialog";

        if (rosten[id] && registry.byId(id)) {
            rosten[id].open();
            rosten[id].refresh();
        } else {
            var args = {
                url : url,
                rootLabel : "资产分类",
                showCheckBox : type,
                title:"资产分类选择",
                folderClass : "departTree"
            };
            rosten[id] = new PickTreeDialog(args);
            rosten[id].open();
        }
        rosten[id].callback = function(data) {
            var _data = [];
            var _data_1 = [];
            for (var k = 0; k < data.length; k++) {
                var item = data[k];
                _data.push(item.name);
                _data_1.push(item.id);

            }
            if( inputName !=undefined){
            	registry.byId(inputName).attr("value", _data.join(","));
            }
            if( inputId !=undefined){
            	if(registry.byId(inputId)){
            		registry.byId(inputId).attr("value", _data_1.join(","));
            	}else{
            		dom.byId(inputId).value = _data_1.join(",");
            	}
            	
            }
        };
        return rosten[id];
    };
    application.addAttachShowNew = function(node,jsonObj){
		var div = document.createElement("div");
		div.setAttribute("style","height:30px;width:50%;float:left");
		div.setAttribute("id",jsonObj.fileId);
		
		var a = document.createElement("a");
		if (has("ie")) {
			a.href = rosten.webPath + "/system/downloadFile/" + jsonObj.fileId;
		}else{
			a.setAttribute("href", rosten.webPath + "/system/downloadFile/" + jsonObj.fileId);
		}
		a.setAttribute("style","margin-right:20px");
		a.setAttribute("dealId",jsonObj.fileId);
		a.innerHTML = jsonObj.fileName;
		div.appendChild(a);
		
		var deleteA = document.createElement("a");
		deleteA.setAttribute("style","color:green");
		if (has("ie")) {
			deleteA.href = "javascript:rosten.deleteFile('" + node.getAttribute("id") + "','" + jsonObj.fileId + "')";
		}else{
			deleteA.setAttribute("href", "javascript:rosten.deleteFile('" + node.getAttribute("id")+"','" + jsonObj.fileId + "')");
		}
		deleteA.innerHTML = "删除";
		div.appendChild(deleteA);
		
		node.appendChild(div);
		
	};
	application.deleteFile = function(objId,attachmentId){
		
		rosten.readNoTime(rosten.webPath + "/share/deleteAttachmentFile/"+attachmentId, {},function(data){
			if(data.result==true || data.result=="true"){
				var node = dom.byId(objId);
				var item = dom.byId(attachmentId);
				node.removeChild(item);
				rosten.alert("删除成功！");
			}else{
				rosten.alert("删除失败！");
			}
		});
	};
    lang.mixin(rosten,application);
    
    return application;
});
