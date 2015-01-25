<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<style type="text/css">
	
</style>
<script type="text/javascript">
require(["rosten/app/Application"],function(){
	search_selectUsedDepart = function(){
		rosten.selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}",true,"scrap_usedDepart","scrap_usedDepartIds",false);
	};
});
</script>
</head>
<body>
	<div class="searchtab">
      <table width="100%" border="0">
        
        <tbody>
          <tr>
            <th width="8%">申请单号</th>
            <td width="18%">
            	<input id="scrap_seriesNo" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true,value:"",style:"width:140px;"'/>
            </td>
            <th width="8%">使用部门</th>
            <!-- <td width="18%">
            	<div id="scrap_usedDepart" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:"",style:"width:140px;"'/>
	            	 <g:each in="${DepartList}" var="item">
	                	<option value="${item.departName }">${item.departName }</option>
	                </g:each>
	            </div>
            </td> -->
            <td width="18%">
            	<div style="width:140px">
            		<g:hiddenField id="scrap_usedDepartIds" name="scrap_usedDepartIds" data-dojo-type="dijit/form/ValidationTextBox" />
	            	<input id="scrap_usedDepart" data-dojo-type="dijit/form/ValidationTextBox" 
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
            <th width="8%">负责人</th>
            <td width="18%">
            	<input id="scrap_usedMan"  data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true,value:"",style:"width:140px;"'/>
            </td>
            <td></td>
            <td width="12%">
            	<div class="btn">
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){assetScrap_search()}'>查询</button>
                	<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){assetScrap_resetSearch()}'>重置</button>
              	</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
	
</body>
</html>
