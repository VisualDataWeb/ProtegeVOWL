package rendering;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.activity.Activity;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import storage.PrefuseForceAbstractLayoutStorage;
import types.ColumnNames;
import types.FontUsed;

public class RenderPrefuseGraph {

	// private static final Logger log = Logger.getLogger(RenderPrefuseGraph.class);
	public static double NameExtraDataSize = 0.80;

	/**
	 * @param prefuseDisplay
	 * @param graph
	 * @param id
	 */
	public RenderPrefuseGraph(Display prefuseDisplay, Graph graph, String id) {

		/* default color for nodes and edges 
		 * both is overwritten later (NodeRenderer and TextLayoutDecorator) */
		ColorAction fill = new ColorAction("GraphDataModifier.nodes", VisualItem.FILLCOLOR, ColorLib.rgb(0, 200, 0));
		ColorAction edges = new ColorAction("GraphDataModifier.edges", VisualItem.STROKECOLOR, ColorLib.rgb(0, 0, 0));
		ColorAction arrow = new ColorAction("GraphDataModifier.edges", VisualItem.FILLCOLOR, ColorLib.rgb(0, 0, 0));
		fill.add(VisualItem.FIXED, ColorLib.rgb(0, 250, 0));
		fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(0, 250, 0));

		ActionList layout = new ActionList(Activity.INFINITY);
		layout.add(fill); // add default node color
		layout.add(edges); // add default edge color
		layout.add(arrow); // add default edge color
		// set layout as force directed layout without (all objects must be displayed)
		ForceDirectedLayoutExtended fdle = new ForceDirectedLayoutExtended("GraphDataModifier", false);
		PrefuseForceAbstractLayoutStorage.addForceDirectedLayout(fdle, id);
		layout.add(fdle);
		layout.add(new TextLayoutDecorator("nodedec"));
		layout.add(new TextLayoutDecorator("NodeExtraData"));
		layout.add(new TextLayoutDecorator("EdgeExtraData"));
		layout.add(new RepaintAction());
		layout.add(new TextLayoutDecorator("edgeDeco"));

		final Visualization vis = new Visualization();
		vis.add("GraphDataModifier", graph);
		vis.putAction("layout", layout);
		NodeRenderer nodeRender = new NodeRenderer();

		EdgeRender edgeRender = new EdgeRender(prefuse.Constants.EDGE_TYPE_LINE, prefuse.Constants.EDGE_ARROW_FORWARD);
		edgeRender.setArrowHeadSize(10, 10);

		DefaultRendererFactory drf = new DefaultRendererFactory();
		drf.setDefaultRenderer(nodeRender);
		drf.setDefaultEdgeRenderer(edgeRender);
		drf.add(new InGroupPredicate("edgeDeco"), new LabelRenderer(ColumnNames.NAME));
		drf.add(new InGroupPredicate("nodedec"), new LabelRenderer(ColumnNames.NAME));
		final Schema DECATOR_SCHEMA = PrefuseLib.getVisualItemSchema();
		DECATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, false);
		DECATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.rgb(255, 255, 255));

		final Schema DECATOR_SCHEMA_EDGES = PrefuseLib.getVisualItemSchema();
		DECATOR_SCHEMA_EDGES.setDefault(VisualItem.INTERACTIVE, true);
		DECATOR_SCHEMA_EDGES.setDefault(VisualItem.TEXTCOLOR, ColorLib.rgb(255, 255, 255));

		DECATOR_SCHEMA.setDefault(VisualItem.FONT, FontUsed.getFont());
		DECATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(0));

		// add extra name field (should be done before the normal name field [for edges], otherwise normal field is covered with extra name background)
		final Schema DECATOR_SCHEMA_NAME_EXTRA = PrefuseLib.getVisualItemSchema();
		DECATOR_SCHEMA_NAME_EXTRA.setDefault(VisualItem.SIZE, NameExtraDataSize);
		DECATOR_SCHEMA_NAME_EXTRA.setDefault(VisualItem.INTERACTIVE, false);
		drf.add(new InGroupPredicate("NodeExtraData"), new LabelRenderer(ColumnNames.NAME_DATA));
		vis.addDecorators("NodeExtraData", "GraphDataModifier.nodes", DECATOR_SCHEMA_NAME_EXTRA);
		drf.add(new InGroupPredicate("EdgeExtraData"), new LabelRenderer(ColumnNames.NAME_DATA));
		// disabled because unhappy with the optical result
		// vis.addDecorators("EdgeExtraData", "GraphDataModifier.edges", DECATOR_SCHEMA_NAME_EXTRA);

		// add short name decorators 
		vis.addDecorators("edgeDeco", "GraphDataModifier.edges", DECATOR_SCHEMA_EDGES);
		vis.addDecorators("nodedec", "GraphDataModifier.nodes", DECATOR_SCHEMA);

		vis.setRendererFactory(drf);
		prefuseDisplay.setVisualization(vis);

		vis.run("layout");

	}

}
