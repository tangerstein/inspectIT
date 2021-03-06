package rocks.inspectit.ui.rcp.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import rocks.inspectit.ui.rcp.InspectIT;
import rocks.inspectit.ui.rcp.provider.ICmrRepositoryProvider;
import rocks.inspectit.ui.rcp.repository.CmrRepositoryDefinition;

/**
 * handler for removing CMR repository.
 * 
 * @author Ivan Senic
 * 
 */
public class RemoveCmrRepositoryHandler extends AbstractHandler implements IHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof StructuredSelection) {
			Object selectedObject = ((StructuredSelection) selection).getFirstElement();
			if (selectedObject instanceof ICmrRepositoryProvider) {
				CmrRepositoryDefinition cmrRepositoryDefinition = ((ICmrRepositoryProvider) selectedObject).getCmrRepositoryDefinition();
				if (null != cmrRepositoryDefinition) {
					boolean isSure = MessageDialog.openConfirm(
							null,
							"Remove Central Management Repository (CMR)",
							"Are you sure that you want to remove the repository " + cmrRepositoryDefinition.getName() + " (" + cmrRepositoryDefinition.getIp() + ":"
									+ cmrRepositoryDefinition.getPort() + ")?");

					if (isSure) {
						InspectIT.getDefault().getCmrRepositoryManager().removeCmrRepositoryDefinition(cmrRepositoryDefinition);
					}
				}
			}
		}
		return null;
	}

}
