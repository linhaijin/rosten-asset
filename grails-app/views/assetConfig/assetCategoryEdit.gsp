<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>资产分类管理</title>
    <script type="text/javascript">
    	require([
		 		"dojo/_base/kernel",
		 		"dijit/registry",
		 		"dojo/dom",
		 		"dojo/_base/xhr",
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/TextBox",
		 		"dijit/form/SimpleTextarea",
		 		"dojox/layout/ContentPane",
		     	"rosten/widget/ActionBar"
		    ],
			function(kernel,registry,dom,xhr){
				
    		assetCategory_save = function(){
    			var categoryName = registry.byId("categoryName");
    			if(!categoryName.isValid()){
    				rosten.alert("资产分类名称不正确！").queryDlgClose = function(){
    					categoryName.focus();
    				};
    				return;
    			}
    			var pane = registry.byId("assetCategoryEditPane");
    			var form = dom.byId("rosten_form");

    			var xhrArgs = {
    	    		form:form,
    	        	handleAs: "text",
    	        	/*
    	        	handle: function(data, ioargs){
    		        	console.debug(ioargs.xhr.status);
    	        		switch(ioargs.xhr.status){
    	        		case 200:
    	        			pane.set("content",data);
    	        			break;
    	        		case 302:
    		        		alert("luhangyu");
    	        			break;
    	        		default:
    		        		console.debug("Unknown error");	
    	        		}
    	        	}*/

    	        	load: function(data) {
    	        		pane.set("content",data);
    	        	},
    	        	error: function(error) {
    	            	pane.set("content",error);
    	        	}
    	     	}
    			var deferred = xhr.post(xhrArgs);
    			
    		}
    		kernel.addOnLoad(function(){
    			<g:if test="${flash.refreshTree}">
    				refreshAssetCategoryTree();
    			</g:if>
    		})
    	});
    </script>
</head>
<body>
	<g:if test="${isRead=='no'}">
		<div data-dojo-type="rosten/widget/ActionBar" id="rosten_actionBar" data-dojo-props='actionBarSrc:"${createLink(controller:'assetConfig',action:'assetCategoryForm')}"'></div>
	</g:if>
	<g:if test="${flash.message}">
		<div class="message">${flash.message}</div>
	</g:if>
	<g:form name="rosten_form" url='[controller:"assetConfig",action:"assetCategorySave"]' class="rosten_form" style="width:none;height:none">
		<g:hiddenField name="id" value="${assetCategory?.id}" />
		<g:hiddenField name="parentId" value="${parentId}"/>
		<g:hiddenField name="companyId" value="${companyId}"/>
        <fieldset class="fieldset-form">
        	<legend class="tableHeader">资产分类配置</legend>
            <table class="tableData">
            	<tbody>
	                <tr>
	                    <td width="100">
	                        <div align="right" >
	                            <span style="color:red">*&nbsp;</span>分类名称：
	                        </div>
	                    </td>
	                    <td  width="180">
	                    	<input id="categoryName" data-dojo-type="dijit/form/ValidationTextBox" 
	                    		data-dojo-props='name:"categoryName",${isRead=='yes'?'disabled:"disabled",':''}
	                    			"class":"input",
	                    			trim:true,
	                    			required:true,
	                    			missingMessage:"请正确输入分类名称！",
	                    			value:"${assetCategory?.categoryName }"
	                    	'/>
	                    </td>
	           		 </tr>
					<tr>
                        <td>
                            <div align="right">
                            	<span style="color:red">*&nbsp;</span>分类代码：
                            </div>
                        </td>
                        <td>
                        	<input id="categoryCode" data-dojo-type="dijit/form/ValidationTextBox" 
	                    		data-dojo-props='name:"categoryCode",${isRead=='yes'?'disabled:"disabled",':''}
	                    			"class":"input",
	                    			trim:true,
	                    			required:true,
	                    			missingMessage:"请正确输入分类代码！",
	                    			value:"${assetCategory?.categoryCode }"
	                    	'/>
                        </td>
                    </tr>
                   <tr>
                      <td>
                          <div align="right" >内容描述：</div>
                      </td>
						<td colspan="3">
							<textarea id="description" data-dojo-type="dijit/form/SimpleTextarea"  
								data-dojo-props='name:"description",
								 	"class":"input",
									trim:true,
									style:{width:"400px"},
									value:"${assetCategory?.description }"
							'></textarea>
  						</td>
                   </tr>
	            </tbody>
	        </table>
	    </fieldset>
	</g:form>
</body>
</html>