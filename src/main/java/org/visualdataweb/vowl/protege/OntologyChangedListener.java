package org.visualdataweb.vowl.protege;

import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;

import java.util.List;

/**
 * this OntologyChangedListener implements OWLOntologyChangeListener but
 * OWLOntologyChangeListener is a listener which will be fired after the user 'changes' the active ontology.
 * This could be done by adding, deleting or modifying elements of the ontology
 * BUT NOT:
 * - loading a new ontology
 * - changing the active ontology through protege
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class OntologyChangedListener implements OWLOntologyChangeListener {

	// TODO
	public void ontologiesChanged(List<? extends OWLOntologyChange> arg0) throws OWLException {
		// not implemented yet
	}

}
