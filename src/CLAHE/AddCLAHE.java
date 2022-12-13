package CLAHE;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;


import java.awt.image.DataBufferByte;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;

public class AddCLAHE{
	File dir;
	String SaveDir;
	String imgForm; // 사진의 형식
	static {System.loadLibrary( Core.NATIVE_LIBRARY_NAME );}
	
	public AddCLAHE(String SaveDir) {
		this.SaveDir = SaveDir;
		this.dir = null;
	}
	
	public AddCLAHE(String SaveDir, File dir) {
		this.SaveDir = SaveDir;
		this.dir = dir;
	}

	// Apply CLAHE for Color Image
	public double ColorClahe_apply(File input, double cl, double tsize) {
		double Luminance=0; // 밝기
		try {
			// Read Image with BufferedImage
			BufferedImage buffImage = ImageIO.read(input); 
			byte[] data = ((DataBufferByte) buffImage.getRaster().getDataBuffer()).getData();
			// build MAT for original image and put image data
			Mat orgImage = new Mat(buffImage.getHeight(),buffImage.getWidth(), CvType.CV_8UC3);
			orgImage.put(0, 0, data);

			// build Mat for Result
			Mat destImage = new Mat(buffImage.getHeight(), buffImage.getWidth(), CvType.CV_8UC4);
			
			// OpenCV Reads IMG by BGR, Convert the IMG BGR to HSV
			Imgproc.cvtColor(orgImage, destImage, Imgproc.COLOR_BGR2HSV);
			
			// Build Mat for HSV's V channel for apply CLAHE
			// Build Mat for Calculate Luminancy of Image 
			Mat HSVImage = new Mat(buffImage.getHeight(),buffImage.getWidth(), CvType.CV_8UC4);
			Mat LumiMat = new Mat(buffImage.getHeight(),buffImage.getWidth(), CvType.CV_8UC4);
			
			// Make Channels of Image
			List<Mat> channels = new LinkedList<>();  // List to save HSV's channels
			List<Mat> Vchannels = new LinkedList<>(); // List of V of HSV needs to apply CLAHE
			Core.split(destImage, channels);	// Split HSV's channels (H, S, V) -> we will use V
			Vchannels.add(channels.get(2)); // set the origin V channel
			
			// Create CLAHE to apply CLAHE
			CLAHE clahe = Imgproc.createCLAHE(cl, new Size(tsize, tsize));
			channels.set(2, Vchannels.get(0));
			clahe.apply(channels.get(2), HSVImage); // Apply CLAHE in V Channel
			// Change the Value of V Channel of Image to other V Channel CLAHE Applied
			channels.set(2, HSVImage);
			Core.merge(channels, destImage); // Merge the channels of HSV Image to destImage
			Imgproc.cvtColor(destImage, destImage, Imgproc.COLOR_HSV2BGR); // Convert Image HSV to BGR
			// Convert Image to Gray for Calculate Luminancy
			Imgproc.cvtColor(destImage, LumiMat, Imgproc.COLOR_BGR2GRAY);
			Luminance = CalLuminance(LumiMat);
			
			// Save Mat Image with Buffered Image
			File saveFile = new File(SavePath(input, (int)cl, (int)tsize, Luminance, "Color_"));
			ImageIO.write(mat2BufferedImage(destImage, true), imgForm, saveFile);
//			Imgcodecs.imwrite(SavePath(input, (int)cl, (int)tsize, Luminance, "Color_"), destImage);
//			System.out.printf("Success CL : %d, TSize : %d, Luminance : %.2f\n", (int)cl, (int)tsize, Luminance);
			return Luminance;
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return 0;
		}
	}
	
	// Apply CLAHE for GrayScaled Image
	public double GrayCLAHE_Apply(File input, double cl, double tsize) {
		double Luminance=0;	
		try {
			// Read Image with BufferedImage
			BufferedImage buffImage = ImageIO.read(input); 
			byte[] data = ((DataBufferByte) buffImage.getRaster().getDataBuffer()).getData();
			// build MAT for original image and add data of image
			Mat orgImage = new Mat(buffImage.getHeight(),buffImage.getWidth(), CvType.CV_8UC3);
			orgImage.put(0, 0, data);
			
			// Build Mat for transform image to Gray
			Mat grayImage = new Mat(buffImage.getHeight(), buffImage.getWidth(), CvType.CV_8UC4);
			Mat destImage = new Mat(buffImage.getHeight(), buffImage.getWidth(), CvType.CV_8UC4);
			// OpenCV Reads IMG by BGR, Convert the IMG BGR to Gray
			Imgproc.cvtColor(orgImage, grayImage, Imgproc.COLOR_BGR2GRAY); 
			
			// Apply CLAHE
			List<Mat> channels = new ArrayList<Mat>();
			Core.split(grayImage, channels);
			CLAHE clahe = Imgproc.createCLAHE(cl, new Size(tsize, tsize));
			clahe.apply(channels.get(0), destImage); // Apply Value(HSV) in IMG
			Luminance = CalLuminance(destImage);
			File saveFile = new File(SavePath(input, (int)cl, (int)tsize, Luminance, "Gray_"));
			ImageIO.write(mat2BufferedImage(destImage, false), imgForm, saveFile);
//			Imgcodecs.imwrite(SavePath(input, (int)cl, (int)tsize, Luminance, "Gray_"), destImage);
//			System.out.printf("Success CL : %d, TSize : %d, Luminance : %.2f\n", (int)cl, (int)tsize, Luminance);
			return Luminance;
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return 0;
		}
	}
	
    private BufferedImage mat2BufferedImage(Mat mat, boolean colorTF) {
        BufferedImage bufferedImage;
        if (colorTF) {
            bufferedImage = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        } else {
//            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY, 0);
            bufferedImage = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_BYTE_GRAY);
        }
        byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);
        mat.release();
        return bufferedImage;
    }
	
	private double CalLuminance(Mat IMG) {
		double Luminance=0;
		Scalar LumiMean = Core.mean(IMG);
		Luminance = LumiMean.val[0];
		return Luminance;
	}
	
	private String SavePath(File input, int CL, int size, double Lumi, String ImageForm) {
		String img_path = String.valueOf(input);
		String[] img_split = img_path.split("\\\\");
		String[] file_name_arr = img_split[img_split.length-1].split("\\.");
		String file_name = "";
		String Save_path = "";
		String form = file_name_arr[file_name_arr.length-1];
		String name = "_CLAHE";
		String lumi = String.format("%.2f", Lumi);
		   
		//make filename+_save
		for(int i=0;i<file_name_arr.length;i++){
			if(i==file_name_arr.length-1)
				file_name += (name+".");
		  	file_name += file_name_arr[i];
		}
		img_split[img_split.length-1] = file_name;
		imgForm = file_name_arr[file_name_arr.length-1];
		  
		file_name = ""; // make file_name null;
		//make filename+_save
		for(int i=0;i<file_name_arr.length;i++){
			if(i==file_name_arr.length-1)
				file_name += "_CLAHE"+"_CL"+String.valueOf(CL)+"_S"+String.valueOf(size)+"_Lumi_"+lumi+".";
			file_name += file_name_arr[i];
		}
		
		Save_path = SaveDir + ("\\"+ImageForm+file_name);
//		img_split[img_split.length-1] = file_name;
//		for(int i=0;i<img_split.length;i++) {
//			if(i != img_split.length-1) {
//				Save_path += (img_split[i] + "\\");
//			}
//			else
//				Save_path += img_split[i];
//		} // Making Save_path Success
////		File save_addr = new File(Save_path);
//		return Save_path;
		return Save_path;
	}
}


