package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.MyMath;

public class AlgoRoundedPolygon extends AlgoElement {

	private GeoPointND[] points;
	private GeoLocus locus;
	private GeoNumberValue radius;

	public AlgoRoundedPolygon(Construction c, GeoPointND[] points,
			GeoNumberValue radius) {
		super(c);
		this.points = points;
		this.radius = radius;
		this.locus = new GeoLocus(c);
		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		this.input = new GeoElement[points.length + 1];
		for (int i = 0; i < points.length; i++) {
			input[i] = points[i].toGeoElement();
		}
		input[input.length - 1] = radius.toGeoElement();
		setOnlyOutput(locus);
		setDependencies();

	}

	@Override
	public void compute() {
		locus.clearPoints();
		locus.insertPoint(cropX(0, 1, tan(1, 0, points.length - 1)),
				cropY(0, 1, tan(1, 0, points.length - 1)), SegmentType.MOVE_TO);
		for (int i = 0; i < points.length; i++) {
			int j = i == points.length - 1 ? 0 : i + 1;
			int k = j == points.length - 1 ? 0 : j + 1;
			double cos = tan(i, j, k);
			locus.insertPoint(cropX(j, i, cos), cropY(j, i, cos),
					SegmentType.LINE_TO);
			locus.insertPoint(points[j].getInhomX(), points[j].getInhomY(),
					SegmentType.AUXILIARY);
			locus.insertPoint(cropX(j, k, cos), cropY(j, k, cos),
					SegmentType.ARC_TO);

		}

		locus.setDefined(true);
	}

	private double tan(int i, int j, int k) {
		double angle = MyMath.angle(
				points[i].getInhomX() - points[j].getInhomX(),
				points[i].getInhomY() - points[j].getInhomY(),
				points[j].getInhomX() - points[k].getInhomX(),
				points[j].getInhomY() - points[k].getInhomY());
		return Math.abs(Math.tan(angle / 2));
	}

	private double cropX(int i, int j, double cos) {
		return points[i].getInhomX()
				+ (points[j].getInhomX() - points[i].getInhomX())
				* radius.getDouble() / points[i].distance(points[j]) * cos;
	}

	private double cropY(int i, int j, double cos) {
		return points[i].getInhomY()
				+ (points[j].getInhomY() - points[i].getInhomY())
				* radius.getDouble() / points[i].distance(points[j]) * cos;
	}

	@Override
	public GetCommand getClassName() {
		return Commands.RoundedPolygon;
	}

}
