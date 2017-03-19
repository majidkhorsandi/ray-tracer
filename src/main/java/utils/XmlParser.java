package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import bitmap.Bitmap;
import domain.*;
import materials.BaseMaterial;
import materials.SolidMaterial;
import materials.Texture;
import materials.TextureMaterial;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import scene.Camera;
import scene.World;
import shapes.*;

public class XmlParser {
    private transient Document document;

    /**
     * this methods gets path of a XML scene file and parses scene elements into
     * corresponding tracer.
     * @param filePath
     * @return an object of type scene.
     */
    public Scene parseXmlFile(final String filePath){
        //get the factory
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Scene scene;

        try {

            //Using factory get an instance of document builder
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            document = documentBuilder.parse(filePath);

            try {
                scene = parseScene();
            } catch (Exception e) {
                 throw new RuntimeException(e.getMessage());
            }

        }catch(ParserConfigurationException pce) {
            throw new RuntimeException(pce.getMessage());
        }catch(SAXException se) {
            throw new RuntimeException(se.getMessage());
        }catch(IOException ioe) {
            throw new RuntimeException(ioe.getMessage());
        }
        return scene;
    }

    /**
     * this method does actual parsing
     * @return a scene object.
     * @throws Exception if number of objects in a scene exceeds 20 parsing will stop.
     */
    private Scene parseScene() {
        //get the root element
        final Element docElement = document.getDocumentElement();

        Camera camera = parseCamera((Element) docElement.getElementsByTagName("camera").item(0));
        Color backgroundColor = parseBackground((Element) docElement.getElementsByTagName("background").item(0));
        World world = parseWorld((Element) docElement.getElementsByTagName("world").item(0));
        return new Scene(camera, backgroundColor, world);
    }

    private Camera parseCamera(final Element camElement) {
        Vector location;
        Vector lookAt;
        Vector upside;

        // here we extract location vector for camera.
        Element locationElement =(Element) camElement.getElementsByTagName("location").item(0);
        location = parseVector((Element) locationElement.getElementsByTagName("vector").item(0));

        //then extract sky vector.
        Element skyElement =(Element) camElement.getElementsByTagName("sky").item(0);
        upside = parseVector((Element) skyElement.getElementsByTagName("vector").item(0));

        //then extract lookAt vector.
        Element lookAtElement =(Element) camElement.getElementsByTagName("look_at").item(0);
        lookAt = parseVector((Element) lookAtElement.getElementsByTagName("vector").item(0));

        //build camera object
        return new Camera(location, lookAt, upside);
    }

    private Color parseBackground(final Element backgroundElement) {
        return parseColor((Element) backgroundElement.getElementsByTagName("color").item(0));
    }

