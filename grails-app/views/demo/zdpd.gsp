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
                <div>
                	<img src="${resource(dir:'images/rosten/demo',file:'pd.jpg')}" >
                </div>
                
                <table class="tableData">
                    <tbody>
						                       
						<tr style="text-align:center;margin-top:10px">
							<td colspan="2">
								
								<button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){rosten.variable.dialog.hide();rosten.variable.dialog.destroy()}'>确定</button>
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
