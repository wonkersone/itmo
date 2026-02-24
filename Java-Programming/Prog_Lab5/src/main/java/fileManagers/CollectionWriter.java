package fileManagers;

import mainClasses.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayDeque;

/**
 * Класс для сохранения коллекции работников в XML файл
 * Обеспечивает сериализацию коллекции объектов Worker в формат XML
 */
public class CollectionWriter {
    /**
     * Записывает коллекцию работников в XML файл
     * @param filePath путь к файлу для сохранения
     * @param collection коллекция работников для сохранения
     */
    public void writeToFile(String filePath, ArrayDeque<Worker> collection) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder(); // создаем парсер
            Document document = builder.newDocument(); // создаем пустой XML документ

            // Создаем корневой элемент
            Element rootElement = document.createElement("workers"); // создаем корневой элемент workers
            document.appendChild(rootElement); // добавляем его в document

            // Добавляем всех работников
            for (Worker worker : collection) {
                Element workerElement = createWorkerElement(document, worker); // для каждого работника создаем XML-узел worker
                rootElement.appendChild(workerElement); // добавляем его внутрь workers
            }

            // Настраиваем форматирование XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(); // создаем Transformer для преобразования XML в текст
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // настраиваем отступы внутри XML файла
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // Записываем в файл с использованием BufferedWriter
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) { // открываем файл с BufferedWriter
                DOMSource source = new DOMSource(document); // Создаем DOMSource с XML-структурой
                StreamResult result = new StreamResult(writer); // Создаем StreamResult, указывая writer как выходной поток.
                transformer.transform(source, result); // Применяем transformer.transform(), чтобы записать XML в файл.
            }

        } catch (Exception e) {
            System.out.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    /**
     * Создает XML элемент для работника
     * @param document XML документ
     * @param worker объект работника для сериализации
     * @return XML элемент, содержащий данные работника
     */
    private Element createWorkerElement(Document document, Worker worker) {
        Element workerElement = document.createElement("worker"); // создаем XML-узел worker

        // Добавляем id
        Element idElement = document.createElement("id"); // создаем поле id
        idElement.setTextContent(String.valueOf(worker.getId())); // записываем в него id работника
        workerElement.appendChild(idElement); // добавляем в worker

        // Добавляем name
        Element nameElement = document.createElement("name"); // создаем поле name
        nameElement.setTextContent(worker.getName()); // записываем в него имя работника
        workerElement.appendChild(nameElement); // добавляем в worker

        // Добавляем coordinates
        Element coordinatesElement = document.createElement("coordinates"); // создаем поле coordinates
        Element xElement = document.createElement("x"); // внутри coordinates создаем поле X и Y
        xElement.setTextContent(String.valueOf(worker.getCoordinates().getX())); // записываем в них значения
        Element yElement = document.createElement("y");
        yElement.setTextContent(String.valueOf(worker.getCoordinates().getY()));
        coordinatesElement.appendChild(xElement); // добавляем X и Y в coordinates
        coordinatesElement.appendChild(yElement);
        workerElement.appendChild(coordinatesElement); // добавляем coordinates в worker

        // Добавляем creationDate
        Element creationDateElement = document.createElement("creationDate"); // аналогично создаем и добавляем остальные поля
        creationDateElement.setTextContent(worker.getCreationDate().toString());
        workerElement.appendChild(creationDateElement);

        // Добавляем salary
        Element salaryElement = document.createElement("salary");
        salaryElement.setTextContent(String.valueOf(worker.getSalary()));
        workerElement.appendChild(salaryElement);

        // Добавляем startDate
        Element startDateElement = document.createElement("startDate");
        startDateElement.setTextContent(worker.getStartDate().toString());
        workerElement.appendChild(startDateElement);

        // Добавляем endDate (если есть)
        if (worker.getEndDate() != null) {
            Element endDateElement = document.createElement("endDate");
            endDateElement.setTextContent(worker.getEndDate().toString());
            workerElement.appendChild(endDateElement);
        }

        // Добавляем position
        Element positionElement = document.createElement("position");
        positionElement.setTextContent(worker.getPosition().toString());
        workerElement.appendChild(positionElement);

        // Добавляем person (если есть)
        if (worker.getPerson() != null) { // если Person не null
            Element personElement = createPersonElement(document, worker.getPerson()); // создаем XML-элемент person c помощью createPersonElement()
            workerElement.appendChild(personElement); // добавляем его в worker
        }

        return workerElement;
    }

    /**
     * Создает XML элемент для данных о человеке
     * @param document XML документ
     * @param person объект с данными о человеке
     * @return XML элемент, содержащий данные о человеке
     */
    private Element createPersonElement(Document document, Person person) {
        Element personElement = document.createElement("person");

        // Добавляем birthday (если есть)
        if (person.getBirthday() != null) {
            Element birthdayElement = document.createElement("birthday");
            birthdayElement.setTextContent(String.valueOf(person.getBirthday().getTime()));
            personElement.appendChild(birthdayElement);
        }

        // Добавляем height (если есть)
        if (person.getHeight() != null) {
            Element heightElement = document.createElement("height");
            heightElement.setTextContent(String.valueOf(person.getHeight()));
            personElement.appendChild(heightElement);
        }

        // Добавляем weight (если есть)
        if (person.getWeight() != null) {
            Element weightElement = document.createElement("weight");
            weightElement.setTextContent(String.valueOf(person.getWeight()));
            personElement.appendChild(weightElement);
        }

        return personElement;
    }
} 