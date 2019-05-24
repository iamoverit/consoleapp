import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class ConsoleApp {
    private static Map<ItemKey, Integer> itemHashMap = new HashMap<>();
    private static Map<CityFloorKey, Integer> cityFloorHashMap = new HashMap<>();

    static class CityFloorKey {
        private String city;
        private String floor;

        CityFloorKey(String city, String floor) {
            this.city = city;
            this.floor = floor;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof CityFloorKey) {
                CityFloorKey s = (CityFloorKey) obj;
                return city.equals(s.city) && floor.equals(s.floor);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (city + "." + floor).hashCode();
        }
    }

    static class ItemKey {
        private String city;
        private String street;
        private String house;
        private String floor;

        ItemKey(String city, String street, String house, String floor) {
            this.city = city;
            this.street = street;
            this.house = house;
            this.floor = floor;
        }

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

        @Override
        public String toString() {
            return "item [city=\"" + city + "\" street=\"" + street + "\" house=\"" + house + "\" floor=\"" + floor + "\"]";
        }
    }

    static public void main(String[] args) throws XMLStreamException {
        if (args.length == 0) {
            System.err.println("Please specify path to file as first argument.");
        } else {
            processFile(args[0]);

            Map<ItemKey, Integer> duplicates = filterByValue(itemHashMap, x -> (x > 1));
            if (duplicates.isEmpty()) {
                System.out.println("Duplicates not found.");
            } else {
                System.out.println("Duplicates:");
                duplicates.forEach((k, v) -> {
                    System.out.println(k.toString() + " occurs " + v + " times");
                });
            }
            if (cityFloorHashMap.isEmpty()) {
                System.out.println("Buildings with storeys not found.");
            } else {
                System.out.println("How many n-storey buildings in each city:");
                cityFloorHashMap.forEach((k, v) -> System.out.println("City: " + k.city + " Floors: " + k.floor + " = " + v + " count"));
            }
        }
    }

    private static void processFile(String arg) throws XMLStreamException {
        File file = new File(arg);
        if (file.exists()) {
            try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get(file.getPath())), System.out)) {
                XMLStreamReader reader = processor.getReader();

                while (reader.hasNext()) {
                    int event = reader.next();
                    if (event == XMLEvent.START_ELEMENT &&
                            "item".equals(reader.getLocalName())) {

                        ItemKey itemKey = new ItemKey(
                                reader.getAttributeValue(null, "city"),
                                reader.getAttributeValue(null, "street"),
                                reader.getAttributeValue(null, "house"),
                                reader.getAttributeValue(null, "floor")
                        );

                        int count = itemHashMap.getOrDefault(itemKey, 0);
                        itemHashMap.put(itemKey, ++count);

                        if (count == 1) {
                            CityFloorKey cityFloorKey = new CityFloorKey(
                                    reader.getAttributeValue(null, "city"),
                                    reader.getAttributeValue(null, "floor")
                            );
                            int floorsCount = cityFloorHashMap.getOrDefault(cityFloorKey, 0);
                            cityFloorHashMap.put(cityFloorKey, ++floorsCount);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMLStreamException e) {
                throw new IllegalArgumentException(arg.concat(" > is invalid XML file."));
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

