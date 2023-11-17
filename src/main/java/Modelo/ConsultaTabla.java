package Modelo;

import Vista.VistaConsultas;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Juan Alberto Dominguez Vazquez
 */
public class ConsultaTabla extends DefaultTableModel 
{
    private VistaConsultas vConsultas;

    /**
     * Constructor de la clase que hace una llamada al metodo
     * dibujaTablaDocumentos para que se muestre el diseño que queremos en la
     * tabla
     *
     * @param vConsultas
     */
    public ConsultaTabla(VistaConsultas vConsultas)
    {
        this.vConsultas = vConsultas;
        dibujarTablaConsultas(vConsultas);
    }

    /**
     * Metodo que prohibe la edicion de las celdas de la tabla
     *
     * @param row
     * @param column
     * @return
     */
    @Override
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }

    /**
     * Metodo que diseña la tabla con los campos que queremos
     *
     * @param vConsultas
     */
    public void dibujarTablaConsultas(VistaConsultas vConsultas)
    {
        String[] columnasTabla =
        {
            "Código", "Autor", "Título", "Contenido", "Score"
        };
        setColumnIdentifiers(columnasTabla);
    }

    /**
     * Metodo que rellena la tabla de documentos con el array de documentos pasado
     * como parametro
     *
     * @param documentos
     */
    public void rellenarTablaConsultas(ArrayList<Documento> documentos)
    {
        Object[] fila = new Object[5];
        int numRegistros = documentos.size();
        for (int i = 0; i < numRegistros; i++)
        {
            fila[0] = documentos.get(i).getId();
            fila[1] = documentos.get(i).getAutor();
            fila[2] = documentos.get(i).getTitulo();
            fila[3] = documentos.get(i).getContenido();
            fila[4] = documentos.get(i).getScore();
            this.addRow(fila);
        }
    }

    /**
     * Metodo que vacia la tabla de documentos
     */
    public void vaciarTablaConsultas()
    {
        while (this.getRowCount() > 0)
        {
            this.removeRow(0);
        }
    }
}