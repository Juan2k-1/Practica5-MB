package Controlador;

import Modelo.Consulta;
import Modelo.ConsultaTabla;
import Modelo.Documento;
import Modelo.DocumentoTabla;
import Vista.VistaCargarFicheroConsultas;
import Vista.VistaConsultas;
import Vista.VistaIndexarConsultas;
import Vista.VistaIndexarDocumentos;
import Vista.VistaMensaje;
import Vista.VistaMostrarDocumentosIndexados;
import Vista.VistaPorDefecto;
import Vista.VistaPrincipal;
import Vista.VistaSeleccionarFichero;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

public class ControladorPrincipal implements ActionListener
{

    private VistaMensaje vMensaje;
    private VistaPorDefecto vPorDefecto;
    private VistaPrincipal vPrincipal;
    private VistaIndexarDocumentos vIndexar;
    private VistaSeleccionarFichero vSeleccionarFichero;
    private VistaMostrarDocumentosIndexados vDocumentosIndexados;
    private VistaCargarFicheroConsultas vCargarFicheroConsulta;
    private VistaIndexarConsultas vIndexarConsultas;
    private VistaConsultas vConsultas;
    private DocumentoTabla dTabla;
    private ConsultaTabla cTabla;
    private SolrClient solrClient;
    private ArrayList<Documento> documentos;
    private Process process;
    private String nombreCore;

