/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 */
public class mainClient {

    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        web_service webService = new web_service("http://localhost:8080/ProvaWeb");
        int responseCode;
        String comune;
        
        System.out.println("Inserisci l'operazione desiderata");
        System.out.println("1 - get From COMUNE");
        System.out.println("2 - aggiorna popolazione comune");
        System.out.println("3 - get From Provincia (ritorna comuni e pop_totale");
        System.out.println("4 - get from Regione ");
        System.out.println("0 - Termina programma");

        String scelta = br.readLine();
        System.out.println("");

        while (!scelta.equals("0")) {
            switch (Integer.parseInt(scelta)) {
                case 1:
                    System.out.println("inserisci comune da ricercare");
                    comune = br.readLine();
                    responseCode = webService.getFromComune(comune);
                    System.out.println("CODICE RISPOSTA: "+responseCode);

                    break;

                case 2:
                    System.out.println("inserisci il comune da ricercare");
                    comune = br.readLine();
                    System.out.println("inserisci la nuova popolazione: ");
                    String popolazione = br.readLine();
                    responseCode = webService.updatePopolazione(comune, popolazione);
                    //UPDATE `automobili` SET `ricercata`=1 WHERE targa="ddd"
                    if (responseCode == 200) {
                        System.out.println("Aggiornamento effettuato");
                    } else {
                        System.out.println("Aggiornamento non effettuato");
                         System.out.println("Codice errore: "+responseCode);

                    }

                    break;
                case 3:
                    System.out.println("inserisci la provincia da ricercare");
                    String provincia = br.readLine();
                    responseCode = webService.getFromProvincia(provincia);
                    System.out.println("CODICE RISPOSTA: "+responseCode);
                    break;
                case 4:
                    System.out.println("inserisci la regione da ricercare");
                    String regione = br.readLine();
                    responseCode = webService.getFromRegione(regione);
                    System.out.println("CODICE RISPOSTA: "+responseCode);

                    break;

            }

            System.out.println("Inserisci l'operazione desiderata");
            System.out.println("1 - GET FROM COMUNE");
            System.out.println("2 - AGGIORNA popolazione comune");
            System.out.println("3 - GET From Provincia (ritorna comuni e pop_totale");
            System.out.println("4 - GET from Regione ");
            System.out.println("0 - Termina programma");

            scelta = br.readLine();
            System.out.println("");
        }
    }
}
