package com.rosten.app.share

import com.rosten.app.system.SystemCode

class ShareService {
	
	def getCommentByStatus ={dealId,status->
		def _list =[]
		def commentList = FlowComment.findAllByBelongToIdAndStatus(dealId,status,[sort: "createDate", order: "desc"])
		if(commentList && commentList.size()>0){
			commentList.each{
				def map =[:]
				map["name"] = it.user.getChinaName()
				map["content"]= it.content
				map["date"]= it.getFormattedCreatedDate()
				_list << map
			}
			
		}
		return _list
	}
	def addFlowLog ={ belongToId,flowCode,currentUser,content ->
		//添加日志
		def _log = new FlowLog()
		_log.user = currentUser
		_log.belongToId = belongToId
		_log.belongToObject = flowCode
		_log.content = content
		_log.company = currentUser.company
		_log.save(flush:true)
	}
	def getSystemCodeItems={company,code ->
		def systemCode = SystemCode.findByCompanyAndCode(company,code)
		if(systemCode){
			return systemCode.items
		}else{
			return []
		}
	}
	
  def getAllDepartByChild ={departList,depart->
		departList << depart
		if(depart.children){
			depart.children.each{
				return getAllDepartByChild(departList,it)
			}
		}else{
			return departList
		}
	}
  
  
}
