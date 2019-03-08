package scanlibsdownload;

public class ScanlibsItem {
    private String title;
    private String infoString;
    private String itemUrl;

    public ScanlibsItem(String title, String infoString, String itemUrl) {
        this.title = title;
        this.infoString = infoString;
        this.itemUrl = itemUrl;
    }

    @Override
    public String toString() {
        return "ScanlibsItem{" +
                "title='" + title + '\'' +
                ", infoString='" + infoString + '\'' +
                ", itemUrl='" + itemUrl + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfoString() {
        return infoString;
    }

    public void setInfoString(String infoString) {
        this.infoString = infoString;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }
}
