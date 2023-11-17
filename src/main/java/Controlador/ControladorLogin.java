package Controlador;

import Vista.VistaLogin;
import Vista.VistaMensaje;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

/**
 *
 * @author Juan Alberto Dominguez Vazquez
 */
public class ControladorLogin implements ActionListener
{
    private VistaLogin vLogin;
    private VistaMensaje vMensaje;
    private String solrUrl;
    private HttpSolrClient solrClient;
    private ControladorPrincipal controladorPrincipal;
    private boolean serverRunning;
    private Process process;

    /**
     *
     */
    public ControladorLogin()
    {
        this.vLogin = new VistaLogin();
        this.vMensaje = new VistaMensaje();
        this.serverRunning = false;

        addListeners();

        vLogin.setLocationRelativeTo(null); // Para que la ventana se muestre en el centro de la pantalla
        vLogin.setVisible(true); // Para que la ventana sea visible al usuario

        this.vLogin.jProgressBarSolr.setStringPainted(true);
    }

    /**
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case "Salir":
            {
                this.vLogin.dispose();
                System.exit(0);
                break;
            }
            case "Conectar":
            {
                if (serverRunning)
                {
                    this.solrUrl = "http://localhost:8983/solr/";
                    this.solrClient = new HttpSolrClient.Builder(solrUrl).build();
                    this.vMensaje.MensajeInformacion("¡Conexión Correcta al servidor Solr!");
                    this.controladorPrincipal = new ControladorPrincipal(vMensaje, solrClient, process);
                    this.vLogin.dispose();

                } else
                {
                    this.vMensaje.MensajeDeError("Error, el servidor de solr no está iniciado");
                }
                break;
            }
            case "IniciarSolr":
            {
                try
                {
                    startSolrServer();
                    this.serverRunning = true;
                } catch (IOException ex)
                {
                    Logger.getLogger(ControladorLogin.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
                break;
            }
        }
    }

    public void startSolrServer() throws IOException
    {
        // Ruta al directorio donde se encuentra el script solr.cmd
        String solrBinPath = "C:\\Users\\juald\\Downloads\\solr-9.3.0-slim\\solr-9.3.0-slim\\bin\\";

        // Comando para iniciar el servidor Solr
        String command = "solr.cmd";
        String argument = "start";

        // Construir la lista de comandos
        List<String> commands = new ArrayList<>();
        commands.add(solrBinPath + command);
        commands.add(argument);

        // Configurar ProcessBuilder
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.redirectErrorStream(true);

        // Crear un hilo para ejecutar el proceso en segundo plano
        Thread thread = new Thread(() ->
        {
            try
            {
                // Iniciar el proceso
                Process process = processBuilder.start();
                // Leer la salida del proceso en segundo plano
                while (process.isAlive())
                {
                    // Actualizar la barra de progreso cada medio segundo
                    Thread.sleep(500);
                    SwingUtilities.invokeLater(() ->
                    {
                        int progressValue = ControladorLogin.this.vLogin.jProgressBarSolr.getValue() + 1;
                        ControladorLogin.this.vLogin.jProgressBarSolr.setValue(progressValue);
                    });
                }
                // Asegurar que la barra de progreso llegue al 100% al finalizar el proceso
                SwingUtilities.invokeLater(() -> ControladorLogin.this.vLogin.jProgressBarSolr.setValue(100));
            } catch (IOException | InterruptedException e)
            {
                Logger.getLogger(ControladorLogin.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        });
        // Iniciar el hilo
        thread.start();
    }

    private void addListeners()
    {
        this.vLogin.jButtonSalir.addActionListener(this);
        this.vLogin.jButtonConectar.addActionListener(this);
        this.vLogin.jButtonIniciarSolr.addActionListener(this);
    }
}