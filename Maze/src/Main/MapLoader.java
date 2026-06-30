package Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {

    public String[][] loadMapFromFile(String filePath) {
        List<String[]> rowList = new ArrayList<>();

        java.io.InputStream is = getClass().getResourceAsStream(filePath);
        if (is == null && filePath.startsWith("src")) {
            // Coba potong "src" untuk path resource jika salah path
            String resourcePath = "/" + filePath.substring(4).replace('\\', '/');
            is = getClass().getResourceAsStream(resourcePath);
        }

        try {
            BufferedReader br;
            if (is != null) {
                br = new BufferedReader(new java.io.InputStreamReader(is));
            } else {
                br = new BufferedReader(new FileReader(filePath));
            }
            
            String line;
            while ((line = br.readLine()) != null) {
                String[] rowElements = line.trim().split("\\s+");
                rowList.add(rowElements);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int maxCols = 0;
        for (String[] row : rowList) {
            if (row.length > maxCols) {
                maxCols = row.length;
            }
        }

        String[][] mapArray = new String[rowList.size()][maxCols];
        for (int i = 0; i < rowList.size(); i++) {
            String[] row = rowList.get(i);
            System.arraycopy(row, 0, mapArray[i], 0, row.length);
            for (int j = row.length; j < maxCols; j++) {
                mapArray[i][j] = "0"; // Pad missing columns with empty floor tiles.
            }
            if (row.length != maxCols) {
                System.err.println("Warning: map row " + i + " has " + row.length + " columns but expected " + maxCols
                        + ". Padding with 0s.");
            }
        }

        return mapArray;
    }
}
