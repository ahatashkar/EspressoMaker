import Utils.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

public class ManifestAnalyzer {

    public String getPackageName(File manifest) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            SAXParser saxParser = factory.newSAXParser();
            ManifestHandler handler = new ManifestHandler();
            saxParser.parse(manifest, handler);
            return handler.getPackageName();

        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
}

class ManifestHandler extends DefaultHandler {
    private String applicationName;
    private String packageName;

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (localName.equals("manifest")) {
            packageName = atts.getValue("package");
        } else if (localName.equals("application")) {
            applicationName = atts.getValue("android:name");
        }
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getPackageName() {
        return packageName;
    }

}
