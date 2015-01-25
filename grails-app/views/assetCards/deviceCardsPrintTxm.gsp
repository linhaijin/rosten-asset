<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String currentPage = request.getParameter("page");

int pagesize = 12;                                   //每页显示的条数
int p = 1;                           				// 页数
if(currentPage != null && !currentPage.equals("")){
	p = Integer.parseInt(currentPage);              // 将页数字符串转成int型
}
int prep = p;                            			// 上一页
int nextp  = p;                            			// 下一页

int listsize = countSize;

int ye = 0;
if(listsize%pagesize==0){
	ye = listsize/pagesize;
}else{
	ye = listsize/pagesize+1;
}
int k = 0;    
if(nextp<ye) {
	nextp = p+1;
	k = (nextp-2)*pagesize;
}else{
	k = (nextp-1)*pagesize; 
}
if( p>1 ){
	prep = p-1;
}
int x = k+pagesize;
if(p == ye)
{
	x = (ye-1)*pagesize+(listsize%pagesize);
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="layout" content="rosten" />
    <title>电子设备资产条形码打印</title>
    <style type="text/css">
    	.rosten .dsj_form table tr{
    		height:30px;
    	}
    	body{
			overflow:auto;
		}
        .print_border table{
        	border:1px solid #CCCCCC;
        } 
		.print_border table td{
			border:1px solid #CCCCCC;
		} 
		.pt_border table{
            border-collapse: collapse;
            border: none;
        }
        .pt_border table td{
            border: solid #000 1px;
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
					rosten.init({webpath:"${request.getContextPath()}"});
					rosten.cssinit();
				});
				deviceCards_print = function(){
					window.print();
				};
				page_quit = function(){
					rosten.pagequit();
				};
			});
		turnPage = function(page){
			var companyId = "${company?.id}";
			var htmlArgs = getHTMLArgs();
			var url = stringLeft(window.location.href,"?") + "?companyId=" + encodeURI(companyId) + "&page=" + page;
			window.location.href=url;
		}
		stringLeft = function(/*String*/str,/*String*/parm){
			var index = str.indexOf(parm);
			if (index==-1) return str;
			return str.substring(0,index);
		}
		getHTMLArgs = function(){
			var args = new Object( );
			var query = location.search.substring(1);      // Get query string
			var pairs = query.split("&");                  // Break at ampersand
			for(var i = 0; i < pairs.length; i++) {
			 	var pos = pairs[i].indexOf('=');           // Look for "name=value"
			 	if (pos == -1) continue;                   // If not found, skip
			 	var argname = pairs[i].substring(0,pos);   // Extract the name
			 	var value = pairs[i].substring(pos+1);     // Extract the value
			 	value = decodeURIComponent(value);         // Decode it, if needed
			 	args[argname] = value;                     // Store as a property
			}
			return args;                                   // Return the object
		}
    </script>
</head>
<body>
<div class="rosten_action">
	<div data-dojo-type="rosten/widget/ActionBar" data-dojo-id="rosten_actionBar" 
		data-dojo-props='actionBarSrc:"${createLink(controller:'deviceCards',action:'deviceCardsPrintForm')}"'>
	</div>
</div>

<div data-dojo-type="dijit/layout/TabContainer" data-dojo-props='persist:false, tabStrip:true,style:{width:"840px",height:"710px",margin:"0 auto"}' >
	<table border="0" width="810" align="center">
<%
if(deviceCardsList != null && deviceCardsList.size()>0){
%>
	<tr>
<%
	int index = deviceCardsList.size();
   	for(int i=k;i<x;i++){
		def obj = deviceCardsList.get(i);
%>
    	<td width="260" style="padding:3px;">
	    	<div class="pt_border">
	    	<table width="260" height="150" border="0">
	    		<tr>
	    			<td width="50" height="70" align="right" style="padding-right:5px;">编号</td>
	    			<td width="210" align="left">
	    				<img src="${createLink(controller:'deviceCards',action:'getBarcode',params:[registerNum:obj.registerNum?.encodeAsHTML()])}" width="200" height="60" style="padding-left:10px;">
	    			</td>
	    		</tr>
	    		<tr>
	    			<td height="20" align="right" style="padding-right:5px;">名称</td>
	    			<td align="left"><div style="padding-left:10px;">${obj.assetName?.encodeAsHTML()}</div></td>
	    		</tr>
	    		<tr>
	    			<td height="20" align="right" style="padding-right:5px;">规格型号</td>
	    			<td align="left"><div style="padding-left:10px;">${obj.specifications?.encodeAsHTML()}</div></td>
	    		</tr>
	    		<tr>
	    			<td height="20" align="right" style="padding-right:5px;">时间</td>
	    			<td align="left"><div style="padding-left:10px;">${obj.getFormattedShowBuyDate()?.encodeAsHTML()}</div></td>
	    		</tr>
	    		<tr>
	    			<td height="20" align="right" style="padding-right:5px;">使用人</td>
	    			<td align="left"><div style="padding-left:10px;">${obj.purchaser?.encodeAsHTML()}</div></td>
	    		</tr>
	    	</table>
	    	</div>
		</td>
<%  
	if(i != 0  && (i+1)%3 == 0){out.print("</tr>");}
    }   
}
%>
		<tr>
			<td colspan="2" align="right" style="padding-right:20px;">
			  	共<%=listsize%>条 当前<%=p%>/<%=ye%>页&nbsp;&nbsp;&nbsp;&nbsp;
				<input id = "btn0" class="btn" type="button" value="首  页" onClick="return turnPage(<%=1%>)" />&nbsp;&nbsp;
				<input id = "btn1" class="btn" type="button" value="上一页" onClick="return turnPage(<%=prep%>)" />&nbsp;&nbsp;
				<input id = "btn2" class="btn" type="button" value="下一页" onClick="return turnPage(<%=nextp%>)"/>&nbsp;&nbsp;
				<input id = "btn3" class="btn" type="button" value="尾  页" onClick="return turnPage(<%=ye%>)"/>&nbsp;&nbsp;
			</td>
		</tr>
	</table>
</div>
</body>
</html>