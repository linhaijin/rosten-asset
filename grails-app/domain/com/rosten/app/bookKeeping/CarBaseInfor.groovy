package com.rosten.app.bookKeeping

import com.rosten.app.system.Company

class CarBaseInfor {
	String id
	
	//车牌号-------------------必填项
	String plateNumber
	
	//规格
	String specification
	
	//型号
	String version1
	
	//品牌
	String trademark
	
	//厂家
	String manufacturers
	
	//供应商
	String supplier
	
	//车架号
	String vehicleFrame
	
	//发动机号
	String engineNumber
	
	//排气量
	String gasDisplacement
	
	//车辆产地
	String productionPlace
	
    static constraints = {
		plateNumber nullable:true ,blank: false, unique: true
		specification nullable:true,blank:true
		version1 nullable:true,blank:true
		trademark nullable:true,blank:true
		manufacturers nullable:true,blank:true
		supplier nullable:true,blank:true
		vehicleFrame nullable:true,blank:true
		engineNumber nullable:true,blank:true
		gasDisplacement nullable:true,blank:true
		productionPlace nullable:true,blank:true
    }
	static belongsTo = [company:Company,carRegister:CarRegister]
	
	static mapping = {
		id generator:'uuid.hex',params:[separator:'-']
		table "ROSTEN_CAR_BASEINFOR"
	}
}
