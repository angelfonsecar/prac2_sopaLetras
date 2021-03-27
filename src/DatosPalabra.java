public class DatosPalabra {
    private String palabra;
    private int xInicio;
    private int yInicio;
    private int xFin;
    private int yFin;

    public DatosPalabra(int xInicio, int yInicio, int xFin, int yFin, String palabra) {
        this.xInicio = xInicio;
        this.yInicio = yInicio;
        this.xFin = xFin;
        this.yFin = yFin;
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
