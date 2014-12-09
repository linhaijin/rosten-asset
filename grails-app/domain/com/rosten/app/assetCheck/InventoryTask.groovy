package com.rosten.app.assetCheck

import com.rosten.app.annotation.GridColumn
import com.rosten.app.assetConfig.AssetCategory;

import java.text.SimpleDateFormat
import java.util.Date

import com.rosten.app.system.Company
import com.rosten.app.system.Depart
import com.rosten.app.util.SystemUtil
import com.rosten.app.system.User

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
	boolean isAllDepart = true
	
	@GridColumn(name="盘点部门",colIdx=3)
	def getDepartName(){
		if(isAllDepart) return "全部"
		if(inventoryDeparts){
			def _list = inventoryDeparts.collect { elem ->
				elem.departName
			}
			return _list.join(",")
		}else return ""
	}
	def getDepartId(){
		if(inventoryDeparts){
			def _list = inventoryDeparts.collect { elem ->
				elem.id
			}
			return _list.join(",")
		}
	}
	
	
	//盘点资产类型
	boolean isAllCategory = true
	
	@GridColumn(name="资产盘点类型",colIdx=4)
	def getCategoryName(){
		if(isAllCategory) return "全部"
		if(inventoryCategorys){
			def _list = inventoryCategorys.collect { elem ->
				elem.categoryName
			}
			return _list.unique().join(",")
		}else return ""
	}
	def getCategoryId(){
		if(inventoryCategorys){
			def _list = inventoryCategorys.collect { elem ->
				elem.id
			}
			return _list.join(",")
		}else return ""
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
	
	def getReceiveManId(){
		if(receiveUsers){
			def _list = receiveUsers.collect { elem ->
				elem.id
			}
			return _list.join(",")
		}else return ""
	}
	
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
	
	static hasMany=[myTasks:MyTask,inventoryDeparts:Depart,inventoryCategorys:AssetCategory,receiveUsers:User]
	
    static constraints = {
		taskNum nullable:false ,blank: false, unique: true
		taskDesc nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "RS_TSK_IV"
		
		//兼容mysql与oracle
		def systemUtil = new SystemUtil()
		if(systemUtil.getDatabaseType().equals("oracle")){
			taskDesc sqlType:"clob"
		}else{
			taskDesc sqlType:"text"
		}
	}
}
