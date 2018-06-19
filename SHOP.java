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
        super(800, 550, "Shop", false, true);

        fensterbreite = (int)fensterGroesse().breite;
        fensterhoehe = (int)fensterGroesse().hoehe;

        maus = new Maus(3);
        mausAnmelden(maus);

        this.spiel = spiel;

        abstandy = 50;
        abstandx = 50;
        buttonbreite = fensterbreite - (2 * abstandx);
        buttonhoehe = 50;
        maxAnzahlButton = 5;

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
        switch(code)
        {
            case 0:
            spiel.shotHeat = spiel.shotHeat - 5;
            break;

            case 1:
            spiel.projektilVel = spiel.projektilVel + 2;
            break;

            case 2:
            spiel.projektilDamage = spiel.projektilDamage + 10;
            break;

            case 3:
            spiel.gegnerVel = spiel.gegnerVel - 1;
            break;

            case 4:
            spiel.gegnerHp = spiel.gegnerHp - 5;
            break;
        }

        spiel.datenbankSpeichern();
        
        Rechteck buttontmp = button.get(code);
        buttontmp.farbeSetzen("Rot");
        warten(50);
        buttontmp.farbeSetzen("Weiss");
    }
}
