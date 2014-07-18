package com.rosten.app.assetApply

import java.text.SimpleDateFormat
import java.util.Date;

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company
import com.rosten.app.system.Depart;
import com.rosten.app.system.User
import com.rosten.app.assetConfig.AssetCategory

class ApplyNotes {
	String id
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= "20"+nowDate.time
		return SeriesDate
	}
	//申请编号
	@GridColumn(name="申请编号",colIdx=1,formatter="assetApply_formatTopic",width="120px")
	String registerNum = getFormattedSeriesDate()
	
	//申请人
	User applyUser
	@GridColumn(name="申请人",colIdx=2,width="60px")
	def getFormattedUser(){
		return applyUser.getFormattedName()
	}
	
	//申请部门
	Depart userDepart
	@GridColumn(name="申请部门",colIdx=3,width="110px")
	def getDepartName(){
		if(userDepart){
			return userDepart.departName
		}else{
			return "协会本部"
		}
	}
	
	//资产分类名称
	AssetCategory userCategory
	@GridColumn(name="资产分类",colIdx=4,width="100px")
	def getCategoryName(){
		if(userCategory){
			return userCategory.categoryName
		}else{
			return "车辆"
		}
	}
	
	//资产分类根（最大类）名称
	String rootAssetCategory
//		@GridColumn(name="设备分类",colIdx=2)
//		String assetCategory = "办公用品"
	
	//资产名称
	@GridColumn(name="资产名称",colIdx=5)
	String assetName
	
	//数量
	@GridColumn(name="数量",colIdx=6,width="50px")
	int amount = 1
	
	//总金额
	@GridColumn(name="金额（元）",colIdx=7,width="80px")
	Double totalPrice = 0
	
	//用途
	@GridColumn(name="用途",colIdx=8,width="80px")
	String usedBy = "办公"
	
	//申请状态
	String applyStatus = "新建"
	
	//是否生产资产卡片
	String isCreatedCards = 0
	@GridColumn(name="是否生产资产卡片",colIdx=11,width="100px")
	def getCardsCreatedLabel(){
		return ["0":"未生成 ","1":"已生成"][isCreatedCards]
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
	
	Date regDate = new Date()
	def getFormattedRegDate(){
		if(regDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(regDate)
		}else{
			return ""
		}
	}
	
	//国别
	String country = "中国"
	
	static belongsTo = [company:Company]
	
	//流程相关字段信息----------------------------------------------------------
	//增加已阅读人员,读者
	static hasMany=[hasReaders:User,readers:User]
	
	//当前处理人
	User currentUser

	@GridColumn(name="当前处理人",colIdx=10,width="70px")
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
	@GridColumn(name="流程状态",colIdx=9,width="70px")
	String status = "新建"
	
	//--------------------------------------------------------------------------
	
	
    static constraints = {
		registerNum nullable:false ,blank: false, unique: true
		applyUser nullable:false,blank:false
		userDepart nullable:false,blank:false
		userCategory nullable:false,blank:false
		rootAssetCategory nullable:false,blank:false
		assetName nullable:false,blank:false
		amount nullable:false,blank:false
		totalPrice nullable:false,blank:false
		usedBy nullable:false,blank:false
		applyStatus nullable:false,blank:false
		country nullable:true,blank:true
		
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
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_APPLY_NOTES"
	}
}
