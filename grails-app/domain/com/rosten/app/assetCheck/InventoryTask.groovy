package com.rosten.app.assetCheck

import com.rosten.app.annotation.GridColumn
import com.rosten.app.assetConfig.AssetCategory;

import java.text.SimpleDateFormat
import java.util.Date

import com.rosten.app.system.Company
import com.rosten.app.system.Depart
import com.rosten.app.util.SystemUtil
/**
 * 资产盘点任务
 * @author ercjlo
 *
 */
class InventoryTask {
	String id
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= "ZCPD_20"+nowDate.time
		return SeriesDate
	}
	
	//任务单号
	@GridColumn(name="任务单号",colIdx=1,formatter="assetCheck_formatTopic",width="150px")
	String taskNum = getFormattedSeriesDate()
	
	//制单日期
	Date makeDate = new Date()
	@GridColumn(name="制单日期",width="106px",colIdx=2)
	def getFormattedMakeDate(){
		if(makeDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(makeDate)
		}else{
			return ""
		}
	}
	
	//盘点部门
	Depart inventoryDepart
	@GridColumn(name="盘点部门",colIdx=3)
	def getDepartName(){
		if(inventoryDepart){
			return inventoryDepart.departName
		}else{
			return ""
		}
	}
	
	//盘点资产类型
	AssetCategory inventoryCategory
	@GridColumn(name="资产盘点类型",colIdx=4)
	def getCategoryName(){
		if(inventoryCategory){
			return inventoryCategory.categoryName
		}else{
			return ""
		}
	}
	
	//任务发布人
	@GridColumn(name="任务发布人",colIdx=5)
	String sendMan
	
	//任务开始日期
	Date startDate = new Date()
	@GridColumn(name="开始日期",width="106px",colIdx=6)
	def getFormattedStartDate(){
		if(startDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(startDate)
		}else{
			return ""
		}
	}
	
	//任务截止日期
	Date endDate = new Date()
	@GridColumn(name="截止日期",width="106px",colIdx=7)
	def getFormattedEndDate(){
		if(endDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(endDate)
		}else{
			return ""
		}
	}
	
	//任务启动状态
	String runStatus = 0
	@GridColumn(name="任务启动状态",colIdx=8)
	def getRunStatusLabel(){
		return ["0":"未启动 ","1":"已启动"][runStatus]
	}
	
	//任务接收人
	@GridColumn(name="任务接收人",colIdx=9)
	String receiveMan
	
	//任务完成状态
	String completeStatus = 0
	@GridColumn(name="任务完成状态",colIdx=10)
	def getCompleteStatusLabel(){
		return ["0":"未完成","1":"已完成"][completeStatus]
	}
	
	//任务状态
	String taskStatus = 0
	def getTaskStatusLabel(){
		return ["0":"新建","1":"已启动","2":"盘点中","3":"已完成"][taskStatus]
	}
	
	//创建时间
	Date createDate = new Date()
	def getFormattedCreatedDate(){
		if(createDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(createDate)
		}else{
			return ""
		}
	}
	
	//描述
	String taskDesc
	
	static belongsTo = [company:Company]
	
    static constraints = {
		taskNum nullable:false ,blank: false, unique: true
		makeDate nullable:false,blank:false
		inventoryDepart nullable:false,blank:false
		inventoryCategory nullable:false,blank:false
		sendMan nullable:false,blank:false
		startDate nullable:false,blank:false
		endDate nullable:false,blank:false
		runStatus nullable:false,blank:false
		receiveMan nullable:false,blank:false
		completeStatus nullable:false,blank:false
		taskStatus nullable:false,blank:false
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_INV_TASK"
		
		//兼容mysql与oracle
		def systemUtil = new SystemUtil()
		if(systemUtil.getDatabaseType().equals("oracle")){
			taskDesc sqlType:"clob"
		}else{
			taskDesc sqlType:"text"
		}
	}
}
