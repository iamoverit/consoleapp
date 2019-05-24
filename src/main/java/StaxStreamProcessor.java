import javax.xml.stream.*;
import java.io.InputStream;
import java.io.OutputStream;

public class StaxStreamProcessor implements AutoCloseable {
    private static final XMLInputFactory IFACTORY = XMLInputFactory.newInstance();

    private final XMLStreamReader reader;

    public StaxStreamProcessor(InputStream is, OutputStream os) throws XMLStreamException {
        reader = IFACTORY.createXMLStreamReader(is);
    }

    public XMLStreamReader getReader() {
        return reader;
    }

    @Override
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException e) { // empty
            }
        }
    }
}
