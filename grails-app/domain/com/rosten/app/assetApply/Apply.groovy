package com.rosten.app.assetApply

import java.text.SimpleDateFormat
import java.util.Date;

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company
import com.rosten.app.system.User

class Apply {
	String id

	User applyUser
	
	//申请
	@GridColumn(name="申请人",colIdx=1,formatter="apply_formatTopic")
	def getFormattedUser(){
		return applyUser.getFormattedName()
	}
	
	@GridColumn(name="设备分类",colIdx=2)
	String assetCategory = "办公用品"
	
	//资产名称
	@GridColumn(name="资产名称",colIdx=3)
	String assetName
	
	//数量
	@GridColumn(name="数量",colIdx=4)
	int amount = 1
	
	//总金额
	@GridColumn(name="总金额（元）",colIdx=5)
	Double totalPrice = 0
	
	//用途
	@GridColumn(name="用途",colIdx=6)
	String usedAddress = "办公"
	
	@GridColumn(name="状态",colIdx=7)
	String status = "新建"
	
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
	
    static constraints = {
		usedAddress nullable:true,blank:true
    }
	static belongsTo = [company:Company]
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_ASSET_APPLY"
		
	}
}
