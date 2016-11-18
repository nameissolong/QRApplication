package com.zxing.encoding;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.Gravity;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Matrix;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

public final class EncodingHandler {
	private static final int BLACK = 0xff000000;

	/**
	 * 生成二维码
	 * @param str
	 * @param widthAndHeight
     * @return
     */
	public static Bitmap createQRCode(String str,int widthAndHeight){
		try {
			// 判断URL合法性
			if (str == null || "".equals(str) || str.length() < 1) {
				return null;
			}
			Hashtable hints = new Hashtable();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			// 图像数据转换，使用了矩阵转换
			BitMatrix matrix = new MultiFormatWriter().encode(str,
					BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight,hints);
			int width = matrix.getWidth();
			int height = matrix.getHeight();
			int[] pixels = new int[width * height];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (matrix.get(x, y)) {
						pixels[y * width + x] = BLACK;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap createQrCodeLogo(String content,int widthAndHeight,Bitmap logo){
		Bitmap qrCode = createQRCode(content, widthAndHeight);
		// 创建一个画布
		Canvas canvas = new Canvas(qrCode);
		// 计算logo的宽高
		int logoHeight = qrCode.getHeight() / 6;
		// Matrix 矩阵变换  3 x 3
		Matrix matrix = new Matrix();
		float scale = (float) logoHeight / logo.getHeight();
		// 设置缩放比例
		matrix.setScale(scale,scale);
		// 生成目标大小的logo
		Bitmap scaleLogo = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
		// 将logo画到画布上
		canvas.drawBitmap(scaleLogo,logoHeight * 2.5f,logoHeight * 2.5f,null);
		// 保存
		canvas.save(Canvas.ALL_SAVE_FLAG);

		canvas.restore();

		return qrCode;
	}


	/**
	 * 生成条形码
	 *
	 * @param context
	 * @param contents
	 *            需要生成的内容
	 * @param desiredWidth
	 *            生成条形码的宽带
	 * @param desiredHeight
	 *            生成条形码的高度
	 * @param displayCode
	 *            是否在条形码下方显示内容
	 * @return
	 */
	public static Bitmap creatBarcode(Context context, String contents,int desiredWidth, int desiredHeight, boolean displayCode) {
		try {
			Bitmap ruseltBitmap = null;
			/**
			 * 图片两端所保留的空白的宽度
			 */
			int marginW = 20;
			/**
			 * 条形码的编码类型
			 */
			BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;

			if (displayCode) {
				Bitmap barcodeBitmap = encodeAsBitmap(contents, barcodeFormat,
						desiredWidth, desiredHeight);
				Bitmap codeBitmap = creatCodeBitmap(contents, desiredWidth + 2
						* marginW, desiredHeight, context);
				ruseltBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(
						0, desiredHeight));
			} else {
				ruseltBitmap = encodeAsBitmap(contents, barcodeFormat,
						desiredWidth, desiredHeight);
			}

			return ruseltBitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 生成条形码的Bitmap
	 *
	 * @param contents
	 *            需要生成的内容
	 * @param format
	 *            编码格式
	 * @param desiredWidth
	 * @param desiredHeight
	 * @return
	 * @throws WriterException
	 */
	protected static Bitmap encodeAsBitmap(String contents,BarcodeFormat format, int desiredWidth, int desiredHeight) {
		final int WHITE = 0xFFFFFFFF;
		final int BLACK = 0xFF000000;

		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result = null;
		try {
			result = writer.encode(contents, format, desiredWidth,
					desiredHeight, null);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		// All are 0, or black, by default
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 生成显示编码的Bitmap
	 *
	 * @param contents
	 * @param width
	 * @param height
	 * @param context
	 * @return
	 */
	protected static Bitmap creatCodeBitmap(String contents, int width,int height, Context context) {
		TextView tv = new TextView(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tv.setLayoutParams(layoutParams);
		tv.setText(contents);
		tv.setHeight(height);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setWidth(width);
		tv.setDrawingCacheEnabled(true);
		tv.setTextColor(Color.BLACK);
		tv.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

		tv.buildDrawingCache();
		Bitmap bitmapCode = tv.getDrawingCache();
		return bitmapCode;
	}

	/**
	 * 将两个Bitmap合并成一个
	 *
	 * @param first
	 * @param second
	 * @param fromPoint
	 *            第二个Bitmap开始绘制的起始位置（相对于第一个Bitmap）
	 * @return
	 */
	protected static Bitmap mixtureBitmap(Bitmap first, Bitmap second,PointF fromPoint) {
		if (first == null || second == null || fromPoint == null) {
			return null;
		}

		int maxWidth = Math.max(first.getWidth(), second.getWidth());
		Bitmap newBitmap = Bitmap.createBitmap(maxWidth,first.getHeight() + second.getHeight() , Config.ARGB_4444);

		Canvas cv = new Canvas(newBitmap);

		cv.drawBitmap(first, maxWidth > first.getWidth() ? (maxWidth - first.getWidth())/2 : 0, 0, null);

		cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);

		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();

		return newBitmap;
	}
}

