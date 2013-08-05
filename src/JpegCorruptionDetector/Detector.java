package JpegCorruptionDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentReader;

public class Detector {

	/*
	 * holds all broken files
	 */
	private static ArrayList<File>	brokenFiles	= new ArrayList<File>();

	/**
	 * @param imgFile
	 * @return boolean
	 * @throws IOException
	 */
	public static boolean isJPEG(File imgFile) throws IOException {
		ImageInputStream imageInputStream = null;
		try {
			imageInputStream = ImageIO.createImageInputStream(new FileInputStream(imgFile));
		} catch (FileNotFoundException e) {
			return false;
		}

		ImageReader imageReader = null;
		try {
			imageReader = ImageIO.getImageReaders(imageInputStream).next();
		} catch (NoSuchElementException e) {
			return false;
		}

		if (!("JPEG").equals(imageReader.getFormatName())) {
			return false;
		}
		return true;
	}

	/**
	 * @param imgFile
	 * @return boolean
	 * @throws IOException
	 * @throws JpegProcessingException
	 */
	public static boolean detectCorrupted(File imgFile) throws IOException, JpegProcessingException {

		ImageInputStream imageInputStream = ImageIO.createImageInputStream(new FileInputStream(imgFile));
		byte[] lastTwoBytes = new byte[2];

		imageInputStream.seek(0);
		imageInputStream.read(lastTwoBytes);
		if ((lastTwoBytes[0] & 0xff) != 0xff || (lastTwoBytes[1] & 0xff) != 0xd8) {
			return false;
		}

		imageInputStream.seek(imgFile.length() - 2);
		imageInputStream.read(lastTwoBytes);
		if ((lastTwoBytes[0] & 0xff) != 0xff || (lastTwoBytes[1] & 0xff) != 0xd9) {
			return false;
		}

		new JpegSegmentReader(imgFile);

		return true;
	}

	/**
	 * check files recursively
	 * 
	 * @param folderPath
	 * @throws IOException
	 * @throws JpegProcessingException
	 */
	private static void _readFolder(File dir) throws IOException {

		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (!file.exists()) {
				continue;
			} else if (file.isDirectory()) {
				_readFolder(file);
			} else if (file.isFile()) {
				if (isJPEG(file)) {
					try {
						if (!detectCorrupted(file)) {
							brokenFiles.add(file);
						}
					} catch (JpegProcessingException e) {
						brokenFiles.add(file);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param folderPath
	 * @return
	 * @throws IOException
	 * @throws JpegProcessingException
	 */
	public static ArrayList<File> readFolder(File dir) throws IOException, JpegProcessingException {
		_readFolder(dir);
		return brokenFiles;
	}
}
