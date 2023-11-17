package Modelo;

/**
 *
 * @author Juan Alberto Dominguez Vazquez
 */
public class Consulta
{
    private int id;
    private String contenido;

    /**
     *
     */
    public Consulta()
    {
    }
    
    /**
     *
     * @param id
     * @param contenido
     */
    public Consulta(int id, String contenido)
    {
        this.id = id;
        this.contenido = contenido;
    }

    /**
     *
     * @return
     */
    public String getContenido()
    {
        return contenido;
    }

    /**
     *
     * @param contenido
     */
    public void setContenido(String contenido)
    {
        this.contenido = contenido;
    }

    @Override
    public String toString()
    {
        return "Consulta{" + "id=" + id + ", contenido=" + contenido + '}';
    }   
}