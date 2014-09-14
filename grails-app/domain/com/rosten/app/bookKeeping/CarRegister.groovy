package com.rosten.app.bookKeeping

import com.rosten.app.annotation.GridColumn

import java.text.SimpleDateFormat
import java.util.Date

import com.rosten.app.system.Company
import com.rosten.app.system.Depart
import com.rosten.app.assetConfig.AssetCategory

/**
 * 车辆登记
 * @author xucy
 *
 */
class CarRegister {
	String id
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= "20"+nowDate.time
		return SeriesDate
	}
	//资产编号
	@GridColumn(name="资产编号",colIdx=1,formatter="carRegister_formatTopic")
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
//	String assetCategory = "车辆"
	
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
	
	//数量
	int amount = 1
	
	//总金额
	@GridColumn(name="总金额",colIdx=7)
	Double totalPrice = 0
	
	//事业收入
	Double undertakingRevenue = 0
	
	//财政拨款
	Double fiscalAppropriation = 0
	
	//其他资金
	Double otherFund = 0
	
	//采购组织形式
	String organizationalType
	
	//存放地点
	String storagePosition
	
	//国别
	String country = "中国"
	
	//车牌号-------------------必填项
	String carplateNo
	
	//车架号
	String carframeNo
	
	//发动机号
	String carengineNo
	
	//排气量
	String gasDisplacement
	
	//规格
	String specifications
	
	//型号
	String modelNo
	
	//厂家
	String produceFactory
	
	//供应商
	String supplier
	
	//编制情况
	String staffingStatus
	
	//车辆产地
	String productionPlace
	
	//备注
	String remark
	
	//创建时间
	Date createDate = new Date()
	@GridColumn(name="创建时间",width="106px",colIdx=8)
	def getFormattedCreatedDate(){
		if(createDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(createDate)
		}else{
			return ""
		}
	}
	
	//资产状态
	@GridColumn(name="资产状态",colIdx=9)
	String assetStatus = "新建"
	
//	String seriesNo
	
	static belongsTo = [company:Company]
	
    static constraints = {
		registerNum nullable:false ,blank: false, unique: true
		userCategory nullable:false,blank:false
		assetName nullable:false,blank:false
		userDepart nullable:false,blank:false
		userStatus nullable:false,blank:false
		assetSource nullable:false,blank:false
		costCategory nullable:false,blank:false
		buyDate nullable:false,blank:false
		amount nullable:false,blank:false
		totalPrice nullable:false,blank:false
		undertakingRevenue nullable:true,blank:true
		fiscalAppropriation nullable:true,blank:true
		otherFund nullable:true,blank:true
		organizationalType nullable:true,blank:true
		storagePosition nullable:true,blank:true
		country nullable:true,blank:true
		carplateNo nullable:false ,blank: false, unique: true
		carframeNo nullable:true,blank:true
		carengineNo nullable:true,blank:true
		gasDisplacement nullable:true,blank:true
		specifications nullable:true,blank:true
		modelNo nullable:true,blank:true
		produceFactory nullable:true,blank:true
		supplier nullable:true,blank:true
		staffingStatus nullable:true,blank:true
		productionPlace nullable:true,blank:true
		remark nullable:true,blank:true
		assetStatus nullable:false,blank:false
//		seriesNo nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_CAR_REGISTER"
		
		remark sqlType:"text"
	}
}
