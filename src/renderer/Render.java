package renderer;

import java.util.Map;

import elements.LightSource;
import elements.PointLight;
import elements.SpotLight;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import geometries.Geometry;
import primitives.*;
import scene.*;

public class Render {
	private Scene _scene;
	private ImageWriter _imageWriter;
	
	// max recursion level
	private final int MAX_CALC_COLOR_LEVEL = 5;
	
	// number of rays per pixel (besides original) for depth of field
	private final int NUM_RAYS_PER_PIXEL = 15;

	/**
	 * 
	 * @param _imageWriter
	 * @param _scene
	 */
	public Render(ImageWriter _imageWriter, Scene _scene) {
		this._scene = _scene;
		this._imageWriter = _imageWriter;
	}

	/**
	 * renders the image
	 */
	public void renderImage() {
		for (int i = 0; i < _imageWriter.getNx(); i++) {
			for (int j = 0; j < _imageWriter.getNy(); j++) {
				if(i == 50 && j == 339)
				{
					System.out.println();
				}
				Ray r = _scene.getCamera().constructRayThroughPixel(_imageWriter.getNx(), _imageWriter.getNy(), i, j,
						_scene.getScreenDistance(), _imageWriter.getWidth(), _imageWriter.getHeight());
				// Map<Geometry, List<Point3D>> intersectionPoints = new
				// HashMap<Geometry, List<Point3D>>(
				// _scene.getRayIntersections(r));
				Map<Geometry, List<Point3D>> intersectionPoints = _scene.getRayIntersections(r);

				Point3D focalp = _scene.getCamera().get_p0().add(
						r.getDirection().multiply(r.getT() * _scene.get_focalLength() / _scene.getScreenDistance()));

				Color c;

				if (intersectionPoints.isEmpty()) {
					c = _scene.getBackground().getWrapperColor();
				} else {
					Map<Geometry, Point3D> closestPoint = getClosestPoint(intersectionPoints);
					Map.Entry<Geometry, Point3D> onlyEntry = closestPoint.entrySet().iterator().next();
					c = calcColor(onlyEntry.getKey(), onlyEntry.getValue(), r).getWrapperColor();
				}

				for (int k = 0; k < this.NUM_RAYS_PER_PIXEL; k++) {
					intersectionPoints.clear();

					double x;
					double y;

					do {
						double rx = Math.random() * 2 - 1;
						double ry = Math.random() * 2 - 1;
						if (rx * rx + ry * ry <= 1) {
							x = rx * _scene.get_aperatureRadius();
							y = ry * _scene.get_aperatureRadius();
							break;
						}

					} while (true);

					Point3D p = _scene.getCamera().get_p0().add(_scene.getCamera().get_vUp().multiply(y))
							.add(_scene.getCamera().get_vRight().multiply(x));
					
					Ray randomRay = new Ray(p, focalp.subtract(p));
					intersectionPoints = _scene.getRayIntersections(randomRay);

					if (intersectionPoints.isEmpty()) {
						c.add(_scene.getBackground().getColor());
					} else {
						Map<Geometry, Point3D> closestPoint = getClosestPoint(intersectionPoints);
						Map.Entry<Geometry, Point3D> onlyEntry = closestPoint.entrySet().iterator().next();
						c.add(calcColor(onlyEntry.getKey(), onlyEntry.getValue(), randomRay).getColor());
					}
				}
				if (this.NUM_RAYS_PER_PIXEL > 0)
					_imageWriter.writePixel(i, j, c.reduce(this.NUM_RAYS_PER_PIXEL).getColor());
				else
					_imageWriter.writePixel(i, j, c.getColor());

			}
			System.out.println(i + "/" + _imageWriter.getNx());
		}
		System.out.println("Finish Render()");
	}

	/**
	 * prints a grid on the picture with a distance of interval between lines
	 * @param interval
	 */
	public void printGrid(double interval) {
		for (int i = 0; i <= _imageWriter.getNx() - 1; i++) {
			for (int j = _imageWriter.getNy() - 1; j >= 0; j--) {
				if (i % interval == 0 || j % interval == 0) {
					System.out.println(i + "," + j);
					_imageWriter.writePixel(i, j, new Color(255, 255, 255).getColor());
				}
			}
		}
		System.out.println("Finish");
	}

	/**
	 *  writes to image
	 */
	public void writeToImage() {
		_imageWriter.writeToimage();
	}

