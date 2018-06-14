import ea.*;

public class HINDERNISSE 
{
    private Rechteck hinderniss; // nur temporär
    protected int ID = 1;
    private int x;
    private int y;
    private int breite;
    private int laenge;
    
    public HINDERNISSE(int Nx,int Ny,int Nb, int Nl)
    {
        x=Nx;
        y=Ny;
        breite=Nb;
        laenge=Nl;

        hinderniss=new Rechteck(Nx,Ny,Nb,Nl);
    } 
    
    public void bewegen(int x, int y)
    {
        hinderniss.bewegen(x, y);
    }

    public HINDERNISSE getThis()
    {
        return this;
    }

    public int getX()
    {
        return (int)hinderniss.getX();
    }

    public int getY()
    {
        return (int)hinderniss.getY();
    }

    public int getBreite()
    {
        return (int)hinderniss.getBreite();
    }

    public int getHoehe()
    {
        return (int)hinderniss.getHoehe();
    }
    
    public Rechteck getRechteck()
    {
        return hinderniss;   
    }
}
