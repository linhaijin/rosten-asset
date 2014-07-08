package com.rosten.app.assetconfig

import grails.converters.JSON
import com.rosten.app.system.User

class AssetConfigActionController {
	
	def imgPath ="images/rosten/actionbar/"
	
	def assetCategoryForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "zcdl"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	def assetCategoryView ={
		def actionList =[]
		def strname = "zcdl"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新增大类",imgPath + "add.png",strname + "_add")
		actionList << createAction("删除大类",imgPath + "read.gif",strname + "_delete")
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
}
