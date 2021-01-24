//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.Serializable;
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.LinkedList;
//
//public class StageReader implements Serializable{
//    public static StageReader instance = new StageReader();
//    public static String filename = "stages.txt";
//
//    ArrayList<Stage> stageList = new ArrayList<>();
//    LinkedList<String> lines = new LinkedList<>();
//
//    public StageReader() {
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(
//                    filename));
//            String line = reader.readLine();
//            while (line != null) {
//                lines.add(line);
//                line = reader.readLine();
//            }
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void readStage() {
//
//    }
//
//    private Material getMaterial(String material) {
//        try {
//            Field materialField = Material.class.getField(material);
//            return (Material) materialField.get(null);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            return null;
//        }
//    }
//
//
//}
