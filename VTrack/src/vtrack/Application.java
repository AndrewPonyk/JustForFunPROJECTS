package vtrack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import vtrack.util.VtrackUtils;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class Application {

	private JFrame appFrame;
	
	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException, InterruptedException {
		
		//new Application().buildGUI();
		//new VtrackUtils().startTracking();
		
		//////////
		DateTime dt = new DateTime();
		System.out.println(dt.toLocalDate()); // good date
		
		DateTime temp = new DateTime(new Date());
		FileAlterationObserver observer;
	}





	public void buildGUI() {
		System.out.println("11:54 AM Monday, October 6, 2014 (GMT+3)");
		appFrame = new JFrame("FrameDemo");
		appFrame.setPreferredSize(new Dimension(770, 450));
		
		GridLayout experimentLayout = new GridLayout(VtrackUtils.TRACKING_IDS.length+1,0);
		appFrame.setLayout(experimentLayout);
		
		JPanel controlPanel = new JPanel();
		controlPanel.add(new JLabel("Control Panel :"));
		
		controlPanel.add(new JLabel("App dir:"));
		controlPanel.add(new JTextField(VtrackUtils.DATA_DIRECTORY,45));
		
		UtilDateModel model = new UtilDateModel();
		JDatePanelImpl datePanel = new JDatePanelImpl(model);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel); 
		controlPanel.add(new JButton("Show from date :"));
		controlPanel.add(datePicker);
		
		controlPanel.add(new JButton("Start Tracking"));
		
		appFrame.add(controlPanel);
		
		for (int i = 0; i < VtrackUtils.TRACKING_IDS.length; i++) {
			JPanel userPanel = new JPanel();
			userPanel.setBackground(new Color(254- i*18,255- i*18, 255- i*18));
			appFrame.add(userPanel);
		}
		
		appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		appFrame.pack();		
		appFrame.setVisible(true);
	}
}

// Example of data file for concrete vk.com id
/*
2014-10-06
11:00-11:34
---
2014-10-07
11:00-11:34 11:44-13:20 18:00-22:00
---





*/
// FIrst part without drawing =)