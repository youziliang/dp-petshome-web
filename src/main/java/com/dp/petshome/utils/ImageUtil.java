package com.dp.petshome.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;

/**
 * @Description 圖片操作工具类
 */
public class ImageUtil {

	public static final Integer WATERMARK = 0;
	public static final Integer RESIZE = 1;
	public static final Integer SCALE = 2;
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String RATIO = "height";

	public static void createThumb(File originFile, Map<Integer, Map<String, Object>> transform) {

		String filePath = originFile.getParent();
		String fileName = "thumb_" + originFile.getName();
		File destFile = new File(filePath, fileName);

		// Map<String, Object> watermark = transform.get(0);
		Map<String, Object> resize = transform.get(1);
		Map<String, Object> scale = transform.get(2);
		try {
			BufferedImage originalImage = ImageIO.read(originFile);
			Builder<BufferedImage> builder = Thumbnails.of(originalImage);
			if (null != resize && resize.size() > 0) {
				Integer width = (Integer) resize.get(WIDTH);
				Integer height = (Integer) resize.get(HEIGHT);
				builder.size(width, height);
			}
			if (null != scale && scale.size() > 0) {
				Double ratio = (Double) scale.get(RATIO);
				builder.scale(ratio);
			}
			builder.toFile(destFile);
			originFile.delete();
			// originFile.deleteOnExit(); // 不会立刻删除，虚拟机停止时再删除
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
}
