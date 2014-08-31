package com.rosten.app.system

import grails.converters.JSON
import com.rosten.app.util.Util
import com.rosten.app.assetApply.Apply
import com.rosten.app.system.Company
import com.rosten.app.util.FieldAcl

class DemoController {
	def imgPath ="images/rosten/actionbar/"
	def applyService
	def springSecurityService
	
	def createApply ={
		def model=[:]
		render(view:'/demo/applyShow',model:model)
	}
	
	def applySubmit ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def apply = Apply.get(it)
				if(apply){
					apply.status = params.status
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def applyForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "apply"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	def applyAdd ={
		redirect(action:"applyShow",params:params)
	}
	
	def applyShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def company = Company.get(params.companyId)
		
		def apply = new Apply()
		if(params.id){
			apply = Apply.get(params.id)
		}else{
			apply.applyUser = currentUser
		}
		
		model["user"] = currentUser
		model["company"] = company
		model["apply"] = apply
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		
		render(view:'/demo/apply',model:model)
	}
	
	def applySave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//设备登记信息保存-------------------------------
		def apply = new Apply()
		if(params.id && !"".equals(params.id)){
			apply = Apply.get(params.id)
		}else{
			apply.company = company
			apply.applyUser = springSecurityService.getCurrentUser()
		}
		apply.properties = params
		apply.clearErrors()
		
		if(apply.save(flush:true)){
			json["result"] = "true"
		}else{
			apply.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	
	def applyGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = applyService.getListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = applyService.getDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = applyService.getCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def applyView1 ={
		def actionList =[]
		def strname = "apply"
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("同意",imgPath + "add.png",strname + "_agrain")
		actionList << createAction("生成设备清单",imgPath + "add.png",strname + "_create")
		actionList << createAction("财务对接",imgPath + "add.png",strname + "_delete")
		actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
	def applyView ={
		def actionList =[]
		def strname = "apply"
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
		actionList << createAction("提交审核",imgPath + "add.png",strname + "_submit")
		actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
	
	def flowShowActionhello ={
		def actionList =[]
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("保存",imgPath + "add.png","demo")
		
		render actionList as JSON
	}
	def showFlowhello ={
		def model=[:]
		render(view:'/demo/showFlow',model:model)
	}
	def importDe ={
		def model=[:]
		render(view:'/demo/importDe',model:model)
	}
	def kpshow ={
		def model=[:]
		render(view:'/demo/kpshow',model:model)
	}
	def staticView ={
		def model=[:]
		render(view:'/demo/designMore',model:model)
	}
	def staticShow ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		render actionList as JSON
	}
	def addTitle ={
		def model=[:]
		render(view:'/demo/addTitle',model:model)
	}
	
	def endPd ={
		def model=[:]
		render(view:'/demo/endPd',model:model)
	}
	def zdpd ={
		def model=[:]
		render(view:'/demo/zdpd',model:model)
	}
	def importPd ={
		def model=[:]
		render(view:'/demo/importPd',model:model)
	}
	def startPdAction ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("盘点数据导入",webPath + imgPath + "add.png","pddr")
		actionList << createAction("扫描枪盘点",webPath + imgPath + "add.png","zdpd")
		actionList << createAction("盘点结束",webPath + imgPath + "add.png","zdpd_ok")
		render actionList as JSON
	}
	def startPd ={
		def model=[:]
		render(view:'/demo/startPd',model:model)
	}
	def myPdrwAction ={
		def actionList =[]
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("开始盘点",imgPath + "add.png","startPd")
		actionList << createAction("刷新",imgPath + "fresh.gif","demo")
		
		render actionList as JSON
	}
	def myPdrw ={
		def model=[:]
		render(view:'/demo/myPdrw',model:model)
	}
	def addRw ={
		def model=[:]
		render(view:'/demo/addRw',model:model)
	}
	def rwfbAction ={
		def actionList =[]
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新建任务",imgPath + "add.png","addRw")
		actionList << createAction("启动任务",imgPath + "add.png","demo")
		actionList << createAction("刷新",imgPath + "fresh.gif","demo")
		
		render actionList as JSON
	}
	def fbrw ={
		def model=[:]
		render(view:'/demo/rwfb',model:model)
	}
	
	def desgine ={
		def actionList =[]
		
		actionList << createAction("返回",imgPath + "quit_1.gif","demoReturn")
		actionList << createAction("新增表头字段",imgPath + "add.png","addDemoTitle")
		actionList << createAction("删除表头字段",imgPath + "add.png","deleteDemoTitle")
		actionList << createAction("预览",imgPath + "add.png","demoView")
		actionList << createAction("保存",imgPath + "add.png", "demoReturn")
		
		render actionList as JSON
	}
	def staticDesign ={
		def actionList =[]
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("报表设计",imgPath + "add.png", "demo_staticDesign")
		actionList << createAction("删除",imgPath + "delete.png","demo")
		actionList << createAction("刷新",imgPath + "fresh.gif","demo")
		
		render actionList as JSON
	}
	def demo ={
		def model=[:]
		model.type = params.type
		render(view:'/demo/' + params.type,model:model)
	}
	
    def index() { }
	
	private def createAction={name,img,action->
		def model =[:]
		model["name"] = name
		model["img"] = img
		model["action"] = action
		return model
	}
}
