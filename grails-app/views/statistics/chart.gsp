<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>图表</title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
</head>
<style type="text/css">
	.dojoxLegendNode {border: 1px solid #ccc; margin: 5px 10px 0px;}
    .dojoxLegendText {vertical-align: text-top; padding-right: 10px}
	.charts {
		clear: both;
	}
	.chart-area {
		float: left;
		border: 1px solid #ccc;
		width:  800px;
        height: 200px;
        margin:0 auto;
	}
	.chart {
		width:  800px;
		height: 168px;
	}
	
	.chart-area-lines1 {
        height: 200px;
        width:520px;
        float:left;
	}
	.chart-lines1 {
		width:520px;
		height: 190px;
	}
	
	.chart-area-pie {
		/*border: 1px solid #ccc;*/
        height: 190px;
        width:400px;
        margin:0 auto;
	}
	.chart-pie {
		width:400px;
		height: 190px;
	}
	
	.chart-area-cols {
		/*border: 1px solid #ccc;*/
        height: 200px;
        width:520px;
        float:left;
	}
	.chart-cols {
		width:520px;
		height: 190px;
	}
	
</style>
<script>
	require([
     	"dojo/_base/kernel", 
     	"dojo/json",
     	"dojo/_base/lang",
     	"dojo/query",
     	"dojo/dom-style",
     	"dojo/dom-class",
     	"dojo/dom-construct",
     	"dijit/registry",
     	"dojo/data/ItemFileWriteStore",
     	"dojox/charting/Chart",
     	"dojox/charting/DataSeries",
     	"dojox/charting/themes/ThreeD",
     	"dojox/charting/widget/Legend",
     	"dojox/charting/axis2d/Default",
     	"dojox/charting/plot2d/Markers",
     	"dojox/charting/action2d/Tooltip",
     	"dojox/charting/action2d/Magnify",
     	"dojox/charting/plot2d/Grid",
     	"dojox/charting/action2d/MoveSlice",
     	"dojox/charting/plot2d/Pie",
     	"dojox/charting/action2d/Shake",
     	"dojox/charting/plot2d/ClusteredColumns",
     	"dojox/charting/plot2d/Columns",
     	"dojox/charting/plot2d/StackedAreas"
     	],
	function( kernel,JSON,lang,query,domStyle,domClass,domConstruct,registry,ItemFileWriteStore,
		Chart,DataSeries,ThreeD,Legend,Default,Markers,Tooltip,Magnify,Grid,MoveSlice,Pie,Shake,ClusteredColumns,Columns,StackedAreas
		) {
		
		kernel.addOnLoad(function() {
			makeCharts();
		});
		
		makeCharts = function(){
			var dom = document.getElementById("lines1");
			if(dom==undefined) return;
			
	        var chartL = new Chart("lines1");
	        
	        chartL.addPlot("default", {type: Markers});
	        chartL.addPlot("grid", { type: "Grid" ,hMajorLines: true, vMajorLines: false});
	        
	        chartL.setTheme(ThreeD)
	        
	        chartL.addAxis("x", {
	        	//title: "2014年度",
	        	//titleGap: 20, 
	        	//titleFontColor: "green",
	            //titleOrientation: "away",
	            dropLabels: false,
	            max:13,
	        	fixLower: "none", fixUpper: "none", natural: true,includeZero: true,
	        	labelFunc: function(value){
	        		if(value==0 ){
	        			return "";
	        		}else if(value ==13){
	        			return "2015年1月";
	        		}
					return value + "月";
					}
	        	});
	        	
	        chartL.addAxis("y", {
	        	title: "金额(万元)",
	        	titleGap: 20, 
	        	max:4000,
	        	titleFontColor: "green",
	            titleOrientation: "axis",
	        	fixLower: "major", fixUpper: "major", natural: true,includeZero: true, vertical: true
	        	
	        	});
	        
	        // chartL.addSeries("技术中心", new DataSeries(store, {query: {depart :"a"}}, "price"));      
	        chartL.addSeries("土地",[
				{ x: 1, y: 1500},{ x: 2, y: 2100},{ x: 3, y: 3100},{ x: 4, y: 3100},{ x: 5, y: 3000},{ x: 6, y: 3500},
	        	{ x: 7, y: 3500},{ x: 8, y: 3500},{ x: 9, y: 3500},{ x: 10, y: 3500},{ x: 11, y: 3500},{ x: 12, y: 3800}
	        ]); 
	        chartL.addSeries("房屋",[
	        	{ x: 1, y: 800},{ x: 2, y: 800},{ x: 3, y: 800},{ x: 4, y: 800},{ x: 5, y: 800},{ x: 6, y: 800},
	        	{ x: 7, y: 1100},{ x: 8, y: 1100},{ x: 9, y: 1500},{ x: 10, y: 1500},{ x: 11, y: 1500},{ x: 12, y: 1500}
	        ]);
	        chartL.addSeries("设备",[
   	        	{ x: 1, y: 2000},{ x: 2, y: 2100},{ x: 3, y: 3000},{ x: 4, y: 3500},{ x: 5, y: 3500},{ x: 6, y: 3600},
	        	{ x: 7, y: 3600},{ x: 8, y: 3000},{ x: 9, y: 3000},{ x: 10, y: 3700},{ x: 11, y: 3600},{ x: 12, y: 3600}
   	        ]);
	        chartL.addSeries("图书",[
  	        	{ x: 1, y: 500},{ x: 2, y: 500},{ x: 3, y: 600},{ x: 4, y: 800},{ x: 5, y: 800},{ x: 6, y: 1000},
        		{ x: 7, y: 1000},{ x: 8, y: 1000},{ x: 9, y: 1500},{ x: 10, y: 1500},{ x: 11, y: 1500},{ x: 12, y: 2000}
  	        ]);
	        chartL.addSeries("车辆",[
  	        	{ x: 1, y: 1000},{ x: 2, y: 1000},{ x: 3, y: 1500},{ x: 4, y: 1500},{ x: 5, y: 1500},{ x: 6, y: 1600},
        		{ x: 7, y: 1600},{ x: 8, y: 1600},{ x: 9, y: 2000},{ x: 10, y: 2000},{ x: 11, y: 2000},{ x: 12, y: 2000}
  	        ]);
	        
	        new Magnify(chartL);
	        new Tooltip(chartL);
	        
	        chartL.render();
	        
	        addLegend(chartL, "lines1_legend");

			//单位固定资产分类统计
	        var store = new ItemFileWriteStore({url: "${createLink(controller:'statistics',action:'getAssetByType',id:company?.id)}"});
	        var chartP = new Chart("pie");
            chartP.setTheme(ThreeD);
            chartP.addPlot("default", {type: Pie, radius: 80});
            chartP.addSeries("number", new DataSeries(store, {query: {id: "*"}},dojo.hitch(null, valTrans, "number")));
            chartP.render();
            
            new Tooltip(chartP);
    		new MoveSlice(chartP);
            
   		 	addLegend(chartP, "pie_legend");

			//各部门固定资产分类统计
   			//柱状图
   			var groupList = JSON.parse('${groupList}');
   			var departList = JSON.parse('${departList}');
   			
   			var store1 = new ItemFileWriteStore({url: "${createLink(controller:'statistics',action:'getAssetByDepart',id:company?.id,params:[departIds:departIds,groupNames:groupNames])}"});
   			
	        var chartC = new Chart("cols");
            chartC.setTheme(ThreeD);
            chartC.addAxis("x", {
            	natural: true,
            	labelFunc: function(value){
            		return departList[value-1];
				}
            });
            chartC.addAxis("y", {vertical: true, fixLower: "major", fixUpper: "major", includeZero: true,
                title: "总金额(万元)",
	        	titleGap: 20,
	        	titleFontColor: "green",
	            titleOrientation: "axis"
	        });
            chartC.addPlot("default", {type: ClusteredColumns,gap: 30, labels: true});

            for (var i = 0; i < groupList.length; i++) {
            	chartC.addSeries(groupList[i], new DataSeries(store1, {query: {group: groupList[i]}}, dojo.hitch(null, valTrans, "number")));
            }
            
            new Shake(chartC, "default", {shiftY: 0});
    		new Tooltip(chartC);
            
            chartC.render();
    		addLegend(chartC, "cols_legend");


			//各部门车辆统计
			
			var store2 = new ItemFileWriteStore({url: "${createLink(controller:'statistics',action:'getAssetStatic',id:company?.id,params:[departIds:departIds])}"});
			
			var chartCl = new Chart("cols_cl");
			chartCl.addPlot("default", {type: Columns,gap: 15});
			chartCl.setTheme(ThreeD);

			chartCl.addAxis("x", {
            	natural: true,
            	labelFunc: function(value){
            		return departList[value-1];
				}
            });

			chartCl.addAxis("y", {
	        	title: "车辆数(辆)",
	        	titleGap: 20, 
	        	titleFontColor: "green",
	            titleOrientation: "axis",
	        	fixLower: "major", fixUpper: "major", natural: true,includeZero: true, vertical: true
	        	
	        });
			chartCl.addSeries("机动车辆", new DataSeries(store2, {query: {type: "cl"}}, dojo.hitch(null, valTrans1, "number"))
			//,{stroke: {color: "lightblue"}, fill: "lightblue"}
			);
			
			new Tooltip(chartCl);
			chartCl.render();
	        addLegend(chartCl, "cols_legend_cl");

	        //各部门土地统计
			var chartTd = new Chart("cols_td");
			chartTd.addPlot("default", {type: Columns,gap: 15});
			chartTd.setTheme(ThreeD);

			chartTd.addAxis("x", {
            	natural: true,
            	labelFunc: function(value){
            		return departList[value-1];
				}
            });

			chartTd.addAxis("y", {
	        	title: "总金额(万元)",
	        	titleGap: 20, 
	        	titleFontColor: "green",
	            titleOrientation: "axis",
	        	fixLower: "major", fixUpper: "major", natural: true,includeZero: true, vertical: true
	        	
	        });
			chartTd.addSeries("土地", new DataSeries(store2, {query: {type: "td"}}, {y: "money", tooltip: "money"}));
			
			new Tooltip(chartTd);
			chartTd.render();
	        addLegend(chartTd, "cols_legend_td");
	        
	        //各部门土地统计
	        var chartFw = new Chart("cols_fw");
	        chartFw.addPlot("default", {type: Columns,gap: 15});
	        chartFw.setTheme(ThreeD);

	        chartFw.addAxis("x", {
            	natural: true,
            	labelFunc: function(value){
            		return departList[value-1];
				}
            });

	        chartFw.addAxis("y", {
	        	title: "总金额(万元)",
	        	titleGap: 20, 
	        	titleFontColor: "green",
	            titleOrientation: "axis",
	        	fixLower: "major", fixUpper: "major", natural: true,includeZero: true, vertical: true
	        	
	        });
	        chartFw.addSeries("房屋", new DataSeries(store2, {query: {type: "td"}}, {y: "money", tooltip: "money"}));
			
			new Tooltip(chartFw);
			chartFw.render();
	        addLegend(chartFw, "cols_legend_fw");
	    };
	    function valTrans1(value, store, item){
	        return {
	            y: store.getValue(item, value),
	            tooltip: "总金额:" + store.getValue(item, "money") + "万元"
	        };
	    };
	    function valTrans(value, store, item){
	        return {
	            y: store.getValue(item, value),
	            text:store.getValue(item,"name"),
	            tooltip: "总金额:" + store.getValue(item, value) + "万元"
	        };
	    };
		function addLegend(chart, node){
	        var legend = new Legend({chart: chart}, node);
	        dojo.connect(chart, "render", legend, "refresh");
	    }
	
	});
	
</script>
<body>
	<div data-dojo-type="dijit/layout/BorderContainer" data-dojo-props='gutters:false,style:{height:"260px"}' >
		<div data-dojo-type="rosten/widget/TitlePane" style="margin-top:1px" 
			data-dojo-props='region:"left",title:"年度固定资产增速趋势统计    (2014年度)",toggleable:false,
				height:"210px",width:"50%",style:{marginRight:"1px"},moreText:""'>
			<div class="charts">
				<div id="lines1_legend"></div>
				<div class="chart-area-lines1">
					<div id="lines1" class="chart-lines1"></div>
				</div>
			</div>
				
		</div>
		<div data-dojo-type="rosten/widget/TitlePane"
			data-dojo-props='region:"center",title:"固定资产分类统计",toggleable:false,
				height:"210px",moreText:""'>
				
			<div class="charts">
				<div id="pie_legend"></div>
				<div class="chart-area-pie">
					<div id="pie" class="chart-pie"></div>
				</div>
			</div>	
				
		</div>						
	</div>
	<div data-dojo-type="dijit/layout/BorderContainer" data-dojo-props='gutters:false,style:{height:"260px"}' >
		<div data-dojo-type="rosten/widget/TitlePane" style="margin-top:1px" 
			data-dojo-props='region:"left",title:"各部门固定资产分类统计",toggleable:false,
				height:"210px",width:"50%",style:{marginRight:"1px"},moreText:""'>
			<div class="charts">
				<div id="cols_legend"></div>
				<div class="chart-area-cols">
					<div id="cols" class="chart-cols"></div>
				</div>
			</div>
				
		</div>
		<div data-dojo-type="rosten/widget/TitlePane"
			data-dojo-props='region:"center",title:"各部门车辆统计",toggleable:false,
				height:"210px",moreText:""'>
				
			<div class="charts">
				<div id="cols_legend_cl"></div>
				<div class="chart-area-cols">
					<div id="cols_cl" class="chart-cols"></div>
				</div>
			</div>	
				
		</div>						
	</div>
	
	<div data-dojo-type="dijit/layout/BorderContainer" data-dojo-props='gutters:false,style:{height:"260px"}' >
		<div data-dojo-type="rosten/widget/TitlePane" style="margin-top:1px" 
			data-dojo-props='region:"left",title:"各部门土地统计",toggleable:false,
				height:"210px",width:"50%",style:{marginRight:"1px"},moreText:""'>
				
			<div class="charts">
				<div id="cols_legend_td"></div>
				<div class="chart-area-cols">
					<div id="cols_td" class="chart-cols"></div>
				</div>
			</div>	
			
		</div>
		<div data-dojo-type="rosten/widget/TitlePane"
			data-dojo-props='region:"center",title:"各部门土地统计",toggleable:false,
				height:"210px",moreText:""'>
				
			<div class="charts">
				<div id="cols_legend_fw"></div>
				<div class="chart-area-cols">
					<div id="cols_fw" class="chart-cols"></div>
				</div>
			</div>		
				
		</div>						
	</div>
	
</body>
</html>
