package org.visualdataweb.vowl.storage;

import org.protege.editor.owl.model.OWLModelManager;
import org.visualdataweb.vowl.protege.OntologyChangedListener;
import org.visualdataweb.vowl.protege.OntologyModelManageListener;

import java.util.HashMap;

/**
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class OWLModelManagerStorage {

	private static HashMap<String, OWLModelManager> _owlManager;
	private static HashMap<String, OntologyModelManageListener> _owlModelManagerListener;
	private static HashMap<String, OntologyChangedListener> _owlOntologyChangedListener;

	/**
	 * Initialize any hashmap if it isn't yet.
	 */
	private static void createIfEmpty() {
		if (_owlManager == null) {
			_owlManager = new HashMap<String, OWLModelManager>();
		}

		if (_owlModelManagerListener == null) {
			_owlModelManagerListener = new HashMap<String, OntologyModelManageListener>();
		}

		if (_owlOntologyChangedListener == null) {
			_owlOntologyChangedListener = new HashMap<String, OntologyChangedListener>();
		}

	}

	/**
	 * saves the current OWLManager for later use
	 *
	 * @param o OWLModelManager
	 */
	public static void setOWLManager(OWLModelManager o, String id) {
		createIfEmpty();
		disposeListener(id);
		_owlManager.put(id, o);
		addListener(id);
	}

	/**
	 * returns the current used OWLManager (if stored from VOWLViewComponent)
	 *
	 * @return  the current OWLModelManager
	 */
	public static OWLModelManager getOWLManager(String id) {
		return _owlManager.get(id);
	}

	/**
	 * removes the ActiveOntologyChangedListener from the current _owlManager
	 */
	public static void disposeListener(String id) {
		if (_owlModelManagerListener != null && _owlManager != null && _owlManager.get(id) != null
				&& _owlModelManagerListener.get(id) != null) {
			_owlManager.get(id).removeListener(_owlModelManagerListener.get(id));
		}

		/* yet not working TODO
		if (_owlOntologyChangedListener != null) {
			_owlManager.removeOntologyChangeListener(_owlOntologyChangedListener);
		}
		*/
	}

	/**
	 * add the ActiveOntologyChangedListener to the current _owlManager
	 */
	private static void addListener(String id) {
		if (_owlModelManagerListener.get(id) == null) {
			_owlModelManagerListener.put(id, new OntologyModelManageListener(id));
			_owlManager.get(id).addListener(_owlModelManagerListener.get(id));
		}

		/*  yet not working
		if (_owlOntologyChangedListener == null){
			_owlManager.addOntologyChangeListener(_owlOntologyChangedListener);
		}
		*/
	}
}
