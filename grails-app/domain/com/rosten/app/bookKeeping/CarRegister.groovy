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
	
	//登记号
	@GridColumn(name="登记号")
	String registerNum
	
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
	
	//机动车来源
	String carSource = "购置"
	
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
	@GridColumn(name="当前状态")
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
	
	//车辆的基本信息
	CarBaseInfor carBaseInfor
	
	//备注
	String remark
	
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
		remark nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_CAR_REGISTER"
		
		remark sqlType:"text"
	}
}
