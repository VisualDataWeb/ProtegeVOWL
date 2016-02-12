package infoPanel;

import org.apache.log4j.Logger;
import types.FontUsed;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

public class QuadratTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private final Logger logger = Logger.getLogger(QuadratTableModel.class);
	private int columnCount = 2;    // only two columns within the info Panel // right
	private int rowCount = 0;    // default 0 rows (empty table, is changed later)

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
	public QuadratTableModel(String id) {
		super();
		viewManagerID = id;
	}

	/**
	 * returns the row count
	 *
	 * @return row count
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * sets the row count
	 *
	 * @param howmany row counts
	 */
	public void setRowCount(int howmany) {
		rowCount = howmany;
	}

	/**
	 * returns the column count
	 *
	 * @return column count
	 */
	public int getColumnCount() {
		return columnCount;
	}

	/**
	 * sets the column count
	 *
	 * @param howmany column counts
	 */
	public void setColumnCount(int howmany) {
		columnCount = howmany;
	}

	/**
	 * extract the values from the InfoPanelDataStorage
	 * and make them compatible with the AbstractTableModel data
	 */
	public Object getValueAt(int row, int col) {
		InfoPanelDataStorage ipds = new InfoPanelDataStorage(viewManagerID);
		InfoPanelDataStorageStructure ipdss = ipds.getInfoPanelDataFromStorage(row);

		if (ipdss != null) {
			if (col == 0) {
				String answer = ipdss.getKey();
				helperCalculateNeededRowHeight(answer, row);
				return answer;
			}

			if (col == 1) {
				String answer = ipdss.getValue();
				helperCalculateNeededRowHeight(answer, row);
				return answer;
			}

			logger.error("ERROR - the infoPanel has only two coloumns!");

			return "";
		} else {
			setRowCount(getRowCount() - 1);
			return "";
		}
	}

	/**  TODO check with regex
	 * calculates the needed row height for a given row, depending on the text
	 * to be shown within this row
	 *
	 * @param givenString String
	 * @param row Integer
	 */
	public void helperCalculateNeededRowHeight(String givenString, int row) {
		int count = 0;

		if (givenString != null) {
			// how many <br> are within the given string
			// givenString.length()-3 because the next 3 chars are checked: < and br>
			for (int i = 0; i < givenString.length() - 3; i++) {
				char iterator = givenString.charAt(i);

				if (iterator == '<') {
					iterator = givenString.charAt(i + 1);

					if (iterator == 'b') {
						iterator = givenString.charAt(i + 2);

						if (iterator == 'r') {
							iterator = givenString.charAt(i + 3);

							if (iterator == '>') {
								count++;
							}
						}
					}
				}
			}

			// end of for (int i=0; i<s.length(); i++)
			// no need to change something if no <br> is found within the given string
			if (count != 0) {
				// calculate the needed size for the given row
				InfoPanelManager ipm = new InfoPanelManager(viewManagerID);
				JTable table = ipm.getInfoPanel(viewManagerID);
				Canvas c = new Canvas();
				FontMetrics fm = c.getFontMetrics(FontUsed.getFont());

				// why * 1.1 ? -> increase the size the font needed a little bit
				table.setRowHeight(row, (int) ((count + 2) * fm.getHeight() * 1.1));
				table.setPreferredSize(table.getPreferredScrollableViewportSize());
			}
		}
	}

}