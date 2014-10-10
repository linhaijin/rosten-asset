package com.rosten.app.assetChange

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company
import com.rosten.app.system.Depart
/**
 * 资产报修
 * @author ercjlo
 *
 */
class AssetRepair {

	String id
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= nowDate.time
		return SeriesDate
	}
	//申请单号
	@GridColumn(name="申请单号",width="120px",colIdx=1,formatter="assetRepair_formatTopic")
	String seriesNo = getFormattedSeriesDate()
	
	//申请日期
	Date applyDate = new Date()
	@GridColumn(name="申请日期",width="120px",colIdx=2)
	def getFormattedApplyDate(){//数据列表展示
		if(applyDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm")
			return sd.format(applyDate)
		}else{
			return ""
		}
	}
	def getFormattedShowApplyDate(){//详细视图展示
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
	@GridColumn(name="申请部门",colIdx=4)
	def getDepartName(){
		if(applyDept){
			return applyDept.departName
		}else{
			return ""
		}
	}
	
	//	审批状态
	@GridColumn(name="审批状态",width="100px",colIdx=5)
	String dataStatus = "未审批"
	
	//报修原因
	String repairReason
	
	//存放地点
	String storagePosition
	
	//预约报修时间
	Date expectDate = new Date()
	def getFormattedShowExpectDate(){//详细视图展示
		if(expectDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(expectDate)
		}else{
			return ""
		}
	}
	
	//联系人
	String contacts
	
	//联系电话
	String contactPhone
	
	//维修人
	@GridColumn(name="维修人",width="120px",colIdx=6)
	String maintenanceMan
	
	//维修联系电话
	@GridColumn(name="维修联系电话",width="120px",colIdx=7)
	String maintenancePhone
	
	//维修时间
	Date maintenanceDate = new Date()
	@GridColumn(name="维修时间",width="120px",colIdx=8)
	def getFormattedShowMaintenanceDate(){//详细视图展示
		if(maintenanceDate!=null){
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd")
			return sd.format(maintenanceDate)
		}else{
			return ""
		}
	}
	
	//维修费用
	double maintenanceCost = 0
	
	//是否修复
	String repairComplete = "未修复"
	
	//维修反馈
	String feedback
	
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
		repairReason nullable:true,blank:true
		storagePosition nullable:true,blank:true
		expectDate nullable:false,blank:false
		contacts nullable:false,blank:false
		contactPhone nullable:false,blank:false
		
		maintenanceMan nullable:true,blank:true
		maintenancePhone nullable:true,blank:true
		maintenanceDate nullable:true,blank:true
		maintenanceCost nullable:true,blank:true
		repairComplete nullable:true,blank:true
		feedback nullable:true,blank:true
		
		dataStatus nullable:false,blank:false
    }
	
	static belongsTo = [company:Company]
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_ASSET_REPAIR"
		
		repairReason sqlType:"text"
		feedback sqlType:"text"
	}
}
