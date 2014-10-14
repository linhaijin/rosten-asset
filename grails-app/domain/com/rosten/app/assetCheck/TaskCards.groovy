package com.rosten.app.assetCheck

import com.rosten.app.annotation.GridColumn

import java.text.SimpleDateFormat
import java.util.Date

class TaskCards {
	String id
	
	//盘点任务ID
	String taskId
	
	//资产卡片ID
	String cardsId
	
	//盘点结果
	String result
	
	//备注
	String remark

    static constraints = {
		taskId nullable:false,blank:false
		cardsId nullable:false,blank:false
		result nullable:true,blank:true
		remark nullable:true,blank:true
    }
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_TASK_CARDS"
		
		remark sqlType:"text"
	}
}
