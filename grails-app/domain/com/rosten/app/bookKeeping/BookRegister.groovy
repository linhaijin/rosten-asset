package com.rosten.app.bookKeeping

import java.text.SimpleDateFormat
import java.util.Date

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company
import com.rosten.app.system.Depart
/**
 * 图书登记
 * @author ercjlo
 *
 */
class BookRegister {
    String id
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= nowDate.time
		return SeriesDate
	}
	//资产编号
	@GridColumn(name="资产编号",formatter="bookRegister_formatTopic")
	String registerNum = getFormattedSeriesDate()
	
	//资产分类名称
	@GridColumn(name="资产分类")
	String assetCategory
	
	//资产名称
	@GridColumn(name="资产名称")
	String assetName
	
	//管理部门
	Depart userDepart
	
	@GridColumn(name="管理部门")
	def getDepartName(){
		if(userDepart){
			return userDepart.departName
		}else{
			return ""
		}
	}
	
	//使用状况
	@GridColumn(name="使用状况")
	String userStatus = "在用"
	
	//资产来源
	String assetSource = "购置"
	
	//价值类型
	String costCategory = "原值"
	
	//购置日期
	Date buyDate = new Date()
	@GridColumn(name="购置日期",width="106px")
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
	int amount = 0
	
	//总金额
	@GridColumn(name="总金额")
	Double totalPrice = 0
	
	//事业收入
	Double undertakingRevenue = 0
	
	//财政拨款
	Double fiscalAppropriation = 0
	
	//其他资金
	Double otherFund = 0
	
	//文物等级
	String antiqueRegistration
	
	//管理单位
	String manageCompany
	
	//存放地点
	String storagePosition
	
	//坐落位置
	String locatePosition
	
	//备注
	String remark
	
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
		assetCategory nullable:false,blank:false
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
		antiqueRegistration nullable:false,blank:false
		manageCompany nullable:true ,blank: true
		storagePosition nullable:true,blank:true
		locatePosition nullable:true,blank:true
		remark nullable:true,blank:true
		assetStatus nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_BOOK_REGISTER"
		
		remark sqlType:"text"
	}
}
