import javax.xml.stream.*;
import java.io.InputStream;
import java.io.OutputStream;

public class StaxStreamProcessor implements AutoCloseable {
    private static final XMLInputFactory IFACTORY = XMLInputFactory.newInstance();
    private static final XMLOutputFactory OFACTORY = XMLOutputFactory.newInstance();

    private final XMLStreamReader reader;
    private final XMLStreamWriter writer;

    public StaxStreamProcessor(InputStream is, OutputStream os) throws XMLStreamException {
        reader = IFACTORY.createXMLStreamReader(is);
        writer = OFACTORY.createXMLStreamWriter(os);
    }

    public XMLStreamReader getReader() {
        return reader;
    }

    public XMLStreamWriter getWriter() {
        return writer;
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
