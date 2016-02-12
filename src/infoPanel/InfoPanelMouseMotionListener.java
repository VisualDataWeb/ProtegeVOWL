package infoPanel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class InfoPanelMouseMotionListener implements MouseMotionListener {

	/* The viewManagerID is an String extracted from the ViewManager of the current OWLWorkspace.
	 * It is used to identify the data which belongs to the current Protégé window.
	 * Within Protégé the user can open different ontologies, which can be shown within the same window or
	 * within different windows. If the are shown within different windows, they are the same protege instance.
	 * So an identifier is needed which is different for each protege instance but ontology independent.  */
	private String viewManagerID;

	/**
	 * Constructor
	 *
	 * @param id viewManagerID
	 */
	public InfoPanelMouseMotionListener(String id) {
		super();
		viewManagerID = id;

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// nothing to do
	}

	/**
	 * mouse moved action listener
	 */
	@Override
	public void mouseMoved(MouseEvent arg0) {
		InfoPanelManager ipm = new InfoPanelManager(viewManagerID);
		Point p = arg0.getPoint();
		int row = ipm.getInfoPanel(viewManagerID).rowAtPoint(p);
		int col = ipm.getInfoPanel(viewManagerID).columnAtPoint(p);

		// check if mouse is inside the infoPanel
		if (row != -1 && col != -1) {
			String value = ipm.getInfoPanel(viewManagerID).getModel().getValueAt(row, col).toString();

			if (value.contains("http:")) {
				// there is a link, so use hand cursor
				ipm.getInfoPanel(viewManagerID).getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			} else {
				// there is no link, use default cursor
				ipm.getInfoPanel(viewManagerID).getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		} else {

			if (ipm.getInfoPanel(viewManagerID) != null && ipm.getInfoPanel(viewManagerID).getRootPane() != null) {
				// outside the infoPanel, use default Cursor
				ipm.getInfoPanel(viewManagerID).getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

}
