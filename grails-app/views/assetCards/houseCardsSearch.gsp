<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<script type="text/javascript">
require(["rosten/app/Application"],function(){
	search_selectUsedDepart = function(){
		rosten.selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}",true,"house_userDepart","house_userDepartIds",false);
	};
});
</script>
</head>
<body>
	<div class="searchtab">
      <table width="100%" border="0">
        <tbody>
          <tr>
            <th width="6%">资产编号</th>
            <td width="12%">
            	<input id="house_registerNum" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true,style:{width:"140px"}
               '/>
            </td>
            <th width="6%">资产分类</th>
            <td width="12%">
            	<div id="house_category" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:"",style:{width:"140px"}'>
	            	<g:each in="${categoryList}" var="item">
	                	<option value="${item.categoryName }">${item.categoryName }</option>
	                </g:each>
	            </div>
            </td>
            <th width="6%">资产名称</th>
            <td width="12%">
            	<input id="house_assetName" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true,style:{width:"140px"}
               '/>
            </td>
            <th width="6%">归属部门</th>
            <!-- <td width="12%">
            	<div id="house_usedDepart" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:"",style:{width:"140px"}'>
	            	 <g:each in="${DepartList}" var="item">
	                	<option value="${item.departName }">${item.departName }</option>
	                </g:each>
	            </div>
            </td> -->
            <td width="12%">
            	<div style="width:140px">
            		<g:hiddenField id="house_userDepartIds" name="house_userDepartIds" data-dojo-type="dijit/form/ValidationTextBox" />
	            	<input id="house_userDepart" data-dojo-type="dijit/form/ValidationTextBox" 
	                	data-dojo-props='trim:true,readOnly:true,style:{width:"120px",float:"left"}
	               '/>
	               <div class="dijitTextBox dijitComboBox" style="width:15px;float:left">
	               		<div role="presentation" class="dijitReset dijitRight dijitButtonNode dijitArrowButton dijitDownArrowButton dijitArrowButtonContainer">
		               		<input type="text" onclick="search_selectUsedDepart()" aria-hidden="true" role="button presentation" readonly="readonly" tabindex="-1" value="▼ " 
		               		class="dijitReset dijitInputField dijitArrowButtonInner">
	               		</div>
	               </div>
               </div>
            </td>
            <td></td>
           	<td width="12%">
            	<div class="btn">
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){houseCards_search()}'>查询</button>
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){houseCards_resetSearch()}'>重置</button>
              	</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
	
</body>
</html>
