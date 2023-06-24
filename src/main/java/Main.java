import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException,
            IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
//        String json = listToJson(list);
//        writeString(json, "data.json");
        writeString(listToJson(list), "data.json");
        List<Employee> list2 = parseXML("data.xml");
        writeString(listToJson(list2), "data2.json");
        String json3 = readString("new_data.json");
        List<Employee> list3 = jsonToList(json3);
        list3.forEach(System.out::println);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException,
            IOException, SAXException {
        List<Employee> employeeList = new ArrayList<>();
        long id = 0;
        String firstName = null;
        String lastName = null;
        String country = null;
        int age = 0;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        Node root = doc.getDocumentElement();
        //System.out.println("Корневой элемент: " + root.getNodeName());
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            int cnt = 0;
            boolean a = true;
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                while (cnt < 6) {
                    //System.out.println("Текущий узел: " + node_.getNodeName());
                    NodeList nodeList2 = node_.getChildNodes();
                    for (int j = 0; j < nodeList2.getLength(); j++) {
                        Node node2_ = nodeList2.item(j);
                        if (Node.ELEMENT_NODE == node2_.getNodeType()) {
                            //System.out.println("Текущий узел2: " + node2_.getNodeName());
                            Element element = (Element) node2_;
                            String value = element.getTextContent();
                            //System.out.println("Текстовое значение: " + value);
                            cnt++;
                            switch (cnt) {
                                case 1:
                                    id = Integer.parseInt(value);
                                    break;
                                case 2:
                                    firstName = value;
                                    break;
                                case 3:
                                    lastName = value;
                                    break;
                                case 4:
                                    country = value;
                                    break;
                                case 5:
                                    age = Integer.parseInt(value);
                                    cnt++;
                                    break;
                                default:
                                    System.out.println("Ошибка");
                                    break;
                            }
                        }
                    }
                    Employee employee = new Employee(id, firstName, lastName, country, age);
                    employeeList.add(employee);
                }
            }
        }
        return employeeList;
    }

//    // Два метода для parseXML с рекурсией
//    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException,
//            IOException, SAXException {
//        List<Employee> employeeList = new ArrayList<>();
//
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        Document doc = builder.parse(new File(fileName));
//        Node root = doc.getDocumentElement();
//        //System.out.println("Корневой элемент: " + root.getNodeName());
//
//        read(root, employeeList);
//
//        return employeeList;
//    }
//
//    public static void read(Node node, List<Employee> employeeList) {
//        long id = 0;
//        String firstName = null;
//        String lastName = null;
//        String country = null;
//        int age = 0;
//        NodeList nodeList = node.getChildNodes();
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            Node node_ = nodeList.item(i);
//            if (Node.ELEMENT_NODE == node_.getNodeType()) {
//                //System.out.println("Текущий узел: " + node_.getNodeName());
//                Element element = (Element) node_;
//                String value = element.getTextContent();
//                //System.out.println("Текстовое значение: " + value);
//                if ("id".equals(node_.getNodeName())) {
//                    id = Integer.parseInt(value);
//                }
//                if ("firstName".equals(node_.getNodeName())) {
//                    firstName = value;
//                }
//                if ("lastName".equals(node_.getNodeName())) {
//                    lastName = value;
//                }
//                if ("country".equals(node_.getNodeName())) {
//                    country = value;
//                }
//                if ("age".equals(node_.getNodeName())) {
//                    age = Integer.parseInt(value);
//                }
//                if (id != 0 & firstName != null & lastName != null & country != null & age != 0) {
//                    Employee employee = new Employee(id, firstName, lastName, country, age);
//                    employeeList.add(employee);
//                }
//            }
//            read(node_, employeeList);
//        }
//    }

    public static String readString(String fileName) {
        String s;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return sb.toString();
    }

    public static List<Employee> jsonToList(String json) {
        List<Employee> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(json);
            JSONArray jArr = (JSONArray) obj;
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (Object employeeObject : jArr) {
                String employeeObjectStr = employeeObject.toString();
                Employee employee = gson.fromJson(employeeObjectStr, Employee.class);
                list.add(employee);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }
}



