package infoPanel;

import graphModifier.GraphDataModifier;
import languages.LanguageInfoPanelEN;
import org.apache.log4j.Logger;
import prefuse.data.Edge;
import prefuse.data.Node;
import types.*;

import java.util.ArrayList;

public class InfoPanelDataExtractor {

	private static final Logger log = Logger.getLogger(InfoPanelDataExtractor.class);
	/* The viewManagerID is an String extracted from the ViewManager of the current OWLWorkspace.
	 * It is used to identify the data which belongs to the current Protégé window.
	 * Within Protégé the user can open different ontologies, which can be shown within the same window or
	 * within different windows. If the are shown within different windows, they are the same protege instance.
	 * So an identifier is needed which is different for each protege instance but ontology independent.  */
	private String viewManagerID;

	/**
	 * constructor for data extraction from a Node to info panel's data.
	 * Using this constructor also guarantees a cleared info panel without any data
	 *
	 * @param node NodeItem
	 * @param id   viewManagerID
	 */
	public InfoPanelDataExtractor(Node node, String id) {
		viewManagerID = id;
		InfoPanelManager ipm = new InfoPanelManager(id);
		ipm.clearTable(id);
		ipm.refreshTable(id);
		extractFromNode(node, ipm);
		ipm.refreshTable(id);
	}

	/**
	 * constructor for data extraction from a Edge to info panel's data.
	 * Using this constructor also guarantees a cleared info panel without any data
	 *
	 * @param edge EdgeItem
	 * @param id   viewManagerID
	 */
	public InfoPanelDataExtractor(Edge edge, String id) {
		viewManagerID = id;
		InfoPanelManager ipm = new InfoPanelManager(id);
		ipm.clearTable(id);
		extractFromEdge(edge, ipm);
		ipm.refreshTable(id);
	}

