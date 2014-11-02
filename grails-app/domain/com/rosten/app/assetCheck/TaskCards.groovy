package com.rosten.app.assetCheck

import com.rosten.app.annotation.GridColumn
import com.rosten.app.util.SystemUtil

import java.text.SimpleDateFormat
import java.util.Date
/**
 * 资产盘点任务与卡片关联
 * @author ercjlo
 *
 */
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
		
		//兼容mysql与oracle
		def systemUtil = new SystemUtil()
		if(systemUtil.getDatabaseType().equals("oracle")){
			remark sqlType:"clob"
		}else{
			remark sqlType:"text"
		}
	}
}
