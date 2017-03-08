package net.wheatlauncher;


import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * @author ci010
 */
class FXSystemTray
{
	private Stage stage;
	private URL imageLoc;

	static boolean createTray(Stage stage, URL icon)
	{
		Objects.requireNonNull(stage);
		Objects.requireNonNull(icon);
		FXSystemTray fxSystemTray = new FXSystemTray(stage, icon);
		return fxSystemTray.addAppToTray();
	}

	private void showStage()
	{
		if (stage != null)
		{
			if (!stage.isShowing())
			{
				stage.show();
				stage.toFront();
			}
			else
			{
				stage.hide();
			}
		}
	}

	private FXSystemTray(Stage stage, URL icon)
	{
		this.stage = stage;
		this.imageLoc = icon;
	}

	private boolean addAppToTray()
	{
		try
		{
			// ensure awt toolkit is initialized.
			java.awt.Toolkit.getDefaultToolkit();

			// app requires system tray support, just exit if there is no support.
			if (!java.awt.SystemTray.isSupported())
			{
				System.out.println("No system tray support, application exiting.");
				Platform.exit();
			}

			// set up a system tray icon.
			java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
			java.awt.Image image = ImageIO.read(imageLoc);
			java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);


			trayIcon.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					Platform.runLater(FXSystemTray.this::showStage);
				}
			});
			// if the user double-clicks on the tray icon, show the main app stage.
//			trayIcon.addActionListener(event ->
//			{
//				System.out.println("action");
//				Platform.runLater(this::showStage);
//			});

			// if the user selects the default menu item (which includes the app name),
			// show the main app stage.
			java.awt.MenuItem openItem = new java.awt.MenuItem("hello, world");
			openItem.addActionListener(event -> Platform.runLater(this::showStage));

			// the convention for tray icons seems to be to set the default icon for opening
			// the application stage in a bold font.
			java.awt.Font defaultFont = java.awt.Font.decode(null);
			java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
			openItem.setFont(boldFont);

			// to really exit the application, the user must go to the system tray icon
			// and select the exit option, this will shutdown JavaFX and remove the
			// tray icon (removing the tray icon will also shut down AWT).
			java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
			exitItem.addActionListener(event ->
			{
				Platform.exit();
				tray.remove(trayIcon);
			});

			// setup the popup menu for the application.
			final java.awt.PopupMenu popup = new java.awt.PopupMenu();
			popup.add(openItem);
			popup.add(exitItem);
			trayIcon.setPopupMenu(popup);

			tray.add(trayIcon);
		}
		catch (java.awt.AWTException | IOException e)
		{
			System.out.println("Unable to init system tray");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
