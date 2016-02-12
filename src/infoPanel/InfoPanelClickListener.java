package infoPanel;

import org.apache.log4j.Logger;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.io.IOException;
import java.net.URISyntaxException;

public class InfoPanelClickListener implements ListSelectionListener {

	private static final Logger logger = Logger.getLogger(InfoPanelClickListener.class);

	/* The viewManagerID is an String extracted from the ViewManager of the current OWLWorkspace.
	 * It is used to identify the data which belongs to the current Protégé window.
	 * Within Protégé the user can open different ontologies, which can be shown within the same window or
	 * within different windows. If the are shown within different windows, they are the same protege instance.
	 * So an identifier is needed which is different for each protege instance but ontology independent.  */
	private String viewManagerID;

	/**
	 * constructor
	 *
	 * @param id - viewManagerID
	 */
	public InfoPanelClickListener(String id) {
		viewManagerID = id;
	}

	/**
	 * click listener for the info panel for opening URIs within a web browser
	 */
	public void valueChanged(ListSelectionEvent event) {
		if (event.getValueIsAdjusting()) {
			return;
		}

		InfoPanelManager ipm = new InfoPanelManager(viewManagerID);
		int[] selected = ipm.getInfoPanel(viewManagerID).getSelectedRows();

		for (int i : selected) {
			String value = ipm.getInfoPanel(viewManagerID).getModel().getValueAt(i, 1).toString();

			if (!value.contains("http:")) {
				continue;
			}

			// the selected row within info panel contains an uri -> open within browser
			java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
			java.net.URI uri;

			try {
				uri = new java.net.URI(InfoPanelManager.decodeToUri(value));
				desktop.browse(uri);
			} catch (IOException e) {
				logger.warn("Exception! InfoPanelClickListener can't decode given URI " + e.toString());
			} catch (URISyntaxException e) {
				logger.warn("Exception! InfoPanelClickListener can't decode given URI " + e.toString());
			}
		}
	}

}
