package org.visualdataweb.vowl.testing;

import prefuse.Display;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import org.visualdataweb.vowl.rendering.RenderPrefuseGraph;
import org.visualdataweb.vowl.rendering.VOWLWheelZoomControl;
import org.visualdataweb.vowl.storage.GraphStorage;

import javax.swing.*;

/**
 * This class if for testing the prefuse GraphDataModifier without protege !
 * It could GraphDataModifier false positives (no protege is available)
 *
 * @author David Bold
 */
public class StandaloneTesting {

	// FIXME check this values, maybe others are better
	private final static String JFRAME_NAME = "VOWL";
	private final static int DISPLAY_SIZE_H = 640;
	private final static int DISPLAY_SIZE_W = 860;

	/**
	 * @param args NONE
	 * @WARNING ONLY CALL PURPOSELY
	 * static main for testing this class without protege.
	 * will probably stop working of a interaction with protege is needed
	 */
	public static void main(String[] args) {

		// GraphDataModifier new (Prefuse)display
		Display innerDisplay = new Display();
		innerDisplay.setDamageRedraw(false);
		innerDisplay.setSize(DISPLAY_SIZE_W, DISPLAY_SIZE_H);
		innerDisplay.setHighQuality(true);

		Graph graph;
		// GraphDataModifier new GraphDataModifier with random Data
		CreateRandomGraphData crgd = new CreateRandomGraphData();
		graph = crgd.create();

		String rndViewID = "1";
		GraphAddVOWLExample example = new GraphAddVOWLExample();
		graph = GraphStorage.getGraph(rndViewID);
		
		
		/* GraphDataModifier JFrame and JPanel
		 * needed later do add the display from above.
		 * Why? Because a JPanel is needed to add it within protege  */
		JFrame jFrame = new JFrame(JFRAME_NAME);
		JPanel jPanel = new JPanel();
		jFrame.add(jPanel);
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		jFrame.add(innerDisplay);
		jFrame.pack();

		RenderPrefuseGraph rpg = new RenderPrefuseGraph(innerDisplay, graph, "1");
		innerDisplay.addControlListener(new DragControl());
		innerDisplay.addControlListener(new PanControl());
		innerDisplay.addControlListener(new ZoomControl());
		innerDisplay.addControlListener(new VOWLWheelZoomControl());
		
		/* show the jFrame 
		 * in this method within a own windows, everywhere als within protege */
		jFrame.setVisible(true);
	}

}
