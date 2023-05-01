import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.io.*;
import java.net.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, ParseException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please Enter Same Date");

        System.out.println("Enter format is: yyyy-MM-dd");

        System.out.print("Enter End Date: ");
        String endDateString = scanner.next();
        System.out.print("Enter Start Date: ");
        String startDateString = scanner.next();
        System.out.println();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date endDate;
        Date startDate;

        try {
            endDate = dateFormat.parse(endDateString);
            startDate = dateFormat.parse(startDateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please enter dates in yyyy-MM-dd format.");
            return;
        }

        String urlStr = "https://seffaflik.epias.com.tr/transparency/service/market/intra-day-trade-history?endDate=";
        urlStr = urlStr.concat(endDateString);
        urlStr = urlStr.concat("&");
        urlStr = urlStr.concat("startDate=");
        urlStr = urlStr.concat(startDateString);

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/xml");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String xml = "";
        String line;
        while ((line = br.readLine()) != null) {
            xml += line;
        }
        br.close();
        conn.disconnect();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));

        document.getDocumentElement().normalize();

        NodeList nodeList = document.getElementsByTagName("intraDayTradeHistoryList");

        ArrayList<Trade> trades = new ArrayList<Trade>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getElementsByTagName("id").item(0).getTextContent();
                String date = element.getElementsByTagName("date").item(0).getTextContent();
                String contract = element.getElementsByTagName("conract").item(0).getTextContent();
                String price = element.getElementsByTagName("price").item(0).getTextContent();
                String quantity = element.getElementsByTagName("quantity").item(0).getTextContent();

                Trade tempTrade = new Trade();
                tempTrade.setId(Integer.parseInt(id));
                tempTrade.setDate(date);
                tempTrade.setContract(contract);
                tempTrade.setPrice(Double.parseDouble(price));
                tempTrade.setQuantity(Integer.parseInt(quantity));

                trades.add(tempTrade);
            }
        }

        ArrayList<Table> tables = new ArrayList<Table>();

        for (int i = 0; i < 24; i++) {
            Table table = new Table();
            table.setWeightedAveragePrice(0);
            table.setTotalTransactionAmount(0);
            table.setTotalTransactionQuantity(0);

            tables.add(table);
        }

        for (Trade trade : trades) {
            if (trade.getContract().contains("PB")) {
                continue;
            } else {
                String contract = trade.getContract();
                String contractSub = contract.substring(2, 10);
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyMMddHH");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date = inputFormat.parse(contractSub);
                String formattedDate = outputFormat.format(date);

                int index = calculateIndex(contract.substring(8, 10));

                if (tables.size() > index && tables.get(index) != null) {
                    double totalTransactionAmount = tables.get(index).getTotalTransactionAmount();
                    double totalTransactionQuantity = tables.get(index).getTotalTransactionQuantity();
                    double weightedAveragePrice = tables.get(index).getWeightedAveragePrice();

                    totalTransactionAmount += (trade.getPrice() * trade.getQuantity()) / 10;
                    tables.get(index).setTotalTransactionAmount(totalTransactionAmount);

                    totalTransactionQuantity += (double) trade.getQuantity() / 10;
                    tables.get(index).setTotalTransactionQuantity(totalTransactionQuantity);

                    weightedAveragePrice = totalTransactionAmount / totalTransactionQuantity;
                    tables.get(index).setWeightedAveragePrice(weightedAveragePrice);

                    if (tables.get(index).getDate() == null) {
                        tables.get(index).setDate(formattedDate);
                    }
                }


            }
        }

        for (Table table : tables) {
            System.out.println(table.toString());
            System.out.println("--------------------");
        }

    }

    public static int calculateIndex(String contractHour) {
        return switch (contractHour) {
            case "00" -> 0;
            case "01" -> 1;
            case "02" -> 2;
            case "03" -> 3;
            case "04" -> 4;
            case "05" -> 5;
            case "06" -> 6;
            case "07" -> 7;
            case "08" -> 8;
            case "09" -> 9;
            case "10" -> 10;
            case "11" -> 11;
            case "12" -> 12;
            case "13" -> 13;
            case "14" -> 14;
            case "15" -> 15;
            case "16" -> 16;
            case "17" -> 17;
            case "18" -> 18;
            case "19" -> 19;
            case "20" -> 20;
            case "21" -> 21;
            case "22" -> 22;
            case "23" -> 23;
            default -> -1;
        };
    }
}
