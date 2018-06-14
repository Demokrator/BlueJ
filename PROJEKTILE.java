import ea.*;

public class PROJEKTILE 
{
    private Rechteck projektile; // nur temporär
    protected int ID = 2;
    private int x;
    private int y;
    private int breite;
    private int laenge;
    private int xvel;
    private int damage;
    
    public PROJEKTILE(int Nx,int Ny,int Nb, int Nl, int vel, int damage)
    {
        x = Nx;
        y = Ny;
        breite = Nb;
        laenge = Nl;
        xvel = vel;
        this.damage = damage;

        projektile = new Rechteck(x,Ny,Nb,Nl);
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
    
    public Rechteck getRechteck()
    {
      return projektile;   
    }
}
