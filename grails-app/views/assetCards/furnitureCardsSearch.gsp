<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<style type="text/css">
	
</style>
<script type="text/javascript">	

</script>
</head>
<body>
	<div class="searchtab">
      <table width="100%" border="0">
        
        <tbody>
          <tr>
            <th width="7%">资产编号</th>
            <td width="15%">
            	<input id="furniture_registerNum" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true,style:{width:"140px"}
               '/>
            </td>
            <th width="7%">资产分类</th>
            <td width="15%">
            	<div id="furniture_category" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:"",style:{width:"140px"}'>
	            	<g:each in="${categoryList}" var="item">
	                	<option value="${item.categoryName }">${item.categoryName }</option>
	                </g:each>
	            </div>
            </td>
            <th width="7%">资产名称</th>
            <td width="15%">
            	<input id="furniture_assetName" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true,style:{width:"140px"}
               '/>
            </td>
            <th width="7%">归属部门</th>
            <td width="15%">
            	<div id="furniture_userDepart" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:"",style:{width:"140px"}'>
	            	 <g:each in="${DepartList}" var="item">
	                	<option value="${item.departName }">${item.departName }</option>
	                </g:each>
	            </div>
            </td>
            <td>
            	<div class="btn">
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){furnitureCards_search()}'>查询</button>
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){furnitureCards_resetSearch()}'>重置</button>
              	</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
	
</body>
</html>
