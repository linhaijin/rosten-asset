package com.rosten.app.assetconfig

import java.util.Date;

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company

import java.text.SimpleDateFormat
import java.util.Date

class AssetCategory {
	String id
	
	//类型名称
	@GridColumn(name="大类名称",formatter="zcdl_formatTitle",colIdx=1)
	String category
	
	Date createdTime = new Date()
	@GridColumn(name="创建日期",width="106px",colIdx=2)
	def getFormattedCreatedTime(){
		if(createdTime!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(createdTime)
		}else{
			return ""
		}
	}
	
	def getFormattedShowCreatedTime(){
		if(createdTime!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(createdTime)
		}else{
			return ""
		}
	}
	
	//描述
	@GridColumn(name="描述")
	String description
	
	static belongsTo = [company:Company]
	
    static constraints = {
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_ASSET_CATEGORY"
		
		description sqlType:"text"
	}
	
}
