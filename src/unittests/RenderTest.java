package unittests;

import java.util.Date;

import org.junit.Test;

import elements.*;
import geometries.*;
import primitives.*;
import renderer.ImageWriter;
import renderer.Render;
import scene.Scene;

public class RenderTest {
	/*@Test
	public void basicRenderingTest(){
			int X = 500;
			int Y = 500;
			Date d = new Date();
			ImageWriter _imageWriter = new ImageWriter("basicRenderingTest", X, Y, X, Y);
			Scene _scene = new Scene("s");
			_scene.setBackground(new Color(0,0,0));
			_scene.setCamera(new Camera(new Point3D(0,0,0), new Vector(0,1,0), new Vector(0,0,-1)));
			_scene.setAmbientLight(new AmbientLight(new Color(50,50,50), 1));
						
			_scene.addGeometry(new Sphere(2, new Point3D(0, 0, -4), new Color(0,200,0), new Material(1,1,1)));
		  	_scene.addGeometry(new Triangle(new Point3D(0, 4, -4), new Point3D(4, 0, -4),new Point3D(4, 4, -4), new Color(200,0,0), new Material(1,1,1)));
		  	_scene.addGeometry(new Triangle(new Point3D(0, 4, -4), new Point3D(-4, 0, -4),new Point3D(-4, 4, -4), new Color(0,0,200), new Material(1,1,1)));
		  	_scene.addGeometry(new Triangle(new Point3D(0, -4, -4), new Point3D(-4, 0, -4),new Point3D(-4, -4, -4), new Color(75,35,75), new Material(1,1,1)));
		  	_scene.addGeometry(new Triangle(new Point3D(0, -4, -4), new Point3D(4, 0, -4),new Point3D(4, -4, -4), new Color(30,100,30), new Material(1,1,1)));
			
			Render r = new Render(_imageWriter,_scene);

		//	r.renderImage();
		//	r.printGrid(10);
		//	r.writeToImage();
			Date d2 = new Date();
			System.out.println(d2.getTime() - d.getTime());
			System.out.println("Finish");
	
	}
	*/
	
	
	
	
}