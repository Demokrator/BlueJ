import ea.*;
import java.util.*;

public class HINDERNISSE 
{
    protected Figur hinderniss;
    //private Rechteck hinderniss; // nur temporär
    protected int ID = 1;
    private int x;
    private int y;
    private int breite;
    private int laenge;
    private Random zufall;
    private String[] pfade = {"files/visual/figuren/Mauer.eaf", "files/visual/figuren/Kugel.eaf"};
    
    public HINDERNISSE(int Nx,int Ny,int Nb, int Nl)
    {
        x=Nx;
        y=Ny;
        breite=Nb;
        laenge=Nl;
        
        zufall = new Random();

        int tmpSprite = zufall.nextInt(pfade.length);
        hinderniss = new Figur(Nx, Ny, pfade[tmpSprite]);
        //hinderniss=new Rechteck(Nx,Ny,Nb,Nl);
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
    
    public Figur getRechteck()
    {
        return hinderniss;   
    }
}
