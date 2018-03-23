package societino.com.societinof;


public class NewsItem {

    private String title;
    private String decription;
    private String imageUrl;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public NewsItem(String title, String decription,String imageUrl, String url) {
        this.title = title;
        this.decription = decription;
        this.imageUrl=imageUrl;
        this.url=url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDecription() {
        return decription;
    }
}


