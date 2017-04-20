package org.visualdataweb.vowl.protege;

import org.visualdataweb.vowl.controlView.GrafityClassesSliderListener;
import org.visualdataweb.vowl.controlView.GravityLiteralsSliderListener;
import org.visualdataweb.vowl.controlView.RunLayoutControl;
import org.visualdataweb.vowl.infoPanel.VersionInfo;
import org.visualdataweb.vowl.languages.LanguageControlViewEN;
import layout.TableLayout;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.visualdataweb.vowl.storage.ControlViewStorage;
import org.visualdataweb.vowl.storage.DisplayStorage;
import org.visualdataweb.vowl.storage.GraphStorage;
import org.visualdataweb.vowl.types.FontUsed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class VOWLControlViewComponent extends AbstractOWLViewComponent {

	private static final long serialVersionUID = 1492294910817476613L;
	private Button renderToggleButton;
	private JSlider gravityLiteralsSlider;
	private JSlider gravityClassesSlider;
	private Button resetViewButton;

	@Override
	protected void initialiseOWLView() throws Exception {
		TableLayout controlDisplayLayout = new TableLayout(
				new double[][]{
						{0.5, TableLayout.FILL},
						{TableLayout.FILL, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL}
				}
		);
		setLayout(controlDisplayLayout);
		
		/* The viewManagerID is an String extracted from the ViewManager of the current OWLWorkspace.
		 * It is used to identify the data which belongs to the current Protégé window.
		 * Within Protégé the user can open different ontologies, which can be shown within the same window or
		 * within different windows. If the are shown within different windows, they are the same protege instance.
		 * So an identifier is needed which is different for each protege instance but ontology independent.  */
		String viewManagerID = getOWLWorkspace().getViewManager().toString();

		// label change gravity for classes
		Label gravityLabel = new Label();
		gravityLabel.setText(LanguageControlViewEN.CLASS_DISTANCE);
		add(gravityLabel, "0,0");

		// label change gravity for Datatype Properties / Literals
		Label gravityNodesLabel = new Label();
		gravityNodesLabel.setText(LanguageControlViewEN.LITERAL_DISTANCE);
		add(gravityNodesLabel, "0,1");

		// slider + slider listener for changing the gravity of classes
		gravityClassesSlider = new JSlider(JSlider.HORIZONTAL, 0, 300, 100);
		GrafityClassesSliderListener grafityClassesSliderListener = new GrafityClassesSliderListener(viewManagerID);
		gravityClassesSlider.addChangeListener(grafityClassesSliderListener);
		gravityClassesSlider.setEnabled(false);
		gravityClassesSlider.setPaintTicks(true);
		add(gravityClassesSlider, "1,0");

		// slider + slider listener for changing the gravity of DataType Properties / Literals
		gravityLiteralsSlider = new JSlider(JSlider.HORIZONTAL, 0, 300, 100);
		GravityLiteralsSliderListener gravityLiteralsSliderListener = new GravityLiteralsSliderListener(viewManagerID);
		gravityLiteralsSlider.addChangeListener(gravityLiteralsSliderListener);
		gravityLiteralsSlider.setPaintTicks(true);
		gravityLiteralsSlider.setEnabled(false);
		add(gravityLiteralsSlider, "1,1");

		// add a label to identify the version number
		final JLabel versionLabel = new JLabel();
		versionLabel.setText("<html>" + LanguageControlViewEN.PLUGIN_VERSION + VersionInfo.VERSION_INFO + "<br>" +
				"<a href=\"" + VersionInfo.HTTP_LINK + "\">" + VersionInfo.HTTP_LINK + "</a>");
		Font versionLabelFont = new Font(FontUsed.getFont().getFontName(), Font.PLAIN, FontUsed.getFont().getSize() - 2);

		versionLabel.addMouseListener(new MouseAdapter() {
			// open link if use clicks on the versionLabel
			public void mousePressed(MouseEvent me) {
				java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
				URI link;
				try {
					link = new URI(VersionInfo.HTTP_LINK);
					desktop.browse(link);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}

			// change mouse cursor if mouse is over the versionLabel to indicate the user the link can be clicked
			public void mouseEntered(MouseEvent arg0) {
				versionLabel.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			// restore mouse cursor if the mouse pointer isn't over the versionLabel
			public void mouseExited(MouseEvent arg0) {
				versionLabel.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});

		versionLabel.setFont(versionLabelFont);
		JPanel versionLabelPanel = new JPanel();
		versionLabelPanel.setLayout(new BorderLayout());
		versionLabelPanel.add(versionLabel, BorderLayout.SOUTH);
		add(versionLabelPanel, "0,3");

		// add a button to determine if the prefuse graph should be paused or not
		renderToggleButton = new Button();
		renderToggleButton.setLabel(LanguageControlViewEN.renderToggleButtonOff);
		renderToggleButton.addActionListener(new ActionListener() {
			// toggle state of the renderToggleButton
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String renderToggleButtonText = renderToggleButton.getLabel();

				if (renderToggleButtonText.equals(LanguageControlViewEN.renderToggleButtonOff)) {
					// stop the visualization
					RunLayoutControl rlc = new RunLayoutControl(getOWLWorkspace().getViewManager().toString());
					rlc.stopLayouting();
					// disable the sliders, because the visualization has stopped
					gravityClassesSlider.setEnabled(false);
					gravityLiteralsSlider.setEnabled(false);
					// update the label of the button to identify the new state of the button
					renderToggleButton.setLabel(LanguageControlViewEN.renderToggleButtonOn);
				}

				if (renderToggleButtonText.equals(LanguageControlViewEN.renderToggleButtonOn)) {
					// activate the visualization
					RunLayoutControl rlc = new RunLayoutControl(getOWLWorkspace().getViewManager().toString());
					rlc.startLayouting();
					// enable the sliders, because the visualization is running again
					gravityClassesSlider.setEnabled(true);
					gravityLiteralsSlider.setEnabled(true);
					// update the label of the button to identify the new state of the button
					renderToggleButton.setLabel(LanguageControlViewEN.renderToggleButtonOff);
				}
			}
		});

		renderToggleButton.setEnabled(false);

		resetViewButton = new Button();
		resetViewButton.setLabel(LanguageControlViewEN.resetViewButton);
		resetViewButton.addActionListener(new ActionListener() {
			// action listener to 'reset' the view (zoom back to default coordinates & zoom level)
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!DisplayStorage.getPrefuseDisplay(getOWLWorkspace().getViewManager().toString()).isTranformInProgress()) {
					DisplayStorage.getPrefuseDisplay(getOWLWorkspace().getViewManager().toString()).animatePanAndZoomToAbs(
							new Point(0, 0),
							1.0 / DisplayStorage.getPrefuseDisplay(getOWLWorkspace().getViewManager().toString()).getScale(),
							100);
				}
			}
		});

		resetViewButton.setEnabled(false);
		add(resetViewButton, "1,3");

		@SuppressWarnings("unused") ControlViewStorage cvs = new ControlViewStorage(renderToggleButton, gravityClassesSlider, gravityLiteralsSlider);

		// add a thread to enable the controls after an ontology has been loaded
		Thread t = new enableGUIComponentsThread(renderToggleButton, gravityClassesSlider, gravityLiteralsSlider, resetViewButton, getOWLWorkspace().getViewManager().toString());
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
		add(renderToggleButton, "1,2");
	}

	@Override
	protected void disposeOWLView() {
		// empty TODO
	}
}