	/**
	 * returns the color at the given point from the perspective of the ray
	 * @param geometry
	 * @param point
	 * @param r
	 * @return color
	 */
	private Color calcColor(Geometry geometry, Point3D point, Ray r) {
		return calcColor(geometry, point, r, MAX_CALC_COLOR_LEVEL, 1);
		/*
		 * Color color = _scene.getAmbientLight().getIntensity(); color =
		 * color.add(geometry.get_emission());
		 * 
		 * Vector v =
		 * point.subtract(_scene.getCamera().get_p0()).normalization(); Vector n
		 * = geometry.getNormal(point); int nShininess =
		 * geometry.get_material().getnShininess(); double kd =
		 * geometry.get_material().get_Kd(); double ks =
		 * geometry.get_material().get_Ks();
		 * 
		 * for (LightSource lightSource : this._scene.getLights()) { Vector l =
		 * lightSource.getL(point); if (n.dot_product(l) * n.dot_product(v) > 0)
		 * { if (!occluded(l, point, geometry)) { Color lightIntensity =
		 * lightSource.getIntensity(point); color.add(calcDiffusive(kd, l, n,
		 * lightIntensity), calcSpecular(ks, l, n, v, nShininess,
		 * lightIntensity)); } } }
		 * 
		 * return color;
		 */
	}

	/**
	 * returns the color at the given point from the perspective of the ray
	 * (takes into account recursion level)
	 * @param geometry
	 * @param point
	 * @param r
	 * @param level
	 * @param k
	 * @return color
	 */
	private Color calcColor(Geometry geometry, Point3D point, Ray r, int level, double k) {
		if (level == 0 || Coordinate.isToCloseToZero(k))
			return new Color(0, 0, 0);
		Color color = _scene.getAmbientLight().getIntensity();
		color = color.add(geometry.get_emission(point));

		Vector v = r.getDirection();
		Vector n = geometry.getNormal(point);
		int nShininess = geometry.get_material().getnShininess();
		double kd = geometry.get_material().get_Kd();
		double ks = geometry.get_material().get_Ks();

		for (LightSource lightSource : this._scene.getLights()) {
			Vector l = lightSource.getL(point);
			if (n.dot_product(l) * n.dot_product(v) > 0) {
				double o = occluded(l, point, geometry, lightSource);
				if (!Coordinate.isToCloseToZero(o * k)) {
					Color lightIntensity = new Color(lightSource.getIntensity(point)).scale(o);
					color.add(calcDiffusive(kd, l, n, lightIntensity),
							calcSpecular(ks, l, n, v, nShininess, lightIntensity));
				}
			}
		}

		// Recursive call for a reflected ray
		Ray reflectedRay = constructReflectedRay(n, point, r);

		Map<Geometry, List<Point3D>> reflectedRayIntersectionPoints = new HashMap<Geometry, List<Point3D>>(
				_scene.getRayIntersections(reflectedRay));
		Color reflectedLight;

		if (reflectedRayIntersectionPoints.isEmpty()) {
			reflectedLight = _scene.getBackground();
		} else {
			Map<Geometry, Point3D> reflectedPoint = getClosestPoint(reflectedRayIntersectionPoints);
			double kr = geometry.get_material().get_Kr();
			Map.Entry<Geometry, Point3D> onlyEntryReflected = reflectedPoint.entrySet().iterator().next();
			reflectedLight = calcColor(onlyEntryReflected.getKey(), onlyEntryReflected.getValue(), reflectedRay,
					level - 1, k * kr).scale(kr);
		}

		// Recursive call for a refracted ray
		Ray refractedRay = constructRefractedRay(point, r);

		Map<Geometry, List<Point3D>> refractedRayIntersectionPoints = new HashMap<Geometry, List<Point3D>>(
				_scene.getRayIntersections(refractedRay));

		Color refractedLight;

		if (refractedRayIntersectionPoints.isEmpty()) {
			refractedLight = _scene.getBackground();
		} else {
			Map<Geometry, Point3D> refractedPoint = getClosestPoint(refractedRayIntersectionPoints);
			double kt = geometry.get_material().get_Kt();
			Map.Entry<Geometry, Point3D> onlyEntryRefracted = refractedPoint.entrySet().iterator().next();
			refractedLight = calcColor(onlyEntryRefracted.getKey(), onlyEntryRefracted.getValue(), refractedRay,
					level - 1, k * kt).scale(kt);
		}

		return color.add(reflectedLight, refractedLight);

	}

