package infoPanel;

/**
 * data structure used within the ArrayList infoPanelData
 */

public class InfoPanelDataStorageStructure {
	private String key;
	private String value;

	/**
	 * get the key from this specific key value pair as String
	 *
	 * @return String
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * set the key from a specific key value pair as String
	 *
	 * @param key setting the key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * get the value from this specific key value pair as String
	 *
	 * @return String
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * set the value from a specific key value pair as String
	 *
	 * @param value setting the value
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
