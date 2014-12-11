<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>批量导入</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>
  
<body>
	<div style="text-Align:center">
        <div class="rosten_form" style="width:800px;text-align:left">
            <fieldset class="fieldset-form">
                <legend class="tableHeader">扫描枪盘点</legend>
                <table class="tableData">
                    <tbody>
						<tr>
						    <td width="120"><div align="right">资产编号：</div></td>
						    <td width="250">
	                           	<input id="registerNum" data-dojo-type="dijit/form/ValidationTextBox" 
	                               	data-dojo-props='id:"registerNum",name:"registerNum",
	                               		value:""
	                           	'/>
				            </td>
						    <td width="120"><div align="right">资产名称：</div></td>
						    <td width="250">
						    	<input id="assetName" data-dojo-type="dijit/form/ValidationTextBox" 
	                               	data-dojo-props='name:"allowCategoryName",
	                               		disabled:true,value:""
	                           	'/>
				           	</td>
						</tr> 
						<tr>
						    <td><div align="right">规格型号：</div></td>
						    <td>
	                           	<input id="specifications" data-dojo-type="dijit/form/ValidationTextBox" 
	                               	data-dojo-props='name:"specifications",
	                               		disabled:true,value:""
	                           	'/>
				            </td>
						    <td><div align="right">购买日期：</div></td>
						    <td>
						    	<input id="buyDate" data-dojo-type="dijit/form/ValidationTextBox" 
	                               	data-dojo-props='name:"buyDate",
	                               		disabled:true,value:""
	                           	'/>
				           	</td>
						</tr>  
						<tr>
						    <td><div align="right">价格（元）：</div></td>
						    <td>
	                           	<input id="onePrice" data-dojo-type="dijit/form/ValidationTextBox" 
	                               	data-dojo-props='name:"onePrice",
	                               		disabled:true,value:""
	                           	'/>
				            </td>
						    <td><div align="right">存放地点：</div></td>
						    <td>
						    	<input id="storagePosition" data-dojo-type="dijit/form/ValidationTextBox" 
	                               	data-dojo-props='name:"storagePosition",
	                               		disabled:true,value:""
	                           	'/>
				           	</td>
						</tr>    
						<tr>
						    <td><div align="right">使用人：</div></td>
						    <td>
	                           	<input id="purchaser" data-dojo-type="dijit/form/ValidationTextBox" 
	                               	data-dojo-props='name:"purchaser",
	                               		disabled:true,value:""
	                           	'/>
				            </td>
						    <td><div align="right">部门：</div></td>
						    <td>
						    	<input id="depart" data-dojo-type="dijit/form/ValidationTextBox" 
	                               	data-dojo-props='name:"depart",
	                               		disabled:true,value:""
	                           	'/>
				           	</td>
						</tr>            	            
						<tr style="text-align:center;margin-top:10px">
							<td colspan="4">
								<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){zdpd_search()}'>资产搜索</button>
								<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){zdpd_pdrk()}'>确定</button>
								<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){rosten.variable.dialog.hide();rosten.variable.dialog.destroy()}'>取消</button>
								
							</td>
						</tr>
                    </tbody>
                </table>
				
				
            </fieldset>
		</div>
	</div>
</body>
</html>
