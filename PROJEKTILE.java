import ea.*;

public class PROJEKTILE 
{
    //private Rechteck projektile;
    protected Figur projektile;
    protected int ID = 2;
    private int x;
    private int y;
    private int breite;
    private int laenge;
    private int xvel;
    private int damage;
    
    public PROJEKTILE(int Nx,int Ny, int vel, int damage)
    {
        x = Nx;
        y = Ny;
        xvel = vel;
        this.damage = damage;
        projektile = new Figur(Nx, Ny, "files/visual/figuren/Projektil.eaf");
        projektile.faktorSetzen(1);
        //projektile = new Rechteck(x,Ny,Nb,Nl);
    }
    
    public void bewegen()
    {
        projektile.bewegen(xvel, 0);
    }

    public PROJEKTILE getThis()
    {
        return this;
    }
    
    public int getDamage()
    {
        return damage;
    }

    public int getX()
    {
        return (int)projektile.getX();
    }

    public int getY()
    {
        return (int)projektile.getY();
    }

    public int getBreite()
    {
        return (int)projektile.getBreite();
    }

    public int getLaenge()
    {
        return (int)projektile.getHoehe();
    }
    
    public Figur getRechteck()
    {
      return projektile;   
    }
}
