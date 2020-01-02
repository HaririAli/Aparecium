package Forms;

import Utilities.SQLiteDbManager;
import Utilities.Constants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.colorchooser.AbstractColorChooserPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ALI
 */
public class NewMaterial extends javax.swing.JDialog {

    private boolean changes = false;
    private String materialColor = null;

    /**
     * Creates new form NewMaterial
     */
    public NewMaterial(JFrame parent) {
        super(parent);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            JOptionPane.showMessageDialog(this, e.toString(), Constants.ERROR, JOptionPane.WARNING_MESSAGE);
        }
        initComponents();
        this.setSize(new Dimension(375, 240));
        this.setResizable(false);
    }

    public boolean showDialog() {
        setVisible(true);
        return changes;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        tfName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tfAtten = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btAdd = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        tfThickness = new javax.swing.JTextField();
        btChooseColor = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Material");
        setMaximumSize(new java.awt.Dimension(375, 245));
        setMinimumSize(new java.awt.Dimension(375, 245));
        setPreferredSize(new java.awt.Dimension(375, 245));

        jLabel1.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel1.setText("Name:");

        tfName.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel2.setText("Attenuation:");

        tfAtten.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tfAtten.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tfAttenKeyTyped(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel3.setText("dBm");

        btAdd.setText("Add");
        btAdd.setMaximumSize(new java.awt.Dimension(69, 32));
        btAdd.setMinimumSize(new java.awt.Dimension(69, 32));
        btAdd.setPreferredSize(new java.awt.Dimension(69, 32));
        btAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAddActionPerformed(evt);
            }
        });

        btCancel.setText("Close");
        btCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel4.setText("Thickness:");

        tfThickness.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        btChooseColor.setText("Choose Color");
        btChooseColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btChooseColorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(116, 116, 116)
                        .addComponent(btAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btChooseColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tfAtten, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tfName))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tfThickness, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfThickness, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfAtten, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addComponent(btChooseColor)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btCancel))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddActionPerformed
        // TODO add your handling code here:
        if (tfName.getText().equals("") || tfAtten.getText().equals("") || tfThickness.getText().equals("") || materialColor == null) {
            JOptionPane.showMessageDialog(this, Constants.MISSING_FEILDS_MSG, Constants.MISSING_FEILDS_TITLE, JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                String materialName = tfName.getText() + " " + tfThickness.getText() + "cm";
                int i = SQLiteDbManager.ExecuteNonQuery(SQLiteDbManager.connect(),
                        "INSERT INTO Material (Material_Name, Attenuation, Color) VALUES ('" + materialName + "', " + tfAtten.getText() + ", '" + materialColor + "')");
                if (i == 0) {
                    JOptionPane.showMessageDialog(this, Constants.NEW_MATERIAL_ERROR_MSG, Constants.NEW_MATERIAL_ERROR_TITLE, JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, Constants.NEW_MATERIAL_SUCCESS_MSG, Constants.NEW_MATERIAL_SUCCESS_TITLE, JOptionPane.INFORMATION_MESSAGE);
                    tfAtten.setText("");
                    tfName.setText("");
                    tfThickness.setText("");
                    getContentPane().setBackground(Color.WHITE);
                    changes = true;
                    //this.DialogResult = DialogResult.OK;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, Constants.NEW_MATERIAL_ERROR_MSG, Constants.ERROR, JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btAddActionPerformed

    private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_btCancelActionPerformed

    private void btChooseColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btChooseColorActionPerformed
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
        int input = JOptionPane.showOptionDialog(null, colorChooser, "The title", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, colorChooser);

        if (input == JOptionPane.OK_OPTION) {
            String rgb = colorChooser.getColor().getRed() + "," + colorChooser.getColor().getGreen() + "," + colorChooser.getColor().getBlue();
            materialColor = rgb;
            getContentPane().setBackground(colorChooser.getColor());
        }
    }//GEN-LAST:event_btChooseColorActionPerformed

    private void tfAttenKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfAttenKeyTyped
         if (!evt.isControlDown() && !Character.isDigit(evt.getKeyChar()) && evt.getKeyChar() != '.' && evt.getKeyChar() != '-') {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }// only allow one decimal point
        else if (evt.getKeyChar() == '.' && tfAtten.getText().indexOf('.') > -1) {
            evt.consume();
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_tfAttenKeyTyped

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btChooseColor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField tfAtten;
    private javax.swing.JTextField tfName;
    private javax.swing.JTextField tfThickness;
    // End of variables declaration//GEN-END:variables
}
