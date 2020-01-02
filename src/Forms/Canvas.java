package Forms;

import Utilities.PathLossModels;
import Utilities.Constants;
import Entities.Material;
import Entities.AccessPointModel;
import Entities.AccessPoint;
import Entities.Obstacle;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;
import org.apache.commons.io.FilenameUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ALI
 */
public class Canvas extends javax.swing.JPanel {

    private MainForm mainForm;

    private int operation = 0; // 0: Select, 1: draw lines, 2: draw AP, 3: Calculate signal
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    public ArrayList<AccessPoint> accessPoints = new ArrayList<>();

    private Point device;

    // The "size" of an object for mouse over purposes.
    private static int objectRadius = 5;

    // We're over an object if the distance squared
    // between the mouse and the object is less than this.
    private static int overDistSquared = objectRadius * objectRadius;

    // Points for the new line and signal calculation.
    private Point newPtStart = new Point(-10, -10), newPtEnd = new Point(-10, -10);

    // Selected segment or AP
    private int segmentNumber = -1, selectedSegment = -1, apNumber = -1, selectedAP = -1;

    private Point hitPoint;

    // if moving a line's start point
    private boolean isMovingStartPoint = false;

    // 
    private int offsetX, offsetY, movingObject = 0; // movingObject 1: segment, 2: endpoint, 3: AP

    // See if we should set a background image and if we have to draw grid on it;
    private boolean drawBackground = false, drawGrid = true, drawHeatMap = false;

    // The background image to draw
    private BufferedImage background = null;

    // The material of the obstacle being drawn
    private Material material;

    // The model of the AP to be added
    private AccessPointModel apModel;

    private String apName = Constants.DEFAULT_AP_NAME + "0";

    /**
     * Creates new form Canvas
     */
    public Canvas(MainForm mainForm) {
        this.mainForm = mainForm;
        initComponents();
        MouseEvents mouseEvent = new MouseEvents();
        addMouseMotionListener(mouseEvent);
        addMouseListener(mouseEvent);
    }

    public Canvas() {
        //this.mainForm = mainForm;
        initComponents();
        MouseEvents mouseEvent = new MouseEvents();
        addMouseMotionListener(mouseEvent);
        addMouseListener(mouseEvent);
    }

    public void clearObstacles() {
        selectedSegment = -1;
        obstacles.clear();
        repaint();
    }

    public void clearAccessPoints() {
        selectedAP = -1;
        device = null;
        drawHeatMap = false;
        accessPoints.clear();
        Constants.isChanged = true;
        repaint();
    }

    public void drawHeatMap(boolean drawHeatMap) {
        this.drawHeatMap = drawHeatMap;
        if (drawHeatMap && accessPoints.size() < 1) {
            drawHeatMap = false;
            JOptionPane.showMessageDialog(this, Constants.NO_APS, Constants.ERROR, JOptionPane.ERROR_MESSAGE);
        }
        repaint();
    }

    public void save(File workingFile) {
        FileOutputStream outputStream = null;        
        try {
            if (FilenameUtils.getExtension(workingFile.getName()).equalsIgnoreCase("ser")) {
                // it's ok
            } else {
                workingFile = new File(workingFile.getAbsolutePath() + ".ser");
            }
            outputStream = new FileOutputStream(workingFile.getAbsolutePath());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(obstacles);
            objectOutputStream.writeObject(accessPoints);
            outputStream.close();
            Constants.isChanged = false;
        } catch (IOException ex) {
            Logger.getLogger(Canvas.class.getName()).log(Level.SEVERE, null, ex);
        }
        repaint();
    }

    public void load(File workingFile) {
        try {
            FileInputStream inputStream = new FileInputStream(workingFile.getAbsolutePath());
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            obstacles = (ArrayList<Obstacle>) objectInputStream.readObject();
            accessPoints = (ArrayList<AccessPoint>) objectInputStream.readObject();
            if (obstacles.isEmpty()) {
                JOptionPane.showMessageDialog(null, Constants.LOADFILEERROR, null, 0);
            }
            Constants.isChanged = false;
        } catch (IOException | ClassNotFoundException | HeadlessException e) {
            System.err.println("can't load");
            e.printStackTrace();
        }
        repaint();
    }

