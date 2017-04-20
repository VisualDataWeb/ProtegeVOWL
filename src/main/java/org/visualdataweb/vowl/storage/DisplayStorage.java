package org.visualdataweb.vowl.storage;

import prefuse.Display;

import java.util.HashMap;

/**
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class DisplayStorage {

	private static HashMap<String, Display> _prefuseDisplay;

	/**
	 * saves current prefuseDisplay for later use (reload ontology)
	 *
	 * @param d display
	 * @param id the id of the Display (like getOWLWorkspace().getViewManager().toString())
	 */
	public static void addPrefuseDisplay(Display d, String id) {
		if (_prefuseDisplay == null) {
			_prefuseDisplay = new HashMap<String, Display>();
		}

		if (!_prefuseDisplay.containsKey(id)) {
			_prefuseDisplay.put(id, d);
		}
	}

	/**
	 * returns current prefuseDisplay
	 *
	 * @return current Display used by prefuse
	 * @param id the id of the Display (like getOWLWorkspace().getViewManager().toString())
	 */
	public static Display getPrefuseDisplay(String id) {
		return _prefuseDisplay.get(id);
	}

}

