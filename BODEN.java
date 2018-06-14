import ea.*;

public class BODEN 
{
    private Rechteck boden;
    protected int ID= 4;
    private int x;
    private int y;
    private int breite;
    private int laenge;

    public BODEN(int Nx,int Ny,int Nb, int Nl)
    {
        x = Nx;
        y = Ny;
        breite = Nb;
        laenge = Nl;
        boden = new Rechteck(Nx,Ny,Nb,Nl);
    }
    
    public void bewegen(int x, int y)
    {
        boden.bewegen(x, y);
    }

    public BODEN getThis()
    {
        return this;
    }

    public int getX()
    {
        return (int)boden.getX();
    }

    public int getY()
    {
        return (int)boden.getY();
    }

    public int getBreite()
    {
        return (int)boden.getBreite();
    }

    public int getHoehe()
    {
        return (int)boden.getHoehe();
    }
    
    public Rechteck getRechteck()
    {
        return boden;   
    }
    
}
