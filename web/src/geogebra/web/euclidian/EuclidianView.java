package geogebra.web.euclidian;

import java.util.ArrayList;

import geogebra.common.awt.Dimension;
import geogebra.common.awt.Font;
import geogebra.common.awt.Graphics2D;
import geogebra.common.euclidian.AbstractEuclidianController;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.AbstractZoomer;
import geogebra.common.euclidian.DrawBoolean;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.GetViewId;
import geogebra.common.euclidian.Previewable;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.factories.AwtFactory;
import geogebra.common.javax.swing.Box;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.web.awt.BasicStroke;
import geogebra.web.gui.app.GeoGebraFrame;
import geogebra.web.main.Application;
import geogebra.web.main.EuclidianViewPanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.GestureChangeEvent;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.BlurEvent; 
import com.google.gwt.event.dom.client.FocusHandler; 
import com.google.gwt.event.dom.client.FocusEvent; 

public class EuclidianView extends AbstractEuclidianView implements SettingListener{
	
	public geogebra.web.awt.Graphics2D g2p = null;
	public boolean isInFocus = false;

	protected static final long serialVersionUID = 1L;
	
	protected ImageElement resetImage, playImage, pauseImage, upArrowImage,
	downArrowImage;

	public EuclidianView(AbsolutePanel euclidianViewPanel,
            AbstractEuclidianController euclidiancontroller, boolean[] showAxes,
            boolean showGrid, EuclidianSettings settings) {		
		super(euclidiancontroller, settings);
		Canvas canvas = (Canvas)(euclidianViewPanel.getWidget(0));
		evNo = 1;
	    // TODO Auto-generated constructor stub
		this.g2p = new geogebra.web.awt.Graphics2D(canvas);
		setAxesColor(geogebra.common.awt.Color.black);
		updateFonts();
		setStandardCoordSystem();
		attachView();
		
		((EuclidianController)euclidiancontroller).setView(this);
//		canvas.addClickHandler((EuclidianController)euclidiancontroller);	
//		canvas.addMouseMoveHandler((EuclidianController)euclidiancontroller);
//		canvas.addMouseOverHandler((EuclidianController)euclidiancontroller);
//		canvas.addMouseOutHandler((EuclidianController)euclidiancontroller);
//		canvas.addMouseDownHandler((EuclidianController)euclidiancontroller);
//		canvas.addMouseUpHandler((EuclidianController)euclidiancontroller);
//		canvas.addMouseWheelHandler((EuclidianController)euclidiancontroller);
//		
//		canvas.addTouchStartHandler((EuclidianController)euclidiancontroller);
//		canvas.addTouchEndHandler((EuclidianController)euclidiancontroller);
//		canvas.addTouchMoveHandler((EuclidianController)euclidiancontroller);
//		canvas.addTouchCancelHandler((EuclidianController)euclidiancontroller);
//		canvas.addGestureStartHandler((EuclidianController)euclidiancontroller);
//		canvas.addGestureChangeHandler((EuclidianController)euclidiancontroller);
//		canvas.addGestureEndHandler((EuclidianController)euclidiancontroller);

		canvas.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent be) {
				focusLost();
			}
		});
		canvas.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent fe) {
				focusGained();
			}
		});
		canvas.addKeyDownHandler(getApplication().getGlobalKeyDispatcher());
		canvas.addKeyUpHandler(getApplication().getGlobalKeyDispatcher());
		
		euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, ClickEvent.getType());
		euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, MouseMoveEvent.getType());
		euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, MouseOverEvent.getType());
		euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, MouseOutEvent.getType());
		euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, MouseDownEvent.getType());
		euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, MouseUpEvent.getType());
		euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, MouseWheelEvent.getType());
		
		euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, TouchStartEvent.getType());
		euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, TouchEndEvent.getType());
		euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, TouchMoveEvent.getType());
		euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, TouchCancelEvent.getType());
		euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, GestureStartEvent.getType());
		euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, GestureChangeEvent.getType());
		euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, GestureEndEvent.getType());
		
		//euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, KeyPressEvent.getType());
