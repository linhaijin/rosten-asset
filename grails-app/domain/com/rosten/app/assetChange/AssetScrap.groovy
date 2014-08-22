package com.rosten.app.assetChange

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company
import com.rosten.app.system.Depart

/**
 * 报废报损
 * @author ercjlo
 *
 */
class AssetScrap {
    String id
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= "20"+nowDate.time
		return SeriesDate
	}
	//申请单号
	@GridColumn(name="申请单号",colIdx=1,formatter="assetScrap_formatTopic")
	String seriesNo = getFormattedSeriesDate()
	
	//申请日期
	Date applyDate = new Date()
	//数据列表展示
	@GridColumn(name="申请日期",width="106px",colIdx=2)
	def getFormattedApplyDate(){
		if(applyDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(applyDate)
		}else{
			return ""
		}
	}
	//详细视图展示
	def getFormattedShowApplyDate(){
		if(applyDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(applyDate)
		}else{
			return ""
		}
	}
	
	//申请人
	@GridColumn(name="申请人",width="100px",colIdx=3)
	String applyMan
	
	//申请部门
	Depart applyDept
	@GridColumn(name="申请部门",width="120px",colIdx=4)
	def getDepartName(){
		if(applyDept){
			return applyDept.departName
		}else{
			return ""
		}
	}
	
	//资产总和
	@GridColumn(name="资产总和",width="100px",colIdx=5)
	Double assetTotal = 0
	
	//申请描述
	@GridColumn(name="申请描述",colIdx=6)
	String applyDesc
	
	//审批状态
	@GridColumn(name="审批状态",width="100px",colIdx=7)
	String dataStatus = "未审批"
	
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
	
    static constraints = {
		seriesNo nullable:false ,blank: false, unique: true
		applyDate nullable:false,blank:false
		applyMan nullable:false,blank:false
		applyDept nullable:false,blank:false
		assetTotal nullable:false,blank:false
		applyDesc nullable:false,blank:false
		dataStatus nullable:false,blank:false
    }
	
	static belongsTo = [company:Company]
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_ASSET_SCRAP"
		
		applyDesc sqlType:"text"
	}
}