	/**
	 * extracts informations from a given node and show them within the info panel
	 *
	 * @param node Node
	 * @param ipm  InfoPanelManager
	 */
	private void extractFromNode(Node node, InfoPanelManager ipm) {
		String vowlType = translateVOWLNodeType((String) node.get(node.getColumnIndex(ColumnNames.NODE_VOWL_TYPE)));
		addHelper(ipm, LanguageInfoPanelEN.TYPE, vowlType);
		/* disabled for release, not used yet
		Object testClassInstanceCount = node.get(node.getColumnIndex(ColumnNames.CLASS_INSTANCE_COUNT));
		if (testClassInstanceCount != null)  {
			// data property nodes don't have a instance count, so this object could be a null pointer
			ipm.add(LanguageInfoPanelEN.INSTANCES, testClassInstanceCount.toString());			
		}   */
		String nodeName = (String) node.get(node.getColumnIndex(ColumnNames.FULL_NAME));
		String uri = (String) node.get(node.getColumnIndex(ColumnNames.RDFS_URI));
		addNameUriHelper(ipm, LanguageInfoPanelEN.NAME, nodeName, uri);
		String comment = (String) node.get(node.getColumnIndex(ColumnNames.RDFS_COMMENT));
		addHelper(ipm, LanguageInfoPanelEN.COMMENT, comment);
		/* disabled, information is 'useless'
		String definedBy = (String) node.get(node.getColumnIndex(ColumnNames.RDFS_DEFINED_BY));
		addHelper(ipm, LanguageInfoPanelEN.DEFINIED_BY, definedBy);   */
		String owlVersionInfo = (String) node.get(node.getColumnIndex(ColumnNames.OWL_VERSION_INFO));
		addHelper(ipm, LanguageInfoPanelEN.OWL_VERS_INFO, owlVersionInfo);
		String rdfsInverseOf = (String) node.get(node.getColumnIndex(ColumnNames.RDFS_INVERSE_OF));
		addHelper(ipm, LanguageInfoPanelEN.RDFS_INVERSE_OF_URI, rdfsInverseOf);
		GraphDataModifier mod = new GraphDataModifier(viewManagerID);
		int rdfsInverseOfID = mod.findNodeElement(rdfsInverseOf);
		if (rdfsInverseOfID != -1) {
			String rdfsInverseOfLabel = (String) mod.findNode(rdfsInverseOfID).get(ColumnNames.FULL_NAME);
			addNameUriHelper(ipm, LanguageInfoPanelEN.RDFS_INVERSE_OF_LABEL, rdfsInverseOfLabel, rdfsInverseOf);
		} else {
			addHelper(ipm, LanguageInfoPanelEN.RDFS_INVERSE_OF_LABEL, rdfsInverseOf);
		}
		try {
			@SuppressWarnings("unchecked")
			ArrayList<EquivalentClassDataStructure> iriList = (ArrayList<EquivalentClassDataStructure>) node.get(ColumnNames.EQUIVALENT_CLASSES);
			if (iriList != null && iriList.size() != 0) {
				for (EquivalentClassDataStructure ecds : iriList) {
					addNameUriHelper(ipm, LanguageInfoPanelEN.EQUIVALENT_CLASS_NAME, ecds.getClassName(), ecds.getClassIRI());
					addHelper(ipm, LanguageInfoPanelEN.EQUIVALENT_CLASS_COMMENT, ecds.getClassComment());
					addHelper(ipm, LanguageInfoPanelEN.EQUIVALENT_CLASS_OWL_VERSION, ecds.getOWLVersion());
					addHelper(ipm, LanguageInfoPanelEN.EQUIVALENT_CLASS_EXTERNAL, ecds.getExternalStatus(), false);
					addHelper(ipm, LanguageInfoPanelEN.EQUIVALENT_CLASS_DEPRECATED, ecds.getDeprecatedStatus(), false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * extracts informations from a given edge and show them within the info panel
	 *
	 * @param edge Edge
	 * @param ipm  InfoPanelManager
	 */
	private void extractFromEdge(Edge edge, InfoPanelManager ipm) {
		String vowlType = translateVOWLEdgeType((String) edge.get(edge.getColumnIndex(ColumnNames.EDGE_VOWL_TYPE)));
		addHelper(ipm, LanguageInfoPanelEN.TYPE, vowlType);
		String edgeName = (String) edge.get(edge.getColumnIndex(ColumnNames.FULL_NAME));
		String uri = (String) edge.get(edge.getColumnIndex(ColumnNames.RDFS_URI));
		addNameUriHelper(ipm, LanguageInfoPanelEN.NAME, edgeName, uri);
		String coment = (String) edge.get(edge.getColumnIndex(ColumnNames.RDFS_COMMENT));
		addHelper(ipm, LanguageInfoPanelEN.COMMENT, coment);
		/*  disabled, information is 'useless'
		String definedBy = (String) edge.get(edge.getColumnIndex(ColumnNames.RDFS_DEFINED_BY));
		addHelper(ipm, LanguageInfoPanelEN.DEFINIED_BY, definedBy);   */
		String owlVersionInfo = (String) edge.get(edge.getColumnIndex(ColumnNames.OWL_VERSION_INFO));
		addHelper(ipm, LanguageInfoPanelEN.OWL_VERS_INFO, owlVersionInfo);
		String rdfsDomain = (String) edge.get(edge.getColumnIndex(ColumnNames.RDFS_DOMAIN));
		GraphDataModifier mod = new GraphDataModifier(viewManagerID);
		int rdfsDomainLabelID = mod.findNodeElement(rdfsDomain);
		if (rdfsDomainLabelID != -1) {
			String rdfsDomainLabel = (String) mod.findNode(rdfsDomainLabelID).get(ColumnNames.FULL_NAME);
			addNameUriHelper(ipm, LanguageInfoPanelEN.RDFS_DOMAIN_LABEL, rdfsDomainLabel, rdfsDomain);
		} else {
			addHelper(ipm, LanguageInfoPanelEN.RDFS_DOMAIN_URI, rdfsDomain);
		}
		String rdfsRange = (String) edge.get(edge.getColumnIndex(ColumnNames.RDFS_RANGE));
		int rdfsRangeLabelID = mod.findNodeElement(rdfsRange);
		if (rdfsRangeLabelID != -1) {
			String rdfsRangeLabel = (String) mod.findNode(rdfsRangeLabelID).get(ColumnNames.FULL_NAME);
			addNameUriHelper(ipm, LanguageInfoPanelEN.RDFS_RANGE_LABEL, rdfsRangeLabel, rdfsRange);
		} else {
			addHelper(ipm, LanguageInfoPanelEN.RDFS_RANGE_URI, rdfsRange);
		}
		String rdfsInverseOf = (String) edge.get(edge.getColumnIndex(ColumnNames.RDFS_INVERSE_OF));
		int rdfsInverseOfID = mod.findEdge(rdfsInverseOf);
		if (rdfsInverseOfID != -1) {
			String rdfsInverseOfLabel = (String) mod.getEdge(rdfsInverseOf).get(ColumnNames.FULL_NAME);
			addNameUriHelper(ipm, LanguageInfoPanelEN.RDFS_INVERSE_OF_LABEL, rdfsInverseOfLabel, rdfsInverseOf);
		} else {
			addHelper(ipm, LanguageInfoPanelEN.RDFS_INVERSE_OF_LABEL, rdfsInverseOf);
		}
		addMultiLineHelper(ipm, LanguageInfoPanelEN.CHARACTERISTIC, translateCharacteristic((String) edge.get(ColumnNames.NAME_DATA)));
	}

	/**
	 * helper to add data to the info panel. This helper checks if value
	 * is not null and contains any data (don't add empty data to the info panel)
	 *
	 * @param ipm   InfoPanelManager
	 * @param name  String
	 * @param value String
	 */
	private void addHelper(InfoPanelManager ipm, String name, String value) {
		if (value != null && value.length() != 0) {
			ipm.add(name, value, viewManagerID);
		}
	}

	/**
	 * helper to add data to the info panel. This helper checks if value is different to the default value.
	 * In this case the value is added.
	 *
	 * @param ipm          InfoPanelManager
	 * @param name         String
	 * @param value        Boolean
	 * @param defaultValue Boolean
	 */
	private void addHelper(InfoPanelManager ipm, String name, Boolean value, Boolean defaultValue) {
		if (value != defaultValue) {
			ipm.add(name, value.toString(), viewManagerID);
		}
	}

	/**
	 * helper to add data to the info panel. This helper checks if value
	 * is not null and contains any data (don't add empty data to the info panel)
	 *
	 * @param ipm   InfoPanelManager
	 * @param name  String
	 * @param uri   String
	 * @param value String
	 */
	private void addNameUriHelper(InfoPanelManager ipm, String name, String value, String uri) {
		if (value != null && value.length() != 0) {
			ipm.addNameUri(name, value, uri, viewManagerID);
		}
	}

	/**
	 * helper to add multiple line data to the info panel. This helper checks if value
	 * is not null and contains any data (don't add empty data to the info panel
	 *
	 * @param ipm   InfoPanelManager
	 * @param name  String
	 * @param value StringArray
	 */
	private void addMultiLineHelper(InfoPanelManager ipm, String name, ArrayList<String> value) {
		if (value != null && value.size() != 0) {
			ipm.addMultipleLine(name, value, viewManagerID);
		}
	}

	/**
	 * converts the VOWL Type from the technical definition from Nodetype.vowltype
	 * to a human better readable from from LanguageInfoPanelEN.VOWL_NODE_TYPE_TRANSLATION
	 *
	 * @param vowlType String
	 * @return translate String
	 */
	private String translateVOWLNodeType(String vowlType) {
		for (int i = 0; i < Nodetype.vowltype.length; i++) {
			if (Nodetype.vowltype[i].equals(vowlType)) {
				if (LanguageInfoPanelEN.VOWL_NODE_TYPE_TRANSLATION.length == Nodetype.vowltype.length) {
					return LanguageInfoPanelEN.VOWL_NODE_TYPE_TRANSLATION[i];
				} else {
					log.warn("no translation found, check Nodetype.vowltype.length and LanguageInfoPanelEN.VOWL_NODE_TYPE_TRANSLATION");
					return vowlType;
				}

			}
		}
		return null;
	}

	/**
	 * converts the VOWL Type from the technical definition from Nodetype.vowltype
	 * to a human better readable from from LanguageInfoPanelEN.VOWL_NODE_TYPE_TRANSLATION
	 *
	 * @param characteristic String
	 * @return translate String
	 */
	private ArrayList<String> translateCharacteristic(String characteristic) {
		ArrayList<String> result = new ArrayList<String>();
		if (characteristic == null) {
			return result;
		}
		for (int i = 0; i < EdgesType.characteristic.length; i++) {
			if (characteristic.contains(EdgesType.characteristic[i])) {
				if (EdgesType.characteristic.length == LanguageInfoPanelEN.CHARACTERISTIC_TRANSLATION.length) {
					result.add(LanguageInfoPanelEN.CHARACTERISTIC_TRANSLATION[i]);
				} else {
					log.warn("no translation found, check EdgesType.characteristic and LanguageInfoPanelEN.characteristic");
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * converts the VOWL Type from the technical definition from PropertyType.type.lengt
	 * to a human better readable from from LanguageInfoPanelEN.VOWL_EDGE_TRANSLATION
	 *
	 * @param vowlType String
	 * @return translate String
	 */
	private String translateVOWLEdgeType(String vowlType) {
		for (int i = 0; i < PropertyType.type.length; i++) {
			if (PropertyType.type[i].equals(vowlType)) {
				return LanguageInfoPanelEN.VOWL_EDGE_TRANSLATION[i];
			}
		}
		return null;
	}
}
