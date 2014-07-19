package com.rosten.app.bookKeeping

import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util

class CarController {

	def carService
	def springSecurityService
	
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
		
		model["company"] = company
		model["carRegister"] = carRegister
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/bookKeeping/carRegister',model:model)
	}
   
}
