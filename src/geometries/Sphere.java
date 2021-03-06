package geometries;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import primitives.*;

public class Sphere extends RadialGeometry {
	private Point3D _center;

	// ***************** Constructors ********************** //
	/**
	 * 
	 * @param _r
	 * @param _center
	 * @param emission
	 * @param material
	 */
	public Sphere(double _r, Point3D _center, Color emission, Material material) {
		super(_r, emission, material);
		this._center = new Point3D(_center);
	}

	/**
	 * 
	 * @param s
	 */
	public Sphere(Sphere s) {
		super(s._radius, s._emission, s._material);
		this._center = new Point3D(s._center);
	}

	// ***************** Getters/Setters ********************** //

	public Point3D get_center() {
		return _center;
	}

	/*
	 * public void set_center(Point3D _center) { this._center = _center; }
	 */

	// ***************** Administration ******************** //

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sphere other = (Sphere) obj;
		if (_center == null) {
			if (other._center != null)
				return false;
		} else if (!_center.equals(other._center))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + "Sphere [_center=" + _center + "]";
	}

	// ***************** Operations ******************** //

	/**
	 * checks if given point is on sphere
	 * @param p1
	 * @return boolean
	 */
	public boolean is_on_sphere(Point3D p1) {
		if (Coordinate.isToCloseToZero(this._radius - _center.distance(p1)))
			return true;
		return false;
	}

	@Override
	public Vector getNormal(Point3D p) {
		// if(!is_on_sphere(p))
		// throw new Exception("this point is not on the Sphere");

		return new Vector(p).subtract(new Vector(this._center)).normalization();
	}

	@Override
	public Map<Geometry, List<Point3D>> findIntersections(Ray r) {
		Map<Geometry, List<Point3D>> intersections = new HashMap<Geometry, List<Point3D>>();
		ArrayList<Point3D> arrPoints = new ArrayList<>();
		Vector u = this._center.subtract(r.getP3D());
		double tm = r.getDirection().dot_product(u);
		double dSquared = u.dot_product(u) - (tm * tm);
		double temp = this._radius * this._radius - dSquared;
		if (temp < 0)
			return new HashMap<Geometry,List<Point3D>>();
		double th = Math.sqrt(temp);

		if (Coordinate.isToCloseToZero(th)) {
			if(!Coordinate.isToCloseToZero(tm))
			arrPoints.add(r.getP3D().add(r.getDirection().multiply(tm)));
			else
				return intersections;
		} else {
			double t1 = tm + th;
			double t2 = tm - th;
			if (t1 > 0 && !Coordinate.isToCloseToZero(t1))
				arrPoints.add(r.getP3D().add(r.getDirection().multiply(t1)));
			if (t2 > 0 && !Coordinate.isToCloseToZero(t2))
				arrPoints.add(r.getP3D().add(r.getDirection().multiply(t2)));
		}
		
		if(arrPoints.size() != 0)
			intersections.put(this, arrPoints);

		return intersections;
	}
}
