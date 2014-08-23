package com.rosten.app.system

import grails.converters.JSON

class DemoController {
	def imgPath ="images/rosten/actionbar/"
	
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
