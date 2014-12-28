<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
</head>
<body>
	<div class="searchtab">
      <table width="100%" border="0">
        <tbody>
          <tr>
            <th width="6%">资产编号</th>
            <td width="12%">
            	<input id="car_registerNum" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true,style:{width:"140px"}
               '/>
            </td>
            <th width="6%">资产分类</th>
            <td width="12%">
            	<div id="car_category" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:"",style:{width:"140px"}'>
	            	<g:each in="${categoryList}" var="item">
	                	<option value="${item.id }">${item.categoryName }</option>
	                </g:each>
	            </div>
            </td>
            <th width="6%">资产名称</th>
            <td width="12%">
            	<input id="car_assetName" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true,style:{width:"140px"}
               '/>
            </td>
            <th width="6%">归属部门</th>
            <td width="12%">
            	<div id="car_userDepart" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:"",style:{width:"140px"}'>
	            	<g:each in="${DepartList}" var="item">
	                	<option value="${item.departName }">${item.departName }</option>
	                </g:each>
	            </div>
            </td>
            <td></td>
            <td width="12%">
            	<div class="btn">
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){carCards_search()}'>查询</button>
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){carCards_resetSearch()}'>重置</button>
              	</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
	
</body>
</html>
