package barrysoft.twinkle.view.gui;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.util.Calendar;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.miginfocom.swing.MigLayout;
import barrysoft.gui.GUIEvent;
import barrysoft.resources.ResourcesManager;
import barrysoft.twinkle.UpdateRequest;
import barrysoft.twinkle.UpdateVersion;
import barrysoft.twinkle.view.UpdaterEventType;
import barrysoft.utils.FileUtils;

public class UpdateProgressDialog extends JDialog 
{
	private static final long serialVersionUID = 136759915854218981L;
	
	private static final Dimension MIN_SIZE = new Dimension(450, 150);
	
	private final JProgressBar 	progressBar = new JProgressBar();
	private final JLabel		statusLabel = new JLabel();
	private final JLabel		messageLabel = new JLabel();
	private final JButton		cancelButton;
	
	private int 	currentSpeed;
	private long 	bytesLast;
	private long 	bytesLastTime;
	
	//TODO: Localize
	
	public UpdateProgressDialog(Action cancelUpdateAction)
	{
		setMinimumSize(MIN_SIZE);
		setResizable(false);
		setEnabled(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setTitle("Updating Application");
		
		enableEvents(GUIEvent.EVENT_ID);
		
		setMessage("Downloading update archive...");
		
		getContentPane().setLayout(new MigLayout("fill"));
		
		getContentPane().add(new JLabel(new ImageIcon(ResourcesManager.getResources().
				getIconURL("software-update-available"))),
				"dock west, gap 10 10 10 10");
		
		getContentPane().add(messageLabel,"dock north, gap 10 10 10 10");
		
		JPanel bottomPanel = new JPanel(new MigLayout("fill, insets 0"));
		
		cancelButton = new JButton(cancelUpdateAction);
		
		bottomPanel.add(statusLabel);
		bottomPanel.add(cancelButton, "tag cancel, align right, wrap");
		
		getContentPane().add(progressBar, "dock center, gap 10 10 10 10");
		getContentPane().add(bottomPanel, "dock south, gap 10 10");
		
		pack();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void processEvent(AWTEvent event)
	{
		if (event instanceof GUIEvent)
		{
			GUIEvent<UpdaterEventType> ue = (GUIEvent<UpdaterEventType>)event;
			
			UpdateRequest source;
			UpdateVersion version;
			
			switch(ue.getType()) {
			case CHECKING_UPDATES:
				source = ue.getDataItem(0, UpdateRequest.class);
				setUpdateRequest(source);
				setOperation("Checking for updates...", false);
				
				setVisible(true);
				break;
				
			case CHECKING_COMPLETED:
				setVisible(false);
				dispose();
				
				break;
				
			case NEW_VERSION_FOUND:
				version = ue.getDataItem(0, UpdateVersion.class);
				source = ue.getDataItem(1, UpdateRequest.class);
				
				setUpdateRequest(source);
				setUpdateVersion(version);
				break;
				
			case DOWNLOAD_STARTED:
				setVisible(true);
				break;
				
			case DOWNLOAD_ENDED:
				break;
				
			case DOWNLOAD_PROGRESS:
				updateProgress(ue.getDataItem(0, Integer.class));
				break;
				
			case EXTRACTION_STARTED:
				setOperation("Extracting update files...", false);
				break;
				
			case VALIDATION_ENDED:
				setOperation("Validating update files...", false);
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
	
	public void setUpdateRequest(UpdateRequest source)
	{
		setTitle("Updating "+source.getApplicationInfo().getSoftwareName());
	}
	
	public void setUpdateVersion(UpdateVersion version)
	{
		progressBar.setMinimum(0);
		progressBar.setMaximum((int)version.getDownloadSize());
	}
	
	protected void updateProgress(int bytesDownloaded)
	{
		if (progressBar.isIndeterminate())
			progressBar.setIndeterminate(false);
		
		if (!cancelButton.isEnabled())
			cancelButton.setEnabled(true);
		
		progressBar.setValue(bytesDownloaded);
		
		if (Calendar.getInstance().getTimeInMillis() - bytesLastTime > 1000)
		{
			bytesLastTime = Calendar.getInstance().getTimeInMillis();
			currentSpeed = (int)(bytesDownloaded - bytesLast);
			
			bytesLast = bytesDownloaded;
		}
		
		statusLabel.setText(String.format("%s of %s (%s/s)", 
				FileUtils.bytesToSize(progressBar.getValue()),
				FileUtils.bytesToSize(progressBar.getMaximum()),
				FileUtils.bytesToSize(currentSpeed)));
	}
	
	protected void setOperation(String message, boolean cancellable)
	{
		setOperation(message, null, cancellable);
	}
	
	protected void setOperation(String message, String details, boolean cancellable)
	{
		if (!progressBar.isIndeterminate())
			progressBar.setIndeterminate(true);
		
		setMessage(message);
		statusLabel.setText(details);
		
		cancelButton.setEnabled(cancellable);
	}
	
	protected void setMessage(String message)
	{
		messageLabel.setText(String.format("<html><b>%s</b></html>", message));
	}
}
