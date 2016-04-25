package org.geogebra.desktop.gui.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.main.AppD;


public class InputDialogDilateD extends InputDialogD {

	GeoPointND[] points;
	GeoElement[] selGeos;

	private Kernel kernel;

	private EuclidianController ec;

	public InputDialogDilateD(AppD app, String title, InputHandler handler,
			GeoPointND[] points, GeoElement[] selGeos, Kernel kernel,
			EuclidianController ec) {
		super(app.getFrame(), false, app.getLocalization());

		this.app = app;
		inputHandler = handler;

		this.points = points;
		this.selGeos = selGeos;
		this.kernel = kernel;

		this.ec = ec;

		createGUI(title, loc.getMenu("Dilate.Factor"), false, DEFAULT_COLUMNS,
				1, true, false, false, false, DialogType.GeoGebraEditor);
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(inputPanel, BorderLayout.CENTER);
		wrappedDialog.getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerOnScreen();
	}

	/**
	 * Handles button clicks for dialog.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
				processInput();
			} else if (source == btApply) {
				processInput();
			} else if (source == btCancel) {
				setVisibleForTools(false);
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			setVisibleForTools(false);
		}
	}

	private void processInput() {

		// avoid labeling of num
		final Construction cons = kernel.getConstruction();
		final boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		inputHandler.processInput(inputPanel.getText(),
				new AsyncOperation<Boolean>() {

					@Override
					public void callback(Boolean ok) {
						cons.setSuppressLabelCreation(oldVal);
						if (ok) {
							DialogManager
									.doDilate(kernel,
											((NumberInputHandler) inputHandler)
													.getNum(),
											points, selGeos, ec);
						}
						setVisibleForTools(!ok);

					}
				});







	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!wrappedDialog.isModal()) {
			app.setCurrentSelectionListener(null);
		}
		((GuiManagerD) app.getGuiManager()).setCurrentTextfield(this, true);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleDialogVisibilityChange(boolean isVisible) {

	}

}