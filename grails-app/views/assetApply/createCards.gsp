<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>创建资产卡片</title>
    <style type="text/css">
    	.rosten .dsj_form table tr{
    		height:30px;
    	}
    	body{
			overflow:auto;
		}
    </style>
	<script type="text/javascript">
	require(["dojo/parser",
	         "dojo/dom",
		 		"dojo/_base/kernel",
		 		"dijit/registry",
		 		"dijit/layout/TabContainer",
		 		"dijit/layout/ContentPane",
		 		"dijit/form/ValidationTextBox",
		 		"dijit/form/DateTextBox",
		 		"dijit/form/SimpleTextarea",
		 		"dijit/form/Button",
		     	"rosten/widget/ActionBar",
		     	"rosten/widget/TitlePane",
		     	"rosten/app/Application",
		     	"rosten/app/SystemApplication",
		     	"rosten/kernel/behavior"],
			function(parser,dom,kernel,registry){
				kernel.addOnLoad(function(){
					alert("11111");
				});
		});
    </script>
</head>
  
<body>
	<div style="text-Align:center">
        <div class="rosten_form" style="width:600px;text-align:left">
            <fieldset class="fieldset-form">
                <legend class="tableHeader">资产卡片清单</legend>
                
                <table class="tableData">
                    <tbody>
                       <tr>
                            <td width="80">
                                <div align="right"><span style="color:red">*</span>资产分类：</div>
                            </td>
                            <td width="250">
                            	<input data-dojo-type="dijit/form/ValidationTextBox" 
                            	data-dojo-props='value:"办公用品"' />
                            	
                            </td>
                        </tr>
                        <tr>
                            <td width="80">
                                <div align="right"><span style="color:red">*</span>资产名称：</div>
                            </td>
                            <td width="250">
                            	<input data-dojo-type="dijit/form/ValidationTextBox" 
                            	data-dojo-props='value:"笔记本电脑"' />
                            	
                            	
                            </td>
                        </tr>
                        <tr>
                            <td width="80">
                                <div align="right"><span style="color:red">*</span>用途：</div>
                            </td>
                            <td width="250">
                            	<input data-dojo-type="dijit/form/ValidationTextBox" 
                            	data-dojo-props='value:"办公"' />
                            	
                            </td>
                        </tr>
                        <tr>
                            <td width="80">
                                <div align="right"><span style="color:red">*</span>数量：</div>
                            </td>
                            <td width="250">
                            	<input data-dojo-type="dijit/form/ValidationTextBox" 
                            	data-dojo-props='value:"10"' />
                            	
                            	
                            </td>
                        </tr>
                        <tr>
                            <td width="80">
                                <div align="right"><span style="color:red">*</span>总金额：</div>
                            </td>
                            <td width="250">
                            	<input data-dojo-type="dijit/form/ValidationTextBox" 
                            	data-dojo-props='value:"60万"' />
                            	
                            	
                            </td>
                        </tr>
                        <tr>
                            <td width="80">
                                <div align="right"><span style="color:red">*</span>使用人：</div>
                            </td>
                            <td width="250">
                            	<input data-dojo-type="dijit/form/ValidationTextBox" 
                            	data-dojo-props='value:"张三"' />
                            	
                            	
                            </td>
                        </tr>
						<tr style="text-align:center;margin-top:10px">
							<td colspan="2">
								
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
