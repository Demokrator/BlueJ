import ea.*;

public class HEATBARnew
{
    private Rechteck background;
    private Figur bar;
    private Rechteck schieber;
    private int currentHeat = 0;
    private int maxHeat;
    private SPIEL spiel;

    public HEATBARnew(int Nx, int Ny, SPIEL spiel)
    {
        this.spiel = spiel;

        bar = new Figur(Nx, Ny, "files/visual/figuren/Hitzebalken.eaf");
        maxHeat = (int)bar.getBreite();

        //schieber = new Rechteck(bar.getX() + 1, Ny, maxHeat, bar.getHoehe());
        schieber = new Rechteck(bar.getX() + bar.getBreite() + 1, Ny, 0, bar.getHoehe());
        //cooldown((int)bar.getBreite());
        spiel.wurzel.add(bar);
        spiel.wurzel.add(schieber);        
    }

    public void cooldown(int cool)
    {   
        if(schieber.getBreite() < maxHeat)
        {
            schieber.loeschen();
            spiel.wurzel.entfernen(schieber);

            schieber.bewegen(-cool, 0);
            schieber.breiteSetzen((int)schieber.getBreite() + cool);

            spiel.wurzel.add(schieber);
        }
    }

    public boolean heat(int heat)
    {   
        if(schieber.getBreite() >= heat)
        {
            schieber.loeschen();
            spiel.wurzel.entfernen(schieber);

            schieber.bewegen(heat, 0);
            schieber.breiteSetzen((int)schieber.getBreite() - heat);

            spiel.wurzel.add(schieber);

            return true;
        }

        return false;
    }

    public void wurzelEntfernen()
    {
        schieber.loeschen();
        spiel.wurzel.entfernen(schieber);
        //spiel.wurzel.entfernen(bar);
    }

    public Figur getBar()
    {
        return bar;   
    }
}
