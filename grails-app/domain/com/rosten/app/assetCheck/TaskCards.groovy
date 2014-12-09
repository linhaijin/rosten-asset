package com.rosten.app.assetCheck

import com.rosten.app.annotation.GridColumn
import com.rosten.app.util.SystemUtil

import java.text.SimpleDateFormat
import java.util.Date
import com.rosten.app.system.Company
/**
 * 资产盘点任务与卡片关联
 * @author ercjlo
 *
 */
class TaskCards {
	String id
	
	//盘点任务
	MyTask myTask
	
	//资产卡片编号
	@GridColumn(name="资产编号",colIdx=1)
	String cardsRegisterNum
	
	//资产卡片名称
	@GridColumn(name="资产名称",colIdx=2)
	String cardsName
	
	//原始数量
	@GridColumn(name="原始数量",colIdx=3)
	int OldNumber = 1
	
	//当前数量
	@GridColumn(name="盘点数量",colIdx=5)
	int nowNumber = 0
	
	//盘点结果
	@GridColumn(name="盘点结果",colIdx=6)
	String result = "盘亏"
	
	//备注
	@GridColumn(name="备注",colIdx=7)
	String remark

    static constraints = {
		remark nullable:true,blank:true
    }
	static belongsTo = [company:Company]
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "RS_TSK_CAD"
		
		//兼容mysql与oracle
		def systemUtil = new SystemUtil()
		if(systemUtil.getDatabaseType().equals("oracle")){
			remark sqlType:"clob"
		}else{
			remark sqlType:"text"
		}
	}
}
