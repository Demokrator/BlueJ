import ea.*;
import java.util.*;

public class SHOP extends Game implements MausReagierbar 
{
    private int buttonbreite, buttonhoehe, abstandy, abstandx, fensterbreite, fensterhoehe, maxAnzahlButton;
    private Rechteck button1, button2, button3;
    private Maus maus;
    private SPIEL spiel;
    private ArrayList <Rechteck> button = new ArrayList <Rechteck>();

    public SHOP(SPIEL spiel)
    {
        super(800, 750, "Shop", false, true);

        fensterbreite = (int)fensterGroesse().breite;
        fensterhoehe = (int)fensterGroesse().hoehe;
        
        maus = new Maus(3);
        mausAnmelden(maus);
        
        this.spiel = spiel;
        
        abstandy = 50;
        abstandx = 50;
        buttonbreite = fensterbreite - (2 * abstandx);
        buttonhoehe = 50;
        maxAnzahlButton = 7;
        
        for (int i = 0; i < maxAnzahlButton; i++)
        {
            button.add(new Rechteck(abstandx, (((i + 1) * abstandy) + (i * buttonhoehe)), buttonbreite, buttonhoehe));
        }
        
        int i = 0;
        for(Rechteck buttontmp: button)
        {
            buttontmp.farbeSetzen("Weiss");
            wurzel.add(buttontmp);
            buttontmp.passivMachen();
            maus.anmelden(this, buttontmp, button.indexOf(buttontmp));
        }
    }

    public void tasteReagieren(int code)
    {
        switch(code)
        {
            case 25: //z
                spiel.shopAbmelden();
            break;
        }
    }

    public void mausReagieren(int code)
    {
        Rechteck buttontmp = button.get(code);
        buttontmp.farbeSetzen("Rot");
    }
}