    public void importCAD(ArrayList<Obstacle> obstacles) {
        this.obstacles = obstacles;
        Constants.isChanged = true;
        repaint();
    }

    public void setBackgroundImage(BufferedImage image, boolean drawGrid) {
        Dimension dimension = new Dimension(image.getWidth(), image.getHeight());
        setPreferredSize(dimension);
        setMaximumSize(dimension);
        setMinimumSize(dimension);
        setSize(dimension);
        background = image;
        this.drawGrid = drawGrid;
        this.drawBackground = true;
    }

    public void deleteAPOrObstacle() {
        Constants.isChanged = true;
        if (selectedSegment > -1) {
            obstacles.remove(selectedSegment);
            selectedSegment = -1;
            repaint();
        }
        if (selectedAP > -1) {
            accessPoints.remove(selectedAP);
            selectedAP = -1;
            repaint();
        }
    }

    public void clearAll() {
        obstacles.clear();
        accessPoints.clear();
        device = null;
        selectedSegment = -1;
        selectedAP = -1;
        drawBackground = false;
        drawGrid = true;
        drawHeatMap = false;
        Constants.isChanged = true;
        repaint();
    }

    public void setOperation(int operation) {
        this.operation = operation;
        if (operation == Constants.OPERATION_CALC_SIGNAL && !accessPoints.isEmpty()) {
            selectedAP = 0;
            repaint();
        } else if (operation > 0) {
            selectedAP = -1;
            selectedSegment = -1;
            repaint();
        }
    }

    public void setMaterial(Material material) {
        if (selectedSegment > -1) {
            obstacles.get(selectedSegment).setMaterial(material);
            Constants.isChanged = true;
        }
        this.material = material;
    }

    public void setAPModel(AccessPointModel model) {
        if (selectedAP > -1) {
            accessPoints.get(selectedAP).setModel(model);
            Constants.isChanged = true;
        }
        this.apModel = model;
    }

    public void setAPName(String name) {
        if (selectedAP > -1) {
            accessPoints.get(selectedAP).setName(name);
            Constants.isChanged = true;
        }
    }

    public void setScale(int scale) {
        Constants.DRAWING_SCALE = scale;
        Constants.isChanged = true;
        repaint();
    }

    public String getAPName() {
        return apName;
    }

    public BufferedImage getDrawingAsImage() {
        BufferedImage drawingImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        printAll(drawingImage.getGraphics());
        return drawingImage;
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs); //To change body of generated methods, choose Tools | Templates.
        int i, j;
        if (drawBackground) {
            grphcs.drawImage(background, 0, 0, null);
        }

        if (drawHeatMap) {
            double value = 0, power = 0, maxPower = -500;
            if (!accessPoints.isEmpty()) {
                AccessPoint closestAP = accessPoints.get(0);
                Point pixel;
                for (i = 0; i < this.getWidth(); i += Constants.HEAT_MAP_STEP) {
                    for (j = 0; j < this.getWidth(); j += Constants.HEAT_MAP_STEP) {
                        pixel = new Point(i - Constants.HEAT_MAP_STEP + 1, j - Constants.HEAT_MAP_STEP + 1);
                        for (AccessPoint ap : accessPoints) {
                            power = ap.getModel().getEmissionPower() - PathLossModels.solahsModelModified(pixel, ap,
                                    obstacles, segmentLength(pixel, ap.getLocation(), true));
                            if (power > maxPower) {
                                maxPower = power;
                                closestAP = ap;
                            }
                        }
                        value = PathLossModels.normalizeValue(maxPower, closestAP.getModel().getEmissionPower() - closestAP.getModel().getPathLoss1m());
                        maxPower = -500;
                        if (value > 1) {
                            grphcs.setColor(Color.RED);
                        } else if (value < 0) {
                            grphcs.setColor(Color.BLUE);
                        } else {
                            grphcs.setColor(new Color((float) (value), (float) ((4 * value * (1 - value))), (float) ((1 - value))));
                        }
                        grphcs.fillRect(i - Constants.HEAT_MAP_STEP + 1, j - Constants.HEAT_MAP_STEP + 1, Constants.HEAT_MAP_STEP, Constants.HEAT_MAP_STEP);
                    }
                }
            }
        }

