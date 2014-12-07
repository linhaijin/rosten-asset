package com.rosten.app.barcode;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.jbarcode.JBarcode;
import org.jbarcode.encode.Code39Encoder;
import org.jbarcode.encode.EAN13Encoder;
import org.jbarcode.encode.InvalidAtributeException;
import org.jbarcode.paint.BaseLineTextPainter;
import org.jbarcode.paint.EAN13TextPainter;
import org.jbarcode.paint.WideRatioCodedPainter;
import org.jbarcode.paint.WidthCodedPainter;
import org.jbarcode.util.ImageUtil;


public class Barcode {
	public byte[] txmStr(String code) throws IOException{
		JBarcode localJBarcode = new JBarcode(EAN13Encoder.getInstance(), WidthCodedPainter.getInstance(), EAN13TextPainter.getInstance());   
		BufferedImage localBufferedImage = null;
		localJBarcode.setEncoder(Code39Encoder.getInstance());   
		localJBarcode.setPainter(WideRatioCodedPainter.getInstance());   
		localJBarcode.setTextPainter(BaseLineTextPainter.getInstance());   
		localJBarcode.setShowCheckDigit(false);   
		try {
			localBufferedImage = localJBarcode.createBarcode(code);
		} catch (InvalidAtributeException e) {
			e.printStackTrace();
		}   
		byte[] bytes = ImageUtil.encode(localBufferedImage, ImageUtil.JPEG);
		return bytes;
	}
}


