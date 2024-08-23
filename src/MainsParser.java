import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

public class MainsParser {

    List<Movie> movies = new ArrayList<>();
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
            dom = documentBuilder.parse("stanford-movies/mains243.xml");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of directorfilm elements, parse each into Movie object
        NodeList nodeList = documentElement.getElementsByTagName("directorfilms");
        for (int i = 0; i < nodeList.getLength(); i++) {

            // get the dirname element
            Element element = (Element) nodeList.item(i);
            String director = getTextValue(element, "dirname");
            NodeList mList = element.getElementsByTagName("film");
            for (int j = 0; j < mList.getLength(); j++) {

                // get the film element
                Element mElement = (Element) mList.item(j);
                // get the movie object
                Movie movie = parseMovie(mElement, director);
                // add it to list
                movies.add(movie);
            }
        }
    }

    /**
     * It takes an employee Element, reads the values in, creates
     * an Employee object for return
     */
    private Movie parseMovie(Element element, String director) {

        int year = 0;
        String title = "";
        String id = "";
        Set<String> genres = new HashSet<>();

        year = getIntValue(element, "year");
        title = getTextValue(element, "t");
        id = getTextValue(element, "fid");
        NodeList mGenres = element.getElementsByTagName("cats");

        String g;
        for (int i = 0; i < mGenres.getLength(); i++) {
            Element el = (Element) mGenres.item(i);
            g = getTextValue(el, "cat");
            if (g != null) {
                genres.add(g);
            }
        }

        return new Movie(id, title, year, director, genres);
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
                textVal = null;
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

        System.out.println("Total parsed " + movies.size() + " movies");

        for (Movie movies : movies) {
            System.out.println("\t" + movies.toString());
        }
    }

    public List<Movie> getMovies() {return movies;}

//    public static void main(String[] args) {
//        // create an instance
//        MainsParser domParser = new MainsParser();
//
//        // call run example
//        domParser.runParser();
//    }

}