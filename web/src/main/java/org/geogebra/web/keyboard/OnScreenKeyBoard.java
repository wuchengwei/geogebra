package org.geogebra.web.keyboard;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.util.Language;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.util.keyboard.HasKeyboard;
import org.geogebra.web.html5.util.keyboard.VirtualKeyboardW;
import org.geogebra.web.keyboard.KeyboardListener.ArrowType;

import com.google.gwt.core.client.Scheduler;

/**
 * on screen keyboard containing mathematical symbols and formulas
 */
public class OnScreenKeyBoard extends KBBase implements VirtualKeyboardW {

	/**
	 * should not be called; use getInstance instead
	 * 
	 * @param app
	 *            application
	 * @param korean
	 *            if korean layout should be supported
	 */
	public OnScreenKeyBoard(HasKeyboard app, boolean korean) {
		super(true);
		if (korean) {
			addSupportedLocale(Language.Korean, "ko");
		}
		this.app = app;
		this.loc = app.getLocalization(); // TODO
		addStyleName("KeyBoard");
		createKeyBoard();
		initAccentAcuteLetters();
		initAccentGraveLetters();
		initAccentCaronLetters();
		initAccentCircumflexLetters();

		setHasKeyboard(app);

	}



	@Override
	public void onClick(KeyBoardButtonBase btn, PointerEventType type) {
		// TODO
		ToolTipManagerW.hideAllToolTips();
		if (processField == null) {
			return;
		}
		if (btn instanceof KeyBoardButtonFunctionalBase) {
			KeyBoardButtonFunctionalBase button = (KeyBoardButtonFunctionalBase) btn;

			switch (button.getAction()) {
			case SHIFT:
				removeAccents();
				processShift();
				break;
			case BACKSPACE:
				processField.onBackSpace();
				break;
			case ENTER:
				// make sure enter is processed correctly
				processField.onEnter();
				if (processField.resetAfterEnter()) {
					updateKeyBoardListener.keyBoardNeeded(false, null);
				}
				break;
			case ARROW_LEFT:
				processField.onArrow(ArrowType.left);
				break;
			case ARROW_RIGHT:
				processField.onArrow(ArrowType.right);
				break;
			case SWITCH_KEYBOARD:
				String caption = button.getCaption();
				if (caption.equals(GREEK)) {
					setToGreekLetters();
				} else if (caption.equals(NUMBER)) {
					setKeyboardMode(KeyboardMode.NUMBER);
				} else if (caption.equals(TEXT)) {
					if (greekActive) {
						greekActive = false;
						switchABCGreek.setCaption(GREEK);
						updateKeys("lowerCase", this.keyboardLocale);
						setStyleName();
					}
					if (shiftIsDown) {
						processShift();
					}
					if (accentDown) {
						removeAccents();
					}
					setKeyboardMode(KeyboardMode.TEXT);
				} else if (caption.equals(SPECIAL_CHARS)) {
					setKeyboardMode(KeyboardMode.SPECIAL_CHARS);
				} else if (caption.equals(PAGE_ONE_OF_TWO)) {
					showSecondPage();
				} else if (caption.equals(PAGE_TWO_OF_TWO)) {
					showFirstPage();
				}
			}
		} else {

			String text = btn.getFeedback();

			if (isAccent(text)) {
				processAccent(text, btn);
			} else {
				processField.insertString(text);
				if (accentDown) {
					removeAccents();
				}
			}

			if (shiftIsDown && !isAccent(text)) {
				processShift();
			}

			processField.setFocus(true);
		}

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				Scheduler.get()
						.scheduleDeferred(new Scheduler.ScheduledCommand() {
					@Override
					public void execute() {
						processField.scrollCursorIntoView();
					}
				});
			}
		});
	}

	private void showFirstPage() {
		specialCharContainer.clear();
		specialCharContainer.add(firstPageChars);
	}

	private void showSecondPage() {
		specialCharContainer.clear();
		specialCharContainer.add(secondPageChars);
	}



	/**
	 * @param mode
	 *            the keyboard mode
	 */
	@Override
	public void setKeyboardMode(final KeyboardMode mode) {
		this.mode = mode;
		if (mode == KeyboardMode.NUMBER) {
			// TODO required for AutoCompleteTextFieldW
			// processField.setKeyBoardModeText(false);
			contentNumber.setVisible(true);
			contentLetters.setVisible(false);
			contentSpecialChars.setVisible(false);
		} else if (mode == KeyboardMode.TEXT) {
			greekActive = false;
			contentNumber.setVisible(false);
			contentLetters.setVisible(true);
			contentSpecialChars.setVisible(false);
			// TODO required for AutoCompleteTextFieldW
			// processField.setKeyBoardModeText(true);
			// updateKeyBoardListener.showInputField();
		} else if (mode == KeyboardMode.SPECIAL_CHARS) {
			// TODO required for AutoCompleteTextFieldW
			// processField.setKeyBoardModeText(false);
			contentNumber.setVisible(false);
			contentLetters.setVisible(false);
			contentSpecialChars.setVisible(true);
		}
	}



	@Override
	public void show() {
		this.keyboardWanted = true;
		updateSize();
		checkLanguage();
		setStyleName();// maybe not needed always, but definitely in Win8 app
		super.show();
	}
}