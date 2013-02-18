package barrysoft.twinkle.view.gui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import barrysoft.gui.GUIEvent;
import barrysoft.resources.ResourcesManager;
import barrysoft.twinkle.UpdateRequest;
import barrysoft.twinkle.UpdateVersion;
import barrysoft.twinkle.view.UpdaterEventType;

import net.miginfocom.swing.MigLayout;

public class UpdateAvailableDialog extends JDialog 
{
	private static final long serialVersionUID = -7287483142094325248L;
	
	private static final Dimension MIN_SIZE = new Dimension(600, 400);

	private final JLabel		icon = new JLabel();
	private final JLabel		subtitle = new JLabel();
	private final JLabel		versionInfo = new JLabel();
	private final JTextPane 	releaseNotes = new JTextPane();
	private final JCheckBox		automaticallyDownload = new JCheckBox();	
	
	private UpdateVersion version;
	private UpdateRequest source;
	
	public UpdateAvailableDialog(Action install, Action skipVersion)
	{
		//TODO: Localization
		
		setMinimumSize(MIN_SIZE);
		setTitle("Software Update");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		enableEvents(GUIEvent.EVENT_ID);
		
		releaseNotes.setContentType("text/html");
		releaseNotes.setEditable(false);
		releaseNotes.setOpaque(true);
		releaseNotes.setBackground(Color.white);
		
		icon.setIcon(new ImageIcon(ResourcesManager.getResources().
				getIconURL("software-update-available")));
		
		JPanel contentPanel = new JPanel(new MigLayout("fill"));
		
		JPanel topPanel = new JPanel(new MigLayout("insets 0"));
		
		topPanel.add(subtitle, "width 100%!, gaptop 10, wrap");
		
		topPanel.add(versionInfo, "width 100%!, wrap");
		 
		topPanel.add(new JLabel("<html><b><small>Release notes:</small></b></html>"),
				"width 100%!, gaptop 10, wrap");
		
		JPanel bottomPanel = new JPanel(new MigLayout("fill, insets 0"));
	
		automaticallyDownload.setText("Automatically download and install updates next time");
		
		automaticallyDownload.setSelected(false);
				
		bottomPanel.add(automaticallyDownload,"gap 0 0 10 10, spanx 2, wrap");
		
		JButton skipVersionButton = new JButton(skipVersion);
		skipVersionButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		
		bottomPanel.add(skipVersionButton, "align left");
		
		JButton remaindLaterButton = new JButton("Remind me later");
		remaindLaterButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		
		bottomPanel.add(remaindLaterButton, "align right");
		
		bottomPanel.add(new JButton(install), "align right, tag ok, wrap");
		
		contentPanel.add(topPanel, "width 100%-20px!, gap 10 10, dock north");
		
		contentPanel.add(new JScrollPane(releaseNotes), 
				"dock center, grow, width 100%-20px!, height 250px, " +
				"gap 10 10, wrap");
		
		contentPanel.add(bottomPanel, "width 100%-20px!, gap 10 10, dock south");
		
		getContentPane().setLayout(new MigLayout("fill"));
		
		getContentPane().add(icon, "dock west, width 48px!, height 48px!, aligny top, gap 10 10 10");
		getContentPane().add(contentPanel, "dock center");
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		pack();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void processEvent(AWTEvent event)
	{
		if (event instanceof GUIEvent)
		{
			GUIEvent<UpdaterEventType> ue = (GUIEvent<UpdaterEventType>)event;
			
			switch(ue.getType()) {
			case NEW_VERSION_FOUND:				
				UpdateVersion version = ue.getDataItem(0, UpdateVersion.class);
				UpdateRequest source = ue.getDataItem(1, UpdateRequest.class);
				
				setUpdateVersion(version, source);
				break;
				
			case CHECKING_COMPLETED:
				setVisible(true);
				break;
				
			case DOWNLOAD_STARTED:
				setVisible(false);
				dispose();
				break;
			
			case UPDATE_COMPLETED:
				setVisible(false);
				dispose();
				break;
				
			case ERROR_OCCURRED:
				setVisible(false);
				dispose();
				break;
				
			default:
				new RuntimeException("Invalid type: "+ue.getType().toString());
			}
		}
		else
		{
			super.processEvent(event);
		}
	}
	
	public boolean isAlwaysDownload()
	{
		return automaticallyDownload.isSelected();
	}
	
	public void setUpdateVersion(UpdateVersion version, UpdateRequest source)
	{
		this.version = version;
		this.source = source;
		
		subtitle.setText(String.format(
			"<html><b>A new version of %s is available!</b></html>",
			source.getApplicationInfo().getSoftwareName()));
		
		String newVersion;
		String currentVersion = source.getApplicationInfo().getVersion();
		
		if (version.getShortVersion() == null) {
			newVersion = version.getVersion();
		} else {
			newVersion = version.getShortVersion() + " ("+version.getVersion()+")";
			currentVersion += " ("+source.getComparableVersion()+")";
		}
		
		versionInfo.setText(String.format(
			"<html>%s %s is now available; you have version %s.<br>Do you wish to update now?</html>",
			source.getApplicationInfo().getSoftwareName(),
			newVersion,
			currentVersion));
		
		try {
			releaseNotes.setPage(version.getReleaseNotesLink());
		} catch (IOException e) {
			releaseNotes.setText("<html><h1>Error while opening the page</h1></html>");
		}
	}

	public UpdateRequest getUpdateSource()
	{
		return source;
	}

	public UpdateVersion getUpdateVersion()
	{
		return version;
	}
	
}
