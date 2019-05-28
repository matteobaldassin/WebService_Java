/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 */
public class web_service {

    private String baseUrl;
    private int status;
    private Vector<String> valoriRichieste;

    web_service(String baseUrl) {
        this.baseUrl = baseUrl;

        this.status = 0;

        valoriRichieste = new Vector<>();
    }

    void printResult() {
        for (String a : valoriRichieste) {
            System.out.println(a);
        }
    }

    public int getFromComune(String comune) throws SAXException, ParserConfigurationException {
        try {
            //invio richiesta al web server

            URL server = new URL(baseUrl + "/getFromComune/" + comune);
            HttpURLConnection service = (HttpURLConnection) server.openConnection();

            service.setRequestProperty("Accept-Charset", "UTF-8");
            service.setRequestProperty("Accept", "text/json");

            service.setDoInput(true);
            service.setRequestMethod("GET");

            service.connect();

            status = service.getResponseCode();
            if (status != 200) {
                return status;
            }

            //ottenimento informazioni dal web server
            BufferedReader input = new BufferedReader(new InputStreamReader(service.getInputStream(), "UTF-8"));

            String line;
            String result = "";
            while ((line = input.readLine()) != null) {
                result += line;
            }
            String parseResult = parseJSONObject(result);
            System.out.println(parseResult);
            input.close();

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(web_service.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(web_service.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(web_service.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

    public int getFromProvincia(String provincia) throws SAXException, ParserConfigurationException {
        try {
            //invio richiesta al web server

            URL server = new URL(baseUrl + "/getFromProvincia/" + provincia);
            HttpURLConnection service = (HttpURLConnection) server.openConnection();

            service.setRequestProperty("Accept-Charset", "UTF-8");
            service.setRequestProperty("Accept", "text/json");

            service.setDoInput(true);
            service.setRequestMethod("GET");

            service.connect();

            status = service.getResponseCode();
            if (status != 200) {
                return status;
            }

            //ottenimento informazioni dal web server
            BufferedReader input = new BufferedReader(new InputStreamReader(service.getInputStream(), "UTF-8"));

            String array = input.readLine();
            String pop_totale = input.readLine();

            String parseArray = parseJSONArray(array);
            String parsePop_totale = parseJSONPOP_TOTALE(pop_totale);

            System.out.println(parseArray);
            System.out.println(parsePop_totale);

            //System.out.input.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(web_service.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(web_service.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(web_service.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

    public int getFromRegione(String regione) throws SAXException, ParserConfigurationException {
        try {
            //invio richiesta al web server

            URL server = new URL(baseUrl + "/getFromRegione/" + regione);
            HttpURLConnection service = (HttpURLConnection) server.openConnection();

            service.setRequestProperty("Accept-Charset", "UTF-8");
            service.setRequestProperty("Accept", "text/json");

            service.setDoInput(true);
            service.setRequestMethod("GET");

            service.connect();

            status = service.getResponseCode();
            if (status != 200) {
                return status;
            }

            //ottenimento informazioni dal web server
            BufferedReader input = new BufferedReader(new InputStreamReader(service.getInputStream(), "UTF-8"));

            String array = input.readLine();
            String parseArray = parseJSONArrayRegione(array);

            System.out.println(parseArray);

            //System.out.input.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(web_service.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(web_service.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(web_service.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }
    public int updatePopolazione(String comune, String popolazione) {
        try {
            //invio richiesta al web server
            URL server = new URL(baseUrl);
            HttpURLConnection service = (HttpURLConnection) server.openConnection();

            service.setRequestProperty("Content-type", "application/json");
            JSONObject obj = new JSONObject();
            obj.put("comune", comune);
            obj.put("nuova_popolazione", popolazione);

            service.setDoOutput(true);
            service.setRequestMethod("PUT");

            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(service.getOutputStream(), "UTF-8"));
            output.write(obj.toString());
            output.flush();
            output.close();

            service.connect();

            status = service.getResponseCode();

            return status;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(web_service.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(web_service.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(web_service.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }
    //metodo per parsare un JSONObject classico (per il comune)
    public String parseJSONObject(String stringa) {
        JSONObject obj = new JSONObject(stringa);
        String result = "";
        int id = obj.getInt("id");
        String comune = obj.getString("comune");
        String provincia = obj.getString("provincia");
        String regione = obj.getString("regione");
        int pop_residente = obj.getInt("pop_residente");

        result = "ID: " + id + "\n";
        result += "comune: " + comune + "\n";
        result += "provincia: " + provincia + "\n";
        result += "regione: " + regione + "\n";
        result += "pop_residente: " + pop_residente + "\n";

        return result;
    }
    //metodo per parsare un json object 
    public String parseJSONPOP_TOTALE(String stringa) {
        JSONObject obj = new JSONObject(stringa);
        String result = "";
        int pop_totale = obj.getInt("popolazione_totale");

        result = "POPTOTALE: " + pop_totale + "\n";
        return result;
    }
    //metodo per parsare json array di province
    String parseJSONArrayRegione(String input) {
        String result = "";
        JSONArray array = new JSONArray(input);
        int contaProvince = 0;
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String provincia = obj.getString("provincia");
            result += "Provincia numero: " + (++contaProvince) + "\n";
            result += "provincia: " + provincia + "\n";
            result += "----------------------------------------" + "\n";

        }
        return result;
    }
    //metodo per parsare un json array generale con tutti i campi
    String parseJSONArray(String input) {
        String result = "";
        JSONArray array = new JSONArray(input);
        int contaComuni = 0;
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            int id = obj.getInt("id");
            String comune = obj.getString("comune");
            String provincia = obj.getString("provincia");
            String regione = obj.getString("regione");
            int pop_residente = obj.getInt("pop_residente");
            result += "Comune numero: " + (++contaComuni) + "\n";
            result += "id: " + id + "\n";
            result += "comune: " + comune + "\n";
            result += "provincia: " + provincia + "\n";
            result += "regione: " + regione + "\n";
            result += "pop_residente: " + pop_residente + "\n";
            result += "----------------------------------------" + "\n";

        }
        return result;
    }

    
}
