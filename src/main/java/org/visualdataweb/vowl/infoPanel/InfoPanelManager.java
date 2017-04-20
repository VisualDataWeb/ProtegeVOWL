package org.visualdataweb.vowl.infoPanel;

import org.apache.log4j.Logger;
import prefuse.util.ColorLib;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class InfoPanelManager {

	private static final Logger logger = Logger.getLogger(InfoPanelManager.class);
	private static HashMap<String, JTable> _infoPanel;

	/* The viewManagerID is an String extracted from the ViewManager of the current OWLWorkspace.
	 * It is used to identify the data which belongs to the current Protégé window.
	 * Within Protégé the user can open different ontologies, which can be shown within the same window or
	 * within different windows. If the are shown within different windows, they are the same protege instance.
	 * So an identifier is needed which is different for each protege instance but ontology independent.  */
	private String viewManagerID;

	/**
	 * constructor without setting/overwriting the stored infoPanel
	 */
	public InfoPanelManager(String id) {
		if (_infoPanel == null) {
			logger.info("infoPanel within InfoPanelManager is null!");
		}

		viewManagerID = id;
	}

	/**
	 * constructor with setting/overwriting the stored infoPanel
	 *
	 * @param id -	viewManagerID
	 */
	public InfoPanelManager(JTable infoPanel, String id) {
		setInfoPanel(infoPanel, id);

		// just clear all data within the info panel data storage (static!)
		InfoPanelDataStorage ipds = new InfoPanelDataStorage(viewManagerID);
		ipds.clearData();
		viewManagerID = id;
	}

	/**
	 * decodes a given string to an uri encoded with InfoPalenManager.helperModURI
	 *
	 * @param value String
	 * @return decoded String (as uri)
	 */
	public static String decodeToUri(String value) {
		final String first = "href=\"";
		int firstCut = value.indexOf(first);
		int endCut = value.indexOf("\">");

		return value.substring(firstCut + first.length(), endCut);
	}

	/**
	 * returns the infoPanel (JTable)
	 *
	 * @param id -	viewManagerID
	 * @return JTable
	 */
	public JTable getInfoPanel(String id) {
		return _infoPanel.get(id);
	}

	/**
	 * set info Panel
	 *
	 * @param infoPanel panel with the information
	 * @param id viewManagerID
	 */
	public void setInfoPanel(JTable infoPanel, String id) {
		// background color very light grey F2F2F2
		infoPanel.setBackground(ColorLib.getColor(242, 242, 242));

		if (_infoPanel == null) {
			_infoPanel = new HashMap<String, JTable>();
		}

		if (!_infoPanel.containsKey(id)) {
			_infoPanel.put(id, infoPanel);
		}
	}

	/**
	 * clears all data from the info Panel
	 *
	 * @param id -	viewManagerID
	 */
	public void clearTable(String id) {
		if (_infoPanel == null) {
			return;
		}

		InfoPanelDataStorage ipds = new InfoPanelDataStorage(viewManagerID);
		ipds.clearData();
		TableModel model = new QuadratTableModel(viewManagerID);

		if (_infoPanel.get(id) != null) {
			_infoPanel.get(id).setModel(model);
		}
	}

	/**
	 * adds data to the info Panel (to be shown within the info Panel)
	 *
	 * @param key   String
	 * @param value String
	 * @param id viewManagerID
	 */
	public void add(String key, String value, String id) {
		// NOTE the JTable doesn't support \n
		key = helperModLongString(key, id);

		if (helperContainsURI(value)) {
			value = helperModURI(value, id);
		} else {
			value = helperModLongString(value, id);
		}

		InfoPanelDataStorage ipds = new InfoPanelDataStorage(viewManagerID);
		ipds.addInfoPanelDataToStorage(key, value);
	}

	/**
	 * adds two line data to the info Panel (to be shown within the info Panel)
	 * like a name and a uri within the same line
	 *
	 * @param key  String
	 * @param name String
	 * @param uri  String
	 * @param id viewManagerID
	 */
	public void addNameUri(String key, String name, String uri, String id) {
		// NOTE the JTable doesn't support \n
		key = helperModLongString(key, id);

		if (helperContainsURI(uri)) {
			uri = helperModURI(uri, id);
		} else {
			uri = helperModLongString(uri, id);
		}

		InfoPanelDataStorage ipds = new InfoPanelDataStorage(viewManagerID);
		ipds.addInfoPanelDataToStorage(key, "<html>" + name + "<br>" + uri);
	}

	/**
	 * adds multiple line data to the info Panel (to be shown within the info Panel)
	 *
	 * @param key String
	 * @param value the multiple line data
	 * @param id viewManagerID
	 */
	public void addMultipleLine(String key, ArrayList<String> value, String id) {
		key = helperModLongString(key, id);
		String tmp = "<html>";

		for (String v : value) {
			if (helperContainsURI(v)) {
				v = helperModURI(v, id);
			}

			tmp = tmp + "<br>" + v;
		}

		InfoPanelDataStorage ipds = new InfoPanelDataStorage(viewManagerID);
		ipds.addInfoPanelDataToStorage(key, tmp);
	}

	/**
	 * refresh the table (updates the shown information within the info panel)
	 *
	 * @param id viewManagerID
	 */
	public void refreshTable(String id) {
		if (_infoPanel == null) {
			return;
		}

		QuadratTableModel model = new QuadratTableModel(viewManagerID);
		InfoPanelDataStorage ipds = new InfoPanelDataStorage(viewManagerID);
		model.setRowCount(ipds.getInfoPanelDataSize());

		if (_infoPanel.get(id) != null) {
			_infoPanel.get(id).setModel(model);
		}
	}

	private boolean helperContainsURI(String givenString) {
		return givenString != null && givenString.contains("http:");

	}

	private String helperModURI(String givenString, String id) {
		String changedString = "<html>  <a href=\"";
		changedString = changedString.concat(givenString);
		changedString = changedString.concat("\">");
		changedString = changedString.concat(helperModLongString(givenString, id));
		changedString = changedString.concat("</a>");

		return changedString;
	}

	/**
	 * remove all line breaks (<br>) from a String
	 *
	 * @param givenString String to remove the line breaks
	 * @return String String without line breaks
	 */
	public String helperRemoveAllLineBreakesInString(String givenString) {
		String result;
		result = givenString.replaceAll("<br>", "");
		return result;
	}

	/**
	 * if the givenString is longer as max_name_length a break line (<br>) is added every max_name_length
	 *
	 * @param givenString String
	 * @param id viewManagerID
	 * @return changedString String
	 */
	public String helperModLongString(String givenString, String id) {
		if (getInfoPanel(id) == null) {
			// abort if InfoPanel is disabled
			return givenString;
		}
		Canvas c = new Canvas();
		FontMetrics fm = c.getFontMetrics(getInfoPanel(id).getFont());
		String result = "<html>";
		String tempString = "";
		int max_pixel_available = getInfoPanel(id).getBounds().width / 2;    // only the half is available
		max_pixel_available = max_pixel_available - 50;

		for (char a : givenString.toCharArray()) {
			String tempStringWithoutBR = tempString.replace("<br>", "");
			tempStringWithoutBR = tempStringWithoutBR.replace("<html>", "");
			int tempStringSize = fm.stringWidth(tempStringWithoutBR + String.valueOf(a));

			if (max_pixel_available >= tempStringSize) {
				tempString = tempString + String.valueOf(a);
			} else {
				// detect blank spaces and use them for <br> to avoid breaking words
				int breakHere = tempString.lastIndexOf(" ");

				if (breakHere != -1) {
					String tempString2 = tempString.substring(0, breakHere) + "<br>" +
							tempString.substring(breakHere, tempString.length()) + String.valueOf(a);
					result = result + tempString2;
					tempString = "";
				} else {
					result = result + tempString + "<br>" + String.valueOf(a);
					tempString = "";
				}
			}
		}

		result = result + tempString + "</html>";

		return result;
	}

}
