package com.rosten.app.assetChange

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company
import com.rosten.app.system.Depart
import com.rosten.app.system.User;
import com.rosten.app.util.SystemUtil
/**
 * 资产调拨
 * @author ercjlo
 *
 */
class AssetAllocate {
    String id
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= "20"+nowDate.time
		return SeriesDate
	}
	//申请单号
	@GridColumn(name="申请单号",formatter="assetAllocate_formatTopic")
	String seriesNo = getFormattedSeriesDate()
	
	//申请日期
	Date applyDate = new Date()
	@GridColumn(name="申请日期",width="106px",colIdx=2)
	def getFormattedApplyDate(){//数据列表展示
		if(applyDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(applyDate)
		}else{
			return ""
		}
	}
	def getFormattedShowApplyDate(){
		if(applyDate!=null){//详细视图展示
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(applyDate)
		}else{
			return ""
		}
	}
	
	//申请人
	@GridColumn(name="申请人",width="100px",colIdx=3)
	String applyMan
	
	//原部门
	Depart originalDepart
	def getOriginalDepartName(){
		if(originalDepart){
			return originalDepart.departName
		}else{
			return ""
		}
	}
	
	//原使用人
	String originalUser
	
	//新部门
	Depart newDepart
	def getNewDepartName(){
		if(newDepart){
			return newDepart.departName
		}else{
			return ""
		}
	}
	
	//新使用人
	String newUser
	
	//调入部门
//	Depart callInDept
//	@GridColumn(name="调入部门",width="120px",colIdx=4)
//	def getInDepartName(){
//		if(callInDept){
//			return callInDept.departName
//		}else{
//			return ""
//		}
//	}
	
	//调出部门
//	Depart callOutDept
//	@GridColumn(name="调出部门",width="120px",colIdx=5)
//	def getOutDepartName(){
//		if(callOutDept){
//			return callOutDept.departName
//		}else{
//			return ""
//		}
//	}
	
	//资产总和
	@GridColumn(name="资产总和",width="100px",colIdx=5)
	Double assetTotal = 0
	
	//调拨原因
	@GridColumn(name="调拨原因",colIdx=6)
	String applyDesc
	
	//审批状态
//	@GridColumn(name="审批状态",width="100px",colIdx=7)
	String dataStatus = "未审批"
	
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
	
	//流程相关字段信息----------------------------------------------------------
	//增加已阅读人员,读者
	static hasMany=[hasReaders:User,readers:User]
	
	//当前处理人
	User currentUser

	@GridColumn(name="当前处理人",colIdx=8,width="70px")
	def getCurrentUserName(){
		if(currentUser!=null){
			return currentUser.getFormattedName()
		}else{
			return ""
		}
	}

	//当前处理部门
	String currentDepart

	//当前处理时间
	Date currentDealDate
	
	//缺省读者；*:允许所有人查看,[角色名称]:允许角色,user:普通人员查看
	String defaultReaders="[应用管理员]"
	def addDefaultReader(String userRole){
		if(defaultReaders==null || "".equals(defaultReaders)){
			defaultReaders = userRole
		}else{
			defaultReaders += "," + userRole
		}
	}
	
	//起草人
	User drafter

	def getFormattedDrafter(){
		if(drafter!=null){
			return drafter.getFormattedName()
		}else{
			return ""
		}
	}

	//起草部门
	String drafterDepart

	//流程定义id
	String processDefinitionId
	
	//流程id
	String processInstanceId
	
	//任务id
	String taskId
	
	//流程状态
	@GridColumn(name="流程状态",colIdx=7,width="110px")
	String status = "新建"
	
	//--------------------------------------------------------------------------
	
    static constraints = {
		seriesNo nullable:false ,blank: false, unique: true
		applyDate nullable:false,blank:false
		applyMan nullable:false,blank:false
		originalDepart nullable:false,blank:false
		originalUser nullable:false,blank:false
		newDepart nullable:false,blank:false
		newUser nullable:false,blank:false
//		callInDept nullable:false,blank:false
//		callOutDept nullable:false,blank:false
		assetTotal nullable:false,blank:false
		applyDesc nullable:false,blank:false
		dataStatus nullable:false,blank:false
		
		//流程相关-------------------------------------------------------------
		defaultReaders nullable:true,blank:true
		currentUser nullable:true,blank:true
		currentDepart nullable:true,blank:true
		currentDealDate nullable:true,blank:true
		drafter nullable:true,blank:true
		drafterDepart nullable:true,blank:true
		
		processInstanceId nullable:true,blank:true
		taskId nullable:true,blank:true
		processDefinitionId nullable:true,blank:true
		//--------------------------------------------------------------------
    }
	
	static belongsTo = [company:Company]
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_AS_ALLOCATE"
		
		//兼容mysql与oracle
		def systemUtil = new SystemUtil()
		if(systemUtil.getDatabaseType().equals("oracle")){
			applyDesc sqlType:"clob"
		}else{
			applyDesc sqlType:"text"
		}
	}
}
