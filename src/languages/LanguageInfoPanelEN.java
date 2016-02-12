package languages;

/**
 * This class contains static strings used as row captions within the info panel.
 * This file contains the translations, it could easily be replaced for different language support
 */
public class LanguageInfoPanelEN {

	public final static String NAME = "Name: ";
	public final static String TYPE = "Type: ";
	public final static String INSTANCES = "Individuals: ";
	public final static String URI = "IRI: ";
	public final static String COMMENT = "Comment : ";
	public final static String DEFINIED_BY = "Defined By: ";
	public final static String OWL_VERS_INFO = "Version Info: ";
	public final static String RDFS_DOMAIN_URI = "Domain IRI: ";
	public final static String RDFS_DOMAIN_LABEL = "Domain: ";
	public final static String RDFS_RANGE_URI = "Range IRI: ";
	public final static String RDFS_RANGE_LABEL = "Range: ";
	public final static String RDFS_INVERSE_OF_URI = "Inverse Property IRI: ";
	public final static String RDFS_INVERSE_OF_LABEL = "Inverse Property: ";
	public final static String EQUIVALENT_CLASS_IRI = "IRI of equivalent class: ";
	public final static String EQUIVALENT_CLASS_NAME = "Equivalent Class: ";
	public final static String CHARACTERISTIC = "Characteristic: ";
	public final static String EQUIVALENT_CLASS_COMMENT = "Equivalent Class Comment: ";
	public final static String EQUIVALENT_CLASS_DEFINED_BY = "Equivalent Class Comment Defined By: ";
	public final static String EQUIVALENT_CLASS_OWL_VERSION = "Equivalent Class Comment Version Info: ";
	public final static String EQUIVALENT_CLASS_DEPRECATED = "Equivalent Class is Deprecated: ";
	public final static String EQUIVALENT_CLASS_EXTERNAL = "Equivalent Class is External: ";

	/**
	 * must be the same order as Nodetype.vowltype but doesn't have to be unique !
	 */
	public final static String[] VOWL_NODE_TYPE_TRANSLATION = {"None", "Class", "Deprecated Class", "External Class", "Datatype", "RDFS Class", "OWLThing", "Literal", "Equivalent Class"};

	/**
	 * must be the same order as PropertyType.type but doesn't have to be unique !
	 */
	public final static String[] VOWL_EDGE_TRANSLATION = {"Property", "Object Property", "Datatype Property", "Sub Class"};

	/**
	 * must be the same order as EdgesType.characteristic but doesn't have to be unique !
	 */
	public final static String[] CHARACTERISTIC_TRANSLATION = {"Functional Property", "Symmetric Property", "Transitive Property", "Inverse-Functional Property"};

}
