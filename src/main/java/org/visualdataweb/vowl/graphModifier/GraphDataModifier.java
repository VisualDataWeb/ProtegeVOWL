package org.visualdataweb.vowl.graphModifier;

import org.visualdataweb.vowl.types.EdgesType;
import org.visualdataweb.vowl.types.Nodetype;
import org.visualdataweb.vowl.types.EquivalentClassDataStructure;
import org.visualdataweb.vowl.types.ColumnNames;
import org.visualdataweb.vowl.types.FontUsed;
import org.visualdataweb.vowl.types.PropertyType;
import org.visualdataweb.vowl.languages.LanguageGraphEN;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import org.visualdataweb.vowl.rendering.RenderPrefuseGraph;
import org.visualdataweb.vowl.storage.GraphStorage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * The GraphDataModifier contains functions to modify the graph data.
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class GraphDataModifier {

	private static final String EQUIVALENT_CLASS_NAME_SEPERATOR = ", ";
	private static final String ISEXTERNAL = "\n(" + LanguageGraphEN.ISEXTERNAL + ")";
	private static final String DEPRECATED = "\n(" + LanguageGraphEN.DEPRECATED + ")";
	private static final Logger logger = Logger.getLogger(GraphDataModifier.class);
	private static int MIN_CLASS_SIZE = 80;
	private static int CLASS_INSTANCES_STEPS = 1;
	private static int MIN_TEXT_SIZE = 11;
	private static int MIN_EDGE_LENGTH_CLASSES = 200;
	private static int MIN_EDGE_LENGTH_PROPERTYS = 120;
	private static int MAX_CLASS_NAME_LENGT = 10;
	private static int MAX_CLASS_NAME_LINES = 2;
	/* The viewManagerID is an String extracted from the ViewManager of the current OWLWorkspace.
	 * It is used to identify the data which belongs to the current Protégé window.
	 * Within Protégé the user can open different ontologies, which can be shown within the same window or
	 * within different windows. If the are shown within different windows, they are the same protege instance.
	 * So an identifier is needed which is different for each protege instance but ontology independent.  */
	private String viewManagerID;

	/**
	 * Constructor
	 *
	 * @param id the viewManager id
	 */
	public GraphDataModifier(String id) {
		viewManagerID = id;
	}

	/**
	 * Add a new Class with a generated id and a given name.
	 * Change of min_size for all classes possible if a new class is added
	 * owl:Class
	 *
	 * @param name the name of the class
	 */
	public void addClass(String name) {
		addClass(GraphStorage.getNewID(), name);
	}

	/**
	 * Add a new Class with an id and a name.
	 * Change of min_size for all classes possible if a new class is added
	 * The id should be unique, also it is not essential.
	 * owl:Class
	 *
	 * @param id   should be unique
	 * @param name the name of the class
	 */
	public void addClass(int id, String name) {
		addClass(id, name, null, null, null, null, false, false);
	}

	/**
	 * Add a new Class with details,
	 * Change of min_size for all classes possible if a new class is added,
	 * The id should be unique, also it is not essential.
	 *
	 * @param id           the id of this class
	 * @param name         the name of this class
	 * @param uri          used as IRI of this class
	 * @param comment      the commentary of this cl
	 * @param definiedBy   used as IRI
	 * @param isDepreceted true if the class is a deprecate class
	 * @param isExternal   true if the class is imported
	 */
	public void addClass(int id, String name, String uri, String comment, String definiedBy,
	                     String owlVersion, boolean isDepreceted, boolean isExternal) {
		int class_size = MIN_CLASS_SIZE;
		String shortName = helperGetShortName(name, MAX_CLASS_NAME_LENGT);
		// now add the new Class
		Node n = GraphStorage.getGraph(viewManagerID).addNode();
		n.set(ColumnNames.CLASS_INSTANCE_COUNT, 0);
		n.set(ColumnNames.ID, id);
		n.set(ColumnNames.FULL_NAME, name);
		n.set(ColumnNames.NODE_FORM, Nodetype.nodetype[0]); // type circle
		n.set(ColumnNames.NODE_HEIGHT, class_size);
		n.set(ColumnNames.NODE_WIDTH, class_size);
		n.set(ColumnNames.TEXT_SIZE, MIN_TEXT_SIZE);
		n.set(ColumnNames.RDFS_COMMENT, comment);
		n.set(ColumnNames.RDFS_URI, uri);
		n.set(ColumnNames.RDFS_DEFINED_BY, definiedBy);
		n.set(ColumnNames.OWL_VERSION_INFO, owlVersion);
		// vowl type none
		n.set(ColumnNames.NODE_VOWL_TYPE, Nodetype.vowltype[0]);
		// rdfs:class #c0b7eb
		if (isDepreceted && !isExternal) {
			n.set(ColumnNames.NAME_DATA, getEnoughLineBreaksForSecondLine(shortName) + DEPRECATED);
			// color #cccccc for deprecate classes
			n.set(ColumnNames.COLOR_RED, 204);
			n.set(ColumnNames.COLOR_GREEN, 204);
			n.set(ColumnNames.COLOR_BLUE, 204);
			// black text color
			n.set(ColumnNames.TEXT_COLOR_RED, 0);
			n.set(ColumnNames.TEXT_COLOR_GREEN, 0);
			n.set(ColumnNames.TEXT_COLOR_BLUE, 0);
			n.set(ColumnNames.NODE_VOWL_TYPE, Nodetype.vowltype[2]);
		}
		if (isExternal) {
			n.set(ColumnNames.NAME_DATA, getEnoughLineBreaksForSecondLine(shortName) + ISEXTERNAL);
			// color #3366cc for imported classes
			n.set(ColumnNames.COLOR_RED, 51);
			n.set(ColumnNames.COLOR_GREEN, 102);
			n.set(ColumnNames.COLOR_BLUE, 204);
			// imported classes have a white text color
			n.set(ColumnNames.TEXT_COLOR_RED, 255);
			n.set(ColumnNames.TEXT_COLOR_GREEN, 255);
			n.set(ColumnNames.TEXT_COLOR_BLUE, 255);
			n.set(ColumnNames.NODE_VOWL_TYPE, Nodetype.vowltype[3]);
		} 
		/* if (isDepreceted && isImported) {
			shortName = shortName + "\n(deprecated)\n(imported)";
			// color midnightblue #191970
			n.set(ColumnNames.COLOR_RED, 25);
			n.set(ColumnNames.COLOR_GREEN, 25);
			n.set(ColumnNames.COLOR_BLUE, 112);
			// imported classes have a white text color
			n.set(ColumnNames.TEXT_COLOR_RED, 255);
			n.set(ColumnNames.TEXT_COLOR_GREEN, 255);
			n.set(ColumnNames.TEXT_COLOR_BLUE, 255);
		} */
		if (!isDepreceted && !isExternal) {
			// color #aaccff for classes
			n.set(ColumnNames.COLOR_RED, 170);
			n.set(ColumnNames.COLOR_GREEN, 205);
			n.set(ColumnNames.COLOR_BLUE, 255);
			// black text color
			n.set(ColumnNames.TEXT_COLOR_RED, 0);
			n.set(ColumnNames.TEXT_COLOR_GREEN, 0);
			n.set(ColumnNames.TEXT_COLOR_BLUE, 0);
			// vowl type class
			n.set(ColumnNames.NODE_VOWL_TYPE, Nodetype.vowltype[1]);
		}
		boolean isRDFClass = false; // FIXME yet not detected?
		if (isRDFClass) {
			// #cc99cc for rdfs classes
			n.set(ColumnNames.COLOR_RED, 204);
			n.set(ColumnNames.COLOR_GREEN, 153);
			n.set(ColumnNames.COLOR_BLUE, 204);
			// white text color
			n.set(ColumnNames.TEXT_COLOR_RED, 255);
			n.set(ColumnNames.TEXT_COLOR_GREEN, 255);
			n.set(ColumnNames.TEXT_COLOR_BLUE, 255);
			// vowl type class
			n.set(ColumnNames.NODE_VOWL_TYPE, Nodetype.vowltype[5]);
		}
		n.set(ColumnNames.NAME, shortName);
	}

	/**
	 * Creates a new property node (with the given attributes) and adds it to the graph.
	 *
	 * @param id         id of the created node (property)
	 * @param name       label of the property
	 * @param uri        uri of the property
	 * @param comment    the comment of the property
	 * @param definiedBy the uri witch defines the property
	 * @param owlVersion the owl version
	 * @param range      the target
	 * @param domain     the source
	 */
	public void addPropertyNode(int id, String name, String uri, String comment, String definiedBy,
	                            String owlVersion, String range, String domain, boolean isGeneric) {
		int class_size = MIN_CLASS_SIZE;
		String shortName = helperGetShortName(name, MAX_CLASS_NAME_LENGT);
		// now add the new data type property as node
		Node n = GraphStorage.getGraph(viewManagerID).addNode();
		n.set(ColumnNames.ID, id);
		n.set(ColumnNames.NAME, shortName);
		n.set(ColumnNames.FULL_NAME, name);
		n.set(ColumnNames.NODE_FORM, Nodetype.nodetype[1]); // type box
		n.set(ColumnNames.NODE_HEIGHT, class_size / 3 + 2);
		n.set(ColumnNames.NODE_WIDTH, class_size + 2);
		n.set(ColumnNames.TEXT_SIZE, MIN_TEXT_SIZE);
		n.set(ColumnNames.RDFS_COMMENT, comment);
		n.set(ColumnNames.RDFS_URI, uri);
		n.set(ColumnNames.RDFS_DEFINED_BY, definiedBy);
		n.set(ColumnNames.OWL_VERSION_INFO, owlVersion);
		n.set(ColumnNames.RDFS_DOMAIN, domain);
		n.set(ColumnNames.RDFS_RANGE, range);
		if (isGeneric) {
			n.set(ColumnNames.NODE_VOWL_TYPE, Nodetype.vowltype[7]); // generic VOWL Literal
		} else {
			n.set(ColumnNames.NODE_VOWL_TYPE, Nodetype.vowltype[4]); // VOWL Literal	
		}
		// color #FFCC33 for data type propertys
		n.set(ColumnNames.COLOR_RED, 255);
		n.set(ColumnNames.COLOR_GREEN, 205);
		n.set(ColumnNames.COLOR_BLUE, 51);
		// black text color
		n.set(ColumnNames.TEXT_COLOR_RED, 0);
		n.set(ColumnNames.TEXT_COLOR_GREEN, 0);
		n.set(ColumnNames.TEXT_COLOR_BLUE, 0);

	}

	/**
	 * Adds one instance to a given class [className].
	 * Individuals, enumerations (e.g. owl:oneOf)
	 *
	 * @param className the class name
	 */
	public void addInstanceToClass(String className) {
		addInstanceToClass(className, 1);
	}

	/**
	 * Add [howManyToAdd] instances to a given class [className].
	 * Individuals, enumerations (e.g. owl:oneOf)
	 *
	 * @param className    the class name
	 * @param howManyToAdd the count of instances that will be added
	 */
	public void addInstanceToClass(String className, int howManyToAdd) {
		for (int i = 0; i < GraphStorage.getGraph(viewManagerID).getNodeCount(); i++) {
			Node n = GraphStorage.getGraph(viewManagerID).getNode(i);
			int nameIndex = n.getColumnIndex(ColumnNames.FULL_NAME);
			try {
				String name = (String) n.get(nameIndex);
				if (name.equals(className)) {
					helperIncreaseClassSizeAfterAddingInstances(n, howManyToAdd);
					helperUpdateShortName(n);
				}
			} catch (NullPointerException npe) {
				// no class name? -> nothing to do
				logger.warn("possible mistake found: node without full name");
			}
		}
	}

	/**
	 * Adds [howManyToAdd] instances to a given class [classID].
	 * Individuals, enumerations (e.g. owl:oneOf)
	 *
	 * @param id           the id of the class
	 * @param howManyToAdd the count of instances
	 */
	public void addInstanceToClass(int id, int howManyToAdd) {
		for (int i = 0; i < GraphStorage.getGraph(viewManagerID).getNodeCount(); i++) {
			Node n = GraphStorage.getGraph(viewManagerID).getNode(i);
			int idIndex = n.getColumnIndex(ColumnNames.ID);
			try {
				int idOfNode = (Integer) n.get(idIndex);
				if (idOfNode == id) {
					// node(class) found
					helperIncreaseClassSizeAfterAddingInstances(n, howManyToAdd);
					helperUpdateShortName(n);
				}
			} catch (NullPointerException npe) {
				// no class name? -> nothing to do
				logger.warn("possible mistake found: node without an id");
			}
		}
	}

	/**
	 * Removes one instance from a given class [classID].
	 * Individuals, enumerations (e.g. owl:oneOf)
	 *
	 * @param id the id of the class
	 */
	public void removeInstanceFromClass(int id) {
		removeInstanceFromClass(id, 1);
	}

	/**
	 * Removes one instance from a given class [className].
	 * Individuals, enumerations (e.g. owl:oneOf)
	 *
	 * @param className the class name
	 */
	public void removeInstanceFromClass(String className) {
		removeInstanceFromClass(className, 1);
	}

	/**
	 * Removes [howManyToRemove] instances from a given class [classID].
	 * Individuals, enumerations (e.g. owl:oneOf)
	 *
	 * @param id              the id of the class
	 * @param howManyToRemove the cound if instances that will be removed
	 */
	public void removeInstanceFromClass(int id, int howManyToRemove) {
		for (int i = 0; i < GraphStorage.getGraph(viewManagerID).getNodeCount(); i++) {
			Node n = GraphStorage.getGraph(viewManagerID).getNode(i);
			int idIndex = n.getColumnIndex(ColumnNames.ID);
			try {
				int idOfNode = (Integer) n.get(idIndex);
				if (idOfNode == id) {
					// node(class) found
					helperDecreaseClassSizeAfterRemovingInstances(n, howManyToRemove);
					helperUpdateShortName(n);
				}
			} catch (NullPointerException npe) {
				// no class name? -> nothing to do
				logger.warn("possible mistake found: node without an id");
			}
		}
	}

	/**
	 * Removes [howManytoRemove] instances from a given class [className].
	 * Individuals, enumerations (e.g. owl:oneOf)
	 *
	 * @param className the name of the class
	 */
	public void removeInstanceFromClass(String className, int howManytoRemove) {
		for (int i = 0; i < GraphStorage.getGraph(viewManagerID).getNodeCount(); i++) {
			Node n = GraphStorage.getGraph(viewManagerID).getNode(i);
			int nameIndex = n.getColumnIndex(ColumnNames.FULL_NAME);
			try {
				String name = (String) n.get(nameIndex);
				if (name.equals(className)) {
					helperDecreaseClassSizeAfterRemovingInstances(n, howManytoRemove);
					helperUpdateShortName(n);
				}
			} catch (NullPointerException npe) {
				// no class name? -> nothing to do
				logger.warn("possible mistake found: node without full name");
			}
		}
	}

	/**
	 * Adds one instance to a class [classID].
	 * Individuals, enumerations (e.g. owl:oneOf)
	 *
	 * @param id the id of the class
	 */
	public void addInstanceToClass(int id) {
		addInstanceToClass(id, 1);
	}

	/**
	 * Adds the special Class "Thing", owl:Thing.
	 * <p/>
	 * NOTE. Yes you could call this function with a name unlike "Thing" or a different URI as
	 * {@link TransformOWLtoGraph#OWL_THING_CLASS_URI} because a user can define an "own" class with the namespace of
	 * OWLThing. In this case the class should be shown as a OWLThing class.
	 * There are also several cases where user doesn't define the target or the source of a property. In this case the
	 * target / source of this property should be an OWLThing class. It should be possible for the GraphDataModifier to
	 * add an OWLClass with different or without an OWLClassThing Namespace (this may not be through for
	 * TransformOWLToGraph).
	 *
	 * @param instances  the count of instances
	 * @param classID    the id of this class
	 * @param name       the name of this class
	 * @param uri        an uri used as IRI of this class
	 * @param comment    the comment of this class
	 * @param definiedBy used as IRI
	 */
	public void addClassThingWithDetails(int instances, int classID, String name, String uri, String comment, String definiedBy,
	                                     String owlVersion) {
		addClass(classID, name, uri, comment, definiedBy, owlVersion, false, false);
		if (instances != 0) {
			addInstanceToClass(classID, instances);
		}
		// change the parts which are special for  OWLClass Thing class types
		Node n = findNode(classID);
		// color 0080FF for classes
		n.set(ColumnNames.COLOR_RED, 255);
		n.set(ColumnNames.COLOR_GREEN, 255);
		n.set(ColumnNames.COLOR_BLUE, 255);
		// black text color
		n.set(ColumnNames.TEXT_COLOR_RED, 0);
		n.set(ColumnNames.TEXT_COLOR_GREEN, 0);
		n.set(ColumnNames.TEXT_COLOR_BLUE, 0);
		// changes the size of the OWLThing Class
		n.set(ColumnNames.NODE_HEIGHT, ((int) (MIN_CLASS_SIZE / 1.25)));
		n.set(ColumnNames.NODE_WIDTH, ((int) (MIN_CLASS_SIZE / 1.25)));
		n.set(ColumnNames.TEXT_SIZE, MIN_TEXT_SIZE);
		n.set(ColumnNames.NODE_VOWL_TYPE, Nodetype.vowltype[6]); // type OWLThing
	}

	/**
	 * Adds the special Class "Thing", owl:Thing.
	 */
	public int addClassThing(int instances) {
		int classId = GraphStorage.getNewID();
		addClassThingWithDetails(0, classId, "Thing", null, null, null, null);
		return classId;
	}

	/**
	 * Adds a deprecated Class, owl:DeprecatedClass.
	 *
	 * @param name String, the name of the deprecated class
	 */
	public void addDeprecatedClass(String name) {
		addClass(GraphStorage.getNewID(), name, null, null, null, null, true, false);
	}

	/**
	 * Searches for an node element with the given classURI.
	 *
	 * @param classURI the uri of a class
	 * @return the id of the element that was found, otherwise -1.
	 */
	public int findNodeElement(String classURI) {
		if (classURI != null && classURI.length() != 0) {
			for (int i = 0; i < GraphStorage.getGraph(viewManagerID).getNodeCount(); i++) {
				Node n = GraphStorage.getGraph(viewManagerID).getNode(i);
				String classURIOfNode = (String) n.get(n.getColumnIndex(ColumnNames.RDFS_URI));
				if (classURIOfNode != null && classURIOfNode.length() != 0) {
					if (classURI.equals(classURIOfNode)) {
						return (Integer) n.get(n.getColumnIndex(ColumnNames.ID));
					}
				}
				Object testObject = n.get(ColumnNames.EQUIVALENT_CLASSES);
				if (testObject != null) {
					@SuppressWarnings("unchecked")
					ArrayList<EquivalentClassDataStructure> iriList = (ArrayList<EquivalentClassDataStructure>) testObject;
					for (EquivalentClassDataStructure ecds : iriList) {
						String equiClassURI = ecds.getClassIRI();
						if (equiClassURI != null && equiClassURI.length() != 0) {
							if (classURI.equals(equiClassURI)) {
								return (Integer) n.get(n.getColumnIndex(ColumnNames.ID));
							}
						}
					}
				}
			}
		}
		return -1;
	}

	/**
	 * Searches for an edge element with the given URI.
	 *
	 * @param edgeURI the uri of the edge
	 * @return the id of the edge that was found, otherwise -1
	 */
	public int findEdge(String edgeURI) {
		if (edgeURI != null && edgeURI.length() != 0) {
			for (int i = 0; i < GraphStorage.getGraph(viewManagerID).getEdgeCount(); i++) {
				Edge e = GraphStorage.getGraph(viewManagerID).getEdge(i);
				String classURIOfEdge = (String) e.get(e.getColumnIndex(ColumnNames.RDFS_URI));
				if (classURIOfEdge != null && classURIOfEdge.length() != 0) {
					if (edgeURI.equals(classURIOfEdge)) {
						return (Integer) e.get(e.getColumnIndex(ColumnNames.ID));
					}
				}
			}
		}
		return -1;
	}

	/**
	 * Searches for an edge element with the given URI.
	 *
	 * @param edgeURI the uri of the edge
	 * @return the edge if the search was successful, otherwise null
	 */
	public Edge getEdge(String edgeURI) {
		if (edgeURI != null && edgeURI.length() != 0) {
			for (int i = 0; i < GraphStorage.getGraph(viewManagerID).getEdgeCount(); i++) {
				Edge e = GraphStorage.getGraph(viewManagerID).getEdge(i);
				String classURIOfEdge = (String) e.get(e.getColumnIndex(ColumnNames.RDFS_URI));
				if (classURIOfEdge != null && classURIOfEdge.length() != 0) {
					if (edgeURI.equals(classURIOfEdge)) {
						return e;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Searches for an edge element with the given ID.
	 *
	 * @param edgeID the id of the edge
	 * @return the edge if the search was successful, otherwise {@code null}
	 */
	public Edge getEdge(int edgeID) {
		for (int i = 0; i < GraphStorage.getGraph(viewManagerID).getEdgeCount(); i++) {
			Edge e = GraphStorage.getGraph(viewManagerID).getEdge(i);
			if (edgeID == (Integer) e.get(ColumnNames.ID)) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Finds a class with a given class name and class type.
	 *
	 * @param className the class name
	 * @param classType the class type
	 * @return classID the id of the found class
	 */
	public int findClass(String className, String classType) {
		for (int i = 0; i < GraphStorage.getGraph(viewManagerID).getNodeCount(); i++) {
			Node n = GraphStorage.getGraph(viewManagerID).getNode(i);
			int nameIndex = n.getColumnIndex(ColumnNames.FULL_NAME);
			int vowlTypeIndex = n.getColumnIndex(ColumnNames.NODE_VOWL_TYPE);
			int idIndex = n.getColumnIndex(ColumnNames.ID);
			try {
				String name = (String) n.get(nameIndex);
				String vowlType = (String) n.get(vowlTypeIndex);
				if (className.equals(name) && classType.equals(vowlType)) {
					return (Integer) n.get(idIndex);
				}
			} catch (NullPointerException npe) {
				// no class name? -> nothing to do
				logger.warn("possible mistake found: node without name or id or vowl type attribute");
				logger.error("findClass SearchCriterias : ClassName: " + className + " ClassType: " + classType);
			}
		}
		return -1;
	}

	/**
	 * Adds a property to two given classes (given by their class name and type).
	 *
	 * @param className1 the name of the first class
	 * @param classType1 the type of the first class
	 * @param className2 the name of the second class
	 * @param classType2 the type of the second class
	 */
	public void addProperty(String className1, String classType1, String className2, String classType2) {
		int classID1 = findClass(className1, classType1);
		int classID2 = findClass(className2, classType2);
		addProperty(classID1, classID2, "Property", PropertyType.PROPERTY);
	}

	/**
	 * Checks whether the class has a subclass property.
	 *
	 * @param classID1 the id of the first class
	 * @param classID2 the id of the second class
	 * @return boolean true, if the class has a SubClassProperty
	 */
	public boolean hasSubClassProperty(int classID1, int classID2) {
		Node n1 = findNode(classID1);
		Node n2 = findNode(classID2);
		if (n1 != null && n2 != null) {
			for (int i = 0; i < GraphStorage.getGraph(viewManagerID).getEdgeCount(); i++) {
				Edge edge = GraphStorage.getGraph(viewManagerID).getEdge(i);
				int sourceNodeID = (Integer) edge.getSourceNode().get(ColumnNames.ID);
				int targetNodeID = (Integer) edge.getTargetNode().get(ColumnNames.ID);
				if (classID1 == sourceNodeID && classID2 == targetNodeID &&
						LanguageGraphEN.IS_SUB_CLASS_OF.equals(edge.get(ColumnNames.NAME))) {
					return true;
				}

			}
		}
		return false;
	}

	/**
	 * Adds more data to a given edge (specified with it's edge ID).
	 *
	 * @param edgeID          the id of the edge
	 * @param label           the label
	 * @param comment         the comment
	 * @param owlVersionInfo  the owl version
	 * @param rdfsIsDefinedBy the IsDefinedBy value
	 * @param rdfsRange       the range
	 * @param rdfsDomain      the domain
	 * @param rdfsInversOf    the inverse of this edge
	 * @param isImported      whether this edge is imported
	 */
	public void addPropertyData(int edgeID, String objectPropertyIRI, String label,
	                            String comment, String owlVersionInfo, String rdfsIsDefinedBy,
	                            String rdfsRange, String rdfsDomain, String rdfsInversOf, Boolean isImported) {
		for (int i = 0; i < GraphStorage.getGraph(viewManagerID).getEdgeCount(); i++) {
			Edge edge = GraphStorage.getGraph(viewManagerID).getEdge(i);
			Object testObject = edge.get(edge.getColumnIndex(ColumnNames.ID));
			if (testObject != null) {
				int id = (Integer) testObject;
				if (id == edgeID) {
					// edge.set(ColumnNames.NAME, label); NO -> the edge exists already ;-)
					edge.set(ColumnNames.NAME, label);
					edge.set(ColumnNames.RDFS_COMMENT, comment);
					edge.set(ColumnNames.RDFS_URI, objectPropertyIRI);
					edge.set(ColumnNames.OWL_VERSION_INFO, owlVersionInfo);
					edge.set(ColumnNames.RDFS_DEFINED_BY, rdfsIsDefinedBy);
					edge.set(ColumnNames.RDFS_DOMAIN, rdfsDomain);
					edge.set(ColumnNames.RDFS_RANGE, rdfsRange);
					edge.set(ColumnNames.RDFS_INVERSE_OF, rdfsInversOf);
					if (isImported) {
						// color #3366cc for imported properties
						edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_RED, 51);
						edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_GREEN, 102);
						edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_BLUE, 204);
						// white text color for imported properties
						edge.set(ColumnNames.TEXT_COLOR_RED, 255);
						edge.set(ColumnNames.TEXT_COLOR_GREEN, 255);
						edge.set(ColumnNames.TEXT_COLOR_BLUE, 255);
					}
				}
			}

		}
	}

	/**
	 * Adds a property to a node. This is a private function only used within this class
	 * the propertyType defines the text background color (vowl 2)
	 *
	 * @param classID1      the id of the first class
	 * @param classID2      the id of the second class
	 * @param propertyLabel the label of the property
	 * @param propertyType  the {@link PropertyType} for type definition
	 */
	public int addProperty(int classID1, int classID2, String propertyLabel, int propertyType) {
		Node n1 = findNode(classID1);
		Node n2 = findNode(classID2);
		int edgeID = GraphStorage.getNewID();
		if (n1 != null && n2 != null) {
			Edge edge = GraphStorage.getGraph(viewManagerID).addEdge(n1, n2);
			edge.set(ColumnNames.ID, edgeID);
			edge.setString(ColumnNames.NAME, propertyLabel);
			edge.setString(ColumnNames.FULL_NAME, propertyLabel);
			edge.set(ColumnNames.EDGE_VOWL_TYPE, PropertyType.type[propertyType]);
			edge.set(ColumnNames.TEXT_SIZE, MIN_TEXT_SIZE);
			edge.set(ColumnNames.TEXT_COLOR_RED, 0);
			edge.set(ColumnNames.TEXT_COLOR_GREEN, 0);
			edge.set(ColumnNames.TEXT_COLOR_BLUE, 0);
			edge.set(ColumnNames.COLOR_RED, 0);
			edge.set(ColumnNames.COLOR_GREEN, 0);
			edge.set(ColumnNames.COLOR_BLUE, 0);
			edge.set(ColumnNames.EDGE_LENGTH, calculateDefaultEdgeLength(n1, n2));
			edge.set(ColumnNames.EDGE_ARROW_TYPE, EdgesType.arrowtype[1]);
			edge.set(ColumnNames.EDGE_LINE_TYPE, EdgesType.linetype[0]);
			switch (propertyType) {
				case (PropertyType.DATATYPE_PROPERTY):
					// color #99cc66 for text background
					edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_RED, 153);
					edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_GREEN, 204);
					edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_BLUE, 102);
					break;
				case (PropertyType.OBJECT_PROPERTY):
					// color #aaccff for text background 
					edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_RED, 170);
					edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_GREEN, 204);
					edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_BLUE, 255);
					break;
				case (PropertyType.PROPERTY):
					edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_RED, 192);
					edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_GREEN, 183);
					edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_BLUE, 235);
					break;
				case (PropertyType.SUBCLASS):
					edge.setString(ColumnNames.NAME, LanguageGraphEN.IS_SUB_CLASS_OF);
					edge.setString(ColumnNames.FULL_NAME, LanguageGraphEN.IS_SUB_CLASS_OF);
					edge.set(ColumnNames.EDGE_VOWL_TYPE, PropertyType.type[3]);
					/* background color: white 
					 * why? avoid the edge bashed through the text */
					edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_RED, 255);
					edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_GREEN, 255);
					edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_BLUE, 255);
					edge.set(ColumnNames.EDGE_LENGTH, calculateDefaultEdgeLength(n1, n2));
					edge.set(ColumnNames.EDGE_ARROW_TYPE, EdgesType.arrowtype[2]);
					edge.set(ColumnNames.EDGE_LINE_TYPE, EdgesType.linetype[1]);
					break;
				default:
					// color : #b8d29a
					edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_RED, 184);
					edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_GREEN, 210);
					edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_BLUE, 154);
			}
		} else {
			logger.warn("ERROR : add property failed, node not found");
		}
		return edgeID;
	}

	/**
	 * Finds a node with the given id.
	 *
	 * @param id the id of the node
	 * @return the found node or null
	 */
	public Node findNode(int id) {
		for (int i = 0; i < GraphStorage.getGraph(viewManagerID).getNodeCount(); i++) {
			Node n = GraphStorage.getGraph(viewManagerID).getNode(i);
			int nodeIDIndex = n.getColumnIndex(ColumnNames.ID);
			try {
				int nodeID = (Integer) n.get(nodeIDIndex);
				if (id == nodeID) {
					return n;
				}
			} catch (NullPointerException npe) {
				logger.warn("possible error found: node without id attribute");
			}
		}
		return null;
	}

	/**
	 * Returns the id of the OWLClass connected to the given node id.
	 * The direction (from OWLClass to node or from node to OWLClass) is independent
	 *
	 * @param classNodeId the id of the class node
	 * @return the id of the connected owl class or -1 if none was found
	 */
	public int getOWLThingClassConnectedToClass(int classNodeId) {
		return getOWLThingClassConnectedToClass(findNode(classNodeId));
	}

	/**
	 * Returns the id of the OWLClass connected to the given node.
	 * Returns -1 if no OWLClass is connected to the given node.
	 * The direction (from OWLClass to node or from node to OWLClass) is independent
	 *
	 * @param classNode the classNode
	 * @return the id of the connected owl class or -1 if none was found
	 */
	public int getOWLThingClassConnectedToClass(Node classNode) {
		int owlThingID = -1;        // -1 means not found
		if (classNode != null) {
			// from node to OWLThing
			Iterator<?> outNeighbors = classNode.outNeighbors();
			while (outNeighbors.hasNext()) {
				Node testNode = (Node) outNeighbors.next();
				if (Nodetype.vowltype[6].equals(testNode.get(ColumnNames.NODE_VOWL_TYPE))) {
					return (Integer) testNode.get(ColumnNames.ID);
				}
			}
			// from OWLThing to node
			Iterator<?> inNeighbors = classNode.inNeighbors();
			while (inNeighbors.hasNext()) {
				Node testNode = (Node) inNeighbors.next();
				if (Nodetype.vowltype[6].equals(testNode.get(ColumnNames.NODE_VOWL_TYPE))) {
					return (Integer) testNode.get(ColumnNames.ID);
				}
			}
		}
		return owlThingID;
	}

	/**
	 * Returns the id of a 'free' OWLClassNode
	 * A 'free' OWLClassNode is an OWLClassNode which has only connections to Literals, GenericLiterals or other OWLClassNodes.
	 * A 'free' OWLClassNode may have symmetric connections (connected to the object himself).
	 * A OWLClassNode having connections to OWLClasses is NOT a 'free' OWLClassNode
	 *
	 * @param graph the graph that will be searched
	 * @return the id of a free owlclass node or -1 if none is found
	 */
	public int getFreeOWLClassNode(Graph graph) {
		for (int i = 0; i < graph.getNodeCount(); i++) {
			Node node1 = graph.getNode(i);
			// check if node is a OWLClass
			if (Nodetype.vowltype[6].equals(node1.get(ColumnNames.NODE_VOWL_TYPE))) {
				// check incoming and outgoing edges
				Iterator<?> inNeighbors = node1.inNeighbors();
				boolean isFreeNode = true;
				while (inNeighbors.hasNext()) {
					Node testNode = (Node) inNeighbors.next();
					String testNodeType = (String) testNode.get(ColumnNames.NODE_VOWL_TYPE);
					if (Nodetype.OWLThing.equals(testNodeType)
						|| Nodetype.Literal.equals(testNodeType)
						|| Nodetype.vowltype[7].equals(testNodeType)
						) {
						// till now no reject criterion was found
					} else {
						isFreeNode = false;
						break;
					}
				}
				Iterator<?> outNeighbors = node1.outNeighbors();
				while (outNeighbors.hasNext()) {
					Node testNode = (Node) outNeighbors.next();
					String testNodeType = (String) testNode.get(ColumnNames.NODE_VOWL_TYPE);
					if (Nodetype.OWLThing.equals(testNodeType)
						|| Nodetype.Literal.equals(testNodeType)
						|| Nodetype.vowltype[7].equals(testNodeType)
						) {
						// till now no reject criterion was found
					} else {
						isFreeNode = false;
						break;
					}
				}
				if (isFreeNode) {
					return (Integer) node1.get(ColumnNames.ID);
				}
			}
		}
		return -1;
	}

	/**
	 * Adds a class IRI and class name to the specified (by it's node id) equivalent class.
	 *
	 * @param nodeID                the id of the node
	 * @param iriOfEquivalentClass  the IRI of the equivalent class
	 * @param nameOfEquivalentClass the name of the equivalent class
	 * @param classComment          a comment
	 * @param definedBy             the defined by value
	 * @param owlVersion            the owl version
	 * @param isDeprecated          whether the class is deprecated
	 * @param isExternal            whether the class is external
	 * @param uniqueNS              add each namespace only once
	 */
	public void addEquivalentClass(int nodeID, String iriOfEquivalentClass, String nameOfEquivalentClass, String classComment, String definedBy, String owlVersion,
	                               Boolean isDeprecated, Boolean isExternal, Boolean uniqueNS) {
		Node n = findNode(nodeID);
		String nName = (String) n.get(ColumnNames.NAME);
		if (nName != null && nName.length() > 2 && !nName.contains("\n")) {
			n.set(ColumnNames.NAME, n.get(ColumnNames.NAME) + "\n");
		}
		Object testObject = n.get(ColumnNames.EQUIVALENT_CLASSES);
		try {
			@SuppressWarnings("unchecked")
			ArrayList<EquivalentClassDataStructure> iriList = (ArrayList<EquivalentClassDataStructure>) testObject;
			if (iriList == null) {
				iriList = new ArrayList<EquivalentClassDataStructure>();
			}
			EquivalentClassDataStructure ecds = new EquivalentClassDataStructure();
			ecds.setClassIRI(iriOfEquivalentClass);
			ecds.setClassName(nameOfEquivalentClass);
			ecds.setDeprecated(isDeprecated);
			ecds.setExternal(isExternal);
			if (uniqueNS) {
				if (!iriList.contains(ecds) && !iriOfEquivalentClass.equals(n.get(ColumnNames.RDFS_URI))) {
					iriList.add(ecds);
				}
			} else {
				iriList.add(ecds);
			}
			n.set(ColumnNames.EQUIVALENT_CLASSES, iriList);
			String extraName = getEnoughLineBreaksForSecondLine(helperGetShortName(nameOfEquivalentClass, (int) (1 / RenderPrefuseGraph.NameExtraDataSize) * MAX_CLASS_NAME_LENGT));
			extraName = extraName + "[";
			for (int i = 0; i < iriList.size(); i++) {
				if (i > 0) {
					extraName = extraName + EQUIVALENT_CLASS_NAME_SEPERATOR + iriList.get(i).getClassName();
				} else {
					extraName = extraName + iriList.get(i).getClassName();
				}
			}
			extraName = helperGetShortName(extraName, (int) (MAX_CLASS_NAME_LENGT * (1 / RenderPrefuseGraph.NameExtraDataSize)));
			extraName = extraName + "]";    // even with a cut, it should have a closing bracket
			n.set(ColumnNames.NAME_DATA, extraName);
			n.set(ColumnNames.NODE_VOWL_TYPE, Nodetype.vowltype[8]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds edge characteristics like (functional) to a given edge.
	 *
	 * @param edgeID         the ID of the egge
	 * @param characteristic a string with the characteristic
	 * @param overwrite      boolean flag, if false the characteristic is added otherwise the new characteristic overwrites the older ones
	 */
	public void addEdgeCharacteristic(int edgeID, String characteristic, Boolean overwrite) {
		String lineBreak = getEnoughLineBreaksForSecondLine(characteristic);
		characteristic = lineBreak + characteristic;
		Edge edge = getEdge(edgeID);
		if (edge != null) {
			if (overwrite) {
				edge.set(ColumnNames.NAME_DATA, characteristic);
			} else {
				String c = (String) edge.get(ColumnNames.NAME_DATA);
				if (c != null) {
					edge.set(ColumnNames.NAME_DATA, characteristic + c);
				} else {
					edge.set(ColumnNames.NAME_DATA, characteristic);
				}
			}
		}
	}

	/**
	 * Adds edge characteristics like (functional) to a given edge.
	 * The edge characteristics are shown within it's name field.
	 *
	 * @param edgeID         the id of the edge
	 * @param characteristic a string with the characteristic
	 */
	public void addEdgeCharacteristicInNameField(int edgeID, String characteristic) {
		Edge edge = getEdge(edgeID);
		if (edge != null) {
			String edgeName = (String) edge.get(ColumnNames.NAME);
			String prevCharacteristic = (String) edge.get(ColumnNames.NAME_DATA);
			if (prevCharacteristic != null) {
				edge.set(ColumnNames.NAME_DATA, prevCharacteristic + "\n" + characteristic);
			} else {
				edge.set(ColumnNames.NAME_DATA, characteristic);
			}
			String result = edgeName + "\n" + characteristic;
			edge.set(ColumnNames.NAME, result);
		}
	}

	/**
	 * This function calculates the edge length depending on the size of both nodes and the wanted minimum edge length.
	 * If the edge is between classes the edge will be longer as between propertys
	 * (within prefuse an edge seems so end before n2 but start at the center of n1)
	 *
	 * @param n1 the first node of the edge
	 * @param n2 the second node of the edge
	 * @return the length of the edge
	 */
	public int calculateDefaultEdgeLength(Node n1, Node n2) {
		int n1_h = (Integer) n1.get(n1.getColumnIndex(ColumnNames.NODE_HEIGHT)) / 2;
		int n1_w = (Integer) n1.get(n1.getColumnIndex(ColumnNames.NODE_WIDTH)) / 2;
		// int n2_h = (Integer) n2.get(n2.getColumnIndex(ColumnNames.NODE_HEIGHT))/2;
		// int n2_w = (Integer) n2.get(n2.getColumnIndex(ColumnNames.NODE_WIDTH))/2;
		int n1_size = (int) Math.sqrt(n1_h * n1_h + n1_w * n1_w);
		// int n2_size = (int) Math.sqrt(n2_h*n2_h + n2_w*n2_h);
		// int edge_length = n1_size + n2_size + MIN_EDGE_LENGTH_CLASSES;
		String node2Type = (String) n2.get(n2.getColumnIndex(ColumnNames.NODE_VOWL_TYPE));
		int edge_length;
		if (node2Type != null && (Nodetype.vowltype[3].equals(node2Type)
								  || Nodetype.vowltype[2].equals(node2Type)
								  || Nodetype.vowltype[1].equals(node2Type))) {
			// edge between classes
			edge_length = n1_size + MIN_EDGE_LENGTH_CLASSES;
		} else {
			// edge between other objects like properties
			edge_length = n1_size + MIN_EDGE_LENGTH_PROPERTYS;
		}
		return edge_length;
	}

	/**
	 * Decreases the visual size of a node after instances have been removed.
	 *
	 * @param n               the node
	 * @param howManyToRemove the count of instances that will be removed
	 */
	private void helperDecreaseClassSizeAfterRemovingInstances(Node n, int howManyToRemove) {
		try {
			int instancesIndex = n.getColumnIndex(ColumnNames.CLASS_INSTANCE_COUNT);
			int instanceCount = (Integer) n.get(instancesIndex);
			int heightIndex = n.getColumnIndex(ColumnNames.NODE_HEIGHT);
			int widhtIndex = n.getColumnIndex(ColumnNames.NODE_WIDTH);
			int height = (Integer) n.get(heightIndex);
			int width = (Integer) n.get(widhtIndex);
			instanceCount = instanceCount - howManyToRemove;
			n.set(ColumnNames.CLASS_INSTANCE_COUNT, instanceCount);
			n.set(ColumnNames.NODE_HEIGHT, height - (int) Math.log(CLASS_INSTANCES_STEPS * howManyToRemove));
			n.set(ColumnNames.NODE_WIDTH, width - (int) Math.log(CLASS_INSTANCES_STEPS * howManyToRemove));
		} catch (NullPointerException npe) {
			logger.error("missing important data, wrong class : " + n.toString());
		}
	}

	/**
	 * Increases the visual size of a node after instances have been removed.
	 *
	 * @param n            the node
	 * @param howManyToAdd the count of instances that will be added
	 */
	private void helperIncreaseClassSizeAfterAddingInstances(Node n, int howManyToAdd) {
		try {
			int instancesIndex = n.getColumnIndex(ColumnNames.CLASS_INSTANCE_COUNT);
			int instanceCount = (Integer) n.get(instancesIndex);
			int heightIndex = n.getColumnIndex(ColumnNames.NODE_HEIGHT);
			int widhtIndex = n.getColumnIndex(ColumnNames.NODE_WIDTH);
			int height = (Integer) n.get(heightIndex);
			int width = (Integer) n.get(widhtIndex);
			instanceCount = instanceCount + howManyToAdd;
			n.set(ColumnNames.CLASS_INSTANCE_COUNT, instanceCount);
			// n.set(ColumnNames.NODE_HEIGHT, height + (int) Math.log(howManyToAdd * CLASS_INSTANCES_STEPS));
			// n.set(ColumnNames.NODE_WIDTH, width + (int) Math.log(howManyToAdd * CLASS_INSTANCES_STEPS));
			n.set(ColumnNames.NODE_HEIGHT, height + (howManyToAdd * CLASS_INSTANCES_STEPS));
			n.set(ColumnNames.NODE_WIDTH, width + (howManyToAdd * CLASS_INSTANCES_STEPS));
		} catch (NullPointerException npe) {
			logger.error("missing important data, wrong class : " + n.toString());
		}
	}

	/**
	 * Updates the shorter name and the additional informations like instance count.
	 *
	 * @param n a node
	 */
	private void helperUpdateShortName(Node n) {
		try {
			int instanceCount = (Integer) n.get(n.getColumnIndex(ColumnNames.CLASS_INSTANCE_COUNT));
			String fullName = (String) n.get(n.getColumnIndex(ColumnNames.FULL_NAME));
			String shortName = helperGetShortName(fullName, MAX_CLASS_NAME_LENGT);
			shortName = "[" + Integer.toString(instanceCount) + "]" + "\n" + shortName;
			String vowlType = (String) n.get(n.getColumnIndex(ColumnNames.NODE_VOWL_TYPE));
			if (vowlType != null) {
				if (vowlType.equals(Nodetype.vowltype[2])) {
					n.set(ColumnNames.NAME_DATA, DEPRECATED);
				}
				if (vowlType.equals(Nodetype.vowltype[3])) {
					n.set(ColumnNames.NAME_DATA, ISEXTERNAL);
				}
				if (vowlType.equals(Nodetype.vowltype[8])) {
					ArrayList<EquivalentClassDataStructure> iriList = new ArrayList<EquivalentClassDataStructure>(); // TODO list is always empty
					String extraName = getEnoughLineBreaksForSecondLine(helperGetShortName(shortName, MAX_CLASS_NAME_LENGT));
					extraName = extraName + "[";
					for (int i = 0; i < iriList.size(); i++) {
						extraName = extraName + iriList.get(i).getClassName();
					}
					extraName = helperGetShortName(extraName, (int) (MAX_CLASS_NAME_LENGT * (1 / RenderPrefuseGraph.NameExtraDataSize)));
					extraName = extraName + "]";        // even when it's cut it should have a closing bracket
					n.set(ColumnNames.NAME_DATA, extraName);
				}
			}
			n.set(ColumnNames.NAME, shortName);
		} catch (NullPointerException npe) {
			logger.error("missing important data within class : " + n.toString());
		}
	}

	/**
	 * Cuts the name if the name is to long. This is done to avoid a longer name than the space within a node.
	 * If the name is longer than maxLength, all empty spaces are replaced.
	 * By \n (max MAX_CLASS_NAME_LINES) and if its still too long all behind maxLength are cut off (and ... is added)
	 *
	 * @param name      the string to be cut of
	 * @param maxLength the max length of a String as Integer
	 * @return the string after the cut is done
	 */
	private String helperGetShortName(String name, int maxLength) {
		String shortName;
		if (name.length() > maxLength) {
			if (!name.contains(" ")) {
				// there hasn't been any white space within the class name
				shortName = name.substring(0, maxLength);
				shortName = shortName + "...";
			} else {
				String[] nameSplit = name.split(" ");

				if (nameSplit.length > MAX_CLASS_NAME_LINES) {
					shortName = nameSplit[0];
					for (int i = 1; i < MAX_CLASS_NAME_LINES; i++) {
						shortName = shortName.concat("\n").concat(nameSplit[i]);
					}
					shortName = shortName.concat("...");
				} else {
					shortName = name.replaceAll(" ", "\n");
				}
			}
		} else {
			shortName = name;
		}
		return shortName;
	}

	/**
	 * Adds an object property on an different way. Between two nodes a third node is placed
	 * and this node contains the label. This was a try to avoid the multiple edge between two nodes
	 * problem within prefuse, but the visualisation looks very bad
	 *
	 * @param sourceClassID     the id of the source class
	 * @param targetClassID     the id of the target class
	 * @param rdfsLabel         the label
	 * @param objectPropertyIRI the object property IRI
	 * @param rdfsComment       the comment
	 * @param owlVersionInfo    the owl version info
	 * @param rdfsIsDefinedBy   the IsDefinedBy value
	 * @param rdfsRange         the range
	 * @param rdfsDomain        the domain
	 * @param rdfsInverseOf     the inverse
	 */
	@Deprecated
	public void addObjectPropertyWithLabelAsNode(int sourceClassID, int targetClassID, String rdfsLabel,
	                                             String objectPropertyIRI, String rdfsComment, String owlVersionInfo, String rdfsIsDefinedBy,
	                                             String rdfsRange, String rdfsDomain, String rdfsInverseOf) {
		Node sourceNode = findNode(sourceClassID);
		Node targetNode = findNode(targetClassID);
		Node viaNode = GraphStorage.getGraph(viewManagerID).addNode();
		// add a viaNode containing all information of the edge
		{
			int viaNodeID = GraphStorage.getNewID();
			viaNode.set(ColumnNames.ID, viaNodeID);
			viaNode.set(ColumnNames.NAME, helperGetShortName(rdfsLabel, MAX_CLASS_NAME_LENGT));
			viaNode.set(ColumnNames.FULL_NAME, rdfsLabel);
			viaNode.set(ColumnNames.NODE_FORM, Nodetype.nodetype[1]); // type box
			viaNode.set(ColumnNames.NODE_HEIGHT, 8);
			viaNode.set(ColumnNames.NODE_WIDTH, 8);
			viaNode.set(ColumnNames.TEXT_SIZE, MIN_TEXT_SIZE);
			viaNode.set(ColumnNames.RDFS_COMMENT, rdfsComment);
			viaNode.set(ColumnNames.RDFS_URI, objectPropertyIRI);
			viaNode.set(ColumnNames.RDFS_DEFINED_BY, rdfsIsDefinedBy);
			viaNode.set(ColumnNames.OWL_VERSION_INFO, owlVersionInfo);
			// vowl type none
			viaNode.set(ColumnNames.NODE_VOWL_TYPE, Nodetype.vowltype[4]); // type DataTypeProperty
			// color white for data type propertys
			viaNode.set(ColumnNames.COLOR_RED, 255);
			viaNode.set(ColumnNames.COLOR_GREEN, 255);
			viaNode.set(ColumnNames.COLOR_BLUE, 255);
			// black text color
			viaNode.set(ColumnNames.TEXT_COLOR_RED, 0);
			viaNode.set(ColumnNames.TEXT_COLOR_GREEN, 0);
			viaNode.set(ColumnNames.TEXT_COLOR_BLUE, 0);
		}
		// add edges from sourceNode to viaNode to targetNode
		Edge sourceToVia = GraphStorage.getGraph(viewManagerID).addEdge(sourceNode, viaNode);
		sourceToVia.set(ColumnNames.ID, GraphStorage.getNewID());
		sourceToVia.setString(ColumnNames.NAME, null);            // this edge has no label
		sourceToVia.setString(ColumnNames.FULL_NAME, null);        // this edge has no label
		sourceToVia.set(ColumnNames.TEXT_SIZE, MIN_TEXT_SIZE);
		sourceToVia.set(ColumnNames.TEXT_COLOR_RED, 0);
		sourceToVia.set(ColumnNames.TEXT_COLOR_GREEN, 0);
		sourceToVia.set(ColumnNames.TEXT_COLOR_BLUE, 0);
		sourceToVia.set(ColumnNames.COLOR_RED, 0);
		sourceToVia.set(ColumnNames.COLOR_GREEN, 0);
		sourceToVia.set(ColumnNames.COLOR_BLUE, 0);
		sourceToVia.set(ColumnNames.EDGE_LENGTH, calculateDefaultEdgeLength(sourceNode, viaNode));
		sourceToVia.set(ColumnNames.EDGE_ARROW_TYPE, EdgesType.arrowtype[1]);
		sourceToVia.set(ColumnNames.EDGE_LINE_TYPE, EdgesType.linetype[0]);
		Edge viaToTarget = GraphStorage.getGraph(viewManagerID).addEdge(viaNode, targetNode);
		viaToTarget.set(ColumnNames.ID, GraphStorage.getNewID());
		viaToTarget.setString(ColumnNames.NAME, null);            // this edge has no label
		viaToTarget.setString(ColumnNames.FULL_NAME, null);        // this edge has no label
		viaToTarget.set(ColumnNames.TEXT_SIZE, MIN_TEXT_SIZE);
		viaToTarget.set(ColumnNames.TEXT_COLOR_RED, 0);
		viaToTarget.set(ColumnNames.TEXT_COLOR_GREEN, 0);
		viaToTarget.set(ColumnNames.TEXT_COLOR_BLUE, 0);
		viaToTarget.set(ColumnNames.COLOR_RED, 0);
		viaToTarget.set(ColumnNames.COLOR_GREEN, 0);
		viaToTarget.set(ColumnNames.COLOR_BLUE, 0);
		viaToTarget.set(ColumnNames.EDGE_LENGTH, calculateDefaultEdgeLength(sourceNode, viaNode));
		viaToTarget.set(ColumnNames.EDGE_ARROW_TYPE, EdgesType.arrowtype[1]);
		viaToTarget.set(ColumnNames.EDGE_LINE_TYPE, EdgesType.linetype[0]);
	}

	/**
	 * Returns enough line breaks (\n) to display a second string below the first string.
	 * (an example: used for the second label render which uses a smaller font size to display
	 * this line below the first label renderer)
	 *
	 * @param shortName the string used as first line
	 * @return a string with enough line breaks (\n) to show a second string below the first string
	 */
	public String getEnoughLineBreaksForSecondLine(String shortName) {
		Canvas c = new Canvas();
		FontMetrics fm = c.getFontMetrics(FontUsed.getFont());
		double hight = fm.getStringBounds(shortName, c.getGraphics()).getHeight();
		int highCounter = 1;
		while (shortName.contains("\n")) {
			shortName = shortName.replaceFirst("\n", "");
			highCounter++;
		}
		hight = hight * highCounter;
		String xDistance = "";
		double xhight = fm.getStringBounds(xDistance, c.getGraphics()).getHeight();
		double xhightOneLine = xhight;
		while (hight >= xhight) {
			xDistance = xDistance + "\n";
			xhight = xhight + xhightOneLine;
		}
		xDistance = xDistance + "\n";
		return xDistance;
	}

}