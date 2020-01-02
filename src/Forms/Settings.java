/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import Utilities.Constants;
import Utilities.Optimality;
import Utilities.PathLossModels;
import Utilities.SQLiteDbManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author ALI
 */
public class Settings extends javax.swing.JDialog {

    public static final String TAG_HEAT_MAP_STEP = "HeatMapStep";
    public static final String TAG_BRUTE_FORCE_STEP = "BruteForceStep";
    public static final String TAG_LOS = "LOS";
    public static final String TAG_NLOS = "NLOS";
    public static final String TAG_BP_DISTANCE = "BreakPointDist";
    public static final String TAG_MIN_LAPTOP_THRESH = "MinLaptopThresh";
    public static final String TAG_MIN_MOBILE_THRESH = "MinMobileThresh";
    public static final String TAG_USE_BRUTE_FORCE = "UseBruteForce";
    public static final String TAG_ENSURE_OPTIMALITY = "EnsureOptimality";

    public static final String TAG_GENERAL = "General";
    public static final String TAG_PATH_LOSS_MODEL = "PathLossModel";
    public static final String TAG_DRAWING_TOOL = "DrawingTool";

    public static final String TAG_SETTINGS = "Settings";

    private JTable table;
    private MainForm mainForm;

    /**
     * Creates new form Settings
     */
    public Settings(MainForm mainForm) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            JOptionPane.showMessageDialog(this, e.toString(), Constants.ERROR, JOptionPane.WARNING_MESSAGE);
        }
        this.mainForm = mainForm;
        initComponents();
        tfBPDist.setText(PathLossModels.BREAK_POINT_DISTANCE + "");
        tfLOS.setText(PathLossModels.LOS_EXPONENT + "");
        tfNLOS.setText(PathLossModels.NLOS_EXPONENT + "");
        tfLaptopThresh.setText(PathLossModels.MIN_THRESHOLDLAPTOP + "");
        tfMobileThresh.setText(PathLossModels.MIN_THRESHOLDMOBILE + "");

        float heatMapStep = Constants.HEAT_MAP_STEP * Constants.DRAWING_SCALE / (float)Constants.DEFAULT_GRID_SIZE;
        tfHeatMapStep.setText(heatMapStep + "");
        tfBruteForceStep.setText((Constants.BRUTE_FORCE_STEP * Constants.DRAWING_SCALE / (float)Constants.DEFAULT_GRID_SIZE) + "");
        
        chbBruteForce.setSelected(Optimality.useBruteForce);
        chbEnsureOptimality.setSelected(Optimality.ensureOptimality);
        chbBruteForce.setEnabled(chbEnsureOptimality.isSelected());

        try {
            ResultSet rset = getMaterialsColor();

            ResultSetMetaData metaData = rset.getMetaData();

            // names of columns
            Vector<String> columnNames = new Vector<String>();
            int columnCount = metaData.getColumnCount();
            for (int column = 1; column <= columnCount; column++) {
                columnNames.add(metaData.getColumnName(column));
            }

            Vector<Vector<Object>> data = new Vector<Vector<Object>>();
            while (rset.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    vector.add(rset.getObject(columnIndex));
                }
                data.add(vector);
            }

            table = new JTable(data, columnNames) {
                @Override
                public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {

                    Component comp = super.prepareRenderer(renderer, row, col);

                    Object value = getModel().getValueAt(row, 1);

                    int[] rgb = Stream.of(value.toString().split(",")).mapToInt(Integer::parseInt).toArray();

                    Color c = new Color(rgb[0], rgb[1], rgb[2], 255);

                    if (col == 1) {
                        comp.setBackground(c);
                        comp.setForeground(c);
                    } else {
                        comp.setBackground(Color.WHITE);
                        comp.setForeground(Color.BLACK);
                    }

                    return comp;
                }
            };

            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent event) {
                    JColorChooser colorChooser = new JColorChooser();
                    AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();
                    for (AbstractColorChooserPanel p : panels) {
                        String displayName = p.getDisplayName();
                        switch (displayName) {
                            case "HSV":
                                colorChooser.removeChooserPanel(p);
                                break;
                            case "HSL":
                                colorChooser.removeChooserPanel(p);
                                break;
                            case "CMYK":
                                colorChooser.removeChooserPanel(p);
                                break;
                            case "Swatches":
                                colorChooser.removeChooserPanel(p);
                                break;
                        }
                    }
                    String materialName = table.getModel().getValueAt(table.getSelectedRow(), 0).toString();
                    int input = JOptionPane.showOptionDialog(null, colorChooser, "Change color of " + materialName, JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, colorChooser);

                    if (input == JOptionPane.OK_OPTION) {
                        String rgb = colorChooser.getColor().getRed() + "," + colorChooser.getColor().getGreen() + "," + colorChooser.getColor().getBlue();

                        int update = updateMaterialColor(materialName, rgb);
                        if (update != 1) {
                            JOptionPane.showMessageDialog(null, "Error", "Cannot update color", JOptionPane.WARNING_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Material Updated Successfully");
                            try{
                                table.setModel(buildTableModel(getMaterialsColor()));
                            }
                            catch(Exception ex){
                                
                            }
                        }

                    }
                }
            });

            table.setDefaultEditor(Object.class, null);
            getContentPane().add(table);

            JScrollPane tableContainer = new JScrollPane(table);

            JPanel panel = new JPanel();
            panel.add(tableContainer, BorderLayout.CENTER);
            panelDrawingTool.add(panel, "Drawing Tool");

        } catch (Exception ex) {

        }
    }

    public static DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        Vector<String> columnNames = new Vector<String>();
        // names of columns
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);

    }

    private int updateMaterialColor(String materialName, String rgb) {
        int update = 0;

        try {
            Connection con = SQLiteDbManager.connect();
            Statement stmt = con.createStatement();
            update = stmt.executeUpdate("update MATERIAL set Color = '" + rgb + "' where Material_Name = '" + materialName + "'");
            mainForm.fillMaterialsList();
        } catch (Exception ex) {
            System.out.println("Error : " + ex);
        }
        return update;
    }

    public ResultSet getMaterialsColor() {
        ResultSet rset = null;
        try {
            Connection con = SQLiteDbManager.connect();
            Statement stmt = con.createStatement();
            rset = stmt.executeQuery("select Material_Name, Color from MATERIAL");
        } catch (Exception ex) {

        }
        return rset;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator2 = new javax.swing.JSeparator();
        btCancel = new javax.swing.JButton();
        panelDrawingTool = new javax.swing.JTabbedPane();
        panelGeneral = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        tfHeatMapStep = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        tfBruteForceStep = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        btDefaultHM = new javax.swing.JButton();
        btDefaultBF = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        chbBruteForce = new javax.swing.JCheckBox();
        chbEnsureOptimality = new javax.swing.JCheckBox();
        panelPLModels = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tfLOS = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tfNLOS = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tfBPDist = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tfLaptopThresh = new javax.swing.JTextField();
        tfMobileThresh = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        btDefaultBp = new javax.swing.JButton();
        btDefaultExp = new javax.swing.JButton();
        btDefaultThresh = new javax.swing.JButton();
        btApply = new javax.swing.JButton();
        btOK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Settings");

        btCancel.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        btCancel.setText("Cancel");
        btCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelActionPerformed(evt);
            }
        });

        panelDrawingTool.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        panelDrawingTool.setToolTipText("");
        panelDrawingTool.setAutoscrolls(true);
        panelDrawingTool.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 14)); // NOI18N
        jLabel14.setText("Heat Map Precision");

        jLabel15.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel15.setText("<html>Heat map precision is the area at which the received power is recalculated. Note that as long as this value decreases, precision increases but more time and resources are consumed. </html>");

        tfHeatMapStep.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tfHeatMapStep.setText("7.5");
        tfHeatMapStep.setToolTipText("");
        tfHeatMapStep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tfHeatMapStepKeyTyped(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel16.setText("cm");

        jLabel17.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 14)); // NOI18N
        jLabel17.setText("Optimality Precision");

        jLabel18.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel18.setText("<html>Optimality precision is the area at which the optimal location can be detected in. When this value decreases, precision increases but more time and resources are needed.</html>");

        tfBruteForceStep.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tfBruteForceStep.setText("62.5");
        tfBruteForceStep.setToolTipText("");
        tfBruteForceStep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tfBruteForceStepKeyTyped(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel19.setText("cm");

        btDefaultHM.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        btDefaultHM.setText("Restore Defaults");
        btDefaultHM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDefaultHMActionPerformed(evt);
            }
        });

        btDefaultBF.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        btDefaultBF.setText("Restore Defaults");
        btDefaultBF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDefaultBFActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 14)); // NOI18N
        jLabel20.setText("Automatic AP Placement");

        jLabel21.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel21.setText("<html>Ensuring full coverage means that the tool will try to find only one AP location that would cover the whole floor plan. If not found, then more than one AP is needed. If full coverage is not ensured, the tool will search for the location which covers the greatest area of the plan.<br>Ensuring full coverage my be applied by 2 methods.  Using brute gives very accrute results but it might, in some cases, take a long time to find the optimal location. If brute force is not selected another algorithm, which is relatively fast but heurisitc, will be used.</html>");

        chbBruteForce.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        chbBruteForce.setText("Brute Force ? ");
        chbBruteForce.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chbBruteForceItemStateChanged(evt);
            }
        });
        chbBruteForce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbBruteForceActionPerformed(evt);
            }
        });

        chbEnsureOptimality.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        chbEnsureOptimality.setText("Ensure Full Coverage");
        chbEnsureOptimality.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chbEnsureOptimalityItemStateChanged(evt);
            }
        });
        chbEnsureOptimality.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbEnsureOptimalityActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelGeneralLayout = new javax.swing.GroupLayout(panelGeneral);
        panelGeneral.setLayout(panelGeneralLayout);
        panelGeneralLayout.setHorizontalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addComponent(tfHeatMapStep, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btDefaultHM))
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addComponent(tfBruteForceStep, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btDefaultBF))
                    .addComponent(jSeparator5)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel17)
                            .addComponent(jLabel20)
                            .addComponent(chbEnsureOptimality)
                            .addComponent(chbBruteForce))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelGeneralLayout.setVerticalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfHeatMapStep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(btDefaultHM))
                .addGap(18, 18, 18)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfBruteForceStep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(btDefaultBF))
                .addGap(18, 18, 18)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addComponent(chbEnsureOptimality)
                .addGap(2, 2, 2)
                .addComponent(chbBruteForce)
                .addContainerGap())
        );

        panelDrawingTool.addTab("General", panelGeneral);

        jLabel1.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 14)); // NOI18N
        jLabel1.setText("Path Loss Exponents");

        jLabel2.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel2.setText("<html>The break point distance is the distance of which there is no line of sight to the access point. The default value is set for a typical room.</html>");

        jLabel3.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel3.setText("Line of sight exponent:");

        tfLOS.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tfLOS.setText("1.04");
        tfLOS.setToolTipText("");
        tfLOS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tfLOSKeyTyped(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel4.setText("Non-line of sight exponent:");

        tfNLOS.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tfNLOS.setText("2.52");
        tfNLOS.setToolTipText("");
        tfNLOS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tfNLOSKeyTyped(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 14)); // NOI18N
        jLabel5.setText("Break Point Distance");

        jLabel6.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel6.setText("<html>A path loss exponent is a value that represents signal loss and varies from 1 (propagation infree space) to 6 (lossy environment). In the model implemented in this software there 2 path loss exponents, one for the line of sight signals and the other for the non-line of sight signals. The default values are set for a typical building. If you don't have knowledge about it, do not change these values.</html>");

        tfBPDist.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tfBPDist.setText("400");
        tfBPDist.setToolTipText("");
        tfBPDist.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tfBPDistKeyTyped(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel7.setText("cm");

        jLabel8.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 14)); // NOI18N
        jLabel8.setText("Wi-Fi Minimum Thresholds");

        jLabel9.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel9.setText("<html>A Wi-Fi minimum threshold is the minimum power a device can receive to stay connected. The default values are for typical smart phones and laptops. These values are used by the software to draw the heat map</html>");

        jLabel10.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel10.setText("Laptop Threshold:");

        tfLaptopThresh.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tfLaptopThresh.setText("-90");
        tfLaptopThresh.setToolTipText("");
        tfLaptopThresh.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tfLaptopThreshKeyTyped(evt);
            }
        });

        tfMobileThresh.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tfMobileThresh.setText("-60");
        tfMobileThresh.setToolTipText("");
        tfMobileThresh.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tfMobileThreshKeyTyped(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel11.setText("Mobile Phone Threshold:");

        jLabel12.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel12.setText("dBm");

        jLabel13.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel13.setText("dBm");

        btDefaultBp.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        btDefaultBp.setText("Restore Defaults");
        btDefaultBp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDefaultBpActionPerformed(evt);
            }
        });

        btDefaultExp.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        btDefaultExp.setText("Restore Defaults");
        btDefaultExp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDefaultExpActionPerformed(evt);
            }
        });

        btDefaultThresh.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        btDefaultThresh.setText("Restore Defaults");
        btDefaultThresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDefaultThreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPLModelsLayout = new javax.swing.GroupLayout(panelPLModels);
        panelPLModels.setLayout(panelPLModelsLayout);
        panelPLModelsLayout.setHorizontalGroup(
            panelPLModelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPLModelsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPLModelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator3)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelPLModelsLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tfMobileThresh, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btDefaultThresh))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelPLModelsLayout.createSequentialGroup()
                        .addGroup(panelPLModelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5)
                            .addGroup(panelPLModelsLayout.createSequentialGroup()
                                .addComponent(tfBPDist, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7))
                            .addComponent(jLabel8)
                            .addGroup(panelPLModelsLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tfLaptopThresh, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12))
                            .addGroup(panelPLModelsLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tfLOS, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelPLModelsLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tfNLOS, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPLModelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btDefaultExp)
                            .addComponent(btDefaultBp, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        panelPLModelsLayout.setVerticalGroup(
            panelPLModelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPLModelsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPLModelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tfLOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPLModelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tfNLOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btDefaultExp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPLModelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(tfBPDist, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btDefaultBp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPLModelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(tfLaptopThresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPLModelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(tfMobileThresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(btDefaultThresh))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        panelDrawingTool.addTab("Path Loss Model", panelPLModels);

        btApply.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        btApply.setText("Apply");
        btApply.setEnabled(false);
        btApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btApplyActionPerformed(evt);
            }
        });

        btOK.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        btOK.setText("OK");
        btOK.setToolTipText("");
        btOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOKActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelDrawingTool)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btOK, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btApply, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelDrawingTool, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btCancel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btApply)
                        .addComponent(btOK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btDefaultBpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDefaultBpActionPerformed
        // TODO add your handling code here:
        tfBPDist.setText(PathLossModels.DEFAULT_BREAK_POINT_DISTANCE + "");
        btApply.setEnabled(true);
    }//GEN-LAST:event_btDefaultBpActionPerformed

    private void btDefaultExpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDefaultExpActionPerformed
        // TODO add your handling code here:
        tfNLOS.setText(PathLossModels.DEFAULT_NLOS_EXPONENT + "");
        tfLOS.setText(PathLossModels.DEFAULT_LOS_EXPONENT + "");
        btApply.setEnabled(true);
    }//GEN-LAST:event_btDefaultExpActionPerformed

    private void btDefaultThreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDefaultThreshActionPerformed
        // TODO add your handling code here:
        tfLaptopThresh.setText(PathLossModels.DEFAULT_MIN_THRESHOLDLAPTOP + "");
        tfMobileThresh.setText(PathLossModels.DEFAULT_MIN_THRESHOLDMOBILE + "");
        btApply.setEnabled(true);
    }//GEN-LAST:event_btDefaultThreshActionPerformed

    private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_btCancelActionPerformed

    private void btApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btApplyActionPerformed
        // TODO add your handling code here:
        applyChanges(
                tfHeatMapStep.getText(),
                tfBruteForceStep.getText(),
                chbBruteForce.isSelected(),
                chbEnsureOptimality.isSelected(),
                tfBPDist.getText(),
                tfNLOS.getText(),
                tfLOS.getText(),
                tfLaptopThresh.getText(),
                tfMobileThresh.getText()
        );
        writeJsonSettings(Constants.JSON_SETTINGS_FILE_NAME);
        btApply.setEnabled(false);
    }//GEN-LAST:event_btApplyActionPerformed

    private void btOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOKActionPerformed
        // TODO add your handling code here:
        if (btApply.isEnabled()) {
            applyChanges(
                    tfHeatMapStep.getText(),
                    tfBruteForceStep.getText(),
                    chbBruteForce.isSelected(),
                    chbEnsureOptimality.isSelected(),
                    tfBPDist.getText(),
                    tfNLOS.getText(),
                    tfLOS.getText(),
                    tfLaptopThresh.getText(),
                    tfMobileThresh.getText()
            );
            writeJsonSettings(Constants.JSON_SETTINGS_FILE_NAME);
            btApply.setEnabled(false);
        }
        this.dispose();
    }//GEN-LAST:event_btOKActionPerformed

    private void tfLOSKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfLOSKeyTyped
        // TODO add your handling code here:
        if (!evt.isControlDown() && !Character.isDigit(evt.getKeyChar()) && evt.getKeyChar() != '.') {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }// only allow one decimal point
        else if (evt.getKeyChar() == '.' && tfLOS.getText().indexOf('.') > -1) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
        btApply.setEnabled(true);
    }//GEN-LAST:event_tfLOSKeyTyped

    private void tfNLOSKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfNLOSKeyTyped
        // TODO add your handling code here:
        if (!evt.isControlDown() && !Character.isDigit(evt.getKeyChar()) && evt.getKeyChar() != '.') {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }// only allow one decimal point
        else if (evt.getKeyChar() == '.' && tfNLOS.getText().indexOf('.') > -1) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
        btApply.setEnabled(true);
    }//GEN-LAST:event_tfNLOSKeyTyped

    private void tfBPDistKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfBPDistKeyTyped
        // TODO add your handling code here:
        if (!evt.isControlDown() && !Character.isDigit(evt.getKeyChar()) && evt.getKeyChar() != '.') {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }// only allow one decimal point
        else if (evt.getKeyChar() == '.' && tfBPDist.getText().indexOf('.') > -1) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
        btApply.setEnabled(true);
    }//GEN-LAST:event_tfBPDistKeyTyped

    private void tfLaptopThreshKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfLaptopThreshKeyTyped
        // TODO add your handling code here:
        if (!evt.isControlDown() && !Character.isDigit(evt.getKeyChar()) && evt.getKeyChar() != '.' && evt.getKeyChar() != '-') {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }// only allow one decimal point
        else if (evt.getKeyChar() == '.' && tfLaptopThresh.getText().indexOf('.') > -1) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
        btApply.setEnabled(true);
    }//GEN-LAST:event_tfLaptopThreshKeyTyped

    private void tfMobileThreshKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfMobileThreshKeyTyped
        // TODO add your handling code here:
        if (!evt.isControlDown() && !Character.isDigit(evt.getKeyChar()) && evt.getKeyChar() != '.' && evt.getKeyChar() != '-') {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }// only allow one decimal point
        else if (evt.getKeyChar() == '.' && tfMobileThresh.getText().indexOf('.') > -1) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
        btApply.setEnabled(true);
    }//GEN-LAST:event_tfMobileThreshKeyTyped

    private void tfHeatMapStepKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfHeatMapStepKeyTyped
        // TODO add your handling code here:
        if (!evt.isControlDown() && !Character.isDigit(evt.getKeyChar()) && evt.getKeyChar() != '.') {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }// only allow one decimal point
        else if (evt.getKeyChar() == '.' && tfHeatMapStep.getText().indexOf('.') > -1) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
        btApply.setEnabled(true);
    }//GEN-LAST:event_tfHeatMapStepKeyTyped

    private void tfBruteForceStepKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfBruteForceStepKeyTyped
        // TODO add your handling code here:
        if (!evt.isControlDown() && !Character.isDigit(evt.getKeyChar()) && evt.getKeyChar() != '.') {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }// only allow one decimal point
        else if (evt.getKeyChar() == '.' && tfBruteForceStep.getText().indexOf('.') > -1) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
        btApply.setEnabled(true);
    }//GEN-LAST:event_tfBruteForceStepKeyTyped

    private void btDefaultHMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDefaultHMActionPerformed
        // TODO add your handling code here:
        tfHeatMapStep.setText(Constants.DEFAULT_HEAT_MAP_STEP + "");
        btApply.setEnabled(true);
    }//GEN-LAST:event_btDefaultHMActionPerformed

    private void btDefaultBFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDefaultBFActionPerformed
        // TODO add your handling code here:
        tfBruteForceStep.setText(Constants.DEFAULT_BRUTE_FORCE_STEP + "");
        btApply.setEnabled(true);
    }//GEN-LAST:event_btDefaultBFActionPerformed

    private void chbBruteForceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chbBruteForceItemStateChanged
        // TODO add your handling code here:
        //btApply.setEnabled(true);
    }//GEN-LAST:event_chbBruteForceItemStateChanged

    private void chbBruteForceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbBruteForceActionPerformed
        // TODO add your handling code here:
        btApply.setEnabled(true);
    }//GEN-LAST:event_chbBruteForceActionPerformed

    private void chbEnsureOptimalityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbEnsureOptimalityActionPerformed
        // TODO add your handling code here:
        btApply.setEnabled(true);
    }//GEN-LAST:event_chbEnsureOptimalityActionPerformed

    private void chbEnsureOptimalityItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chbEnsureOptimalityItemStateChanged
        // TODO add your handling code here:
        chbBruteForce.setEnabled(chbEnsureOptimality.isSelected());
    }//GEN-LAST:event_chbEnsureOptimalityItemStateChanged

    private static void applyChanges(int heatMapStep, int bruteForceStep, boolean useBruteForce, boolean ensureOptimality,
            String breakPointDist, String nLOS, String lOS, String laptopThresh, String mobileThresh) {
        PathLossModels.BREAK_POINT_DISTANCE = Integer.parseInt(breakPointDist);
        PathLossModels.LOS_EXPONENT = Double.parseDouble(lOS);
        PathLossModels.NLOS_EXPONENT = Double.parseDouble(nLOS);
        PathLossModels.MIN_THRESHOLDLAPTOP = Double.parseDouble(laptopThresh);
        PathLossModels.MIN_THRESHOLDMOBILE = Double.parseDouble(mobileThresh);

        Constants.HEAT_MAP_STEP = heatMapStep;
        Constants.BRUTE_FORCE_STEP = bruteForceStep;
        Optimality.useBruteForce = useBruteForce;
        Optimality.ensureOptimality = ensureOptimality;
    }
    
    private static void applyChanges(String heatMapStep, String bruteForceStep, boolean useBruteForce, boolean ensureOptimality,
            String breakPointDist, String nLOS, String lOS, String laptopThresh, String mobileThresh) {
        PathLossModels.BREAK_POINT_DISTANCE = Integer.parseInt(breakPointDist);
        PathLossModels.LOS_EXPONENT = Double.parseDouble(lOS);
        PathLossModels.NLOS_EXPONENT = Double.parseDouble(nLOS);
        PathLossModels.MIN_THRESHOLDLAPTOP = Double.parseDouble(laptopThresh);
        PathLossModels.MIN_THRESHOLDMOBILE = Double.parseDouble(mobileThresh);

        Constants.HEAT_MAP_STEP = (int) Math.round(Double.parseDouble(heatMapStep) * Constants.DEFAULT_GRID_SIZE / Constants.DRAWING_SCALE);
        Constants.BRUTE_FORCE_STEP = (int) Math.round(Double.parseDouble(bruteForceStep) * Constants.DEFAULT_GRID_SIZE / Constants.DRAWING_SCALE);
        Optimality.useBruteForce = useBruteForce;
        Optimality.ensureOptimality = ensureOptimality;
    }

    public static boolean readJsonSettings(String settingsFilePath) {

        try {
            JsonReader jsonReader = Json.createReader(new InputStreamReader(new FileInputStream(settingsFilePath)));

            JsonObject jsonObject = jsonReader.readObject();
            JsonObject settings = jsonObject.getJsonObject(TAG_SETTINGS);
            JsonObject general = settings.getJsonObject(TAG_GENERAL);
            JsonObject pathLossModel = settings.getJsonObject(TAG_PATH_LOSS_MODEL);

            applyChanges(
                    general.getInt(TAG_HEAT_MAP_STEP),
                    general.getInt(TAG_BRUTE_FORCE_STEP),
                    general.getBoolean(TAG_USE_BRUTE_FORCE),
                    general.getBoolean(TAG_ENSURE_OPTIMALITY),
                    pathLossModel.getString(TAG_BP_DISTANCE),
                    pathLossModel.getString(TAG_NLOS),
                    pathLossModel.getString(TAG_LOS),
                    pathLossModel.getString(TAG_MIN_LAPTOP_THRESH),
                    pathLossModel.getString(TAG_MIN_MOBILE_THRESH));

            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            applyChanges(
                    Constants.DEFAULT_HEAT_MAP_STEP,
                    Constants.DEFAULT_BRUTE_FORCE_STEP,
                    false,
                    true,
                    PathLossModels.DEFAULT_BREAK_POINT_DISTANCE + "",
                    PathLossModels.DEFAULT_NLOS_EXPONENT + "",
                    PathLossModels.DEFAULT_LOS_EXPONENT + "",
                    PathLossModels.DEFAULT_MIN_THRESHOLDLAPTOP + "",
                    PathLossModels.DEFAULT_MIN_THRESHOLDMOBILE + "");
            return false;
        }
    }

    private boolean writeJsonSettings(String settingsFilePath) {

        try {
            JsonWriter settingsWriter = Json.createWriter(new FileOutputStream(settingsFilePath));

            JsonObjectBuilder general = Json.createObjectBuilder();
            general.add(TAG_HEAT_MAP_STEP, Constants.HEAT_MAP_STEP);
            general.add(TAG_BRUTE_FORCE_STEP, Constants.BRUTE_FORCE_STEP);
            general.add(TAG_USE_BRUTE_FORCE, Optimality.useBruteForce);
            general.add(TAG_ENSURE_OPTIMALITY, Optimality.ensureOptimality);

            JsonObjectBuilder pathLossModel = Json.createObjectBuilder();
            pathLossModel.add(TAG_LOS, PathLossModels.LOS_EXPONENT + "");
            pathLossModel.add(TAG_NLOS, PathLossModels.NLOS_EXPONENT + "");
            pathLossModel.add(TAG_BP_DISTANCE, PathLossModels.BREAK_POINT_DISTANCE + "");
            pathLossModel.add(TAG_MIN_LAPTOP_THRESH, PathLossModels.MIN_THRESHOLDLAPTOP + "");
            pathLossModel.add(TAG_MIN_MOBILE_THRESH, PathLossModels.MIN_THRESHOLDMOBILE + "");

            JsonObjectBuilder settings = Json.createObjectBuilder();
            settings.add(TAG_GENERAL, general);
            settings.add(TAG_PATH_LOSS_MODEL, pathLossModel);

            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
            jsonObjectBuilder.add(TAG_SETTINGS, settings);

            settingsWriter.writeObject(jsonObjectBuilder.build());

            return true;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btApply;
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btDefaultBF;
    private javax.swing.JButton btDefaultBp;
    private javax.swing.JButton btDefaultExp;
    private javax.swing.JButton btDefaultHM;
    private javax.swing.JButton btDefaultThresh;
    private javax.swing.JButton btOK;
    private javax.swing.JCheckBox chbBruteForce;
    private javax.swing.JCheckBox chbEnsureOptimality;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JTabbedPane panelDrawingTool;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JPanel panelPLModels;
    private javax.swing.JTextField tfBPDist;
    private javax.swing.JTextField tfBruteForceStep;
    private javax.swing.JTextField tfHeatMapStep;
    private javax.swing.JTextField tfLOS;
    private javax.swing.JTextField tfLaptopThresh;
    private javax.swing.JTextField tfMobileThresh;
    private javax.swing.JTextField tfNLOS;
    // End of variables declaration//GEN-END:variables
}
