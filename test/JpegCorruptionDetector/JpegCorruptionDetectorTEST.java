package JpegCorruptionDetector;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.drew.imaging.jpeg.JpegProcessingException;

import JpegCorruptionDetector.Detector;

public class JpegCorruptionDetectorTEST {

	private static final String	baseDir	= "testfiles/";

	@Test
	public void isJPEGTest() throws IOException {
		assertFalse(Detector.isJPEG(new File(baseDir + "test.txt")));
		assertFalse(Detector.isJPEG(new File(baseDir + "safety.bmp")));
		assertTrue(Detector.isJPEG(new File(baseDir + "broken.jpg")));
		assertTrue(Detector.isJPEG(new File(baseDir + "safety.jpg")));
	}

	@Test
	public void detectCorruptedTest() throws IOException, JpegProcessingException {
		assertFalse(Detector.detectCorrupted(new File(baseDir + "broken.jpg")));
		assertTrue(Detector.detectCorrupted(new File(baseDir + "safety.jpg")));
	}

	@Test
	public void readFolerTest() throws IOException, JpegProcessingException {
		assertThat(Detector.readFolder(new File(baseDir)).size(), is(4));
	}
}
