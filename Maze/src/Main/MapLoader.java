package Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {

    public String[][] loadMapFromFile(String filePath) {
        List<String[]> rowList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] rowElements = line.trim().split("\\s+");
                rowList.add(rowElements);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[][] mapArray = new String[rowList.size()][];
        for (int i = 0; i < rowList.size(); i++) {
            mapArray[i] = rowList.get(i);
        }

        return mapArray;
    }
}
