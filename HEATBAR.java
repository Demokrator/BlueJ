import ea.*;

public class HEATBAR
{
    private Rechteck heatbar;
    protected int ID= 5;
    private int x;
    private int y;
    private int heat;
    private int maxHeat = 100;
    private int laenge;

    public HEATBAR(int Nx,int Ny,int Nb, int Nl)
    {
        x = Nx;
        y = Ny;
        heat =Nb;
        laenge = Nl;
        heatbar = new Rechteck(Nx,Ny,Nb,Nl);
        heatbar.farbeSetzen("rot");
    }
    
    public Rechteck cooldown(int cool)
    {   
        heatbar.breiteSetzen((int)heatbar.getBreite() - cool); //leiste kleiner machen
        
        return heatbar; //zum leichter in die wurzel fügen
    }
    
    public Rechteck heat(int heat)
    {
        heatbar.breiteSetzen((int)heatbar.getBreite() + heat); //leiste größer machen
        
        return heatbar; //zum leichter in die wurzel fügen
    }

    public HEATBAR getThis()
    {
        return this;
    }

    public int getX()
    {
        return (int)heatbar.getY();
    }

    public int getY()
    {
        return (int)heatbar.getY();
    }

    public int getHeat()
    {
        return (int)heatbar.getBreite();
    }

    public int getHoehe()
    {
        return (int)heatbar.getHoehe();
    }
    
    public Rechteck getRechteck()
    {
        return heatbar;   
    }
    
}
