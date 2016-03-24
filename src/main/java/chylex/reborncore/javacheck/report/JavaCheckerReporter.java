package chylex.reborncore.javacheck.report;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;

import chylex.reborncore.javacheck.util.ForgeCompatibility;

public final class JavaCheckerReporter
{
	public static void reportOutdatedJava(JavaVersion minVersion)
	{
		String consoleReport = getConsoleReport(minVersion);

		if (!ForgeCompatibility.tryLog(consoleReport))
		{
			System.out.println(consoleReport);
		}

		if (!GraphicsEnvironment.isHeadless() && ForgeCompatibility.isClientSide())
		{
			displayErrorPopup("Outdated Java", getHtmlReport(minVersion));
		}
	}

	private static void displayErrorPopup(String title, String contents)
	{
		JEditorPane pane = new JEditorPane("text/html",
				"<html><body style='font-family:Dialog;font-size:12;font-weight:bold'>" + contents + "</body></html>");
		pane.setBackground(new JLabel().getBackground());
		pane.setEditable(false);

		pane.addHyperlinkListener(new HyperlinkListener()
		{
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e)
			{
				if (e.getEventType() == EventType.ACTIVATED)
				{
					try
					{
						if (Desktop.isDesktopSupported())
							Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});

		JOptionPane.showMessageDialog(null, pane, title, JOptionPane.ERROR_MESSAGE);
	}

	private static String getConsoleReport(JavaVersion minVersion)
	{
		return "\n\n!! DO NOT REPORT !!\n\n" + "One of the mods requires Java " + minVersion
				+ " or newer, you are using " + SystemUtils.JAVA_VERSION + ".\n"
				+ "Visit https://java.com/download/ for the latest version.\n"
				+ "Please, uninstall the old version first to prevent further issues." + "\n\n!! DO NOT REPORT !!\n";
	}

	private static String getHtmlReport(JavaVersion minVersion)
	{
		return "One of the mods requires Java " + minVersion + " or newer, you are using " + SystemUtils.JAVA_VERSION
				+ ".<br>"
				+ "Visit <a href=\"https://java.com/download/\"><span style=\"color:blue\">https://java.com/download/</span></a> for the latest version.<br>"
				+ "Please, uninstall the old version first to prevent further issues.";
	}
}
