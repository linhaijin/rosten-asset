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
	@GridColumn(name="申请人",colIdx=2,width="100px")
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
	@GridColumn(name="数量",colIdx=6,width="80px")
	int amount = 1
	
	//总金额
	@GridColumn(name="总金额（元）",colIdx=7,width="80px")
	Double totalPrice = 0
	
	//用途
	@GridColumn(name="用途",colIdx=8,width="80px")
	String usedBy = "办公"
	
	//申请状态
	@GridColumn(name="状态",colIdx=9,width="80px")
	String applyStatus = "新建"
	
	//是否生产资产卡片
	String isCreatedCards = 0
	@GridColumn(name="是否生产资产卡片",colIdx=10)
	def getCardsCreatedLabel(){
		return ["0":"未生成 ","1":"已生产"][isCreatedCards]
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
	
	//国别
	String country = "中国"
	
	static belongsTo = [company:Company]
	
    static constraints = {
		registerNum nullable:false ,blank: false, unique: true
		applyUser nullable:false,blank:false
		userCategory nullable:false,blank:false
		rootAssetCategory nullable:false,blank:false
		assetName nullable:false,blank:false
		amount nullable:false,blank:false
		totalPrice nullable:false,blank:false
		usedBy nullable:false,blank:false
		applyStatus nullable:false,blank:false
		country nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_APPLY_NOTES"
	}
}
