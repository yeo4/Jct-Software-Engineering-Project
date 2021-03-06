package geometries;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import primitives.*;

public class Cylinder extends Tube {
	private double _hight;
	private Point3D _Pcenter1;
	private Point3D _Pcenter2;
	private Plane _plane1;
	private Plane _plane2;
	
	// ***************** Constructors ********************** //
	
	/**
	 * regular constructor
	 * @param r
	 * @param _axisPoint
	 * @param _axisDirection
	 * @param _hight
	 * @param emission
	 * @param material
	 */
	public Cylinder(double r, Point3D _axisPoint, Vector _axisDirection, double _hight, Color emission, Material material){
		super(r, _axisPoint, _axisDirection, emission, material);
		if(_hight <= 0)
			throw new IllegalArgumentException("Hight must be positive");
		this._hight = _hight;
		this._Pcenter1 = this._axisPoint.add(this._axisDirection.multiply(this._hight / 2));
		this._Pcenter2 = this._axisPoint.add(this._axisDirection.multiply(-this._hight / 2));
		this._plane1 = new Plane(this._Pcenter1, this._axisDirection, this._emission, this._material);
		this._plane2 = new Plane(this._Pcenter2, this._axisDirection, this._emission, this._material);
	}
	
	/**
	 * copy constructor
	 * @param c
	 */
	public Cylinder(Cylinder c) {
		super(c._radius, c._axisPoint, c._axisDirection, c._emission, c._material);
		this._hight = c._hight;
		this._Pcenter1 = new Point3D (c._Pcenter1);
		this._Pcenter2 = new Point3D (c._Pcenter2);
		this._plane1 = new Plane(c._plane1);
		this._plane2 = new Plane(c._plane2);
	}

	// ***************** Getters/Setters ********************** // 
	
	public double get_hight() {
		return _hight;
	}
	
	/*public void set_hight(double _hight) {
		this._hight = _hight;
	}*/
	
	// ***************** Administration  ******************** //

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cylinder other = (Cylinder) obj;
		if (Double.doubleToLongBits(_hight) != Double.doubleToLongBits(other._hight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + "Cylinder [_hight=" + _hight + "]";
	}
	
	// ***************** Operations ******************** // 
	
	/**
	 * checks if the point is on the first cap of the cylinder
	 * @param p
	 * @return boolean
	 */
	public boolean is_on_cap1(Point3D p) {
		Vector v = p.subtract(_Pcenter1);
		if(Coordinate.isToCloseToZero(p.subtract(this._Pcenter1).dot_product(this._axisDirection)) && v.dot_product(v) < this._radius * this._radius)
			return true;
		return false;
			
	}
	
	/**
	 * checks if the point is on the second cap of the cylinder
	 * @param p
	 * @return boolean
	 */
	public boolean is_on_cap2(Point3D p) {
		Vector v = p.subtract(_Pcenter2);
		if(Coordinate.isToCloseToZero(p.subtract(this._Pcenter2).dot_product(this._axisDirection)) && v.dot_product(v) < this._radius * this._radius)
			return true;
		return false;
			
	}
	
	/**
	 * checks if the point is on the first cap of the cylinder
	 * given that is definitely on the plane of the cap
	 * @param p
	 * @return boolean
	 */
	public boolean is_on_cap1_given_on_plane(Point3D p) {
		Vector v = p.subtract(_Pcenter1);
		double a = Math.sqrt(v.dot_product(v));
		
		if(Coordinate.isToCloseToZero((a - this._radius)))
			return false;
		if(a < this._radius)
			return true;
		return false;
			
	}
	
	/**
	 * checks if the point is on the second cap of the cylinder
	 * given that is definitely on the plane of the cap
	 * @param p
	 * @return boolean
	 */
	public boolean is_on_cap2_given_on_plane(Point3D p) {
		Vector v = p.subtract(_Pcenter2);
		double a = Math.sqrt(v.dot_product(v));
		
		if(Coordinate.isToCloseToZero((a - this._radius)))
			return false;
		if(a < this._radius)
			return true;
		return false;
			
	}
	
	@Override
	public Vector getNormal(Point3D p) {

		if(this.is_on_cap1(p))
			return p.subtract(this._axisPoint.add(p.subtract(this._Pcenter1))).normalization();
		if(this.is_on_cap2(p))
			return p.subtract(this._axisPoint.add(p.subtract(this._Pcenter2))).normalization();
		
		return super.getNormal(p);
	}

	@Override
	public Map<Geometry, List<Point3D>> findIntersections(Ray r) {
		Map<Geometry, List<Point3D>> intersections = new HashMap<Geometry, List<Point3D>>();
		ArrayList<Point3D> arrPoints = new ArrayList<>();
		
		List<Point3D> plane1Points = this._plane1.findIntersections(r).get(this._plane1);
		List<Point3D> plane2Points = this._plane2.findIntersections(r).get(this._plane2);
		List<Point3D> tubePoints = super.findIntersections(r).get( (Tube)this);
		
		if(plane1Points != null)
			if(plane1Points.size() != 0)
				if(is_on_cap1_given_on_plane(plane1Points.get(0)))
					arrPoints.add(plane1Points.get(0));
		if(plane2Points != null)
			if(plane2Points.size() != 0)
				if(is_on_cap2_given_on_plane(new Point3D(plane2Points.get(0))))
					arrPoints.add(new Point3D(plane2Points.get(0)));
		if(tubePoints != null)
		{
			for(int i = 0; i < tubePoints.size(); i++)
			{
				double d1 = this.get_axisDirection().dot_product(tubePoints.get(i).subtract(this._Pcenter1));
				double d2 = this.get_axisDirection().dot_product(tubePoints.get(i).subtract(this._Pcenter2));
				if(d1 <= 0 && d2 >= 0)
					arrPoints.add(tubePoints.get(i));
				else if(Coordinate.isToCloseToZero(d1) && Coordinate.isToCloseToZero(d2))
					arrPoints.add(tubePoints.get(i));
			}
		}
		
		if(arrPoints.size() != 0)
			intersections.put(this, arrPoints);
			
			return intersections;
	}
}
