package com.platform.ems.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.pdf.qrcode.ByteMatrix;
import com.itextpdf.text.pdf.qrcode.QRCode;

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * @Author qhq
 * @create 2021/11/11 17:23
 */
public class QCUtil {

	/**
	 * 生成二维码
	 * @param text 值
	 * @param width
	 * @param height
	 * @param filePath 输出路径
	 * @throws Exception
	 */
	public static void generateQRCodeImage(String text, int width, int height, String filePath) throws Exception {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
		Path path = FileSystems.getDefault().getPath(filePath);
		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
	}

	public static void generateCode128Image(String text, int width, int height, String filePath) throws Exception {
		Code128Writer code128Writer = new Code128Writer();
		BitMatrix bitMatrix = code128Writer.encode(text, BarcodeFormat.CODE_128, width, height);
		Path path = FileSystems.getDefault().getPath(filePath);
		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
	}

	private static BitMatrix renderResult(QRCode code, int width, int height, int quietZone) {
		ByteMatrix input = code.getMatrix();
		int inputWidth = input.getWidth();
		int inputHeight = input.getHeight();
		int qrWidth = inputWidth + (quietZone * 2);
		int qrHeight = inputHeight + (quietZone * 2);
		int outputWidth = Math.max(width, qrWidth);
		int outputHeight = Math.max(height, qrHeight);
		int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);

		outputWidth = qrWidth * multiple;// 改动点
		outputHeight = qrWidth * multiple;// 改动点

		int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
		int topPadding = (outputHeight - (inputHeight * multiple)) / 2;
		leftPadding = 0 ;//改动点
		topPadding = 0 ;//改动点
		BitMatrix output = new BitMatrix(outputWidth, outputHeight);
		for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += multiple) {
			// Write the contents of this row of the barcode
			for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
				if (input.get(inputX, inputY) == 1) {
					output.setRegion(outputX, outputY, multiple, multiple);
				}
			}
		}

		return output;
	}

	public static void main (String[] args) throws Exception {
//		QCUtil.generateCode128Image("12345",56,56,"C:\\Users\\Administrator\\Desktop\\barcode_12345.png");
//		QCUtil.generateQRCodeImage(material.getMaterialCode(),70,70,CommonUtil.getDeskTopPath()+"\\"+material.getMaterialCode()+".png");
	}

}
