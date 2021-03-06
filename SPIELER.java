import ea.*;
public class SPIELER
{
    protected Figur spieler;
    
    //protected Rechteck spieler;//nur temporär //protected zum Zugreifen
    
    
    public SPIELER(int Nx,int Ny,int Nb, int Nl)
    {
       spieler = new Figur(Nx, Ny, "files/visual/figuren/Figur.eaf");
       //spieler.faktorSetzen(2);
       //spieler = new Rechteck(Nx,Ny,Nb,Nl); 
       //spieler.farbeSetzen("grün");
    }
    
     public void bewegen(int x, int y)
    {
        spieler.bewegen(x, y);
    }
    
    public void sprung(int s)
    {
        spieler.sprung(s);
    }
    
    public SPIELER getThis ()
    {
        return this;
    }
    
    public Figur getRechteck()
    {
        return spieler;   
    } 
    
    public int getX()
    {
        return (int)spieler.getX();
    }

    public int getY()
    {
        return (int)spieler.getY();
    }

    public int getBreite()
    {
        return (int)spieler.getBreite();
    }

    public int getHoehe()
    {
        return (int)spieler.getHoehe();
    }
}
