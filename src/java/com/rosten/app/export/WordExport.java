package com.rosten.app.export;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.rosten.app.export.ZipUtil;

public class WordExport {
	
	
	/**
	 * 单个打印
	 */
	public String singlePrint(HttpServletResponse response,String xmlName,String names,Map<String, Object> data) throws Exception {
		File wordFile = getWord(xmlName,names,data);
		FileUtil.outputWord(response,wordFile);
		return null;
	}
	
	/**
	 * 批量打印
	 */
	public String downloadZip(HttpServletResponse response,String xmlName,String zipName,String fileNames,ArrayList dataLists)
			throws Exception {
		String[] pks = fileNames.split(",");
		if (null!=pks) {
			List<File> files = new ArrayList<File>();
			for (int i = 0, n = pks.length; i < n; i++) {
//				File file = getWord(xmlName,pks[i],(Map<String,Object>)dataLists[i]);
//				files.add(file);
			}
			File zipFile = ZipUtil.zip(zipName,files.toArray(new File[] {}));
			FileUtil.outputZip(response, zipFile);
		}
		return null;
	}
	
	// 填充模版数据生成word文件,通用方法
	private File getWord(String xmlName,String fileName,Map<String, Object> data) throws Exception {
		File wordFile = FreeMarkerUtil.getWordFile(data,
				"classpath:com/rosten/app/template", xmlName,fileName);
		return wordFile;
	}

}
