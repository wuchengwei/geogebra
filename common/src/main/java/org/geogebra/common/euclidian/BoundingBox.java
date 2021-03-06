package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.factories.AwtFactory;

/**
 * bounding box construction for selected elements
 * 
 * @author csilla
 *
 */
public class BoundingBox {


	private GRectangle2D rectangle;
	private ArrayList<GEllipse2DDouble> handlers;
	private int nrHandlers = 8;

	/**
	 * @param view
	 *            - euclidianView
	 */
	public BoundingBox(EuclidianView view) {
		handlers = new ArrayList<GEllipse2DDouble>();
	}

	/**
	 * @return box part of bounding box construction
	 */
	public GRectangle2D getRectangle() {
		return rectangle;
	}

	/**
	 * @param rectangle
	 *            - box part of bounding box construction
	 */
	public void setRectangle(GRectangle2D rectangle) {
		this.rectangle = rectangle;
		if (rectangle != null) {
			createHandlers();
		}
	}

	/**
	 * @return handler points of bounding box construction
	 */
	public ArrayList<GEllipse2DDouble> getHandlers() {
		return handlers;
	}

	/**
	 * @param handlers
	 *            - points of bounding box construction
	 */
	public void setHandlers(ArrayList<GEllipse2DDouble> handlers) {
		this.handlers = handlers;
	}

	/**
	 * @return number of needed handlers
	 */
	public int getNrHandlers() {
		return nrHandlers;
	}

	/**
	 * @param nrHandlers
	 *            - number of handlers
	 */
	public void setNrHandlers(int nrHandlers) {
		this.nrHandlers = nrHandlers;
	}

	private void createHandlers() {
		if (handlers == null) {
			handlers = new ArrayList<GEllipse2DDouble>();
		}

		handlers.clear();
		
		// init handler list
		for (int i = 0; i < /* = */nrHandlers; i++) {
			GEllipse2DDouble handler = AwtFactory.getPrototype()
					.newEllipse2DDouble();
			handlers.add(handler);
		}

		if (nrHandlers == 8) {
			// corner handlers
			handlers.get(0).setFrameFromCenter(rectangle.getX(),
					rectangle.getY(),
				rectangle.getX() + 3, rectangle.getY() + 3);
			handlers.get(1).setFrameFromCenter(rectangle.getX(),
				rectangle.getMaxY(), rectangle.getX() + 3,
				rectangle.getMaxY() + 3);
			handlers.get(2).setFrameFromCenter(
				rectangle.getMaxX(), rectangle.getMaxY(),
				rectangle.getMaxX() + 3, rectangle.getMaxY() + 3);
			handlers.get(3).setFrameFromCenter(
				rectangle.getMaxX(), rectangle.getY(), rectangle.getMaxX() + 3,
				rectangle.getY() + 3);

			// side handlers
			handlers.get(4).setFrameFromCenter(
				(rectangle.getMinX() + rectangle.getMaxX()) / 2,
				rectangle.getMinY(),
				(rectangle.getMinX() + rectangle.getMaxX()) / 2 + 3,
				rectangle.getMinY() + 3);
			handlers.get(5).setFrameFromCenter(rectangle.getMinX(),
				(rectangle.getMinY() + rectangle.getMaxY()) / 2,
				rectangle.getMinX() + 3,
				(rectangle.getMinY() + rectangle.getMaxY()) / 2 + 3);
			handlers.get(6).setFrameFromCenter(
				(rectangle.getMinX() + rectangle.getMaxX()) / 2,
				rectangle.getMaxY(),
				(rectangle.getMinX() + rectangle.getMaxX()) / 2 + 3,
				rectangle.getMaxY() + 3);
			handlers.get(7).setFrameFromCenter(rectangle.getMaxX(),
				(rectangle.getMinY() + rectangle.getMaxY()) / 2,
				rectangle.getMaxX() + 3,
					(rectangle.getMinY() + rectangle.getMaxY()) / 2 + 3);
			// handler for rotation
			// handlers.get(8).setFrameFromCenter(
			// (rectangle.getMinX() + rectangle.getMaxX()) / 2,
			// rectangle.getMaxY() + 15,
			// (rectangle.getMinX() + rectangle.getMaxX()) / 2 + 3,
			// rectangle.getMaxY() + 15 + 3);
		}

	}

	/**
	 * method to draw the bounding box construction for selected geo
	 * 
	 * @param g2
	 *            - graphics
	 */
	public void draw(GGraphics2D g2) {
		// draw bounding box
		if (rectangle != null) {
			g2.setColor(GColor.newColor(192, 192, 192, 0.0));
			g2.setStroke(AwtFactory.getPrototype().newBasicStroke(2.0f,
					GBasicStroke.CAP_BUTT, GBasicStroke.JOIN_MITER));
			g2.fill(rectangle);
			g2.setColor(GColor.GEOGEBRA_GRAY);
			g2.draw(rectangle);
		}
		if (handlers != null && !handlers.isEmpty()) {
			// join rotation handler and bounding box
			// GLine2D line = AwtFactory.getPrototype().newLine2D();
			// line.setLine((rectangle.getMinX() + rectangle.getMaxX()) / 2,
			// rectangle.getMaxY(),
			// (rectangle.getMinX() + rectangle.getMaxX()) / 2,
			// rectangle.getMaxY() + 15);
			// g2.setColor(GColor.GEOGEBRA_GRAY);
			// g2.draw(line);
			for (int i = 0; i < /* = */nrHandlers; i++) {
				g2.setPaint(GColor.GEOGEBRA_BLUE);
				g2.fill(handlers.get(i));
				g2.setStroke(AwtFactory.getPrototype().newBasicStroke(2.0f,
						GBasicStroke.CAP_BUTT, GBasicStroke.JOIN_MITER));
				g2.setColor(GColor.GEOGEBRA_GRAY);
				g2.draw(handlers.get(i));
			}
		}
	}

	/**
	 * reset the parts of bounding box construction
	 */
	public void resetBoundingBox() {
		rectangle = null;
		handlers.clear();
	}

	/**
	 * 
	 * @param threshold
	 *            controller threshold
	 * @return distance threshold to select a point
	 */
	public static final int getSelectionThreshold(int threshold) {
		return threshold + 12;
	}

	/**
	 * @param x
	 *            - mouse event x
	 * @param y
	 *            - mouse event y
	 * @param hitThreshold
	 *            - threshold
	 * @return number of handler which was hit
	 */
	public int hitHandlers(int x, int y, int hitThreshold) {
		int index = -1;
		if (!handlers.isEmpty()) {
			for (int i = 0; i < handlers.size(); i++) {
				GEllipse2DDouble point = handlers.get(i);
				int r = getSelectionThreshold(hitThreshold);
				double dx = point.getBounds().getX()
						+ point.getBounds().getWidth() / 2 - x;
				double dy = point.getBounds().getY()
						+ point.getBounds().getHeight() / 2 - y;
				if (dx < r && dx > -r && dx * dx + dy * dy <= r * r) {
					return i;
				}
			}
		}
		return index;
	}

}
