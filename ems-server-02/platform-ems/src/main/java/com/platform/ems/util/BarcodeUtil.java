package com.platform.ems.util;

import com.platform.common.utils.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 条形码工具类
 * @Author qhq
 * @create 2021/11/10 16:57
 */
public class BarcodeUtil {
	/**
	 * 生成文件
	 *
	 * @param msg
	 * @param path
	 * @return
	 */
	public static File generateFile(String msg, String path) {
		File file = new File(path);
		try {
			generate(msg, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return file;
	}

	/**
	 * 生成字节
	 *
	 * @param msg
	 * @return
	 */
	public static byte[] generate(String msg) {
		ByteArrayOutputStream ous = new ByteArrayOutputStream();
		generate(msg, ous);
		return ous.toByteArray();
	}

	/**
	 * 生成到流
	 *
	 * @param msg
	 * @param ous
	 */
	public static void generate(String msg, OutputStream ous) {
		if (StringUtils.isEmpty(msg) || ous == null) {
			return;
		}

		Code128Bean bean = new Code128Bean();

		// 精细度
		final int dpi = 150;
		// module宽度
		final double moduleWidth = UnitConv.in2mm(1.0f / dpi);
		//高度
		bean.setBarHeight(4f);

		// 配置对象
		bean.setModuleWidth(moduleWidth);
//		bean.setWideFactor(3);
		bean.doQuietZone(false);

		String format = "image/png";
		try {

			// 输出到流
			BitmapCanvasProvider canvas = new BitmapCanvasProvider(ous, format, dpi,
					BufferedImage.TYPE_BYTE_BINARY, false, 0);

			// 生成条形码
			bean.generateBarcode(canvas, msg);

			// 结束绘制
			canvas.finish();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 生成code128条形码
	 *
	 * @param height        条形码的高度
	 * @param width         条形码的宽度
	 * @param message       要生成的文本
	 * @param withQuietZone 是否两边留白
	 * @param hideText      隐藏可读文本
	 * @return 图片对应的字节码
	 */
	public static byte[] generateBarCode128(String message, Double height, Double width, boolean withQuietZone, boolean hideText) {
		Code128Bean bean = new Code128Bean();
		// 分辨率
		int dpi = 512;
		// 设置两侧是否留白
		bean.doQuietZone(withQuietZone);

		// 设置条形码高度和宽度
		bean.setBarHeight((double) ObjectUtils.defaultIfNull(height, 9.0D));
		if (width != null) {
			bean.setModuleWidth(width);
		}
		// 设置文本位置（包括是否显示）
		if (hideText) {
			bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
		}
		String format = "image/png";
		// 设置图片类型

		ByteArrayOutputStream ous = new ByteArrayOutputStream();
		BitmapCanvasProvider canvas = new BitmapCanvasProvider(ous, format, dpi,
				BufferedImage.TYPE_BYTE_BINARY, false, 0);

		// 生产条形码
		bean.generateBarcode(canvas, message);
		try {
			canvas.finish();
		} catch (IOException e) {

		}
		return ous.toByteArray();
	}

	public static void main(String[] args) {
		String msg = "123456789";
		String path = System.getProperty("user.home")+"\\Desktop\\barcode.png";
		generateFile(msg, path);
	}
}
