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
            <th width="8%">申请编号</th>
            <td width="18%">
            	<input id="s_registerNum" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true
               '/>
            </td>
            <th width="8%">资产名称</th>
            <td width="18%">
            	<input id="s_assetName"  data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true
               '/>
            </td>
            <th width="8%">资产分类</th>
            <td width="18%">
            	<div id="s_category" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:""
	            '>
	            	 <g:each in="${categoryList}" var="item">
	                	<option value="${item.categoryName }">${item.categoryName }</option>
	                </g:each>
	            </div>
            </td>
            <td>
            	<div class="btn">
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){mineApply_search()}'>查询</button>
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){mineApply_resetSearch()}'>重置</button>
              	</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
	
</body>
</html>
