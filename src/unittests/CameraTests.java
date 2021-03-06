package unittests;
import elements.*;
import primitives.*;
import scene.Scene;

import static org.junit.Assert.*;

import org.junit.Test;
  
public class CameraTests {

	
	@Test
	public void testConstructRay() throws Exception  {
		final int WIDTH = 3;
		final int HEIGHT = 3;
		  
		Point3D[][] screen = new Point3D [HEIGHT][WIDTH];
		
		Scene scene = new Scene("TestScene");
		System.out.println("Camera:\n" + scene.getCamera());
		
		for (int i = 0; i < HEIGHT; i++)
		{
			for (int j = 0; j < WIDTH; j++)
			{
				Ray ray = scene.getCamera().constructRayThroughPixel(WIDTH, HEIGHT, i, j, 1, 3 * WIDTH, 3 * HEIGHT);
				screen[i][j] = ray.getP3D().add(ray.getDirection().multiply(ray.getT()));
				
				//Checking z-coordinate
				if(Double.compare(screen[i][j].getZ().get(), -1.0) == 0)
			 		assertTrue(true);
				else
					fail("Wrong z coordinate" + screen[i][j].getZ().get() );
				
				// Checking all options
				//double x = screen[i][j].getX().get();
				//double y = screen[i][j].getY().get();
				
				if (screen[i][j].getX().equals(new Coordinate(3)) || screen[i][j].getX().equals(new Coordinate(0))|| screen[i][j].getX().equals(new Coordinate(-3))){
					if (screen[i][j].getY().equals(new Coordinate(3)) || screen[i][j].getY().equals(new Coordinate(0)) || screen[i][j].getY().equals(new Coordinate(-3))){
						assertTrue(true);
					}
					else
						fail("Wrong y coordinate");
				} else
					fail("Wrong x coordinate");
			}
			System.out.println("---");
		}
		
	}

}
