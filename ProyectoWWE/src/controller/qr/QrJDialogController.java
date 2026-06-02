/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.qr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import service.ServicioQr;
import view.qr.QrJDialog;

public class QrJDialogController {

    private final QrJDialog dialog;
    private final String url;

    public QrJDialogController(QrJDialog dialog, String url) {
        this.dialog = dialog;
        this.url = url;

        initEvents();
        procesarQr();
    }

    private void initEvents() {
        this.dialog.getBtnPechar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
    }

    private void procesarQr() {
        ImageIcon qrIcon = ServicioQr.generarQRDeTexto(url, 250, 250);

        if (qrIcon != null) {
            this.dialog.getLblQR().setIcon(qrIcon);
            this.dialog.getTxtUrl().setText(url);
        } else {
            JOptionPane.showMessageDialog(dialog, "Error ao xerar o QR.");
            this.dialog.dispose();
        }
    }
}
