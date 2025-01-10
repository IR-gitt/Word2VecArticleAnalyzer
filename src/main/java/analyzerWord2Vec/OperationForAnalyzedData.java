package analyzerWord2Vec;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class OperationForAnalyzedData {

    // Создаем из файла для анализа
    public static List<String> createDataForAnalysis(String filePathText) throws IOException {

        List<String> content2;

        StringBuilder result = new StringBuilder();

        // получем текст из файла
        BufferedReader reader = new BufferedReader(new FileReader(filePathText));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        // получим токены
        content2 = Arrays.asList(result.toString().split(" "));

        return content2;
    }

    // получение содержимого файла по ссылке
    public static String getDataToURL(String fileURL) throws IOException {

        URL url = new URL(fileURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        int responseCode = connection.getResponseCode();

        StringBuilder content = new StringBuilder();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            reader.close();
            inputStream.close();

        }
        return content.toString();
    }

    // Получение файла по ссылке и конвертация строки
    public static String convertStringCsvToUTF8(String url) throws IOException {

        InputStream inputStream = new URL(url).openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "Windows-1251"));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }

        // Преобразованная строка в UTF-8
        String utf8String = result.toString();

        return utf8String;
    }
}



