/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.stejskal.global;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Norbert
 */
public class FileOperation {

    public static BufferedReader loadData(String filename) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
            return br;

        } catch (UnsupportedEncodingException ex) {
            //showMessage(true, "Encoding Exception", "Codepage nicht verfügbar!");
            ex.printStackTrace();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            //Logger.getLogger(Global.class.getName()).log(Level.SEVERE, null, ex);
            //showMessage(true, "Not Found Exception", "Datei nicht gefunden: " + filename);
        }
        return null;

    }

    public static void printData(String collectedData, String filename) {
        Writer out = null;

        try {

            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename), "Cp1252"));
            out.write(collectedData);

        } catch (UnsupportedEncodingException ex) {
            //showMessage(true, "Encoding Exception", "Codepage nicht verfügbar!");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            //Logger.getLogger(Global.class.getName()).log(Level.SEVERE, null, ex);
            //showMessage(true, "Not Found Exception", "Datei nicht gefunden: " + filename);
        } catch (IOException ex) {
            //showMessage(true, "IO Exception", "Fehler beim Schrieben der Datei:" + filename);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {

            }
        }

    }
}
