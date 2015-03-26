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
            <th width="5%">类型</th>
            <td width="18%">
	            <div id="s_status" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:"",style:{}'>
	                <option value="调拨">调拨</option>
	                <option value="报修">报修</option>
	                <option value="报废">报废</option>
	                <option value="报失">报失</option>
	            </div>
            </td>
            <th width="5%">开始日期</th>
            <td width="18%">
	            <input id="s_startDate"  data-dojo-type="dijit/form/DateTextBox" 
	            	data-dojo-props='trim:true
	           '/>
            </td>
            <th width="5%">结束日期</th>
            <td width="18%">
	            <input id="s_endDate"  data-dojo-type="dijit/form/DateTextBox" 
	            	data-dojo-props='trim:true
	           '/>
            </td>
            <td>
            	<div class="btn">
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){static_search()}'>查询</button>
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){static_resetSearch()}'>重置</button>
              	</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
	
</body>
</html>
