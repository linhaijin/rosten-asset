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
            <th width="8%">申请单号</th>
            <td width="18%">
            	<input id="allocate_seriesNo" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true,style:{width:"120px"}'/>
            </td>
            <th width="8%">原部门</th>
            <td width="18%">
            	<div id="allocate_originalDepart" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:""'>
	            	 <g:each in="${DepartList}" var="item">
	                	<option value="${item.departName }">${item.departName }</option>
	                </g:each>
	            </div>
            </td>
            <th width="8%">新部门</th>
            <td width="18%">
            	<div id="allocate_newDepart" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:""'>
	            	 <g:each in="${DepartList}" var="item">
	                	<option value="${item.departName }">${item.departName }</option>
	                </g:each>
	            </div>
            </td>
            <td>
            	<div class="btn">
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){assetAllocate_search()}'>查询</button>
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){assetAllocate_resetSearch()}'>重置</button>
              	</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
	
</body>
</html>
