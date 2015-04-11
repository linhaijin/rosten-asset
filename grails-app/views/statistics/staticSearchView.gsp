<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <title>系统工具</title>
	<script type="text/javascript">
		require([
				"dojo/dom",
				"dijit/registry",
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/FilteringSelect",
		 		"rosten/widget/ActionBar"
		     	],function(dom,registry){
	     	

	     	

		});
    </script>
</head>
<body>
	<div style="text-Align:center">
        <div class="rosten_form">
            <fieldset class="fieldset-form">
                <legend class="tableHeader">统计汇总</legend>
                <table class="tableData" style="text-align:left;font-size:35px">
                   <tr>
                       <td>
                           <button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){staticSearch_export("HTML")}'>页面打印</button>
                           <button data-dojo-type="dijit/form/Button" data-dojo-props='onClick:function(){staticSearch_export("XLS")}'>导出（excel）</button>
                       </td>
                          
               		</tr>
                        
                </table>
            </fieldset>
		</div>
	</div>
</body>
</html>