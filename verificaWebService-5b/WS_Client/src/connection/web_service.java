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

    public int getAll() throws ParserConfigurationException, SAXException {
        try {
            //invio richiesta al web server

            URL server = new URL(baseUrl + "/visualizzaVeicoli");
            HttpURLConnection service = (HttpURLConnection) server.openConnection();

            service.setRequestProperty("Accept-Charset", "UTF-8");
            service.setRequestProperty("Accept", "text/xml");

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
            String parseResult = "";
            parseResult += parseJSONArray(input.readLine());

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

    public int inserisciMacchina(String Targa, String ScadenzaAssicurazione, String ScadenzaBollo, String ClasseInquinamento, String Ricercato) throws ParserConfigurationException, SAXException {
        try {
            //invio richiesta al web server
            URL server = new URL(baseUrl);
            HttpURLConnection service = (HttpURLConnection) server.openConnection();

            service.setRequestProperty("Content-type", "application/json");
            JSONObject obj = new JSONObject();
            obj.put("Targa", Targa);
            obj.put("ScadenzaAssicurazione", ScadenzaAssicurazione);
            obj.put("ClasseInquinamento", ClasseInquinamento);
            obj.put("Ricercato", Ricercato);
            obj.put("ScadenzaBollo", ScadenzaBollo);

            service.setDoOutput(true);
            service.setRequestMethod("POST");

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

    void printResult() {
        for (String a : valoriRichieste) {
            System.out.println(a);
        }
    }

    int getFromTarga(String Targa) throws SAXException, ParserConfigurationException {
        try {
            //invio richiesta al web server

            URL server = new URL(baseUrl + "/getTarga/" + Targa);
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

    public String parseJSONObject(String stringa) {
        JSONObject obj = new JSONObject(stringa);
        String result = "";
        String targa = obj.getString("targa");
        String assicurazione = obj.getString("scadenzaAssicurazione");
        String scadenza = obj.getString("scadenzaBollo");
        String classeInquinamento = obj.getString("classeInquinamento");
        String ricercata = obj.getString("ricercata");

        result = "Targa: " + targa + "\n";
        result += "scadenza assicurazione: " + assicurazione + "\n";
        result += "scadenza bollo: " + scadenza + "\n";
        result += "classe inquinamento: " + classeInquinamento + "\n";
        result += "ricercata: " + ricercata + "\n";

        return result;
    }

    String parseJSONArray(String input) {
        String result = "";
        JSONArray array = new JSONArray(input);
        int contaAuto = 0;
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String targa = obj.getString("targa");
            String assicurazione = obj.getString("scadenzaAssicurazione");
            String scadenza = obj.getString("scadenzaBollo");
            String classeInquinamento = obj.getString("classeInquinamento");
            String ricercata = obj.getString("ricercata");
            result += "Macchina numero: " + (++contaAuto) + "\n";
            result += "Targa: " + targa + "\n";
            result += "scadenza assicurazione: " + assicurazione + "\n";
            result += "scadenza bollo: " + scadenza + "\n";
            result += "classe inquinamento: " + classeInquinamento + "\n";
            result += "ricercata: " + ricercata + "\n";
            result += "----------------------------------------" + "\n";

        }
        return result;
    }

    public int update(String targa, String ricercato) {
        try {
            //invio richiesta al web server
            URL server = new URL(baseUrl);
            HttpURLConnection service = (HttpURLConnection) server.openConnection();

            service.setRequestProperty("Content-type", "application/json");
            JSONObject obj = new JSONObject();
            obj.put("Targa", targa);
            obj.put("Ricercato", ricercato);


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
}
