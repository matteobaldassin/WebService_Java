/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.

CIAOOOO
 */
package data;

import java.sql.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 *
 */
public class ProvaWeb extends HttpServlet {

    final private String driver = "com.mysql.jdbc.Driver";
    final private String dbms_url = "jdbc:mysql://localhost/";
    final private String database = "verifica02";
    final private String user = "root";
    final private String password = "";
    private Connection circolari;
    private boolean connected;

    // attivazione servlet (connessione a DBMS)
    public void init() {
        String url = dbms_url + database;
        try {
            Class.forName(driver);
            circolari = DriverManager.getConnection(url, user, password);
            connected = true;
        } catch (SQLException e) {
            connected = false;
        } catch (ClassNotFoundException e) {
            connected = false;
        }
    }

    // disattivazione servlet (disconnessione da DBMS)
    public void destroy() {
        try {
            circolari.close();
        } catch (SQLException e) {
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet WS_Phone</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet WS_Phone at " + request.getContextPath() + "</h1>");
            out.println("<p> Prova</p>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     *
     * examples of use: http://localhost:8080/ProvaWeb
     * http://localhost:8080/ProvaWeb/visualizzaUtenti
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String operazione = "";
        int id, pop_residente;
        String comune, provincia, regione;
        String url;
        String[] url_section;
        // verifica stato connessione a DBMS
        if (!connected) {
            response.sendError(500, "DBMS server error!");
            return;
        }
        // estrazione nominativo da URL

        url = request.getRequestURL().toString();
        //url richiesto viene splittato in base agli /
        url_section = url.split("/");

        /* if (url_section[url_section.length - 1].contains("visualizzaTutto")) {
            operazione = "visualizzaTutto";
         */
        //se il penultimo campo dell'url continene "..." allora l'ultimo conterrà il parametro da usare
        if (url_section[url_section.length - 2].contains("getFromProvincia")) {
            operazione = "getFromProvincia";
        } else if (url_section[url_section.length - 2].contains("getFromRegione")) {
            operazione = "getFromRegione";
        } else if (url_section[url_section.length - 2].contains("getFromComune")) {
            operazione = "getFromComune";
        }

        if (operazione == null) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        if (operazione.isEmpty()) {
            response.sendError(400, "Request syntax error!");
            return;
        }

        if (operazione.equals("getFromProvincia")) {
            response.setContentType("text/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                Statement statement = circolari.createStatement();
                provincia = url_section[url_section.length - 1];

                //la stringa viene ricevuta col %20 se c'e uno spazio
                if (provincia.contains("%20")) // se la stringa comune contiene il %20             
                    provincia = provincia.replace("%20", " "); //viene sostituito con lo spazio
                
                int pop_totale = 0;
                String sql = "SELECT * FROM `comuni` WHERE provincia = '" + provincia + "'";
                ResultSet result = statement.executeQuery(sql);
                JSONArray array = new JSONArray();
                JSONObject obj_pop = new JSONObject();

                if (result.next()) {
                    do {
                        id = result.getInt(1);
                        comune = result.getString(2);
                        provincia = result.getString(3);
                        regione = result.getString(4);
                        pop_residente = result.getInt(5);
                        JSONObject obj = new JSONObject();

                        obj.put("id", id);
                        obj.put("comune", comune);
                        obj.put("provincia", provincia);
                        obj.put("regione", regione);
                        obj.put("pop_residente", pop_residente);
                        array.put(obj);
                        //conto per popolazione totale
                        pop_totale += pop_residente;
                    } while (result.next());
                    obj_pop.put("popolazione_totale", pop_totale);
                } else {
                    response.sendError(404, "Provincia inserita non trovata!");
                    result.close();
                    statement.close();
                    return;
                }

                out.println(array.toString());
                out.println(obj_pop.toString());

            } catch (Exception ex) {
                //exception
            } finally {
                out.close();
                response.setStatus(200); // OK
            }
        } else if (operazione.equals("getFromRegione")) {
            response.setContentType("text/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                Statement statement = circolari.createStatement();
                regione = url_section[url_section.length - 1];
                //la stringa viene ricevuta col %20 se c'e uno spazio
                if (regione.contains("%20")) // se la stringa comune contiene il %20
                {
                    regione = regione.replace("%20", " "); //viene sostituito con lo spazio
                }
                String sql = "SELECT DISTINCT provincia FROM `comuni` WHERE regione = '" + regione + "'";
                ResultSet result = statement.executeQuery(sql);
                JSONArray array = new JSONArray();

                if (result.next()) {
                    do {
                        provincia = result.getString(1);

                        JSONObject obj = new JSONObject();
                        obj.put("provincia", provincia);

                        array.put(obj);
                    } while (result.next());
                } else {
                    response.sendError(404, "Regione inserita non trovata!");
                    result.close();
                    statement.close();
                    return;
                }

                out.println(array.toString());

            } catch (Exception ex) {
                //exception
            } finally {
                out.close();
                response.setStatus(200); // OK
            }

        } else if (operazione.equals("getFromComune")) {
            url = request.getRequestURL().toString();
            url_section = url.split("/");
            comune = url_section[url_section.length - 1];
            //la stringa viene ricevuta col %20 se c'e uno spazio
            if (comune.contains("%20")) // se la stringa comune contiene il %20
            {
                comune = comune.replace("%20", " "); //viene sostituito con lo spazio
            }
            response.setContentType("text/json;charset=UTF-8");
            PrintWriter out = response.getWriter();

            JSONObject obj = new JSONObject();

            try {

                Statement statement = circolari.createStatement();

                String sql = "SELECT * FROM `comuni` where comune = '" + comune + "'";
                ResultSet result = statement.executeQuery(sql);

                if (result.next()) {
                    id = result.getInt(1);
                    //comune gia lo ho
                    provincia = result.getString(3);
                    regione = result.getString(4);
                    pop_residente = result.getInt(5);
                    do {
                        obj.put("id", id);
                        obj.put("comune", comune);
                        obj.put("provincia", provincia);
                        obj.put("regione", regione);
                        obj.put("pop_residente", pop_residente);
                    } while (result.next());

                } else {
                    response.sendError(404, "Comune non trovato nel database!");
                    result.close();
                    statement.close();
                    return;
                }

                out.println(obj.toString());

            } catch (Exception ex) {
                //exception
            } finally {
                out.close();
                response.setStatus(200); // OK
            }
        }
    }

    /**
     * Handles the HTTP <code>PUT</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //per update a DB
        String line;

        // verifica stato connessione a DBMS
        if (!connected) {
            response.sendError(502, "DBMS server error!");
            return;
        }

        BufferedReader input = request.getReader();
        String result = input.readLine();
        input.close();

        JSONObject obj = new JSONObject(result);

        try {

            String comune = obj.getString("comune");
            String nuova_popolazione = obj.getString("nuova_popolazione");
            //controlli sulla formazione corretta della stringa json ricevuto
            if (comune == null || nuova_popolazione == null) {
                response.sendError(400, "Malformed JSON!");
                return;
            }
            if (comune.isEmpty() || nuova_popolazione.isEmpty()) {
                response.sendError(400, "Malformed JSON!");
                return;
            }

            Statement statement = circolari.createStatement();
            String sql = "UPDATE `comuni` SET pop_residente='" + nuova_popolazione + "' WHERE comune = '" + comune + "';";

            int resultSQL = statement.executeUpdate(sql);
            if (resultSQL <= 0) {
                response.sendError(404, "Comune non trovata");
                statement.close();
                return;
            }
            statement.close();
        } catch (SQLException e) {
            response.sendError(501, "Errore sulla query!");
        }
        response.setStatus(200); // OK

    }

    @Override
    public String getServletInfo() {
        return "CircolariDB";
    }// </editor-fold>

}
