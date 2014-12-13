<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<style type="text/css">
	
</style>
<script type="text/javascript">
require(["rosten/app/Application"],function(){
	search_selectDepart = function(){
		rosten.selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}",true,"check_userDepart","check_userDepartIds",false);
	};
});
         

	
</script>
</head>
<body>
	<div class="searchtab">
      <table width="100%" border="0">
        
        <tbody>
          <tr>
            <th width="7%">资产编号</th>
            <td width="15%">
            	<input id="check_registerNum" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true,style:{width:"140px"}
               '/>
            </td>
            <th width="7%">资产分类</th>
            <td width="15%">
            	<div id="check_category" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:"",style:{width:"140px"}'>
	            	<g:each in="${categoryList}" var="item">
	                	<option value="${item}">${item }</option>
	                </g:each>
	            </div>
            </td>
            <th width="7%">资产名称</th>
            <td width="15%">
            	<input id="check_assetName" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true,style:{width:"140px"}
               '/>
            </td>
            <th width="7%">归属部门</th>
            <td width="15%">
            	<div style="width:145px">
            		<g:hiddenField id="check_userDepartIds" name="check_userDepartIds" data-dojo-type="dijit/form/ValidationTextBox" />
	            	<input id="check_userDepart" data-dojo-type="dijit/form/ValidationTextBox" 
	                	data-dojo-props='trim:true,readOnly:true,style:{width:"123px",float:"left"}
	               '/>
	               <div class="dijitTextBox dijitComboBox" style="width:17px;float:left">
	               		<div role="presentation" class="dijitReset dijitRight dijitButtonNode dijitArrowButton dijitDownArrowButton dijitArrowButtonContainer">
		               		<input type="text" onclick="search_selectDepart()" aria-hidden="true" role="button presentation" readonly="readonly" tabindex="-1" value="▼ " 
		               		class="dijitReset dijitInputField dijitArrowButtonInner">
	               		</div>
	               </div>
               </div>
            </td>
            <td>
            	<div class="btn">
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){assetCheck_search()}'>查询</button>
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){assetCheck_resetSearch()}'>重置</button>
              	</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
	
</body>
</html>
