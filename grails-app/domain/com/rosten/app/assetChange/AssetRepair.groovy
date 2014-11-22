package com.rosten.app.assetChange

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company
import com.rosten.app.system.Depart
import com.rosten.app.system.User
import com.rosten.app.util.SystemUtil
/**
 * 资产报修
 * @author ercjlo
 *
 */
class AssetRepair {

	String id
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= "20"+nowDate.time
		return SeriesDate
	}
	//申请单号
	@GridColumn(name="申请单号",width="120px",colIdx=1,formatter="assetRepair_formatTopic")
	String seriesNo = getFormattedSeriesDate()
	
	//申请日期
	Date applyDate = new Date()
	@GridColumn(name="申请日期",width="120px",colIdx=2)
	def getFormattedApplyDate(){//数据列表展示
		if(applyDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(applyDate)
		}else{
			return ""
		}
	}
	def getFormattedShowApplyDate(){//详细视图展示
		if(applyDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(applyDate)
		}else{
			return ""
		}
	}
	
	//申请人
//	@GridColumn(name="申请人",width="100px",colIdx=3)
	String applyMan
	
	//使用部门
	Depart usedDepart
	@GridColumn(name="使用部门",colIdx=3)
	def getUsedDepartName(){
		if(usedDepart){
			return usedDepart.departName
		}else{
			return ""
		}
	}
	
	//使用人
	@GridColumn(name="使用人",width="100px",colIdx=4)
	String usedMan

	//申请部门
//	Depart applyDept
//	@GridColumn(name="申请部门",colIdx=4)
//	def getDepartName(){
//		if(applyDept){
//			return applyDept.departName
//		}else{
//			return ""
//		}
//	}
	
	//资产总和
//	@GridColumn(name="资产总和",width="100px",colIdx=5)
	Double assetTotal = 0
	
	//	审批状态
//	@GridColumn(name="审批状态",width="100px",colIdx=5)
	String dataStatus = "未审批"
	
	//报修原因
	String repairReason
	
	//存放地点
	String storagePosition
	
	//预约报修时间
	Date expectDate = new Date()
	def getFormattedShowExpectDate(){//详细视图展示
		if(expectDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(expectDate)
		}else{
			return ""
		}
	}
	
	//联系人
	String contacts
	
	//联系电话
	String contactPhone
	
	//维修人
	@GridColumn(name="维修人",width="120px",colIdx=5)
	String maintenanceMan
	
	//维修联系电话
	@GridColumn(name="维修联系电话",width="120px",colIdx=6)
	String maintenancePhone
	
	//维修时间
	Date maintenanceDate = new Date()
	@GridColumn(name="维修时间",width="120px",colIdx=7)
	def getFormattedShowMaintenanceDate(){//详细视图展示
		if(maintenanceDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(maintenanceDate)
		}else{
			return ""
		}
	}
	
	//维修费用
	double maintenanceCost = 0
	
	//是否修复
	String repairComplete = "未修复"
	
	//维修反馈
	String feedback
	
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

	@GridColumn(name="当前处理人",colIdx=9,width="70px")
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
	@GridColumn(name="流程状态",colIdx=8,width="110px")
	String status = "新建"
	
	//--------------------------------------------------------------------------
	
    static constraints = {
		seriesNo nullable:false ,blank: false, unique: true
		applyDate nullable:false,blank:false
		applyMan nullable:false,blank:false
		usedDepart nullable:false,blank:false
		usedMan nullable:false,blank:false
//		applyDept nullable:false,blank:false
		repairReason nullable:true,blank:true
		storagePosition nullable:true,blank:true
		expectDate nullable:false,blank:false
		contacts nullable:false,blank:false
		contactPhone nullable:false,blank:false
		
		maintenanceMan nullable:true,blank:true
		maintenancePhone nullable:true,blank:true
		maintenanceDate nullable:true,blank:true
		maintenanceCost nullable:true,blank:true
		repairComplete nullable:true,blank:true
		feedback nullable:true,blank:true
		
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
		table "ROSTEN_AS_REPAIR"
		
		//兼容mysql与oracle
		def systemUtil = new SystemUtil()
		if(systemUtil.getDatabaseType().equals("oracle")){
			repairReason sqlType:"clob"
			feedback sqlType:"clob"
		}else{
			repairReason sqlType:"text"
			feedback sqlType:"text"
		}
	}
}
