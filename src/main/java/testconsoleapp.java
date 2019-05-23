import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class testconsoleapp {
    static public void main(String[] args) throws IOException, XMLStreamException {
        if (args.length == 0) {
            System.err.println("Please specify path to file as first argument.");
        }
        File file = new File(args[0]);
        if (file.exists()) {
            try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get(file.getPath())))) {
                XMLStreamReader reader = processor.getReader();
                while (reader.hasNext()) {       // while not end of XML
                    int event = reader.next();   // read next event
                    if (event == XMLEvent.START_ELEMENT &&
                            "item".equals(reader.getLocalName())) {

                        System.out.println(reader.getAttributeValue("", "city"));
                    }
                }
            }

        } else {
            throw new IllegalArgumentException(args[0].concat(" > is not valid file resource."));
        }

    }
}

