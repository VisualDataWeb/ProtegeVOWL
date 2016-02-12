package protege;

import infoPanel.*;
import layout.TableLayout;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import types.FontUsed;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class VOWLSideBarComponent extends AbstractOWLViewComponent {

	private static final long serialVersionUID = 2L;    // default? TODO

	@Override
	protected void initialiseOWLView() throws Exception {
		
		/* TableLayout as layout manager, VOWLSideBarComponent fills the size of the view component in both directions.
		 * (remember the default size is given/limited from \view\VOWLTab.xml) 
		 * Useful if the layout is changed later! This LayoutManager is similar to the C# WPF Grid Layout,
		 * 	 {0.8, TableLayout.FILL} for 80% of the size for view 1 and the rest for view 2.
		 */
		TableLayout pluginMainFrameLayout = new TableLayout(
				new double[][]{
						{TableLayout.FILL},
						{TableLayout.FILL}
				}
		);
		setLayout(pluginMainFrameLayout);
		
		/* The viewManagerID is an String extracted from the ViewManager of the current OWLWorkspace.
		 * It is used to identify the data which belongs to the current Protégé window.
		 * Within Protégé the user can open different ontologies, which can be shown within the same window or
		 * within different windows. If the are shown within different windows, they are the same protege instance.
		 * So an identifier is needed which is different for each protege instance but ontology independent.  */
		String viewManagerID = getOWLWorkspace().getViewManager().toString();

		JPanel tableViewPanel = new JPanel();
		tableViewPanel.setLayout(new BorderLayout());
		JTable infoPanel = new JTable(new QuadratTableModel(viewManagerID));

		infoPanel.setFillsViewportHeight(true);
		infoPanel.setCellSelectionEnabled(true);

		// add click listener to the info panel (e.g.: for opening links)
		infoPanel.getSelectionModel().addListSelectionListener(new InfoPanelClickListener(viewManagerID));

		// add listener to modify the mouse cursor if mouse is over a link
		infoPanel.addMouseMotionListener(new InfoPanelMouseMotionListener(viewManagerID));

		// add component listener to change max_name_lenght if the component size is changed by the user
		infoPanel.addComponentListener(new InfoPanelComponentListener(viewManagerID));

		//infoPanel should be modified from ControlListener because it should react on clicks within the prefuse graph
		@SuppressWarnings("unused") InfoPanelManager ipm = new InfoPanelManager(infoPanel, viewManagerID);

		FontUsed.setFont(infoPanel.getFont());

		// don't want a table header
		infoPanel.setTableHeader(null);

		// reset the row count of the table 
		// needed to reset after ontology changed otherwise an empty table is visible 
		QuadratTableModel tModel = new QuadratTableModel(viewManagerID);
		tModel.setRowCount(0);

		// add scroll support 
		JScrollPane scrollPane = new JScrollPane(infoPanel);
		scrollPane.setSize(tableViewPanel.getSize().width, tableViewPanel.getSize().height);
		tableViewPanel.add(scrollPane);

		add(tableViewPanel, "0,0");
	}

	@Override
	protected void disposeOWLView() {
		// Nothing to do
	}
}