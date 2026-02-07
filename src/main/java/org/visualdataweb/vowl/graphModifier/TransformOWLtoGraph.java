package org.visualdataweb.vowl.graphModifier;

import org.visualdataweb.vowl.languages.LanguageGraphEN;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.*;
//import org.semanticweb.owlapi.search.Searcher;
import org.semanticweb.owlapi.search.EntitySearcher;
import prefuse.data.Graph;
import org.visualdataweb.vowl.storage.GraphStorage;
import org.visualdataweb.vowl.types.EdgesType;
import org.visualdataweb.vowl.types.OWLTypes;
import org.visualdataweb.vowl.types.PropertyType;

import java.util.Set;
import java.util.Collection;

/**
 * This class transforms the OWL ontology to a graph.
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class TransformOWLtoGraph {
	private static final Logger logger = Logger.getLogger(TransformOWLtoGraph.class);
	private static final String GENERIC_LITERAL_URI = "http://www.w3.org/2000/01/rdf-schema#Literal";
	private static final String OWL_THING_CLASS_URI = "http://www.w3.org/2002/07/owl#Thing";

	/**
	 * Constructor
	 */
	public TransformOWLtoGraph() {
		// doesn't do anything
	}

	/**
	 * Transforms a given OWL Ontology to a graph.
	 *
	 * @param onto the OWL ontology
	 * @param id   the viewManager id
	 */
	public void transformOWLtoGraph(OWLOntology onto, String id) {
		Graph graph = GraphStorage.getGraph(id);

		if (onto == null || graph == null) {
			return;
		}

		GraphDataModifier mod = GraphStorage.getDataModifier(id);
		Set<OWLClass> OWLClassSet = onto.getClassesInSignature();
		transformClasses(OWLClassSet, onto, mod);
		Set<OWLObjectProperty> OWLObjectPropertiesSet = onto.getObjectPropertiesInSignature();
		transformObjectProperty(OWLObjectPropertiesSet, onto, mod, id);
		Set<OWLDataProperty> OWLDataPropertiesSet = onto.getDataPropertiesInSignature();
		transformDataTypeProperty(OWLDataPropertiesSet, onto, mod, id);
		//	logger.info("# direct imports: " + onto.getDirectImports().toString());
		//	logger.info("# imports: " + onto.getImports().toString());
		//	logger.info("# import declaration: " + onto.getImportsDeclarations().toString());
		transformSubClassesDefinedOutsideOtherClass(OWLClassSet, onto, mod);
	}

	/**
	 * Transforms subclasses defined as separate SubClassOf instead as
	 * rdfs:subClassOf definition within a rdfs:Class definition.
	 *
	 * @param OWLClassSet a set of classes
	 * @param onto        the owl ontology
	 * @param mod         the GraphDataModifier
	 */
	private void transformSubClassesDefinedOutsideOtherClass(Set<OWLClass> OWLClassSet, OWLOntology onto,
	                                                         GraphDataModifier mod) {
		for (OWLClass owlClass : OWLClassSet) {
			Set<OWLSubClassOfAxiom> SetSubClass = onto.getSubClassAxiomsForSubClass(owlClass);
			for (OWLSubClassOfAxiom subClass : SetSubClass) {
				if (!subClass.getSubClass().isAnonymous() && !subClass.getSuperClass().isAnonymous()) {
					String iriSubClass = subClass.getSubClass().asOWLClass().getIRI().toString();
					String iriSuperClass = subClass.getSuperClass().asOWLClass().getIRI().toString();
					if (!OWL_THING_CLASS_URI.equals(iriSuperClass)) {
						TransformOWLtoGraphUtilities tgu = new TransformOWLtoGraphUtilities();
						tgu.helperCheckSubClass(iriSubClass, iriSuperClass, mod);
					}
				} else {
					String classExpressionType1 = subClass.getSubClass().getClassExpressionType().toString();
					String classExpressionType2 = subClass.getSuperClass().getClassExpressionType().toString();
					//	logger.info("not implemented yet : " + classExpressionType1 + " " + classExpressionType2);
					Set<OWLClass> classesInSignature1 = subClass.getSubClass().getClassesInSignature();
					Set<OWLClass> classesInSignature2 = subClass.getSuperClass().getClassesInSignature();
					for (OWLClass classInSig : classesInSignature1) {
						//		logger.info("class iri " + classInSig.getIRI().toString());
					}
					for (OWLClass classInSig : classesInSignature2) {
						//		logger.info("class iri " + classInSig.getIRI().toString());
					}
				}
			}
		}
	}

	/**
	 * Process datatype properties.
	 *
	 * @param OWLDataPropertiesSet a set of OWLDataProperties
	 * @param onto                 the owl ontology
	 * @param mod                  the GraphDataModifier
	 * @param id                   the viewManager id
	 */
	private void transformDataTypeProperty(Set<OWLDataProperty> OWLDataPropertiesSet, OWLOntology onto,
	                                       GraphDataModifier mod, String id) {

		for (OWLDataProperty owlDataProp : OWLDataPropertiesSet) {

			String rdfsDomain = "";
			String rdfsRange = "";
			String rdfsLabel = "";
			String rdfsComment = "";
			String rdfsIsDefinedBy = "";
			String owlVersionInfo = "";
			String objectPropertyIRI = owlDataProp.getIRI().toString();

			Collection<OWLAnnotation> owlPropAnoSet = EntitySearcher.getAnnotations(owlDataProp,onto);
			for (OWLAnnotation owlPropAno : owlPropAnoSet) {
				// extract data from owl:ObjectProperty
				String checkFor = owlPropAno.getProperty().toString();
				String value = owlPropAno.getValue().toString();
				TransformOWLtoGraphUtilities tgu = new TransformOWLtoGraphUtilities();
				rdfsLabel = tgu.helperExtractValueIfCondition(checkFor, value, OWLTypes.RDFS_LABEL, rdfsLabel);
				rdfsComment = tgu.helperExtractValueIfCondition(checkFor, value, OWLTypes.RDFS_COMMENT, rdfsComment);
				rdfsIsDefinedBy = tgu.helperExtractValueIfCondition(checkFor, value, OWLTypes.RDFS_DEFINED_BY,
						rdfsIsDefinedBy);
				owlVersionInfo = tgu.helperExtractValueIfCondition(checkFor, value, OWLTypes.OWL_VERSIONINFO,
						owlVersionInfo);
			}

			// get the domain of the property
			for (OWLClassExpression domain : EntitySearcher.getDomains(owlDataProp,onto)) {
				if (!domain.isAnonymous()) {
					rdfsDomain = domain.asOWLClass().getIRI().toString();
				} else {
					String classExpressionType = domain.getClassExpressionType().toString();
					//	logger.info("not implemented yet : " + classExpressionType);
					Set<OWLClass> classesInSignature = domain.getClassesInSignature();
					for (OWLClass classInSig : classesInSignature) {
						//	logger.info("class iri " + classInSig.getIRI().toString());
					}
				}
			}
			// get the range of the property
			for (OWLDataRange domain : EntitySearcher.getRanges(owlDataProp,onto)) {
				try {
					rdfsRange = domain.asOWLDatatype().getIRI().toString();
				} catch (OWLRuntimeException e) {
					logger.warn("could not transform DataProperty, Exception: " + e);
					logger.warn("This should not happen, because owl data propertys should alway have a domain!");
				}
			}

			TransformOWLtoGraphUtilities tgu = new TransformOWLtoGraphUtilities();

			// if rdfsLabel is empty extract the name from the end of the iri (# or last / as seperator)
			rdfsLabel = tgu.helperExtractLabelNameFromIRIIfLabelHasNoName(rdfsLabel, objectPropertyIRI);
			// add blank space, avoid text runs out of the colored background (only an optic fix)
			rdfsLabel = rdfsLabel.concat(" ");

			int sourceNodeID = mod.findNodeElement(rdfsDomain);
			int targetNodeID = GraphStorage.getNewID(); // each has itÄs own literal node
			String rdfsRessourceType = "";
			boolean isGenericLiteral = false;
			rdfsRessourceType = tgu.helperExtractLabelNameFromIRIIfLabelHasNoName(rdfsRessourceType, rdfsRange);
			if (rdfsRessourceType == null || rdfsRessourceType.length() < 1) {
				rdfsRessourceType = LanguageGraphEN.RDFS_LITERAL;
				// if no name is given a generic name should be taken instead of the property name
				// rdfsRessourceType = TransformOWLtoGraphUtilities.helperExtractLabelNameFromIRIIfLabelHasNoName(rdfsRessourceType, objectPropertyIRI);
				isGenericLiteral = true;
			}
			if (rdfsRange.equals(GENERIC_LITERAL_URI)) {
				isGenericLiteral = true;
			}

			IRI ontoIRI = onto.getOntologyID().getOntologyIRI().orNull();
			if (ontoIRI == null && rdfsIsDefinedBy != null) {
				ontoIRI = IRI.create(rdfsIsDefinedBy);
			}

			// add non generic literal node
			mod.addPropertyNode(targetNodeID, rdfsRessourceType, rdfsRange, null, null, null, null, null, isGenericLiteral);

			if (sourceNodeID != -1 && targetNodeID != -1) {
				// connect the literal node with the specified class node
				int edgeID = mod.addProperty(sourceNodeID, targetNodeID, rdfsLabel, PropertyType.DATATYPE_PROPERTY);
				// check the namespace of the class if the class is imported or not		
				boolean isImported = tgu.hasDifferentNamespace(objectPropertyIRI, ontoIRI);
				mod.addPropertyData(edgeID, objectPropertyIRI, rdfsLabel, rdfsComment, owlVersionInfo, rdfsIsDefinedBy,
						rdfsRange, rdfsDomain, null, isImported); // no inverse!
				if (tgu.isFuntionalDataProperty(onto, owlDataProp)) {
					// mod.addEdgeCharacteristic(edgeID, EdgesType.characteristic[0], false);
					mod.addEdgeCharacteristicInNameField(edgeID, EdgesType.characteristic[0]);
				}
			}
			if (sourceNodeID == -1) {
				// no class node specified? -> check if OWLThingClassNode already exists
				sourceNodeID = mod.getFreeOWLClassNode(GraphStorage.getGraph(id));
				if (sourceNodeID == -1) {
					// no OWLThingClassNode? -> create one
					// sourceNodeID = mod.addClassThing(0); better to add with OWLClass Thing URI
					sourceNodeID = GraphStorage.getNewID();
					mod.addClassThingWithDetails(0, sourceNodeID, "Thing", OWL_THING_CLASS_URI, null, null, null);
				}
				// connect literal node to OWLThingClassNode 
				int edgeID = mod.addProperty(sourceNodeID, targetNodeID, rdfsLabel, PropertyType.DATATYPE_PROPERTY);
				boolean isImported = tgu.hasDifferentNamespace(objectPropertyIRI, ontoIRI);
				mod.addPropertyData(edgeID, objectPropertyIRI, rdfsLabel, rdfsComment, owlVersionInfo, rdfsIsDefinedBy,
						rdfsRange, rdfsDomain, null, isImported); // no inverse!
				if (tgu.isFuntionalDataProperty(onto, owlDataProp)) {
					// mod.addEdgeCharacteristic(edgeID, EdgesType.characteristic[0], false);
					mod.addEdgeCharacteristicInNameField(edgeID, EdgesType.characteristic[0]);
				}
			}
		}
	}

	// TODO fix inverse of and subproperty

	/**
	 * Extracts all needed informations from <owl:ObjectProperty...>
	 * (if the ObjectProperty is a inverse property and no range or domain is given,
	 * the information is extracted from the property inverse to the inverse property).
	 *
	 * @param OWLObjectPropertiesSet a set of OWLObjectProperties
	 * @param onto                   an owl ontology
	 * @param mod                    the GraphDataModifier
	 * @param id                     the viewManager id
	 */
	private void transformObjectProperty(Set<OWLObjectProperty> OWLObjectPropertiesSet, OWLOntology onto,
	                                     GraphDataModifier mod, String id) {
		TransformOWLtoGraphUtilities tgu = new TransformOWLtoGraphUtilities();
		for (OWLObjectProperty owlProp : OWLObjectPropertiesSet) {
			Collection<OWLAnnotation> owlPropAnoSet = EntitySearcher.getAnnotations(owlProp,onto);
			String rdfsDomain = "";
			String rdfsRange = "";
			String rdfsInversOf = "";
			String rdfsLabel = "";
			String rdfsComment = "";
			String rdfsIsDefinedBy = "";
			String owlVersionInfo = "";
			String objectPropertyIRI = owlProp.getIRI().toString();
			for (OWLAnnotation owlPropAno : owlPropAnoSet) {
				// extract data from owl:ObjectProperty
				String checkFor = owlPropAno.getProperty().toString();
				String value = owlPropAno.getValue().toString();
				rdfsLabel = tgu.helperExtractValueIfCondition(checkFor, value, OWLTypes.RDFS_LABEL, rdfsLabel);
				rdfsComment = tgu.helperExtractValueIfCondition(checkFor, value, OWLTypes.RDFS_COMMENT, rdfsComment);
				rdfsIsDefinedBy = tgu.helperExtractValueIfCondition(checkFor, value, OWLTypes.RDFS_DEFINED_BY,
						rdfsIsDefinedBy);
				owlVersionInfo = tgu.helperExtractValueIfCondition(checkFor, value, OWLTypes.OWL_VERSIONINFO,
						owlVersionInfo);
			}
			// get the domain of the property
			for (OWLClassExpression domain : EntitySearcher.getDomains(owlProp,onto)) {
				if (!domain.isAnonymous()) {
					rdfsDomain = domain.asOWLClass().getIRI().toString();
				} else {
					String classExpressionType = domain.getClassExpressionType().toString();
					//	logger.info("not implemented yet : " + classExpressionType);
					Set<OWLClass> classesInSignature = domain.getClassesInSignature();
					for (OWLClass classInSig : classesInSignature) {
						//	logger.info("class iri " + classInSig.getIRI().toString());
					}
				}
			}
			// get the range of the property
			for (OWLClassExpression domain : EntitySearcher.getRanges(owlProp,onto)) {
				if (!domain.isAnonymous()) {
					rdfsRange = domain.asOWLClass().getIRI().toString();
				} else {
					String classExpressionType = domain.getClassExpressionType().toString();
					//	logger.info("not implemented yet : " + classExpressionType);
					Set<OWLClass> classesInSignature = domain.getClassesInSignature();
					for (OWLClass classInSig : classesInSignature) {
						//	logger.info("class iri " + classInSig.getIRI().toString());
					}
				}
			}
			// if rdfsLabel is empty extract the name from the end of the iri (# or last / as seperator)
			rdfsLabel = tgu.helperExtractLabelNameFromIRIIfLabelHasNoName(rdfsLabel, objectPropertyIRI);
			// add blank space, avoid text runs out of the colored background (only an optic fix)
			rdfsLabel = rdfsLabel.concat(" ");
			// get the IRI of the object property which is inverse to 'this' object property
			Collection<OWLObjectPropertyExpression> OWLObjectPropertyExpressionSet = EntitySearcher.getInverses(owlProp,onto);
			for (OWLObjectPropertyExpression owlOPE : OWLObjectPropertyExpressionSet) {

				rdfsInversOf = owlOPE.asOWLObjectProperty().getIRI().toString();

				// maybe we need to extract range or domain data from the inverse object property
				for (OWLObjectProperty owlProp2 : OWLObjectPropertiesSet) {
					if (owlProp2.getIRI().toString().equals(rdfsInversOf)) {
						// logger.info("for - " + rdfsInversOf);
						// logger.info("old Range - " + rdfsRange);
						// logger.info("old Domain - " + rdfsDomain);
						if (rdfsRange == null || rdfsRange.isEmpty()) {
							for (OWLClassExpression domain : EntitySearcher.getRanges(owlProp,onto)) {
								if (!domain.isAnonymous()) {
									rdfsRange = domain.asOWLClass().getIRI().toString();
								} else {
									String classExpressionType = domain.getClassExpressionType().toString();
									//	logger.info("not implemented yet : " + classExpressionType);
									Set<OWLClass> classesInSignature = domain.getClassesInSignature();
									for (OWLClass classInSig : classesInSignature) {
										//	logger.info("class iri " + classInSig.getIRI().toString());
									}
								}
							}
						}
						if (rdfsDomain == null || rdfsDomain.isEmpty()) {
							for (OWLClassExpression domain : EntitySearcher.getDomains(owlProp,onto)) {
								if (!domain.isAnonymous()) {
									rdfsDomain = domain.asOWLClass().getIRI().toString();
								} else {
									String classExpressionType = domain.getClassExpressionType().toString();
									//	logger.info("not implemented yet : " + classExpressionType);
									Set<OWLClass> classesInSignature = domain.getClassesInSignature();
									for (OWLClass classInSig : classesInSignature) {
										//	logger.info("class iri " + classInSig.getIRI().toString());
									}
								}
							}
						}
					}
				}

			}

			int sourceNodeID = mod.findNodeElement(rdfsDomain);
			int targetNodeID = mod.findNodeElement(rdfsRange);

			// if domain or range have the uri of the owl class thing, we have to thread them as OWLClassThing.
			// this is the reason why their IDs are set to -1.
			if (OWL_THING_CLASS_URI.equals(rdfsDomain)) {
				sourceNodeID = -1;
			}
			if (OWL_THING_CLASS_URI.equals(rdfsRange)) {
				targetNodeID = -1;
			}

			IRI ontoIRI = onto.getOntologyID().getOntologyIRI().orNull();
			if (ontoIRI == null && rdfsIsDefinedBy != null) {
				ontoIRI = IRI.create(rdfsIsDefinedBy);
			}


			if (sourceNodeID != -1 && targetNodeID != -1) {
				int edgeID = mod.addProperty(sourceNodeID, targetNodeID, rdfsLabel, PropertyType.OBJECT_PROPERTY);
				boolean isImported = tgu.hasDifferentNamespace(objectPropertyIRI, ontoIRI);
				mod.addPropertyData(edgeID, objectPropertyIRI, rdfsLabel, rdfsComment, owlVersionInfo, rdfsIsDefinedBy,
						rdfsRange, rdfsDomain, rdfsInversOf, isImported);
				// mod.addObjectPropertyWithLabelAsNode(sourceNodeID, targetNodeID, rdfsLabel, objectPropertyIRI,
				// rdfsComment, owlVersionInfo, rdfsIsDefinedBy, rdfsRange, rdfsDomain, null);
				if (tgu.isFuntionalObjectProperty(onto, owlProp)) {
					// mod.addEdgeCharacteristic(edgeID, EdgesType.characteristic[0], false);
					mod.addEdgeCharacteristicInNameField(edgeID, EdgesType.characteristic[0]);
				}
				if (tgu.isSymmetricObjectProperty(onto, owlProp)) {
					mod.addEdgeCharacteristicInNameField(edgeID, EdgesType.characteristic[1]);
				}
				if (tgu.isTransitiveObjectProperty(onto, owlProp)) {
					mod.addEdgeCharacteristicInNameField(edgeID, EdgesType.characteristic[2]);
				}
				if (tgu.isInverseFunctionalObjectProperty(onto, owlProp)) {
					mod.addEdgeCharacteristicInNameField(edgeID, EdgesType.characteristic[3]);
				}
			} else {
				if (sourceNodeID == -1 && targetNodeID == -1) {
					// neither source nor target is given
					sourceNodeID = mod.getFreeOWLClassNode(GraphStorage.getGraph(id));
					if (sourceNodeID == -1) {
						// sourceNodeID = mod.addClassThing(0); better to add with OWLClass Thing URI
						sourceNodeID = GraphStorage.getNewID();
						mod.addClassThingWithDetails(0, sourceNodeID, "Thing", OWL_THING_CLASS_URI, null, null, null);
					}
					targetNodeID = sourceNodeID;
				} else {
					// no sourceID for object property? Take OWLClassThing as source
					if (sourceNodeID == -1) {
						// check if OWLClassThing exists already.
						sourceNodeID = mod.getOWLThingClassConnectedToClass(targetNodeID);
						// no OWLClassThing connected to this class foudn, we have to create one
						if (sourceNodeID == -1) {
							// sourceNodeID = m od.addClassThing(0); better to add with OWLClass Thing URI
							sourceNodeID = GraphStorage.getNewID();
							mod.addClassThingWithDetails(0, sourceNodeID, "Thing", OWL_THING_CLASS_URI, null, null, null);
						}
					}
					// no sourceID for object property? Take OWLClassThing as source
					if (targetNodeID == -1) {
						// check if OWLClassThing exists already. If not create one
						targetNodeID = mod.getOWLThingClassConnectedToClass(sourceNodeID);
						if (targetNodeID == -1) {
							// targetNodeID =  mod.addClassThing(0); better to add with OWLClass Thing URI
							targetNodeID = GraphStorage.getNewID();
							mod.addClassThingWithDetails(0, targetNodeID, "Thing", OWL_THING_CLASS_URI, null, null, null);
						}
					}
				}
				// logger.warn("source id : " +sourceNodeID + "target id: "+ targetNodeID);
				// add object property with OWLClassThing as part of the object property
				int edgeID = mod.addProperty(sourceNodeID, targetNodeID, rdfsLabel, PropertyType.OBJECT_PROPERTY);
				boolean isImported = tgu.hasDifferentNamespace(objectPropertyIRI, ontoIRI);
				mod.addPropertyData(edgeID, objectPropertyIRI, rdfsLabel, rdfsComment, owlVersionInfo, rdfsIsDefinedBy,
						rdfsRange, rdfsDomain, rdfsInversOf, isImported);

				if (tgu.isFuntionalObjectProperty(onto, owlProp)) {
					// mod.addEdgeCharacteristic(edgeID, EdgesType.characteristic[0], false);
					mod.addEdgeCharacteristicInNameField(edgeID, EdgesType.characteristic[0]);
				}
				if (tgu.isSymmetricObjectProperty(onto, owlProp)) {
					mod.addEdgeCharacteristicInNameField(edgeID, EdgesType.characteristic[1]);
				}
				if (tgu.isTransitiveObjectProperty(onto, owlProp)) {
					mod.addEdgeCharacteristicInNameField(edgeID, EdgesType.characteristic[2]);
				}
				if (tgu.isInverseFunctionalObjectProperty(onto, owlProp)) {
					mod.addEdgeCharacteristicInNameField(edgeID, EdgesType.characteristic[3]);
				}
			}
		}
	}

	/**
	 * Extracts all needed informations from <owl:Class...>.
	 *
	 * @param OWLClassSet a set of OWLClasses
	 * @param onto an owl ontology
	 * @param mod the GraphDataModifier
	 */
	private void transformClasses(Set<OWLClass> OWLClassSet, OWLOntology onto, GraphDataModifier mod) {
		TransformOWLtoGraphUtilities tgu = new TransformOWLtoGraphUtilities();
		for (OWLClass owl_class : OWLClassSet) {
			String className = "";
			String classIRI = owl_class.getIRI().toString();
			String classComment = "";
			String definedBy = "";
			String owlVersion = "";
			Boolean isDeprecated = false;
			Collection<OWLAnnotation> owl_class_annotationSET = EntitySearcher.getAnnotations(owl_class,onto);
			for (OWLAnnotation owl_class_annotation : owl_class_annotationSET) {
				// extract data from owl:Class
				OWLAnnotationProperty owl_class_annotation_property = owl_class_annotation.getProperty();
				OWLAnnotationValue owl_class_annotation_value = owl_class_annotation.getValue();
				String checkFor = owl_class_annotation_property.toString();
				String value = owl_class_annotation_value.toString();
				className = tgu.helperExtractValueIfCondition(checkFor, value, OWLTypes.RDFS_LABEL, className);
				classComment = tgu.helperExtractValueIfCondition(checkFor, value, OWLTypes.RDFS_COMMENT, classComment);
				definedBy = tgu.helperExtractValueIfCondition(checkFor, value, OWLTypes.RDFS_DEFINED_BY, definedBy);
				owlVersion = tgu.helperExtractValueIfCondition(checkFor, value, OWLTypes.OWL_VERSIONINFO, owlVersion);
				String dep = "";
				dep = tgu.helperExtractValueIfCondition(checkFor, value, OWLTypes.OWL_DEPRECATED, dep);
				if (dep.equals("true")) {
					isDeprecated = true;
				}
			}

			className = tgu.helperExtractLabelNameFromIRIIfLabelHasNoName(className, classIRI);
			int classID = GraphStorage.getNewID();
			// if the namespace of this class is equals to OWL_THING_CLASS_URI its a OWLThingClass -> we have to add a special class
			if (OWL_THING_CLASS_URI.equals(classIRI) || owl_class.isOWLThing()) {
				mod.addClassThingWithDetails(0, classID, className, classIRI, classComment, definedBy, owlVersion);
			} else {
				// check the namespace of the class if the class is imported or not
				IRI ontoIRI = onto.getOntologyID().getOntologyIRI().orNull();
				if (ontoIRI == null && definedBy != null) {
					ontoIRI = IRI.create(definedBy);
				}
				boolean isImported = tgu.hasDifferentNamespace(classIRI, ontoIRI);

				// check if the extracted Class is a equivalent class
				if (onto.getEquivalentClassesAxioms(owl_class).isEmpty()) {
					mod.addClass(classID, className, classIRI, classComment, definedBy, owlVersion, isDeprecated, isImported);
				} else {
					Boolean equivalentClassHasAlreadyBeenAdded = false;
					for (OWLClassExpression equiClassExpression : EntitySearcher.getEquivalentClasses(owl_class,onto)) {
						if (!equiClassExpression.isAnonymous()) {
							// check if one the equivalent classes has already been added to the graph
							String equiClassIRI = equiClassExpression.asOWLClass().getIRI().toString();
							int equiClassID = mod.findNodeElement(equiClassIRI);
							if (equiClassID != -1) {
								// one of the equivalent classes has already been added
								equivalentClassHasAlreadyBeenAdded = true;
								// add the class
								// yes only this class is added as equivalent classes, other equivalent classes are added within their loop
								mod.addEquivalentClass(equiClassID, classIRI, className, classComment, definedBy, owlVersion, isDeprecated, isImported, true);
								break;
							}
						}
					}
					// no equival1ent class has been added yet -> add it
					if (!equivalentClassHasAlreadyBeenAdded) {
						for (OWLClassExpression equiClassExpression : EntitySearcher.getEquivalentClasses(owl_class,onto)) {
							if (!equiClassExpression.isAnonymous()) {
								// take the first class with ontology namespace as equivalent class master
								String equiClassIRI = equiClassExpression.asOWLClass().getIRI().toString();
								Boolean differentNS = tgu.hasDifferentNamespace(equiClassIRI, ontoIRI);
								if (!differentNS) {
									// add the class
									String className2 = "";
									String classComment2 = "";
									String definedBy2 = "";
									String owlVersion2 = "";
									Collection<OWLAnnotation> owl_class_annotationSET2 = EntitySearcher.getAnnotations(equiClassExpression.asOWLClass(),onto);
									Boolean isDeprecated2 = false;
									for (OWLAnnotation owl_class_annotation : owl_class_annotationSET2) {
										// extract data from owl:Class
										OWLAnnotationProperty owl_class_annotation_property2 = owl_class_annotation.getProperty();
										OWLAnnotationValue owl_class_annotation_value2 = owl_class_annotation.getValue();
										String checkFor2 = owl_class_annotation_property2.toString();
										String value2 = owl_class_annotation_value2.toString();
										className2 = tgu.helperExtractValueIfCondition(checkFor2, value2, OWLTypes.RDFS_LABEL, className2);
										classComment2 = tgu.helperExtractValueIfCondition(checkFor2, value2, OWLTypes.RDFS_COMMENT, classComment2);
										definedBy2 = tgu.helperExtractValueIfCondition(checkFor2, value2, OWLTypes.RDFS_DEFINED_BY, definedBy2);
										owlVersion2 = tgu.helperExtractValueIfCondition(checkFor2, value2, OWLTypes.OWL_VERSIONINFO, owlVersion2);
										isDeprecated2 = owl_class_annotation.isDeprecatedIRIAnnotation();
										String dep = "";
										dep = tgu.helperExtractValueIfCondition(checkFor2, value2, OWLTypes.OWL_DEPRECATED, dep);
										if (dep.equals("true")) {
											isDeprecated2 = true;
										}
									}
									className2 = tgu.helperExtractLabelNameFromIRIIfLabelHasNoName(className2, equiClassIRI);
									int classID2 = GraphStorage.getNewID();
									boolean isImported2 = tgu.hasDifferentNamespace(equiClassIRI, ontoIRI);
									// yes only this class is added as equivalent classes, other equivalent classes are added within their loop
									mod.addClass(classID2, className2, equiClassIRI, classComment2, definedBy2, owlVersion2, isDeprecated2, isImported2);
									if (!equiClassIRI.equals(classIRI)) {
										mod.addEquivalentClass(classID2, classIRI, className, classComment, definedBy, owlVersion, isDeprecated, differentNS, true);
									}
									break;
								}
							} else {
								Boolean differentNS = tgu.hasDifferentNamespace(classIRI, ontoIRI);
								if (!differentNS) {
									String className2 = "";
									String classComment2 = "";
									String definedBy2 = "";
									String owlVersion2 = "";
									Set<OWLEntity> equiClassExpressionSignatureSet = equiClassExpression.getSignature();

									Boolean isDeprecated2 = false;
									for (OWLEntity owl_class_entity : equiClassExpressionSignatureSet) {
										String equiClassIRI = owl_class_entity.getIRI().toString();

										Collection<OWLAnnotation> owl_class_annotationSET2 = EntitySearcher.getAnnotations(owl_class_entity,onto);
										for (OWLAnnotation owl_class_annotation : owl_class_annotationSET2) {
											// extract data from owl:Class
											OWLAnnotationProperty owl_class_annotation_property2 = owl_class_annotation.getProperty();
											OWLAnnotationValue owl_class_annotation_value2 = owl_class_annotation.getValue();
											String checkFor2 = owl_class_annotation_property2.toString();
											String value2 = owl_class_annotation_value2.toString();
											className2 = tgu.helperExtractValueIfCondition(checkFor2, value2, OWLTypes.RDFS_LABEL, className2);
											classComment2 = tgu.helperExtractValueIfCondition(checkFor2, value2, OWLTypes.RDFS_COMMENT, classComment2);
											definedBy2 = tgu.helperExtractValueIfCondition(checkFor2, value2, OWLTypes.RDFS_DEFINED_BY, definedBy2);
											owlVersion2 = tgu.helperExtractValueIfCondition(checkFor2, value2, OWLTypes.OWL_VERSIONINFO, owlVersion2);
											String dep = "";
											dep = tgu.helperExtractValueIfCondition(checkFor2, value2, OWLTypes.OWL_DEPRECATED, dep);
											if (dep.equals("true")) {
												isDeprecated2 = true;
											}
										}
										className2 = tgu.helperExtractLabelNameFromIRIIfLabelHasNoName(className2, equiClassIRI);
										int classID2 = GraphStorage.getNewID();
										boolean isImported2 = tgu.hasDifferentNamespace(equiClassIRI, ontoIRI);
										// yes only this class is added as equivalent classes, other equivalent classes are added within their loop
										mod.addClass(classID2, className2, equiClassIRI, classComment2, definedBy2, owlVersion2, isDeprecated2, isImported2);
										if (!equiClassIRI.equals(classIRI)) {

											mod.addEquivalentClass(classID2, classIRI, className, classComment, definedBy, owlVersion, isDeprecated2, isImported2, true);
										}
										break;
									}
								}
							}
						}
					}
				}

			}
			// check if class has a subclass relationship (yes -> add it to the graph)
			for (OWLClassExpression subClassExpression : EntitySearcher.getSubClasses(owl_class,onto)) {
				if (!subClassExpression.isAnonymous()) {
					String subClassURI = subClassExpression.asOWLClass().getIRI().toString();
					// ignore subclass with the namespace of OWLClass Thing
					if (!OWL_THING_CLASS_URI.equals(subClassURI))
						tgu.helperCheckSubClass(subClassURI, classIRI, mod);
				} else {
					String classExpressionType = subClassExpression.getClassExpressionType().toString();
					//	logger.info("not implemented yet : " + classExpressionType);
					Set<OWLClass> classesInSignature = subClassExpression.getClassesInSignature();
					for (OWLClass classInSig : classesInSignature) {
						//	logger.info("class iri " + classInSig.getIRI().toString());
					}
				}
			}
		}
	}

}
