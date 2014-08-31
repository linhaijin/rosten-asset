package com.rosten.app.bookKeeping

import java.text.SimpleDateFormat
import java.util.Date

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company
import com.rosten.app.system.Depart
/**
 * 房屋登记
 * @author ercjlo
 *
 */
class HouseRegister {
    String id
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= "20"+nowDate.time
		return SeriesDate
	}
	//资产编号
	@GridColumn(name="资产编号",colIdx=1,formatter="houseRegister_formatTopic")
	String registerNum = getFormattedSeriesDate()
	
	//资产分类名称
	@GridColumn(name="资产分类",colIdx=2)
	String assetCategory = "房屋"
	
	//资产名称
	@GridColumn(name="资产名称",colIdx=3)
	String assetName
	
	//管理部门
	Depart userDepart
	
	@GridColumn(name="管理部门",colIdx=4)
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
	
	//总金额
	@GridColumn(name="总金额",colIdx=7)
	Double totalPrice = 0
	
	//事业收入
	Double undertakingRevenue = 0
	
	//财政拨款
	Double fiscalAppropriation = 0
	
	//其他资金
	Double otherFund = 0
	
	//建筑面积
	Double houseArea
	
	//采购组织形式
	String organizationalType
	
	//备注
	String remark
	
	//产权形式
	String rightModality = "有产权"
	
	//建筑结构
	String houseStructure = "砖混结构"
	
	//权属证明
	String ownershipProve
	
	//权属证号
	String ownershipNo
	
	//发证时间
	Date certificationDate = new Date()
	def getFormattedShowCertificationDate(){
		if(certificationDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(certificationDate)
		}else{
			return ""
		}
	}
	
	//坐落位置
	String houseLocated
	
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
	
	String seriesNo
	
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
		totalPrice nullable:false,blank:false
		undertakingRevenue nullable:true,blank:true
		fiscalAppropriation nullable:true,blank:true
		otherFund nullable:true,blank:true
		houseArea nullable:true,blank:true
		organizationalType nullable:true,blank:true
		remark nullable:true,blank:true
		rightModality nullable:false ,blank: false
		houseStructure nullable:false,blank:false
		ownershipProve nullable:true,blank:true
		ownershipNo nullable:true,blank:true
		certificationDate nullable:true,blank:true
		houseLocated nullable:false,blank:false
		assetStatus nullable:true,blank:true
		seriesNo nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_HOUSE_REGISTER"
		
		remark sqlType:"text"
	}
}
