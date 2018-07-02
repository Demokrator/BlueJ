import ea.*;
import java.util.*;

public class GEGNER
{
    protected Figur gegner;
    //private Rechteck gegner;
    protected int ID = 3;
    private Random zufall;
    private Figur explosion;
    private String[] pfade = {"files/visual/figuren/Gegner1Rot.eaf", "files/visual/figuren/Gegner2Gruen.eaf", "files/visual/figuren/Gegner3Mischmasch.eaf"};

    private int x;
    private int y;
    private int breite;
    private int laenge;
    private int hp = 100;
    private int vel;
    private boolean alive = true;
    private int points;

    public GEGNER(int Nx,int Ny,int Nb, int Nl, int vel, int hp, int points)
    {
        x = Nx;
        y = Ny;
        breite = Nb;
        laenge = Nl;
        this.vel = vel;
        this.hp = hp;
        this.points = points;

        zufall = new Random();
        int tmpSprite = zufall.nextInt(pfade.length);
        
        //gegner=new Rechteck(Nx,Ny,Nb,Nl);  
        gegner = new Figur(Nx, Ny, pfade[tmpSprite]);
    }

    public boolean spielerSchneiden(SPIELER spieler)
    {
        if(alive == true) //kollision nur wenn gegner noch am leben ist
        {
            if(gegner.schneidet(spieler.spieler) == true || spieler.spieler.stehtAuf(gegner))
            {
                return true;
            }
        }

        return false;
    }

    public void projektilSchneiden(ArrayList<PROJEKTILE> projektile, int index, SPIEL spiel)
    {
        //Index = projektilZahl

        if(alive == true) //nur wenn am leben --> man bekommt keinen score mehr wenn toter gegner getroffen wird
        {
            for (int i = 0; i < index; i++)
            {
                if(gegner.schneidet(projektile.get(i).getRechteck()) == true || projektile.get(i).getRechteck().stehtAuf(gegner))
                {
                    hit(projektile.get(i).getDamage(), spiel); //schaden machen
                    projektile.get(i).getRechteck().loeschen();
                    spiel.wurzel.entfernen(projektile.get(i).getRechteck());
                    projektile.remove(i);
                    spiel.projektilZahl--;
                    index--;//geändert
                }
            }   
        }
    }
    
    public void animationStoppen(SPIEL spiel)
    {
        if(alive == false)
        {
            if(explosion.aktuellesBild() == explosion.animation().length - 1) //wenn letztes bild der animation erreicht --> aktuelles bild gleich letztes der animation
            {
                explosion.loeschen();
                spiel.wurzel.entfernen(explosion);
                
                spiel.gegnerZahl--;
                spiel.gegner.remove(this);
                
            }
        }
    }

    public void hit(int damage, SPIEL spiel)
    {
        if(hp - damage <= 0)
        {
            alive = false; //unschädlich machen
            gegner.sichtbarSetzen(false); //optisch verschwinden lassen --> werden immer noch gelöscht wenn bildschirm verlassen
            gegner.loeschen();
            spiel.wurzel.entfernen(gegner); //aus wurzel entfernen damit man nicht auf unsichtbarem gegner stehen kann

            

            explosion = new Figur(gegner.getX(), gegner.getY(), "files/visual/figuren/Explosion.eaf");
            explosion.animationsGeschwindigkeitSetzen(2);
            spiel.wurzel.add(explosion);
            
            spiel.updateScore(points);
        }
        else
        {
            hp = hp - damage;
        }
    }

    public void bewegen()
    {
        int b = zufall.nextInt(4); //?

        switch(b)
        {
            case 0:

            break;

            case 1:
            gegner.bewegen(-vel, -vel); //nach oben
            break;  

            case 2:
            gegner.bewegen(-vel, vel); //nach unten
            break;

            case 3:
            gegner.bewegen(-vel, 0); //zum spieler
            break;
        }
    }

    public GEGNER getThis()
    {
        return this;
    }

    public int getX()
    {
        return (int)gegner.getX();
    }

    public int getY()
    {
        return (int)gegner.getY();
    }

    public int getBreite()
    {
        return (int)gegner.getBreite();
    }

    public int getLaenge()
    {
        return (int)gegner.getHoehe();
    }

    public Figur getRechteck()
    {
        return gegner;   
    }
}
