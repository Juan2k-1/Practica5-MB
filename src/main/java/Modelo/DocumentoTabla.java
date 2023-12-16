package Modelo;

import Vista.VistaMostrarDocumentosIndexados;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Juan Alberto Dominguez Vazquez
 */
public class DocumentoTabla extends DefaultTableModel
{
    private VistaMostrarDocumentosIndexados vDocumentos;

    /**
     * Constructor de la clase que hace una llamada al metodo
     * dibujaTablaDocumentos para que se muestre el diseño que queremos en la
     * tabla
     *
     * @param vDocumentos
     */
    public DocumentoTabla(VistaMostrarDocumentosIndexados vDocumentos)
    {
        this.vDocumentos = vDocumentos;
        dibujarTablaDocumentos(vDocumentos);
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
     * @param vDocumentos
     */
    public void dibujarTablaDocumentos(VistaMostrarDocumentosIndexados vDocumentos)
    {
        String[] columnasTabla =
        {
            "Código", "Autor", "Título", "Contenido", "Persona", "Fecha", "Organización", "Localización", "Dinero"
        };
        setColumnIdentifiers(columnasTabla);
    }

    /**
     * Metodo que rellena la tabla de documentos con el array de documentos pasado
     * como parametro
     *
     * @param documentos
     */
    public void rellenarTablaDocumentos(ArrayList<Documento> documentos)
    {
        Object[] fila = new Object[9];
        int numRegistros = documentos.size();
        for (int i = 0; i < numRegistros; i++)
        {
            fila[0] = documentos.get(i).getId();
            fila[1] = documentos.get(i).getAutor();
            fila[2] = documentos.get(i).getTitulo();
            fila[3] = documentos.get(i).getContenido();
            fila[4] = documentos.get(i).getPersona();
            fila[5] = documentos.get(i).getDate();
            fila[6] = documentos.get(i).getOrganization();
            fila[7] = documentos.get(i).getLocation();
            fila[8] = documentos.get(i).getMoney();
            this.addRow(fila);
        }
    }

    /**
     * Metodo que vacia la tabla de documentos
     */
    public void vaciarTablaDocumentos()
    {
        while (this.getRowCount() > 0)
        {
            this.removeRow(0);
        }
    }
}