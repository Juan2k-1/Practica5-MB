package Modelo;

/**
 *
 * @author Juan Alberto Dominguez Vazquez
 */
public class Documento
{
    private Long id;
    private String autor;
    private String titulo;
    private String contenido;
    private Float score;
    private String persona;
    private String date;
    private String organization;
    private String location;
    private String money;

    public Documento()
    {
    }

    public Documento(Long id, String autor, String titulo, String contenido)
    {
        this.id = id;
        this.autor = autor;
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public Documento(Long id, String autor, String titulo, String contenido, Float score)
    {
        this.id = id;
        this.autor = autor;
        this.titulo = titulo;
        this.contenido = contenido;
        this.score = score;
    }

    public Documento(Long id, String autor, String titulo, String contenido, Float score, String persona, String date, String organization, String location, String money)
    {
        this.id = id;
        this.autor = autor;
        this.titulo = titulo;
        this.contenido = contenido;
        this.score = score;
        this.persona = persona;
        this.date = date;
        this.organization = organization;
        this.location = location;
        this.money = money;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getAutor()
    {
        return autor;
    }

    public void setAutor(String autor)
    {
        this.autor = autor;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public String getContenido()
    {
        return contenido;
    }

    public void setContenido(String contenido)
    {
        this.contenido = contenido;
    }  

    public Float getScore()
    {
        return score;
    }

    public void setScore(Float score)
    {
        this.score = score;
    }   

    public String getPersona()
    {
        return persona;
    }

    public void setPersona(String persona)
    {
        this.persona = persona;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getOrganization()
    {
        return organization;
    }

    public void setOrganization(String organization)
    {
        this.organization = organization;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getMoney()
    {
        return money;
    }

    public void setMoney(String money)
    {
        this.money = money;
    }

    @Override
    public String toString()
    {
        return "Documento{" + "id=" + id + ", autor=" + autor + ", titulo=" + titulo + ", contenido=" + contenido + ", score=" + score + ", persona=" + persona + ", date=" + date + ", organization=" + organization + ", location=" + location + ", money=" + money + '}';
    }   
}