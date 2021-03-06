package unittests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import elements.*;
import geometries.Geometry;
import geometries.Plane;
import primitives.*;

public class PlaneTests {
	/*** Plane test 
	 * @throws Exception ***/
	@Test
	public void testIntersectionPoints() throws Exception{
		final int WIDTH = 3;
		final int HEIGHT = 3;
		Ray[][] rays = new Ray[HEIGHT][WIDTH];
		Camera camera = new Camera(new Point3D(0.0, 0.0, 0.0),
		 new Vector(0.0, 1.0, 0.0),
		 new Vector(0.0, 0.0, -1.0));
		// plane orthogonal to the view plane
		Plane plane = new Plane(new Point3D(0.0, 0.0, -3.0), new Vector(0.0, 0.0, -1.0), new Color(0,0,0), new Material(1,1,1,0,0));
		// 45 degrees to the view plane
		Plane plane2 = new Plane(new Point3D(0.0, 0.0, -3.0), new Vector(0.0, 0.25, -1.0), new Color(0,0,0), new Material(1,1,1,0,0));
		// special case: all points intersect at cameras point so i expect ZERO intersection points
		Plane plane3 = new Plane(new Point3D(0.0, 0.0, 0), new Vector(0.0, 0, -1.0), new Color(0,0,0), new Material(1,1,1,0,0));
		// rays either intersect at cameras point or are encompassed by the plane so i expect ZERO intersection points 
		Plane plane4 = new Plane(new Point3D(0.0, 0.0, 0), new Vector(0.0, 1, 0), new Color(0,0,0), new Material(1,1,1,0,0));
		List < Point3D > intersectionPointsPlane = new ArrayList < Point3D > ();
		List < Point3D > intersectionPointsPlane2 = new ArrayList < Point3D > ();
		List < Point3D > intersectionPointsPlane3 = new ArrayList < Point3D > ();
		List < Point3D > intersectionPointsPlane4 = new ArrayList < Point3D > ();


		System.out.println("Camera: \n" + camera);
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0; j < WIDTH; j++) {
				  rays[i][j] = camera.constructRayThroughPixel(WIDTH, HEIGHT, j, i, 1, 3 * WIDTH, 3 * HEIGHT);
				  Map<Geometry, List<Point3D>> rayIntersectionPoints = plane.findIntersections(rays[i][j]);
				  Map<Geometry, List<Point3D>> rayIntersectionPoints2 = plane2.findIntersections(rays[i][j]);
				  Map<Geometry, List<Point3D>> rayIntersectionPoints3 = plane3.findIntersections(rays[i][j]);
				  Map<Geometry, List<Point3D>> rayIntersectionPoints4 = plane4.findIntersections(rays[i][j]);
		
				  if(rayIntersectionPoints.size() !=0)
					  for (Point3D iPoint: rayIntersectionPoints.get(plane))
						  intersectionPointsPlane.add(iPoint);
				  if(rayIntersectionPoints2.size() !=0)
					  for (Point3D iPoint: rayIntersectionPoints2.get(plane2))
						  intersectionPointsPlane2.add(iPoint);
				  if(rayIntersectionPoints3.size() !=0)
					  for (Point3D iPoint: rayIntersectionPoints3.get(plane3))
						   intersectionPointsPlane3.add(iPoint);
				  if(rayIntersectionPoints4.size() !=0)
					  for (Point3D iPoint: rayIntersectionPoints4.get(plane4))
						   intersectionPointsPlane4.add(iPoint);
			 }
		}
		
		
		assertTrue(intersectionPointsPlane.size() == 9);
		assertTrue(intersectionPointsPlane2.size() == 9);
		assertTrue(intersectionPointsPlane3.size() == 0);
		assertTrue(intersectionPointsPlane4.size() == 0);

		}
}
