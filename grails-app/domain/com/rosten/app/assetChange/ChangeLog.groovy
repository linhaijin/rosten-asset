package com.rosten.app.assetChange

import java.text.SimpleDateFormat
import java.util.Date;
import com.rosten.app.system.Company

class ChangeLog {
	String id
	
	String changeId
	
	String cardId
	
	//调拨，报失，报废，报修
	String changeType
	
	//卡片类型
	String cardType
	
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
	
	static belongsTo = [company:Company]
	
    static constraints = {
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_CHG_LOG"
	}
}
