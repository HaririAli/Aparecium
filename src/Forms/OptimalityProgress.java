/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms;

import Entities.AccessPointModel;
import Utilities.APOptimalPlaceCalculation;
import Utilities.BruteForceCalculation;
import Utilities.Constants;
import Utilities.Optimality;
import javax.swing.JFrame;

/**
 *
 * @author ALI
 */
public class OptimalityProgress extends javax.swing.JDialog {
    
    private APOptimalPlaceCalculation optimalityThread;
    private BruteForceCalculation bruteForceThread;
    private boolean isThreadRunning = false;

    /**
     * Creates new form OptimalityProgress
     */
    
    public OptimalityProgress(JFrame parent, AccessPointModel apModel, Canvas canvas) {
        super(parent);
        initComponents();
        this.setLocationRelativeTo(null);
        setTitle(Constants.OPTIMAL_LOCATION + (Constants.isLaptop ? Constants.LAPTOP : Constants.MOBILE));
        if(Optimality.ensureOptimality && !Optimality.useBruteForce){
            optimalityThread = new APOptimalPlaceCalculation(canvas, pbOptimality, apModel, this);
            optimalityThread.start();
        }
        else{
            bruteForceThread = new BruteForceCalculation(canvas, pbOptimality, apModel, this);
            bruteForceThread.start();
        }
        
        isThreadRunning = true;
    }
    
    public void setMessage(String msg){
        lblOptimality.setText(msg);
        btOkCancel.setText("OK");
        pbOptimality.setValue(pbOptimality.getMaximum());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblOptimality = new javax.swing.JLabel();
        pbOptimality = new javax.swing.JProgressBar();
        btOkCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(500, 250));
        setMinimumSize(new java.awt.Dimension(500, 250));
        setResizable(false);

        lblOptimality.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 16)); // NOI18N
        lblOptimality.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOptimality.setText("Finding optimal location. This might take a While, please wait...");

        btOkCancel.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        btOkCancel.setText("Cancel");
        btOkCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOkCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pbOptimality, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btOkCancel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(lblOptimality, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(lblOptimality)
                .addGap(49, 49, 49)
                .addComponent(pbOptimality, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addComponent(btOkCancel)
                .addGap(32, 32, 32))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btOkCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOkCancelActionPerformed
        // TODO add your handling code here:
        if(isThreadRunning){
           if(Optimality.ensureOptimality && !Optimality.useBruteForce)
                optimalityThread.cancel();
            else
                bruteForceThread.cancel();
        }
        this.dispose();  
    }//GEN-LAST:event_btOkCancelActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btOkCancel;
    private javax.swing.JLabel lblOptimality;
    private javax.swing.JProgressBar pbOptimality;
    // End of variables declaration//GEN-END:variables
}
