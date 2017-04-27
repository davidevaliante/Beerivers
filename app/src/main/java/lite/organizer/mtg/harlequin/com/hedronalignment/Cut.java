package lite.organizer.mtg.harlequin.com.hedronalignment;


public class Cut {

    public String imagePath;
    public String cutName;

    public Cut() {

    }

    public Cut(String imagePath,String cutName){

        this.imagePath = imagePath;
        this.cutName = cutName;
    }

    public String getCutName() {
        return cutName;
    }

    public void setCutName(String cutName) {
        this.cutName = cutName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
