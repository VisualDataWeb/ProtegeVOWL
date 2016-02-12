package storage;

import rendering.ForceDirectedLayoutExtended;

import java.util.HashMap;

/**
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class PrefuseForceAbstractLayoutStorage {

	private static HashMap<String, ForceDirectedLayoutExtended> _prefuseForceDirectedLayoutExtended;

	/**
	 * Add the current force directed layout to the force direct layout storage.
	 * An id (eg from getOWLWorkspace().getViewManager()) is used as identifier to detect with
	 * force directed layout belongs to wich view.
	 *
	 * @param forceLayout ForceDirectedLayoutExtended
	 * @param id String, the id of the view
	 */
	public static void addForceDirectedLayout(ForceDirectedLayoutExtended forceLayout, String id) {
		if (_prefuseForceDirectedLayoutExtended == null) {
			_prefuseForceDirectedLayoutExtended = new HashMap<String, ForceDirectedLayoutExtended>();
		}

		if (!_prefuseForceDirectedLayoutExtended.containsKey(id)) {
			_prefuseForceDirectedLayoutExtended.put(id, forceLayout);
		} else {
			_prefuseForceDirectedLayoutExtended.remove(id);
			_prefuseForceDirectedLayoutExtended.put(id, forceLayout);
		}
	}

	/**
	 * returns the current force directed layout from the force direct layout storage.
	 * An id (eg from getOWLWorkspace().getViewManager()) is used as identifier to detect with
	 * force directed layout belongs to wich view.
	 *
	 * @param id String, the id of the view
	 * @return the ForceDirectedLayout with belongs to the id
	 */
	public static ForceDirectedLayoutExtended getForceDirectedLayout(String id) {
		return _prefuseForceDirectedLayoutExtended.get(id);
	}

}
