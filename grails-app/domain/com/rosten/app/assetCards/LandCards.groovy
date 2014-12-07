package com.rosten.app.assetCards

import com.rosten.app.annotation.GridColumn
import com.rosten.app.assetConfig.AssetCategory;

import java.text.SimpleDateFormat
import java.util.Date

import com.rosten.app.system.Company
import com.rosten.app.system.Depart
import com.rosten.app.util.SystemUtil
//import com.rosten.app.bookKeeping.LandRegister
import com.rosten.app.assetApply.ApplyNotes

class LandCards {
	String id
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= "20"+nowDate.time
		return SeriesDate
	}
	//资产编号
	@GridColumn(name="资产编号",colIdx=1,formatter="landCards_formatTopic")
	String registerNum
	
	//资产分类名称
	AssetCategory userCategory
	@GridColumn(name="资产分类",colIdx=2)
	def getCategoryName(){
		if(userCategory){
			return userCategory.categoryName
		}else{
			return "土地"
		}
	}
	
	//资产名称
	@GridColumn(name="资产名称",colIdx=3)
	String assetName
	
	//归属部门
	Depart userDepart
	@GridColumn(name="归属部门",colIdx=4)
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
	@GridColumn(name="购置日期",width="106px",colIdx=6)
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
	
	//价格
	@GridColumn(name="价格（元）",colIdx=7)
	Double onePrice = 0
	
	//事业收入
	Double undertakingRevenue = 0
	
	//财政拨款
	Double fiscalAppropriation = 0
	
	//其他资金
	Double otherFund = 0
	
	//土地面积
	Double landArea = 0
	
	//坐落位置
	String landLocated
	
	//采购人
	String purchaser
	
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
	String assetStatus = "已入库"
	
	//资产操作号
	String seriesNo
	
	//规格型号
	String specifications
	
	//备注
	String remark
	
	//从资产建账创建卡片转为资产申请创建卡片
	ApplyNotes applyNotes
	
	static belongsTo = [company:Company]
	
    static constraints = {
		applyNotes nullable:true,blank:true
		registerNum nullable:false ,blank: false, unique: true
		userCategory nullable:true,blank:true
		assetName nullable:false,blank:false
		userDepart nullable:true,blank:true
		userStatus nullable:false,blank:false
		assetSource nullable:false,blank:false
		costCategory nullable:false,blank:false
		buyDate nullable:false,blank:false
		onePrice nullable:false,blank:false
		undertakingRevenue nullable:true,blank:true
		fiscalAppropriation nullable:true,blank:true
		otherFund nullable:true,blank:true
		landArea nullable:false,blank:false
		organizationalType nullable:true,blank:true
		landLocated nullable:true,blank:true
		purchaser nullable:true ,blank: true
		country nullable:true,blank:true
		assetStatus nullable:false,blank:false
		seriesNo nullable:true,blank:true
		specifications nullable:true,blank:true
		remark nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_LAND_CARDS"
		
		//兼容mysql与oracle
		def systemUtil = new SystemUtil()
		if(systemUtil.getDatabaseType().equals("oracle")){
			remark sqlType:"clob"
		}else{
			remark sqlType:"text"
		}
	}
}
