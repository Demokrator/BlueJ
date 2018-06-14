import ea.*;

public class SOUND
{
    private Sound mariojump;
    
    public SOUND()
    {
        mariojump = new Sound("files/sound/mario_jump.mp3");
    }
    
    public void playSound(String SoundName)
    {
        switch(SoundName)
        {
            case "mariojump":
                mariojump.play();
            break;
        }
    }
}