//		euclidianViewPanel.addKeyDownHandler(getApplication().getGlobalKeyDispatcher());
//		euclidianViewPanel.addKeyUpHandler(getApplication().getGlobalKeyDispatcher());
//		euclidianViewPanel.addKeyPressHandler(getApplication().getGlobalKeyDispatcher());
		
		if ((evNo == 1) || (evNo == 2)) {
			EuclidianSettings es = getApplication().getSettings().getEuclidian(evNo);
			settingsChanged(es);
			es.addListener(this);
		}
    }


	public void paintBackground(geogebra.common.awt.Graphics2D g2) {
		((geogebra.web.awt.Graphics2D)g2).drawGraphics(
				(geogebra.web.awt.Graphics2D)bgGraphics, 0, 0, null);
	}

	// STROKES
	protected static MyBasicStroke standardStroke = new MyBasicStroke(1.0f);

	protected static MyBasicStroke selStroke = new MyBasicStroke(
			1.0f + EuclidianStyleConstants.SELECTION_ADD);

	protected boolean unitAxesRatio;

	static public MyBasicStroke getDefaultStroke() {
		return standardStroke;
	}

	static public MyBasicStroke getDefaultSelectionStroke() {
		return selStroke;
	}
	
	private boolean disableRepaint;
	public void setDisableRepaint(boolean disableRepaint) {
		this.disableRepaint = disableRepaint;
	}
	
	
	/**
	 * Creates a stroke with thickness width, dashed according to line style
	 * type.
	 * @param width 
	 * @param type 
	 * @return stroke
	 */
	public static BasicStroke getStroke(float width, int type) {
		float[] dash;

		switch (type) {
		case EuclidianStyleConstants.LINE_TYPE_DOTTED:
			dash = new float[2];
			dash[0] = width; // dot
			dash[1] = 3.0f; // space
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
			dash = new float[2];
			dash[0] = 4.0f + width;
			// short dash
			dash[1] = 4.0f; // space
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
			dash = new float[2];
			dash[0] = 8.0f + width; // long dash
			dash[1] = 8.0f; // space
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
			dash = new float[4];
			dash[0] = 8.0f + width; // dash
			dash[1] = 4.0f; // space before dot
			dash[2] = width; // dot
			dash[3] = dash[1]; // space after dot
			break;

		default: // EuclidianStyleConstants.LINE_TYPE_FULL
			dash = null;
		}

		int endCap = dash != null ? BasicStroke.CAP_BUTT : standardStroke.getEndCap();

		return new BasicStroke(width, endCap, standardStroke.getLineJoin(),
				standardStroke.getMiterLimit(), dash, 0.0f);
	}

	public static void setAntialiasingStatic(Graphics2D g2d) {
		// In GWT, everything is anti-aliased by default, so we don't need to do anything here.
    }

	public void setCoordinateSpaceSize(int width, int height) {
		g2p.setCoordinateSpaceWidth(width);
		g2p.setCoordinateSpaceHeight(height);
		try {
			g2p.getCanvas().getElement().getParentElement().getStyle().setWidth(width, Style.Unit.PX);
			g2p.getCanvas().getElement().getParentElement().getStyle().setHeight(height, Style.Unit.PX);
		} catch (Exception exc) {
			Application.debug("Problem with the parent element of the canvas");
		}
	}

	public void synCanvasSize() {
		setCoordinateSpaceSize(g2p.getOffsetWidth(), g2p.getOffsetHeight());
	}

	/**
	 * Gets the coordinate space width of the &lt;canvas&gt;.
	 * 
	 * @return the logical width
	 */
	public int getWidth() {
		return g2p.getCoordinateSpaceWidth();
	}

	/**
	 * Gets the coordinate space height of the &lt;canvas&gt;.
	 * 
	 * @return the logical height
	 */
	public int getHeight() {
		return g2p.getCoordinateSpaceHeight();
	}
	
	/**
	 * Gets pixel width of the &lt;canvas&gt;.
	 * 
	 * @return the physical width in pixels
	 */
	public int getPhysicalWidth() {
		return g2p.getOffsetWidth();
	}
	
	/**
	 * Gets pixel height of the &lt;canvas&gt;.
	 * 
	 * @return the physical height in pixels
	 */
	public int getPhysicalHeight() {
		return g2p.getOffsetHeight();
	}
	
	public int getAbsoluteTop() {
		return g2p.getAbsoluteTop();
	}
	
	public int getAbsoluteLeft() {
		return g2p.getAbsoluteLeft();
	}

	public EuclidianController getEuclidianController() {
		return (EuclidianController)euclidianController;
    }

	
	public void clearView() {
		//TODO: remove hotEqns?
		resetLists();
		//TODO: setpreferredsize setting?
		updateBackgroundImage(); // clear traces and images
		// resetMode();
    }

    @Override
    public Application getApplication() {
    	return (Application)super.getApplication();
    }

	
	private Graphics2D g2dtemp;

	private geogebra.common.awt.Color backgroundColor = geogebra.web.awt.Color.white;
	@Override
    public Graphics2D getTempGraphics2D(Font plainFontCommon) {
	    if(g2dtemp==null)
	    	g2dtemp = new geogebra.web.awt.Graphics2D(Canvas.createIfSupported());
	    g2dtemp.setFont(plainFontCommon);
	    return g2dtemp;
    }

	@Override
    public Font getFont() {
		return new geogebra.web.awt.Font((geogebra.web.awt.Font)g2p.getFont());
    }

    public geogebra.common.awt.Color getBackgroundCommon() {
	    return backgroundColor ;
    }

	@Override
    protected void setHeight(int h) {
	    //TODO: what should this method do in Web and in Desktop? 
	 	AbstractApplication.debug("implementation needed really"); 
	 	//g2p.setCoordinateSpaceWidth(h); 
	 	//g2p.setWidth(h); 
    }

	@Override
    protected void setWidth(int h) {
	    //TODO: what should this method do in Web and in Desktop? 
	    AbstractApplication.debug("implementation needed really");
    }


    public void repaint() {
    	if (!disableRepaint)
    		paint(g2p);
    }

	@Override
    protected void initCursor() {
		setDefaultCursor();
    }

	@Override
    protected void setStyleBarMode(int mode) {
	    AbstractApplication.debug("implementation needed for GUI"); // TODO
    }


	private ImageElement getResetImage() {
		if (resetImage == null) {
			resetImage = getApplication().getRefreshViewImage();
		}
		return resetImage;
	}

	private ImageElement getPlayImage() {
		if (playImage == null) {
			playImage = getApplication().getPlayImage();
		}
		return playImage;
	}

	private ImageElement getPauseImage() {
		if (pauseImage == null) {
			pauseImage = getApplication().getPauseImage();
		}
		return pauseImage;
	}

	public boolean hitAnimationButton(AbstractEvent e) {
		// draw button in focused EV only
				if (!drawPlayButtonInThisView()) {
					return false;
				}

				return kernel.needToShowAnimationButton() && (e.getX() - g2p.getAbsoluteLeft() + Window.getScrollLeft() <= 20)
						&& (e.getY() - g2p.getAbsoluteTop() + Window.getScrollTop() >= (getHeight() - 20));
    }


	@Override
    public void updateSize() {

		if ((getWidth() <= 0) || (getHeight() <= 0)) {
			return;
		}

		// real world values
		setRealWorldBounds();

		try {
			createImage();
		} catch (Exception e) {
			bgImage = null;
			bgGraphics = null;
			System.gc();
		}

		updateBackgroundImage();

		if (!firstPaint) // if is here to avoid infinite loop
			updateAllDrawables(true);
    }

	private void createImage() {
		bgImage = new geogebra.web.awt.BufferedImage(getWidth(), getHeight(), 0, false);
		bgGraphics = bgImage.createGraphics();
		if (antiAliasing) {
			setAntialiasing(bgGraphics);
		}
	}

	@Override
    public boolean requestFocusInWindow() {		
		g2p.getCanvas().getCanvasElement().focus();
		focusGained();
		return true;
    }

	public void focusLost() {
		if (isInFocus) {
			this.isInFocus = false;
			GeoGebraFrame.useDataParamBorder(
				getApplication().getArticleElement(),
				getApplication().getGeoGebraFrame());
		}
	}

	public void focusGained() {
		if (!isInFocus) {
			this.isInFocus = true;
			GeoGebraFrame.useFocusedBorder(
				getApplication().getArticleElement(),
				getApplication().getGeoGebraFrame());
		}
	}

	public void setDefaultCursor() {
		getApplication().resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_default");
    }

	public void setHitCursor() {
		getApplication().resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_hit");
    }
	
	public geogebra.common.euclidian.EuclidianStyleBar getStyleBar() {
		if (styleBar == null) {
			styleBar = new EuclidianStyleBar(this);
		}

		return styleBar;
	}
	
	@Override
    protected boolean drawPlayButtonInThisView() {

		// just one view
		if ( getApplication().getGuiManager() == null) {
			return true;
		}
		// eg ev1 just closed
		// GetViewId evp = getApplication().getGuiManager().getLayout().getDockManager().getFocusedEuclidianPanel();
		//we dont have above yet: so just be null :-)
		GetViewId evp = null;
		if (evp == null) {
			return true;
		}

		return !((getApplication().getGuiManager() != null) && (this.getViewID() != evp
				.getViewId()));
	}
	
	@Override
	final protected void drawAnimationButtons(geogebra.common.awt.Graphics2D g2) {

		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) {
			return;
		}

		int x = 6;
		int y = getHeight() - 22;

		if (highlightAnimationButtons) {
			// draw filled circle to highlight button
			g2.setColor(geogebra.common.awt.Color.darkGray);
		} else {
			g2.setColor(geogebra.common.awt.Color.lightGray);
		}

		g2.setStroke(geogebra.common.euclidian.EuclidianStatic.getDefaultStroke());

		// draw pause or play button
		g2.drawRect(x - 2, y - 2, 18, 18);
		ImageElement img = kernel.isAnimationRunning() ? getPauseImage()
				: getPlayImage();
		g2.drawImage(new geogebra.web.awt.BufferedImage(img), x, y, null);
	}

	@Override
    protected void drawActionObjects(Graphics2D g) {
	    AbstractApplication.debug("implementation needed really"); // TODO
    }


	@Override
    protected void setAntialiasing(Graphics2D g2) {
		// In GWT, everything is anti-aliased by default, so we don't need to do anything here.
    }

	@Override
    public void setBackground(geogebra.common.awt.Color bgColor) {
		if (bgColor != null)
			backgroundColor = AwtFactory.prototype.newColor(
			bgColor.getRed(),
			bgColor.getGreen(),
			bgColor.getBlue(),
			bgColor.getAlpha());
    }

	@Override
  public void setPreferredSize(Dimension preferredSize) {
    g2p.setPreferredSize(preferredSize);
    updateSize();
    setReIniting(false);
  }

	/**
	 * Updates the size of the canvas and coordinate system
	 * @param width the new width (in pixel)
	 * @param height the new height (in pixel)
	 */
	public void setPreferredSize(int width, int height) {
		setPreferredSize(new geogebra.web.awt.Dimension(width, height));
	}

	public void setDragCursor() {
		getApplication().resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_drag");
    }

	public void setToolTipText(String plainTooltip) {
	    // TODO Auto-generated method stub
	    
    }

	public void setResizeXAxisCursor() {
		getApplication().resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_resizeXAxis");
    }

	public void setResizeYAxisCursor() {
		getApplication().resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_resizeYAxis");
    }

	@Override
    public void updatePreviewable() {
	    // TODO Auto-generated method stub
	    
    }

	public void setMoveCursor() {
		getApplication().resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_move");
    }

	@Override
    protected AbstractZoomer newZoomer() {
	    return new MyZoomer(this);
    }

	public boolean hasFocus() {
	    return isInFocus;
    }

	@Override
    public void add(Box box) {
	    AbstractApplication.debug("implementation needed - just finishing"); // TODO
	    getApplication().getEuclidianViewpanel().add(
	    		geogebra.web.javax.swing.Box.getImpl((geogebra.web.javax.swing.Box) box),
	    		(int)box.getBounds().getX(), (int)box.getBounds().getY());
	    
    }

	@Override
    public void remove(Box box) {
		AbstractApplication.debug("implementation needed - just finishing"); // TODO
	    getApplication().getEuclidianViewpanel().remove(
	    		geogebra.web.javax.swing.Box.getImpl((geogebra.web.javax.swing.Box) box));
	    
    }

	@Override
	protected void drawResetIcon(geogebra.common.awt.Graphics2D g){
		int w = getWidth() + 2;
		((geogebra.web.awt.Graphics2D)g).getCanvas().getContext2d().drawImage(
			getResetImage(), w - 18, 2);
	}

}