        if (drawGrid) {
            grphcs.setColor(Color.LIGHT_GRAY);
            for (i = 0; i < this.getWidth(); i += Constants.DEFAULT_GRID_SIZE) {
                grphcs.drawLine(i, 0, i, this.getHeight());
            }
            for (i = 0; i < this.getHeight(); i += Constants.DEFAULT_GRID_SIZE) {
                grphcs.drawLine(0, i, this.getWidth(), i);
            }

        }

        Graphics2D g2d = (Graphics2D) grphcs;
        
        g2d.setStroke(new BasicStroke(8));

        // Draw the segments.
        for (Obstacle obs : obstacles) {
            // Draw the segment.
            Color color = obs.getMaterial().getColor();
            g2d.setColor(color);
            g2d.drawLine(obs.getStartPoint().x, obs.getStartPoint().y, obs.getEndPoint().x, obs.getEndPoint().y);
        }
        try {
            BufferedImage image = ImageIO.read(Canvas.class.getResourceAsStream(Constants.DRAWACCESPOINTBUTTONIMAGE));
            for (AccessPoint AP : accessPoints) {
                // Draw the Access Points.
                g2d.drawImage(image, null, AP.getLocation().x - 15, AP.getLocation().y - 15);
            }

            if (device != null) {
                BufferedImage deviceImage = ImageIO.read(Canvas.class.getResourceAsStream((Constants.isLaptop ? Constants.LAPTOPIMAGE : Constants.MOBILEIMAGE)));
                g2d.drawImage(deviceImage, null, device.x - 15, device.y - 15);
            }

        } catch (IOException ex) {
            Logger.getLogger(Canvas.class.getName()).log(Level.SEVERE, null, ex);
        }

