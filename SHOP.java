import ea.*;
import java.util.*;

public class SHOP extends Game implements MausReagierbar 
{
    private int buttonbreite, buttonhoehe, abstandy, abstandx, fensterbreite, fensterhoehe, maxAnzahlButton;
    private Rechteck button1, button2, button3;
    private Maus maus;
    private SPIEL spiel;
    private ArrayList <Rechteck> button = new ArrayList <Rechteck>();

    private int preisShotHeat;
    private int preisProjektilVel;
    private int preisProjektilDamage;
    private int preisGegnerVel;
    private int preisGegnerHp;

    private int minShotHeat;
    private int maxProjektilVel;
    private int maxProjektilDamage;
    private int minGegnerVel;
    private int minGegnerHp;

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

        preisShotHeat = 150;
        preisProjektilVel = 100;
        preisProjektilDamage = 100;
        preisGegnerVel = 200;
        preisGegnerHp = 150;

        minShotHeat = 5;
        maxProjektilVel = 17;
        maxProjektilDamage = 100;
        minGegnerVel = 2;
        minGegnerHp = 70;

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
        String farbe = "Rot";
        switch(code)
        {
            case 0:
            if(spiel.score > preisShotHeat && spiel.shotHeat > minShotHeat)
            {
                spiel.shotHeat = spiel.shotHeat - 5;
                spiel.updateScore(-preisShotHeat);
            }
            else
            {
                farbe = "Blau";
            }
            break;

            case 1:
            if(spiel.score > preisProjektilVel && spiel.projektilVel < maxProjektilVel)
            {
                spiel.projektilVel = spiel.projektilVel + 2;
                spiel.updateScore(-preisProjektilVel);
            }
            else
            {
                farbe = "Blau";
            }            
            break;

            case 2:
            if(spiel.score > preisProjektilDamage && spiel.projektilDamage < maxProjektilDamage)
            {
                spiel.projektilDamage = spiel.projektilDamage + 10;
                spiel.updateScore(-preisProjektilDamage);
            }
            else
            {
                farbe = "Blau";
            }            
            break;

            case 3:
            if(spiel.score > preisGegnerVel && spiel.gegnerVel > minGegnerVel)
            {
                spiel.gegnerVel = spiel.gegnerVel - 1;
                spiel.updateScore(-preisGegnerVel);
            }
            else
            {
                farbe = "Blau";
            }            
            break;

            case 4:
            if(spiel.score > preisGegnerHp && spiel.gegnerHp > minGegnerHp)
            {
                spiel.gegnerHp = spiel.gegnerHp - 5;
                spiel.updateScore(-preisGegnerHp);
            }
            else
            {
                farbe = "Blau";
            }            
            break;
        }

        spiel.datenbankSpeichern();

        Rechteck buttontmp = button.get(code);
        buttontmp.farbeSetzen(farbe);
        warten(50);
        buttontmp.farbeSetzen("Weiss");
    }    
}
