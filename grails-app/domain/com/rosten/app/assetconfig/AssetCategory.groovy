package com.rosten.app.assetconfig

import com.rosten.app.annotation.GridColumn
import com.rosten.app.system.Company

class AssetCategory {
	String id
	
	//类型名称
	@GridColumn(name="大类名称",formatter="zcdl_formatTitle")
	String category
	
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
