package JpegCorruptionDetector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.drew.imaging.jpeg.JpegProcessingException;

public class CLI {

	/**
	 * @param args
	 * @throws IOException
	 * @throws JpegProcessingException 
	 */
	public static void main(String[] args) throws IOException, JpegProcessingException {
		if (args.length < 1) {
			throw new IllegalArgumentException("You must give folder path.");
		}

		ArrayList<File> brokenFiles = Detector.readFolder(new File(args[0]));
		if (brokenFiles.size() != 0) {
			for (File f : brokenFiles) {
				System.out.println(f.getAbsolutePath() + " is broken.");
			}
			System.exit(1);
		}

		System.out.println("All files are safety.");
	}
}
