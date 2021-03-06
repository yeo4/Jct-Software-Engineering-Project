package primitives;

public class Ray {
	private Point3D _P3D;
	private Vector _direction;
	private double _t;
	
	// ***************** Constructors ********************** //
	
	/**
	 * 
	 * @param P3D
	 * @param direction
	 */
	public Ray(Point3D P3D, Vector direction) {
		_P3D = new Point3D(P3D);
		_direction = direction.normalization();
		_t = direction.length();
	}
	
	/**
	 * 
	 * @param ray
	 */
	public Ray(Ray ray) {
		_P3D = new Point3D(ray.getP3D());
		_direction = new Vector(ray.getDirection());
		_t = ray._t;
	}
	// ***************** Getters/Setters ********************** //
	/**
	 * @return the 3DPoint
	 */
	public Point3D getP3D() {
		return _P3D;
	}
	/**
	 * @return the Vector
	 */
	public Vector getDirection() {
		return _direction;
	}
	/**
	 * @return the size of vector
	 */
	public double getT() {
		return _t;
	}
	
	// ***************** Administration  ******************** //
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ray other = (Ray) obj;
		if (_P3D == null) {
			if (other._P3D != null)
				return false;
		} else if (!_P3D.equals(other._P3D))
			return false;
		if (_direction == null) {
			if (other._direction != null)
				return false;
		} else if (!_direction.equals(other._direction))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[Point=" + _P3D + ", Vector=" + _direction + "]";
	}
}
