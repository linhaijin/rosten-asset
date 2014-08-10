package com.rosten.app.bookKeeping

import com.rosten.app.annotation.GridColumn

import java.text.SimpleDateFormat
import java.util.Date

import com.rosten.app.system.Company
import com.rosten.app.system.Depart

/**
 * 车辆登记
 * @author xucy
 *
 */
class CarRegister {
	String id
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= nowDate.time
		return SeriesDate
	}
	//资产编号
	@GridColumn(name="资产编号",formatter="carRegister_formatTopic")
	String registerNum = getFormattedSeriesDate()
	
	//资产分类名称
	@GridColumn(name="资产分类")
	String assetCategory
	
	//资产名称
	@GridColumn(name="资产名称")
	String assetName
	
	//使用部门
	Depart userDepart
	
	@GridColumn(name="使用部门")
	def getDepartName(){
		if(userDepart){
			return userDepart.departName
		}else{
			return ""
		}
	}
	
	//使用人
	String userName
	
	//管理人
	String manager
	
	//资产来源
	String assetSource = "购置"
	
	//购买日期
	Date buyDate = new Date()
	
	@GridColumn(name="购买日期",width="106px")
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
	
	//使用方向	 
	String userDirection = "行政"
	
	//使用状况
	@GridColumn(name="使用状况")
	String userStatus = "在用"
	
	//价值类型
	String costCategory = "原值"
	
	//单价
//	@GridColumn(name="单价")
	Double price = 0
	
	//事业收入
	Double undertakingRevenue = 0
	
	//财政拨款
	Double fiscalAppropriation = 0
	
	//其他资金
	Double otherFund = 0 
	
	//总金额
	@GridColumn(name="总金额")
	Double totalPrice = 0
	
	//采购组织形式
	String organizationalType
	
	//存放地点
	String storePlace
	
	//编制情况
	String staffingStatus
	
	//用途分类
	String userCategory
	
	//经费来源
	String costResources
	
	//备注
	String remark
	
	//车牌号-------------------必填项
	String plateNumber
	
	//规格
	String specification
	
	//型号
	String version1
	
	//品牌
	String trademark
	
	//厂家
	String manufacturers
	
	//供应商
	String supplier
	
	//车架号
	String vehicleFrame
	
	//发动机号
	String engineNumber
	
	//排气量
	String gasDisplacement
	
	//车辆产地
	String productionPlace
	
	//资产状态
	int assetStatus
	
	//创建时间
	Date createDate = new Date()

	@GridColumn(name="创建时间",width="106px")
	def getFormattedCreatedDate(){
		if(createDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(createDate)
		}else{
			return ""
		}
	}
	
	static belongsTo = [company:Company]
	
    static constraints = {
		registerNum nullable:false ,blank: false, unique: true
		organizationalType nullable:true,blank:true
		storePlace nullable:true,blank:true
		staffingStatus nullable:true,blank:true
		userCategory nullable:true,blank:true
		costResources nullable:true,blank:true
		plateNumber nullable:true ,blank: false, unique: true
		specification nullable:true,blank:true
		version1 nullable:true,blank:true
		trademark nullable:true,blank:true
		manufacturers nullable:true,blank:true
		supplier nullable:true,blank:true
		vehicleFrame nullable:true,blank:true
		engineNumber nullable:true,blank:true
		gasDisplacement nullable:true,blank:true
		productionPlace nullable:true,blank:true
		remark nullable:true,blank:true
		assetStatus nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_CAR_REGISTER"
		
		remark sqlType:"text"
	}
}
