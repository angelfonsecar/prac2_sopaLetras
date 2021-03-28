public class DatosPalabra {
    private int xInicio;
    private int yInicio;
    private int xFin;
    private int yFin;
    private String palabra;

    public DatosPalabra(int xInicio, int yInicio, int xFin, int yFin, String palabra) {
        this.xInicio = xInicio;
        this.yInicio = yInicio;
        this.xFin = xFin;
        this.yFin = yFin;
        this.palabra = palabra;
    }

    public String getPalabra() {
        return palabra;
    }

    public int getxInicio() {
        return xInicio;
    }

    public int getyInicio() {
        return yInicio;
    }

    public int getyFin() {
        return yFin;
    }

    public int getxFin() {
        return xFin;
    }
}
