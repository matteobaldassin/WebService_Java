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
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
    {
        web_service webService = new web_service("http://localhost:8080/ProvaWeb");
        
        System.out.println("Inserisci l'operazione desiderata");
        System.out.println("1 - Inserisci Nuove Vetture");
        System.out.println("2 - Get All");
        System.out.println("3 - get From targa");
        System.out.println("4 - Update auto (ricercata)");
        System.out.println("0 - Termina programma");
        
        String scelta = br.readLine();
        System.out.println("");
        
        while(!scelta.equals("0"))
        {
            switch(Integer.parseInt(scelta)){
                case 1:
                    System.out.println("Inserisci Nuovo Veicolo");
                    
                    System.out.println("Targa");
                    String Targa = br.readLine();
                    
                    System.out.println("Scadenza assicurazione (yyyy-mm-dd)");
                    String DataAssicurazione = br.readLine();
                    
                    System.out.println("Scadenza bollo (yyyy-mm-dd)");
                    String DataBollo =  br.readLine();
                    
                    System.out.println("Classe Inquinamento (da 0 a 6)");
                    String ClasseInquinamento = br.readLine();
                    
                    System.out.println("Ricercato? 0/1");
                    String Ricercato = br.readLine();
                    
                    int ritorno = webService.inserisciMacchina(Targa,DataAssicurazione,DataBollo ,ClasseInquinamento, Ricercato);
                    if(ritorno == 200)
                        System.out.println("OK! Inserito");
                    else
                        System.out.println(ritorno);
                    
                    break;
                case 2:
                    ritorno = webService.getAll();
                    webService.printResult();
                    break;
                case 3:
                    System.out.println("inserisci targa da ricercare");
                    String targa;
                    targa = br.readLine();
                    ritorno = webService.getFromTarga(targa);
                    break;
                    
                case 4:                    
                    System.out.println("inserisci targa da ricercare");
                    targa = br.readLine();      
                    System.out.println("inserisci lo stato (0/1)");
                    String ricercato;
                    ricercato = br.readLine();
                    ritorno =webService.update(targa,ricercato);
                    //UPDATE `automobili` SET `ricercata`=1 WHERE targa="ddd"
                    if(ritorno == 200)
                        System.out.println("Inserimento effettuato");
                    else
                        System.out.println("Inserimento non effettuato");
                    
                    
                    break;
                default:
            }
            
            System.out.println("Inserisci l'operazione desiderata");
            System.out.println("1 - Inserisci Nuove Vetture");
            System.out.println("2 - Get All");
            System.out.println("3 - get From targa");
            System.out.println("4 - Update auto (ricercata)");
            System.out.println("0 - Termina programma");
            
            scelta = br.readLine();
            System.out.println("");
        }
    }
}
