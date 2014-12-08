package com.rosten.app.assetApply

import java.text.SimpleDateFormat
import java.util.Date

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company
import com.rosten.app.system.Depart
import com.rosten.app.system.User
import com.rosten.app.assetConfig.AssetCategory
import com.rosten.app.assetCards.CarCards
import com.rosten.app.assetCards.LandCards
import com.rosten.app.assetCards.HouseCards
import com.rosten.app.assetCards.DeviceCards
import com.rosten.app.assetCards.BookCards
import com.rosten.app.assetCards.FurnitureCards
import com.rosten.app.gtask.Gtask
import com.rosten.app.share.*

class ApplyNotes {
	String id
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= "20"+nowDate.time
		return SeriesDate
	}
	
	def getDoubleFormat(price){
		def df = String.format("%.2f", price)
		return df
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
			return ""
		}
	}
	
	//资产分类名称
	AssetCategory userCategory
	@GridColumn(name="资产分类",colIdx=4,width="100px")
	def getCategoryName(){
		if(userCategory){
			return userCategory.categoryName
		}else{
			return ""
		}
	}
	
	//资产分类根（最大类）名称
	String rootAssetCategory
	
	//资产名称
	@GridColumn(name="资产名称",colIdx=5)
	String assetName
	
	//使用人
	@GridColumn(name="使用人")
	String userName
	
	//数量
	@GridColumn(name="数量",colIdx=6,width="50px")
	int amount = 1
	
	//单价
	
	@GridColumn(name="单价（元）",colIdx=7,width="80px")
	Double onePrice = 0
	
	//规格型号
	String specifications
	
	//是否列入年度预算
	boolean isInYearPlan = true
	def getFormattedIsInYearPlan(){
		return isInYearPlan?"是":"否"
	}
	
	//参考厂家或供应商
	String factory
	
	//用途
	@GridColumn(name="用途",colIdx=8,width="80px")
	String usedBy = "办公"
	
	//审核状态
	String applyStatus = "未审核"
	
	//是否生产资产卡片
	String isCreatedCards = "0"
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
	
	//需求时间
	Date regDate = new Date()
	def getFormattedRegDate(){
		if(regDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(regDate)
		}else{
			return ""
		}
	}
	
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
		userName nullable:false,blank:false
		amount nullable:false,blank:false
		onePrice nullable:false,blank:false
		usedBy nullable:false,blank:false
		applyStatus nullable:false,blank:false
		
		specifications nullable:true,blank:true
		isInYearPlan nullable:true,blank:true
		factory nullable:true,blank:true
		
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
	def beforeDelete(){
		ApplyNotes.withNewSession{session ->
			CarCards.findAllByApplyNotes(this).each{item->
				item.delete()
			}
			LandCards.findAllByApplyNotes(this).each{item->
				item.delete()
			}
			HouseCards.findAllByApplyNotes(this).each{item->
				item.delete()
			}
			DeviceCards.findAllByApplyNotes(this).each{item->
				item.delete()
			}
			BookCards.findAllByApplyNotes(this).each{item->
				item.delete()
			}
			FurnitureCards.findAllByApplyNotes(this).each{item->
				item.delete()
			}
			Gtask.findAllByContentId(this.id).each{item->
				item.delete()
			}
			FlowComment.findAllByBelongToId(this.id).each{item->
				item.delete()
			}
			FlowLog.findAllByBelongToId(this.id).each{item->
				item.delete()
			}
			session.flush()
		}
	}
}
