package com.rosten.app.assetCheck

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company
import com.rosten.app.system.User

import java.text.SimpleDateFormat
import java.util.Date

/**
 * 我的资产盘点任务
 * @Rosten
 */
class MyTask {
	String id
	
	User user
	
	//任务单号
	@GridColumn(name="任务单号",colIdx=1,formatter="myTask_formatTopic",width="150px")
	def getTaskNumber(){
		return inventoryTask.taskNum
	}
	
	//盘点资产类型
	@GridColumn(name="盘点资产类型",colIdx=2,width="90px")
	def getTaskName(){
		return inventoryTask.getCategoryName()
	}
	
	@GridColumn(name="任务描述",colIdx=3)
	def getTaskDesc(){
		return inventoryTask.taskDesc
	}
	
	//盘点人员
	@GridColumn(name="盘点人员",colIdx=4,width="60px")
	def getTaskUserName(){
		return user.getFormattedName()
	}
	
	//任务开始时间
	@GridColumn(name="开始时间",colIdx=5,width="130px")
	def getStartDate(){
		return inventoryTask.getFormattedStartDate()
	}
	
	//任务结束时间
	@GridColumn(name="结束时间",colIdx=6,width="130px")
	def getEndDate(){
		return inventoryTask.getFormattedEndDate()
	}
	
	//任务完成状态
	@GridColumn(name="完成情况",colIdx=7,width="60px")
	String status = "未完成"
	
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
	
	static belongsTo = [company:Company,inventoryTask:InventoryTask]
	
    static constraints = {
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "RS_TSK_MY"
		
	}
	def beforeDelete(){
		MyTask.withNewSession{session ->
			
			TaskCards.findAllByMyTask(this).each{item->
				item.delete()
			}
			
			session.flush()
		}
	}
}
