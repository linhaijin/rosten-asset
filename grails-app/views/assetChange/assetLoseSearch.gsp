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
            	<input id="lose_seriesNo" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true,value:"",style:"width:140px;"'/>
            </td>
            <th width="8%">使用部门</th>
            <td width="18%">
            	<div id="lose_usedDepart" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:"",style:"width:140px;"'/>
	            	 <g:each in="${DepartList}" var="item">
	                	<option value="${item.departName }">${item.departName }</option>
	                </g:each>
	            </div>
            </td>
            <th width="8%">使用人</th>
            <td width="18%">
            	<input id="lose_usedMan"  data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true,value:"",style:"width:140px;"'/>
            </td>
            <td></td>
            <td width="12%">
            	<div class="btn">
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){assetLose_search()}'>查询</button>
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){assetLose_resetSearch()}'>重置</button>
              	</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
	
</body>
</html>
