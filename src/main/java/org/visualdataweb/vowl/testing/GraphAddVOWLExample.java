package org.visualdataweb.vowl.testing;

import org.visualdataweb.vowl.graphModifier.GraphDataModifier;
import org.visualdataweb.vowl.storage.GraphStorage;
import org.visualdataweb.vowl.types.Nodetype;

/**
 * This class adds an example graph to the graph storage.
 *
 * @author David Bold
 */
public class GraphAddVOWLExample {

	/**
	 * Adds a graph to the graph storage.
	 */
	public GraphAddVOWLExample() {
		String rndViewID = "1";
		GraphStorage.newGraph(rndViewID);
		GraphDataModifier mod = new GraphDataModifier(rndViewID);
		mod.addClassThing(1);
		mod.addClass("Person");
		mod.addInstanceToClass("Person", 5);
		mod.addClass("Agent");
		mod.addInstanceToClass("Agent", 16);
		mod.addClass("Document");
		mod.addProperty("Agent", Nodetype.vowltype[1], "Agent", Nodetype.vowltype[1]);
		mod.addInstanceToClass("Document", 12);
		mod.addDeprecatedClass("Spartial Thing");
		mod.addInstanceToClass(mod.findClass("Spartial Thing", Nodetype.vowltype[2]), 5);
		mod.addProperty("Person", Nodetype.vowltype[1], "Spartial Thing", Nodetype.vowltype[2]);
		mod.addProperty("Document", Nodetype.vowltype[1], "Agent", Nodetype.vowltype[1]);
	}

}