	/**
	 * constructs refracted ray
	 * @param point
	 * @param r
	 * @return ray
	 */
	private Ray constructRefractedRay(Point3D point, Ray r) {
		return new Ray(point, r.getDirection());
	}

	/**
	 * constructs reflected ray
	 * @param n
	 * @param point
	 * @param r
	 * @return ray
	 */
	private Ray constructReflectedRay(Vector n, Point3D point, Ray r) {
		return new Ray(point, r.getDirection().add(n.multiply(-2 * r.getDirection().dot_product(n))));
	}

	/**
	 * returns to what extent a given point is occluded from the given light source
	 * @param l
	 * @param point
	 * @param geometry
	 * @param light
	 * @return double (between 0 and 1)
	 */
	private double occluded(Vector l, Point3D point, Geometry geometry, LightSource light) {
		Vector lightDirection = l.multiply(-1); // from point to light source
		Vector normal = geometry.getNormal(point);
		Vector epsVector = normal.multiply((normal.dot_product(lightDirection) > 0) ? 1 / 10000 : -1 / 10000);
		Point3D geometryPoint = point.add(epsVector);
		Ray lightRay = new Ray(geometryPoint, lightDirection);
		
		boolean b = light instanceof PointLight || light instanceof SpotLight;
		
		double dSquared = 0;
		
		if(b)
			dSquared = point.distanceSquare(((PointLight) light).getPosition());
			
		
		double shadowK = 1;
		Map<Geometry, List<Point3D>> intersectionsPoints = _scene.getGeometries().findIntersections(lightRay);

		for (Map.Entry<Geometry, List<Point3D>> entry : intersectionsPoints.entrySet()) {
			int count = 0;
			for(Point3D p : entry.getValue())
			{
				if(p.distanceSquare(point) >= dSquared)
					count ++;
			}
			
			if(entry.getValue().size() == count)
				continue;
			
			shadowK *= entry.getKey().get_material().get_Kt();
		}

		return shadowK;
	}

	/**
	 * calculates specular component
	 * @param ks
	 * @param l
	 * @param n
	 * @param v
	 * @param nShininess
	 * @param lightIntensity
	 * @return color
 	 */
	private Color calcSpecular(double ks, Vector l, Vector n, Vector v, int nShininess, Color lightIntensity) {
		Vector r = l.add(n.multiply(-2 * l.dot_product(n))).normalization();
		double vr = v.dot_product(r);
		if (vr > 0)
			return new Color(0, 0, 0);
		return new Color(lightIntensity).scale(ks * Math.pow(Math.abs(vr), nShininess));
	}

	/**
	 * calculates diffuse component
	 * @param kd
	 * @param l
	 * @param n
	 * @param lightIntensity
	 * @return
	 */
	private Color calcDiffusive(double kd, Vector l, Vector n, Color lightIntensity) {
		return new Color(lightIntensity).scale(kd * Math.abs(l.dot_product(n)));
	}

	/**
	 * returns closest point from intersection points to cameras origin point
	 * @param intersectionPoints
	 * @return point3D
	 */
	private Map<Geometry, Point3D> getClosestPoint(Map<Geometry, List<Point3D>> intersectionPoints) {
		Point3D From = _scene.getCamera().get_p0();
		double minDisSquare = Double.MAX_VALUE;
		Map<Geometry, Point3D> closest = new HashMap<>();

		if (intersectionPoints.size() == 1 && intersectionPoints.entrySet().iterator().next().getValue().size() == 1) {
			Map.Entry<Geometry, List<Point3D>> onlyEntry = intersectionPoints.entrySet().iterator().next();
			closest.put(onlyEntry.getKey(), onlyEntry.getValue().get(0));
			return closest;
		}

		for (Map.Entry<Geometry, List<Point3D>> entry : intersectionPoints.entrySet())
		{
			for (Point3D p : entry.getValue()) {

				double dSquare = From.distanceSquare(p);

				if (dSquare <= minDisSquare) {
					closest.clear();
					closest.put(entry.getKey(), new Point3D(p));
					minDisSquare = dSquare;
				}
			}
		}
		if (closest.size() == 1)
			return closest;
		return null;

	}

	public Scene get_scene() {
		return _scene;
	}

	public void set_scene(Scene _scene) {
		this._scene = _scene;
	}

	public ImageWriter get_imageWriter() {
		return _imageWriter;
	}

	public void set_imageWriter(ImageWriter _imageWriter) {
		this._imageWriter = _imageWriter;
	}
}