/**
 * this class is just a thread to enable the slider and the button after an ontology has been loaded.
 * It checks every second if an ontology has been loaded by determining weather at least one node exists within the ontology.
 * If this check is confirmed, the buttons get enabled.
 */
class enableGUIComponentsThread extends Thread {

	private Button renderToggleButton;
	private Button resetViewButton;
	private JSlider js1;
	private JSlider js2;
	private String viewId;

	/**
	 * a constructor to store several gui elements to change them within this thread
	 *
	 * @param button the pause / start layouting button
	 * @param j1 the gravityClassesSlider
	 * @param j2 the gravityLiteralsSlider
	 * @param reset the button to reset the graph view
	 * @param id the id of the corresponding view manager (getOWLWorkspace().getViewManager().toString())
	 * @throws InterruptedException
	 */
	enableGUIComponentsThread(Button button, JSlider j1, JSlider j2, Button reset, String id) throws InterruptedException {
		renderToggleButton = button;
		resetViewButton = reset;
		js1 = j1;
		js2 = j2;
		viewId = id;
		setDaemon(true);
	}

	/**
	 * A thread which checks the current graph every second if it contains elements.
	 * If the Graph contains elements it can't be an empty graph, 	from this it follows that an ontology has been loaded.
	 * This means we have to enable the buttons and slider and disable this thread.
	 */
	@Override
	public void run() {
		while (!isInterrupted()) {
			int nodeCount = GraphStorage.getGraph(viewId).getNodeCount();

			if (nodeCount != 0) {
				renderToggleButton.setEnabled(true);
				resetViewButton.setEnabled(true);
				js1.setEnabled(true);
				js2.setEnabled(true);
				js1.setToolTipText(LanguageControlViewEN.SLIDER_TOOLTIP + js1.getValue());
				js2.setToolTipText(LanguageControlViewEN.SLIDER_TOOLTIP + js2.getValue());
				interrupt();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				interrupt();
			}
		}
	}
}