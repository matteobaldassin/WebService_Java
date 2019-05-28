/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
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
    final private String database = "verifica_ws";
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
        String targa, scadenzaAssicurazione, scadenzaBollo, classeInquinamento, ricercata;
        String url;
        String[] url_section;
        // verifica stato connessione a DBMS
        if (!connected) {
            response.sendError(500, "DBMS server error!");
            return;
        }
        // estrazione nominativo da URL

        url = request.getRequestURL().toString();
        url_section = url.split("/");

        if (url_section[url_section.length - 1].contains("visualizzaVeicoli")) {
            operazione = "visualizzaVeicoli";
        } else if (url_section[url_section.length - 2].contains("getTarga")) {
            operazione = "getTarga";
        }

        if (operazione == null) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        if (operazione.isEmpty()) {
            response.sendError(400, "Request syntax error!");
            return;
        }

        if (operazione.equals("visualizzaVeicoli")) {
            response.setContentType("text/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                Statement statement = circolari.createStatement();

                String sql = "SELECT * FROM `automobili`";
                ResultSet result = statement.executeQuery(sql);
                JSONArray array = new JSONArray();

                if (result.next()) {
                    do {
                        targa = result.getString(1);
                        scadenzaAssicurazione = result.getString(2);
                        scadenzaBollo = result.getString(3);
                        classeInquinamento = result.getString(4);
                        ricercata = result.getString(5);

                        JSONObject obj = new JSONObject();
                        obj.put("targa", targa);
                        obj.put("scadenzaAssicurazione", scadenzaAssicurazione);
                        obj.put("scadenzaBollo", scadenzaBollo);
                        obj.put("classeInquinamento", classeInquinamento);
                        obj.put("ricercata", ricercata);
                        array.put(obj);
                    } while (result.next());

                } else {
                    response.sendError(404, "Auto non trovate!");
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
        } else if (operazione.equals("getTarga")) {
            url = request.getRequestURL().toString();
            url_section = url.split("/");
            targa = url_section[url_section.length - 1];
            response.setContentType("text/json;charset=UTF-8");
            PrintWriter out = response.getWriter();

            JSONObject obj = new JSONObject();

            try {

                Statement statement = circolari.createStatement();

                String sql = "SELECT * FROM `automobili` where targa = '" + targa + "'";
                ResultSet result = statement.executeQuery(sql);

                if (result.next()) {
                    scadenzaAssicurazione = result.getString(2);
                    scadenzaBollo = result.getString(3);
                    classeInquinamento = result.getString(4);
                    ricercata = result.getString(5);
                    do {
                        obj.put("targa", targa);
                        obj.put("scadenzaAssicurazione", scadenzaAssicurazione);
                        obj.put("scadenzaBollo", scadenzaBollo);
                        obj.put("classeInquinamento", classeInquinamento);
                        obj.put("ricercata", ricercata);
                    } while (result.next());

                } else {
                    response.sendError(404, "Auto non trovata!");
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
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String operazione = "";
        String line = "";

        if (!connected) {
            response.sendError(505, "DBMS server error!");
            return;
        }

        BufferedReader input = request.getReader();

        String result = "";
        //BufferedWriter file = new BufferedWriter(new FileWriter("entry.xml"));
        while ((line = input.readLine()) != null) {
            result += line;
        }
        JSONObject obj = new JSONObject(result);
        input.close();

        PrintWriter out = response.getWriter();
        try {
            String Targa = obj.getString("Targa");
            String ScadenzaAssicurazione = obj.getString("ScadenzaAssicurazione");
            String ClasseInquinamento = obj.getString("ClasseInquinamento");
            String Ricercato = obj.getString("Ricercato");
            String ScadenzaBollo = obj.getString("ScadenzaBollo");

            if (Targa == null || ScadenzaAssicurazione == null || ClasseInquinamento == null || Ricercato == null || ScadenzaBollo == null) {
                response.sendError(400, "Malformed JSON!");
                return;
            }
            if (Targa.isEmpty() || ScadenzaAssicurazione.isEmpty() || ClasseInquinamento.isEmpty() || Ricercato.isEmpty() || ScadenzaBollo.isEmpty()) {
                response.sendError(400, "Malformed JSON!");
                return;
            }

            try {
                Statement statement = circolari.createStatement();
                String stringaSql = "INSERT INTO `automobili`(`targa`, `scadenzaAssicurazione`, `scadenzaBollo`, `classeInquinamento`, `ricercata`) VALUES ('" + Targa + "','" + ScadenzaAssicurazione + "','" + ScadenzaBollo + "'," + ClasseInquinamento + ",'" + Ricercato + "')";
                if (statement.executeUpdate(stringaSql) <= 0) {
                    statement.close();
                    return;
                }
                statement.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
                response.sendError(500, "DBMS server error!");
                return;
            }
            response.setStatus(200);

        } catch (Exception ex) {
        } finally {
            out.close();
        }

    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String line;

        // verifica stato connessione a DBMS
        if (!connected) {
            response.sendError(500, "DBMS server error!");
            return;
        }

        BufferedReader input = request.getReader();
        String result = "";
        while ((line = input.readLine()) != null) {
            result += line;
        }
        input.close();

        JSONObject obj = new JSONObject(result);

        try {

            String Targa = obj.getString("Targa");
            String Ricercato = obj.getString("Ricercato");
            //controlli sulla formazione corretta della stringa json ricevuto
            if (Targa == null || Ricercato == null) {
                response.sendError(400, "Malformed JSON!");
                return;
            }
            if (Targa.isEmpty() || Ricercato.isEmpty()) {
                response.sendError(400, "Malformed JSON!");
                return;
            }
            
            Statement statement = circolari.createStatement();
            if (statement.executeUpdate("UPDATE `automobili` SET ricercata='" + Ricercato + "'WHERE targa = '" + Targa + "';") <= 0) {
                response.sendError(404, "Auto non trovata");
                statement.close();
                return;
            }
            statement.close();
        } catch (SQLException e) {
            response.sendError(500, "DBMS server error!");
            return;
        }
        response.setStatus(200); // OK

    }

    @Override
    public String getServletInfo() {
        return "CircolariDB";
    }// </editor-fold>

}