        switch (operation) {
            case Constants.OPERATION_SELECT:
                if (selectedSegment > -1) {
                    g2d.setStroke(new BasicStroke(1));

                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(obstacles.get(selectedSegment).getStartPoint().x - objectRadius,
                            obstacles.get(selectedSegment).getStartPoint().y - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                    g2d.fillOval(obstacles.get(selectedSegment).getEndPoint().x - objectRadius,
                            obstacles.get(selectedSegment).getEndPoint().y - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);

                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(obstacles.get(selectedSegment).getStartPoint().x - objectRadius,
                            obstacles.get(selectedSegment).getStartPoint().y - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                    g2d.drawOval(obstacles.get(selectedSegment).getEndPoint().x - objectRadius,
                            obstacles.get(selectedSegment).getEndPoint().y - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                }
                if (selectedAP > -1) {
                    g2d.setStroke(new BasicStroke(1));

                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(accessPoints.get(selectedAP).getLocation().x - 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y - 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                    g2d.fillOval(accessPoints.get(selectedAP).getLocation().x + 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y - 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                    g2d.fillOval(accessPoints.get(selectedAP).getLocation().x - 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y + 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                    g2d.fillOval(accessPoints.get(selectedAP).getLocation().x + 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y + 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);

                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(accessPoints.get(selectedAP).getLocation().x - 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y - 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                    g2d.drawOval(accessPoints.get(selectedAP).getLocation().x + 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y - 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                    g2d.drawOval(accessPoints.get(selectedAP).getLocation().x - 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y + 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                    g2d.drawOval(accessPoints.get(selectedAP).getLocation().x + 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y + 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                }
                break;
            case Constants.OPERATION_DRAW_LINE:
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(8));
                g2d.drawLine(newPtStart.x, newPtStart.y, newPtEnd.x, newPtEnd.y);
                break;
            case Constants.OPERATION_CALC_SIGNAL:
                if (!accessPoints.isEmpty() && selectedAP > -1) {
                    g2d.setStroke(new BasicStroke(1));

                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(accessPoints.get(selectedAP).getLocation().x - 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y - 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                    g2d.fillOval(accessPoints.get(selectedAP).getLocation().x + 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y - 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                    g2d.fillOval(accessPoints.get(selectedAP).getLocation().x - 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y + 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                    g2d.fillOval(accessPoints.get(selectedAP).getLocation().x + 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y + 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);

                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(accessPoints.get(selectedAP).getLocation().x - 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y - 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                    g2d.drawOval(accessPoints.get(selectedAP).getLocation().x + 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y - 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                    g2d.drawOval(accessPoints.get(selectedAP).getLocation().x - 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y + 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                    g2d.drawOval(accessPoints.get(selectedAP).getLocation().x + 15 - objectRadius,
                            accessPoints.get(selectedAP).getLocation().y + 15 - objectRadius, 2 * objectRadius + 1, 2 * objectRadius + 1);
                }
                break;
        }
    }

    private class MouseEvents extends MouseInputAdapter {

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e); //To change body of generated methods, choose Tools | Templates.
            switch (operation) {
                case Constants.OPERATION_SELECT:
                    Constants.isChanged = true;
                    switch (movingObject) {
                        case Constants.MOVING_LINE:
                            int new_x1 = e.getX() + offsetX;
                            int new_y1 = e.getY() + offsetY;

                            int dx = new_x1 - obstacles.get(selectedSegment).getStartPoint().x;
                            int dy = new_y1 - obstacles.get(selectedSegment).getStartPoint().y;

                            // Move the segment to its new location.
                            obstacles.get(selectedSegment).setStartPoint(new Point(new_x1, new_y1));
                            obstacles.get(selectedSegment).setEndPoint(new Point(
                                    obstacles.get(selectedSegment).getEndPoint().x + dx,
                                    obstacles.get(selectedSegment).getEndPoint().y + dy));
                            break;

                        case Constants.MOVING_ENDPT:
                            // Move the point to its new location.
                            if (isMovingStartPoint) {
                                obstacles.get(selectedSegment).setStartPoint(new Point(e.getX() + offsetX, e.getY() + offsetY));
                            } else {
                                obstacles.get(selectedSegment).setEndPoint(new Point(e.getX() + offsetX, e.getY() + offsetY));
                            }
                            mainForm.setTFLength(String.format("%.2f", segmentLength(obstacles.get(selectedSegment).getStartPoint(),
                                    obstacles.get(selectedSegment).getEndPoint(), true)));
                            break;

                        case Constants.MOVING_AP:
                            accessPoints.get(selectedAP).setLocation(new Point(e.getX() + offsetX, e.getY() + offsetY));
                            break;
                    }
                    break;
                case Constants.OPERATION_DRAW_LINE:
                    newPtEnd = new Point(e.getPoint());
                    mainForm.setTFLength(String.format("%.2f", segmentLength(newPtStart, newPtEnd, true)));
                    break;
            }
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e); //To change body of generated methods, choose Tools | Templates.

            // Mouse is moving and not pressed. Set the cursor according to operation mode and position
            if (operation == Constants.OPERATION_DRAW_LINE || operation == Constants.OPERATION_DRAW_AP) {
                setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
            } else if (isMouseOverEndpoint(e.getPoint()) || operation == Constants.OPERATION_CALC_SIGNAL) {
                setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            } else if (isMouseOverSegment(e.getPoint()) || isMouseOverAP(e.getPoint())) {
                setCursor(new java.awt.Cursor(java.awt.Cursor.MOVE_CURSOR));
            } else {
                setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            super.mouseReleased(me); //To change body of generated methods, choose Tools | Templates.
            switch (operation) {
                case Constants.OPERATION_DRAW_LINE:
                    Constants.isChanged = true;
                    obstacles.add(new Obstacle(newPtStart, newPtEnd, material));
                    newPtStart = new Point(-10, -10);
                    newPtEnd = new Point(-10, -10);
                    break;

                case Constants.OPERATION_DRAW_AP:
                    Constants.isChanged = true;
                    device = null;
                    accessPoints.add(new AccessPoint(me.getPoint(), apName, apModel));
                    apName = Constants.DEFAULT_AP_NAME + accessPoints.size();
                    mainForm.setTFAPName(apName);
                    break;
                case Constants.OPERATION_CALC_SIGNAL:
                    if (accessPoints.isEmpty()) {
                        JOptionPane.showMessageDialog(null, Constants.NO_APS);
                    } else {
                        if (isMouseOverAP(me.getPoint())) {
                            selectedAP = apNumber;
                        } else {

                            //LAPTOPIMAGE
                            device = me.getPoint();

                            double distToAP = segmentLength(accessPoints.get(selectedAP).getLocation(), me.getPoint(), true);
                            double receivedPower = accessPoints.get(selectedAP).getModel().getEmissionPower()
                                    - PathLossModels.solahsModelModified(me.getPoint(), accessPoints.get(selectedAP), obstacles, distToAP);
                            mainForm.setPanelSignalVisible(true);
                            mainForm.setPanelAPVisible(false);
                            mainForm.setObstaclesPanelVisible(false);
                            mainForm.setTFSignalText(String.format("%.2f", receivedPower));
                            mainForm.setTFDistanceText(String.format("%.2f", distToAP));
                        }
                    }
                    break;
            }
            repaint();
        }

        @Override
        public void mousePressed(MouseEvent me) {
            super.mousePressed(me); //To change body of generated methods, choose Tools | Templates.
            switch (operation) {
                case Constants.OPERATION_SELECT:
                    if (isMouseOverEndpoint(me.getPoint())) {
                        // see if we are moving a start point
                        selectedSegment = segmentNumber;
                        selectedAP = -1;
                        isMovingStartPoint = (obstacles.get(selectedSegment).getStartPoint().equals(hitPoint));

                        // Remember the offset from the mouse to the point.
                        offsetX = hitPoint.x - me.getPoint().x;
                        offsetY = hitPoint.y - me.getPoint().y;
                        movingObject = Constants.MOVING_ENDPT;

                        mainForm.setObstaclesPanelVisible(true);
                        mainForm.setPanelAPVisible(false);
                        mainForm.setPanelSignalVisible(false);
                        mainForm.setTFLength(String.format("%.2f", segmentLength(obstacles.get(selectedSegment).getStartPoint(),
                                obstacles.get(selectedSegment).getEndPoint(), true)));
                        mainForm.setSelectedMaterial(obstacles.get(selectedSegment).getMaterial().getId() - 1);
                    } else if (isMouseOverSegment(me.getPoint())) {
                        selectedSegment = segmentNumber;
                        selectedAP = -1;

                        // Remember the offset from the mouse to the segment's first point.
                        offsetX = obstacles.get(selectedSegment).getStartPoint().x - me.getPoint().x;
                        offsetY = obstacles.get(selectedSegment).getStartPoint().y - me.getPoint().y;
                        movingObject = Constants.MOVING_LINE;

                        mainForm.setObstaclesPanelVisible(true);
                        mainForm.setPanelAPVisible(false);
                        mainForm.setPanelSignalVisible(false);
                        mainForm.setTFLength(String.format("%.2f", segmentLength(obstacles.get(selectedSegment).getStartPoint(),
                                obstacles.get(selectedSegment).getEndPoint(), true)));
                        mainForm.setSelectedMaterial(obstacles.get(selectedSegment).getMaterial().getId() - 1);
                    } else if (isMouseOverAP(me.getPoint())) {
                        selectedSegment = -1;
                        selectedAP = apNumber;

                        offsetX = accessPoints.get(selectedAP).getLocation().x - me.getPoint().x;
                        offsetY = accessPoints.get(selectedAP).getLocation().y - me.getPoint().y;
                        movingObject = Constants.MOVING_AP;

                        mainForm.setPanelAPVisible(true);
                        mainForm.setPanelSignalVisible(false);
                        mainForm.setObstaclesPanelVisible(false);
                        mainForm.setSelectedAPModel(accessPoints.get(selectedAP).getModel().getId() - 1);
                        mainForm.setTFAPName(accessPoints.get(selectedAP).getName());
                    } else {
                        movingObject = Constants.MOVING_NOTHING;
                        selectedSegment = -1;
                        selectedAP = -1;

                        mainForm.setObstaclesPanelVisible(false);
                        mainForm.setTFLength("");
                        mainForm.setSelectedMaterial(0);
                        mainForm.setPanelAPVisible(false);
                        mainForm.setPanelSignalVisible(false);
                        mainForm.setSelectedAPModel(0);
                    }
                    break;

                case Constants.OPERATION_DRAW_LINE:
                    selectedSegment = -1;
                    newPtStart = new Point(me.getPoint());
                    newPtEnd = new Point(me.getPoint());
                    break;

                case Constants.OPERATION_DRAW_AP:
                    selectedAP = -1;
                    break;

                case Constants.OPERATION_CALC_SIGNAL:
                    break;

                default:
                    selectedAP = -1;
                    selectedSegment = -1;
                    repaint();
            }
        }

    }

    private int findDistanceToPointSquared(Point pt1, Point pt2) {
        int dx = pt1.x - pt2.x;
        int dy = pt1.y - pt2.y;
        return dx * dx + dy * dy;
    }

    private double findDistanceToSegmentSquared(Point pt, Point p1, Point p2) {
        float dx = p2.x - p1.x;
        float dy = p2.y - p1.y;
        if ((dx == 0) && (dy == 0)) {
            // It's a point not a line segment.
            dx = pt.x - p1.x;
            dy = pt.y - p1.y;
            return dx * dx + dy * dy;
        }

        // Calculate the t that minimizes the distance.
        float t = ((pt.x - p1.x) * dx + (pt.y - p1.y) * dy) / (dx * dx + dy * dy);

        // See if this represents one of the segment's
        // end points or a point in the middle.
        if (t < 0) {
            dx = pt.x - p1.x;
            dy = pt.y - p1.y;
        } else if (t > 1) {
            dx = pt.x - p2.x;
            dy = pt.y - p2.y;
        } else {
            dx = pt.x - (p1.x + t * dx);
            dy = pt.y - (p1.y + t * dy);
        }

        return dx * dx + dy * dy;
    }

    private boolean isMouseOverSegment(Point mousePoint) {
        for (int i = 0; i < obstacles.size(); i++) {
            // See if we're over the segment.
            if (findDistanceToSegmentSquared(mousePoint, obstacles.get(i).getStartPoint(),
                    obstacles.get(i).getEndPoint()) < overDistSquared) {
                // We're over this segment.
                segmentNumber = i;
                return true;
            }
        }
        segmentNumber = -1;
        return false;
    }

    private boolean isMouseOverEndpoint(Point mousePoint) {
        for (int i = 0; i < obstacles.size(); i++) {
            // Check the starting point.
            if (findDistanceToPointSquared(mousePoint, obstacles.get(i).getStartPoint()) < overDistSquared) {
                // We're over this point.
                segmentNumber = i;
                hitPoint = obstacles.get(i).getStartPoint();
                return true;
            }

            // Check the end point.
            if (findDistanceToPointSquared(mousePoint, obstacles.get(i).getEndPoint()) < overDistSquared) {
                // We're over this point.
                segmentNumber = i;
                hitPoint = obstacles.get(i).getEndPoint();
                return true;
            }
        }
        segmentNumber = -1;
        hitPoint = new Point(-10, -10);
        return false;
    }

    private boolean isMouseOverAP(Point mousePoint) {
        int i = 0;
        for (AccessPoint AP : accessPoints) {
            if ((mousePoint.x - AP.getLocation().x < 20 && mousePoint.x - AP.getLocation().x > -20)
                    && (mousePoint.y - AP.getLocation().y < 20 && mousePoint.y - AP.getLocation().y > -20)) {
                apNumber = i;
                return true;
            }
            i++;
        }

        apNumber = -1;
        return false;
    }

    private double segmentLength(Point P1, Point P2, boolean isCentiMeters) {
        return isCentiMeters ? Math.sqrt((P1.x - P2.x) * (P1.x - P2.x) + (P1.y - P2.y) * (P1.y - P2.y)) * Constants.DRAWING_SCALE / Constants.DEFAULT_GRID_SIZE
                : Math.sqrt((P1.x - P2.x) * (P1.x - P2.x) + (P1.y - P2.y) * (P1.y - P2.y));
    }

    public void setAPOptimalLocation(AccessPoint ap) {
        Constants.isChanged = true;
        accessPoints.clear();
        accessPoints.add(ap);
        repaint();
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }
    
    public void setObstacles(ArrayList<Obstacle> obstacles){
        this.obstacles = obstacles;
        repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1919, 1079));
        setMinimumSize(new java.awt.Dimension(1919, 1079));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1919, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1079, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
