package protege;

import org.protege.editor.core.ui.workspace.WorkspaceViewsTab;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLEntityDisplayProvider;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;

/**
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class VOWLWorkspaceTab extends WorkspaceViewsTab {
	
	/*
	 * this class is not used yet and auto generated
	 * it was created to 'test' if a workspace tab could be shown automatically
	 * if this class is deleted plugin.xml has to bee changed (<class value="protege.VOWLWorkspaceTab" />)
	 */
	private static final long serialVersionUID = 5568530822382224792L;

	// private Set<ViewComponentPlugin> viewPlugins = new HashSet<>();
	private OWLEntityDisplayProvider provider = new OWLEntityDisplayProvider() {
		@Override
		public boolean canDisplay(OWLEntity owlEntity) {
			return true;
		}

		@Override
		public JComponent getDisplayComponent() {
			return VOWLWorkspaceTab.this;
		}
	};

	/**
	 * Initialise the workspace.
	 */
	public void initialise() {
		super.initialise();
		getOWLEditorKit().getWorkspace().registerOWLEntityDisplayProvider(provider);
	}

	/**
	 * Disposes the workspace?
	 */
	public void dispose() {
		getOWLEditorKit().getWorkspace().unregisterOWLEntityDisplayProvider(provider);
		super.dispose();
	}

	/**
	 *
	 * @return the owl model manager
	 */
	public OWLModelManager getOWLModelManager() {
		return (OWLModelManager) getWorkspace().getEditorKit().getModelManager();
	}

	/**
	 *
	 * @return the owl editor kit
	 */
	public OWLEditorKit getOWLEditorKit() {
		return (OWLEditorKit) getWorkspace().getEditorKit();
	}

	/**
	 * Private class. Not used yet. Not sure what tasks it have. TODO
	 */
	class NavFinder implements OWLEntityVisitor {

		private String nav;

		public String getNav(OWLEntity owlEntity) {
			nav = null;
			owlEntity.accept(this);
			return nav;
		}

		@Override
		public void visit(OWLClass arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(OWLObjectProperty arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(OWLDataProperty arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(OWLNamedIndividual arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(OWLDatatype arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(OWLAnnotationProperty arg0) {
			// TODO Auto-generated method stub

		}
	}
}