import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CastsParser {

    List<MovieStar> mStars = new ArrayList<>();
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
            dom = documentBuilder.parse("stanford-movies/casts124.xml");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        NodeList nodeList = documentElement.getElementsByTagName("filmc");
        for (int i = 0; i < nodeList.getLength(); i++) {

            // get the dirname element
            Element element = (Element) nodeList.item(i);
            NodeList mList = element.getElementsByTagName("m");
            // System.out.println(mList);
            // System.out.println("length: " + mList.getLength());
            for (int j = 0; j < mList.getLength(); j++) {

                // get the film element
                Element mElement = (Element) mList.item(j);
                // get the movie object
                MovieStar mStar = parseStar(mElement);
                // add it to list
                mStars.add(mStar);
            }
        }
    }

    /**
     * It takes an employee Element, reads the values in, creates
     * an Employee object for return
     */
    private MovieStar parseStar(Element element) {

        // for each <employee> element get text or int values of
        // name ,id, age and name
        int birthyear = 0;
        String name = "";
        String mid = "";
        String title = "";

        title = getTextValue(element, "t");
        mid = getTextValue(element, "f");
        name = getTextValue(element, "a");

        //handle name =='sa' (some actor)
        //ignore it
        return new MovieStar(mid, name, title);
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
            return 1900;
        }
    }

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {

        System.out.println("Total parsed " + mStars.size() + " stars");

        for (MovieStar mStars : mStars) {
            System.out.println("\t" + mStars.toString());
        }
    }

    public List<MovieStar> getCast() {return mStars;}

//    public static void main(String[] args) {
//        // create an instance
//        CastsParser domParser = new CastsParser();
//
//        // call run example
//        domParser.runParser();
//    }

}
