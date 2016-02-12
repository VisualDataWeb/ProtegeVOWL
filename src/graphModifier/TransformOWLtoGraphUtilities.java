package graphModifier;

import languages.LanguageGraphEN;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.*;
import storage.GraphStorage;
import types.PropertyType;

/**
 * This class utilities for processing a graph.
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class TransformOWLtoGraphUtilities {

	private static final Logger logger = Logger.getLogger(TransformOWLtoGraphUtilities.class);

	/**
	 * This helper function removes the first and the last " from a given String.
	 *
	 * @param s string
	 * @return filtered string
	 */
	public String helperFilterQuotes(String s) {
		if (s.contains("\"")) {
			s = s.substring(s.indexOf("\"") + 1, s.lastIndexOf("\""));
		}
		return s;
	}

	/**
	 * This helper checks if checkFor is equal to condition and trims the values quotation marks if it was fulfilled.
	 *
	 * @param checkFor    value that will be compared with
	 * @param value       the value that will be trimmed if the condition is fulfilled
	 * @param condition   condition that will be compared with the passed {@code checkFor} value
	 * @param returnValue the default return value
	 * @return the trimmed value if the condition applied, otherwise the default return value
	 */
	public String helperExtractValueIfCondition(String checkFor, String value, String condition, String returnValue) {
		if (condition.equals(checkFor)) {
			returnValue = helperFilterQuotes(value);
		}
		return returnValue;
	}

	/**
	 * Adds a sub class relationship between sub class and parent class.
	 *
	 * @param subClassURI    the subclass URI
	 * @param parentClassIRI the parent class URI
	 * @param mod            the graph data modifier
	 */
	public void helperCheckSubClass(String subClassURI, String parentClassIRI, GraphDataModifier mod) {
		if (subClassURI == null || subClassURI.length() == 0
				|| parentClassIRI == null || parentClassIRI.length() == 0) {

			return;
		}

		int idSubClass;
		String OWL_THING_CLASS_URI = "http://www.w3.org/2002/07/owl#Thing";
		if (OWL_THING_CLASS_URI.equals(subClassURI)) {
			idSubClass = mod.getOWLThingClassConnectedToClass(mod.findNodeElement(parentClassIRI));
			if (idSubClass == -1) {
				idSubClass = GraphStorage.getNewID();
				mod.addClassThingWithDetails(0, idSubClass, "Thing", OWL_THING_CLASS_URI, null, null, null);
			}
		} else {
			idSubClass = mod.findNodeElement(subClassURI);
		}

		int idParentClass = mod.findNodeElement(parentClassIRI);
		if (idSubClass != -1 && idParentClass != -1) {
			if (!mod.hasSubClassProperty(idSubClass, idParentClass)) {
				mod.addProperty(idSubClass, idParentClass, LanguageGraphEN.IS_SUB_CLASS_OF, PropertyType.SUBCLASS);
			}
		}
	}

	/**
	 * Extract the rdfs:label from the IRI if it isn't given.
	 *
	 * @param name name of the label
	 * @param iri  IRI of the label
	 * @return rdfsLabel the (maybe extracted) name of the label
	 */
	public String helperExtractLabelNameFromIRIIfLabelHasNoName(String name, String iri) {
		if (name == null || iri == null || name.length() != 0) {
			return name;
		}

		try {
			if (iri.contains("#")) {
				// IRI contains a # -> take name behind #
				name = iri.substring(iri.indexOf("#") + 1);
			} else {
				if (iri.contains("/")) {
					// IRI contains / -> take name behind the last /
					name = iri.substring(iri.lastIndexOf("/") + 1);
				} else {
					name = iri;
					//	logger.info("unable to extract a good name from the given iri - " + iri);
				}
			}
		} catch (Exception e) {
			logger.warn("Exception while trying to extract a better class name. " + e);
		}

		return name;
	}

	/**
	 * Checks if an element has a different namespace than the ontology.
	 * The function will return true if the element's namespace doesn't contain the namespace of the URI.
	 *
	 * @param elementNamespace the namespace of an element as string (URI to string)
	 * @param onto             the whole owl ontology to extract the ontology namespace (URI as string)
	 * @return boolean true, if the namespace is different
	 */
	public boolean hasDifferentNamespace(String elementNamespace, OWLOntology onto) {
		return hasDifferentNamespace(elementNamespace, onto.getOntologyID().getOntologyIRI());
	}

	/**
	 * Checks if an element has a different namespace as an other element.
	 * The function will return true if the element's namespace doesn't contain the namespace of the URI.
	 *
	 * @param elementNamespace  the namespace of an element as string (URI to string)
	 * @param ontologyNamespace the namespace of the ontology as IRI
	 * @return true, if the namespace is different
	 */
	public boolean hasDifferentNamespace(String elementNamespace, IRI ontologyNamespace) {
		if (elementNamespace == null || ontologyNamespace == null) {
			return false;
		}

		return !(elementNamespace.contains(ontologyNamespace));
	}

	/**
	 * Checks whether the given OWLDataProperty is a functional property.
	 *
	 * @param onto        the whole OWLOntology
	 * @param owlDataProp the specific OWLDataProperty
	 * @return true, if the given OWLDataProperty is a functional property
	 */
	public boolean isFuntionalDataProperty(OWLOntology onto, OWLDataProperty owlDataProp) {
		return !onto.getFunctionalDataPropertyAxioms(owlDataProp).isEmpty();
	}

	/**
	 * Checks whether the given OWLObjectProperty is a functional property.
	 *
	 * @param onto          the whole OWLOntology
	 * @param owlObjectProp the specific OWLObjectProperty
	 * @return true, if the given OWLObjectProperty is a functional property
	 */
	public boolean isFuntionalObjectProperty(OWLOntology onto, OWLObjectProperty owlObjectProp) {
		return !onto.getFunctionalObjectPropertyAxioms(owlObjectProp).isEmpty();
	}

	/**
	 * Checks whether the given OWLObjectProperty is a transitive property.
	 *
	 * @param onto          the whole OWLOntology
	 * @param owlObjectProp the specific OWLObjectProperty
	 * @return true, if the given OWLObjectProperty is a transitive property
	 */
	public boolean isTransitiveObjectProperty(OWLOntology onto, OWLObjectProperty owlObjectProp) {
		return !onto.getTransitiveObjectPropertyAxioms(owlObjectProp).isEmpty();
	}

	/**
	 * checks wheaten the given OWLObjectPropertyExpression is a inverse functional object property
	 *
	 * @param onto   the whole OWLOntology
	 * @param owlOPE OWLObjectPropertyExpression
	 * @return boolean            true if the given OWLObjectProperty is a inverse functional object property
	 */
	public boolean isInverseFunctionalObjectProperty(OWLOntology onto, OWLObjectPropertyExpression owlOPE) {
		return !onto.getInverseFunctionalObjectPropertyAxioms(owlOPE).isEmpty();
	}

	/**
	 * checks wheaten the given OWLObjectPropertyExpression is a symmetric object property
	 *
	 * @param onto   the whole OWLOntology
	 * @param owlOPE OWLObjectPropertyExpression
	 * @return boolean            true if the given OWLObjectProperty is a symmetric object property
	 */
	public boolean isSymmetricObjectProperty(OWLOntology onto, OWLObjectPropertyExpression owlOPE) {
		return !onto.getSymmetricObjectPropertyAxioms(owlOPE).isEmpty();
	}
}
