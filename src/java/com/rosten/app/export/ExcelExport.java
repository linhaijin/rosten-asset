package com.rosten.app.export;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import com.rosten.app.assetChange.AssetScrap;
import com.rosten.app.assetChange.AssetAllocate;

import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelExport {
	//资产报损导出
	public String scrapDc(OutputStream os,List<AssetScrap> scrapList){
		WritableWorkbook wwb = null;
		WritableSheet ws = null;
		try {
			
			VerticalAlignment vcenter = VerticalAlignment.CENTRE;
			Alignment acenter = Alignment.CENTRE;
			
			WritableCellFormat titlewcfStyle = new WritableCellFormat();
			WritableFont titlefont = new WritableFont(WritableFont.ARIAL, 14);
			titlefont.setBoldStyle(WritableFont.BOLD);
			titlewcfStyle.setFont(titlefont);
			titlewcfStyle.setBorder(Border.ALL, BorderLineStyle.THIN);
			titlewcfStyle.setAlignment(acenter);
			titlewcfStyle.setVerticalAlignment(vcenter);
			
			wwb = Workbook.createWorkbook(os);
			ws = wwb.createSheet("数据导出", 0);
			
			ws.mergeCells(0, 0, 6, 0);
			ws.addCell(new Label(0 , 0, "资产报损列表",titlewcfStyle));
			
			ws.addCell(new Label(0, 1, "申请编号"));
			ws.addCell(new Label(1, 1, "申请日期"));
			ws.addCell(new Label(2, 1, "申请人"));
			ws.addCell(new Label(3, 1, "申请部门"));
			ws.addCell(new Label(4, 1, "资产总和（元）"));
			ws.addCell(new Label(5, 1, "申请描述"));
			ws.addCell(new Label(6, 1, "申请状态"));

			if(scrapList != null && scrapList.size() > 0){
				for(int i=0;i<scrapList.size();i++){
					ws.addCell(new Label(0, i+2, scrapList.get(i).getSeriesNo()));
					ws.addCell(new Label(1, i+2, (String)scrapList.get(i).getFormattedShowApplyDate()));
					ws.addCell(new Label(2, i+2, (String)scrapList.get(i).getApplyMan()));
					ws.addCell(new Label(3, i+2, (String)scrapList.get(i).getDepartName()));
					ws.addCell(new Label(4, i+2, scrapList.get(i).getAssetTotal().toString()));
					ws.addCell(new Label(5, i+2, scrapList.get(i).getApplyDesc()));
					ws.addCell(new Label(6, i+2, scrapList.get(i).getStatus()));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("数据导出失败!");
		} finally {
			try {
				wwb.write();
				wwb.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	//资产调拨导出
	public String allocateDc(OutputStream os,List<AssetAllocate> allocateList){
		WritableWorkbook wwb = null;
		WritableSheet ws = null;
		try {
			
			VerticalAlignment vcenter = VerticalAlignment.CENTRE;
			Alignment acenter = Alignment.CENTRE;
			
			WritableCellFormat titlewcfStyle = new WritableCellFormat();
			WritableFont titlefont = new WritableFont(WritableFont.ARIAL, 14);
			titlefont.setBoldStyle(WritableFont.BOLD);
			titlewcfStyle.setFont(titlefont);
			titlewcfStyle.setBorder(Border.ALL, BorderLineStyle.THIN);
			titlewcfStyle.setAlignment(acenter);
			titlewcfStyle.setVerticalAlignment(vcenter);
			
			wwb = Workbook.createWorkbook(os);
			ws = wwb.createSheet("数据导出", 0);
			
			ws.mergeCells(0, 0, 6, 0);
			ws.addCell(new Label(0 , 0, "资产报损列表",titlewcfStyle));
			
			ws.addCell(new Label(0, 1, "申请编号"));
			ws.addCell(new Label(1, 1, "申请日期"));
			ws.addCell(new Label(2, 1, "申请人"));
			ws.addCell(new Label(3, 1, "调出部门"));
			ws.addCell(new Label(4, 1, "调入部门"));
			ws.addCell(new Label(5, 1, "资产总和（元）"));
			ws.addCell(new Label(6, 1, "申请描述"));
			ws.addCell(new Label(7, 1, "申请状态"));

			if(allocateList != null && allocateList.size() > 0){
				for(int i=0;i<allocateList.size();i++){
					ws.addCell(new Label(0, i+2, allocateList.get(i).getSeriesNo()));
					ws.addCell(new Label(1, i+2, (String)allocateList.get(i).getFormattedShowApplyDate()));
					ws.addCell(new Label(2, i+2, (String)allocateList.get(i).getApplyMan()));
					ws.addCell(new Label(3, i+2, (String)allocateList.get(i).getOutDepartName()));
					ws.addCell(new Label(4, i+2, (String)allocateList.get(i).getInDepartName()));
					ws.addCell(new Label(5, i+2, allocateList.get(i).getAssetTotal().toString()));
					ws.addCell(new Label(6, i+2, allocateList.get(i).getApplyDesc()));
					ws.addCell(new Label(7, i+2, allocateList.get(i).getStatus()));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("数据导出失败!");
		} finally {
			try {
				wwb.write();
				wwb.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**模板下载*/
	public String mbxz(OutputStream os){
		WritableWorkbook wwb = null;
		WritableSheet ws = null;
		try {
			wwb = Workbook.createWorkbook(os);
			ws = wwb.createSheet("数据导出", 0);
			
			ws.addCell(new Label(0, 0, "姓名"));
			ws.addCell(new Label(1, 0, "身份证号"));
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("数据导出失败!");
		} finally {
			try {
				wwb.write();
				wwb.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**数据导入*/
	public String hkcjdrsjdr(String filePath) throws Exception{
		
		Sheet sourceSheet = Workbook.getWorkbook(new File(filePath)).getSheet(0);
		int sourceRowCount = sourceSheet.getRows();//获得源excel的行数
		String failInfo="";
		boolean b=false;
	
		if(b){
			return "更新成功！"+failInfo;
		}else{
			return "更新失败！"+failInfo;
		}
	}


}
