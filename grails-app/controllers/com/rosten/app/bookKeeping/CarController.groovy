package com.rosten.app.bookKeeping

import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart

class CarController {

	def carService
	def springSecurityService
	
	def carRegisterSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//车辆基本信息---------------------------
		def carBaseInfor = new CarBaseInfor()
		if(params.carBaseInforId && !"".equals(params.carBaseInforId)){
			carBaseInfor = CarBaseInfor.get(params.carBaseInforId)
		}else{
			carBaseInfor.company = company
		}
		carBaseInfor.properties = params
		carBaseInfor.clearErrors()
		//----------------------------------------
		
		//车辆登记信息保存-------------------------------
		def carRegister = new CarRegister()
		if(params.id && !"".equals(params.id)){
			carRegister = CarRegister.get(params.id)
		}else{
			carRegister.company = company
			carRegister.carBaseInfor = carBaseInfor
		}
		carRegister.properties = params
		carRegister.clearErrors()
		
		//特殊字段信息处理
		carRegister.buyDate = Util.convertToTimestamp(params.buyDate)
		carRegister.userDepart = Depart.get(params.allowdepartsId)
		
		if(carRegister.save(flush:true)){
			json["result"] = "true"
		}else{
			carRegister.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	def carRegisterDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def carRegister = CarRegister.get(it)
				if(carRegister){
					carRegister.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def carRegisterGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = carService.getCarRegisterListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = carService.getCarRegisterDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = carService.getCarRegisterCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	
	def carRegisterAdd ={
		redirect(action:"carRegisterShow",params:params)
	}
	
	def carRegisterShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def carRegister = new CarRegister()
		if(params.id){
			carRegister = CarRegister.get(params.id)
		}
		
		model["company"] = company
		model["carRegister"] = carRegister
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/bookKeeping/carRegister',model:model)
	}
   
}
