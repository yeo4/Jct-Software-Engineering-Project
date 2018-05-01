package unittests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import elements.Camera;
import geometries.Cylinder;
import geometries.Tube;
import primitives.Point3D;
import primitives.Ray;
import primitives.Vector;

public class CylinderTests {

	@Test
	public void testIntersectionPoints() throws Exception {
		  final int WIDTH = 3;
		  final int HEIGHT = 3;
		  Ray[][] rays = new Ray[HEIGHT][WIDTH];
		  Camera camera = new Camera(new Point3D(0.0, 0.0, 0.0),
		   new Vector(0.0, 1.0, 0.0),
		   new Vector(0.0, 0.0, -1.0));
		  
		  Cylinder c = new Cylinder(15,new Point3D(0.0, 0.0, 0),new Vector(0,0,1),5);
		  //only one ray intersects and it goes through both caps
		  Cylinder c1 = new Cylinder(1,new Point3D(-4.5, 0.0, -1.5),new Vector(-3,0,-1),3);
		  //special case test: ray intersects between cylinder and cap
		  Cylinder c2 = new Cylinder(3,new Point3D(0.0, 0.0, 0),new Vector(0,0,1),2);
		  //special case: middle ray is encompassed in side of cylinder 
		  //so ZERO intersection points are expected (because no other rays intersect the cylinder)
		  Cylinder c3 = new Cylinder(1, new Point3D(1, 0, -6), new Vector(0,0,1), 2);
		  Cylinder c4 = new Cylinder(1, new Point3D(1, 0, -8), new Vector(1,0,0),2);

		  List <Point3D> intersectionPointsC = new ArrayList <Point3D> ();
		  List <Point3D> intersectionPointsC1 = new ArrayList <Point3D> ();
		  List <Point3D> intersectionPointsC2 = new ArrayList <Point3D> ();
		  List <Point3D> intersectionPointsC3 = new ArrayList <Point3D> ();
		  List <Point3D> intersectionPointsC4 = new ArrayList <Point3D> ();


		  
		  System.out.println("Camera:\n " + camera);
		    for (int i = 0; i < HEIGHT; i++) {
			     for (int j = 0; j < WIDTH; j++) {
				      rays[i][j] = camera.constructRayThroughPixel(
				       WIDTH, HEIGHT, j, i, 1, 3 * WIDTH, 3 * HEIGHT);
				      
				      List < Point3D > rayIntersectionPoints = c.findIntersections(rays[i][j]);
				      List < Point3D > rayIntersectionPoints1 = c1.findIntersections(rays[i][j]);
				      List < Point3D > rayIntersectionPoints2 = c2.findIntersections(rays[i][j]);
				      List < Point3D > rayIntersectionPoints3 = c3.findIntersections(rays[i][j]);
				      List < Point3D > rayIntersectionPoints4 = c4.findIntersections(rays[i][j]);

				      
				      for (Point3D iPoint: rayIntersectionPoints)
					       intersectionPointsC.add(iPoint);
				      for (Point3D iPoint: rayIntersectionPoints1)
					       intersectionPointsC1.add(iPoint);
				      for (Point3D iPoint: rayIntersectionPoints2)
					       intersectionPointsC2.add(iPoint);
				      for (Point3D iPoint: rayIntersectionPoints3)
					       intersectionPointsC3.add(iPoint);
				      for (Point3D iPoint: rayIntersectionPoints4)
					       intersectionPointsC4.add(iPoint);
			     }
		    }

		    assertTrue(intersectionPointsC.size() == 9);
		    assertTrue(intersectionPointsC1.size() == 2);
		    assertTrue(intersectionPointsC2.size() == 9);
			assertTrue(intersectionPointsC3.size() == 0);
			assertTrue(intersectionPointsC4.size() == 2);

	}

}


