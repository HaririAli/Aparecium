package Utilities;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Entities.Material;
import Entities.Obstacle;
import java.awt.Color;
import java.awt.Point;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.kabeja.dxf.Bounds;
import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFLine;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.ParseException;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;

/**
 *
 * @author JAD4
 */
public class ReadAutocadFile {

    // SQLite Query Result reader
    private static ResultSet resultSet;

    private static Map<Color, Material> map = new HashMap<>();

    public static ArrayList<Obstacle> getAutocadFileObstacles(String filePath) throws ParseException {

        fillMaterialsColor();

        ArrayList<Obstacle> obstacles = new ArrayList<>();
        Parser parser = ParserBuilder.createDefaultParser();
        parser.parse(filePath, DXFParser.DEFAULT_ENCODING);
        DXFDocument doc = parser.getDocument();
        List<DXFLine> lst = doc.getDXFLayer("layername ... whatever").getDXFEntities(DXFConstants.ENTITY_TYPE_LINE);
        for (int index = 0; index < lst.size(); index++) {
            Bounds bounds = lst.get(index).getBounds();
            int colorCode = lst.get(index).getColor();
            Color color = getColor(colorCode);
            Material material = map.get(color) == null ? readFirstMaterial() : map.get(color);
            Obstacle obstacle = new Obstacle(
                    new Point(new Double(bounds.getMinimumX()).intValue() * 25,
                            new Double(bounds.getMinimumY()).intValue() * 25),
                    new Point(new Double(bounds.getMaximumX()).intValue() * 25,
                            new Double(bounds.getMaximumY()).intValue() * 25),
                    material);
            System.out.println("Test " + obstacle.getStartPoint() + " " + obstacle.getEndPoint() + "color : " + lst.get(index).getColor());
            obstacles.add(obstacle);
        }
        return obstacles;
    }

    private static void fillMaterialsColor() {
        try {
            resultSet = SQLiteDbManager.executeReader(SQLiteDbManager.connect(), "SELECT * FROM MATERIAL");
            while (resultSet.next()) {
                int[] rgb = Stream.of(resultSet.getString("Color").split(",")).mapToInt(Integer::parseInt).toArray();
                Color color = new Color(rgb[0], rgb[1], rgb[2]);
                Material material = new Material(resultSet.getInt("ID"), resultSet.getString("Material_Name"), resultSet.getFloat("Attenuation"), color);
                map.put(color, material);
            }
        } catch (Exception ex) {
            System.out.println("Error : " + ex);
        }
    }

    // If an autocad line has a color not contained in the db -> consider the line like the first material (concrete)
    private static Material readFirstMaterial() {
        Material material = null;
        try {
            resultSet = SQLiteDbManager.executeReader(SQLiteDbManager.connect(), "SELECT * FROM MATERIAL");
            if (resultSet.next()) {
                int[] rgb = Stream.of(resultSet.getString("Color").split(",")).mapToInt(Integer::parseInt).toArray();
                Color color = new Color(rgb[0], rgb[1], rgb[2]);
                material = new Material(resultSet.getInt("ID"), resultSet.getString("Material_Name"), resultSet.getFloat("Attenuation"), color);
            }
        } catch (Exception ex) {
            System.out.println("Error : " + ex);
        }
        return material;
    }

    // Here is how Kabeja library read the lines color
    public static Color getColor(int code) {
        switch (code) {
            case 0:
                return Color.BLACK;
            case 1:
                return Color.RED;
            case 2:
                return Color.YELLOW;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.CYAN;
            case 5:
                return Color.BLUE;
            case 6:
                return Color.MAGENTA;
            case 251:
                return new Color(90, 90, 90);
            default:
                return Color.BLACK;
        }
    }

}
