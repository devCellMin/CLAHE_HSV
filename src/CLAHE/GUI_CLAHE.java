package CLAHE;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GUI_CLAHE extends JFrame {
	// Constructor
	public GUI_CLAHE() {
		run();
	}

	JButton BtnReadDir, BtnSaveDir, BtnStart;
	JTextField TFReadDir, TFSaveDir; AddCLAHE addCLAHE;
	JTextField TFCL_Start, TFCL_End, TFCL_Increase;
	JTextField TFTS_Start, TFTS_End, TFTS_Increase;
	JTextArea TACLAHE;JRadioButton RadioColor, RadioGray;
	JScrollPane TAScroll; String FileName; String[] filedata;
	double CLStart, CLEnd, CLIncrease;
	double TSStart, TSEnd, TSIncrease;
	boolean loopTF = false; Thread thread;
	private void run() {
		setLayout(new BorderLayout());
		
		showNorth();
		showWest();
		showCenter();
		showSouth();
		
		
		// Add Button Listener
		MyActionEvent myActionEvent = new MyActionEvent();
		BtnReadDir.addActionListener(myActionEvent);		
		BtnSaveDir.addActionListener(myActionEvent);
		BtnStart.addActionListener(myActionEvent);
		
		
		
		
		
		setTitle("CLAHE GUI");
		setSize(480, 640);
		setResizable(false);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private class MyActionEvent implements ActionListener{
		JFileChooser JFC = new JFileChooser(); double Luminance;
		public void actionPerformed(ActionEvent e) {
			Object btn = e.getSource();
			
			if(btn == BtnReadDir) {
				TFReadDir.setText("");
				JFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(JFC.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					TFReadDir.setText(JFC.getSelectedFile().toString());
					TFSaveDir.setText(JFC.getSelectedFile().toString());
				}
			}else if(btn == BtnSaveDir) {
				TFSaveDir.setText("");
				JFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(JFC.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					TFSaveDir.setText(JFC.getSelectedFile().toString());
				}
			}else if(btn == BtnStart) {
				if(beforeCheck()) {
					CLStart = Double.parseDouble(TFCL_Start.getText());
					CLEnd = Double.parseDouble(TFCL_End.getText());
					CLIncrease = Double.parseDouble(TFCL_Increase.getText());
					TSStart = Double.parseDouble(TFTS_Start.getText());
					TSEnd = Double.parseDouble(TFTS_End.getText());
					TSIncrease = Double.parseDouble(TFTS_Increase.getText());
					
					// Check StartNumber and EndNumber
					if(CLStart > CLEnd) {JOptionPane.showMessageDialog(null, "Clip Limit�� ���۰��� �������� Ů�ϴ�.", "Warning", JOptionPane.WARNING_MESSAGE);return;}
					if(TSStart > TSEnd) {JOptionPane.showMessageDialog(null, "Tile Size�� ���۰��� �������� Ů�ϴ�.", "Warning", JOptionPane.WARNING_MESSAGE);return;}
					
					// Real Start
					addCLAHE = new AddCLAHE(TFSaveDir.getText());
					File dir = new File(TFReadDir.getText());				
					File[] dirArr = dir.listFiles();
					
					
					thread = new Thread(()-> {
						int dirCount = 0; double cl = CLStart, tsize = TSStart;
						if(loopTF == true) {
							return;
						}loopTF = true;
						while(loopTF) {
							File input = dirArr[dirCount];
							if(RadioColor.isSelected()) {
								Luminance = addCLAHE.ColorClahe_apply(input, cl, tsize);
							}else if(RadioGray.isSelected()) {
								Luminance = addCLAHE.GrayCLAHE_Apply(input, cl, tsize);
							}
							
							filedata = String.valueOf(input).split("\\\\");
							FileName = filedata[filedata.length-1];
							TACLAHE.append("\n"+FileName);
							TACLAHE.append(String.format("\nResult CL : %d, TSize : %d\nLuminance : %.2f\n", (int)cl, (int)tsize, Luminance));
							TAScroll.getVerticalScrollBar().setValue(TAScroll.getVerticalScrollBar().getMaximum());
							
							tsize+=TSIncrease;
							if(tsize > TSEnd) {
								cl+=CLIncrease;
								tsize = TSStart;
							}
							
							if(cl>CLEnd) {
								dirCount++;
								cl = CLStart;
								tsize = TSStart;
							}

							if(dirCount>=dirArr.length) {
								loopTF = false;
							}
						}
					});
					thread.start();
				}
			}
			
		}
		private boolean beforeCheck() {
			if(TFReadDir.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "���� ������ �����Ͽ� �ּ���.", "Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if(TFSaveDir.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "���� ������ �����Ͽ� �ּ���.", "Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if(TFCL_Start.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Clip Limit ���۰��� ����ֽ��ϴ�.", "Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if(TFCL_End.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Clip Limit ������ ����ֽ��ϴ�.", "Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if(TFCL_Increase.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Clip Limit �������� ����ֽ��ϴ�.", "Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if(TFTS_Start.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Tile Size ���۰��� ����ֽ��ϴ�.", "Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if(TFTS_End.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Tile Size ������ ����ֽ��ϴ�.", "Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if(TFTS_Increase.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Tile Size �������� ����ֽ��ϴ�.", "Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			} return true;
		}
	}

	private void showNorth() {
		JPanel NorthPanel = new JPanel(new GridLayout(3, 0));
		
		// Panel to show Title of this GUI
		JPanel TitlePanel = new JPanel();
		JLabel LblTitle = new JLabel("OpenCV CLAHE");
		
		LblTitle.setFont(new Font("���� ���", Font.BOLD, 23));
		LblTitle.setHorizontalAlignment(JLabel.CENTER);
		TitlePanel.add(LblTitle);
		
		// Panel to show ReadDir Part
		JPanel ReadDirPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel LblReadDir = new JLabel("�б��� ");
		TFReadDir = new JTextField(23);
		BtnReadDir = new JButton("���� ����");
		
		LblReadDir.setFont(new Font("���� ���", Font.BOLD, 13));
		TFReadDir.setFont(new Font("���� ���", Font.BOLD, 13));
		BtnReadDir.setFont(new Font("���� ���", Font.BOLD, 13));
		LblReadDir.setHorizontalAlignment(JLabel.CENTER);
		LblReadDir.setVerticalTextPosition(JLabel.CENTER);
		TFReadDir.setPreferredSize(new Dimension(100, 32));

		ReadDirPanel.add(LblReadDir);
		ReadDirPanel.add(TFReadDir);
		ReadDirPanel.add(BtnReadDir);
		
		// Panel to show SaveDir Part
		JPanel SaveDirPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel LblSaveDir = new JLabel("������ ");
		TFSaveDir = new JTextField(23);
		BtnSaveDir = new JButton("���� ����");
		
		LblSaveDir.setFont(new Font("���� ���", Font.BOLD, 13));
		TFSaveDir.setFont(new Font("���� ���", Font.BOLD, 13));
		BtnSaveDir.setFont(new Font("���� ���", Font.BOLD, 13));
		LblSaveDir.setHorizontalAlignment(JLabel.CENTER);
		TFSaveDir.setPreferredSize(new Dimension(100, 32));

		SaveDirPanel.add(LblSaveDir);
		SaveDirPanel.add(TFSaveDir);
		SaveDirPanel.add(BtnSaveDir);
		
		// Set Border and Add Panels to Frame
		NorthPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 00));
		
		NorthPanel.add(TitlePanel);
		NorthPanel.add(ReadDirPanel);
		NorthPanel.add(SaveDirPanel);
		add(NorthPanel, BorderLayout.NORTH);
	}
	
	private void showWest() {
		JPanel WestPanel = new JPanel(new GridLayout(2, 0));
		
		
		// Add Clip Limit Part
		JPanel ClipLimitPanel = new JPanel(new GridLayout(4, 0));
		JPanel CLTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel LblCLTitle = new JLabel("Clip Limit");
		
		LblCLTitle.setFont(new Font("���� ���", Font.BOLD, 15));
		LblCLTitle.setHorizontalAlignment(JLabel.CENTER);
		CLTitlePanel.add(LblCLTitle);
		ClipLimitPanel.add(CLTitlePanel);
		
		// Add CL_Start Part
		JPanel CL_StartPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel LblCL_Start = new JLabel("���۰� : ");
		TFCL_Start = new JTextField(5);
		
		LblCL_Start.setHorizontalAlignment(JLabel.CENTER);
		LblCL_Start.setFont(new Font("���� ���", Font.BOLD, 15));
		TFCL_Start.setFont(new Font("���� ���", Font.BOLD, 14));
		CL_StartPanel.add(LblCL_Start);
		CL_StartPanel.add(TFCL_Start);
		ClipLimitPanel.add(CL_StartPanel);
		
		// Add CL_End Part
		JPanel CL_EndPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel LblCL_End = new JLabel("�� �� : ");
		TFCL_End = new JTextField(5);
		
		LblCL_End.setHorizontalAlignment(JLabel.CENTER);
		LblCL_End.setFont(new Font("���� ���", Font.BOLD, 15));
		TFCL_End.setFont(new Font("���� ���", Font.BOLD, 14));
		CL_EndPanel.add(LblCL_End);
		CL_EndPanel.add(TFCL_End);
		ClipLimitPanel.add(CL_EndPanel);
		
		// Add CL_End Part
		JPanel CL_IncreasePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel LblCL_Increase = new JLabel("������ : ");
		TFCL_Increase = new JTextField(5);
		
		LblCL_Increase.setHorizontalAlignment(JLabel.CENTER);
		LblCL_Increase.setFont(new Font("���� ���", Font.BOLD, 15));
		TFCL_Increase.setFont(new Font("���� ���", Font.BOLD, 14));
		CL_IncreasePanel.add(LblCL_Increase);
		CL_IncreasePanel.add(TFCL_Increase);
		ClipLimitPanel.add(CL_IncreasePanel);
		
		/////////////////////////////////////////////////////////////////////////////////
		// Add TileSize Part
		JPanel TileSizePanel = new JPanel(new GridLayout(4, 0));
		JPanel TSTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel LblTSTitle = new JLabel("Tile Size");
		
		LblTSTitle.setFont(new Font("���� ���", Font.BOLD, 15));
		LblTSTitle.setHorizontalAlignment(JLabel.CENTER);
		TSTitlePanel.add(LblTSTitle);
		TileSizePanel.add(TSTitlePanel);
		
		// Add CL_Start Part
		JPanel TS_StartPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel LblTS_Start = new JLabel("���۰� : ");
		TFTS_Start = new JTextField(5);
		
		LblTS_Start.setHorizontalAlignment(JLabel.CENTER);
		LblTS_Start.setFont(new Font("���� ���", Font.BOLD, 15));
		TFTS_Start.setFont(new Font("���� ���", Font.BOLD, 14));
		TS_StartPanel.add(LblTS_Start);
		TS_StartPanel.add(TFTS_Start);
		TileSizePanel.add(TS_StartPanel);
		
		// Add CL_End Part
		JPanel TS_EndPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel LblTS_End = new JLabel("�� �� : ");
		TFTS_End = new JTextField(5);
		
		LblTS_End.setHorizontalAlignment(JLabel.CENTER);
		LblTS_End.setFont(new Font("���� ���", Font.BOLD, 15));
		TFTS_End.setFont(new Font("���� ���", Font.BOLD, 14));
		TS_EndPanel.add(LblTS_End);
		TS_EndPanel.add(TFTS_End);
		TileSizePanel.add(TS_EndPanel);
		
		// Add CL_End Part
		JPanel TS_IncreasePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel LblTS_Increase = new JLabel("������ : ");
		TFTS_Increase = new JTextField(5);
		
		LblTS_Increase.setHorizontalAlignment(JLabel.CENTER);
		LblTS_Increase.setFont(new Font("���� ���", Font.BOLD, 15));
		TFTS_Increase.setFont(new Font("���� ���", Font.BOLD, 14));
		TS_IncreasePanel.add(LblTS_Increase);
		TS_IncreasePanel.add(TFTS_Increase);
		TileSizePanel.add(TS_IncreasePanel);
		
		
		// Set Increase TextField default value 2
		TFCL_Increase.setText("2");
		TFTS_Increase.setText("2");
		

		// Set Border and Add Panels to Frame
		WestPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 0));
		WestPanel.add(ClipLimitPanel);
		WestPanel.add(TileSizePanel);
		add(WestPanel, BorderLayout.WEST);
	}

	private void showCenter() {
		JPanel CenterPanel = new JPanel();
		JPanel ButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		BtnStart = new JButton("�� ��");
		RadioColor = new JRadioButton(" Color ", true);
		RadioGray = new JRadioButton(" Gray ");
		
		BtnStart.setFont(new Font("���� ���", Font.BOLD, 14));
		RadioColor.setFont(new Font("���� ���", Font.BOLD, 14));
		RadioGray.setFont(new Font("���� ���", Font.BOLD, 14));
		ButtonGroup BGColorTF = new ButtonGroup();
		BGColorTF.add(RadioColor);
		BGColorTF.add(RadioGray);
		ButtonPanel.add(RadioColor);
		ButtonPanel.add(RadioGray);
		ButtonPanel.add(BtnStart);
		
		TACLAHE = new JTextArea(16, 18);
		TACLAHE.setFont(new Font("���� ���", Font.BOLD, 14));
		TACLAHE.setText("����"+
				"\n���������� ������ �а�\nCLAHE�� ����\n\n" + 
				"Clip Limit�� Tile Size�� ���\n" + 
				"�������� �⺻������ 2�� ����\n" + 
				"�������� �ٲٰ� �ʹٸ�\n" + 
				"�ش� ĭ�� Ŭ���ϸ� ���� ����\n\n"+
				"Made by �Źμ�\n");
		TACLAHE.setEditable(false);
		TAScroll = new JScrollPane(TACLAHE, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		CenterPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		CenterPanel.add(ButtonPanel);
		CenterPanel.add(TAScroll);
		add(CenterPanel, BorderLayout.CENTER);
	}

	private void showSouth() {
		Image logo = new ImageIcon("images/icon.png").getImage().getScaledInstance(150, 40, Image.SCALE_SMOOTH);
		JPanel SouthPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel LblLogo = new JLabel(new ImageIcon(logo));
		LblLogo.setHorizontalAlignment(JLabel.CENTER);
		SouthPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		SouthPanel.add(LblLogo);
		add(SouthPanel, BorderLayout.SOUTH);
	}	
}


















