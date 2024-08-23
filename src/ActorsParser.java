import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ActorsParser {

    List<Star> stars = new ArrayList<>();
    Document dom;

    public void runParser() {

        System.setProperty("file.encoding", "ISO-8859-1");
        // parse the xml file and get the dom object
        parseXmlFile();

        // get each directionFilm element and create a Movie object
        parseDocument();

        // iterate through the list and print the data (good for testing)
        //printData();

    }

    private void parseXmlFile() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse("stanford-movies/actors63.xml");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        NodeList nodeList = documentElement.getElementsByTagName("actor");
        for (int i = 0; i < nodeList.getLength(); i++) {


            Element element = (Element) nodeList.item(i);
            Star star = parseStar(element);

            stars.add(star);
        }
    }

    /**
     * It takes an employee Element, reads the values in, creates
     * an Employee object for return
     */
    private Star parseStar(Element element) {

        int birthyear = 0;
        String name = "";
        String id = "";

        name = getTextValue(element, "stagename");
        birthyear = getIntValue(element, "dob");
        id = UUID.randomUUID().toString().substring(0,8) + name.substring(0,1);

        //handle name =='sa' (some actor)
        //ignore it

        return new Star(id, name, birthyear);
    }

    /**
     * It takes an XML element and the tag name, look for the tag and get
     * the text content
     * i.e for <Employee><Name>John</Name></Employee> xml snippet if
     * the Element points to employee node and tagName is name it will return John
     */
    private String getTextValue(Element element, String tagName) {
        String textVal = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            // here we expect only one <Name> would present in the <Employee>
            if (nodeList.item(0).getFirstChild() != null) {
                textVal = nodeList.item(0).getFirstChild().getNodeValue();
            }
            else {
                textVal = "n/a";
            }
        }
        return textVal;
    }

    /**
     * Calls getTextValue and returns a int value
     */
    private int getIntValue(Element ele, String tagName) {
        // in production application you would catch the exception
        try {
            return Integer.parseInt(getTextValue(ele, tagName));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {

        System.out.println("Total parsed " + stars.size() + " stars");

        for (Star stars : stars) {
            System.out.println("\t" + stars.toString());
        }
    }

    public List<Star> getStars() {
        return stars;
    }

//    public static void main(String[] args) {
//        ActorsParser domParser = new ActorsParser();
//        domParser.runParser();
//    }

}
