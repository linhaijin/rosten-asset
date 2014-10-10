package com.rosten.app.assetCards

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart
import com.rosten.app.assetConfig.AssetCategory
import com.rosten.app.assetApply.ApplyNotes

class DeviceCardsController {

    def assetCardsService
	def springSecurityService
	
	def imgPath ="images/rosten/actionbar/"
	
	def deviceCardsForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "deviceCards"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	def deviceCardsView ={
		def actionList =[]
		def strname = "deviceCards"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
//		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
		actionList << createAction("删除",imgPath + "read.gif",strname + "_delete")
		actionList << createAction("提交",imgPath + "hf.gif",strname + "_submit")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
	private def createAction={name,img,action->
		def model =[:]
		model["name"] = name
		model["img"] = img
		model["action"] = action
		return model
	}
	
	def deviceCardsAdd ={
		redirect(action:"deviceCardsShow",params:params)
	}
	
	def deviceCardsShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def deviceCards = new DeviceCards()
		if(params.id){
			deviceCards = DeviceCards.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["deviceCards"] = deviceCards
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/assetCards/deviceCards',model:model)
	}
	
	def deviceCardsSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//设备资产信息保存-------------------------------
		def deviceCards = new DeviceCards()
		if(params.id && !"".equals(params.id)){
			deviceCards = DeviceCards.get(params.id)
			//处理申请单金额
			def applyNotesId =deviceCards.applyNotes.id
			def applyNotes = ApplyNotes.get(applyNotesId)
			applyNotes.totalPrice += params.onePrice.toDouble()
		}else{
			deviceCards.company = company
			//新资产卡片编号
			if(!params.registerNum_form.equals("")){
				deviceCards.registerNum = params.registerNum_form
			}
		}
		deviceCards.properties = params
		deviceCards.clearErrors()
		
		//特殊字段信息处理
		deviceCards.buyDate = Util.convertToTimestamp(params.buyDate)
		deviceCards.userDepart = Depart.get(params.allowdepartsId)
		
		if(deviceCards.save(flush:true)){
			json["result"] = "true"
		}else{
			deviceCards.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def deviceCardsDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def deviceCards = DeviceCards.get(it)
				if(deviceCards){
					deviceCards.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def deviceCardsSubmit ={
		def ids = params.id.split(",")
		def json
		def assetStatus
		def assetCount
		def company = Company.get(params.companyId)
		try{
			ids.each{
				def deviceCards = DeviceCards.get(it)
				if(deviceCards){
					assetStatus = deviceCards.assetStatus
					if(assetStatus=="新建"){
						deviceCards.assetStatus = "已入库"
					}
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= "20"+nowDate.time
		return SeriesDate
	}
	
	def deviceCardsGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetCardsService.getDeviceCardsListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetCardsService.getDeviceCardsDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetCardsService.getDeviceCardsCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
}
