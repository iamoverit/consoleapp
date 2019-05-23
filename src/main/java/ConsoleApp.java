import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class ConsoleApp {
    static class ItemKey {
        private String city;
        private String street;
        private String house;
        private String floor;

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof ItemKey) {
                ItemKey s = (ItemKey) obj;
                return city.equals(s.city) && street.equals(s.street) && house.equals(s.house) && floor.equals(s.floor);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (city + "." + street + "." + house + "." + floor).hashCode();
        }
    }

    //    private static List<String> cityList = new ArrayList<>();
    private static Map<ItemKey, Integer> cityHashMap = new HashMap<>();

    static public void main(String[] args) throws XMLStreamException, UnsupportedEncodingException {
        if (args.length == 0) {
            System.err.println("Please specify path to file as first argument.");
        } else {
            processFile(args[0]);
        }
    }

    private static void processFile(String arg) throws XMLStreamException, UnsupportedEncodingException {
        PrintStream ps = new PrintStream(System.out, true, "cp866");
        File file = new File(arg);
        if (file.exists()) {
            try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get(file.getPath())))) {
                XMLStreamReader reader = processor.getReader();
                while (reader.hasNext()) {
                    int event = reader.next();
                    if (event == XMLEvent.START_ELEMENT &&
                            "item".equals(reader.getLocalName())) {

                        ItemKey itemKey = new ItemKey();
                        itemKey.city = reader.getAttributeValue(null, "city");
                        itemKey.street = reader.getAttributeValue(null, "street");
                        itemKey.house = reader.getAttributeValue(null, "house");
                        itemKey.floor = reader.getAttributeValue(null, "floor");

                        int count = cityHashMap.getOrDefault(itemKey, 0);
                        cityHashMap.put(itemKey, ++count);
                        if (count == 2) {
                            System.out.println(reader.getElementText());
                        }
                    }
                }
//                Map<ItemKey, Integer> dublicates = filterByValue(cityHashMap, x -> (x > 1));
                int x = 10;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            throw new IllegalArgumentException(arg.concat(" > is not valid file resource."));
        }
    }

    public static <K, V> Map<K, V> filterByValue(Map<K, V> map, Predicate<V> predicate) {
        return map.entrySet()
                .stream()
                .filter(x -> predicate.test(x.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