    private World parseWorld(Element worldElement) {
        final NodeList spheres = worldElement.getElementsByTagName("sphere");
        final NodeList planes = worldElement.getElementsByTagName("plane");
        final NodeList triangles = worldElement.getElementsByTagName("triangle");
        final NodeList lamps = worldElement.getElementsByTagName("light");

        Shapes shapes = new Shapes();
        ArrayList<Light> lights = new ArrayList<>();

        IntStream.range(0, planes.getLength()).forEach(i -> {
            try {
                shapes.add(parsePlane((Element) planes.item(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        IntStream.range(0, spheres.getLength()).forEach(i -> shapes.add(parseSphere((Element) spheres.item(i))));
        IntStream.range(0, triangles.getLength()).forEach(i -> shapes.add(parseTriangle((Element) triangles.item(i))));
        IntStream.range(0, lamps.getLength()).forEach(i -> lights.add(getLight((Element) lamps.item(i))));

        return new World(shapes, lights);
    }


    private PlaneShape parsePlane(final Element planeElement) throws Exception {
        Vector normal;
        Vector point;
        double distance;
        BaseMaterial material;

        final Element materialElement =(Element)planeElement.getElementsByTagName("surface").item(0);
        material = getMaterial(materialElement);

        final Element normalElement =(Element)planeElement.getElementsByTagName("normal").item(0);
        normal = parseVector((Element)normalElement.getElementsByTagName("vector").item(0));

        if (!planeElement.getAttribute("distance").isEmpty()) {
            distance = Double.parseDouble(planeElement.getAttribute("distance"));
            return new PlaneShape(distance, normal, material);
        } else {
            final Element pointElement = (Element) planeElement.getElementsByTagName("point").item(0);
            point = parseVector((Element)pointElement.getElementsByTagName("vector").item(0));
            return new PlaneShape(point, normal, material);
        }
    }

    /**
     * this methods gets a SphereElement element, parse it and returns a corresponding
     * Sphere object.
     * @param SphereElement
     * @return Sphere object
     * @throws Exception
     */
    private SphereShape parseSphere(final Element SphereElement) {
        double radius;
        Vector position;
        BaseMaterial material;

        radius = Double.parseDouble(SphereElement.getAttribute("radius"));

        final Element locElement =(Element)SphereElement.getElementsByTagName("location").item(0);
        final Element elm = (Element)locElement.getElementsByTagName("vector").item(0);
        position = parseVector(elm);

        final Element materialElement =(Element)SphereElement.getElementsByTagName("surface").item(0);
        material = getMaterial(materialElement);

        return new SphereShape(position, radius, material);
    }

    private TriangleShape parseTriangle(final Element triangleElement) {
        Vector c0Vect;
        Vector c1Vect;
        Vector c2Vect;
        BaseMaterial material;
        boolean isCorner = true;

        final Element c0Element =(Element)triangleElement.getElementsByTagName("c0").item(0);
        Element elm = (Element)c0Element.getElementsByTagName("vector").item(0);
        c0Vect = parseVector(elm);

        final Element c1Element =(Element)triangleElement.getElementsByTagName("c1").item(0);
        elm = (Element)c1Element.getElementsByTagName("vector").item(0);
        c1Vect = parseVector(elm);

        final Element c2Element =(Element)triangleElement.getElementsByTagName("c2").item(0);
        elm = (Element)c2Element.getElementsByTagName("vector").item(0);
        c2Vect = parseVector(elm);

        final Element v1Element =(Element)triangleElement.getElementsByTagName("v1").item(0);
        if (v1Element != null){
            elm = (Element)v1Element.getElementsByTagName("vector").item(0);
            isCorner = false;
            c1Vect = parseVector(elm);
        }

        final Element v2Element =(Element)triangleElement.getElementsByTagName("v2").item(0);
        if (v2Element != null){
            elm = (Element)v2Element.getElementsByTagName("vector").item(0);
            c2Vect = parseVector(elm);
        }

        final Element materialElement = (Element)triangleElement.getElementsByTagName("surface").item(0);
                
        if (isCorner == false) {
            c1Vect = TriangleShape.triangleComputeCorner(c0Vect, c1Vect);
            c2Vect = TriangleShape.triangleComputeCorner(c0Vect, c2Vect);
        }

        material = getMaterial(materialElement);
        return new TriangleShape(c0Vect, c1Vect, c2Vect, material);
    }

    private BaseMaterial getMaterial(final Element materialElement) {
        BaseMaterial material = null;
        Texture texture;
        final Bitmap bmp= Bitmap.createNewBitmap(0, 0);
        double diffuse = 1;
        double reflection = 0;

        // Parse diffuse and reflection
        if (materialElement.getElementsByTagName("finish").getLength()> 0){
            final Element elm= (Element) materialElement.getElementsByTagName("finish").item(0);
            reflection = (elm.getAttribute("reflect").toString().isEmpty()) ? reflection : Double.parseDouble(elm.getAttribute("reflect"));
            diffuse = (elm.getAttribute("diffuse").toString().isEmpty()) ? diffuse : Double.parseDouble(elm.getAttribute("diffuse"));
        }

        if (materialElement.getElementsByTagName("color").getLength()> 0){
            final Element elm = (Element) materialElement.getElementsByTagName("color").item(0) ;
            material = new SolidMaterial(parseColor(elm), reflection, diffuse);
        } else {
            final Element elm = (Element) materialElement.getElementsByTagName("ppm").item(0) ;
            final String bitmapFile = elm.getAttribute("file").toString();
            try {
                bmp.createBitmapFromFile(bitmapFile);
                texture = Texture.fromBitmap(bmp);
                material = new TextureMaterial(texture, reflection, diffuse);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return material;
    }

    /** this methods gets a docElement element, parse it and returns a 
     * material's color object.
     * @param docElement
     * @return color object
     * @throws Exception
     */
    private Color parseColor(final Element docElement) {
        int red =(int) (Double.parseDouble(docElement.getAttribute("red")) * 255);
        int green =(int) (Double.parseDouble(docElement.getAttribute("green")) * 255);
        int blue = (int) (Double.parseDouble(docElement.getAttribute("blue")) * 255);

        return new Color(red, green, blue);
    }

    private Double getReflection(final Element docElement){
        double ref;
        ref = Double.parseDouble(docElement.getAttribute("reflect").toString());
        return ref;
    }

    /**
     * this methods gets a lightElement element, parse it and returns a corresponding
     * light object.
     * @param lightElement
     * @return light object
     */
    private Light getLight(final Element lightElement) {
        Light light;
        final Element posElement =(Element) lightElement.getElementsByTagName("position").item(0);
        final Element elm = (Element)posElement.getElementsByTagName("vector").item(0);
        light = new Light(parseVector(elm));
        return light;
    }

    private Vector parseVector(final Element vectorElement) {
        return new Vector(
                Double.parseDouble(vectorElement.getAttribute("x")),
                Double.parseDouble(vectorElement.getAttribute("y")),
                Double.parseDouble(vectorElement.getAttribute("z")));
    }
}