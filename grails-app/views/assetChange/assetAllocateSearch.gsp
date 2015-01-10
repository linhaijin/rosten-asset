<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<style type="text/css">
	
</style>
<script type="text/javascript">
require(["rosten/app/Application"],function(){
	search_selectOriginalDepart = function(){
		rosten.selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}",true,"allocate_originalDepart","allocate_originalDepartIds",false);
	};
	search_selectNewDepart = function(){
		rosten.selectDepart("${createLink(controller:'system',action:'departTreeDataStore',params:[companyId:company?.id])}",true,"allocate_newDepart","allocate_newDepartIds",false);
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
            <td width="14%">
            	<input id="allocate_seriesNo" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true,value:"",style:"width:140px;"'/>
            </td>
            <th width="8%">原部门</th>
            <!-- <td width="14%">
            	<div id="allocate_originalDepart" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:"",style:"width:140px;"'>
	            	 <g:each in="${DepartList}" var="item">
	                	<option value="${item.departName }">${item.departName }</option>
	                </g:each>
	            </div>
            </td> -->
            <td width="14%">
            	<div style="width:140px">
            		<g:hiddenField id="allocate_originalDepartIds" name="allocate_originalDepartIds" data-dojo-type="dijit/form/ValidationTextBox" />
	            	<input id="allocate_originalDepart" data-dojo-type="dijit/form/ValidationTextBox" 
	                	data-dojo-props='trim:true,readOnly:true,style:{width:"120px",float:"left"}
	               '/>
	               <div class="dijitTextBox dijitComboBox" style="width:15px;float:left">
	               		<div role="presentation" class="dijitReset dijitRight dijitButtonNode dijitArrowButton dijitDownArrowButton dijitArrowButtonContainer">
		               		<input type="text" onclick="search_selectOriginalDepart()" aria-hidden="true" role="button presentation" readonly="readonly" tabindex="-1" value="▼ " 
		               		class="dijitReset dijitInputField dijitArrowButtonInner">
	               		</div>
	               </div>
               </div>
            </td>
            <th width="8%">新部门</th>
            <!-- <td width="14%">
            	<div id="allocate_newDepart" data-dojo-type="dijit/form/ComboBox"
	                data-dojo-props='trim:true,value:"",style:"width:140px;"'>
	            	 <g:each in="${DepartList}" var="item">
	                	<option value="${item.departName }">${item.departName }</option>
	                </g:each>
	            </div>
            </td> -->
            <td width="14%">
            	<div style="width:140px">
            		<g:hiddenField id="allocate_newDepartIds" name="allocate_newDepartIds" data-dojo-type="dijit/form/ValidationTextBox" />
	            	<input id="allocate_newDepart" data-dojo-type="dijit/form/ValidationTextBox" 
	                	data-dojo-props='trim:true,readOnly:true,style:{width:"120px",float:"left"}
	               '/>
	               <div class="dijitTextBox dijitComboBox" style="width:15px;float:left">
	               		<div role="presentation" class="dijitReset dijitRight dijitButtonNode dijitArrowButton dijitDownArrowButton dijitArrowButtonContainer">
		               		<input type="text" onclick="search_selectNewDepart()" aria-hidden="true" role="button presentation" readonly="readonly" tabindex="-1" value="▼ " 
		               		class="dijitReset dijitInputField dijitArrowButtonInner">
	               		</div>
	               </div>
               </div>
            </td>
            <th width="8%">新使用人</th>
            <td width="14%">
            	<input id="allocate_newUser" data-dojo-type="dijit/form/ValidationTextBox" 
                	data-dojo-props='trim:true,value:"",style:"width:140px;"'/>
            </td>
            <td width="12%">
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
