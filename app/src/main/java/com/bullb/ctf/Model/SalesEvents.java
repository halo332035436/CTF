package com.bullb.ctf.Model;

public class SalesEvents {
    private String title, image, link;

    public SalesEvents() {

    }

    public SalesEvents(String title, String image, String link) {
        this.title = title;
        this.image = image;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLink(String link){
        this.link = link;
    }

    public String getLink(){
        return link;
    }
}
