<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title></title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>
  
<body>
	<div style="text-Align:center">
        <div class="rosten_form" style="width:500px;text-align:left">
            <fieldset class="fieldset-form">
                <legend class="tableHeader">卡片信息</legend>
                <table class="tableData">
                	<tr>
                		<td>资产名称：</td>
                		<td><input data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='
             						value:"笔记本电脑"
                           	'/></td>
                		<td>购买时间</td>
                		<td><input data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='
             						value:"2014-8-25"
                           	'/></td>
                	</tr>
                	<tr>
                		<td>使用人：</td>
                		<td><input data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='
             						value:"张三"
                           	'/></td>
                		<td>使用部门</td>
                		<td><input data-dojo-type="dijit/form/ValidationTextBox" 
                               	data-dojo-props='
             						value:"人力资源部"
                           	'/></td>
                	</tr>
                </table>
                <div style="text-align:center">
                	<img src="${resource(dir:'images/rosten/demo',file:'kp.jpg')}" >
                </div>
                
                <table class="tableData">
                    <tbody>
						                        
						<tr style="text-align:center;margin-top:10px">
							<td colspan="2">
								<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){}'>打印</button>
								<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){rosten.kernel.hideRostenShowDialog()}'>确定</button>
								<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){rosten.kernel.hideRostenShowDialog()}'>取消</button>
								
							</td>
						</tr>
                    </tbody>
                </table>
				
				
            </fieldset>
		</div>
	</div>
</body>
</html>
