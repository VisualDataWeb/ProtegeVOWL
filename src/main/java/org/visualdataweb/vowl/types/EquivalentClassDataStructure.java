package org.visualdataweb.vowl.types;

/**
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class EquivalentClassDataStructure {

	private String classIRI;
	private String className;
	private String classComment;
	private String definedBy;
	private String owlVersion;
	private Boolean isExternal = false;
	private Boolean isDeprecated = false;

	/**
	 * returns the iri of the class
	 *
	 * @return classIRI as String
	 */
	public String getClassIRI() {
		return classIRI;
	}

	/**
	 * sets the iri of the class
	 *
	 * @param iri as String
	 */
	public void setClassIRI(String iri) {
		classIRI = iri;
	}

	/**
	 * returns the name of the class
	 *
	 * @return className    as String
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * set the name of the class
	 *
	 * @param name as String
	 */
	public void setClassName(String name) {
		className = name;
	}

	/**
	 * get the comment of an class as string
	 *
	 * @return String
	 */
	public String getClassComment() {
		return classComment;
	}

	/**
	 * saves the comment of an class as string
	 *
	 * @param comment as string
	 */
	public void setClassComment(String comment) {
		classComment = comment;
	}

	/**
	 * get the uri as string by which the class is defined by
	 *
	 * @return String
	 */
	public String getDefinedBy() {
		return definedBy;
	}

	/**
	 * saves the uri as string by which this class is defined by
	 *
	 * @param uri the uri to set
	 */
	public void setDefinedBy(String uri) {
		definedBy = uri;
	}

	/**
	 * get the owl version as string
	 *
	 * @return String
	 */
	public String getOWLVersion() {
		return owlVersion;
	}

	/**
	 * saves the owl version from this class as string
	 *
	 * @param version the owl version
	 */
	public void setOWLVersion(String version) {
		owlVersion = version;
	}

	/**
	 * change the state of the isDeprecated Boolean value. Per Default this value is set to false
	 *
	 * @param state the deprecated state
	 */
	public void setDeprecated(Boolean state) {
		isDeprecated = state;
	}

	/**
	 * change the state of the isExternal Boolean value. Per Default this value is set to false
	 *
	 * @param state the external state
	 */
	public void setExternal(Boolean state) {
		isExternal = state;
	}

	/**
	 * get the state of the isDeprecated Boolean value.The Default of this value is false
	 * return Boolean
	 */
	public Boolean getDeprecatedStatus() {
		return isDeprecated;
	}

	/**
	 * get the state of the isExternal Boolean value.The Default of this value is false
	 * return Boolean
	 */
	public Boolean getExternalStatus() {
		return isExternal;
	}
}
