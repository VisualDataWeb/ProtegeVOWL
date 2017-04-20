package org.visualdataweb.vowl.infoPanel;

import java.util.ArrayList;
import java.util.HashMap;

public class InfoPanelDataStorage {

	/* _infoPanelHashMap is an HashMap storing all data for the InfoPanel.
	 * The key is the viewManagerID to separate the data structure for each protege instance.
	 * The ArrayList contains several InfoPanelDataStorageStructure and stores the data for the sidebar  */
	private static HashMap<String, ArrayList<InfoPanelDataStorageStructure>> _infoPanelHashMap;

	/* The viewManagerID is an String extracted from the ViewManager of the current OWLWorkspace.
	 * It is used to identify the data which belongs to the current Protégé window.
	 * Within Protégé the user can open different ontologies, which can be shown within the same window or
	 * within different windows. If the are shown within different windows, they are the same protege instance.
	 * So an identifier is needed which is different for each protege instance but ontology independent.  */
	private String viewManagerID;

	/**
	 * constructor
	 *
	 * @param id viewManagerID
	 */
	public InfoPanelDataStorage(String id) {
		viewManagerID = id;

		if (_infoPanelHashMap == null) {
			_infoPanelHashMap = new HashMap<String, ArrayList<InfoPanelDataStorageStructure>>();
		}

		if (!_infoPanelHashMap.containsKey(id)) {
			ArrayList<InfoPanelDataStorageStructure> arrayList = new ArrayList<InfoPanelDataStorageStructure>();
			_infoPanelHashMap.put(id, arrayList);
		}
	}

	/**
	 * clears all data stored within InfoPanelDataStorage
	 */
	public void clearData() {
		if (_infoPanelHashMap != null && _infoPanelHashMap.get(viewManagerID) != null) {
			ArrayList<InfoPanelDataStorageStructure> arrayList = _infoPanelHashMap.get(viewManagerID);
			arrayList.clear();
		}
	}

	/**
	 * add data to the info panel data storage
	 *
	 * @param key   String
	 * @param value String
	 */
	public void addInfoPanelDataToStorage(String key, String value) {
		InfoPanelDataStorageStructure ipdss = new InfoPanelDataStorageStructure();
		ipdss.setKey(key);
		ipdss.setValue(value);
		_infoPanelHashMap.get(viewManagerID).add(ipdss);
	}

	/**
	 * returns the size of the info panel (how many columns)
	 *
	 * @return integer
	 */
	public int getInfoPanelDataSize() {
		if (_infoPanelHashMap != null && _infoPanelHashMap.get(viewManagerID) != null)
			return _infoPanelHashMap.get(viewManagerID).size();
		else
			return 0;
	}

	/**
	 * get data from the info panel data storage
	 *
	 * @param index number of the index.
	 * @return InfoPanelDataStorageStructure (key value pair)
	 */
	public InfoPanelDataStorageStructure getInfoPanelDataFromStorage(int index) {
		boolean nullCheck = _infoPanelHashMap == null;
		boolean sizeCheck = _infoPanelHashMap != null && _infoPanelHashMap.get(viewManagerID).size() == 0;

		if (nullCheck || sizeCheck) {
			return null;
		}

		if (_infoPanelHashMap.get(viewManagerID).size() > index) {
			return _infoPanelHashMap.get(viewManagerID).get(index);
		} else {
			return null;
		}
	}

}