    public ControladorPrincipal(VistaMensaje vMensaje, SolrClient solrClient, Process process)
    {
        this.vMensaje = vMensaje;
        this.nombreCore = "CORPUS";
        this.solrClient = solrClient;
        this.vPorDefecto = new VistaPorDefecto();
        this.vPrincipal = new VistaPrincipal();
        this.vIndexar = new VistaIndexarDocumentos();
        this.vDocumentosIndexados = new VistaMostrarDocumentosIndexados();
        this.vCargarFicheroConsulta = new VistaCargarFicheroConsultas();
        this.vSeleccionarFichero = new VistaSeleccionarFichero();
        this.vIndexarConsultas = new VistaIndexarConsultas();
        this.vConsultas = new VistaConsultas();
        this.dTabla = new DocumentoTabla(vDocumentosIndexados);
        this.cTabla = new ConsultaTabla(vConsultas);
        this.process = process;

        addListeners();

        this.vPrincipal.setLayout(new CardLayout());

        this.vPrincipal.add(vPorDefecto);
        this.vPrincipal.add(vIndexar);
        this.vPrincipal.add(vSeleccionarFichero);
        this.vPrincipal.add(vDocumentosIndexados);
        this.vPrincipal.add(vConsultas);
        this.vPrincipal.add(vCargarFicheroConsulta);
        this.vPrincipal.add(vIndexarConsultas);

        this.vPorDefecto.setVisible(true);
        this.vIndexar.setVisible(false);
        this.vSeleccionarFichero.setVisible(false);
        this.vDocumentosIndexados.setVisible(false);
        this.vConsultas.setVisible(false);
        this.vCargarFicheroConsulta.setVisible(false);
        this.vIndexarConsultas.setVisible(false);

        this.vPrincipal.setLocationRelativeTo(null); //Para que la ventana se muestre en el centro de la pantalla
        this.vPrincipal.setVisible(true); // Para hacer la ventana visible
        this.vPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Para que la ventana se cierra cuando le doy a cerrar

        this.vConsultas.jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.vConsultas.jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case "IndexarDocumentos":
            {
                this.vPorDefecto.setVisible(false);
                this.vIndexar.setVisible(false);
                this.vDocumentosIndexados.setVisible(false);
                this.vSeleccionarFichero.setVisible(false);
                this.vIndexar.setVisible(true);
                break;
            }
            case "SeleccionarFichero":
            {
                this.vPorDefecto.setVisible(false);
                this.vIndexar.setVisible(false);
                this.vDocumentosIndexados.setVisible(false);
                this.vSeleccionarFichero.setVisible(true);
                int seleccion = this.vSeleccionarFichero.jFileChooserFicheros.showOpenDialog(null);
                if (seleccion == JFileChooser.APPROVE_OPTION)
                {
                    File ficheroSeleccionado = this.vSeleccionarFichero.jFileChooserFicheros.getSelectedFile();
                    String cisiAllFilePath = ficheroSeleccionado.getAbsolutePath();
                    this.vIndexar.jTextFieldFichero.setText(cisiAllFilePath);
                    if (cisiAllFilePath == null)
                    {
                        this.vMensaje.MensajeDeError("Error, ruta vacía");
                    } else
                    {
                        try
                        {
                            this.documentos = indexarDocumentos(solrClient, cisiAllFilePath);
                            this.vMensaje.MensajeInformacion("¡Documentos indexados con éxito!");
                        } catch (SolrServerException | IOException ex)
                        {
                            Logger.getLogger(ControladorPrincipal.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                        }
                    }
                }
                break;
            }
            case "MostrarDocumentosIndexados":
            {
                if (this.documentos == null)
                {
                    this.vMensaje.MensajeDeError("Error, no hay documentos indexados");
                } else
                {
                    this.vDocumentosIndexados.setVisible(true);
                    this.vPorDefecto.setVisible(false);
                    this.vIndexar.setVisible(false);
                    this.vSeleccionarFichero.setVisible(false);
                    this.vDocumentosIndexados.jTableMostrarDocumentos.setModel(dTabla);
                    DiseñoTablaDocumentos();
                    pideDocumentos();
                }
                break;
            }
            case "CargarFicheroConsultas":
            {
                this.vPorDefecto.setVisible(false);
                this.vIndexar.setVisible(false);
                this.vDocumentosIndexados.setVisible(false);
                this.vSeleccionarFichero.setVisible(false);
                this.vIndexarConsultas.setVisible(true);
                break;
            }
            case "SeleccionarFicheroConsultas":
            {
                this.vPorDefecto.setVisible(false);
                this.vIndexar.setVisible(false);
                this.vDocumentosIndexados.setVisible(false);
                this.vSeleccionarFichero.setVisible(false);
                this.vIndexarConsultas.setVisible(false);
                this.vCargarFicheroConsulta.setVisible(true);
                int seleccion = this.vCargarFicheroConsulta.jFileChooserConsultas.showOpenDialog(null);
                if (seleccion == JFileChooser.APPROVE_OPTION)
                {
                    File ficheroSeleccionado = this.vCargarFicheroConsulta.jFileChooserConsultas.getSelectedFile();
                    String cisiQueryFilePath = ficheroSeleccionado.getAbsolutePath();
                    this.vIndexarConsultas.jTextFieldFichero.setText(cisiQueryFilePath);
                    if (cisiQueryFilePath == null)
                    {
                        this.vMensaje.MensajeDeError("Error, ruta vacía");
                    } else
                    {
                        try
                        {
                            indexarConsultas(cisiQueryFilePath);
                            this.vMensaje.MensajeInformacion("¡Consultas indexados con éxito!");
                        } catch (IOException ex)
                        {
                            Logger.getLogger(ControladorPrincipal.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                        }

                    }
                }
                break;
            }
            case "Generar fichero de evaluación":
            {
                String solrUrl = "http://localhost:8983/solr/" + this.nombreCore;
                SolrClient solrClientmicoleccion = new HttpSolrClient.Builder(solrUrl).build();
                //String cisiQueryFilePath = "src\\main\\java\\resources\\CISI.QRY";
                String cisiQueryFilePath = this.vIndexarConsultas.jTextFieldFichero.getText();
                String trecTopFilePath = "trec_solr_file.trec";

                try
                {
                    Map<Integer, String> queries = indexarConsultas(cisiQueryFilePath);
                    BufferedWriter writer = new BufferedWriter(new FileWriter(trecTopFilePath));

                    for (Map.Entry<Integer, String> entry : queries.entrySet())
                    {
                        int queryId = entry.getKey();
                        System.out.println(queryId);
                        String queryString = entry.getValue();
                        System.out.println(queryString);

                        SolrQuery solrQuery = new SolrQuery();
                        solrQuery.setQuery("content:" + queryString);
                        solrQuery.set("fl", "id,score");

                        List<String> relevantDocuments = performSolrSearch(solrClientmicoleccion, solrQuery);

                        // Escribe los resultados en el archivo trec_top_file
                        for (int i = 0; i < relevantDocuments.size(); i++)
                        {
                            writer.write(queryId + " ");
                            writer.write(relevantDocuments.get(i) + "\n");
                        }
                    }
                    writer.close();
                    solrClientmicoleccion.close();
                    this.vMensaje.MensajeInformacion("¡Fichero generado con éxito!");
                    System.out.println("Los resultados se han escrito en " + trecTopFilePath);
                } catch (IOException | SolrServerException ex)
                {
                    Logger.getLogger(ControladorPrincipal.class.getName()).log(Level.SEVERE, ex.getMessage(), e);
                }
                break;
            }
            case "RealizarConsulta":
            {
                this.vConsultas.setVisible(true);
                this.vDocumentosIndexados.setVisible(false);
                this.vPorDefecto.setVisible(false);
                this.vIndexar.setVisible(false);
                this.vSeleccionarFichero.setVisible(false);
                this.vConsultas.jTableConsultas.setModel(cTabla);
                DiseñoTablaConsultas();
                break;
            }
            case "Buscar":
            {
                int i = 1;
                String contenido = this.vConsultas.jTextFieldConsulta.getText();
                Consulta consulta = new Consulta(i, contenido);
                ArrayList<Documento> documentos = null;
                try
                {
                    documentos = buscar(consulta);
                } catch (SolrServerException | IOException ex)
                {
                    Logger.getLogger(ControladorPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
                documentosEncontrados(documentos);
                i++;
                break;
            }

            case "Desconectar":
            {
                try
                {
                    this.solrClient.close();
                    this.vMensaje.MensajeInformacion("Desconectado del servidor con éxito");
                } catch (IOException ex)
                {
                    Logger.getLogger(ControladorPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case "Cerrar Servidor":
            {
                stopSolrServer();
                this.vMensaje.MensajeInformacion("Servidor Solr, cerrado con éxito");
                this.vPrincipal.dispose();
                break;
            }
            case "Salir":
            {
                this.vPrincipal.dispose();
                System.exit(0);
                break;
            }
        }
    }

    private List<String> performSolrSearch(SolrClient solr, SolrQuery query)
            throws SolrServerException, IOException
    {
        // Realiza la búsqueda en Solr y devuelve una lista de documentos relevantes
        QueryResponse response = solr.query(query);
        SolrDocumentList results = response.getResults();
        List<String> relevantDocumentIds = new ArrayList<>();
        int ranking = 1;
        for (SolrDocument document : results)
        {
            String trecLine = "QO" + " " + (String) document.getFieldValue("id") + " " + ranking + " " + document.getFieldValue("score").toString() + " " + "ETSI";
            relevantDocumentIds.add(trecLine);
            ranking++;
        }
        return relevantDocumentIds;
    }

    private ArrayList<Documento> buscar(Consulta consulta) throws SolrServerException, IOException
    {
        ArrayList<Documento> documentos = new ArrayList<>();
        SolrQuery solrQuery = new SolrQuery();
        String solrUrl = "http://localhost:8983/solr/" + this.nombreCore;
        SolrClient solrClientmicoleccion = new HttpSolrClient.Builder(solrUrl).build();
        solrQuery.setQuery("content:" + consulta.getContenido());
        solrQuery.set("fl", "id,author,title,content,score");

        solrQuery.setHighlight(true);
        solrQuery.addHighlightField("content");  // Campo que se resaltará
        solrQuery.setHighlightSimplePre("<em><b>");  // Etiqueta de inicio del resaltado
        solrQuery.setHighlightSimplePost("</em></b>"); // Etiqueta de fin del resaltado

        QueryResponse response = solrClientmicoleccion.query(solrQuery);
        SolrDocumentList results = response.getResults();
        for (int i = 0; i < results.size(); i++)
        {
            SolrDocument document = results.get(i);
            Long id = Long.valueOf((String) document.getFieldValue("id"));
            String author = document.getFieldValue("author").toString();
            String title = document.getFieldValue("title").toString();
            //String content = "<html>" + "<body>" + "<p>" + document.getFieldValue("content").toString() + "</p>" + "</body>" + "</html>";
            String contentHighlight = "<html>" + "<body>" + "<p>" + response.getHighlighting().get(id.toString()).get("content").get(0) + "</p>" + "</body>" + "</html>";
            Float score = (Float) document.getFieldValue("score");
            Documento documento = new Documento(id, author, title, contentHighlight, score);
            documentos.add(documento);
        }
        solrClientmicoleccion.close();
        return documentos;
    }

    private void addListeners()
    {
        this.vPrincipal.jMenuItemCerrarServidor.addActionListener(this);
        this.vPrincipal.jMenuItemDesconectar.addActionListener(this);
        this.vPrincipal.jMenuItemIndexar.addActionListener(this);
        this.vPrincipal.jMenuItemMostrarIndexados.addActionListener(this);
        this.vPrincipal.jMenuItemRealizarConsulta.addActionListener(this);
        this.vPrincipal.jMenuItemCargarFicheroConsultas.addActionListener(this);
        this.vPrincipal.jMenuItemSalir.addActionListener(this);
        this.vIndexar.jButtonSeleccionarFichero.addActionListener(this);
        this.vIndexarConsultas.jButtonSeleccionarFichero.addActionListener(this);
        this.vConsultas.jButtonBuscar.addActionListener(this);
        this.vPrincipal.jMenuItemGenerarFichero.addActionListener(this);
    }

    private ArrayList<Documento> indexarDocumentos(SolrClient solr, String cisiAllFilePath) throws IOException, SolrServerException
    {
        Path pathToDocument = null;
        BufferedReader br = null;

        pathToDocument = Paths.get(cisiAllFilePath);
        br = Files.newBufferedReader(pathToDocument.toAbsolutePath());

        String line;
        String marcaFinTexto = ".X";
        boolean inDocument = false;
        String id = null;
        String title = null;
        String author = null;
        StringBuilder content = new StringBuilder();
        ArrayList<Documento> documents = new ArrayList();

        while ((line = br.readLine()) != null)
        {
            if (line.startsWith(".I"))
            {
                // Nuevo documento comienza
                if (inDocument)
                {
                    // Si ya estábamos en un documento, enviamos el documento a Solr
                    SolrInputDocument document = new SolrInputDocument();
                    document.addField("id", id);
                    document.addField("title", title);
                    document.addField("author", author);
                    document.addField("content", content.toString());
                    solr.add("CORPUS", document);
                    Documento documento = new Documento(Long.parseLong(id), author, title, content.toString());
                    documents.add(documento);
                }
                inDocument = true;
                id = line.substring(3).trim(); // Obtener el ID del documento
                content.setLength(0); // Limpiar el contenido
            } else if (line.startsWith(".T"))
            {
                // Título del documento
                title = br.readLine().trim();
            } else if (line.startsWith(".A"))
            {
                // Autor del documento
                author = br.readLine().trim();
            } else if (line.startsWith(".W"))
            {
                while ((line = br.readLine()) != null && !line.equals(marcaFinTexto))
                {
                    content.append(line.trim()).append(" ");
                }
            }
        }

        // Procesamos el último documento 
        if (inDocument)
        {
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", id);
            document.addField("title", title);
            document.addField("author", author);
            document.addField("content", content.toString());
            solr.add("CORPUS", document);
            Documento documento = new Documento(Long.parseLong(id), author, title, content.toString());
            documents.add(documento);
        }

        // Enviar los cambios al servidor Solr
        solr.commit("CORPUS");
        br.close();
        return documents;
    }

    private Map<Integer, String> indexarConsultas(String filePath) throws IOException
    {
        Map<Integer, String> queries = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        boolean readingContent = false;
        int currentQueryId = -1;
        StringBuilder currentQuery = new StringBuilder();

        while ((line = reader.readLine()) != null)
        {
            if (line.startsWith(".I"))
            {
                if (currentQueryId != -1)
                {
                    //queries.put(currentQueryId, currentQuery.toString().trim());
                    String encodedQuery = URLEncoder.encode(currentQuery.toString().trim(), "UTF-8");
                    //String encodedQuery = removeColon(currentQuery.toString().trim());
                    queries.put(currentQueryId, encodedQuery);
                }
                currentQueryId = Integer.parseInt(line.split("\\s+")[1]);
                currentQuery = new StringBuilder();
            } else if (line.startsWith(".A"))
            {
                readingContent = false;
            } else if (line.startsWith(".T"))
            {
                readingContent = false;
            } else if (line.startsWith(".W"))
            {
                readingContent = true;
                continue;
            } else if (line.startsWith(".B"))
            {
                readingContent = false;
            } else if (readingContent)
            {
                currentQuery.append(line).append(" ");
            }
        }

        // Add the last query
        if (currentQueryId != -1)
        {
            String encodedQuery = URLEncoder.encode(currentQuery.toString().trim(), "UTF-8");
            //queries.put(currentQueryId, currentQuery.toString().trim());
            encodedQuery = encodedQuery.replace(":", "");
            queries.put(currentQueryId, encodedQuery);
        }
        reader.close();
        return queries;
    }

    /**
     * Diseño de la tabla documentos con el espacio entre columnas y la
     * prohibición de editar celdas
     */
    private void DiseñoTablaDocumentos()
    {
        //Para no permitir el redimensionamiento de las columnas con el ratón
        vDocumentosIndexados.jTableMostrarDocumentos.getTableHeader().setResizingAllowed(true);
        vDocumentosIndexados.jTableMostrarDocumentos.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        vDocumentosIndexados.jTableMostrarDocumentos.getColumnModel().getColumn(0).setPreferredWidth(90);
        vDocumentosIndexados.jTableMostrarDocumentos.getColumnModel().getColumn(1).setPreferredWidth(90);
        vDocumentosIndexados.jTableMostrarDocumentos.getColumnModel().getColumn(2).setPreferredWidth(90);
        vDocumentosIndexados.jTableMostrarDocumentos.getColumnModel().getColumn(3).setPreferredWidth(1500);
    }

    /**
     * Diseño de la tabla consultas con el espacio entre columnas y la
     * prohibición de editar celdas
     */
    private void DiseñoTablaConsultas()
    {
        //Para no permitir el redimensionamiento de las columnas con el ratón
        vConsultas.jTableConsultas.getTableHeader().setResizingAllowed(true);
        vConsultas.jTableConsultas.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        vConsultas.jTableConsultas.getColumnModel().getColumn(0).setPreferredWidth(90);
        vConsultas.jTableConsultas.getColumnModel().getColumn(1).setPreferredWidth(90);
        vConsultas.jTableConsultas.getColumnModel().getColumn(2).setPreferredWidth(90);
        vConsultas.jTableConsultas.getColumnModel().getColumn(3).setPreferredWidth(1500);
        vConsultas.jTableConsultas.getColumnModel().getColumn(4).setPreferredWidth(90);
    }

    /**
     * Este metodo rellena la tabla de documentos con los documentos de solr
     */
    private void pideDocumentos()
    {
        this.dTabla.vaciarTablaDocumentos();
        this.dTabla.rellenarTablaDocumentos(this.documentos);
    }

    /**
     * Este metodo rellena la tabla de documentos con los documentos de solr
     */
    private void documentosEncontrados(ArrayList<Documento> documents)
    {
        this.cTabla.vaciarTablaConsultas();
        this.cTabla.rellenarTablaConsultas(documents);
    }

    private void stopSolrServer()
    {
        // Ruta al directorio donde se encuentra el script solr.cmd
        String solrBinPath = "C:\\Users\\juald\\Downloads\\solr-9.3.0-slim\\solr-9.3.0-slim\\bin\\";

        String command = "solr.cmd";
        String argument1 = "stop";
        String argument2 = "-p";
        String argument3 = "8983";

        List<String> commands = new ArrayList<>();
        commands.add(solrBinPath + command);
        commands.add(argument1);
        commands.add(argument2);
        commands.add(argument3);

        // Configurar ProcessBuilder
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.redirectErrorStream(true);

        try
        {
            // Iniciar el proceso
            process = processBuilder.start();
            try ( BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    System.out.println(line);
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            Thread.sleep(5000); // Espera 5 segundos (ajusta según sea necesario)
            // Esperar a que el proceso termine
            int exitCode = process.waitFor();
            System.out.println("Proceso Solr terminado con código de salida: " + exitCode);
        } catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}