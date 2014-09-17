package com.rosten.app.assetCards

import com.rosten.app.annotation.GridColumn
import com.rosten.app.assetConfig.AssetCategory;

import java.text.SimpleDateFormat
import java.util.Date

import com.rosten.app.system.Company
import com.rosten.app.system.Depart
import com.rosten.app.bookKeeping.CarRegister

class CarCards {
	String id
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= "20"+nowDate.time
		return SeriesDate
	}
	//资产编号
	@GridColumn(name="资产编号",colIdx=1,formatter="carCards_formatTopic")
	String registerNum = getFormattedSeriesDate()
	
	//资产分类名称
	AssetCategory userCategory
	@GridColumn(name="资产分类",colIdx=2)
	def getCategoryName(){
		if(userCategory){
			return userCategory.categoryName
		}else{
			return "车辆"
		}
	}
	
	//资产名称
	@GridColumn(name="资产名称",colIdx=3)
	String assetName
	
	//管理部门
	Depart userDepart
	@GridColumn(name="使用部门",colIdx=4)
	def getDepartName(){
		if(userDepart){
			return userDepart.departName
		}else{
			return ""
		}
	}
	
	//使用状况
	@GridColumn(name="使用状况",colIdx=5)
	String userStatus = "在用"
	
	//资产来源
	String assetSource = "购置"
	
	//价值类型
	String costCategory = "原值"
	
	//购置日期
	Date buyDate = new Date()
	@GridColumn(name="购买日期",width="106px",colIdx=6)
	def getFormattedBuyDate(){
		if(buyDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(buyDate)
		}else{
			return ""
		}
	}
	def getFormattedShowBuyDate(){
		if(buyDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(buyDate)
		}else{
			return ""
		}
	}
	
	//采购组织形式
	String organizationalType
	
	//单价
	@GridColumn(name="单价",colIdx=7)
	Double onePrice = 0
	
	//事业收入
	Double undertakingRevenue = 0
	
	//财政拨款
	Double fiscalAppropriation = 0
	
	//其他资金
	Double otherFund = 0
	
	//存放地点
	String storagePosition
	
	//国别
	String country = "中国"
	
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
	
	//资产卡片状态
	@GridColumn(name="资产状态",colIdx=8)
	String assetStatus = "新建"
	
	//资产操作号
	String seriesNo
	
	//备注
	String remark
	
	static belongsTo = [company:Company,carRegister:CarRegister]
	
    static constraints = {
		registerNum nullable:false ,blank: false, unique: true
		userCategory nullable:false,blank:false
		assetName nullable:false,blank:false
		userDepart nullable:false,blank:false
		userStatus nullable:false,blank:false
		assetSource nullable:false,blank:false
		costCategory nullable:false,blank:false
		buyDate nullable:false,blank:false
		onePrice nullable:false,blank:false
		undertakingRevenue nullable:true,blank:true
		fiscalAppropriation nullable:true,blank:true
		otherFund nullable:true,blank:true
		organizationalType nullable:true,blank:true
		storagePosition nullable:true,blank:true
		country nullable:true,blank:true
		assetStatus nullable:false,blank:false
		seriesNo nullable:true,blank:true
		remark nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_CAR_CARDS"
		
		remark sqlType:"text"
	}
}
