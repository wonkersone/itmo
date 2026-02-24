package fileManagers;

import mainClasses.*;
import managers.CollectionManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.Date;

/**
 * Класс для парсинга коллекции работников из XML файла
 * Обеспечивает чтение и преобразование XML-документа в коллекцию объектов Worker
 */
public class CollectionParser {
    private final CollectionManager collectionManager;

    /**
     * Создает новый парсер коллекции
     * @param collectionManager менеджер коллекции для управления данными
     */
    public CollectionParser(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Читает и парсит XML файл в коллекцию работников
     * @param filePath путь к XML файлу
     * @return коллекция работников, прочитанная из файла
     */
    public ArrayDeque<Worker> parseFromFile(String filePath) {
        ArrayDeque<Worker> workers = new ArrayDeque<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); //создаем фабрику для создания парсеров
            DocumentBuilder builder = factory.newDocumentBuilder(); // создаем парсер XML
            
            // Используем InputStreamReader для чтения файла
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)) { //открываем XML файл для чтения и читаем в кодировке URF-8
                InputSource inputSource = new InputSource(reader); // оборачиваем поток в формат, понятный XML-парсеру
                Document document = builder.parse(inputSource); // разбираем XML в объект Document
                NodeList workerNodes = document.getElementsByTagName("worker"); // получаем все элементы "worker" из XML
                for (int i = 0; i < workerNodes.getLength(); i++) { // проходим по каждому узлу worker
                    Node workerNode = workerNodes.item(i);
                    if (workerNode.getNodeType() == Node.ELEMENT_NODE) { // проверяем что это действительно элемент
                        Element workerElement = (Element) workerNode;
                        Worker worker = parseWorker(workerElement); // парсим Worker
                        if (worker != null) { // если все успешно -> добавляем в коллекцию
                            workers.add(worker);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }
        return workers;
    }

    /**
     * Парсит XML элемент в объект Worker
     * @param workerElement XML элемент, содержащий данные работника
     * @return объект Worker или null в случае ошибки парсинга
     */
    private Worker parseWorker(Element workerElement) {
        try {
            Integer id = Integer.parseInt(getElementText(workerElement, "id")); // получаем id и name с помощью метода getElementText()
            String name = getElementText(workerElement, "name");
            Coordinates coordinates = parseCoordinates(workerElement.getElementsByTagName("coordinates").item(0));
            LocalDate creationDate = LocalDate.parse(getElementText(workerElement, "creationDate")); // просто парсим данные и приводим к нужному типу
            long salary = Long.parseLong(getElementText(workerElement, "salary"));
            LocalDateTime startDate = LocalDateTime.parse(getElementText(workerElement, "startDate"));
            ZonedDateTime endDate = null;
            Node endDateNode = workerElement.getElementsByTagName("endDate").item(0);
            if (endDateNode != null) { // если endDate есть, то преобразовываем его к ZonedDateTime
                endDate = ZonedDateTime.parse(endDateNode.getTextContent());
            }
            Position position = Position.valueOf(getElementText(workerElement, "position")); // просто парсим данные
            Person person = parsePerson(workerElement.getElementsByTagName("person").item(0));

            return new Worker(id, name, coordinates, creationDate, salary, startDate, endDate, position, person); // возвращаем новый экземпляр класса  Worker
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге работника: " + e.getMessage());
            return null;
        }
    }

    /**
     * Парсит XML элемент в объект Coordinates
     * @param coordinatesNode XML узел с координатами
     * @return объект Coordinates или null в случае ошибки
     */
    private Coordinates parseCoordinates(Node coordinatesNode) {
        if (coordinatesNode.getNodeType() == Node.ELEMENT_NODE) { // проверяем, что узел coordinates существует
            Element coordinatesElement = (Element) coordinatesNode;
            Double x = Double.parseDouble(getElementText(coordinatesElement, "x")); // просто парсим данные
            long y = Long.parseLong(getElementText(coordinatesElement, "y"));
            return new Coordinates(x, y); // возвращаем экземпляр класса Coordinates
        }
        return null;
    }

    /**
     * Парсит XML элемент в объект Person
     * @param personNode XML узел с данными о человеке
     * @return объект Person или null в случае ошибки или отсутствия данных
     */
    private Person parsePerson(Node personNode) {
        if (personNode == null || personNode.getNodeType() != Node.ELEMENT_NODE) { // проверяем, что узел person существует
            return null;
        }

        Element personElement = (Element) personNode;
        try {
            java.util.Date birthday = null;
            Node birthdayNode = personElement.getElementsByTagName("birthday").item(0);
            if (birthdayNode != null) { // если birthday есть, преобразуем в Date.
                long timestamp = Long.parseLong(birthdayNode.getTextContent());
                birthday = new Date(timestamp);
            }

            Float height = null;
            Node heightNode = personElement.getElementsByTagName("height").item(0); // просто парсим данные
            if (heightNode != null) {
                height = Float.parseFloat(heightNode.getTextContent());
            }

            Float weight = null;
            Node weightNode = personElement.getElementsByTagName("weight").item(0);
            if (weightNode != null) {
                weight = Float.parseFloat(weightNode.getTextContent());
            }

            return new Person(birthday, height, weight); // возвращаем экземпляр класса Person
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге данных о человеке: " + e.getMessage());
            return null;
        }
    }

    /**
     * Получает текстовое содержимое элемента по имени тега
     * @param parent родительский XML элемент
     * @param tagName имя искомого тега
     * @return текстовое содержимое элемента или null, если элемент не найден
     */
    private String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName); // Получаем список узлов с тегом tagName внутри элемента parent
        if (nodeList.getLength() > 0) { // проверяем есть ли хотя бы 1 элемент с таким тегом
            return nodeList.item(0).getTextContent(); // берем первый элемент и возвращаем его текст
        }
        return null;
    }
} 